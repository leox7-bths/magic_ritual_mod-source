package com.example.magic_ritual_mod.block.custom;

import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.effect.ModEffects;
import com.example.magic_ritual_mod.item.ModItems;
import com.example.magic_ritual_mod.ui.CenterFuelMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.*;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Vector3f;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.HashSet;
import java.util.Set;

public class MagicCenterBlockEntity extends BlockEntity implements MenuProvider {

    private boolean fogActive = false;
    private float fuel = 0.0f;
    private int fuelAccumulator = 0;

    private final Set<String> blacklistedPlayers = new HashSet<>();
    private boolean allowHostileMobs = true;
    private boolean allowPassiveMobs = true;

    public static volatile double[][] FOG_POINTS;
    public static volatile double[][] INVERTED_POINTS;
    public static volatile double[][] FORCEFIELD_POINTS;
    public static volatile double[][] NORMAL_POINTS;

    public static void loadPatterns(ResourceManager manager) {
        FOG_POINTS = MagicPatternLoader.load(
                manager,
                ResourceLocation.fromNamespaceAndPath("magic_ritual_mod", "pattern/fog.json")
        );
        INVERTED_POINTS = MagicPatternLoader.load(
                manager,
                ResourceLocation.fromNamespaceAndPath("magic_ritual_mod", "pattern/inverted.json")
        );
        FORCEFIELD_POINTS = MagicPatternLoader.load(
                manager,
                ResourceLocation.fromNamespaceAndPath("magic_ritual_mod", "pattern/forcefield.json")
        );
        NORMAL_POINTS = MagicPatternLoader.load(
                manager,
                ResourceLocation.fromNamespaceAndPath("magic_ritual_mod", "pattern/normal.json")
        );
    }

    private final ItemStackHandler fuelSlot = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.SPIRIT_STONE.get());
        }
    };

    public MagicCenterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGIC_CENTER_BE.get(), pos, state);
    }

    public static void spawnParticles(Level level, BlockPos pos, BlockState state,
                                      MagicCenterBlockEntity be,
                                      double[][] points,
                                      Vector3f color) {

        if (level == null || !be.particlesEnabled) return;

        int radius = state.getValue(MagicCenterBlock.RADIUS);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.05;
        double cz = pos.getZ() + 0.5;

        long time = level.getGameTime();
        double angle = (time * 0.8) % 360;

        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double scale = radius / 6.0;

        for (double[] p : points) {

            double px = p[0] * scale;
            double pz = p[1] * scale;

            double rx = px * cos - pz * sin;
            double rz = px * sin + pz * cos;

            level.addParticle(
                    new DustParticleOptions(color, 0.5f),
                    cx + rx, cy, cz + rz,
                    0, 0, 0
            );
        }
    }

    //Combo Detect

    public boolean isAllowHostileMobs() {
        return allowHostileMobs;
    }

    public void setAllowHostileMobs(boolean value) {
        if (this.allowHostileMobs != value) {
            this.allowHostileMobs = value;
            setChanged();
        }
    }

    public boolean isAllowPassiveMobs() {
        return allowPassiveMobs;
    }

    public void setAllowPassiveMobs(boolean value) {
        if (this.allowPassiveMobs != value) {
            this.allowPassiveMobs = value;
            setChanged();
        }
    }

    public Set<String> getBlacklistedPlayers() {
        return blacklistedPlayers;
    }

    public boolean addBlacklistedPlayer(String name) {
        boolean added = blacklistedPlayers.add(name);
        if (added) setChanged();
        return added;
    }

    public boolean removeBlacklistedPlayer(String name) {
        boolean removed = blacklistedPlayers.remove(name);
        if (removed) setChanged();
        return removed;
    }

    private boolean particlesEnabled = true;

    public boolean isParticlesEnabled() {
        return particlesEnabled;
    }

    public void setParticlesEnabled(boolean particlesEnabled) {
        this.particlesEnabled = particlesEnabled;
        setChanged();
    }

    private static int getCircleCount(Level level, BlockPos centerPos, int radius, CircleType type) {
        int count = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if (x * x + z * z > radius * radius) continue;

                for (int y = -3; y <= 3; y++) {
                    BlockPos p = centerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(p);

                    if (state.is(ModBlocks.MAGIC_CIRCLE_BLOCK.get())
                            && state.getValue(MagicCircleBlock.CIRCLE_TYPE) == type) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MagicCenterBlockEntity be) {

        if (level.isClientSide) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level == null || mc.gameRenderer == null) {
                return;
            }

            Frustum frustum = mc.levelRenderer.getFrustum();
            if (frustum == null) {
                return;
            }

            AABB box = new AABB(pos).inflate(1);

            if (!frustum.isVisible(box)) {
                return;
            }

            if (level.getNearestPlayer(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    32,
                    false
            ) == null) {
                return;
            }

            if (be.particlesEnabled) {
                spawnParticles(
                        level,
                        pos,
                        state,
                        be,
                        NORMAL_POINTS,
                        new Vector3f(1.0f, 0.0f, 0.0f)
                );
            }
            return;
        }

        int radius = state.getValue(MagicCenterBlock.RADIUS);
        detectCombos(level, pos, state, radius);

        int attackCount = getCircleCount(level, pos, radius, CircleType.ATTACK);
        int defenseCount = getCircleCount(level, pos, radius, CircleType.DEFENSE);
        int regenCount = getCircleCount(level, pos, radius, CircleType.REGENERATION);

        int circleCount = attackCount + defenseCount + regenCount;

        boolean fogActive = false;
        boolean invertedActive = false;
        boolean forcefieldActive = false;

        outer:
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                BlockPos base = pos.offset(x, 0, z);

                if (ComboBlockEntity.isPatternValid(level, base, ComboType.FOG)) {
                    fogActive = true;
                }

                if (ComboBlockEntity.isPatternValid(level, base, ComboType.INVERTED)) {
                    invertedActive = true;
                }

                if (ComboBlockEntity.isPatternValid(level, base, ComboType.FORCEFIELD)) {
                    forcefieldActive = true;
                }

                if (fogActive && invertedActive && forcefieldActive) break outer;
            }
        }

        boolean comboActive = fogActive || invertedActive || forcefieldActive;

        int baseCost = switch (radius) {
            case 3 -> 8;
            case 5 -> 16;
            case 7 -> 32;
            case 9 -> 64;
            case 11 -> 128;
            case 13 -> 256;
            default -> 16;
        };

        int costPerMinute = baseCost + circleCount * 10;

        be.fuelAccumulator += costPerMinute;

        if (be.fuelAccumulator >= 1200) {
            be.fuelAccumulator -= 1200;
            be.fuel -= 1.0f;
        }

        while (be.fuel <= 0.0f) {
            ItemStack stack = be.fuelSlot.extractItem(0, 1, false);
            if (stack.isEmpty()) break;
            be.fuel += 1.0f;
        }

        if (be.fuel > 0 && forcefieldActive) {
            be.bounceBlacklistedPlayers(level, pos, radius);
        }

        if (be.fuel > 0 && level.getGameTime() % 20 == 0) {

            AABB area = new AABB(pos).inflate(radius);

            for (Player player : level.getEntitiesOfClass(Player.class, area)) {

                boolean inRange = player.distanceToSqr(
                        pos.getX(), pos.getY(), pos.getZ()
                ) <= radius * radius;

                if (!inRange) continue;

                if (comboActive) {

                    if (fogActive) {
                        player.addEffect(new MobEffectInstance(
                                ModEffects.FOG,
                                60,
                                0,
                                true,
                                false
                        ));
                    }

                    if (invertedActive) {
                        player.addEffect(new MobEffectInstance(
                                ModEffects.INVERTED_CONTROL,
                                60,
                                0,
                                true,
                                false
                        ));
                    }

                } else {

                    if (attackCount > 0) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.DAMAGE_BOOST,
                                40,
                                attackCount - 1,
                                true,
                                false
                        ));
                    }

                    if (defenseCount > 0) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.DAMAGE_RESISTANCE,
                                40,
                                defenseCount - 1,
                                true,
                                false
                        ));
                    }

                    if (regenCount > 0) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.REGENERATION,
                                40,
                                regenCount - 1,
                                true,
                                false
                        ));
                    }
                }
            }
        }

        if (level.getGameTime() % 20 == 0) {

            for (Direction dir : Direction.values()) {

                BlockPos adj = pos.relative(dir);
                BlockEntity beAdj = level.getBlockEntity(adj);

                if (beAdj == null) continue;

                IItemHandler handler = level.getCapability(
                        Capabilities.ItemHandler.BLOCK,
                        adj,
                        dir.getOpposite()
                );

                if (handler == null) continue;

                for (int i = 0; i < handler.getSlots(); i++) {

                    ItemStack simulated = handler.extractItem(i, 64, true);

                    if (!simulated.isEmpty() && simulated.is(ModItems.SPIRIT_STONE.get())) {

                        int amount = simulated.getCount();

                        ItemStack real = handler.extractItem(i, amount, false);

                        ItemStack leftover = be.fuelSlot.insertItem(0, real, false);

                        if (!leftover.isEmpty()) {
                            handler.insertItem(i, leftover, false);
                        }
                    }
                }
            }
        }
    }

    private void bounceBlacklistedPlayers(Level level, BlockPos centerPos, int radius) {
        AABB area = new AABB(centerPos).inflate(radius + 4.0);
        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            bounceBlacklistedPlayer(centerPos, radius, player);
        }
    }

    private void bounceBlacklistedPlayer(BlockPos centerPos, int radius, Player player) {
        if (!blacklistedPlayers.contains(player.getGameProfile().getName())) {
            return;
        }

        double centerX = centerPos.getX() + 0.5;
        double centerZ = centerPos.getZ() + 0.5;
        double dx = player.getX() - centerX;
        double dz = player.getZ() - centerZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 0.001) {
            dx = 1.0;
            dz = 0.0;
            distance = 1.0;
        }

        double unitX = dx / distance;
        double unitZ = dz / distance;
        Vec3 velocity = player.getDeltaMovement();
        double outwardSpeed = velocity.x * unitX + velocity.z * unitZ;
        double barrierDistance = radius + 1.75;
        boolean movingIntoBarrier = distance <= radius + 4.0 && outwardSpeed < -0.05;
        if (distance > barrierDistance && !movingIntoBarrier) {
            return;
        }

        double penetration = Math.max(0.0, barrierDistance - distance);
        double reboundSpeed = Math.min(3.0, 1.35 + penetration * 0.35);

        double bounceSpeed = Math.max(reboundSpeed, Math.abs(outwardSpeed) + 0.65);
        player.setDeltaMovement(unitX * bounceSpeed, Math.max(velocity.y, 0.35), unitZ * bounceSpeed);
        player.push(unitX * 0.2, 0.05, unitZ * 0.2);
        player.hasImpulse = true;
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
        }
    }

    private static void detectCombos(Level level, BlockPos centerPos, BlockState state, int radius) {
        HashSet<BlockPos> checkedSPositions = new HashSet<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double hDist = Math.sqrt(x * x + z * z);
                if (hDist > radius + 0.5) continue;

                BlockPos sPos = centerPos.offset(x, 0, z);
                if (!checkedSPositions.add(sPos)) continue;

                BlockState currentState = level.getBlockState(sPos);
                boolean hasComboBlock = currentState.is(ModBlocks.COMBO_BLOCK.get());

                for (ComboType comboType : ComboType.values()) {
                    boolean patternValid = ComboBlockEntity.isPatternValid(level, sPos, comboType);
                    boolean matchesType = hasComboBlock
                            && currentState.getValue(ComboBlock.COMBO_TYPE) == comboType;

                    if (patternValid && !matchesType) {
                        if (!hasComboBlock && !currentState.isAir()) {
                            continue;
                        }

                        level.setBlock(
                                sPos,
                                ModBlocks.COMBO_BLOCK.get()
                                        .defaultBlockState()
                                        .setValue(ComboBlock.COMBO_TYPE, comboType),
                                3
                        );
                        break;

                    } else if (!patternValid && matchesType) {
                        level.destroyBlock(sPos, false);
                        break;
                    }
                }
            }
        }
    }

    public boolean hasFuel() {
        return fuel > 0.0f || !fuelSlot.getStackInSlot(0).isEmpty();
    }

    public boolean isAffectedByCircle(LivingEntity entity) {
        if (entity instanceof Player player) {
            return !blacklistedPlayers.contains(player.getGameProfile().getName());
        }
        if (entity instanceof Enemy) {
            return allowHostileMobs;
        }
        return allowPassiveMobs;
    }

    private static int countCirclesInRange(Level level, BlockPos centerPos, int radius) {
        int count = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > radius + 0.5) continue;

                for (int y = -1; y <= 1; y++) {
                    BlockPos p = centerPos.offset(x, y, z);
                    if (level.getBlockState(p).is(ModBlocks.MAGIC_CIRCLE_BLOCK.get())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.magic_ritual_mod.center_fuel");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public void onDataPacket(Connection net,
                             ClientboundBlockEntityDataPacket pkt,
                             HolderLookup.Provider registries) {
        loadAdditional(pkt.getTag(), registries);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
        buffer.writeBoolean(allowHostileMobs);
        buffer.writeBoolean(allowPassiveMobs);
        buffer.writeCollection(blacklistedPlayers, (buf, s) -> buf.writeUtf(s));
        buffer.writeBoolean(particlesEnabled);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CenterFuelMenu(id, playerInv, fuelSlot,
                ContainerLevelAccess.create(level, worldPosition),
                worldPosition, allowHostileMobs, allowPassiveMobs, blacklistedPlayers);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("particlesEnabled", particlesEnabled);
        tag.putFloat("fuel", fuel);
        tag.putInt("fuelAccumulator", fuelAccumulator);
        tag.put("inventory", fuelSlot.serializeNBT(registries));
        tag.putBoolean("allowHostileMobs", allowHostileMobs);
        tag.putBoolean("allowPassiveMobs", allowPassiveMobs);

        ListTag list = new ListTag();
        for (String s : blacklistedPlayers) list.add(StringTag.valueOf(s));
        tag.put("blacklistedPlayers", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        particlesEnabled = tag.getBoolean("particlesEnabled");
        fuel = tag.getFloat("fuel");
        fuelAccumulator = tag.getInt("fuelAccumulator");
        fuelSlot.deserializeNBT(registries, tag.getCompound("inventory"));
        allowHostileMobs = tag.getBoolean("allowHostileMobs");
        allowPassiveMobs = tag.getBoolean("allowPassiveMobs");

        blacklistedPlayers.clear();
        ListTag list = tag.getList("blacklistedPlayers", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            blacklistedPlayers.add(list.getString(i));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void dropContents() {
        if (level != null) {
            for (int i = 0; i < fuelSlot.getSlots(); i++) {
                ItemStack stack = fuelSlot.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(
                            level,
                            worldPosition.getX(),
                            worldPosition.getY(),
                            worldPosition.getZ(),
                            stack
                    );
                }
            }
        }
    }

    public ItemStackHandler getFuelSlot() {
        return fuelSlot;
    }
}
