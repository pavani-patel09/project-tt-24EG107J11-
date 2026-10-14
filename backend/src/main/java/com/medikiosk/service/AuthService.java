package com.medikiosk.service;

import com.medikiosk.config.JwtService;
import com.medikiosk.dto.AuthDTOs.*;
import com.medikiosk.model.User;
import com.medikiosk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuditService auditService;

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is inactive");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole(), user.getId());
        auditService.log(user.getId(), null, "USER_LOGIN", "User " + user.getUsername() + " logged in with role " + user.getRole());

        return new TokenResponse(token, user.getRole(), user.getUsername(), user.getFullName(), user.getId(), user.getDepartment());
    }

    public User register(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()) ||
            (request.getEmail() != null && userRepository.existsByEmail(request.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email is already registered");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getRole() != null ? request.getRole() : "staff",
                request.getDepartment() != null ? request.getDepartment() : "General Medicine",
                request.getLicenseNo()
        );

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        return null;
    }
}
