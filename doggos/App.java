package namesayer;
import namesayer.NSButton;
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
import java.io.File;
import javafx.animation.PauseTransition;
import javafx.scene.layout.Border;

public class App extends Application {

    Media pick;
    MediaPlayer player;
    MediaView mediaView;
    String listSelected;
    Stage createWindow = new Stage();
    ListView<String> creationList;
    ObservableList<String> listItems = FXCollections.observableArrayList();
    Stage primaryStage;
    Scene mainScene;
    CommandHandler cmdHandler = new CommandHandler();

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        VBox base = new VBox();
        base.setSpacing(50);
        base.setAlignment(Pos.CENTER);

        HBox creationBox = new HBox(20);
        creationBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

     
        NSButton playBtn = new NSButton("play");
        playBtn.setOnAction(e -> playCreation());
        playBtn.setDisable(true);

        NSButton createBtn = new NSButton("create");
        createBtn.setOnAction(e -> createCreation(primaryStage));
        
        NSButton deleteBtn = new NSButton("delete");
        deleteBtn.setOnAction(e -> deleteCreation());;
        deleteBtn.setDisable(true);

        buttonBox.getChildren().addAll(playBtn, createBtn, deleteBtn);

        creationList = new ListView<String>();
        updateListView();

        creationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	if(newValue == null)
            		return;
                System.out.println("ListView selection changed from oldValue = " + oldValue + " to newValue = " + newValue);
            	listSelected = newValue;
            	
                pick = new Media(new File(System.getProperty("user.dir")+"/creations/"+listSelected).toURI().toString());
                player = new MediaPlayer(pick);
                mediaView.setMediaPlayer(player);

                playBtn.setDisable(false);
                deleteBtn.setDisable(false);
            }
        });

        //pick = new Media(getClass().getResource("creations/BigBuckBunny_320x180.mp4").toExternalForm());
        ///player = new MediaPlayer(pick);
        //mediaView = new MediaView(player);
        mediaView = new MediaView();

        VBox mediaBox = new VBox();
        mediaBox.setAlignment(Pos.CENTER);
        mediaBox.getChildren().add(mediaView);
        
        creationBox.getChildren().addAll(creationList, mediaBox);
        creationBox.setMargin(creationList, new Insets(20, 20, 20, 20));

        base.getChildren().addAll(creationBox, buttonBox);

        mainScene = new Scene(base, 800, 800);
        this.primaryStage.setScene(mainScene);
        this.primaryStage.setTitle("NameSayer");
        this.primaryStage.show();

        createWindow.initModality(Modality.APPLICATION_MODAL);
        createWindow.initOwner(primaryStage);

    }
    
    
    private void updateListView() {
    	creationList.getItems().clear();
    	listItems.clear();
        listItems = populateList();
        //creationList.setItems(listItems);
        this.primaryStage.setScene(mainScene);
        this.primaryStage.show();
    }


    private ObservableList<String> populateList() {
    	cmdHandler.doCommand("mkdir creations");
        ObservableList<String> list = FXCollections.observableArrayList();
		
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
				//list.add(line.substring(0, line.length() - 4)); // Remove the .mp4 extension
				creationList.getItems().add(line);
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
        player.play();
    }


    private void createCreation(Stage primaryStage) {

        Label promptMessage = new Label("Enter a name");
        TextField enterName = new TextField();
        enterName.setPrefSize(100, 20);


        NSButton confirmBtn = new NSButton("Confirm");
        confirmBtn.setOnAction(e -> {
            String creationName = enterName.getText().toLowerCase();
            if (checkName(creationName)) {
                if (creationExists(creationName)) {
                    promptMessage.setText("That name already exists");
                }
                else {
                    startCreating(creationName);
                }
            }
            else {
                promptMessage.setText("Please enter valid name (only letters, digits, underscores and hyphens)");
            }
        });

        NSButton cancelBtn = new NSButton("Cancel");
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
        cmdHandler.doCommand("mkdir creations");
        promptToRecord(name);
    }

    
    private void promptToRecord(String name) {
        Label promptMessage = new Label("Click to start recording yourself saying \"" + name + "\" for 5 seconds");
    
        NSButton recordBtn = new NSButton("Record");
        recordBtn.setOnAction(e -> recordAudio(name));

        NSButton cancelBtn = new NSButton("Cancel");
        cancelBtn.setOnAction(e -> createWindow.hide());

        HBox btnBox = new HBox(30);
        btnBox.getChildren().addAll(cancelBtn, recordBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().addAll(promptMessage, btnBox);

        Scene dialogScene = new Scene(dialogVbox, 600, 150);
        createWindow.setScene(dialogScene);
        createWindow.setTitle("Record Audio");
        createWindow.show();
    }


    private void recordAudio(String name) {
        Label promptMessage = new Label("Recording...");
        ProgressBar progressBar = new ProgressBar();

        VBox dialogVbox2 = new VBox(20);
        dialogVbox2.setAlignment(Pos.CENTER);
        dialogVbox2.getChildren().addAll(promptMessage, progressBar);

        Scene dialogScene2 = new Scene(dialogVbox2, 600, 150);
        createWindow.setScene(dialogScene2);
        createWindow.setTitle("Recording...");
        createWindow.show();

        cmdHandler.removeTempFiles();
        cmdHandler.doCommand("ffmpeg -f alsa -i default -t 5 _audio.wav");

        // Go to next screen after 5 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> promptAfterRecording(name));
        delay.play();
    }


    private void promptAfterRecording(String name) {
        Label promptMessage = new Label("Choose one of the options below");
    
        NSButton listenBtn = new NSButton("Listen");
        listenBtn.setOnAction(e -> listen());

        NSButton recordBtn = new NSButton("Re-Record");
        recordBtn.setOnAction(e -> promptToRecord(name));

        NSButton confirmBtn = new NSButton("Confirm");
        confirmBtn.setOnAction(e -> {
            makeVideo(name);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> {
            	cmdHandler.removeTempFiles();
            	updateListView();
            });
            delay.play();
            //listItems.add(name);
            updateListView();
            createWindow.hide();
        });

        HBox btnBox = new HBox(30);
        btnBox.getChildren().addAll(listenBtn, recordBtn, confirmBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().addAll(promptMessage, btnBox);

        Scene dialogScene = new Scene(dialogVbox, 600, 150);
        createWindow.setScene(dialogScene);
        createWindow.setTitle("Check Audio");
        createWindow.show();
    }


    private void listen() {
        Media sound = new Media(new File(System.getProperty("user.dir")+"/_audio.wav").toURI().toString());
        //pick = new Media(new File(System.getProperty("user.dir")+"/creations/"+listSelected).toURI().toString());
        MediaPlayer soundPlayer = new MediaPlayer(sound);
        soundPlayer.play();
    }


    private void makeVideo(String name) {
        Process videoProcess = cmdHandler.doCommand("ffmpeg -f lavfi -i color=c=blue:s=320x240:d=5 -vf \"drawtext=fontfile=/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+name+"'\" _video.mp4");
        try {
        	videoProcess.waitFor();
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        String command = "ffmpeg -i _video.mp4 -i _audio.wav -c:v copy -c:a aac -strict experimental creations/'"+name+".mp4'";
        cmdHandler.doCommand(command);        
        System.out.println("CREATED!");
    }


    private boolean checkName(String name) {
        boolean out = (name.matches("^[a-zA-Z0-9 _-]+$")) ? true : false;
        return out;
    }


    private boolean creationExists(String name) {
        boolean out = (listItems.contains(name)) ? true : false;
        return out;
    }


    private void deleteCreation() {
        confirmDelete();
        updateListView();
    }


    private void confirmDelete() {
        Label promptMessage = new Label("Are you sure you want to delete '"+listSelected+"'?");
    
        NSButton confirmBtn = new NSButton("Confirm");
        confirmBtn.setOnAction(e -> removeCreation());

        NSButton cancelBtn = new NSButton("Cancel");
        cancelBtn.setOnAction(e -> createWindow.hide());

        HBox btnBox = new HBox(30);
        btnBox.getChildren().addAll(confirmBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().addAll(promptMessage, btnBox);

        Scene dialogScene = new Scene(dialogVbox, 600, 150);
        createWindow.setScene(dialogScene);
        createWindow.setTitle("Are you sure you want to delete this creation?");
        createWindow.show();
    }


    private void removeCreation() {
        cmdHandler.doCommand("rm creations/'"+listSelected+"'");
        updateListView();
        mediaView.setMediaPlayer(null);
        createWindow.hide();
    }  

    
    


}