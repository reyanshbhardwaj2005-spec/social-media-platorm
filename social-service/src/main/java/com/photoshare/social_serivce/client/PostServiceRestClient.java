package com.photoshare.social_serivce.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PostServiceRestClient {

    private final RestClient client;
    private final String existsPath;
    private final String feedPath;

    public PostServiceRestClient(RestClient postRestClient,
                             @Value("${app.clients.post-exists-path}") String existsPath,
                             @Value("${app.clients.post-feed-path}") String feedPath) {

        this.client=postRestClient;
        this.existsPath=existsPath;
        this.feedPath=feedPath;
    }

    public void requirePost(UUID postId){

        try {
            Boolean exists = client.get().uri(existsPath, Map.of("postId",postId))
                    .retrieve().body(Boolean.class);
            if(!Boolean.TRUE.equals(exists)) throw new IllegalArgumentException("Post does not exist: "+postId);
        } catch(RestClientResponseException ex) {
            if(ex.getStatusCode().value()==404) throw new IllegalArgumentException("Post does not exist: "+postId);
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String,Object> getFeed(List<UUID> userIds, int page, int size){
        return client.get().uri(uri -> uri.path(feedPath)
                        .queryParam("userIds",userIds).queryParam("page",page).queryParam("size",size).build())
                .retrieve().body(new ParameterizedTypeReference<Map<String,Object>>() {});
    }
}
