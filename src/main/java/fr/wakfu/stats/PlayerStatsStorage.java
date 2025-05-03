package fr.wakfu.stats;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerStatsStorage implements IStorage<IPlayerStats> {
    @Override
    public NBTTagCompound writeNBT(Capability<IPlayerStats> cap, IPlayerStats stats, EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        // Stats de base
        tag.setInteger("Force",      stats.getForce());
        tag.setInteger("Stamina",    stats.getStamina());
        tag.setInteger("Wakfu",      stats.getWakfu());
        tag.setInteger("Agility",    stats.getAgility());
        // Leveling
        tag.setInteger("Level",      stats.getLevel());
        tag.setInteger("SkillPoints",stats.getSkillPoints());
        tag.setInteger("Xp",         stats.getXp());
        tag.setInteger("XpToNext",   stats.getXpToNextLevel());
        // Intensity
        tag.setInteger("Intensity",  stats.getIntensity());
        // Currents & multipliers si tu veux
        tag.setFloat("CurrentWakfu",   stats.getCurrentWakfu());
        tag.setFloat("CurrentStamina", stats.getCurrentStamina());
        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerStats> cap, IPlayerStats stats, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound) nbt;
        // Stats de base
        stats.setForce     (tag.getInteger("Force"));
        stats.setStamina   (tag.getInteger("Stamina"));
        stats.setWakfu     (tag.getInteger("Wakfu"));
        stats.setAgility   (tag.getInteger("Agility"));
        // Leveling
        stats.setLevel     (tag.getInteger("Level"));
        stats.setSkillPoints(tag.getInteger("SkillPoints"));
        stats.setXp        (tag.getInteger("Xp"));
        stats.setXpToNextLevel(tag.getInteger("XpToNext"));
        // Intensity
        stats.setIntensity(tag.getInteger("Intensity"));
        // Currents
        stats.setCurrentWakfu  (tag.getFloat("CurrentWakfu"));
        stats.setCurrentStamina(tag.getFloat("CurrentStamina"));
    }
}
