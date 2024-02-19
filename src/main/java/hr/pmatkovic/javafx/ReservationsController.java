package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.entities.service.Customer;
import hr.pmatkovic.entities.service.Reservation;
import hr.pmatkovic.entities.service.ReservationType;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DatabaseException;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.threads.GetReservationTask;
import hr.pmatkovic.threads.ModifyReservationTask;
import hr.pmatkovic.threads.ReservationQueryService;
import hr.pmatkovic.utils.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.DB_TEXT;
import static hr.pmatkovic.utils.GeneralUtils.DATE_TIME_FORMATTER;
import static hr.pmatkovic.utils.ValidationUtils.*;

/**
 * Reservations controller
 */

public class ReservationsController {

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
    private TextField addNameTextField;
    @FXML
    private TextField addSurnameTextField;
    @FXML
    private TextField addEmailTextField;
    @FXML
    private ComboBox<Boolean> addRegularCustomerComboBox;
    @FXML
    private ComboBox<Boolean> addVipCustomerComboBox;
    @FXML
    private ComboBox<ReservationType> addReservationTypeComboBox;
    @FXML
    private TextField addNumberOfGuestsTextField;
    @FXML
    private CustomDateTimePicker addReservationDateTimePicker;
    
    @FXML
    private TableView<Reservation> reservationsTableView;
    @FXML
    private TableColumn<Reservation, String> nameTableColumn;
    @FXML
    private TableColumn<Reservation, String> surnameTableColumn;
    @FXML
    private TableColumn<Reservation, String> emailTableColumn;
    @FXML
    private TableColumn<Reservation, String> regularCustomerTableColumn;
    @FXML
    private TableColumn<Reservation, String> vipCustomerTableColumn;
    @FXML
    private TableColumn<Reservation, String> reservationTypeTableColumn;
    @FXML
    private TableColumn<Reservation, String> numberOfGuestsTableColumn;
    @FXML
    private TableColumn<Reservation, String> reservationDateTimeTableColumn;

    @FXML
    private GridPane inputFieldsGridPane;
    @FXML
    private Button button;
    @FXML
    private Label modelUpdateLabel;
    @FXML
    private Label newEntryLabel;

    private List<Reservation> reservations;
    private List<Reservation> filteredReservations;
    private List<Customer> customers;
    private List<Customer> filteredCustomers;

    private ObservableList<Reservation> reservationsObservableList;
    private Map<Reservation, ChangedData<Reservation, Employee>> dataMap = new HashMap<>();

    public void initialize() {

        try {
            reservations = DatabaseUtils.getReservations(null);
            customers = DatabaseUtils.getCustomers(null);

        } catch (DatabaseException e) {

            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }

        reservationsObservableList = FXCollections.observableArrayList(reservations);

        reservationTypeTableColumn.setCellFactory(StyleCellValue::new);

        setColumnValueFactory(nameTableColumn, r -> r.getCustomer().getName());
        setColumnValueFactory(surnameTableColumn, r -> r.getCustomer().getSurname());
        setColumnValueFactory(emailTableColumn, r -> r.getCustomer().getEmail());
        setColumnValueFactory(regularCustomerTableColumn, r -> r.getCustomer().getRegularCustomer().toString());
        setColumnValueFactory(vipCustomerTableColumn, r -> r.getCustomer().getVipCustomer().toString());
        setColumnValueFactory(reservationTypeTableColumn, r -> r.getReservationType().toString());
        setColumnValueFactory(numberOfGuestsTableColumn, r -> r.getNumberOfGuests().toString());
        setColumnValueFactory(reservationDateTimeTableColumn, r -> r.getReservationDateTime().format(DATE_TIME_FORMATTER));

        reservationsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                addNameTextField.setText(newVal.getCustomer().getName());
                addSurnameTextField.setText(newVal.getCustomer().getSurname());
                addEmailTextField.setText(newVal.getCustomer().getEmail());
                addRegularCustomerComboBox.getSelectionModel().select(newVal.getCustomer().getRegularCustomer());
                addVipCustomerComboBox.getSelectionModel().select(newVal.getCustomer().getVipCustomer());
                addReservationTypeComboBox.getSelectionModel().select(newVal.getReservationType());
                addNumberOfGuestsTextField.setText(newVal.getNumberOfGuests().toString());
                addReservationDateTimePicker.setDateTimeValue(newVal.getReservationDateTime());
                button.setText("Edit");
            }
            else {
                ControllerUtils.clearInputFields(inputFieldsGridPane);
                button.setText("Add");
            }
        });

        numberOfGuestsTableColumn.setComparator(new NumbersComparator());
        reservationDateTimeTableColumn.setComparator(new DateTimeComparator());

        clearRowSelectionIfSelected(reservationsTableView, inputFieldsGridPane, button);
        clearRowSelectionIfOutside(Main.getMainStage().getScene(), reservationsTableView, inputFieldsGridPane, button);

        setTextComboBox(regularCustomerComboBox);
        setTextComboBox(vipCustomerComboBox);
        setTextComboBox(reservationTypeComboBox);
        setTextComboBox(addRegularCustomerComboBox);
        setTextComboBox(addVipCustomerComboBox);
        setTextComboBox(addReservationTypeComboBox);
        clearSelectionComboBox(regularCustomerComboBox);
        clearSelectionComboBox(vipCustomerComboBox);
        clearSelectionComboBox(reservationTypeComboBox);
        clearSelectionComboBox(addRegularCustomerComboBox);
        clearSelectionComboBox(addVipCustomerComboBox);
        clearSelectionComboBox(addReservationTypeComboBox);

        regularCustomerComboBox.setItems(FXCollections.observableArrayList(List.of(true, false)));
        vipCustomerComboBox.setItems(FXCollections.observableArrayList(List.of(true, false)));
        reservationTypeComboBox.setItems(FXCollections.observableArrayList(ReservationType.getListOfEnumValues()));
        addRegularCustomerComboBox.setItems(FXCollections.observableArrayList(List.of(true, false)));
        addVipCustomerComboBox.setItems(FXCollections.observableArrayList(List.of(true, false)));
        addReservationTypeComboBox.setItems(FXCollections.observableArrayList(ReservationType.getListOfEnumValues()));

        addReservationTypeComboBox.setValue(ReservationType.LOCAL);
        addReservationTypeComboBox.setDisable(true);

        button.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Add")) {
                addReservationTypeComboBox.setValue(ReservationType.LOCAL);
                addReservationTypeComboBox.setDisable(true);
            } else if (newValue.equals("Edit")) {
                addReservationTypeComboBox.setItems(FXCollections.observableArrayList(ReservationType.getListOfEnumValues()));
                addReservationTypeComboBox.setDisable(false);
            }
        });

        startReservationQueryService();

        reservationsTableView.setItems(reservationsObservableList);

    }

    public void filterReservations() {

        String name = nameTextField.getText();
        String surname = surnameTextField.getText();
        String email = emailTextField.getText();
        Boolean regularCustomer = regularCustomerComboBox.getSelectionModel().getSelectedItem();
        Boolean vipCustomer = vipCustomerComboBox.getSelectionModel().getSelectedItem();
        ReservationType reservationType = reservationTypeComboBox.getSelectionModel().getSelectedItem();
        String numberOfGuests = numberOfGuestsTextField.getText();
        LocalDateTime reservationDateTime = reservationDateTimePicker.getValue() == null ? null : reservationDateTimePicker.getDateTimeValue();

        Optional<String> numericInput = checkIfNumericInput(Map.of("Number of guests", numberOfGuests));
        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String inputErrors = Stream.of(numericInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (numericInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, inputErrors, Alert.AlertType.ERROR);
            return;
        }

        Map <List<Customer>, Reservation> mapOfCustomers = new HashMap<>();

        try {

            boolean nullReservation = ValidationUtils.isEveryObjectNull(name, surname, email, regularCustomer, vipCustomer, reservationType, numberOfGuests, reservationDateTime);

            if (nullReservation) {

                GetReservationTask modifyTask = new GetReservationTask();
                modifyTask.valueProperty().addListener((observableValue, oldValue, newValue) -> filteredReservations = newValue);

                Thread thread = new Thread(modifyTask);
                thread.setDaemon(true);
                thread.start();

//                filteredReservations = DatabaseUtils.getReservations(null);
            }
            else {

                Customer customer = new Customer(0L, name, surname, email, regularCustomer, vipCustomer);

                filteredCustomers = DatabaseUtils.getCustomers(customer);

                mapOfCustomers.put(filteredCustomers, new Reservation(0L, customer, reservationType, ValidationUtils.validateNumber(numberOfGuests, Integer.class), reservationDateTime));

                filteredReservations = DatabaseUtils.getReservationsFromCustomers(mapOfCustomers);
            }

        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }

        reservationsTableView.setItems(FXCollections.observableArrayList(filteredReservations));
    }

    public void updateReservation() {

        String name = addNameTextField.getText();
        String surname = addSurnameTextField.getText();
        String email = addEmailTextField.getText();
        Boolean regularCustomer = addRegularCustomerComboBox.getSelectionModel().getSelectedItem();
        Boolean vipCustomer = addVipCustomerComboBox.getSelectionModel().getSelectedItem();
        ReservationType reservationType = addReservationTypeComboBox.getSelectionModel().getSelectedItem();
        String numberOfGuests = addNumberOfGuestsTextField.getText();
        LocalDateTime reservationDateTime = addReservationDateTimePicker.getValue() == null ? null : addReservationDateTimePicker.getDateTimeValue();

        Optional<String> emptyInput = checkIfEmptyInput(Map.of("Name", name, "Surname", surname, "E-mail", email, "Regular customer", Optional.ofNullable(regularCustomer), "VIP customer", Optional.ofNullable(vipCustomer), "Reservation type", Optional.ofNullable(reservationType), "Number of guests", numberOfGuests, "Reservation date & time", Optional.ofNullable(reservationDateTime)));

        Optional<String> numericInput = checkIfNumericInput(Map.of("Number of guests", numberOfGuests));
        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String errors = Stream.of(emptyInput, numericInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (emptyInput.isPresent() || numericInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, errors, Alert.AlertType.ERROR);
            return;
        }

        Reservation selectedItem = reservationsTableView.getSelectionModel().getSelectedItem();
        Reservation reservation = new Reservation(0L, new Customer(0L, name, surname, email, regularCustomer, vipCustomer), reservationType, ValidationUtils.validateNumber(numberOfGuests, Integer.class), reservationDateTime);


        ChangeType change = button.getText().equals("Add") ? ChangeType.ADD : ChangeType.EDIT;

        try {
            switch (change) {
                case ADD -> {

                    System.out.println(reservationsObservableList);

                    ModifyReservationTask modifyTask = new ModifyReservationTask(reservation, ChangeType.ADD);
                    Thread thread = new Thread(modifyTask);
                    thread.setDaemon(true);
                    thread.start();
//                    DatabaseUtils.addReservation(reservation);
                    reservationsObservableList.add(reservation);
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.ADDED, "", 2);
                    System.out.println(reservationsObservableList);
                }
                case EDIT -> {

                    reservation.setId(selectedItem.getId());
                    (reservation.getCustomer()).setId(selectedItem.getCustomer().getId());

                    if (reservation.equals(selectedItem)) {
                        displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, GeneralUtils.INPUT_NO_CHANGE, Alert.AlertType.INFORMATION);
                        return;
                    } else {
                        ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_CHANGE + "data for " + selectedItem.getCustomer() + "?");
                        if (selectedButton.equals(ButtonType.OK)) {

                            ModifyReservationTask modifyTask = new ModifyReservationTask(reservation, ChangeType.EDIT);
                            Thread thread = new Thread(modifyTask);
                            thread.setDaemon(true);
                            thread.start();

//                            DatabaseUtils.editReservation(reservation);
                            reservationsObservableList.replaceAll(i -> (i.getId().equals(selectedItem.getId()) ? reservation : i));
                            ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.CHANGED, "", 2);
                        }
                    }
                }
            }
        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }

        ChangedData<Reservation, Employee> changedData = new ChangedData<>(reservation, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), change, LocalDateTime.now());

        try {
            dataMap = FileUtils.deserializeData(Reservation.class);
        } catch (DeserializationException e) {
            GeneralUtils.logger.error("Unable to deserialize. Reservation data is not available.");
        }

        dataMap.put(selectedItem, changedData);

        FileUtils.serializeData(dataMap, Reservation.class);

    }

    public void deleteReservation() {

        int selectedIndex = reservationsTableView.getSelectionModel().getSelectedIndex();
        Reservation selectedItem = reservationsTableView.getSelectionModel().getSelectedItem();

        if (selectedIndex >= 0) {

            ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_DELETE + selectedItem.getCustomer() + "?");

            if (selectedButton.equals(ButtonType.OK)) {

                try {
                    reservationsTableView.getItems().remove(selectedIndex);
                    reservationsTableView.getSelectionModel().clearSelection();
                    reservationsObservableList.remove(selectedItem);
                } catch (Exception e) {
                    displayGeneralDialog(GeneralUtils.UI_TITLE, GeneralUtils.UI_HEADER, "", Alert.AlertType.ERROR);
                }

                try {

                    ModifyReservationTask modifyTask = new ModifyReservationTask(selectedItem, ChangeType.DELETE);
                    Thread thread = new Thread(modifyTask);
                    thread.setDaemon(true);
                    thread.start();

//                    DatabaseUtils.deleteReservation(selectedItem);
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.DELETED, "", 2);
                } catch (DatabaseException e) {
                    displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
                }

                ChangedData<Reservation, Employee> changedData = new ChangedData<>(selectedItem, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), ChangeType.DELETE, LocalDateTime.now());

                try {
                    dataMap = FileUtils.deserializeData(Reservation.class);
                } catch (DeserializationException e) {
                    GeneralUtils.logger.error("Unable to deserialize. Reservation data is not available.");
                }

                dataMap.put(selectedItem, changedData);
                FileUtils.serializeData(dataMap, Reservation.class);
            }
        } else {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, "Select a reservation to delete.", Alert.AlertType.INFORMATION);
        }
    }

    private void startReservationQueryService() {

        newEntryLabel.setText("Waiting for reservations..");
        modelUpdateLabel.setAlignment(Pos.BASELINE_RIGHT);

        ReservationQueryService service = new ReservationQueryService();

        service.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            reservationsObservableList.add(newValue);
            displayChangeLabel(newEntryLabel, "New reservation received!", "Waiting for reservations..", 4);
        });

        service.setPeriod(Duration.seconds(2));
        service.start();

    }
}
