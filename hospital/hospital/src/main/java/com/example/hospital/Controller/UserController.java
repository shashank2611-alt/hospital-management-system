package com.example.hospital.Controller;

import com.example.hospital.DTO.ResetDTO;
import com.example.hospital.DTO.UserDTO;
import com.example.hospital.Module.ResetToken;
import com.example.hospital.Module.UserLogin;
import com.example.hospital.Repository.ResetRepository;
import com.example.hospital.Service.UserService;
import com.example.hospital.Service.DrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final DrService drService;
    @Autowired
    private ResetRepository resetToken;

    public UserController(UserService userService, DrService drService) {
        this.userService = userService;
        this.drService = drService;
    }

    // LOGIN / REGISTER
    @PostMapping("/login")
    @ResponseBody
    public Object login(@RequestBody UserDTO dto) {
        return userService.createUser(dto);
    }

    // GET ALL USERS
    @GetMapping("/get")
    @ResponseBody
    public List<UserLogin> getAllUsers() {
        return userService.getAllUser();
    }

    // GET DEPARTMENTS
    @GetMapping("/departments")
    @ResponseBody
    public List<String> getDepartments() {
        return drService.getDepartments();
    }

    // GET USER BY MOBILE
    @GetMapping("/find/{mobile}")
    @ResponseBody
    public UserLogin findByMobile(@PathVariable String mobile) {
        return userService.findByMobile(mobile);
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody UserLogin updateUr){
        Object result = userService.update(id, updateUr);
        if(result == null){
            return ResponseEntity.status(404).body("User not found");
        }
        return  ResponseEntity.ok(result);
    }

    @PostMapping("/ForgotPassword")
    @ResponseBody
    public String forgot (@RequestBody ResetDTO dto){
        return userService.sendReset(dto);
    }

    @PostMapping("/ResetPassword")
    @ResponseBody
    public String reset(@RequestBody ResetDTO dto){
        return userService.resetPass(dto);
    }

    @GetMapping("/ResetPassword")
    public String validateEmailLink(@RequestParam("token") String token){

        ResetToken byToken = resetToken.findByToken(token);

        if(byToken == null){
            return "redirect:/invalid_link.html";
        }

        return "redirect:/hospital.html?token=" + token;
    }

}
