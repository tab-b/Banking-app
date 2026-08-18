package com.app.banking.model;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String fName;

    @Column(nullable = false)
    private String lName;

    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    private Instant created_at;
    private String passwordHash;

    public User(Long id, String fName, String lName, String email, Set<String> roles, Instant created_at, String passwordHash) {
        Id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.roles = roles;
        this.created_at = created_at;
        this.passwordHash = passwordHash;
    }

    public User() {
    }

    public String getPasswordHash() {
        return passwordHash;
    }


    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public String[] getRoles() {
        return roles;
    }

    public Integer getCreated_at() {
        return created_at;
    }

}
