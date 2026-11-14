package com.photoshare.media_serivce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadImage(MultipartFile file, String folder) {
        try {
            return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image",
                    "overwrite", false
            ));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to upload image to cloud storage.", ex);
        }
    }

    public void deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
            Object status = result.get("result");
            if (status != null && !"ok".equals(status) && !"not found".equals(status)) {
                throw new IllegalStateException("Cloudinary did not confirm image deletion.");
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to delete image from cloud storage.", ex);
        }
    }
}
