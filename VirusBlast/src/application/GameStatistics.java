package application;

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
    	reset();
    }

    public static GameStatistics getInstance() {
        if (instance == null) {
            instance = new GameStatistics();
        }
        return instance;
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
    
    // Getters and setters...
    public void setTotalVirusesDefeated(int totalVirusesDefeated) {
        this.totalVirusesDefeated = totalVirusesDefeated;
    }

    public void incrementTotalVirusesDefeated() {
        totalVirusesDefeated++;
    }
    
    public int getTotalVirusesDefeated() {
        return totalVirusesDefeated;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setHighestComboStreak(int highestComboStreak) {
        this.highestComboStreak = highestComboStreak;
    }

    public int getHighestComboStreak() {
        return highestComboStreak;
    }

    public void setAccuracyPercentage(double accuracyPercentage) {
        this.accuracyPercentage = accuracyPercentage;
    }

    public double getAccuracyPercentage() {
        return accuracyPercentage;
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
        totalVirusesDefeated = 0;
        finalScore = 0;
        highestComboStreak = 0;
        accuracyPercentage = 0.0;
        totalPlayTime = 0;
        startTime = System.currentTimeMillis();
        isGameRunning = false;
        System.out.println("Game statistics reset completely.");
    }
    
    
}
