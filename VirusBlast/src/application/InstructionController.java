package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InstructionController {
	
	@FXML
	Button backToMainMenuButton;
	
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
