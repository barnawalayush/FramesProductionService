package com.barnawal.framesproductionservice.service.serviceImpl;

import com.barnawal.framesproductionservice.service.FrameStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Primary
public class LocalFileSystemStorageService implements FrameStorageService {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private AtomicInteger counter = new AtomicInteger(0);

    private final String storageDirectory = "/Users/abarnawal/frames/";

    public LocalFileSystemStorageService() {
        try {
            Files.createDirectories(Paths.get(storageDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Directory creation Problem : ", e);
        }
    }

    @Override
    public String storeFrame(byte[] frameData) {

        String fileName = "frame_" + counter.getAndIncrement() + ".jpg";
        Path filePath = Paths.get(storageDirectory, fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(frameData);
            kafkaProducerService.sendFrameURL(String.valueOf(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store frame", e);
        }
        return filePath.toString();
    }

}
