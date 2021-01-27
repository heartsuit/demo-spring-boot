package com.heartsuit.api.keycloak;

import com.heartsuit.api.ApiBinding;

/**
 * @Author Heartsuit
 * @Date 2021-01-27
 */
public class Keycloak extends ApiBinding {
    private String userInfoEndpointUri;

    public Keycloak(String accessToken, String userInfoEndpointUri) {
        super(accessToken);
        this.userInfoEndpointUri = userInfoEndpointUri;
    }

    public String getProfile() {
        return restTemplate.getForObject(userInfoEndpointUri, String.class);
    }
}
