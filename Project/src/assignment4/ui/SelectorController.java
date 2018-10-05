package assignment4.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import assignment4.model.Name;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class SelectorController extends BaseController {
	private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	private FileChooser fileChooser = new FileChooser();
	private File lastSelected;
	//@formatter:off
	@FXML private TextField searchTextField;
	@FXML private ListView<Name> namesList;
	
	@FXML private TextArea textInput;
	
	@FXML private Button backButton;
	@FXML private Button playButton;
	@FXML private Button loadButton;
	@FXML private Button saveButton;
	
	@FXML private Button helpButton;
	//@formatter:on

	@Override
	public void init() {
		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			namesList.setItems(FXCollections.observableArrayList(namesDB.getNames(newValue)));
		});

		namesList.setCellFactory(Value -> new ListCell<Name>() {
			@Override
			protected void updateItem(Name item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
		namesList.getItems().addAll(namesDB.getAllNames());

		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Text", "*.txt"),
				new FileChooser.ExtensionFilter("All", "*.*")
		);
		
		
        concatenateNames("Junyan Zhao");
	}

	@FXML
	private void backPressed() {
		showScene("/resources/MainMenu.fxml", false);
	}

	@FXML
	private void loadPressed() {
		fileChooser.setTitle("Select playlist file");
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			try (Scanner scanner = new Scanner(file).useDelimiter("\\Z")) {
				textInput.setText(scanner.next());
				lastSelected = file;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void savePressed() {
		fileChooser.setTitle("Save playlist to");
		if(lastSelected != null) {
			fileChooser.setInitialDirectory(lastSelected.getParentFile());
			fileChooser.setInitialFileName(lastSelected.getName());
		} else {
			fileChooser.setInitialFileName(fileDateFormat.format(new Date()));
		}
		File file = fileChooser.showSaveDialog(primaryStage);
		if(file != null) {
			try {
				Files.write(file.toPath(), textInput.getText().getBytes(), StandardOpenOption.CREATE);
				lastSelected = file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void playPressed() {
		
	}
	

	@FXML
	private void helpPressed() {
		// show help popup
	}
	
	/*--------------------------------------------------------------------------------------------------------------------------*/
	
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
}
