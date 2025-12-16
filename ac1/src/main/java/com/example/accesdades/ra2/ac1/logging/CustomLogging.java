package com.example.accesdades.ra2.ac1.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class CustomLogging {
    
    private static final String LOG_FILE = "accesdades/src/main/resources/application.log";
    
    private static final DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void logError(String className, String methodName, String errorMsg, Exception exception) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[ERROR] %s - Class: %s - Method: %s - Message: %s", 
                timestamp, className, methodName, errorMsg);
        
        if (exception != null) {
            logEntry += " - Exception: " + exception.getMessage();
        }
        
        writeToFile(logEntry);
        System.out.println(logEntry); 
    }
    
    public void logInfo(String className, String methodName, String infoMsg) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[INFO] %s - Class: %s - Method: %s - Message: %s", 
                timestamp, className, methodName, infoMsg);
        
        writeToFile(logEntry);
        System.out.println(logEntry);
    }
    
    private void writeToFile(String message) {
        Path logPath = Paths.get(LOG_FILE);
        try {
            Files.createDirectories(logPath.getParent());
            
            try (BufferedWriter bw = Files.newBufferedWriter(logPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                bw.write(message);
                bw.newLine();
                bw.flush(); 
            }
        } catch (IOException e) {
            System.err.println("ERROR escribiendo en el fichero de log: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    public void logWarning(String className, String methodName, String warningMsg) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[WARN] %s - Class: %s - Method: %s - Message: %s", 
                timestamp, className, methodName, warningMsg);
        
        writeToFile(logEntry);
        System.out.println(logEntry);
    }
    
    public void logDebug(String className, String methodName, String debugMsg) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[DEBUG] %s - Class: %s - Method: %s - Message: %s", 
                timestamp, className, methodName, debugMsg);
        
        writeToFile(logEntry);
        System.out.println(logEntry);
    }
}