package assignment4.ui;

import java.io.IOException;

import assignment4.NameSayerApp;
import assignment4.model.AchievementStats;
import assignment4.model.NamesDB;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * BaseController for all other controllers to inherit that require access to the name database
 * and other application wide objects
 * 
 *  @author Dhruv Phadnis, Vanessa Ciputra
 */
public abstract class BaseController {
	public static final String ROOT_DIR = NameSayerApp.ROOT_DIR;
	
	protected Stage primaryStage;
	protected NamesDB namesDB;
	protected AchievementStats stats;

	public final void setup(Stage primaryStage, NamesDB namesDB, AchievementStats stats) {
		this.primaryStage = primaryStage;
		this.namesDB = namesDB;
		this.stats = stats;
	}

	/**
	 * Called before new scene is visible and after FXML fields are initialised.
	 * Equivalent to Initializable interface.
	 */
	public void init() {
		// Empty by default
	}

	/**
	 * @see #showScene(String, boolean, boolean, ExtraSetup) 
	 */
	protected void showScene(String filename, boolean asPopUp, boolean resizeable) {
		showScene(filename, asPopUp, resizeable, null);
	}

	/**
	 * Handles loading the specified GUI and ensures data is passed between the controllers.
	 * @param filename path to the fxml file
	 * @param asPopUp if the next scene opens in a new window
	 * @param resizeable if the next window should be resizeable
	 * @param extra a callback to pass controller specific data
	 */
	protected void showScene(String filename, boolean asPopUp, boolean resizeable, ExtraSetup extra) {
		Stage nextStage = primaryStage;
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxmls/" + filename));
			Scene scene = new Scene(loader.load());

			BaseController controller = loader.getController();

			if (asPopUp) {
				nextStage = new Stage();
				nextStage.setAlwaysOnTop(true);
				nextStage.initOwner(primaryStage);
				nextStage.initModality(Modality.APPLICATION_MODAL);
			}
			// Pass data to child controllers
			controller.primaryStage = nextStage;
			controller.namesDB = namesDB;
			controller.stats = stats;
			
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
		/**
		 * Callback to enable passing extra data
		 */
		void call(BaseController controller);
	}
}
