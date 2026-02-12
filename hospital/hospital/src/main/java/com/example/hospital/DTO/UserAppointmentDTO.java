package com.example.hospital.DTO;

import lombok.Data;

@Data
public class UserAppointmentDTO {

    private int patientId;
    private int doctorId;
    private int slotId;

    private String reason;

}
