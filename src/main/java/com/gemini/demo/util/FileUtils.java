package com.gemini.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    
    private static final Set<String> SUPPORTED_IMAGE_FORMATS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "bmp")
    );
    
    private static final Set<String> SUPPORTED_AUDIO_FORMATS = new HashSet<>(
            Arrays.asList("wav", "mp3", "flac", "m4a")
    );

    /**
     * Validates if the file exists and is readable
     * @param filePath Path to the file
     * @return true if the file is valid and readable
     */
    public static boolean isValidFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.canRead();
    }

    /**
     * Checks if the file is a supported image format
     * @param filePath Path to the file
     * @return true if the file is a supported image format
     */
    public static boolean isSupportedImageFormat(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();
        return SUPPORTED_IMAGE_FORMATS.contains(extension);
    }

    /**
     * Checks if the file is a supported audio format
     * @param filePath Path to the file
     * @return true if the file is a supported audio format
     */
    public static boolean isSupportedAudioFormat(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();
        return SUPPORTED_AUDIO_FORMATS.contains(extension);
    }

    /**
     * Gets the file extension
     * @param filePath Path to the file
     * @return File extension without the dot
     */
    public static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * Creates a directory if it doesn't exist
     * @param directoryPath Path to the directory
     * @throws IOException if directory creation fails
     */
    public static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("Created directory: {}", directoryPath);
        }
    }

    /**
     * Gets the file size in MB
     * @param filePath Path to the file
     * @return File size in MB
     */
    public static double getFileSizeInMB(String filePath) {
        File file = new File(filePath);
        return file.length() / (1024.0 * 1024.0);
    }

    /**
     * Validates file size
     * @param filePath Path to the file
     * @param maxSizeMB Maximum allowed size in MB
     * @return true if the file size is within limits
     */
    public static boolean isFileSizeValid(String filePath, double maxSizeMB) {
        double fileSizeMB = getFileSizeInMB(filePath);
        return fileSizeMB <= maxSizeMB;
    }
}
