package com.heartsuit;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
@Slf4j
public class SpringbootSecurityOpenIDApplication {
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException, AuthException {
//        String providerUrl = "https://www.sfkj119.com/auth/realms/heartsuit/";
//        URI issuerURI = new URI(providerUrl);
//        URL providerConfigurationURL = issuerURI.resolve(".well-known/openid-configuration").toURL();
//        InputStream stream = providerConfigurationURL.openStream();
//        // Read all data from URL
//        String providerInfo = null;
//        try (java.util.Scanner s = new java.util.Scanner(stream)) {
//            providerInfo = s.useDelimiter("\\A").hasNext() ? s.next() : "";
//        }
//        OIDCProviderMetadata providerMetadata = OIDCProviderMetadata.parse(providerInfo);
//        try {
//            URL url = new URI(providerMetadata.getJWKSetURI().toString().replace("http", "http")).toURL();
//            log.debug("Using url {}", url);
//            JSONObject providerRSAJWK = getProviderRSAJWK(url.openStream());
//        } catch (IOException e) {
//            throw new AuthException("Unable to start the Auth" + e.toString());
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
        SpringApplication.run(SpringbootSecurityOpenIDApplication.class, args);
    }

    private static JSONObject getProviderRSAJWK(InputStream is) throws java.text.ParseException {
        // Read all data from stream
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(is)) {
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }
        }

        // Parse the data as json
        String jsonString = sb.toString();
        JSONObject json = (JSONObject) JSONObjectUtils.parse(jsonString);

        // Find the RSA signing key
        JSONArray keyList = (JSONArray) json.get("keys");
        for (Object key : keyList) {
            JSONObject k = (JSONObject) key;
            if (k.get("use").equals("sig") && k.get("kty").equals("RSA")) {
                return k;
            }
        }
        return null;
    }
}
