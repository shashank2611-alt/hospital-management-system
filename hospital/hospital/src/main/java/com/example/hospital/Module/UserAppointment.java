package com.example.hospital.Module;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private UserLogin patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorDetails doctor;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private UserSlot slot;         

    private String reason;
    private String status;
}