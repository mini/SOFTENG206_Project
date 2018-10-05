package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * -- PopUpWindow Class --
 *
 * PopUpWindow encapsulates the pop up screens when listening to the original pronunciation, recording
 * the user's own attempt and listening to any selected attempts. This allows a visual time frame to
 * be shown to the user.
 *
 */
public class PopUpController {

	@FXML
	private Label messageLabel;

	public void setText(String text) {
		messageLabel.setText(text);
	}
}
