package com.example.auth.service;


import com.example.auth.entities.Session;
import com.example.auth.entities.User;
import com.example.auth.repositories.SessionRepository;
import lombok.*;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    private final int SESSION_LIMIT = 2;


    public void generateNewSession(User user , String RefreshToken){
        List<Session> sessions = sessionRepository.findByUser(user);

        if(sessions.size() >= SESSION_LIMIT){
            sessions.sort(Comparator.comparing(Session::getLastusedAt));
            Session lastUsed = sessions.getFirst();
            sessionRepository.delete(lastUsed);

        }

        Session newSession = Session.builder()
                .user(user)
                .refreshToken(RefreshToken)
                .build();


        sessionRepository.save(newSession);

    }

    public void ValidateSession(String refreshToken){
        Session s = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("RefreshToken not Found"));

        s.setLastusedAt(
                LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
        );

        sessionRepository.save(s);
    }

    public void deleteSession(String refreshToken){
        Session s = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("RefreshToken not Found"));
        sessionRepository.delete(s);
    }


}
