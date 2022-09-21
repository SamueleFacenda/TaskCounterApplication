package com.taskapp.dataClasses;

import java.util.Objects;

public final class Auth {
    private final String user;
    private final String psw;

    public Auth(
            String user,
            String psw
    ) {
        this.user = user;
        this.psw = psw;
    }

    public String user() {
        return user;
    }

    public String psw() {
        return psw;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Auth) obj;
        return Objects.equals(this.user, that.user) &&
                Objects.equals(this.psw, that.psw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, psw);
    }

    @Override
    public String toString() {
        return "Auth[" +
                "user=" + user + ", " +
                "psw=" + psw + ']';
    }
}
