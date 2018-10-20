package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class RewardsController extends BaseController  {

    public static int records = 0;
    public static int compares = 0;
    public static int special = 0;
    private static int count = 0;

    private static boolean recordCheck = false;
    private static boolean saveCheck = false;
    private static boolean specialCheck = false;

    @FXML private ImageView prize1, prize2, prize3;
    @FXML private Circle record1, record2, record3, compare1, compare2, compare3, special1, special2, special3;
    @FXML private Text compareText, recordText, specialText;
    @FXML private Text rewardCount;

    public void init() {

        setRecordTrophy();

        setCompareTrophy();

        setSpecialTrophy();
    }




    @FXML
    private void backPressed() {
        showScene("MainMenu.fxml", false, false);
    }


    // Icons made by Freepik from https://www.flaticon.com and is licensed by
    // http://creativecommons.org/licenses/by/3.0/
    private void setRecordTrophy() {

        recordText.setText("Recorded " + records + "/10 times");

        // If the user has recorded more than 10 times, display the first prize
        if (records >= 10 && records < 25 && !recordCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/firstach.png"));
            prize1.setImage(prize);
            record1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            count++;
            rewardCount.setText(count+"/3");
            recordText.setText("Recorded " + records + "/25 times");

            // If the user has recorded more than 25 times, display the second prize
        } else if (records >= 25 && records < 50 && !recordCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/secondach.png"));
            prize1.setImage(prize);
            record1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            record2.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            count++;
            rewardCount.setText(count+"/3");
            recordText.setText("Recorded " + records + "/50 times");

            // If the user has recorded more than 50 times, display the third prize
        } else if (records >= 50 && !recordCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/thirdach.png"));
            prize1.setImage(prize);
            record1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            record2.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            record3.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            count++;
            rewardCount.setText(count+"/3");
            recordText.setText("Recorded " + records + " times in total");
        }
    }

    // Icons made by Freepik from https://www.flaticon.com and is licensed by
    // http://creativecommons.org/licenses/by/3.0/
    private void setCompareTrophy() {

        compareText.setText("Compared " + compares + "/5 times");

        // If the user has saved 5 or more attempts, display the second prize
        if (compares >= 5 && compares < 15 && !saveCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/firstach.png"));
            prize2.setImage(prize);
            compare1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            count++;
            rewardCount.setText(count+"/3");
            compareText.setText("Compared " + compares + "/15 times");


            // If the user has recorded more than 25 times, display the second prize
        } else if (compares >= 15 && compares < 30 && !saveCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/secondach.png"));
            prize2.setImage(prize);
            compare1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            compare2.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            count++;
            rewardCount.setText(count+"/3");
            compareText.setText("Compared " + compares + "/30 times");


            // If the user has recorded more than 50 times, display the third prize
        } else if (compares >= 30 && !saveCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/thirdach.png"));
            prize2.setImage(prize);
            compare1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            compare2.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            compare3.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            count++;
            rewardCount.setText(count+"/3");
            compareText.setText("Compared " + compares + " times in total");

        }


    }


    // Icons made by Freepik from https://www.flaticon.com and is licensed by
    // http://creativecommons.org/licenses/by/3.0/
    private void setSpecialTrophy() {


        // If the user finds one of the special features, display bronze trophy
        if (special == 1 && !specialCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/firstach.png"));
            prize3.setImage(prize);
            special1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            rewardCount.setText(count+"/3");
            specialCheck = false;
        }

        // If the user finds two of the special features, display bronze trophy
        if (special == 2 && !specialCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/secondach.png"));
            prize3.setImage(prize);
            special1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            special2.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            rewardCount.setText(count+"/3");
            specialCheck = false;
        }

        // If the user finds two of the special features, display bronze trophy
        if (special == 3 && !specialCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/secondach.png"));
            prize3.setImage(prize);
            special1.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            special2.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            special3.setFill(javafx.scene.paint.Color.valueOf("#ffb71b"));
            rewardCount.setText(count+"/3");
            specialCheck = false;
        }


    }


    


}
