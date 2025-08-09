package fr.skill.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class PlayerSkills implements IPlayerSkills, INBTSerializable<NBTTagCompound> {

    private final List<String> ids = new ArrayList<>();

    @Override
    public List<String> getUnlocked() {
        return new ArrayList<>(ids);
    }

    @Override
    public void setUnlocked(List<String> ids) {
        this.ids.clear();
        if (ids != null) this.ids.addAll(ids);
    }

    @Override
    public boolean addSkill(String id) {
        if (id == null || ids.contains(id)) return false;
        ids.add(id);
        return true;
    }

    @Override
    public boolean removeSkill(String id) {
        return ids.remove(id);
    }

    @Override
    public boolean hasSkill(String id) {
        return id != null && ids.contains(id);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (String s : ids) list.appendTag(new NBTTagString(s));
        tag.setTag("skills", list);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        ids.clear();
        if (nbt == null) return;
        NBTTagList list = nbt.getTagList("skills", 8);
        for (int i = 0; i < list.tagCount(); i++) ids.add(list.getStringTagAt(i));
    }
}
