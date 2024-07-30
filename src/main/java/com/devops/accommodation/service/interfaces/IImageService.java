package com.devops.accommodation.service.interfaces;

import ftn.devops.db.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageService {
    Image addImage(MultipartFile imageFile) throws IOException;

    void decompressImage(Image image);
}
