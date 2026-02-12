package com.example.hospital.Repository;

import com.example.hospital.Module.UserSlot;
import com.example.hospital.Module.weekDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SlotRepository extends JpaRepository<UserSlot, Integer> {

    List<UserSlot> findByDoctorId(Integer doctorId);

    List<UserSlot> findByBookedFalse();

    @Query("SELECT DISTINCT s.doctor.department FROM UserSlot s")
    List<String> findDistinctByDept();

    @Query("""
        SELECT DISTINCT s.day
        FROM UserSlot s
        WHERE s.doctor.id = :doctorId
          AND s.booked = false
    """)
    List<weekDays> findAvailableDays(@Param("doctorId") Integer doctorId);
}
