package com.skillstorm.animalshelter.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String frontendUrl;

    public OAuth2LoginFailureHandler(@Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.frontendUrl = frontendUrl.endsWith("/") ? frontendUrl : frontendUrl + "/";
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String redirectUrl = frontendUrl + "auth/callback?error=oauth_denied";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
