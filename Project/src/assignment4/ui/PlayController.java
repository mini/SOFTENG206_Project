package assignment4.ui;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class PlayController implements Initializable {

    Stage primaryStage;


    // Override the abstract initialize method to load the player screen when required
    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

}
