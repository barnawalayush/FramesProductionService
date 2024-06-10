package com.barnawal.framesproductionservice.service.serviceImpl;

import com.barnawal.framesproductionservice.config.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public boolean sendFrameURL(String frame_url) {
        try {
            kafkaTemplate.send(AppConstants.FRAME_TOPIC_NAME, frame_url);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
