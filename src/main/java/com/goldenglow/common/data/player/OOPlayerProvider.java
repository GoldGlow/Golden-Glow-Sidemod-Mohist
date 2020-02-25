package com.goldenglow.common.data.player;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OOPlayerProvider implements ICapabilitySerializable<NBTBase> {

    @CapabilityInject(IPlayerData.class)
    public static final Capability<IPlayerData> OO_DATA = null;

    IPlayerData instance = OO_DATA.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == OO_DATA;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == OO_DATA ? OO_DATA.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return OO_DATA.getStorage().writeNBT(OO_DATA, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        OO_DATA.getStorage().readNBT(OO_DATA, this.instance, null, nbt);
    }
}
