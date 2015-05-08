package com.github.sionin;

import java.util.Map;
import java.util.Set;

public class UserWrapper {

    final String username;
    final String email;
    final Map<String, Object> additionalData;
    final Set<String> groups;

    public UserWrapper(String username, String email, Map<String, Object> additionalData, Set<String> groups) {
        this.username = username;
        this.email = email;
        this.additionalData = additionalData;
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "UserWrapper{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", additionalData=" + additionalData +
                ", groups=" + groups +
                '}';
    }
}
