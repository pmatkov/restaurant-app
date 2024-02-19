package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.entities.inventory.Category;
import hr.pmatkovic.entities.inventory.Food;
import hr.pmatkovic.entities.inventory.InventoryItem;
import hr.pmatkovic.entities.service.Reservation;
import hr.pmatkovic.entities.service.ReservationType;
import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.utils.CustomDateTimePicker;
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
import java.util.List;
import java.util.Map;

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.*;

/**
 * Reservation history controller
 */

public class ReservationHistoryController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private ComboBox<Boolean> regularCustomerComboBox;
    @FXML
    private ComboBox<Boolean> vipCustomerComboBox;
    @FXML
    private ComboBox<ReservationType> reservationTypeComboBox;
    @FXML
    private TextField numberOfGuestsTextField;
    @FXML
    private CustomDateTimePicker reservationDateTimePicker;
    @FXML
    private TextField changeTypeTextField;
    @FXML
    private TextField changedByTextField;
    @FXML
    private DatePicker timeOfChangeDatePicker;

    @FXML
    private TextField nameCurrentTextField;
    @FXML
    private TextField surnameCurrentTextField;
    @FXML
    private TextField emailCurrentTextField;
    @FXML
    private TextField regularCurrentTextField;
    @FXML
    private TextField vipCurrentTextField;
    @FXML
    private TextField reservationTypeCurrentTextField;
    @FXML
    private TextField numberOfGuestsCurrentTextField;
    @FXML
    private TextField reservationDateCurrentTextField;
    
    @FXML
    private TextField nameOldTextField;
    @FXML
    private TextField surnameOldTextField;
    @FXML
    private TextField emailOldTextField;
    @FXML
    private TextField regularOldTextField;
    @FXML
    private TextField vipOldTextField;
    @FXML
    private TextField reservationTypeOldTextField;
    @FXML
    private TextField numberOfGuestsOldTextField;
    @FXML
    private TextField reservationDateOldTextField;

    @FXML
    private GridPane historyFieldsGridPane;

    @FXML
    private TableView<Map.Entry<Reservation, ChangedData<Reservation, Employee>>> reservationHistoryTableView;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> nameTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> surnameTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> emailTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> regularTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> vipTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> reservationTypeTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> numberOfGuestsTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> reservationDateTimeTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> changeTypeTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> changedByTableColumn;
    @FXML
    private TableColumn<Map.Entry<Reservation, ChangedData<Reservation, Employee>>, String> timeOfChangeTableColumn;

    private Map<Reservation, ChangedData<Reservation, Employee>> dataMap;

    public void initialize() {

        changeTypeTableColumn.setCellFactory(StyleCellValue::new);

        setColumnValueFactory(nameTableColumn, e -> e.getValue().getChangedObject().getCustomer().getName());
        setColumnValueFactory(surnameTableColumn, e -> e.getValue().getChangedObject().getCustomer().getSurname());
        setColumnValueFactory(emailTableColumn, e -> e.getValue().getChangedObject().getCustomer().getEmail());
        setColumnValueFactory(regularTableColumn, e -> e.getValue().getChangedObject().getCustomer().getRegularCustomer().toString());
        setColumnValueFactory(vipTableColumn, e -> e.getValue().getChangedObject().getCustomer().getVipCustomer().toString());
        setColumnValueFactory(reservationTypeTableColumn, e -> e.getValue().getChangedObject().getReservationType().toString());        
        setColumnValueFactory(numberOfGuestsTableColumn, e -> e.getValue().getChangedObject().getNumberOfGuests().toString());
        setColumnValueFactory(reservationDateTimeTableColumn, e -> e.getValue().getChangedObject().getReservationDateTime().format(DATE_TIME_FORMATTER));
        setColumnValueFactory(changedByTableColumn, e -> e.getValue().getChangedBy().toString());
        setColumnValueFactory(changeTypeTableColumn, e -> e.getValue().getChangeType().getString());
        setColumnValueFactory(timeOfChangeTableColumn, e -> e.getValue().getTimeOfChange().format(DATE_TIME_FORMATTER));
        

        reservationHistoryTableView.setSortPolicy(tv -> {
            Comparator<Map.Entry<Reservation, ChangedData<Reservation, Employee>>> comparator = new HistoricDataSorter();
            FXCollections.sort(tv.getItems(), comparator);
            return true;
        });
        reservationHistoryTableView.getSortOrder().add(timeOfChangeTableColumn);
        reservationHistoryTableView.sort();

        reservationHistoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameCurrentTextField.setText(newVal.getValue().getChangedObject().getCustomer().getName());
                surnameCurrentTextField.setText(newVal.getValue().getChangedObject().getCustomer().getSurname());
                emailCurrentTextField.setText(newVal.getValue().getChangedObject().getCustomer().getEmail());
                regularCurrentTextField.setText(newVal.getValue().getChangedObject().getCustomer().getRegularCustomer().toString());
                vipCurrentTextField.setText(newVal.getValue().getChangedObject().getCustomer().getVipCustomer().toString());
                reservationTypeCurrentTextField.setText(newVal.getValue().getChangedObject().getReservationType().toString());
                numberOfGuestsCurrentTextField.setText(newVal.getValue().getChangedObject().getNumberOfGuests().toString());
                reservationDateCurrentTextField.setText(newVal.getValue().getChangedObject().getReservationDateTime().format(DATE_FORMATTER));

                if (newVal.getValue().getChangeType().equals(ChangeType.EDIT)) {
                    nameOldTextField.setText(newVal.getKey().getCustomer().getName());
                    surnameOldTextField.setText(newVal.getKey().getCustomer().getSurname());
                    emailOldTextField.setText(newVal.getKey().getCustomer().getEmail());
                    regularOldTextField.setText(newVal.getKey().getCustomer().getRegularCustomer().toString());
                    vipOldTextField.setText(newVal.getKey().getCustomer().getVipCustomer().toString());
                    reservationTypeOldTextField.setText(newVal.getKey().getReservationType().toString());
                    numberOfGuestsOldTextField.setText(newVal.getKey().getNumberOfGuests().toString());
                    reservationDateOldTextField.setText(newVal.getKey().getReservationDateTime().format(DATE_FORMATTER));
                }
                else {
                    clearTextFields(historyFieldsGridPane);
                }
            }
        });

        compareTextFields(nameCurrentTextField, nameOldTextField);
        compareTextFields(surnameCurrentTextField, surnameOldTextField);
        compareTextFields(emailCurrentTextField, emailOldTextField);
        compareTextFields(regularCurrentTextField, regularOldTextField);
        compareTextFields(vipCurrentTextField, vipOldTextField);
        compareTextFields(reservationTypeCurrentTextField, reservationTypeOldTextField);
        compareTextFields(numberOfGuestsCurrentTextField, numberOfGuestsOldTextField);
        compareTextFields(reservationDateCurrentTextField, reservationDateOldTextField);

        ObservableList<Map.Entry<Reservation, ChangedData<Reservation, Employee>>> list = FXCollections.observableArrayList();;

        try {
            dataMap = FileUtils.deserializeData(Reservation.class);
            for (Map.Entry<Reservation, ChangedData<Reservation, Employee>> entry : dataMap.entrySet()) {
                list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }

        } catch (DeserializationException e) {
            displayGeneralDialog(HISTORY_TITLE, HISTORY_HEADER, "Reservation history is not available.", Alert.AlertType.INFORMATION);
            logger.info("Unable to deserialize. Reservation history is not available.");

        }

        setTextComboBox(regularCustomerComboBox);
        setTextComboBox(vipCustomerComboBox);
        setTextComboBox(reservationTypeComboBox);
        clearSelectionComboBox(regularCustomerComboBox);
        clearSelectionComboBox(vipCustomerComboBox);
        clearSelectionComboBox(reservationTypeComboBox);

        regularCustomerComboBox.setItems(FXCollections.observableArrayList(List.of(true, false)));
        vipCustomerComboBox.setItems(FXCollections.observableArrayList(List.of(true, false)));
        reservationTypeComboBox.setItems(FXCollections.observableArrayList(ReservationType.getListOfEnumValues()));

        reservationHistoryTableView.setItems(list);


    }

}
