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

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox base = new VBox();
        base.setAlignment(Pos.CENTER);
        HBox creationBox = new HBox(20);
        creationBox.setAlignment(Pos.CENTER);
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        ListView<String> creationList = new ListView<String>();
        ObservableList<String> items = populateList();
        creationList.setItems(items);

        //scrollList.setPrefSize(120, 120);
        Rectangle rect = new Rectangle(50, 50, Color.RED);
        creationBox.getChildren().addAll(creationList, rect);

        Button playBtn = new Button("play");
        Button createBtn = new Button("create");
        Button deleteBtn = new Button("delete");
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
			ProcessBuilder builder = new ProcessBuilder("ls");
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
			System.out.println("POPULATE TRY CATCH FAILED");
		}
        

        return list;
    }

}