package org.arkngbot.datastructures.enums;

public enum TESSex {
    FEMALE ("Female"),
    MALE ("Male");

    private final String name;

    TESSex(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
