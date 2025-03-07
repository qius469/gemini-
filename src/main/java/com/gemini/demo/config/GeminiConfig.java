package com.gemini.demo.config;

import com.google.cloud.aiplatform.v1.GenerativeModel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeminiConfig {
    private static final Logger logger = LoggerFactory.getLogger(GeminiConfig.class);
    
    @Getter
    private final GenerativeModel geminiProModel;
    @Getter
    private final GenerativeModel geminiVisionModel;
    
    private static final String PROJECT_ID = "your-project-id"; // Replace with your project ID
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-pro";
    private static final String VISION_MODEL_NAME = "gemini-pro-vision";

    public GeminiConfig() {
        // Initialize Gemini Pro model for text
        this.geminiProModel = GenerativeModel.builder()
                .setProjectId(PROJECT_ID)
                .setLocation(LOCATION)
                .setModelName(MODEL_NAME)
                .build();

        // Initialize Gemini Pro Vision model for images
        this.geminiVisionModel = GenerativeModel.builder()
                .setProjectId(PROJECT_ID)
                .setLocation(LOCATION)
                .setModelName(VISION_MODEL_NAME)
                .build();

        logger.info("Gemini models initialized successfully");
    }
}
