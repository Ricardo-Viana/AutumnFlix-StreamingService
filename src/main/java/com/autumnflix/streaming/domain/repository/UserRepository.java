package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CustomJpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdentificationDocument_Value(String identificationDocumentValue);
}
