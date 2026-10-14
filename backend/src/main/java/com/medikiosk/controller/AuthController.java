package com.medikiosk.controller;

import com.medikiosk.dto.AuthDTOs.*;
import com.medikiosk.model.User;
import com.medikiosk.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setFullName(user.getFullName());
        resp.setRole(user.getRole());
        resp.setDepartment(user.getDepartment());
        resp.setLicenseNo(user.getLicenseNo());
        resp.setIsActive(user.getIsActive());
        resp.setCreatedAt(user.getCreatedAt());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        User user = authService.register(request);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setFullName(user.getFullName());
        resp.setRole(user.getRole());
        resp.setDepartment(user.getDepartment());
        resp.setLicenseNo(user.getLicenseNo());
        resp.setIsActive(user.getIsActive());
        resp.setCreatedAt(user.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
