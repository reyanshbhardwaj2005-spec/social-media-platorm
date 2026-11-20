package com.photoshare.social_serivce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private ClientHttpRequestInterceptor bearerTokenInterceptor() {

        return (request, body, execution) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
            }
            return execution.execute(request, body);
        };
    }

    @Bean
    RestClient userRestClient(@Value("${app.clients.user-service-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(bearerTokenInterceptor())
                .build();
    }

    @Bean
    RestClient postRestClient(@Value("${app.clients.post-service-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(bearerTokenInterceptor())
                .build();
    }
}