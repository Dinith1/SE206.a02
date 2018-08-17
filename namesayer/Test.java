package namesayer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class Test extends Application {

    Scene scene, scene2;
    Button button, button2;
    Label label, label2;

    public static void main(String[] args) {
        launch(args);
    }

   @Override
    public void start(Stage primaryStage) throws Exception {

        button = new Button("Click me!");
        button.setOnAction(e -> System.out.println("Succ"));
        
        button2 = new Button("Click me!");
        button2.setOnAction(e -> System.out.println(":)"));

        HBox layout = new HBox();
        layout.setPadding(new Insets(15, 12, 15, 12));
        layout.setSpacing(10);
        layout.setStyle("-fx-background-color: #ff8000;");
        layout.getChildren().addAll(button, button2);
        scene = new Scene(layout, 300, 250);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("NameSayer");
        primaryStage.show();

    }
    
}