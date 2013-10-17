package pojogenerator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.swing.JOptionPane;

import scala.Tuple2;
import scala.collection.mutable.ListBuffer;

public class SettingsPane extends BorderPane {
	private TextField classNameField = new TextField();
	private TextArea fieldsArea = new TextArea();
	private CheckBox includeConstructorsBox = checkBox("Include Constructors");
	private CheckBox overrideToStringBox = checkBox("Override toString");
	private CheckBox overrideEqualsBox = checkBox("Override equals");
	private TextField tabSizeField = new TextField();
	private EditorPane editorPane;
	
	public SettingsPane(EditorPane editorPane) {
		this.editorPane = editorPane;
		setPadding(new Insets(20, 20, 20, 20));
		setCenter(createCenterPane());
		setBottom(createBottomPane());
		reset();
	}
	
	private Node createCenterPane() {
		VBox classNamePane = new VBox();
		VBox fieldsPane = new VBox();
		VBox optionsPane = new VBox();
		VBox tabSizePane = new VBox();
		
		classNamePane.setPadding(new Insets(0, 0, 20, 0));
		classNamePane.setSpacing(3);
		classNamePane.getChildren().addAll(label("Class Name (e.g. \"Person\"):"), classNameField);
		
		fieldsPane.setPadding(new Insets(0, 0, 30, 0));
		fieldsPane.setSpacing(3);
		fieldsPane.getChildren().addAll(
				label("Fields (separated by commas e.g. \"String name, String address, int age\"):"), 
				fieldsArea);
		
		optionsPane.setSpacing(7);
		optionsPane.setPadding(new Insets(0, 0, 20, 0));
		optionsPane.getChildren().addAll(includeConstructorsBox, 
				overrideToStringBox, overrideEqualsBox);
		
		tabSizePane.setSpacing(3);
		tabSizePane.getChildren().addAll(label("Tab size:"), tabSizeField);
		
		VBox centerPane = new VBox();
		centerPane.getChildren().addAll(classNamePane, fieldsPane, optionsPane, tabSizePane);
		return centerPane;
	}
	
	private Node createBottomPane() {
		Button generateButton = new Button("Generate POJO");
		Button resetButton = new Button("Reset Defaults");
		
		HBox buttonsPane = new HBox();
		buttonsPane.setSpacing(7);
		buttonsPane.getChildren().addAll(generateButton, resetButton);
		
		BorderPane bottomPane = new BorderPane();
		bottomPane.setPadding(new Insets(50, 0, 0, 0));
		bottomPane.setRight(buttonsPane);
		
		generateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				String className = classNameField.getText().trim();
				if (className.isEmpty()) {
					showErrorDialog("Class Name is required");
					classNameField.requestFocus();
					return;
				} 
				
				String fieldsStr = fieldsArea.getText().trim();
				if (fieldsStr.isEmpty()) {
					showErrorDialog("Fields are required");
					fieldsArea.requestFocus();
					return;
				}
				
				ListBuffer<Tuple2<String, String>> fieldsBuff = new ListBuffer<>();
				String[] fieldsArray = fieldsStr.split(",");
				
				boolean allFieldsValid = true;
				for (String fieldStr : fieldsArray) {
					fieldStr = fieldStr.trim();
					if (fieldStr.length() < 3) { 
						allFieldsValid = false;
						break;
					}
					int commaIndex = fieldStr.indexOf(" ");
					if (commaIndex == -1) {
						allFieldsValid = false;
						break;
					}
					String type = fieldStr.substring(0, commaIndex);
					String field = fieldStr.substring(commaIndex + 1).trim();
					fieldsBuff.$plus$eq(new Tuple2<String, String>(type, field));
				}
				
				if (!allFieldsValid) {
					showErrorDialog("Some fields are invalid");
					fieldsArea.requestFocus();
					return;
				}
				
				String tabSizeStr = tabSizeField.getText().trim();
				if (tabSizeStr.isEmpty()) {
					showErrorDialog("Tab size is required");
					tabSizeField.requestFocus();
					return;
				}
				if (!tabSizeStr.matches("\\d")) {
					showErrorDialog("Invalid tab size format");
					tabSizeField.requestFocus();
					return;
				}
				
				int tabSize = Integer.parseInt(tabSizeStr);
				
				boolean includeConstructors = includeConstructorsBox.isSelected();
				boolean overrideToString = overrideToStringBox.isSelected();
				boolean overrideEquals = overrideEqualsBox.isSelected();
				
				String pojoString = POJOGenerator.apply(className, 
						fieldsBuff.toList(), tabSize, includeConstructors, overrideToString, overrideEquals); 
				editorPane.getTextArea().setText(pojoString);
			}
		});
		
		resetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				reset();
			}
		});
		
		return bottomPane;
	}
	
	public void reset() {
		classNameField.setText("");
		fieldsArea.setText("");
		includeConstructorsBox.setSelected(true);
		overrideToStringBox.setSelected(true);
		overrideEqualsBox.setSelected(true);
		tabSizeField.setText("4");
		classNameField.requestFocus();
	}
	
	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "Input Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private Label label(String text) {
		Label label = new Label(text);
		label.setTextFill(Color.WHITE);
		return label;
	}
	
	private CheckBox checkBox(String text) {
		CheckBox cbox = new CheckBox(text);
		cbox.setStyle("-fx-text-fill: white");
		return cbox;
	}
}
