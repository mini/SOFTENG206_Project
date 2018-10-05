package assignment4.ui;

import assignment4.model.Name;
import assignment4.model.Version;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
        concatenateNames("Junyan Zhao");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // REMEMBER TO CREATE FUNCTION TO DELETE THIS FILE AFTER USER IS DONE
    public void concatenateNames(String name) {

        // Create a thread to ensure that the GUI does not freeze for concurrency
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {


                // Create the text file to concatenate all the names in the string
                createCombinedNameFile(name);

                String mergedName = name.replaceAll("\\s", "");

                // Use a process to concatenate the separate wav files into one file
                String concat = ("ffmpeg -f concat -i " + mergedName + ".txt -c copy " + mergedName + ".wav");
                System.out.println(concat);

                File textConcat = new File("./src/resources/names/"+mergedName+".txt");


                try {
                    File directory = new File(System.getProperty("user.dir") + "/src/resources/names");
                    ProcessBuilder merge = new ProcessBuilder("bash", "-c", concat);
                    merge.directory(directory);
                    Process pro = merge.start();
                    pro.waitFor();

                    textConcat.delete();
                    deleteEqualisedFile(name);

                } catch (IOException e) {
                    System.out.println("COULD NOT CONCATENATE FILE");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }

            };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    public void deleteEqualisedFile(String name) {

        // Separates the string with spaces only
        for (String word: name.split(" ")) {
            String eqName = searchFileWithName(word);
            File eqFile = new File("./src/resources/names/"+eqName);
            System.out.println(eqFile);
            eqFile.delete();
        }
    }

    /**
     * Separates the line of the text file to obtain the separate name files to concatenate by creating a
     * new text file with the correct format to concatenate the wav files
     * @param disjointName
     */
    public void createCombinedNameFile(String disjointName) {

       String mergedName = disjointName.replaceAll("\\s","");



        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("./src/resources/names/"+mergedName+".txt"), "utf-8")
            );

            // Separates the string with spaces only
            for (String word: disjointName.split(" ")) {
                String fileName = searchFileWithName(word);
                System.out.println(fileName);
                equaliseVolume(fileName);
                writer.write("file 'EQ_"+fileName+"'");
                ((BufferedWriter) writer).newLine();
                System.out.println("Written");
            }

            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void equaliseVolume(String fileName) {


        // Use a process to concatenate the separate wav files into one file
        String eq = ("ffmpeg -i "+fileName+" -filter:a loudnorm EQ_"+fileName);

        try {
                    File directory = new File(System.getProperty("user.dir") + "/src/resources/names");
                    ProcessBuilder volume = new ProcessBuilder("bash", "-c", eq);
                    volume.directory(directory);
                    Process pro = volume.start();
                    pro.waitFor();

                } catch (IOException e) {
                    System.out.println("COULD NOT CONCATENATE FILE");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


    }

    /**
     * // MOVE INTO NEW CLASS?
     * Searches the names Database for the corresponding name to add the exact file name into the concatenation file
     * @param searchName
     * @return
     */
    public String searchFileWithName(String searchName) {
        File file = new File("./src/resources/names");
        System.out.println(file.isDirectory());
        System.out.println(file.isFile());

        List<String> fileNames = new ArrayList<String>();

        String[] files = file.list();
        for (String recordingName : files) {
            if (recordingName.contains(searchName+".wav")) {
                fileNames.add(recordingName);
            }
        }

        if (fileNames == null) {
            System.out.println("ERROR!!! NO FILE FOUND");
        } else if (fileNames.size() == 1) {
            return fileNames.get(0);
        }

        // Check for good quality file
        // TBD!!!

        return fileNames.get(0);
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
