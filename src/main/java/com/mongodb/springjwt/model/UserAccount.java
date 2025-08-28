package com.mongodb.springjwt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("users")
public class UserAccount {
    @Id private String id;

    @Indexed(unique = true)
    private String username;

    private String password;        // BCrypt-hashed
    private Set<String> roles;      // e.g. ["USER", "ADMIN"]

    // getters/setters...
}
