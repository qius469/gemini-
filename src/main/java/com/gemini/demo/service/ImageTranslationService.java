package com.gemini.demo.service;

import com.gemini.demo.config.GeminiConfig;
import com.google.cloud.aiplatform.v1.GenerativeModel;
import com.google.cloud.aiplatform.v1.Part;
import com.google.cloud.aiplatform.v1.GenerateContentResponse;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ImageTranslationService {
    private static final Logger logger = LoggerFactory.getLogger(ImageTranslationService.class);
    
    private final GeminiConfig geminiConfig;

    /**
     * Analyzes an image and translates its content to the specified language
     * @param imagePath Path to the image file
     * @param targetLanguage Target language for translation
     * @return Translated description of the image
     */
    public String analyzeAndTranslateImage(String imagePath, String targetLanguage) {
        try {
            // Read the image file
            byte[] imageBytes = Files.readAllBytes(Path.of(imagePath));
            
            // Create parts list for the request
            List<Part> parts = new ArrayList<>();
            
            // Add the prompt
            parts.add(Part.text("Please analyze this image and describe its content in " + targetLanguage));
            
            // Add the image
            parts.add(Part.image(ByteString.copyFrom(imageBytes)));

            // Generate content using Gemini Vision model
            GenerateContentResponse response = geminiConfig.getGeminiVisionModel()
                    .generateContent(parts)
                    .execute();

            // Extract and return the response text
            String result = response.getCandidates(0).getContent().getText();
            logger.info("Successfully analyzed and translated image content");
            return result;

        } catch (IOException e) {
            logger.error("Error processing image: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process image", e);
        }
    }

    /**
     * Validates if the file is a supported image format
     * @param file Image file to validate
     * @return true if the image is valid, false otherwise
     */
    public boolean isValidImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            return image != null;
        } catch (IOException e) {
            logger.error("Error validating image: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets a more detailed analysis of the image with specific aspects
     * @param imagePath Path to the image file
     * @param targetLanguage Target language for the analysis
     * @return Detailed analysis of the image
     */
    public String getDetailedImageAnalysis(String imagePath, String targetLanguage) {
        try {
            byte[] imageBytes = Files.readAllBytes(Path.of(imagePath));
            
            List<Part> parts = new ArrayList<>();
            String prompt = String.format("""
                Please analyze this image and provide a detailed description in %s, including:
                1. Main subjects or objects
                2. Colors and visual elements
                3. Actions or activities (if any)
                4. Setting or background
                5. Any text or writing visible in the image
                6. Overall mood or atmosphere
                """, targetLanguage);
            
            parts.add(Part.text(prompt));
            parts.add(Part.image(ByteString.copyFrom(imageBytes)));

            GenerateContentResponse response = geminiConfig.getGeminiVisionModel()
                    .generateContent(parts)
                    .execute();

            return response.getCandidates(0).getContent().getText();

        } catch (IOException e) {
            logger.error("Error processing image for detailed analysis: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process image for detailed analysis", e);
        }
    }
}
