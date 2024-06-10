package com.barnawal.framesproductionservice.service.serviceImpl;

import com.barnawal.framesproductionservice.service.FrameStorageService;
import com.barnawal.framesproductionservice.service.VideoFrameProvider;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
@Primary
public class FFMpegFrameProvider implements VideoFrameProvider {

    private FFmpegFrameGrabber frameGrabber;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private FrameStorageService frameStorageService;

    @Override
    public void start(String source) {
        frameGrabber = new FFmpegFrameGrabber(source);
        try {
            frameGrabber.start();
            Frame frame;
            while((frame = frameGrabber.grab()) != null){
                // Ensure the frame has image data
                if (frame.image != null) {
                    // Convert the image to a byte array
                    BufferedImage bufferedImage;
                    try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                        bufferedImage = converter.getBufferedImage(frame);
                    }

                    // Convert BufferedImage to byte array
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                    byte[] frameData = byteArrayOutputStream.toByteArray();

                    // Store frame and send message
                    String filePath = frameStorageService.storeFrame(frameData);
                    System.out.println(filePath);
//                    kafkaProducerService.sendFrameURL(filePath);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error while grabbing frames", e);
        } finally {
            try {
                frameGrabber.stop();
            } catch (Exception e) {
                // Handle stop exception
            }
        }
    }

    @Override
    public void stop() {
        try {
            if (frameGrabber != null) {
                frameGrabber.stop();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error stopping frame grabber", e);
        }
    }
}
