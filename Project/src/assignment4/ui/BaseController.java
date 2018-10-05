package assignment4.ui;

import java.io.IOException;

import assignment4.model.NamesDB;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseController {
	protected Stage primaryStage;
	protected NamesDB namesDB;

	public final void setup(Stage primaryStage, NamesDB namesDB) {
		this.primaryStage = primaryStage;
		this.namesDB = namesDB;
	}
	
	protected void init() {
		// Empty
	}
	
	protected void showScene(String path, boolean asPopUp) {
		Stage nextStage = primaryStage;
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Scene scene = new Scene(loader.load());

			BaseController controller = loader.getController();

			if (asPopUp) {
				nextStage = new Stage();
				nextStage.setAlwaysOnTop(true);
				nextStage.initOwner(primaryStage);
				nextStage.initModality(Modality.APPLICATION_MODAL);
			}

			controller.primaryStage = nextStage;
			controller.namesDB = namesDB;
			nextStage.setScene(scene);
			controller.init();
			
			if (asPopUp) {
				nextStage.showAndWait();
			} else {
				nextStage.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
