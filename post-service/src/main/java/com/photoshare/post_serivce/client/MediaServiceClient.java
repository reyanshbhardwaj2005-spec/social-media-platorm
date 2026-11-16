package com.photoshare.post_serivce.client;

import com.photoshare.post_serivce.dto.media.MediaUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class MediaServiceClient {

    private final DiscoveryClient discoveryClient;
    private final RestClient.Builder restClientBuilder;
    private final String mediaServiceId;

    public MediaServiceClient(
            DiscoveryClient discoveryClient,
            RestClient.Builder restClientBuilder,
            @Value("${app.media.service-id:media-service}") String mediaServiceId) {
        this.discoveryClient = discoveryClient;
        this.restClientBuilder = restClientBuilder;
        this.mediaServiceId = mediaServiceId;
    }

    public MediaUploadResponse uploadPostImage(MultipartFile file, UUID userId, String accessToken) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Post image is required.");
        }

        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            ByteArrayResource imageResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return StringUtils.hasText(file.getOriginalFilename())
                            ? file.getOriginalFilename()
                            : "post-image";
                }
            };

            MediaType contentType = file.getContentType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(file.getContentType());

            bodyBuilder.part("file", imageResource)
                    .contentType(contentType)
                    .filename(imageResource.getFilename());

            MediaUploadResponse response = client()
                    .post()
                    .uri("/api/v1/media/upload?mediaCategory=POST_IMAGE")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(bodyBuilder.build())
                    .retrieve()
                    .body(MediaUploadResponse.class);

            if (response == null || response.getId() == null || !StringUtils.hasText(response.getImageUrl())) {
                throw new IllegalStateException("Media Service returned an incomplete upload response.");
            }

            if (response.getUserId() != null && !response.getUserId().equals(userId)) {
                throw new IllegalStateException("Media Service returned media belonging to a different user.");
            }

            return response;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read the uploaded image.", ex);
        }
    }

    public void deleteMedia(UUID mediaId, String accessToken) {
        client()
                .delete()
                .uri("/api/v1/media/{mediaId}", mediaId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

    private RestClient client() {
        List<ServiceInstance> instances = discoveryClient.getInstances(mediaServiceId);
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("Media Service is not currently registered with Eureka.");
        }

        ServiceInstance instance = instances.get(0);
        return restClientBuilder.baseUrl(instance.getUri().toString()).build();
    }
}
