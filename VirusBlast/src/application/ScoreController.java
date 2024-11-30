//package application;
//
//import java.io.IOException;
//
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.stage.Stage;
//
//public class ScoreController {
//
//    @FXML
//    private Label totalVirusesDefeatedLabel;
//    @FXML
//    private Label finalScoreLabel;
//    @FXML
//    private Label highestComboStreakLabel;
//    @FXML
//    private Label accuracyPercentageLabel;
//    @FXML
//    private Label timePlayedLabel;
//
//    private Stage scoreStage;
//
//    public void setScoreStage(Stage stage) {
//        this.scoreStage = stage;
//    }
//
//    public void displayStatistics() {
//        GameStatistics stats = GameStatistics.getInstance();
//
//        totalVirusesDefeatedLabel.setText("Total Viruses Defeated: " + stats.getTotalVirusesDefeated());
//        finalScoreLabel.setText("Final Score: " + stats.getFinalScore());
//        highestComboStreakLabel.setText("Highest Combo Streak: " + stats.getHighestComboStreak());
//        accuracyPercentageLabel.setText("Accuracy Percentage: " + String.format("%.2f%%", stats.getAccuracyPercentage()));
//        timePlayedLabel.setText("Time Played: " + stats.getTimePlayed() + " seconds");
//    }
//
//    @FXML
//    private void handleCloseButton() {
//        if (scoreStage != null) {
//            scoreStage.close();
//        }
//    }
//    
//    public void showScoreView() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("score.fxml"));
//            Parent scoreView = loader.load();
//
//            // Get the controller and pass statistics
//            ScoreController scoreController = loader.getController();
//
//            Stage scoreStage = new Stage();
//            scoreStage.setScene(new Scene(scoreView));
//            scoreStage.setTitle("Game Statistics");
//
//            scoreController.setScoreStage(scoreStage);
//            scoreController.displayStatistics();
//
//            scoreStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
