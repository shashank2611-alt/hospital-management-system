package com.example.hospital.Repository;

import com.example.hospital.Module.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetRepository extends JpaRepository <ResetToken, Integer>{
    ResetToken findByToken(String token);
}
