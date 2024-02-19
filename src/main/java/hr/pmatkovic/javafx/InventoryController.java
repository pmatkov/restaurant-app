package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.entities.inventory.*;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DatabaseException;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.threads.ExpiringItemsThread;
import hr.pmatkovic.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hr.pmatkovic.entities.inventory.InventoryItem.calculateExpiryDate;
import static hr.pmatkovic.entities.staff.Employee.createEmployee;
import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.*;
import static hr.pmatkovic.utils.ValidationUtils.*;

/**
 * Inventory controller
 */

public class InventoryController {

    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TextField pricePerItemTextField;
    @FXML
    private TextField bestBeforeTextField;
    @FXML
    private DatePicker purchaseDateDatePicker;
    @FXML
    private DatePicker expiryDateDatePicker;
    @FXML
    private ListView<InventoryItem> soonToExpireListView;

    @FXML
    private TextField addNameTextField;
    @FXML
    private ComboBox<Category> addCategoryComboBox;
    @FXML
    private TextField addQuantityTextField;
    @FXML
    private TextField addPricePerItemTextField;
    @FXML
    private TextField addBestBeforeTextField;
    @FXML
    private DatePicker addPurchaseDateDatePicker;
    @FXML
    private DatePicker addExpiryDateDatePicker;

    @FXML
    private TableView<InventoryItem> inventoryTableView;
    @FXML
    private TableColumn<InventoryItem, String> nameTableColumn;
    @FXML
    private TableColumn<InventoryItem, String> categoryTableColumn;
    @FXML
    private TableColumn<InventoryItem, String> quantityTableColumn;
    @FXML
    private TableColumn<InventoryItem, String> pricePerItemTableColumn;
    @FXML
    private TableColumn<InventoryItem, String> bestBeforeTableColumn;
    @FXML
    private TableColumn<InventoryItem, String> purchaseDateTableColumn;
    @FXML
    private TableColumn<InventoryItem, String> expiryDateTableColumn;

    @FXML
    private GridPane inputFieldsGridPane;
    @FXML
    private Button button;
    @FXML
    private Label modelUpdateLabel;

    private List<InventoryItem> inventoryItems;
    private List<InventoryItem> filteredInventoryItems;

    private ObservableList<InventoryItem> inventoryItemsObservableList;
    private Map<InventoryItem, ChangedData<InventoryItem, Employee>> dataMap = new HashMap<>();

    public void initialize() {

        try {
            inventoryItems = DatabaseUtils.getInventoryItems(null);

            InventoryItem.setExpiryDates(inventoryItems);
            DatabaseUtils.updateInventoryItemExpiry(inventoryItems);

        } catch (DatabaseException e) {

            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }

        inventoryItemsObservableList = FXCollections.observableArrayList(inventoryItems);

        soonToExpireListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getCategory() + " - " + calculateExpiryDate(item.getPurchaseDate(), ((Food) item).getBestBefore()).format(DATE_FORMATTER));
                }
            }
        });

        setColumnValueFactory(nameTableColumn, InventoryItem::getName);
        setColumnValueFactory(categoryTableColumn, i -> i.getCategory().toString());
        setColumnValueFactory(quantityTableColumn, i -> i.getQuantity().toString());
        setColumnValueFactory(pricePerItemTableColumn, i -> i.getPricePerItem().toString());

        bestBeforeTableColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Food) {
                return new SimpleStringProperty(((Food) cellData.getValue()).getBestBefore().toString());
            }
            else
                return new SimpleStringProperty("0");
        });

        setColumnValueFactory(purchaseDateTableColumn, e -> e.getPurchaseDate().format(DATE_FORMATTER));

        expiryDateTableColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Food) {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue(((Food) cellData.getValue()).getExpiryDate().format(DATE_FORMATTER));
                return property;
            }
            else
                return new SimpleStringProperty("0");
        });

        setDefaultColumnValue(bestBeforeTableColumn);
        setDefaultColumnValue(expiryDateTableColumn);

        inventoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DecimalFormat df = new DecimalFormat("###.##");
                addNameTextField.setText(newVal.getName());
                addCategoryComboBox.getSelectionModel().select(newVal.getCategory());
                addQuantityTextField.setText(df.format(newVal.getQuantity()));
                addPricePerItemTextField.setText(df.format(newVal.getPricePerItem()));
                if (newVal instanceof Food) {
                    addBestBeforeTextField.setDisable(false);
                    addBestBeforeTextField.setText(((Food) newVal).getBestBefore().toString());
                    addExpiryDateDatePicker.setValue(((Food) newVal).getExpiryDate());

                }
                else {
                    addBestBeforeTextField.setText("Not available");
                    addBestBeforeTextField.setDisable(true);
                    addExpiryDateDatePicker.setValue(LocalDate.of(2023, 1, 1));
                }
                addPurchaseDateDatePicker.setValue(newVal.getPurchaseDate());

                button.setText("Edit");
            }
            else {

                ControllerUtils.clearInputFields(inputFieldsGridPane);
                button.setText("Add");
            }
        });

        quantityTableColumn.setComparator(new NumbersComparator());
        pricePerItemTableColumn.setComparator(new NumbersComparator());
        bestBeforeTableColumn.setComparator(new NumbersComparator());
        purchaseDateTableColumn.setComparator(new DatesComparator());
        expiryDateTableColumn.setComparator(new DatesComparator());

        clearRowSelectionIfSelected(inventoryTableView, inputFieldsGridPane, button);
        clearRowSelectionIfOutside(Main.getMainStage().getScene(), inventoryTableView, inputFieldsGridPane, button);

        setTextComboBox(categoryComboBox);
        setTextComboBox(addCategoryComboBox);
        clearSelectionComboBox(categoryComboBox);
        clearSelectionComboBox(addCategoryComboBox);

        categoryComboBox.setItems(FXCollections.observableArrayList(Category.getListOfEnumValues()));
        addCategoryComboBox.setItems(FXCollections.observableArrayList(Category.getListOfEnumValues()));

        setDateConverter(purchaseDateDatePicker);
        setDateConverter(expiryDateDatePicker);
        setDateConverter(addPurchaseDateDatePicker);
        setDateConverter(addExpiryDateDatePicker);

        inventoryTableView.setItems(inventoryItemsObservableList);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame keyframe = GeneralUtils.setNewKeyFrame(javafx.util.Duration.seconds(5), new ExpiringItemsThread(soonToExpireListView, inventoryItemsObservableList));
        timeline.getKeyFrames().add(keyframe);
        timeline.play();

    }

    public void filterInventoryItems() {

        String name = nameTextField.getText();
        Category category = categoryComboBox.getSelectionModel().getSelectedItem();
        String quantity = quantityTextField.getText();
        String pricePerItem = pricePerItemTextField.getText();
        String bestBefore = bestBeforeTextField.getText();
        LocalDate purchaseDate = purchaseDateDatePicker.getValue();
        LocalDate expiryDate = expiryDateDatePicker.getValue();

        Optional<String> numericInput = checkIfNumericInput(Map.of("Quantity", quantity, "Price per item", pricePerItem, "Best before", bestBefore));
        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String inputErrors = Stream.of(numericInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (numericInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, inputErrors, Alert.AlertType.ERROR);
            return;
        }

        InventoryItem item = InventoryItem.createInventoryItem(0L, name, category, ValidationUtils.validateNumber(quantity,Double.class), ValidationUtils.validateNumber(pricePerItem, Double.class), ValidationUtils.validateNumber(bestBefore, Integer.class), purchaseDate, expiryDate);

        try {
            filteredInventoryItems = DatabaseUtils.getInventoryItems(item);

        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }

        inventoryTableView.setItems(FXCollections.observableArrayList(filteredInventoryItems));
    }

    public void updateInventoryItem() {

        String name = addNameTextField.getText();
        Category category = addCategoryComboBox.getSelectionModel().getSelectedItem();
        String quantity = addQuantityTextField.getText();
        String pricePerItem = addPricePerItemTextField.getText();
        String bestBefore = addBestBeforeTextField.getText().equals("Not available") ? "0" : addBestBeforeTextField.getText();
        LocalDate purchaseDate = addPurchaseDateDatePicker.getValue();

        Optional<String> emptyInput = checkIfEmptyInput(Map.of("Name", name, "Category", Optional.ofNullable(category), "Quantity", quantity, "Price per item", pricePerItem, "Best before", bestBefore, "Purchase date", Optional.ofNullable(purchaseDate)));
        Optional<String> numericInput = checkIfNumericInput(Map.of("Quantity", quantity, "Price per item", pricePerItem, "Best before", bestBefore));
        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String errors = Stream.of(emptyInput, numericInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (emptyInput.isPresent() || numericInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, errors, Alert.AlertType.ERROR);
            return;
        }

        InventoryItem selectedItem = inventoryTableView.getSelectionModel().getSelectedItem();
        InventoryItem item = InventoryItem.createInventoryItem(0L, name, category, ValidationUtils.validateNumber(quantity,Double.class), ValidationUtils.validateNumber(pricePerItem, Double.class), ValidationUtils.validateNumber(bestBefore, Integer.class), purchaseDate);

        ChangeType change = button.getText().equals("Add") ? ChangeType.ADD : ChangeType.EDIT;

        try {
            switch (change) {
                case ADD -> {
                    DatabaseUtils.addInventoryItem(item);
                    inventoryItemsObservableList.add(item);
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.ADDED, "", 2);
                }
                case EDIT -> {
                    item.setId(selectedItem.getId());

                    if (item.equals(selectedItem)) {
                        displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, GeneralUtils.INPUT_NO_CHANGE, Alert.AlertType.INFORMATION);
                        return;
                    } else {
                        ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_CHANGE + "data for " + selectedItem.getName() + "?");
                        if (selectedButton.equals(ButtonType.OK)) {
                            DatabaseUtils.editInventoryItem(item);
                            inventoryItemsObservableList.replaceAll(i -> (i.getId().equals(selectedItem.getId()) ? item : i));
                            ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.CHANGED, "", 2);

                        }
                    }
                }
            }
        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }

        ChangedData<InventoryItem, Employee> changedData = new ChangedData<>(item, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), change, LocalDateTime.now());

        try {
            dataMap = FileUtils.deserializeData(InventoryItem.class);
        } catch (DeserializationException e) {
            GeneralUtils.logger.error("Unable to deserialize. Inventory data is not available.");
        }

        dataMap.put(selectedItem, changedData);

        FileUtils.serializeData(dataMap, InventoryItem.class);

    }

    public void deleteInventoryItem() {

        int selectedIndex = inventoryTableView.getSelectionModel().getSelectedIndex();
        InventoryItem selectedItem = inventoryTableView.getSelectionModel().getSelectedItem();

        if (selectedIndex >= 0) {

            ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_DELETE + selectedItem.getName() + "?");

            if (selectedButton.equals(ButtonType.OK)) {

                try {
                    inventoryTableView.getItems().remove(selectedIndex);
                    inventoryTableView.getSelectionModel().clearSelection();
                    inventoryItemsObservableList.remove(selectedItem);
                } catch (Exception e) {
                    displayGeneralDialog(GeneralUtils.UI_TITLE, GeneralUtils.UI_HEADER, "", Alert.AlertType.ERROR);
                }

                try {
                    DatabaseUtils.deleteEmployee(selectedItem.getId());
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.DELETED, "", 2);

                } catch (DatabaseException e) {
                    displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
                }

                ChangedData<InventoryItem, Employee> changedData = new ChangedData<>(selectedItem, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), ChangeType.DELETE, LocalDateTime.now());

                try {
                    dataMap = FileUtils.deserializeData(InventoryItem.class);
                } catch (DeserializationException e) {
                    GeneralUtils.logger.error("Unable to deserialize. Inventory data is not available.");
                }

                dataMap.put(selectedItem, changedData);
                FileUtils.serializeData(dataMap, InventoryItem.class);
            }
        } else {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, "Select an inventory item to delete.", Alert.AlertType.INFORMATION);
        }
    }

}
