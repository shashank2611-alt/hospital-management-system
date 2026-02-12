package com.example.hospital.Service;

import com.example.hospital.Repository.DrRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrService {

    private final DrRepository drRepository;

    public DrService(DrRepository drRepository) {
        this.drRepository = drRepository;
    }

    public List<String> getDepartments() {
        return drRepository.findAllDepartment();
    }
}
