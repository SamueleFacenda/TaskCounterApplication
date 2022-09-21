package com.taskapp.dataClasses;

import java.sql.Timestamp;
import java.util.Objects;

public final class SessionMetadata {
    private final boolean isNew;
    private final Timestamp rsaTimestamp;

    public SessionMetadata(
            boolean isNew,
            Timestamp rsaTimestamp
    ) {
        this.isNew = isNew;
        this.rsaTimestamp = rsaTimestamp;
    }

    public boolean isNew() {
        return isNew;
    }

    public Timestamp rsaTimestamp() {
        return rsaTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SessionMetadata) obj;
        return this.isNew == that.isNew &&
                Objects.equals(this.rsaTimestamp, that.rsaTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNew, rsaTimestamp);
    }

    @Override
    public String toString() {
        return "SessionMetadata[" +
                "isNew=" + isNew + ", " +
                "rsaTimestamp=" + rsaTimestamp + ']';
    }
}
