package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SummaryController {
    @FXML
    private Label totalVirusesDefeatedLabel;
    
    @FXML
    private Label finalScoreLabel;
    
    @FXML
    private Label highestComboStreakLabel;
    
    @FXML
    private Label accuracyPercentageLabel;
    
    @FXML
    private Label timePlayedLabel;

    @FXML
    Button restartButton, mainMenuButton;
    
    private Stage summaryStage;
    private Stage gameStage;
    private GameController gameController;

    public void setGameStage(Stage gameStage) {
        this.gameStage = gameStage;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    @FXML
    private void handleRestartButtonAction() {
        if (gameController != null) {
            gameController.resetGame(); // Reset the game state
        }

        // Close the summary stage after restarting the game
        Stage summaryStage = (Stage) restartButton.getScene().getWindow();
        summaryStage.close();
    }

    @FXML
    private void handleMainMenuButtonAction() {
        // Logic to go back to the main menu
        try {
            // Load the main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuStage.fxml")); // Ensure the path is correct
            Parent mainMenuView = loader.load();
            
            // Create a new stage for the main menu
            Stage mainMenuStage = new Stage();
            mainMenuStage.setScene(new Scene(mainMenuView));
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();
            
            Stage currentGameStage2 = (Stage) mainMenuButton.getScene().getWindow();
            currentGameStage2.close(); // This will close the game stage
            
            if (gameStage != null) {
                Stage currentGameStage = (Stage) gameStage.getScene().getWindow();
                currentGameStage.close(); // This will close the game stage
            } else {
                System.out.println("gameStage is null, cannot close.");
            }
           
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    // Method to display summary details
    public void displaySummary(int totalVirusesDefeated, int finalScore, int highestComboStreak, double accuracyPercentage, long timePlayed) {
        // Use the passed parameters instead of retrieving from GameStatistics
        totalVirusesDefeatedLabel.setText("Total Viruses Defeated: " + totalVirusesDefeated);
        finalScoreLabel.setText("Final Score: " + finalScore);
        highestComboStreakLabel.setText("Highest Combo Streak: " + highestComboStreak);
        accuracyPercentageLabel.setText("Accuracy: " + String.format("%.2f%%", accuracyPercentage));
        timePlayedLabel.setText(formatTime(timePlayed));

        // Optional: Save the current game's statistics
        GameStatistics stats = GameStatistics.getInstance();
        stats.setFinalScore(finalScore);
    }

    // Helper method to format time
    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

}