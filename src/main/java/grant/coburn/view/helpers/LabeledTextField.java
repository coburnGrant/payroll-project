package grant.coburn.view.helpers;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class LabeledTextField extends HBox {
    private final Text textFieldLabel;
    private final TextField textField;

    public LabeledTextField(String label) {
        this.textFieldLabel = new Text(label);
        this.textField = new TextField();

        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(textFieldLabel, textField);
    }

    /** Getter for the current value of the textField's text */
    public String getText() {
        return textField.getText();
    }

    /** Setter for the textField's text */
    public void setText(String text) {
        textField.setText(text);
    }

    /** Adds a listener to the text field to ensure only numbers and decimals are entered */
    public void setNumbersOnly() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d.]", ""));
                // Ensure only one decimal point
                int index = textField.getText().indexOf(".");
                if (index != -1) {
                    textField.setText(textField.getText().substring(0, index + 1) +
                            textField.getText().substring(index + 1).replaceAll("\\.", ""));
                }
            }
        });
    }

    /** Attempts to get a double value from the text field */
    public double getDoubleValue() {
        return Double.parseDouble(getText());
    }

    /** Sets the textField's text an empty string */
    public void clearText() {
        setText("");
    }
}