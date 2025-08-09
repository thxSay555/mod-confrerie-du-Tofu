package fr.wakfu.allies;

import java.util.Set;
import java.util.UUID;

public interface IAllyCapability {
    Set<UUID> getAllies();
    boolean isAlly(UUID uuid);
    void addAlly(UUID uuid);
    void removeAlly(UUID uuid);
    void clear();
}
