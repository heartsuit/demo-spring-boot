package com.heartsuit.api.github;

import com.heartsuit.api.ApiBinding;

/**
 * @Author Heartsuit
 * @Date 2021-01-15
 */
public class Github extends ApiBinding {
    private static final String BASE_URL = "https://api.github.com";

    public Github(String accessToken) {
        super(accessToken);
    }
    public String getProfile() {
        return restTemplate.getForObject(BASE_URL + "/user", String.class);
    }
}
