package com.gemini.demo;

import com.gemini.demo.config.GeminiConfig;
import com.gemini.demo.service.AudioTranslationService;
import com.gemini.demo.service.ImageTranslationService;
import com.gemini.demo.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeminiTranslationDemo {
    private static final Logger logger = LoggerFactory.getLogger(GeminiTranslationDemo.class);
    
    private final ImageTranslationService imageTranslationService;
    private final AudioTranslationService audioTranslationService;

    public GeminiTranslationDemo() {
        GeminiConfig geminiConfig = new GeminiConfig();
        this.imageTranslationService = new ImageTranslationService(geminiConfig);
        this.audioTranslationService = new AudioTranslationService(geminiConfig);
    }

    public void processImage(String imagePath, String targetLanguage) {
        try {
            // Validate image file
            if (!FileUtils.isValidFile(imagePath)) {
                logger.error("Image file does not exist or is not readable: {}", imagePath);
                return;
            }

            if (!FileUtils.isSupportedImageFormat(imagePath)) {
                logger.error("Unsupported image format for file: {}", imagePath);
                return;
            }

            // Process image
            logger.info("Processing image: {}", imagePath);
            String basicAnalysis = imageTranslationService.analyzeAndTranslateImage(imagePath, targetLanguage);
            logger.info("Basic image analysis:\n{}", basicAnalysis);

            // Get detailed analysis
            String detailedAnalysis = imageTranslationService.getDetailedImageAnalysis(imagePath, targetLanguage);
            logger.info("Detailed image analysis:\n{}", detailedAnalysis);

        } catch (Exception e) {
            logger.error("Error processing image: " + e.getMessage(), e);
        }
    }

    public void processAudio(String audioPath, String sourceLanguage, String targetLanguage) {
        try {
            // Validate audio file
            if (!FileUtils.isValidFile(audioPath)) {
                logger.error("Audio file does not exist or is not readable: {}", audioPath);
                return;
            }

            if (!FileUtils.isSupportedAudioFormat(audioPath)) {
                logger.error("Unsupported audio format for file: {}", audioPath);
                return;
            }

            // Process audio
            logger.info("Processing audio: {}", audioPath);
            String basicTranslation = audioTranslationService.transcribeAndTranslateAudio(
                    audioPath, sourceLanguage, targetLanguage);
            logger.info("Basic audio translation:\n{}", basicTranslation);

            // Get detailed analysis
            String detailedAnalysis = audioTranslationService.getDetailedAudioAnalysis(
                    audioPath, sourceLanguage, targetLanguage);
            logger.info("Detailed audio analysis:\n{}", detailedAnalysis);

        } catch (Exception e) {
            logger.error("Error processing audio: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        GeminiTranslationDemo demo = new GeminiTranslationDemo();

        // Example usage for image translation
        String imagePath = "path/to/your/image.jpg";
        String targetLanguage = "Chinese"; // or any other language
        demo.processImage(imagePath, targetLanguage);

        // Example usage for audio translation
        String audioPath = "path/to/your/audio.wav";
        String sourceLanguage = "en-US"; // source language code
        demo.processAudio(audioPath, sourceLanguage, targetLanguage);
    }
}
