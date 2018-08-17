//package namesayer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.scene.control.TextArea;



public class Test extends Application {

    Scene scene, scene2;
    Button button, button2;
    Label label, label2;
    private TextArea txtOutput = new TextArea();
    

    public static void main(String[] args) {
        launch(args);
    }


   @Override
    public void start(Stage primaryStage) throws Exception {

        populateList();

        button = new Button("Click me!");
        button.setOnAction(e -> System.out.println("Succ"));
        
        button2 = new Button("Click me!");
        button2.setOnAction(e -> System.out.println(":)"));

        Rectangle rect = new Rectangle(50, 50, Color.RED);
        ScrollPane s1 = new ScrollPane();
        s1.setPrefSize(120, 120);
        s1.setContent(rect);

        HBox layout = new HBox();
        layout.setPadding(new Insets(15, 12, 15, 12));
        layout.setSpacing(10);
        layout.setStyle("-fx-background-color: #ff8000");
        layout.getChildren().addAll(button, button2, s1, txtOutput);
        scene = new Scene(layout, 500, 500);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("NameSayer");
        primaryStage.show();

    }


    private void populateList() {
		String dir = new String("~");
        //txtOutput.setText("");
        System.out.println("Inside populateList()");
		
		try {
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", "ls");
			Process process = builder.start();

			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader stderrBuffered = new BufferedReader(new InputStreamReader(stderr));

			String outStr = stdoutBuffered.readLine();
            String errStr = stderrBuffered.readLine();
            
            System.out.println(outStr);
            System.out.println(errStr);
			
			String line = null;
			while ((errStr == null) && (line = stdoutBuffered.readLine()) != null) {
                //txtOutput.appendText(line + "\n");
                System.out.println(line);
			}
			if (errStr != null) {
                System.out.println(":(");
                txtOutput.appendText(":(");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("FAILED");
		}
		
	}

    
    
}