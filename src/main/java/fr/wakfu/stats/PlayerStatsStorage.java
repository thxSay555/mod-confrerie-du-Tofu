package fr.wakfu.stats;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerStatsStorage implements IStorage<IPlayerStats> {
    @Override
    public NBTTagCompound writeNBT(Capability<IPlayerStats> capability, IPlayerStats instance, EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Force",    instance.getForce());
        tag.setInteger("Stamina",  instance.getStamina());
        tag.setInteger("Wakfu",    instance.getWakfu());
        tag.setInteger("Agility",  instance.getAgility());
        // persister aussi les multiplicateurs si besoin
        tag.setFloat("WakfuMult",   instance.getWakfuMultiplier());
        tag.setFloat("StaminaMult", instance.getStaminaMultiplier());
        tag.setFloat("CurrentWakfu", instance.getCurrentWakfu());
        tag.setFloat("CurrentStamina", instance.getCurrentStamina());
        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerStats> capability, IPlayerStats instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setForce(tag.getInteger("Force"));
        instance.setStamina(tag.getInteger("Stamina"));
        instance.setWakfu(tag.getInteger("Wakfu"));
        instance.setAgility(tag.getInteger("Agility"));
        // restaurer aussi les multiplicateurs
        instance.setWakfuMultiplier(tag.getFloat("WakfuMult"));
        instance.setStaminaMultiplier(tag.getFloat("StaminaMult"));
        instance.setCurrentWakfu(tag.getFloat("CurrentWakfu"));
        instance.setCurrentStamina(tag.getFloat("CurrentStamina"));

    }
}
