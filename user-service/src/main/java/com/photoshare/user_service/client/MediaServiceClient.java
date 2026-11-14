package com.photoshare.user_service.client;

import com.photoshare.user_service.dto.response.MediaUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class MediaServiceClient {
    private final DiscoveryClient discoveryClient;
    private final String mediaServiceId;

    public MediaServiceClient(
            DiscoveryClient discoveryClient,
            @Value("${app.media.service-id:media-service}") String mediaServiceId) {
        this.discoveryClient = discoveryClient;
        this.mediaServiceId = mediaServiceId;
    }

    public MediaUploadResponse uploadProfilePicture(
            byte[] fileBytes,
            String originalFilename,
            String contentType,
            String accessToken) {

        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("Profile image is empty.");
        }

        List<ServiceInstance> instances = discoveryClient.getInstances(mediaServiceId);
        if (instances == null || instances.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Media Service is not currently available through service discovery."
            );
        }

        // Eureka may return multiple instances. Use the first available instance for now.
        ServiceInstance instance = instances.get(0);
        String filename = (originalFilename == null || originalFilename.isBlank())
                ? "profile-image" : originalFilename;
        MediaType partContentType = MediaType.APPLICATION_OCTET_STREAM;
        if (contentType != null && !contentType.isBlank()) {
            try {
                partContentType = MediaType.parseMediaType(contentType);
            } catch (IllegalArgumentException ignored) {
                // Media Service performs the authoritative file type validation.
            }
        }

        MultipartBodyBuilder multipart = new MultipartBodyBuilder();
        multipart.part("file", new ByteArrayResource(fileBytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                })
                .contentType(partContentType)
                .filename(filename);
        MultiValueMap<String, HttpEntity<?>> body = multipart.build();

        try {
            MediaUploadResponse response = RestClient.builder().baseUrl(instance.getUri().toString()).build()
                    .post()
                    .uri("/api/v1/media/upload?mediaCategory=PROFILE_PICTURE")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(MediaUploadResponse.class);

            if (response == null || response.getMediaId() == null ||
                    response.getImageUrl() == null || response.getImageUrl().isBlank()) {
                throw new IllegalStateException("Media Service returned an incomplete upload response.");
            }
            return response;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Media Service could not upload the profile image. Check Media Service logs.",
                    ex
            );
        }
    }
}
