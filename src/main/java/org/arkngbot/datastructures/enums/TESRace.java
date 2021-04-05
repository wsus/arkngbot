package org.arkngbot.datastructures.enums;

import org.arkngbot.datastructures.NameAware;

public enum TESRace implements NameAware {

    ALTMER ("Altmer", false),
    ARGONIAN ("Argonian", false),
    ASHLANDER ("Ashlander", true),
    BOSMER ("Bosmer", false),
    BRETON ("Breton", true),
    DUNMER ("Dunmer", true),
    IMPERIAL ("Imperial", true),
    KHAJIIT ("Khajiit", false),
    NORD ("Nord", true),
    ORC ("Orc", true),
    REACHMAN ("Reachman", false),
    REDGUARD ("Redguard", true);

    private final String name;

    private final boolean hasSurname;

    TESRace(String name, boolean hasSurname) {
        this.name = name;
        this.hasSurname = hasSurname;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isHasSurname() {
        return hasSurname;
    }
}
