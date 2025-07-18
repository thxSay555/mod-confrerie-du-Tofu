package fr.wakfu.common.capabilities;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class RaceCapability {
    public static final ResourceLocation RACE_CAPABILITY_ID = new ResourceLocation("wakfu", "race");

    @CapabilityInject(IRace.class)
    public static Capability<IRace> RACE_CAPABILITY = null;

    public interface IRace {
        String getRace();
        void setRace(String race);
        boolean hasRace();
    }

    public static class Storage implements Capability.IStorage<IRace> {
        @Override
        public NBTBase writeNBT(Capability<IRace> capability, IRace instance, EnumFacing side) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("race", instance.getRace());
            return tag;
        }

        @Override
        public void readNBT(Capability<IRace> capability, IRace instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) {
                instance.setRace(((NBTTagCompound) nbt).getString("race"));
            }
        }
    }

    public static class Implementation implements IRace {
        private String race = "";

        @Override
        public String getRace() {
            return race;
        }

        @Override
        public void setRace(String race) {
            this.race = race != null ? race : "";
        }

        @Override
        public boolean hasRace() {
            return !race.isEmpty();
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final IRace instance = new Implementation(); // Création directe

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == RACE_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == RACE_CAPABILITY ? RACE_CAPABILITY.cast(instance) : null;
        }


        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) RACE_CAPABILITY.getStorage().writeNBT(RACE_CAPABILITY, instance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            RACE_CAPABILITY.getStorage().readNBT(RACE_CAPABILITY, instance, null, nbt);
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(
            IRace.class,
            new Storage(),
            Implementation::new
        );
    }
}