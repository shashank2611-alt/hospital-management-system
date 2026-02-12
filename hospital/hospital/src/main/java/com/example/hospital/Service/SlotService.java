package com.example.hospital.Service;

import com.example.hospital.Module.UserSlot;
import com.example.hospital.DTO.UserSlotDTO;
import com.example.hospital.Module.weekDays;
import com.example.hospital.Repository.SlotRepository;
import com.example.hospital.Module.DoctorDetails;
import com.example.hospital.Repository.DrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlotService {

    private final SlotRepository slotRepository;
    private final DrRepository drRepository;

    public SlotService(SlotRepository slotRepository,
                       DrRepository drRepository) {
        this.slotRepository = slotRepository;
        this.drRepository = drRepository;
    }

    // ================= CREATE SINGLE SLOT =================
    @Transactional
    public void createSlot(UserSlotDTO dto) {

        if(dto.getDrId()== null){
            throw new RuntimeException("Doctor Id not found");
        }

        DoctorDetails doctor= drRepository.findById(dto.getDrId()).orElseThrow(()->new RuntimeException("Doctor not found"));

        if (dto.getSlotDate()==null){
            throw new RuntimeException("Slot Date is required");
        }

        UserSlot slot = new UserSlot();
        slot.setSlotDate(dto.getSlotDate());

        // ✅ derive day from date
        slot.setDay(
                weekDays.valueOf(
                        dto.getSlotDate().getDayOfWeek().name()
                )
        );

        slot.setTimeSlot(dto.getTimeSlot());
        slot.setMode(dto.getMode());
        slot.setDoctor(doctor);
        slot.setBooked(false);

        slotRepository.save(slot);
    }

    // ================= CREATE MULTIPLE SLOTS =================
    @Transactional
    public void createMultipleSlots(List<UserSlotDTO> dtoList) {
        for (UserSlotDTO dto : dtoList) {
            createSlot(dto);
        }
    }

    // ================= GET DOCTOR SLOTS =================
    @Transactional(readOnly = true)
    public List<UserSlot> getDoctorSlots(Integer doctorId) {
        return slotRepository.findByDoctorId(doctorId);
    }

    // ================= GET AVAILABLE SLOTS =================
    @Transactional(readOnly = true)
    public List<UserSlot> getAvailableSlots() {
        return slotRepository.findByBookedFalse();
    }

    // ================= GET SLOT BY ID =================
    @Transactional(readOnly = true)
    public UserSlot getSlotById(Integer slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
    }

    // ================= DELETE SLOT =================
    @Transactional
    public void deleteSlot(Integer slotId) {
        UserSlot slot = getSlotById(slotId);

        if (slot.isBooked()) {
            throw new RuntimeException("Cannot delete a booked slot");
        }

        slotRepository.delete(slot);
    }
}
