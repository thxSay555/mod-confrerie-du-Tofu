package fr.wakfu.allies;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class AllyProvider implements ICapabilitySerializable<NBTTagCompound> {
    private final AllyCapability impl = new AllyCapability();

    @Override
    public boolean hasCapability(Capability<?> capability, net.minecraft.util.EnumFacing facing) {
        return capability == AllyRegistry.ALLY_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, net.minecraft.util.EnumFacing facing) {
        if (capability == AllyRegistry.ALLY_CAPABILITY) {
            return (T) impl;
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) AllyRegistry.ALLY_CAPABILITY.getStorage().writeNBT(AllyRegistry.ALLY_CAPABILITY, impl, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        AllyRegistry.ALLY_CAPABILITY.getStorage().readNBT(AllyRegistry.ALLY_CAPABILITY, impl, null, nbt);
    }
}
