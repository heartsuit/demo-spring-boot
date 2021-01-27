package com.heartsuit.controller;

import com.heartsuit.api.keycloak.Keycloak;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @Author Heartsuit
 * @Date 2021-01-15
 */
@RestController
@Slf4j
public class HelloController {
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    Keycloak keycloak;

    @GetMapping(value = "/")
    public String index(Principal principal) {
        return "Welcome " + principal;
    }

    @GetMapping(value = "/user/reg")
    public String registration() {
        ClientRegistration keycloakRegistration = this.clientRegistrationRepository.findByRegistrationId("keycloak");
        log.info(keycloakRegistration.toString());
        return keycloakRegistration.toString();
    }

    @GetMapping(value = "/user/token")
    public OAuth2AccessToken accessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken;
    }

    @GetMapping(value = "/user/info")
    public String info() {
        String profile = keycloak.getProfile();
        log.info(keycloak.getProfile());
        return profile;
    }
}
