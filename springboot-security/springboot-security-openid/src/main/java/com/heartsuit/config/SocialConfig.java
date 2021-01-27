package com.heartsuit.config;

import com.heartsuit.api.keycloak.Keycloak;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

/**
 * @Author Heartsuit
 * @Date 2021-01-15
 */
@Configuration
@Slf4j
public class SocialConfig {
    @Bean
    @RequestScope
    public Keycloak keycloak(OAuth2AuthorizedClientService clientService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = null;
        String userInfoEndpointUri = null;
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
            if (clientRegistrationId.equals("keycloak")) {
                OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
                if (client != null) {
                    accessToken = client.getAccessToken().getTokenValue();
                    userInfoEndpointUri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
                }
                log.info(accessToken);
                log.info(userInfoEndpointUri);
            }
        }
        return new Keycloak(accessToken, userInfoEndpointUri);
    }
}
