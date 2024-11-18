package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class scenceController {
	private Stage stage;
	private Scene scene; 
	private Parent root;
	
	public void swithToMainMenu(ActionEvent event) throws IOException{
		Parent root = FXMLLoader.load(getClass().getResource("MenuStage.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void swithToGame(ActionEvent event) throws IOException{
		Parent root = FXMLLoader.load(getClass().getResource("GameStage.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}