package com.example.magic_ritual_mod.block.custom;

import net.minecraft.util.StringRepresentable;

public enum ComboType implements StringRepresentable {
    FOG("fog"),
    INVERTED("inverted"),
    FORCEFIELD("forcefield");

    private final String name;

    ComboType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
