package com.example.magic_ritual_mod.item;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.item.custom.RitualDaggerItem;
import com.example.magic_ritual_mod.item.custom.SpiritMapItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicRitualMod.MODID);

    public static final DeferredItem<Item> RITUAL_DAGGER = registerItem("ritual_dagger",
            () -> new RitualDaggerItem(Tiers.IRON, new Item.Properties()
                    .durability(250)
                    .stacksTo(1)));

    public static final DeferredItem<Item> SPIRIT_STONE = registerItem("spirit_stone",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> SPIRIT_MAP = registerItem("spirit_map",
            () -> new SpiritMapItem(new Item.Properties()
                    .stacksTo(1)));

    public static DeferredItem<Item> registerItem(String name, Supplier<Item> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }
}
