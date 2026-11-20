package com.photoshare.social_serivce.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;
import java.util.UUID;

@Component
public class UserServiceRestClient {

    private final RestClient client;
    private final String existsPath;

    public UserServiceRestClient(RestClient userRestClient,
                                 @Value("${app.clients.user-exists-path}") String existsPath) {
        this.client=userRestClient;
        this.existsPath=existsPath;
    }

    public void requireUser(UUID userId){
        try {
            Boolean exists = client.get().uri(existsPath, Map.of("userId",userId))
                    .retrieve().body(Boolean.class);
            if(!Boolean.TRUE.equals(exists)) throw new IllegalArgumentException("User does not exist: "+userId);
        } catch(RestClientResponseException ex) {
            if(ex.getStatusCode().value()==404) throw new IllegalArgumentException("User does not exist: "+userId);
            throw ex;
        }
    }
}
