package fr.skill.capability;

import java.util.List;

public interface IPlayerSkills {
    List<String> getUnlocked();
    void setUnlocked(List<String> ids);
    boolean addSkill(String id);
    boolean removeSkill(String id);
    boolean hasSkill(String id);
}
