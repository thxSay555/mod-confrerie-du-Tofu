package fr.skill.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class PlayerSkillsProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final PlayerSkills instance = new PlayerSkills();

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityPlayerSkills.PLAYER_SKILLS;
    }

    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityPlayerSkills.PLAYER_SKILLS) {
            return CapabilityPlayerSkills.PLAYER_SKILLS.cast(instance);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        instance.deserializeNBT(nbt);
    }
}
