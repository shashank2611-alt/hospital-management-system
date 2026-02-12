package com.example.hospital.Repository;

import com.example.hospital.Module.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserLogin, Integer> {

    Optional<UserLogin> findByMobile(String mobile);
    Optional<UserLogin> findByEmail(String email);

}
