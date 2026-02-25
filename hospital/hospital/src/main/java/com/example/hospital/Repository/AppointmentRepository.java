package com.example.hospital.Repository;

import com.example.hospital.Module.UserAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;   // ✅ IMPORTANT
import java.util.List;

public interface AppointmentRepository extends JpaRepository<UserAppointment, Integer> {

    List<UserAppointment> findByPatientId(int patientId);

    @Query("SELECT a FROM UserAppointment a WHERE a.slot.id = :slotId")
    UserAppointment findBySlotId(@Param("slotId") Integer slotId);

    List<UserAppointment> findByDoctor_IdAndSlot_SlotDateAndStatusOrderBySlot_StartTime(
            int doctorId,
            LocalDate slotDate,
            String status
    );
}