package assignment4.model;

import assignment4.ui.RewardsController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.Scanner;

public class Achievements {

    public static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "NameSayer/";

    public Achievements(Stage primaryStage) {

        // When the user closes the application, ensure that the achievement scores are saved
        saveAchievements(primaryStage);

        // Load any previous scores to the current session
        loadSave();
    }

    /**
     * Store the values of the achievements progress of the particular user on closing the window
     * @param primaryStage the stage being used
     *
     * Used https://stackoverflow.com/questions/22576261/how-do-i-get-the-close-event-of-a-stage-in-javafx as a reference
     * on how to track the closing of a window
     */
    private void saveAchievements(Stage primaryStage) {


        // On closing of the window
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            // Create a new file called achievements.txt that stores the integer values
                            // of the records, compares and specials scores of the current user
                            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                    new FileOutputStream(ROOT_DIR + "achievements.txt"), "utf-8"))) {
                                writer.write("" + RewardsController.records + "\n");
                                writer.write("" + RewardsController.compares + "\n");
                                writer.write("" + RewardsController.special);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        System.exit(0);

                    }
                });
            }
        });
    }


    /**
     * Loads the previous attempt of progress in terms of achievements and recording, comparing and special scores.
     * This relates to the rewards page.
     */
    private void loadSave() {


        // Read the achievements save file from previous attempts
        File file = new File(ROOT_DIR + "achievements.txt");

        // If there has been a previous score, apply the scores to continue progress. Otherwise, start fresh.
        if (file.exists()) {

            try {
                Scanner scanner = new Scanner(file);

                // Read the first line that corresponds to the previous recording score, and set it
                // to be the current recording score to continue progress
                int recordScore = scanner.nextInt();
                RewardsController.records = recordScore;

                // Read the second line that corresponds to the previous comparison score, and set it
                // to be the current compare score to continue progress
                int compareScore = scanner.nextInt();
                RewardsController.compares = compareScore;

                // Read the third line that corresponds to the previous special score, and set it
                // to be the current special score to continue progress
                int specialScore = scanner.nextInt();
                RewardsController.special = specialScore;


                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}
