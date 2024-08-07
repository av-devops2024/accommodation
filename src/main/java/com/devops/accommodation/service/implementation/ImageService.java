package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.ImageRepository;
import com.devops.accommodation.service.interfaces.IImageService;
import com.devops.accommodation.utils.ImageUtils;
import ftn.devops.db.Image;
import ftn.devops.log.LogType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    @Autowired
    private LogClientService logClientService;
    @Autowired
    private final ImageRepository imageRepository;

    @Override
    public Image addImage(MultipartFile imageFile) throws IOException {
        logClientService.sendLog(LogType.INFO, "Create image", imageFile);
        Image imageToSave = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .imageData(ImageUtils.compressImage(imageFile.getBytes()))
                .build();
        imageRepository.save(imageToSave);
        return imageToSave;
    }

    @Override
    public void decompressImage(Image image) {
        try {
            logClientService.sendLog(LogType.INFO, "Decompress image", image.getId());
            image.setImageData(ImageUtils.decompressImage(image.getImageData()));
        } catch (DataFormatException | IOException exception) {
            logClientService.sendLog(LogType.ERROR, "Decompress image failed: " + exception.getMessage(), image.getId());
            image.setImageData(new byte[0]);
        }
    }
}