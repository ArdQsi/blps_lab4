package com.webapp.repository;

import com.webapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findUserByEmail(String email);
    Optional<UserEntity> findUserByLogin(String login);
    UserEntity findUserById(Long id);
}
