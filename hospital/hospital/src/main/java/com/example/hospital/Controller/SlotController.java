package com.example.hospital.Controller;

import com.example.hospital.Module.UserSlot;
import com.example.hospital.DTO.UserSlotDTO;
import com.example.hospital.Module.weekDays;
import com.example.hospital.Repository.SlotRepository;
import com.example.hospital.Service.SlotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/slot")
public class SlotController {

    private final SlotService slotService;
    private final SlotRepository slotRepository;

    public SlotController(SlotService slotService, SlotRepository slotRepository) {
        this.slotService = slotService;
        this.slotRepository = slotRepository;
    }

    @PostMapping("/create")
    public String createSlot(@Valid @RequestBody UserSlotDTO dto) {
        slotService.createSlot(dto);
        return ("Slot created successfully");
    }

    @GetMapping("/doctor/{doctorId}")
    public List<UserSlot> getDoctorSlots(@PathVariable Integer doctorId) {
        return slotService.getDoctorSlots(doctorId);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSlot(@PathVariable Integer id) {
        slotService.deleteSlot(id);
        return "Slot deleted successfully";
    }

    @GetMapping("/available")
    public List<UserSlot> getAvailable() {
        return slotService.getAvailableSlots();
    }

    @PostMapping("/create-bulk")
    public ResponseEntity<String> createBulkSlots(@Valid @RequestBody List<@Valid UserSlotDTO> dtoList) {
        slotService.createMultipleSlots(dtoList);
        return ResponseEntity.ok("All slots created successfully");
    }

    @GetMapping("/get/{id}")
    public UserSlot getSlot(@PathVariable Integer id){
        return slotRepository.findById(id).orElseThrow(()-> new RuntimeException("slot not found"));
    }

    @GetMapping("/department")
    public List<String> getDepartments(){
        return slotRepository.findDistinctByDept();
    }

    @GetMapping("/doctor/{doctorId}/days")
    public List<weekDays>getDoctorAvailableDays(
            @PathVariable Integer doctorId) {
        return slotRepository.findAvailableDays(doctorId);
    }

    @GetMapping("/SplitSlot")
    public String triggerSplit() {
        slotService.splitSlot();
        return "Slots updated successfully!";
    }
}