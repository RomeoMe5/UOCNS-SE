package ru.stepanov.uocns.network.common;

public class TNocParameter {
    private String fKey;
    private String fValue;

    public TNocParameter(String aKey, String aValue) {
        this.fKey = aKey;
        this.fValue = aValue;
    }

    public String getName() {
        return this.fKey;
    }

    public String getValue() {
        return this.fValue;
    }
}

