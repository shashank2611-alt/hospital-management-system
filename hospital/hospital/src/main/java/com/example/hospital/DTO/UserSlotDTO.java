package com.example.hospital.DTO;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserSlotDTO {

    private Integer id;
    private Integer drId;

    private LocalDate slotDate;

    private String timeSlot;
    private String mode;
}
