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
import javafx.scene.Node;

public class App extends Application {

    Media pick;
    MediaPlayer player;
    MediaView mediaView;
    String listSelected;
    Double btnWidth = 90.0, btnHeight = 45.0;
    Stage createWindow = new Stage();

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

     
        Button playBtn = new Button("play");
        playBtn.setMaxWidth(Double.MAX_VALUE);
        playBtn.setPrefSize(btnWidth, btnHeight);
        playBtn.setOnAction(e -> playCreation());
        playBtn.setDisable(true);

        Button createBtn = new Button("create");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setPrefSize(btnWidth, btnHeight);
        createBtn.setOnAction(e -> createCreation(primaryStage));
        
        Button deleteBtn = new Button("delete");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setPrefSize(btnWidth, btnHeight);
        deleteBtn.setOnAction(e -> deleteCreation());;
        deleteBtn.setDisable(true);

        buttonBox.getChildren().addAll(playBtn, createBtn, deleteBtn);

        ListView<String> creationList = new ListView<String>();
        ObservableList<String> items = populateList();
        creationList.setItems(items);

        creationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("ListView selection changed from oldValue = " + oldValue + " to newValue = " + newValue);
                playBtn.setDisable(false);
                deleteBtn.setDisable(false);
                listSelected = newValue;
            }
        });

        pick = new Media(getClass().getResource("creations/BigBuckBunny_320x180.mp4").toExternalForm());
        player = new MediaPlayer(pick);
        mediaView = new MediaView(player);
        
        creationBox.getChildren().addAll(creationList, mediaView);

        base.getChildren().addAll(creationBox, buttonBox);

        Scene scene = new Scene(base, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("NameSayer");
        primaryStage.show();

        createWindow.initModality(Modality.APPLICATION_MODAL);
        createWindow.initOwner(primaryStage);

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

        Label promptMessage = new Label("Enter a name");
        TextField enterName = new TextField();
        enterName.setPrefSize(100, 20);


        Button confirmBtn = new Button("Confirm");
        confirmBtn.setPrefSize(btnWidth, btnHeight);
        confirmBtn.setOnAction(e -> {
            String creationName = enterName.getText().toLowerCase();
            if (checkName(creationName)) {
                startCreating(creationName);
            }
            else {
                promptMessage.setText("Please enter valid name (only letters, digits, underscores and hyphens)");
            }
        });
        confirmBtn.setPrefSize(btnWidth, btnHeight);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefSize(btnWidth, btnHeight);
        cancelBtn.setOnAction(e -> createWindow.hide());

        HBox btnBox = new HBox(30);
        btnBox.getChildren().addAll(cancelBtn, confirmBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().addAll(promptMessage, enterName, btnBox);

        Scene dialogScene = new Scene(dialogVbox, 600, 150);
        createWindow.setScene(dialogScene);
        createWindow.setTitle("New Creation");
        createWindow.show();

    }

    private void startCreating(String name) {
        doCommand("ffmpeg", "-f", "lavfi", "-i", "color=c=blue:s=320x240:d=5", "-vf", "drawtext=fontfile=/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+name+"'", name+".mp4"/*, "&> /dev/null"*/);
        System.out.println("CREATED!");
        promptToRecord(name);
    }

    
    private void promptToRecord(String name) {
        Label promptMessage = new Label("Click to start recording yourself saying \"" + name + "\" for 5 seconds");
    
        Button recordBtn = new Button("Record");
        recordBtn.setPrefSize(btnWidth, btnHeight);
        recordBtn.setOnAction(e -> recordAudio());
        recordBtn.setPrefSize(btnWidth, btnHeight);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefSize(btnWidth, btnHeight);
        cancelBtn.setOnAction(e -> createWindow.hide());

        HBox btnBox = new HBox(30);
        btnBox.getChildren().addAll(cancelBtn, recordBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().addAll(promptMessage, btnBox);

        Scene dialogScene = new Scene(dialogVbox, 600, 150);
        createWindow.setScene(dialogScene);
        createWindow.setTitle("New Creation");
        createWindow.show();
    }


    private void recordAudio() {
        doCommand("ffmpeg", "-f", "alsa", "-i", "default", "-t", "5", "_audio.wav" /*&> /dev/null*/);
    }

    private boolean checkName(String name) {
        if (name.matches("^[a-zA-Z0-9 _-]+$")) {
            return true;
        } 
        else {
            return false;
        }
    }


    private void deleteCreation() {
        player.stop();
    }
    
    
    
    private void doCommand(String... cmd) {
        try {
            // Remove pre-existing temporary files if they exist
            ProcessBuilder removeBuilder = new ProcessBuilder("rm", "_audio.wav", "_video.mp4");
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


    

}