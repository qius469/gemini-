package com.gemini.demo.service;

import com.gemini.demo.config.GeminiConfig;
import com.google.cloud.aiplatform.v1.GenerateContentResponse;
import com.google.cloud.aiplatform.v1.Part;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AudioTranslationService {
    private static final Logger logger = LoggerFactory.getLogger(AudioTranslationService.class);
    
    private final GeminiConfig geminiConfig;

    /**
     * Transcribes audio and translates its content to the specified language
     * @param audioPath Path to the audio file
     * @param sourceLanguage Source language code (e.g., "en-US")
     * @param targetLanguage Target language for translation
     * @return Translated transcription of the audio
     */
    public String transcribeAndTranslateAudio(String audioPath, String sourceLanguage, String targetLanguage) {
        try {
            // First, transcribe the audio to text
            String transcription = transcribeAudio(audioPath, sourceLanguage);
            
            // Then, translate the transcription using Gemini
            return translateText(transcription, targetLanguage);
            
        } catch (Exception e) {
            logger.error("Error processing audio: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process audio", e);
        }
    }

    /**
     * Transcribes audio file to text using Google Cloud Speech-to-Text
     * @param audioPath Path to the audio file
     * @param languageCode Language code for the audio (e.g., "en-US")
     * @return Transcribed text
     */
    private String transcribeAudio(String audioPath, String languageCode) throws IOException {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Read the audio file
            byte[] audioData = Files.readAllBytes(Path.of(audioPath));
            
            // Configure the recognition
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode(languageCode)
                    .build();
            
            // Create the recognition audio
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(com.google.protobuf.ByteString.copyFrom(audioData))
                    .build();

            // Perform the transcription
            RecognizeResponse response = speechClient.recognize(config, audio);
            
            // Process the results
            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcription.append(result.getAlternatives(0).getTranscript());
            }
            
            logger.info("Successfully transcribed audio");
            return transcription.toString();
        }
    }

    /**
     * Translates text using Gemini
     * @param text Text to translate
     * @param targetLanguage Target language
     * @return Translated text
     */
    private String translateText(String text, String targetLanguage) {
        List<Part> parts = new ArrayList<>();
        String prompt = String.format("Translate the following text to %s:\n\n%s", targetLanguage, text);
        parts.add(Part.text(prompt));

        GenerateContentResponse response = geminiConfig.getGeminiProModel()
                .generateContent(parts)
                .execute();

        String translation = response.getCandidates(0).getContent().getText();
        logger.info("Successfully translated text to {}", targetLanguage);
        return translation;
    }

    /**
     * Gets a more detailed analysis of the audio content
     * @param audioPath Path to the audio file
     * @param sourceLanguage Source language code
     * @param targetLanguage Target language for analysis
     * @return Detailed analysis of the audio content
     */
    public String getDetailedAudioAnalysis(String audioPath, String sourceLanguage, String targetLanguage) {
        try {
            String transcription = transcribeAudio(audioPath, sourceLanguage);
            
            List<Part> parts = new ArrayList<>();
            String prompt = String.format("""
                Analyze the following transcribed text and provide a detailed analysis in %s, including:
                1. Main topics or subjects discussed
                2. Speaker's tone and emotion (if detectable)
                3. Key points or messages
                4. Context and setting (if apparent)
                5. Any notable quotes or statements
                
                Transcription:
                %s
                """, targetLanguage, transcription);
            
            parts.add(Part.text(prompt));

            GenerateContentResponse response = geminiConfig.getGeminiProModel()
                    .generateContent(parts)
                    .execute();

            return response.getCandidates(0).getContent().getText();

        } catch (IOException e) {
            logger.error("Error processing audio for detailed analysis: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process audio for detailed analysis", e);
        }
    }
}
