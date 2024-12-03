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
	        Parent mainMenuView = FXMLLoader.load(getClass().getResource("MenuStage.fxml")); // Ensure the path is correct
	        
	        // Get the current stage and set the new scene
	        Stage currentStage = (Stage) backToMainMenuButton.getScene().getWindow();
	        Scene newScene = new Scene(mainMenuView);
	        currentStage.setScene(newScene);
	        newScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        // Optionally, you can set the title again if needed
	        currentStage.setTitle("Main Menu");
	    } catch (IOException e) {
	        e.printStackTrace(); // Print stack trace for debugging
	    }
	}

}
