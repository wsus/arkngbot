package org.arkngbot.datastructures.enums;

import org.arkngbot.datastructures.NameAware;

public enum TESSex implements NameAware {
    FEMALE ("Female"),
    MALE ("Male");

    private final String name;

    TESSex(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
