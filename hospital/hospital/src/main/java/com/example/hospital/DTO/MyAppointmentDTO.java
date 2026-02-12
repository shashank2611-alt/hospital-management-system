package com.example.hospital.DTO;

import lombok.Data;

@Data
public class MyAppointmentDTO {
    private int id;
    private String doctorName;
    private String department;
    private String slotDate;
    private String day;
    private String timeSlot;
    private String reason;
    private String status;
}
