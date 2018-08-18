import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.scene.control.TextArea;
import javafx.collections.*;
import javafx.util.*;
import javafx.scene.media.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Modality;

public class App extends Application {

    Media pick;
    MediaPlayer player;
    MediaView mediaView;
    String listSelected;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox base = new VBox();
        base.setSpacing(50);
        base.setAlignment(Pos.CENTER);

        HBox creationBox = new HBox(20);
        creationBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        ListView<String> creationList = new ListView<String>();
        ObservableList<String> items = populateList();
        creationList.setItems(items);

        //listView.getSelectionModel().getSelectedItem();
        creationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("ListView selection changed from oldValue = " + oldValue + " to newValue = " + newValue);
                listSelected = newValue;
            }
        });



        pick = new Media(getClass().getResource("creations/BigBuckBunny_320x180.mp4").toExternalForm());
        player = new MediaPlayer(pick);
        mediaView = new MediaView(player);
        



        //scrollList.setPrefSize(120, 120);
        //Rectangle rect = new Rectangle(50, 50, Color.RED);
        creationBox.getChildren().addAll(creationList, mediaView);


        Button playBtn = new Button("play");
        playBtn.setMaxWidth(Double.MAX_VALUE);
        playBtn.setOnAction(e -> playCreation());

        Button createBtn = new Button("create");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> createCreation(primaryStage));
        
        Button deleteBtn = new Button("delete");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> deleteCreation());;

        buttonBox.getChildren().addAll(playBtn, createBtn, deleteBtn);

        base.getChildren().addAll(creationBox, buttonBox);

        Scene scene = new Scene(base, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("NameSayer");
        primaryStage.show();

    } 


    private ObservableList<String> populateList() {
        ObservableList<String> list = FXCollections.observableArrayList();;
		
		try {
			ProcessBuilder builder = new ProcessBuilder("ls", "creations/");
			Process process = builder.start();

			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            BufferedReader stderrBuffered = new BufferedReader(new InputStreamReader(stderr));
            
			String errStr = stderrBuffered.readLine();
			
			String line = null;
			while ((errStr == null) && (line = stdoutBuffered.readLine()) != null) {
				list.add(line);
			}
			if (errStr != null) {
				System.out.println("errStr is not null: " + errStr);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("POPULATE() TRY CATCH FAILED");
		}
        
        return list;
    }


    private void playCreation() {
        pick = new Media(getClass().getResource("creations/"+listSelected).toExternalForm());
        player = new MediaPlayer(pick);
        mediaView.setMediaPlayer(player);
        player.play();
    }

    private void createCreation(Stage primaryStage) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);

        TextField enterName = new TextField();
        Button confirmBtn = new Button("Confirm");
        Button cancelBtn = new Button("Cancel");

        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().addAll(new Label("Enter a name"), enterName, confirmBtn, cancelBtn);

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();


        String name = "memes";

        doCommand("ffmpeg", "-f", "lavfi", "-i", "color=c=blue:s=320x240:d=5", "-vf", "drawtext=fontfile=/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+name+"'", "creations/memes.mp4"/*, "&> /dev/null"*/);
        System.out.println("CREATED!");
        recordAudio();
    }

    private void deleteCreation() {

    }

    private void doCommand(String... cmd) {
        try {
            // Remove pre-existing temporary files if they exist
            ProcessBuilder removeBuilder = new ProcessBuilder("rm", "creations/_audio.wav", "creations/_video.mp4");
            Process removeProcess = removeBuilder.start();

			ProcessBuilder builder = new ProcessBuilder(cmd);
			Process process = builder.start();

			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            BufferedReader stderrBuffered = new BufferedReader(new InputStreamReader(stderr));
            
			String errStr = stderrBuffered.readLine();
			
			String line = null;
			while ((errStr == null) && (line = stdoutBuffered.readLine()) != null) {
				System.out.println(line);
			}
			if (errStr != null) {
				System.out.println("errStr is not null: " + errStr);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(cmd + " ***************TRY CATCH FAILED");
		}
    }


    private void recordAudio() {

    }

}