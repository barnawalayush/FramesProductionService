package com.barnawal.framesproductionservice.controller;

import com.barnawal.framesproductionservice.service.VideoFrameProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoFrameProvider ffmpegFrameProvider;

    @Autowired
    private VideoFrameProvider webrtcFrameProvider;

    @PostMapping("/start")
    public String startStreaming(@RequestParam String source, @RequestParam String type) {
        if ("rtmp".equalsIgnoreCase(type)) {
            ffmpegFrameProvider.start(source);
            return "Streaming started from " + source;
        } else if ("webrtc".equalsIgnoreCase(type)) {
            webrtcFrameProvider.start(source);
            return "Streaming started from " + source;
        } else {
            throw new IllegalArgumentException("Unknown source type: " + type);
        }
    }

    @PostMapping("/stop")
    public String stopStreaming(@RequestParam String type) {
        if ("rtmp".equalsIgnoreCase(type)) {
            ffmpegFrameProvider.stop();
            return "Streaming stopped";
        } else if ("webrtc".equalsIgnoreCase(type)) {
            webrtcFrameProvider.stop();
            return "Streaming stopped";
        } else {
            throw new IllegalArgumentException("Unknown source type: " + type);
        }
    }
}

