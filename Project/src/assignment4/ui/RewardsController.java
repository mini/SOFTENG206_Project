package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;

/**
 * RewardsController is a class that handles all the processing and storing of information in terms of the users
 * progress through tracking the number of recordings and comparisons of names. Special features have been randomly
 * scattered throughout the application, and this class tracks the discovery of these applications when found.
 *
 * The layout is determined by 3 levels for each category: Records, Compares, and Special Features.
 */
public class RewardsController extends BaseController {

	//@formatter:off
	@FXML private ImageView prize1, prize2, prize3;
	@FXML private Circle record1, record2, record3, compare1, compare2, compare3, special1, special2, special3;
	@FXML private Text compareText, recordText, specialText;
	@FXML private Button closeButton;
	//@formatter:on

	public void init() {
		setRecordTrophy();
		setCompareTrophy();
		setSpecialTrophy();
	}

	/**
	 * updateTrophy is a class that manages the updating of icons for the ImageView and the filling of the level indicators
	 * on the Rewards screen. The determination of how each category is shown from the users' progress is dependent on one
	 * of the parameters - 'level'. Once the level is determined, the layout is therefore decided at runtime.
	 * 
	 * @param level       the progress level of each category that determines which prizes the user has earned
	 * @param prizeNum    the category that the method is changing
	 * @param circleName1 the first progress dot of the category
	 * @param circleName2 the second progress dot of the category
	 * @param circleName3 the third progress dot of the category
	 */
	private void updateTrophy(int level, ImageView prizeNum, Circle circleName1, Circle circleName2, Circle circleName3) {

		// Level 1 means that only the first achievement of the category has been achieved. This sets a bronze
		// trophy to be displayed, and only one progress indicator dot to be filled in
		if (level == 1) {
			Image prize = new Image(getClass().getResourceAsStream("/resources/icons/firstach.png"));
			prizeNum.setImage(prize);
			circleName1.setFill(Color.valueOf("#ffb71b"));
		}

		// Level 2 means that the second achievement of the category has been achieved. This sets a silver
		// trophy to be displayed, and two progress indicator dots to be filled in
		else if (level == 2) {
			Image prize = new Image(getClass().getResourceAsStream("/resources/icons/secondach.png"));
			prizeNum.setImage(prize);
			circleName1.setFill(Color.valueOf("#ffb71b"));
			circleName2.setFill(Color.valueOf("#ffb71b"));
		}

		// Level 3 means that all achievements of the category have been achieved. This sets a gold
		// trophy to be displayed, and all three progress indicator dots to be filled in
		else if (level == 3) {
			Image prize = new Image(getClass().getResourceAsStream("/resources/icons/thirdach.png"));
			prizeNum.setImage(prize);
			circleName1.setFill(Color.valueOf("#ffb71b"));
			circleName2.setFill(Color.valueOf("#ffb71b"));
			circleName3.setFill(Color.valueOf("#ffb71b"));
		}

	}

	// Icons made by Freepik from https://www.flaticon.com and is licensed by
	// http://creativecommons.org/licenses/by/3.0/
	private void setRecordTrophy() {
		int records = stats.getRecords();
		recordText.setText("Recorded " + records + "/10 times");

		// If the user has recorded more than 10 times, display the first prize
		if (records >= 10 && records < 25) {
			updateTrophy(1, prize1, record1, record2, record3);
			recordText.setText("Recorded " + records + "/25 times");

			// If the user has recorded more than 25 times, display the second prize
		} else if (records >= 25 && records < 50) {
			updateTrophy(2, prize1, record1, record2, record3);
			recordText.setText("Recorded " + records + "/50 times");

			// If the user has recorded more than 50 times, display the third prize
		} else if (records >= 50) {
			updateTrophy(3, prize1, record1, record2, record3);
			recordText.setText("Recorded " + records + " times in total");
		}
	}

	// Icons made by Freepik from https://www.flaticon.com and is licensed by
	// http://creativecommons.org/licenses/by/3.0/
	private void setCompareTrophy() {
		int compares = stats.getCompares();
		compareText.setText("Compared " + compares + "/5 times");

		// If the user has saved 5 or more attempts, display the second prize
		if (compares >= 5 && compares < 15) {
			updateTrophy(1, prize2, compare1, compare2, compare3);
			compareText.setText("Compared " + compares + "/15 times");

			// If the user has recorded more than 25 times, display the second prize
        } else if (compares >= 15 && compares < 30) {
			updateTrophy(2, prize2, compare1, compare2, compare3);
			compareText.setText("Compared " + compares + "/30 times");

			// If the user has recorded more than 50 times, display the third prize
		} else if (compares >= 30) {
			updateTrophy(3, prize2, compare1, compare2, compare3);
			compareText.setText("Compared " + compares + " times in total");

		}

	}

	// Icons made by Freepik from https://www.flaticon.com and is licensed by
	// http://creativecommons.org/licenses/by/3.0/
	private void setSpecialTrophy() {
		int special = stats.getSpecial();

		// If the user finds one of the special features, display bronze trophy
		if (special == 1) {
			updateTrophy(1, prize3, special1, special2, special3);
		}

		// If the user finds two of the special features, display bronze trophy
		if (special == 2) {
			updateTrophy(2, prize3, special1, special2, special3);

		}

		// If the user finds three of the special features, display bronze trophy
		if (special == 3) {
			updateTrophy(3, prize3, special1, special2, special3);

		}
	}

	@FXML
	private void backPressed() {
		showScene("MainMenu.fxml", false, false);
	}

    @FXML
    private void closePressed() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
