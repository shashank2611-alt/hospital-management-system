package com.example.hospital.DTO;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UserSlotDTO {

    private Integer id;
    private Integer drId;

    private LocalDate slotDate;

    private String timeSlot;
    private String mode;

    private LocalTime startTime;
    private LocalTime endTime;
    private String appointmentStatus;
}

