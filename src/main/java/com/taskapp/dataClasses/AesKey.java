package com.taskapp.dataClasses;

import java.util.Objects;

public final class AesKey {
    private final String key;
    private final String iv;

    public AesKey(
            String key,
            String iv
    ) {
        this.key = key;
        this.iv = iv;
    }

    public String key() {
        return key;
    }

    public String iv() {
        return iv;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AesKey) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.iv, that.iv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, iv);
    }

    @Override
    public String toString() {
        return "AesKey[" +
                "key=" + key + ", " +
                "iv=" + iv + ']';
    }
}
