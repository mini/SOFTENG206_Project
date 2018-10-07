package assignment4.ui;

import java.io.IOException;

import assignment4.NameSayerApp;
import assignment4.model.NamesDB;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseController {
	public static final String ROOT_DIR = NameSayerApp.ROOT_DIR;
	
	protected Stage primaryStage;
	protected NamesDB namesDB;

	public final void setup(Stage primaryStage, NamesDB namesDB) {
		this.primaryStage = primaryStage;
		this.namesDB = namesDB;
	}

	public void init() {
		// Empty
	}

	protected void showScene(String path, boolean asPopUp, boolean resizeable) {
		showScene(path, asPopUp, resizeable, null);
	}

	protected void showScene(String path, boolean asPopUp, boolean resizeable, ExtraSetup extra) {
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
			nextStage.setResizable(resizeable);
			if (extra != null) {
				extra.call(controller);
			}
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

	interface ExtraSetup {
		void call(BaseController controller);
	}
}
