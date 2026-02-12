package com.example.hospital.Module;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;
    @Column(unique = true, nullable = false)
    private String mobile;
    @Column(nullable = false)
    private String password;
    private String role;
}
