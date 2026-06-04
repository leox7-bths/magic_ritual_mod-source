package com.example.magic_ritual_mod.item.custom;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class RitualDaggerItem extends SwordItem {
    public RitualDaggerItem(Tier tier, Properties properties) {
        super(tier, properties
                .attributes(SwordItem.createAttributes(tier, 1, -2.4f)));
    }
}
