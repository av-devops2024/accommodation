package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.ImageRepository;
import com.devops.accommodation.service.interfaces.IImageService;
import com.devops.accommodation.utils.ImageUtils;
import ftn.devops.db.Image;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final Logger logger = LoggerFactory.getLogger(ImageService.class);
    @Autowired
    private final ImageRepository imageRepository;

    @Override
    public Image addImage(MultipartFile imageFile) throws IOException {
        logger.info("Create image");
        Image image = new Image();
        image.setImageData(imageFile.getBytes());
        image.setName(imageFile.getOriginalFilename());
        image.setType(imageFile.getContentType());
        imageRepository.save(image);
        return image;
    }

    @Override
    public void decompressImage(Image image) {
        try {
            logger.info("Decompress image");
            image.setImageData(ImageUtils.decompressImage(image.getImageData()));
        } catch (DataFormatException | IOException exception) {
            logger.error("Decompress image failed");
            image.setImageData(new byte[0]);
        }
    }
}