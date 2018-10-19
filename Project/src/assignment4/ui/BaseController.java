package assignment4.ui;

import java.io.IOException;

import assignment4.NameSayerApp;
import assignment4.model.NamesDB;
import assignment4.model.PermanentTooltip;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * BaseController for all other controllers to inherit that require access to the name database 
 */
public abstract class BaseController {
	public static final String ROOT_DIR = NameSayerApp.ROOT_DIR;
	
	protected Stage primaryStage;
	protected NamesDB namesDB;

	public final void setup(Stage primaryStage, NamesDB namesDB) {
		this.primaryStage = primaryStage;
		this.namesDB = namesDB;
	}

	/**
	 * Called before new scene is visible.
	 * Equivalent to Initializable interface.
	 */
	public void init() {
		// Empty
	}

	/**
	 * @see #showScene(String, boolean, boolean, ExtraSetup) 
	 */
	protected void showScene(String filename, boolean asPopUp, boolean resizeable) {
		showScene(filename, asPopUp, resizeable, null);
	}

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

//	public void showToolTip(Button helpButton, Tooltip tooltip) {
//
//		PermanentTooltip.setTooltipTimers(0, 99999,0);
//
//		helpButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				Point2D p = helpButton.localToScreen(helpButton.getLayoutBounds().getMaxX(), helpButton.getLayoutBounds().getMaxY()); //I position the tooltip at bottom right of the node (see below for explanation)
//				tooltip.show(helpButton, p.getX(), p.getY());
//			}
//		});
//		helpButton.setOnMouseExited(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				tooltip.hide();
//			}
//		});
//
//		helpButton.setTooltip(tooltip);
//	}

	interface ExtraSetup {
		/**
		 * Callback to enable passing extra data
		 */
		void call(BaseController controller);
	}
}
