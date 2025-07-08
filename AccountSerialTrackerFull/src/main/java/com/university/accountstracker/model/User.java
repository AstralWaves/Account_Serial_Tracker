package com.university.accountstracker.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password; // Will store ENCODED password
    private String email;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();
    private boolean enabled = true;

    public User() {}

    public User(String username, String password, String email, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }
    
    public User(String username, String password, String... roles) { // For admin
        this.username = username;
        this.password = password; // Raw password initially
        this.email = null; 
         for (String role : roles) {
            this.roles.add(role);
        }
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; } // Service will set encoded password
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
