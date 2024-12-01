package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Properties;

public class GameStatistics {
    private static GameStatistics instance;
    // Highest scores
    // Highest scores
    private int highestTotalVirusesDefeated;
    private int highestComboStreak;
    private double highestAccuracyPercentage;
    private long highestTotalPlayTime;
    private int highestFinalScore; // New field for highest final score

    // Current game statistics
    private int currentTotalVirusesDefeated;
    private int currentFinalScore;
    private int currentComboStreak;
    private double currentAccuracyPercentage;
    private long currentTotalPlayTime; long startTime;
    private long totalPlayTime;
    
    private boolean isGameRunning = false;
    private static final String STATISTICS_FILE = "virus_blast_statistics.properties";
    
    GameStatistics() {
        // Initialize to zero
    	currentTotalVirusesDefeated = 0;
    	currentFinalScore = 0;
    	currentComboStreak = 0;
        currentAccuracyPercentage = 0.0;
        currentTotalPlayTime = 0;
    }

    public static GameStatistics getInstance() {
        if (instance == null) {
            instance = new GameStatistics();
            instance.loadStatistics(); // Load existing statistics on instantiation
        }
        return instance;
    }
    
    // Getters and setters for current statistics
    public void setCurrentTotalVirusesDefeated(int count) { this.currentTotalVirusesDefeated = count; }
    public void setCurrentComboStreak(int streak) { this.currentComboStreak = streak; }
    public void setCurrentAccuracyPercentage(double accuracy) { this.currentAccuracyPercentage = accuracy; }
    public void setCurrentTotalPlayTime(long time) { this.currentTotalPlayTime = time; }
    
 // Method to save statistics
    public void saveStatistics() {
        Properties properties = new Properties();
        File file = new File(STATISTICS_FILE);

        // Load existing data if the file exists
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                properties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Check current highest scores
        highestTotalVirusesDefeated = Integer.parseInt(properties.getProperty("totalVirusesDefeated", "0"));
        highestComboStreak = Integer.parseInt(properties.getProperty("comboStreak", "0"));
        highestAccuracyPercentage = Double.parseDouble(properties.getProperty("accuracyPercentage", "0.0"));
        highestTotalPlayTime = Long.parseLong(properties.getProperty("totalPlayTime", "0"));
        highestFinalScore = Integer.parseInt(properties.getProperty("highestFinalScore", "0")); // Load previous highest final score

        // Calculate the current final score based on game metrics
        int currentFinalScore = getFinalScore();

        // Update highest scores if current scores exceed them
        if (currentTotalVirusesDefeated > highestTotalVirusesDefeated) {
            highestTotalVirusesDefeated = currentTotalVirusesDefeated;
            properties.setProperty("totalVirusesDefeated", String.valueOf(highestTotalVirusesDefeated));
        }

        if (currentComboStreak > highestComboStreak) {
            highestComboStreak = currentComboStreak;
            properties.setProperty("comboStreak", String.valueOf(highestComboStreak));
        }

        if (currentAccuracyPercentage > highestAccuracyPercentage) {
            highestAccuracyPercentage = currentAccuracyPercentage;
            properties.setProperty("accuracyPercentage", String.valueOf(highestAccuracyPercentage));
        }

        if (currentTotalPlayTime > highestTotalPlayTime) {
            highestTotalPlayTime = currentTotalPlayTime;
            properties.setProperty("totalPlayTime", String.valueOf(highestTotalPlayTime));
        }

        // Check and update the highest final score
        if (currentFinalScore > highestFinalScore) {
            highestFinalScore = currentFinalScore;
            properties.setProperty("highestFinalScore", String.valueOf(highestFinalScore));
        }

        // Save updated properties back to file
        try (FileOutputStream out = new FileOutputStream(file)) {
            properties.store(out, "Game Statistics");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void loadStatistics() {
        File file = new File(STATISTICS_FILE);
        Properties properties = new Properties();

        try {
            // Ensure the file exists, creating it if necessary
            if (!file.exists()) {
                // Use the current working directory explicitly
                file = new File(System.getProperty("user.dir"), STATISTICS_FILE);
                
                // Ensure the parent directory exists
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                
                // Create the file
                file.createNewFile();
                
                // Set default values
                properties.setProperty("totalVirusesDefeated", "0");
                properties.setProperty("comboStreak", "0");
                properties.setProperty("accuracyPercentage", "0.0");
                properties.setProperty("totalPlayTime", "0");
                properties.setProperty("highestFinalScore", "0");
                properties.setProperty("finalScore", "0");
                properties.setProperty("timePlayed", "0");

                // Save the properties
                try (FileOutputStream out = new FileOutputStream(file)) {
                    properties.store(out, "Game Statistics - Initial Configuration");
                }
            }

            // Now read the properties from the file
            try (FileInputStream input = new FileInputStream(file)) {
                properties.load(input);
                
                // Load values into class fields
                highestTotalVirusesDefeated = Integer.parseInt(properties.getProperty("totalVirusesDefeated", "0"));
                highestComboStreak = Integer.parseInt(properties.getProperty("comboStreak", "0"));
                highestAccuracyPercentage = Double.parseDouble(properties.getProperty("accuracyPercentage", "0.0"));
                highestTotalPlayTime = Long.parseLong(properties.getProperty("totalPlayTime", "0"));
                highestFinalScore = Integer.parseInt(properties.getProperty("highestFinalScore", "0"));

                currentTotalVirusesDefeated = Integer.parseInt(properties.getProperty("totalVirusesDefeated", "0"));
                currentFinalScore = Integer.parseInt(properties.getProperty("finalScore", "0"));
                currentComboStreak = Integer.parseInt(properties.getProperty("comboStreak", "0"));
                currentAccuracyPercentage = Double.parseDouble(properties.getProperty("accuracyPercentage", "0.0"));
                totalPlayTime = Long.parseLong(properties.getProperty("totalPlayTime", "0"));

            }
        } catch (IOException e) {
            e.printStackTrace();
            // Set default values in memory if file reading fails
            reset();
        }
    }

    
    public void startTimer() {
        startTime = System.currentTimeMillis();
        isGameRunning = true;
    }
    
    public void stopTimer() {
        if (isGameRunning) {
            long endTime = System.currentTimeMillis();
            totalPlayTime += (endTime - startTime) / 1000;
            isGameRunning = false;
        }
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
        totalPlayTime = 0;
        isGameRunning = false;
    }
    
//    private int calculateFinalScore() {
//        return currentTotalVirusesDefeated * 10 + currentComboStreak * 5; // Example calculation
//    }

    // Getters and setters...
    public void setTotalVirusesDefeated(int totalVirusesDefeated) {
        this.currentTotalVirusesDefeated = totalVirusesDefeated;
    }

    public void incrementTotalVirusesDefeated() {
    	currentTotalVirusesDefeated++;
    }
    
    public int getTotalVirusesDefeated() {
        return currentTotalVirusesDefeated;
    }

    public void setFinalScore(int finalScore) {
        this.currentFinalScore = finalScore;
    }

    public int getFinalScore() {
        return currentFinalScore;
    }

    public void setcomboStreak(int comboStreak) {
        this.currentComboStreak = comboStreak;
    }

    public int getcomboStreak() {
        return currentComboStreak;
    }

    public void setAccuracyPercentage(double accuracyPercentage) {
        this.currentAccuracyPercentage = accuracyPercentage;
    }

    public double getAccuracyPercentage() {
        return currentAccuracyPercentage;
    }

    public void setTimePlayed(long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    public long getTimePlayed() {
        if (isGameRunning) {
            long currentTime = System.currentTimeMillis();
            return totalPlayTime + ((currentTime - startTime) / 1000);
        }
        return totalPlayTime;
    }

    public void reset() {
        // Reset all statistics to zero
    	highestTotalVirusesDefeated = 0;
    	highestFinalScore = 0;
    	highestComboStreak = 0;
    	highestAccuracyPercentage = 0.0;
    	highestTotalPlayTime = 0;
        System.out.println("Reset....");
        
    	currentTotalVirusesDefeated = 0;
    	currentFinalScore = 0;
    	currentComboStreak = 0;
        currentAccuracyPercentage = 0.0;
        currentTotalPlayTime = 0;
    }
    
    public int getHighestTotalVirusesDefeated() { return highestTotalVirusesDefeated; }
    public int getHighestComboStreak() { return highestComboStreak; }
    public double getHighestAccuracyPercentage() { return highestAccuracyPercentage; }
    public long getHighestTotalPlayTime() { return highestTotalPlayTime; }
    public int getHighestFinalScore() { return highestFinalScore; }
}