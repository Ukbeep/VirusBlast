package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Properties;

public class GameStatistics {
    private static GameStatistics instance;
    private int totalVirusesDefeated;
    private int finalScore;
    private int highestComboStreak;
    private double accuracyPercentage;
    private long startTime;
    private long totalPlayTime;
    private boolean isGameRunning = false;
    
    private GameStatistics() {
        // Initialize to zero
        totalVirusesDefeated = 0;
        finalScore = 0;
        highestComboStreak = 0;
        accuracyPercentage = 0.0;
        totalPlayTime = 0;

        // Load statistics from the file
        //loadStatistics();
    }

    public static GameStatistics getInstance() {
        if (instance == null) {
            instance = new GameStatistics();
        }
        return instance;
    }


//    public void loadStatistics() {
//        Properties properties = new Properties();
//        File statsFile = new File("game_statistics.properties");
//
//        if (!statsFile.exists()) {
//            try {
//                // Create a new file if it does not exist
//                statsFile.createNewFile();
//
//                // Set default properties
//                properties.setProperty("TotalVirusesDefeated", "0");
//                properties.setProperty("FinalScore", "0");
//                properties.setProperty("HighestComboStreak", "0");
//                properties.setProperty("AccuracyPercentage", "0");
//                properties.setProperty("TimePlayed", "0");
//
//                // Save the default properties
//                //saveStatistics(properties);
//
//            } catch (IOException e) {
//                System.err.println("Failed to create properties file: " + e.getMessage());
//            }
//        } else {
//            // Load properties from the existing file
//            try (FileInputStream inputStream = new FileInputStream(statsFile)) {
//                properties.load(inputStream);
//
//                // Parse properties
//                totalVirusesDefeated = Integer.parseInt(properties.getProperty("TotalVirusesDefeated", "0"));
//                finalScore = Integer.parseInt(properties.getProperty("FinalScore", "0"));
//                highestComboStreak = Integer.parseInt(properties.getProperty("HighestComboStreak", "0"));
//                accuracyPercentage = Double.parseDouble(properties.getProperty("AccuracyPercentage", "0"));
//                timePlayed = Long.parseLong(properties.getProperty("TimePlayed", "0"));
//
//            } catch (IOException e) {
//                System.err.println("Error loading statistics: " + e.getMessage());
//            } catch (NumberFormatException e) {
//                System.err.println("Error parsing statistics: " + e.getMessage());
//            }
//        }
//    }

	 
//	 * public void saveStatistics(Properties properties) { try (FileOutputStream
//	 * outputStream = new FileOutputStream("game_statistics.properties")) {
//	 * properties.setProperty("TotalVirusesDefeated",
//	 * String.valueOf(totalVirusesDefeated)); properties.setProperty("FinalScore",
//	 * String.valueOf(finalScore)); properties.setProperty("HighestComboStreak",
//	 * String.valueOf(highestComboStreak));
//	 * properties.setProperty("AccuracyPercentage",
//	 * String.valueOf(accuracyPercentage)); properties.setProperty("TimePlayed",
//	 * String.valueOf(timePlayed)); properties.store(outputStream,
//	 * "Game Statistics"); } catch (IOException e) { e.printStackTrace(); } }
//	 */

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
    
    // Getters and setters...
    public void setTotalVirusesDefeated(int totalVirusesDefeated) {
        this.totalVirusesDefeated = totalVirusesDefeated;
        //saveStatistics(new Properties()); // Update the file whenever you change the statistic
    }

    public void incrementTotalVirusesDefeated() {
        totalVirusesDefeated++;
    }
    
    public int getTotalVirusesDefeated() {
        return totalVirusesDefeated;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
        //saveStatistics(new Properties());
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setHighestComboStreak(int highestComboStreak) {
        this.highestComboStreak = highestComboStreak;
        //saveStatistics(new Properties());
    }

    public int getHighestComboStreak() {
        return highestComboStreak;
    }

    public void setAccuracyPercentage(double accuracyPercentage) {
        this.accuracyPercentage = accuracyPercentage;
        //saveStatistics(new Properties());
    }

    public double getAccuracyPercentage() {
        return accuracyPercentage;
    }

    public void setTimePlayed(long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
        //saveStatistics(new Properties());
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
        totalVirusesDefeated = 0;
        finalScore = 0;
        highestComboStreak = 0;
        accuracyPercentage = 0.0;
        totalPlayTime = 0;
        // Optionally save these reset values to the properties file
        //saveStatistics(new Properties());
    }
    
    
}