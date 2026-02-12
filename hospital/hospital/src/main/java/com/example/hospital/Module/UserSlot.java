package com.example.hospital.Module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(
        name = "user_slot",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"slot_date", "time_slot", "doctor_id"})
        }
)
public class UserSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Doctor-selected date
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    // Auto-derived day name
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private weekDays day;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(nullable = false, length = 20)
    private String mode; // SINGLE / WEEK

    @Column(name = "is_booked", nullable = false)
    private boolean booked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnore
    private DoctorDetails doctor;


}