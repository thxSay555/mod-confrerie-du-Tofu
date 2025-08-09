package fr.wakfu.allies;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AllyCapability implements IAllyCapability {
    private final Set<UUID> allies = new HashSet<>();

    @Override
    public Set<UUID> getAllies() {
        return allies;
    }

    @Override
    public boolean isAlly(UUID uuid) {
        return uuid != null && allies.contains(uuid);
    }

    @Override
    public void addAlly(UUID uuid) {
        if (uuid != null) allies.add(uuid);
    }

    @Override
    public void removeAlly(UUID uuid) {
        if (uuid != null) allies.remove(uuid);
    }

    @Override
    public void clear() {
        allies.clear();
    }
}
