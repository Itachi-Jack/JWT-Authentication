package com.example.auth.handlers;

import com.example.auth.entities.User;
import com.example.auth.security.JwtService;
import com.example.auth.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final JwtService jwtService;

    public void onAuthenticationSuccess(HttpServletRequest request , HttpServletResponse response , Authentication authentication) throws IOException, ServletException{
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User OAuth2user = (DefaultOAuth2User) token.getPrincipal();
        log.info("OAuth2 SUCCESS HANDLER CALLED");

        logger.info(OAuth2user.getAttribute("email"));

        String email = OAuth2user.getAttribute("email");
        User user = userService.getByEmail(email);
        if(user == null){
            User newUser = User.builder()
                    .name(OAuth2user.getAttribute("name"))
                    .email(email)
                    .build();
           user = userService.saveUser(newUser);
        }

        String accesstoken = jwtService.generateAccessToken(user);
        log.info("ACtoken success");
        String refreshtoken = jwtService.generateRefreshToken(user);
        log.info("RFtoken success");


        Cookie cookie = new Cookie("RefreshToken" , refreshtoken);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        String frontendUrl = "http://localhost:8080/home.html?token="+accesstoken;
        response.sendRedirect(frontendUrl);



    }

}
