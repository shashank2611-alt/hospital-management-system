package com.example.hospital.Controller;

import com.example.hospital.Module.DoctorDetails;
import com.example.hospital.Repository.DrRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DrController {

    private final DrRepository drRepository;

    public DrController(DrRepository drRepository) {
        this.drRepository = drRepository;
    }

    @GetMapping("/all")
    public List<DoctorDetails> getAllDoctors() {
        return drRepository.findAll();
    }

    @GetMapping("/department/{dept}")
    public List<DoctorDetails> getDoctorsByDepartment(
            @PathVariable String dept) {
        return drRepository.findByDepartment(dept);
    }
}
