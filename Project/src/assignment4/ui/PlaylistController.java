package assignment4.ui;

import assignment4.model.Name;
import assignment4.model.Version;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {

    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final SimpleDateFormat labelDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private Stage primaryStage;

    //@formatter:off
    @FXML
    private TextField searchTextField;
    @FXML private TableView<Name> namesTable;
    @FXML private TableColumn<Name, CheckBox> selectColumn;
    @FXML private TableColumn<Name, String> nameColumn;
    @FXML private TableColumn<Name, String> playingColumn;

    @FXML private TextArea playlistArea;

    @FXML private Label currentLabel;


    @FXML private Button mainMenuButton;
    @FXML private Button nextButton;


    // Override the abstract initialize method to load the player screen when required
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Enable the button as soon as the user begins writing
        playlistArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                nextButton.setDisable(false);
            }
        });

        // Testing function
        createCombinedNameFile("Hello my name is vee");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    /**
     * Separates the line of the text file to obtain the separate name files to concatenate by creating a
     * new text file with the correct format to concatenate the wav files
     * @param disjointName
     */
    public void createCombinedNameFile(String disjointName) {

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(disjointName+".txt"), "utf-8")
            );

            // Separates the string with spaces only
            for (String word: disjointName.split(" ")) {
                writer.write("file './"+word+".wav'");
                ((BufferedWriter) writer).newLine();
            }

            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Switches scene back to the main menu
     */
    @FXML
    private void mainMenuPressed() {
        try {

            // Load the scene of the Main Menu fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/MainMenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = (MainMenuController) loader.getController();

            // Set the stage to use the current stage in order to switch scenes with the same stage
            controller.setPrimaryStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void nextPressed() {

        try {

            // Load the scene of the Main Menu fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/MainPlayer.fxml"));
            Parent root = loader.load();

            MainPlayerController controller = (MainPlayerController) loader.getController();

            // Set the stage to use the current stage in order to switch scenes with the same stage
            controller.setPrimaryStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
