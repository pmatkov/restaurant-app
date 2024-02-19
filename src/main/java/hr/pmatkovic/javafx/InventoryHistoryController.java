package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.entities.inventory.Category;
import hr.pmatkovic.entities.inventory.Food;
import hr.pmatkovic.entities.inventory.InventoryItem;
import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.utils.FileUtils;
import hr.pmatkovic.utils.StyleCellValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.*;

/**
 * Inventory history controller
 */

public class InventoryHistoryController {


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
    private TextField changeTypeTextField;
    @FXML
    private TextField changedByTextField;
    @FXML
    private DatePicker timeOfChangeDatePicker;

    @FXML
    private TextField nameCurrentTextField;
    @FXML
    private TextField categoryCurrentTextField;
    @FXML
    private TextField quantityCurrentTextField;
    @FXML
    private TextField pricePerItemCurrentTextField;
    @FXML
    private TextField bestBeforeCurrentTextField;
    @FXML
    private TextField purhcaseDateCurrentTextField;

    @FXML
    private TextField nameOldTextField;
    @FXML
    private TextField categoryOldTextField;
    @FXML
    private TextField quantityeOldTextField;
    @FXML
    private TextField pricePerItemOldTextField;
    @FXML
    private TextField bestBeforeOldTextField;
    @FXML
    private TextField purhcaseDateOldTextField;

    @FXML
    private GridPane historyFieldsGridPane;

    @FXML
    private TableView<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>> inventoryHistoryTableView;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> nameTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> categoryTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> quantityTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> pricePerItemTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> bestBeforeTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> purchaseDateTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> changeTypeTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> changedByTableColumn;
    @FXML
    private TableColumn<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>, String> timeOfChangeTableColumn;

    private Map<InventoryItem, ChangedData<InventoryItem, Employee>> dataMap;

    public void initialize() {

        changeTypeTableColumn.setCellFactory(StyleCellValue::new);

        setColumnValueFactory(nameTableColumn, e -> e.getValue().getChangedObject().getName());
        setColumnValueFactory(categoryTableColumn, e -> e.getValue().getChangedObject().getCategory().toString());
        setColumnValueFactory(quantityTableColumn, e -> e.getValue().getChangedObject().getQuantity().toString());
        setColumnValueFactory(pricePerItemTableColumn, e -> e.getValue().getChangedObject().getPricePerItem().toString());

        bestBeforeTableColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getKey() instanceof Food) {
                return new SimpleStringProperty(((Food)cellData.getValue().getKey()).getBestBefore().toString());
            }
            else
                return new SimpleStringProperty("0");
        });

        setColumnValueFactory(purchaseDateTableColumn, e -> e.getValue().getChangedObject().getPurchaseDate().format(DATE_FORMATTER));
        setColumnValueFactory(changedByTableColumn, e -> e.getValue().getChangedBy().toString());
        setColumnValueFactory(changeTypeTableColumn, e -> e.getValue().getChangeType().getString());
        setColumnValueFactory(timeOfChangeTableColumn, e -> e.getValue().getTimeOfChange().format(DATE_TIME_FORMATTER));


        setDefaultColumnValue(bestBeforeTableColumn);

        inventoryHistoryTableView.setSortPolicy(tv -> {
            Comparator<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>> comparator = new HistoricDataSorter();
            FXCollections.sort(tv.getItems(), comparator);
            return true;
        });
        inventoryHistoryTableView.getSortOrder().add(timeOfChangeTableColumn);
        inventoryHistoryTableView.sort();

        inventoryHistoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DecimalFormat df = new DecimalFormat("###.##");
                nameCurrentTextField.setText(newVal.getValue().getChangedObject().getName());
                categoryCurrentTextField.setText(newVal.getValue().getChangedObject().getCategory().toString());
                quantityCurrentTextField.setText(newVal.getValue().getChangedObject().getQuantity().toString());
                pricePerItemCurrentTextField.setText(newVal.getValue().getChangedObject().getPricePerItem().toString());
                if (newVal.getValue().getChangedObject() instanceof Food) {
                    bestBeforeCurrentTextField.setText(((Food)newVal.getValue().getChangedObject()).getBestBefore().toString());
                }
                else {
                    bestBeforeCurrentTextField.setText("Not available");
                }
                purhcaseDateCurrentTextField.setText(newVal.getValue().getChangedObject().getPurchaseDate().format(DATE_FORMATTER));

                if (newVal.getValue().getChangeType().equals(ChangeType.EDIT)) {
                    nameOldTextField.setText(newVal.getKey().getName());
                    categoryOldTextField.setText(newVal.getKey().getCategory().toString());
                    quantityeOldTextField.setText(newVal.getKey().getQuantity().toString());
                    pricePerItemOldTextField.setText(newVal.getKey().getPricePerItem().toString());
                    if (newVal.getValue().getChangedObject() instanceof Food) {
                        bestBeforeOldTextField.setText(((Food)newVal.getValue().getChangedObject()).getBestBefore().toString());
                    }
                    else {
                        bestBeforeOldTextField.setText("Not available");
                    }
                    purhcaseDateOldTextField.setText(newVal.getValue().getChangedObject().getPurchaseDate().format(DATE_FORMATTER));
                }
                else {
                    clearTextFields(historyFieldsGridPane);
                }
            }
        });

        compareTextFields(nameCurrentTextField, nameOldTextField);
        compareTextFields(categoryCurrentTextField, categoryOldTextField);
        compareTextFields(quantityCurrentTextField, quantityeOldTextField);
        compareTextFields(pricePerItemCurrentTextField, pricePerItemOldTextField);
        compareTextFields(bestBeforeCurrentTextField, bestBeforeOldTextField);
        compareTextFields(purhcaseDateCurrentTextField, purhcaseDateOldTextField);

        ObservableList<Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>>> list = FXCollections.observableArrayList();;

        try {
            dataMap = FileUtils.deserializeData(InventoryItem.class);
            for (Map.Entry<InventoryItem, ChangedData<InventoryItem, Employee>> entry : dataMap.entrySet()) {
                list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }

        } catch (DeserializationException e) {
            displayGeneralDialog(HISTORY_TITLE, HISTORY_HEADER, "Inventory history is not available.", Alert.AlertType.INFORMATION);
            logger.info("Unable to deserialize. Inventory history is not available.");

        }

        setTextComboBox(categoryComboBox);
        clearSelectionComboBox(categoryComboBox);

        categoryComboBox.setItems(FXCollections.observableArrayList(Category.getListOfEnumValues()));
        categoryComboBox.setItems(FXCollections.observableArrayList(Category.getListOfEnumValues()));

        inventoryHistoryTableView.setItems(list);

    }
}
