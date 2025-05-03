// src/main/java/fr/wakfu/stats/StatsStorage.java
package fr.wakfu.stats;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class StatsStorage implements Capability.IStorage<IPlayerStats> {
    @Override
    public NBTBase writeNBT(Capability<IPlayerStats> capability, IPlayerStats stats, EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Force", stats.getForce());
        tag.setInteger("Stamina", stats.getStamina());
        tag.setInteger("Wakfu", stats.getWakfu());
        tag.setInteger("Agility", stats.getAgility());
        tag.setInteger("Level", stats.getLevel());
        tag.setInteger("SkillPoints", stats.getSkillPoints());
        tag.setInteger("Xp", stats.getXp());
        tag.setInteger("XpToNext", stats.getXpToNextLevel());
        tag.setInteger("Intensity", stats.getIntensity());
        tag.setFloat("CurrentWakfu", stats.getCurrentWakfu());
        tag.setFloat("CurrentStamina", stats.getCurrentStamina());
        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerStats> capability, IPlayerStats stats, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound) nbt;
        stats.setForce(tag.getInteger("Force"));
        stats.setStamina(tag.getInteger("Stamina"));
        stats.setWakfu(tag.getInteger("Wakfu"));
        stats.setAgility(tag.getInteger("Agility"));
        stats.setLevel(tag.getInteger("Level"));
        stats.setSkillPoints(tag.getInteger("SkillPoints"));
        stats.setXp(tag.getInteger("Xp"));
        stats.setXpToNextLevel(tag.getInteger("XpToNext"));
        stats.setIntensity(tag.getInteger("Intensity"));
        stats.setCurrentWakfu(tag.getFloat("CurrentWakfu"));
        stats.setCurrentStamina(tag.getFloat("CurrentStamina"));
    }
}