package com.photoshare.auth_service.repository;

import com.photoshare.auth_service.entity.User;
import com.photoshare.auth_service.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.BitSet;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByProviderAndProviderSubject(
            com.photoshare.auth_service.enums.AuthProvider provider, String providerSubject);

    boolean existsByUsernameIgnoreCase(String result);

}
