package pojogenerator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;

public class EditorPane extends BorderPane {
	private TextArea textArea;
	
	public EditorPane() {
		setCenter(initTextArea());
		setBottom(createBottomPane());
		setPadding(new Insets(20, 20, 20, 20));
	}
	
	private TextArea initTextArea() {
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 14");
		textArea.setPrefWidth(500);
		return textArea;
	}
	
	private Node createBottomPane() {
		Button copyButton = new Button("Copy to Clipboard");
		BorderPane bottomPane = new BorderPane();
		bottomPane.setRight(copyButton);
		bottomPane.setPadding(new Insets(20, 0, 0, 0));
		
		copyButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString(textArea.getText());
				clipboard.setContent(content);
			}
		});
		return bottomPane;
	}
	
	public TextArea getTextArea() {
		return textArea;
	}
}
