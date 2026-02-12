package com.example.hospital.Service;

import com.example.hospital.DTO.ResetDTO;
import com.example.hospital.Module.DoctorDetails;
import com.example.hospital.Module.ResetToken;
import com.example.hospital.Repository.DrRepository;
import com.example.hospital.DTO.UserDTO;
import com.example.hospital.Module.UserLogin;
import com.example.hospital.Repository.ResetRepository;
import com.example.hospital.Repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ResetRepository resetToken;

    private final UserRepository userRepository;
    private final DrRepository drRepository;

    public UserService(UserRepository userRepository,
                       DrRepository drRepository) {
        this.userRepository = userRepository;
        this.drRepository = drRepository;
    }

    @Transactional
    public void book(int id) {
        // future logic
    }

    // ================= LOGIN / REGISTER =================
    public Object createUser(UserDTO dto) {

        if (dto.getMobile() == null || dto.getMobile().isEmpty()) {
            throw new RuntimeException("Mobile is required");
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        boolean isRegisterRequest =
                dto.getName() != null && !dto.getName().isEmpty();

        // ======== CHECK DOCTOR ========
        DoctorDetails doctor =
                drRepository.findByMobile(dto.getMobile()).orElse(null);

        if (doctor != null) {

            // Doctor LOGIN
            if (!isRegisterRequest) {
                if (!doctor.getPassword().equals(dto.getPassword())) {
                    throw new RuntimeException("Invalid Doctor Credentials");
                }
                return doctor;
            }

            // Doctor already exists
            throw new RuntimeException("Doctor already registered");
        }

        // ======== CHECK PATIENT ========
        UserLogin user =
                userRepository.findByMobile(dto.getMobile()).orElse(null);

        // PATIENT LOGIN
        if (!isRegisterRequest) {
            if (user == null) {
                throw new RuntimeException("User not registered");
            }
            if (!user.getPassword().equals(dto.getPassword())) {
                throw new RuntimeException("Invalid Credentials");
            }
            return user;
        }

        // ======== REGISTER LOGIC ========
        if ("DOCTOR".equalsIgnoreCase(dto.getRole())) {

            if (dto.getDepartment() == null || dto.getDepartment().isEmpty()
                    || dto.getDrCode() == null || dto.getDrCode().isEmpty()) {
                throw new RuntimeException("Department and Doctor Code required");
            }

            DoctorDetails newDoctor = new DoctorDetails();
            newDoctor.setName(dto.getName());
            newDoctor.setEmail(dto.getEmail());
            newDoctor.setMobile(dto.getMobile());
            newDoctor.setPassword(dto.getPassword());
            newDoctor.setDepartment(dto.getDepartment());
            newDoctor.setDrCode(dto.getDrCode());
            newDoctor.setRole("DOCTOR");

            return drRepository.save(newDoctor);
        }

        if (user != null) {
            throw new RuntimeException("User already registered");
        }

        UserLogin newUser = new UserLogin();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setMobile(dto.getMobile());
        newUser.setPassword(dto.getPassword());
        newUser.setRole("PATIENT");

        return userRepository.save(newUser);
    }

    // ================= UPDATE PROFILE =================
    public Object update(int id, UserLogin updateUser) {

        UserLogin ur = userRepository.findById(id).orElse(null);

        if (ur != null) {
            ur.setName(updateUser.getName());
            ur.setEmail(updateUser.getEmail());
            ur.setMobile(updateUser.getMobile());

            if (updateUser.getPassword() != null &&
                    !updateUser.getPassword().isBlank()) {

                ur.setPassword(updateUser.getPassword());
            }

            return userRepository.save(ur);
        }

        DoctorDetails dr = drRepository.findById(id).orElse(null);

        if (dr != null) {
            dr.setName(updateUser.getName());
            dr.setEmail(updateUser.getEmail());
            dr.setMobile(updateUser.getMobile());

            if (updateUser.getPassword() != null &&
                    !updateUser.getPassword().isBlank()) {

                dr.setPassword(updateUser.getPassword());
            }

            return drRepository.save(dr);
        }

        return null;
    }

    public List<UserLogin> getAllUser() {
        return userRepository.findAll();
    }

    public UserLogin findByMobile(String mobile) {
        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    // ================= SEND RESET MAIL =================
    public String sendReset(ResetDTO resetDTO) {

        String userEmail = resetDTO.getEmail();

        UserLogin user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("Email is not registered"));

        String token = UUID.randomUUID().toString();

        ResetToken rt = new ResetToken();
        rt.setEmail(userEmail);
        rt.setToken(token);
        rt.setExpiry(LocalDateTime.now().plusMinutes(5));

        resetToken.save(rt);

        String link =
                "http://localhost:8080/user/ResetPassword?token=" + token;

        // ======== HTML BUTTON MAIL ========
        String html = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Reset Your Password</title>
    </head>
    <body style="margin:0;padding:0;background-color:#f4f7fa;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif">
        
        <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color:#f4f7fa">
            <tr>
                <td style="padding:40px 20px">
                    
                    <!-- Main Container -->
                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="max-width:600px;margin:0 auto;background-color:#ffffff;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.05)">
                        
                        <!-- Header -->
                        <tr>
                            <td style="padding:40px 40px 30px;text-align:center;border-bottom:1px solid #e8eef3">
                                <h1 style="margin:0;color:#1a1a1a;font-size:24px;font-weight:600;letter-spacing:-0.5px">
                                    Password Reset Request
                                </h1>
                            </td>
                        </tr>
                        
                        <!-- Body Content -->
                        <tr>
                            <td style="padding:40px">
                                
                                <p style="margin:0 0 20px;color:#4a5568;font-size:16px;line-height:1.6">
                                    Hi there,
                                </p>
                                
                                <p style="margin:0 0 30px;color:#4a5568;font-size:16px;line-height:1.6">
                                    We received a request to reset your password. Click the button below to create a new password:
                                </p>
                                
                                <!-- CTA Button -->
                                <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%">
                                    <tr>
                                        <td style="text-align:center;padding:10px 0 30px">
                                            <a href="%s" 
                                               style="display:inline-block;
                                                      background-color:#007bff;
                                                      color:#ffffff;
                                                      text-decoration:none;
                                                      padding:14px 32px;
                                                      border-radius:8px;
                                                      font-size:16px;
                                                      font-weight:600;
                                                      letter-spacing:0.3px;
                                                      transition:background-color 0.3s ease">
                                                Reset Password
                                            </a>
                                        </td>
                                    </tr>
                                </table>
                          
                                <!-- Warning Box -->
                                <div style="background-color:#fff5f5;
                                            border-left:4px solid #fc8181;
                                            border-radius:6px;
                                            padding:16px;
                                            margin:0 0 30px">
                                    <p style="margin:0;color:#742a2a;font-size:14px;line-height:1.5">
                                        <strong>⏱️ This link expires in 5 minutes</strong><br>
                                        For your security, please reset your password as soon as possible.
                                    </p>
                                </div>
                                
                                <p style="margin:0 0 10px;color:#4a5568;font-size:14px;line-height:1.6">
                                    If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.
                                </p>
                                
                            </td>
                        </tr>
                        
                        <!-- Footer -->
                        <tr>
                            <td style="padding:30px 40px;background-color:#f7fafc;border-top:1px solid #e8eef3;border-radius:0 0 12px 12px">
                                
                                <p style="margin:0 0 10px;color:#718096;font-size:13px;line-height:1.5;text-align:center">
                                    Need help? Contact our support team at 
                                    <a href="mailto:support@yourcompany.com" style="color:#3182ce;text-decoration:none">
                                        support@hsp.com
                                    </a>
                                </p>
                                
                                <p style="margin:0;color:#a0aec0;font-size:12px;text-align:center">
                                    © 2024 MGM. All rights reserved.
                                </p>
                                
                            </td>
                        </tr>
                        
                    </table>
                    
                </td>
            </tr>
        </table>
        
    </body>
    </html>
    """.formatted(link, link, link);

        emailService.emailSender(
                userEmail,
                "Reset Your Password",
                html
        );

        return "Reset link sent successfully";}

    // ================= RESET PASSWORD =================
    public String resetPass(ResetDTO dto) {

        ResetToken tr =
                resetToken.findByToken(dto.getToken());

        if (tr == null) {
            throw new RuntimeException("Invalid Link");
        }

        if (tr.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link is expired");
        }

        UserLogin userLogin =
                userRepository.findByEmail(tr.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        userLogin.setPassword(dto.getNewPassword());

        userRepository.save(userLogin);

        resetToken.delete(tr);

        return "Password updated successfully";
    }
}