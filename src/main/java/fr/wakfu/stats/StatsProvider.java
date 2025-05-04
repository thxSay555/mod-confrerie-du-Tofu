 // src/main/java/fr/wakfu/stats/StatsProvider.java
package fr.wakfu.stats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public class StatsProvider implements ICapabilitySerializable<NBTTagCompound> {
    @CapabilityInject(IPlayerStats.class)
    public static Capability<IPlayerStats> PLAYER_STATS = null;

    private final IPlayerStats instance = PLAYER_STATS.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing side) {
        return cap == PLAYER_STATS;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing side) {
        return cap == PLAYER_STATS ? (T) instance : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        if (PLAYER_STATS == null || PLAYER_STATS.getStorage() == null) return new NBTTagCompound();
        return (NBTTagCompound) PLAYER_STATS.getStorage().writeNBT(PLAYER_STATS, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (PLAYER_STATS != null && PLAYER_STATS.getStorage() != null)
            PLAYER_STATS.getStorage().readNBT(PLAYER_STATS, instance, null, nbt);
    }
}