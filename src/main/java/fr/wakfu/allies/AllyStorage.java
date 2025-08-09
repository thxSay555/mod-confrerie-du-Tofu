package fr.wakfu.allies;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.capabilities.Capability;
import java.util.UUID;

public class AllyStorage implements Capability.IStorage<IAllyCapability> {

    private static final String TAG_ALLIES = "WakfuAllies";

    @Override
    public NBTBase writeNBT(Capability<IAllyCapability> capability, IAllyCapability instance, net.minecraft.util.EnumFacing side) {
        NBTTagList list = new NBTTagList();
        for (UUID u : instance.getAllies()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("uuid", u.toString());
            list.appendTag(tag);
        }
        NBTTagCompound out = new NBTTagCompound();
        out.setTag(TAG_ALLIES, list);
        return out;
    }

    @Override
    public void readNBT(Capability<IAllyCapability> capability, IAllyCapability instance, net.minecraft.util.EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound)) return;
        NBTTagCompound comp = (NBTTagCompound) nbt;
        if (!comp.hasKey(TAG_ALLIES)) return;
        NBTTagList list = comp.getTagList(TAG_ALLIES, 10);
        instance.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            if (tag.hasKey("uuid")) {
                try {
                    UUID u = UUID.fromString(tag.getString("uuid"));
                    instance.addAlly(u);
                } catch (Exception ignored) {}
            }
        }
    }
}
