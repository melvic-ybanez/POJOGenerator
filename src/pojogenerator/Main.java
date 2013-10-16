package pojogenerator;

import com.aquafx_project.AquaFx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("POJOGenerator"); 
		EditorPane editorPane = new EditorPane();
		SettingsPane settingsPane = new SettingsPane(editorPane);
		BorderPane root = new BorderPane();
		root.setLeft(settingsPane);
		root.setCenter(editorPane);
		root.setStyle("-fx-background-color: #336699");
		primaryStage.setScene(new Scene(root, root.getPrefWidth(), 600));
		primaryStage.show();
		primaryStage.setResizable(false);
	}
}
