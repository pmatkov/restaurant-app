package hr.pmatkovic.utils;

import hr.pmatkovic.entities.general.Identifier;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

/**
 * A utility class with helper methods for controllers
 */

public class ControllerUtils {

    public static <T> void setColumnValueFactory(TableColumn<T, String> column, Function<T, String> getterMethod) {
        column.setCellValueFactory(cellData -> new SimpleStringProperty(getterMethod.apply(cellData.getValue())));
    }

    public static <T extends TableView<S>, S> void clearRowSelectionIfSelected(T tableView, GridPane inputFieldsGridPane, Button button) {

        tableView.setRowFactory(tr -> {

            final TableRow<S> row = new TableRow<>();

            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)) {
                    tableView.getSelectionModel().clearSelection();
                    event.consume();
                    }
                }
            );
            return row;
        });
    }

    public static void clearRowSelectionIfOutside(Scene scene, TableView<? extends Identifier> tableView, GridPane inputFieldsGridPane, Button button) {

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {

            Node selectedNode = event.getPickResult().getIntersectedNode();

            boolean nodeSelected = false;
            for (Node node : inputFieldsGridPane.getChildren()) {
                if (node instanceof TextField || node instanceof DatePicker || node instanceof ComboBox<?>) {
                    if (node.isFocused()) {
                        nodeSelected = true;
                        break;
                    }
                }
            }
            if (!nodeSelected) {
                while (selectedNode != null && !(selectedNode instanceof TableRow)) {
                    selectedNode = selectedNode.getParent();
                }
                if (selectedNode == null) {
                    tableView.getSelectionModel().clearSelection();
                }
            }
        });
    }


    public static <T extends Pane> void clearInputFields(T pane) {

        for (Node node : pane.getChildren()) {
            if (node instanceof TextField)
                ((TextField) node).clear();
            else if (node instanceof DatePicker)
                ((DatePicker) node).setValue(null);
            else if (node instanceof ComboBox<?>) {
                ((ComboBox<?>) node).setValue(null);
            }
            node.setStyle(null);
        }
    }

    public static <T extends Pane> void clearTextFields(T pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof TextField && node.getId().contains("Old")) {
                ((TextField) node).clear();
            }
        }
    }

    public static void setDateConverter(DatePicker datePicker) {

        datePicker.setConverter(new StringConverter<>() {

            @Override
            public String toString(LocalDate date) {

                ValidationUtils.setDateValidationStatus(true);
                try {
                    if (date != null) {
                        return GeneralUtils.DATE_FORMATTER.format(date);
                    } else {
                        return "";
                    }
                } catch (DateTimeParseException e) {
                    ValidationUtils.setDateValidationStatus(false);
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String text) {

                ValidationUtils.setDateValidationStatus(true);
                try {
                    if (text != null && !text.isEmpty()) {
                        return LocalDate.parse(text, GeneralUtils.DATE_FORMATTER);
                    }
                    else
                        return null;
                } catch (DateTimeParseException e) {
                    ValidationUtils.setDateValidationStatus(false);
                    return null;
                }
            }
        });
    }


    public static <T> void setDefaultColumnValue(TableColumn<T, String> column) {

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                if (item != null && (item.equals("0.0") || item.equals("0"))) {
                    setText("Not available");
                } else if (item != null) {
                    setText(item);
                }
            } else {
                setText(null);
            }
            }
        });
    }

    public static <T> void setTextComboBox(ComboBox<T> comboBox) {

        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T obj, boolean empty) {
                super.updateItem(obj, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(obj.toString());
                }
            }
        });
    }
    public static <T extends ComboBox<S>, S> void clearSelectionComboBox(T comboBox) {

        comboBox.setCellFactory(cb -> {
            final ListCell<S> cell = new ListCell<>(){

                @Override
                public void updateItem(final S item, final boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty)
                        setText(item.toString());
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = cell.getIndex();
                if (index >= 0 && index < comboBox.getItems().size() && comboBox.getSelectionModel().isSelected(index)) {
                    comboBox.getSelectionModel().clearSelection();
                    event.consume();
                }
            });
            return cell;
        });
    }

    public static void compareTextFields(TextField textfield1, TextField textfield2) {

        textfield1.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!textfield1.getText().equals(textfield2.getText()) && !textfield2.getText().isEmpty()) {
                textfield2.setStyle("-fx-text-box-border: red");
            } else {
                textfield2.setStyle("");
            }
        });

        textfield2.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!textfield1.getText().equals(textfield2.getText()) && !textfield2.getText().isEmpty()) {
                textfield2.setStyle("-fx-text-box-border: red");
            } else {
                textfield2.setStyle("");
            }
        });
    }

    public static void displayChangeLabel(Label modelUpdateLabel, String text, String nextText, int delay) {

        modelUpdateLabel.setText(text);
        modelUpdateLabel.setStyle("-fx-text-fill: cornflowerblue");
        modelUpdateLabel.setAlignment(Pos.BASELINE_RIGHT);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            modelUpdateLabel.setText(nextText);
            modelUpdateLabel.setStyle("");
        }));
        timeline.play();

    }

    public static void displayGeneralDialog(String title, String header, String content, Alert.AlertType alertType) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static ButtonType displayConfirmationDialog(String title, String header, String content) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait().orElse(null);
    }

}

