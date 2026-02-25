package com.example.hospital.Controller;

import com.example.hospital.DTO.DetailDTO;
import com.example.hospital.DTO.MyAppointmentDTO;
import com.example.hospital.Module.UserAppointment;
import com.example.hospital.DTO.UserAppointmentDTO;
import com.example.hospital.Service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/my/{patientId}")
    public List<MyAppointmentDTO> myBookings(@PathVariable int patientId) {
        return appointmentService.getMyBookings(patientId);
    }

    @PostMapping("/book")
    public UserAppointment book(@RequestBody UserAppointmentDTO dto) {
        return appointmentService.bookAppointment(
                dto.getPatientId(),
                dto.getDoctorId(),
                dto.getSlotId(),
                dto.getReason()
        );
    }
    @PutMapping("/cancel/{appointmentId}/{patientId}")
    public String cancelAppointment(@PathVariable int appointmentId,
                                    @PathVariable int patientId) {
        appointmentService.cancelAppointment(appointmentId, patientId);
        return "Appointment cancelled successfully";
    }

    @GetMapping("/slot-details/{slotId}")
    public DetailDTO getSlotDetails(@PathVariable Integer slotId) {
        return appointmentService.getSlot(slotId);
    }

    @PostMapping("/doctor/delay")
    public ResponseEntity<String> handleDelay(@RequestParam int doctorId, @RequestParam int minutes) {
        String message = appointmentService.delayDoctor(doctorId, minutes);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/arrived/{appointmentId}")
    public String arrived(@PathVariable Integer appointmentId) {
        return appointmentService.markArrived(appointmentId);
    }
}