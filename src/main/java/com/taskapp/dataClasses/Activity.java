package com.taskapp.dataClasses;

import java.sql.Timestamp;
import java.util.Objects;

public final class Activity {
    private final String user;
    private final String label;
    private final Timestamp ts;
    private final String file;
    private final String comment;

    public Activity(
            String user,
            String label,
            Timestamp ts,
            String file,
            String comment
    ) {
        this.user = user;
        this.label = label;
        this.ts = ts;
        this.file = file;
        this.comment = comment;
    }

    public String user() {
        return user;
    }

    public String label() {
        return label;
    }

    public Timestamp ts() {
        return ts;
    }

    public String file() {
        return file;
    }

    public String comment() {
        return comment;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Activity) obj;
        return Objects.equals(this.user, that.user) &&
                Objects.equals(this.label, that.label) &&
                Objects.equals(this.ts, that.ts) &&
                Objects.equals(this.file, that.file) &&
                Objects.equals(this.comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, label, ts, file, comment);
    }

    @Override
    public String toString() {
        return "Activity[" +
                "user=" + user + ", " +
                "label=" + label + ", " +
                "ts=" + ts + ", " +
                "file=" + file + ", " +
                "comment=" + comment + ']';
    }

    public Activity setUser(String user) {
        return new Activity(user, label(), ts(), file(), comment());
    }
}
