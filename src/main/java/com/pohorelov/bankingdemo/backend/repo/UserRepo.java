package com.pohorelov.bankingdemo.backend.repo;

import com.pohorelov.bankingdemo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

}
