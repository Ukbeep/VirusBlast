package application;

import java.io.IOException;
import java.util.Properties;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ScoreController {

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

    private Stage scoreStage; // Reference to the score stage

    public void setScoreStage(Stage stage) {
        this.scoreStage = stage; // Store the reference to the score stage
    }
    
    public void displayStatistics() {
        GameStatistics stats = GameStatistics.getInstance();
        stats.loadStatistics(); // Ensure statistics are loaded from the properties file

        // Update UI labels with statistics
        totalVirusesDefeatedLabel.setText("Total Viruses Defeated: " + stats.getHighestTotalVirusesDefeated());
        finalScoreLabel.setText("Final Score: " + stats.getHighestFinalScore()); // This will now reflect the updated score
        highestComboStreakLabel.setText("Highest Combo Streak: " + stats.getHighestComboStreak());
        accuracyPercentageLabel.setText("Accuracy Percentage: " + String.format("%.2f%%", stats.getHighestAccuracyPercentage()));
        timePlayedLabel.setText("Time Played: " + stats.getHighestTotalPlayTime() + " seconds");
    }

    @FXML
    private void handleResetScoreAction() {
        GameStatistics stats = GameStatistics.getInstance();
        stats.reset(); // Call the reset method in GameStatistics

        // Update the labels to reflect the reset
        displayStatistics();
    }
    
    @FXML
    private Button backToMainMenuButton;

    @FXML
    private void handleBackToMainMenuButtonAction() {
        try {
            // Load the main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuStage.fxml")); // Ensure the path is correct
            Parent mainMenuView = loader.load();
            
            // Create a new stage for the main menu
            Stage mainMenuStage = new Stage();
            mainMenuStage.setScene(new Scene(mainMenuView));
            mainMenuStage.setTitle("Main Menu");
            mainMenuStage.show();
            
            // Close the current score stage
            Stage currentStage = (Stage) backToMainMenuButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

}