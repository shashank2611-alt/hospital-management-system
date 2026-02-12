package com.example.hospital.Repository;

import com.example.hospital.Module.DoctorDetails;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DrRepository extends JpaRepository<DoctorDetails, Integer> {

    List<DoctorDetails> findByDepartment(String department);

    @Query("SELECT DISTINCT d.department FROM DoctorDetails d")
    List<String> findAllDepartment();


    Optional<DoctorDetails> findByMobile(String mobile);
}

