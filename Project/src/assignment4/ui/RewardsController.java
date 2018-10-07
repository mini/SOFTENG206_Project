package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class RewardsController extends BaseController  {

    public static int records = 0;
    public static int saves = 0;
    private static int count = 0;

    private static boolean recordCheck = false;
    private static boolean saveCheck = false;
    private static boolean specialCheck = false;
    public static boolean special = false;



    @FXML
    private ImageView prize1, prize2, prize3;

    @FXML
    private Text rewardCount;


    public void init() {

        // If the user has recorded more than 10 times, display the first prize
        if (records >= 10 && !recordCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/prize.png"));
            prize1.setImage(prize);
            count++;
            rewardCount.setText(count+"/3");
            recordCheck = true;
        }

        // If the user has saved 5 or more attempts, display the second prize
        if (saves >= 5 && !saveCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/prize.png"));
            prize2.setImage(prize);
            count++;
            rewardCount.setText(count+"/3");
            saveCheck = false;
        }

        if (special && !specialCheck) {
            Image prize = new Image(getClass().getResourceAsStream("/resources/icons/prize.png"));
            prize3.setImage(prize);
            rewardCount.setText(count+"/3");
            specialCheck = false;
        }
    }

    @FXML
    private void backPressed() {
        showScene("MainMenu.fxml", false, false);
    }
}
