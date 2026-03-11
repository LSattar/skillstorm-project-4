package com.skillstorm.animalshelter.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.skillstorm.animalshelter.exceptions.OAuthUsePasswordRequiredException;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.services.JwtService;
import com.skillstorm.animalshelter.services.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);
    private static final String STATE_PREFIX_LINK = "link.";

    private final UserService userService;
    private final JwtService jwtService;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(UserService userService, JwtService jwtService,
            @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl.endsWith("/") ? frontendUrl : frontendUrl + "/";
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String state = request.getParameter("state");
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String sub = getAttribute(oauth2User, "sub");
        String email = getAttribute(oauth2User, "email");
        String name = getAttribute(oauth2User, "name");

        if (state != null && state.startsWith(STATE_PREFIX_LINK)) {
            handleLinkFlow(request, response, state.substring(STATE_PREFIX_LINK.length()), sub);
            return;
        }

        try {
            User user = userService.findOrCreateFromGoogle(sub, email, name);
            List<String> roles = userService.getRoleNamesByUserId(user.getId());
            String token = jwtService.createToken(user.getId(), user.getUsername(), roles);
            String redirectUrl = frontendUrl + "auth/callback#token=" + token;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (OAuthUsePasswordRequiredException e) {
            log.warn("OAuth use-password required: {}", e.getMessage());
            String redirectUrl = frontendUrl + "auth/callback?error=use_password";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }

    private void handleLinkFlow(HttpServletRequest request, HttpServletResponse response, String linkToken, String googleSub) throws IOException {
        Claims claims = jwtService.parseAndValidate(linkToken);
        if (claims == null || !Boolean.TRUE.equals(claims.get("link", Boolean.class))) {
            log.warn("Invalid or expired link state token");
            getRedirectStrategy().sendRedirect(request, response, frontendUrl + "auth/callback?error=oauth_denied");
            return;
        }
        UUID userId = jwtService.getUserIdFromClaims(claims);
        if (userId == null) {
            getRedirectStrategy().sendRedirect(request, response, frontendUrl + "auth/callback?error=oauth_denied");
            return;
        }
        userService.linkGoogle(userId, googleSub);
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "auth/callback?linked=true");
    }

    private static String getAttribute(OAuth2User user, String name) {
        Object v = user.getAttribute(name);
        return v != null ? v.toString() : null;
    }
}
