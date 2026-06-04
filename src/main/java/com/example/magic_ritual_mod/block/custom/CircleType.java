package com.example.magic_ritual_mod.block.custom;

import net.minecraft.util.StringRepresentable;

public enum CircleType implements StringRepresentable {
    ATTACK("attack"),
    DEFENSE("defense"),
    REGENERATION("regeneration");

    private final String name;

    CircleType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
