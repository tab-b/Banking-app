package com.app.banking.model;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fName;

    @Column(nullable = false)
    private String lName;

    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "owner")
    private List<Account> accounts = new ArrayList<>();

    public AppUser(String fName, String lName, String email, Set<String> roles, String passwordHash, List<Account> accounts) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.roles = roles;
        this.createdAt = Instant.now();
        this.passwordHash = passwordHash;
        this.accounts = accounts;
    }

    protected AppUser() {

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

    public Long getId() {return id;}

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}