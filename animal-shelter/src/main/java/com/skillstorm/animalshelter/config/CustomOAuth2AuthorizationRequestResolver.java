package com.skillstorm.animalshelter.config;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

/**
 * When the request has a linkToken parameter (e.g. from /api/auth/link-google redirect),
 * sets the OAuth2 state to "link.{linkToken}" so the success handler can identify the link flow.
 */
@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String PARAM_LINK_TOKEN = "linkToken";
    private static final String STATE_PREFIX_LINK = "link.";

    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest resolved = delegate.resolve(request);
        if (resolved == null) {
            return null;
        }
        String linkToken = request.getParameter(PARAM_LINK_TOKEN);
        if (linkToken != null && !linkToken.isBlank()) {
            return OAuth2AuthorizationRequest.from(resolved)
                    .state(STATE_PREFIX_LINK + linkToken)
                    .build();
        }
        return resolved;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest resolved = delegate.resolve(request, clientRegistrationId);
        if (resolved == null) {
            return null;
        }
        String linkToken = request.getParameter(PARAM_LINK_TOKEN);
        if (linkToken != null && !linkToken.isBlank()) {
            return OAuth2AuthorizationRequest.from(resolved)
                    .state(STATE_PREFIX_LINK + linkToken)
                    .build();
        }
        return resolved;
    }
}
