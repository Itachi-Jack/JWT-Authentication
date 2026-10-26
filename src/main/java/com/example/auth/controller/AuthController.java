package com.example.auth.controller;


import com.example.auth.dto.LogInRequest;
import com.example.auth.dto.LogInResponseDTO;
import com.example.auth.dto.SignUpRequest;
import com.example.auth.entities.User;
import com.example.auth.security.JwtService;
import com.example.auth.service.AuthService;
import com.example.auth.service.SessionService;
import com.example.auth.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final SessionService sessionService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest){
        authService.signUp(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponseDTO> LogIn(@RequestBody LogInRequest logInRequest , HttpServletResponse response){
        LogInResponseDTO logIn = authService.LogIn(logInRequest);



        Cookie cookie = new Cookie("refresh" , logIn.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);



        return ResponseEntity.ok(logIn);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LogInResponseDTO> refreshToken(HttpServletRequest request ){
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refresh".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationException("RefreshToken not found") {
                });
        LogInResponseDTO logInResponseDTO = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(logInResponseDTO);


    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSession(String refreshToken){

        sessionService.deleteSession(refreshToken);

        return ResponseEntity.ok("User logged out successfully");



    }


}
