package com.example.hospital.Service;

import com.example.hospital.DTO.DetailDTO;
import com.example.hospital.DTO.MyAppointmentDTO;
import com.example.hospital.Module.UserAppointment;
import com.example.hospital.Repository.AppointmentRepository;
import com.example.hospital.Module.UserSlot;
import com.example.hospital.Repository.SlotRepository;
import com.example.hospital.Module.DoctorDetails;
import com.example.hospital.Module.UserLogin;
import com.example.hospital.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private EmailService emailService;

    private final AppointmentRepository appointmentRepo;
    private final SlotRepository slotRepo;
    private final UserRepository userRepo;

    public AppointmentService(AppointmentRepository appointmentRepo,
                              SlotRepository slotRepo,
                              UserRepository userRepo) {
        this.appointmentRepo = appointmentRepo;
        this.slotRepo = slotRepo;
        this.userRepo = userRepo;
    }

    // ===================== BOOK APPOINTMENT =====================

    @Transactional
    public UserAppointment bookAppointment(int patientId,
                                           int doctorId,
                                           int slotId,
                                           String reason) {

        UserLogin patient = userRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        UserSlot slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isBooked()) {
            throw new RuntimeException("Slot already booked");
        }

        DoctorDetails doctor = slot.getDoctor();

        if (doctor.getId() != doctorId) {
            throw new RuntimeException("Slot does not belong to this doctor");
        }

        // Mark slot booked
        slot.setBooked(true);
        slotRepo.save(slot);

        // Create appointment
        UserAppointment appointment = new UserAppointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setReason(reason);
        appointment.setStatus("BOOKED");

        UserAppointment saved = appointmentRepo.save(appointment);

        // ===== SEND PROFESSIONAL EMAIL =====

        try {
            String date = slot.getSlotDate().toString();
            String time = slot.getTimeSlot();

            // -------- Doctor Email --------

            String doctorBody = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial; background:#f2f2f2; padding:20px;">
            <div style="max-width:600px;margin:auto;background:white;
                        padding:20px;border-radius:10px;
                        border-top:5px solid #2a7fff;">

            <h2 style="color:#2a7fff;">📌 New Appointment Booked</h2>

            <p><b>Patient:</b> %s</p>
            <p><b>Mobile:</b> %s</p>
            <p><b>Reason:</b> %s</p>

            <div style="background:#f9f9f9;padding:10px;">
                <p><b>Date:</b> %s</p>
                <p><b>Time:</b> %s</p>
            </div>

            <p style="font-size:12px;color:gray;">
            Hospital Management System
            </p>

            </div>
            </body>
            </html>
            """.formatted(
                    patient.getName(),
                    patient.getMobile(),
                    reason,
                    date,
                    time
            );

            emailService.emailSender(
                    doctor.getEmail(),
                    "New Appointment Scheduled",
                    doctorBody
            );

            // -------- Patient Email --------

            String patientBody = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial; background:#f2f2f2; padding:20px;">
            <div style="max-width:600px;margin:auto;background:white;
                        padding:20px;border-radius:10px;
                        border-top:5px solid #28a745;">

            <h2 style="color:#28a745;">✅ Appointment Confirmed</h2>

            <p>Hello <b>%s</b>,</p>

            <p>Your appointment is successfully booked.</p>

            <div style="background:#f9f9f9;padding:10px;">
                <p><b>Doctor:</b> %s</p>
                <p><b>Department:</b> %s</p>
                <p><b>Date:</b> %s</p>
                <p><b>Time:</b> %s</p>
                <p><b>Reason:</b> %s</p>
            </div>

            <p>Please reach 10 minutes early.</p>

            </div>
            </body>
            </html>
            """.formatted(
                    patient.getName(),
                    doctor.getName(),
                    doctor.getDepartment(),
                    date,
                    time,
                    reason
            );

            emailService.emailSender(
                    patient.getEmail(),
                    "Appointment Confirmation",
                    patientBody
            );

        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }

        return saved;
    }

    // ===================== MY BOOKINGS =====================

    public List<MyAppointmentDTO> getMyBookings(int patientId) {

        return appointmentRepo.findByPatientId(patientId)
                .stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .map(a -> {

                    MyAppointmentDTO dto = new MyAppointmentDTO();

                    dto.setId(a.getId());
                    dto.setDoctorName(a.getDoctor().getName());
                    dto.setDepartment(a.getDoctor().getDepartment());
                    dto.setSlotDate(a.getSlot().getSlotDate().toString());
                    dto.setDay(a.getSlot().getDay().name());
                    dto.setTimeSlot(a.getSlot().getTimeSlot());
                    dto.setReason(a.getReason());
                    dto.setStatus(a.getStatus());

                    return dto;
                })
                .toList();
    }

    // ===================== CANCEL APPOINTMENT =====================

    @Transactional
    public void cancelAppointment(int appointmentId, int patientId) {

        UserAppointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException("Appointment not found..!"));

        if (appointment.getPatient().getId() != patientId) {
            throw new RuntimeException("You are not allowed to cancel this appointment");
        }

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException("Already cancelled");
        }

        UserSlot slot = appointment.getSlot();
        slot.setBooked(false);
        slotRepo.save(slot);

        appointmentRepo.delete(appointment);
    }

    // ===================== GET SLOT DETAIL =====================

    public DetailDTO getSlot(Integer slotId) {

        UserAppointment appointment =
                appointmentRepo.findBySlotId(slotId);

        if (appointment == null) {
            throw new RuntimeException("Slot is not booked");
        }

        DetailDTO dto = new DetailDTO();

        dto.setSlotId(slotId);
        dto.setPatientName(appointment.getPatient().getName());
        dto.setPatientMobile(appointment.getPatient().getMobile());
        dto.setReason(appointment.getReason());
        dto.setSlotDate(appointment.getSlot().getSlotDate().toString());
        dto.setTimeSlot(appointment.getSlot().getTimeSlot());
        dto.setDay(String.valueOf(appointment.getSlot().getDay()));

        return dto;
    }
}