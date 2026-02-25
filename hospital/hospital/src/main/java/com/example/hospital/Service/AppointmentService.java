package com.example.hospital.Service;
import java.time.LocalDate;
import com.example.hospital.DTO.DetailDTO;
import com.example.hospital.DTO.MyAppointmentDTO;
import com.example.hospital.Module.UserAppointment;
import com.example.hospital.Repository.AppointmentRepository;
import com.example.hospital.Module.UserSlot;
import com.example.hospital.Repository.SlotRepository;
import com.example.hospital.Module.DoctorDetails;
import com.example.hospital.Module.UserLogin;
import com.example.hospital.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

        dto.setAppointmentId(appointment.getId());
        dto.setSlotId(slotId);
        dto.setPatientName(appointment.getPatient().getName());
        dto.setPatientMobile(appointment.getPatient().getMobile());
        dto.setReason(appointment.getReason());
        dto.setSlotDate(appointment.getSlot().getSlotDate().toString());
        dto.setTimeSlot(appointment.getSlot().getTimeSlot());
        dto.setDay(String.valueOf(appointment.getSlot().getDay()));

        return dto;
    }

    // ===================== UPDATE SLOT TIME ===========================

    private void notifyPatient(UserAppointment appt) {
        String body = "Dear " + appt.getPatient().getName() +
                ", your appointment time has been updated to: " +
                appt.getSlot().getTimeSlot();

        emailService.emailSender(
                appt.getPatient().getEmail(),
                "Schedule Update",
                body
        );
    }

    @Transactional
    public String markArrived(Integer appointmentId) {
        log.info("String");
        UserAppointment current = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Prevent double processing
        if ("ARRIVED".equalsIgnoreCase(current.getStatus())) {
           // return "Already processed";
            System.out.println("Already processed");
        }

        // Mark current as arrived
        current.setStatus("ARRIVED");
        appointmentRepo.save(current);

        LocalDate today = LocalDate.now();
        int doctorId = current.getDoctor().getId();

        System.out.println();

        List<UserAppointment> list = appointmentRepo.findAll().stream()
                .filter(a -> a.getDoctor().getId() == (doctorId)).toList();


        // We process the stream and trigger the email ONLY if a patient is found
        // Fixed primitive comparison
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getDoctor().getId()==(doctorId)) // Fixed primitive comparison
                .filter(a -> a.getSlot().getSlotDate().equals(today))
                .filter(a -> "BOOKED".equalsIgnoreCase(a.getStatus()))
                .filter(a -> a.getSlot().getStartTime() != null).min(Comparator.comparing(a -> a.getSlot().getStartTime(),
                        Comparator.nullsLast(Comparator.naturalOrder()))) // This grabs the first person and returns an Optional
                .map(next -> {
                    // This block runs ONLY if someone was found in the queue
                    String email = """
                <h3>Your Turn is Next</h3>
                <p>Dear %s,</p>
                <p>Please proceed to the doctor cabin.</p>
                <p><b>Time:</b> %s</p>
                """.formatted(
                            next.getPatient().getName(),
                            next.getSlot().getTimeSlot()
                    );

                    emailService.emailSender(
                            next.getPatient().getEmail(),
                            "Doctor is Ready for You",
                            email
                    );
                    return "Next patient notified";
                })
                .orElse("No next patient"); // Returns this if the stream was empty
    }

    @Transactional
    public String delayDoctor(int doctorId, int minutes) {

        LocalDate today = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm "); //changes in hh=HH

        List<UserAppointment> appointments =
                appointmentRepo.findByDoctor_IdAndSlot_SlotDateAndStatusOrderBySlot_StartTime(
                        doctorId,
                        today,
                        "BOOKED"
                );

        if (appointments.isEmpty()) {
            return "No appointments found to delay today.";
        }

        LocalTime nextStart = null;

        for (int i = 0; i < appointments.size(); i++) {

            UserAppointment appointment = appointments.get(i);
            UserSlot slot = appointment.getSlot();

            long duration = java.time.Duration
                    .between(slot.getStartTime(), slot.getEndtime())
                    .toMinutes();

            LocalTime newStart;

            if (i == 0) {
                newStart = slot.getStartTime().plusMinutes(minutes);
            } else {
                newStart = nextStart;
            }

            LocalTime newEnd = newStart.plusMinutes(duration);

            slot.setStartTime(newStart);
            slot.setEndtime(newEnd);
            slot.setTimeSlot(newStart.format(format) + " - " + newEnd.format(format));

            nextStart = newEnd;

            sendDelayEmail(appointment);
        }

        return "Successfully delayed " + appointments.size() + " appointments.";
    }
    private void sendDelayEmail(UserAppointment appt) {
        String body = """
        <html>
        <body style="font-family: Arial; border: 1px solid #eee; padding: 20px;">
            <h2 style="color: #e67e22;">🕒 Schedule Update</h2>
            <p>Dear <b>%s</b>,</p>
            <p>Due to an emergency/delay, your appointment with <b>Dr. %s</b> has been rescheduled.</p>
            <div style="background: #fff3e0; padding: 15px; border-radius: 5px; font-size: 1.1em;">
                <b>Your New Appointment Time:</b> %s
            </div>
            <p>We apologize for the inconvenience and appreciate your patience.</p>
        </body>
        </html>
        """.formatted(
                appt.getPatient().getName(),
                appt.getDoctor().getName(),
                appt.getSlot().getTimeSlot()
        );

        emailService.emailSender(
                appt.getPatient().getEmail(),
                "Important: Your Appointment Time has Changed",
                body
        );
    }
}