package fr.skill.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

public class PlayerSkillsStorage implements Capability.IStorage<IPlayerSkills> {

    @Override
    public NBTBase writeNBT(Capability<IPlayerSkills> capability, IPlayerSkills instance, net.minecraft.util.EnumFacing side) {
        if (instance instanceof PlayerSkills) {
            return ((PlayerSkills) instance).serializeNBT();
        }
        return new NBTTagCompound();
    }

    @Override
    public void readNBT(Capability<IPlayerSkills> capability, IPlayerSkills instance, net.minecraft.util.EnumFacing side, NBTBase nbt) {
        if (instance instanceof PlayerSkills && nbt instanceof NBTTagCompound) {
            ((PlayerSkills) instance).deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
