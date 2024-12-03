package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
        gameStage.setOnCloseRequest(this::handleCloseRequest);
        
    }

    public void setStage(Stage stage) {
        // Set the close request handler
        stage.setOnCloseRequest(this::handleCloseRequest);
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
        try {
            // Load the main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuStage.fxml"));
            Parent mainMenuView = loader.load();

            // Create and set up the main menu stage
            Stage mainMenuStage = new Stage();
            Scene mainMenuScene = new Scene(mainMenuView);
            
            // Optional: Add CSS styling
            mainMenuScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            
            mainMenuStage.setScene(mainMenuScene);
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();

            // Close the current stages
            Stage currentStage = (Stage) mainMenuButton.getScene().getWindow();
            currentStage.close();

            // Safely close gameStage if it exists
            if (gameStage != null) {
                gameStage.close();
            }

        } catch (IOException e) {
            // Log the error with more context
            System.err.println("Error loading main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to display summary details
    public void displaySummary(int totalVirusesDefeated, int finalScore, int highestComboStreak, double accuracyPercentage, long timePlayed) {
        // Use the passed parameters instead of retrieving from GameStatistics
        totalVirusesDefeatedLabel.setText("Total Viruses Defeated: " + totalVirusesDefeated);
        finalScoreLabel.setText("Final Score: " + finalScore);
        highestComboStreakLabel.setText("Highest Combo Streak: " + highestComboStreak);
        accuracyPercentageLabel.setText("Accuracy: " + String.format("%.2f%%", accuracyPercentage));
        timePlayedLabel.setText("Time Played" + formatTime(timePlayed));

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
    
    private void handleCloseRequest(WindowEvent event) {
        event.consume(); // Prevent the window from closing
        // Optionally, you can show an alert or status message if needed
        System.out.println("The close button is disabled for this view.");
    }

}