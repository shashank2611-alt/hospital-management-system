package com.example.hospital.DTO;

import lombok.Data;

@Data
public class DetailDTO {
    private Integer appointmentId;
    private Integer slotId;
    private String patientName;
    private String patientMobile;
    private String reason;
    private String slotDate;
    private String timeSlot;
    private String day;
}
