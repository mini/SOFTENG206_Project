package assignment4.ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import javax.swing.text.html.ImageView;
import javax.tools.Tool;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * -- MainMenuController Class --
 *
 * MainMenuController acts as the controller for the MainMenu GUI, where events are listened and handled
 * appropriately with this class. It ensures that the two buttons link to its corresponding event
 * handler methods startPressed() and testMic(). Due to being a main menu, the function of the screen
 * is to direct the user action of each button to switch to its appropriate scene.
 *
 */
public class MainMenuController extends BaseController implements Initializable {

    @FXML
    Button helpButton;


    @FXML
    private void startPressed() {
        showScene("/resources/NameSelector.fxml", false);
    }


    /**
     * Switches scene to the Reward GUI
     */
    @FXML
    private void rewardPressed() {
        showScene("/resources/Rewards.fxml", false);
    }

    /**
     * Switches scene to the Player GUI
     */
    @FXML
    private void practisePressed() {
        showScene("/resources/Practise.fxml", false);
    }

    /**
     * Switches scene to the TestMicrophone GUI
     */
    @FXML
    private void testMic() {
        showScene("/resources/TestMicrophone.fxml", false);
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Welcome to NameSayer. In this main menu, you can click: \n\n" +
                "* START to begin choosing your playlist to start learning your chosen names. \n" +
                "* PRACTISE to practise any name on the database without needing to select a list.\n" +
                "* TEST MICROPHONE to measure your microphone levels.\n" +
                "* The Trophy icon to show your current achievements.");
        helpButton.setTooltip(tooltip);
    }

}
