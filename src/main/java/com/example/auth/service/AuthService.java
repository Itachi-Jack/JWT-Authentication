package com.example.auth.service;

import com.example.auth.dto.LogInRequest;
import com.example.auth.dto.LogInResponseDTO;
import com.example.auth.dto.SignUpRequest;
import com.example.auth.entities.User;
import com.example.auth.repositories.UserRepository;
import com.example.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final UserService userService;

    private final SessionService sessionService;





    public void signUp(SignUpRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email is already registered");

        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword
        );
        userRepository.save(user);

    }

    public LogInResponseDTO LogIn(LogInRequest login){
        String email = login.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid email"));

        boolean passwordMatches = passwordEncoder.matches(login.getPassword() , user.getPassword());

        if(!passwordMatches){
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        sessionService.generateNewSession(user , refreshToken);




        return new LogInResponseDTO(user.getId() , accessToken , refreshToken);

    }

    public LogInResponseDTO refreshAccessToken(String refreshToken) {
        Long id = jwtService.extractId(refreshToken);

        sessionService.ValidateSession(refreshToken);
        User user = userService.getUserById(id);
        String accessToken = jwtService.generateAccessToken(user);

        return new LogInResponseDTO(user.getId() , accessToken , refreshToken);
    }
}
