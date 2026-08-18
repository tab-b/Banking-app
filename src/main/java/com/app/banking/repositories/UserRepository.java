package com.app.banking.repositories;

import com.app.banking.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import java.lang.annotation.Native;
import java.util.Optional;


public interface UserRepository extends JpaRepository<AppUser, Long> {
    @NativeQuery(value = """
            SELECT 1
            FROM users
            WHERE email = :email""")
    Optional<AppUser> getUserByEmail(@Param("email") String email);

    @NativeQuery(value = """
        SELECT EXISTS (
            SELECT 1
            FROM users
            WHERE email = :email
        )
    """)
    boolean existsByEmail(@Param("email") String email);
}
