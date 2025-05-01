package fr.wakfu.stats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import javax.annotation.Nullable;

public class StatsProvider implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(IPlayerStats.class)
    public static final Capability<IPlayerStats> PLAYER_STATS = null;

    private final IPlayerStats instance = new PlayerStats();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PLAYER_STATS;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == PLAYER_STATS ? (T) instance : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) PLAYER_STATS.getStorage().writeNBT(PLAYER_STATS, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        PLAYER_STATS.getStorage().readNBT(PLAYER_STATS, instance, null, nbt);
    }
}
