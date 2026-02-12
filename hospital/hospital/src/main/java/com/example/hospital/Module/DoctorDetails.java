package com.example.hospital.Module;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "doctor_details")
public class DoctorDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;

    @Column(unique = true, nullable = false)
    private String mobile;

    private String password;

    private String role;

    private String department;

    @Column(name = "dr_code")
    private String drCode;
}
