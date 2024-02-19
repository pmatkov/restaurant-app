package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.entities.general.DepartmentName;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.*;

import hr.pmatkovic.threads.BestPerformingEmployeeThread;
import hr.pmatkovic.threads.PerformanceStatusThread;
import hr.pmatkovic.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hr.pmatkovic.entities.staff.Employee.createEmployee;
import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.ValidationUtils.*;

/**
 * Employee controller
 */

public class EmployeeController {

    @FXML
    private TextField employeePinTextField;
    @FXML
    private TextField employeeNameTextField;
    @FXML
    private TextField employeeSurnameTextField;
    @FXML
    private DatePicker employeeBirthdateDatePicker;
    @FXML
    private ComboBox<Position> positionComboBox;
    @FXML
    private ComboBox<Rank> rankComboBox;
    @FXML
    private TextField employeeSalaryTextField;
    @FXML
    private TextField employeeBonusTextField;
    @FXML
    private TextField employeePerformanceTextField;
    @FXML
    private TextField employeeExperienceTextField;

    @FXML
    private TextField addEmployeePinTextField;
    @FXML
    private TextField addEmployeeNameTextField;
    @FXML
    private TextField addEmployeeSurnameTextField;
    @FXML
    private DatePicker addEmployeeBirthdateDatePicker;
    @FXML
    private ComboBox<Position> addEmployeePositionComboBox;
    @FXML
    private ComboBox<Rank> addEmployeeRankComboBox;
    @FXML
    private TextField addEmployeeSalaryTextField;
    @FXML
    private TextField addEmployeeBonusTextField;
    @FXML
    private TextField addEmployeePerformanceTextField;
    @FXML
    private TextField addEmployeeExperienceTextField;

    @FXML
    private TableView<Employee> employeesTableView;
    @FXML
    private TableColumn<Employee, String> employeePinTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeNameTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeSurnameTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeBirthdateTableColumn;
    @FXML
    private TableColumn<Employee, String> employeePositionTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeRankTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeSalaryTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeBonusTableColumn;
    @FXML
    private TableColumn<Employee, String> employeePerformanceTableColumn;
    @FXML
    private TableColumn<Employee, String> employeeExperienceTableColumn;

    @FXML
    private Pane performancePane;
    @FXML
    private GridPane inputFieldsGridPane;

    @FXML
    private Button button;
    @FXML
    private ToggleButton performanceToggleButton;
    @FXML
    private Label performanceChangeLabel;
    @FXML
    private Label bestEmployeeLabel;
    @FXML
    private Label modelUpdateLabel;

    private List<Employee> employees;
    private List<Employee> filteredEmployees;

    private ObservableList<Employee> employeesObservableList;
    private Map<Employee, ChangedData<Employee, Employee>> dataMap = new HashMap<>();

    public void initialize() {

        try {
            employees = DatabaseUtils.getEmployees(null);

            Employee.setEmployeesBonus(employees);
            DatabaseUtils.updateEmployeeBonus(employees);

        } catch (DatabaseException e) {

            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }

        employeesObservableList = FXCollections.observableArrayList(employees);

        setColumnValueFactory(employeeNameTableColumn, Employee::getName);
        setColumnValueFactory(employeeSurnameTableColumn, Employee::getSurname);
        setColumnValueFactory(employeePinTableColumn, e -> e.getPin().toString());
        setColumnValueFactory(employeeBirthdateTableColumn, e -> e.getBirthdate().format(GeneralUtils.DATE_FORMATTER));
        setColumnValueFactory(employeePositionTableColumn, e -> e.getPosition().toString());
        setColumnValueFactory(employeeRankTableColumn, e -> e.getRank().toString());
        setColumnValueFactory(employeeSalaryTableColumn, e -> e.getSalary().toString());
        setColumnValueFactory(employeeBonusTableColumn, e -> e.getBonus().toString());
        setColumnValueFactory(employeeExperienceTableColumn, e -> e.getExperience().toString());

        employeePerformanceTableColumn.setCellValueFactory(cellData -> {
            Double performance = cellData.getValue().getPerformance();
            DecimalFormat df = new DecimalFormat("###.##");
            return new SimpleObjectProperty<>(df.format(performance));
        });

        setDefaultColumnValue(employeePerformanceTableColumn);

        employeesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DecimalFormat df = new DecimalFormat("###.##");
                addEmployeePinTextField.setText(newVal.getPin().toString());
                addEmployeeNameTextField.setText(newVal.getName());
                addEmployeeSurnameTextField.setText(newVal.getSurname());
                addEmployeeBirthdateDatePicker.setValue(newVal.getBirthdate());
                addEmployeePositionComboBox.getSelectionModel().select(newVal.getPosition());
                addEmployeeRankComboBox.getSelectionModel().select(newVal.getRank());
                addEmployeeSalaryTextField.setText(newVal.getSalary().toString());
                addEmployeeBonusTextField.setText(newVal.getBonus().toString());
                addEmployeePerformanceTextField.setText(df.format(newVal.getPerformance()));
                addEmployeeExperienceTextField.setText(newVal.getExperience().toString());

                button.setText("Edit");
            }

            else {

                ControllerUtils.clearInputFields(inputFieldsGridPane);
                button.setText("Add");
            }
        });

        employeeBirthdateTableColumn.setComparator(new DatesComparator());
        employeeSalaryTableColumn.setComparator(new NumbersComparator());
        employeeBonusTableColumn.setComparator(new NumbersComparator());
        employeePerformanceTableColumn.setComparator(new NumbersComparator());
        employeePerformanceTableColumn.setComparator(new NumbersComparator());

        clearRowSelectionIfSelected(employeesTableView, inputFieldsGridPane, button);
        clearRowSelectionIfOutside(Main.getMainStage().getScene(), employeesTableView, inputFieldsGridPane, button);

        setTextComboBox(positionComboBox);
        setTextComboBox(rankComboBox);
        setTextComboBox(addEmployeePositionComboBox);
        setTextComboBox(addEmployeeRankComboBox);
        clearSelectionComboBox(positionComboBox);
        clearSelectionComboBox(rankComboBox);
        clearSelectionComboBox(addEmployeePositionComboBox);
        clearSelectionComboBox(addEmployeeRankComboBox);

        positionComboBox.setItems(FXCollections.observableArrayList(Position.getListOfEnumValues()));
        rankComboBox.setItems(FXCollections.observableArrayList(Rank.getListOfEnumValues()));
        addEmployeePositionComboBox.setItems(FXCollections.observableArrayList(Position.getListOfEnumValues()));
        addEmployeeRankComboBox.setItems(FXCollections.observableArrayList(Rank.getListOfEnumValues()));
        performancePane.setVisible(false);

        setDateConverter(employeeBirthdateDatePicker);
        setDateConverter(addEmployeeBirthdateDatePicker);

        employeesTableView.setItems(employeesObservableList);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame keyframe1 = GeneralUtils.setNewKeyFrame(javafx.util.Duration.seconds(5), new PerformanceStatusThread(employeesTableView, employeesObservableList, performanceChangeLabel));
        KeyFrame keyframe2 = GeneralUtils.setNewKeyFrame(javafx.util.Duration.seconds(10), new BestPerformingEmployeeThread(bestEmployeeLabel));
        timeline.getKeyFrames().addAll(keyframe1, keyframe2);

        performanceToggleButton.setOnAction(event -> {
            if (performanceToggleButton.isSelected()) {
                timeline.play();
                performancePane.setVisible(true);
            } else {
                timeline.pause();
                performancePane.setVisible(false);
            }
        });
    }

    public void filterEmployees() {

        String pin = employeePinTextField.getText();
        String name = employeeNameTextField.getText();
        String surname = employeeSurnameTextField.getText();
        LocalDate birthdate = employeeBirthdateDatePicker.getValue();
        Position position = positionComboBox.getSelectionModel().getSelectedItem();
        Rank rank = rankComboBox.getSelectionModel().getSelectedItem();
        String salary = employeeSalaryTextField.getText();
        String experience = employeeExperienceTextField.getText();

        Optional<String> numericInput = checkIfNumericInput(Map.of("Salary", salary, "Experience", experience));
        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String inputErrors = Stream.of(numericInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (numericInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, inputErrors, Alert.AlertType.ERROR);
            return;
        }
        DepartmentName department = DepartmentName.convertPositionToDepartment(position);

        Employee employee = Employee.createEmployee(0L, name, surname, ValidationUtils.validateNumber(pin, Long.class), birthdate, department, position, rank, ValidationUtils.validateNumber(salary, Double.class), ValidationUtils.validateNumber(experience, Integer.class));

        try {
            filteredEmployees = DatabaseUtils.getEmployees(employee);

        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }

        employeesTableView.setItems(FXCollections.observableArrayList(filteredEmployees));

    }

    public void updateEmployee() {

        String pin = addEmployeePinTextField.getText();
        String name = addEmployeeNameTextField.getText();
        String surname = addEmployeeSurnameTextField.getText();
        LocalDate birthdate = addEmployeeBirthdateDatePicker.getValue();
        Position position = addEmployeePositionComboBox.getSelectionModel().getSelectedItem();
        Rank rank = addEmployeeRankComboBox.getSelectionModel().getSelectedItem();
        String salary = addEmployeeSalaryTextField.getText();
        String experience = addEmployeeExperienceTextField.getText();

        Optional<String> emptyInput = checkIfEmptyInput(Map.of("Pin", pin, "Name", name, "Surname", surname, "Birthdate", Optional.ofNullable(birthdate), "Position", Optional.ofNullable(position), "Rank", Optional.ofNullable(rank), "Salary", salary, "Experience", experience));
        Optional<String> numericInput = checkIfNumericInput(Map.of("Salary", salary, "Experience", experience));
        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String errors = Stream.of(emptyInput, numericInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (emptyInput.isPresent() || numericInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, errors, Alert.AlertType.ERROR);
            return;
        }

        DepartmentName department = DepartmentName.convertPositionToDepartment(position);

        Employee selectedEmployee = employeesTableView.getSelectionModel().getSelectedItem();
        Employee employee = createEmployee(0L, name, surname, Long.valueOf(pin), birthdate, department, position, rank, Double.valueOf(salary), Integer.valueOf(experience));
        ChangeType change = button.getText().equals("Add") ? ChangeType.ADD : ChangeType.EDIT;

        try {
            switch (change) {
                case ADD -> {
                    DatabaseUtils.addEmployee(employee);
                    employeesObservableList.add(employee);
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.ADDED, "", 2);
                }
                case EDIT -> {
                    System.out.println(selectedEmployee.getId());

                    employee.setId(selectedEmployee.getId());

                    if (employee.equals(selectedEmployee)) {
                        displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, GeneralUtils.INPUT_NO_CHANGE, Alert.AlertType.INFORMATION);
                        return;
                    } else {
                        ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_CHANGE + "data for " + selectedEmployee + "?");
                        if (selectedButton.equals(ButtonType.OK)) {
                            DatabaseUtils.editEmployee(employee);
                            employeesObservableList.replaceAll(e -> (e.getId().equals(selectedEmployee.getId()) ? employee : e));
                            ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.CHANGED, "", 2);
                        }
                    }
                }
            }
        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }

        ChangedData<Employee, Employee> changedData = new ChangedData<>(employee, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), change, LocalDateTime.now());

        try {
            dataMap = FileUtils.deserializeData(Employee.class);
        } catch (DeserializationException e) {
            GeneralUtils.logger.error("Unable to deserialize. Employee data is not available.");
        }

        dataMap.put(selectedEmployee, changedData);

        FileUtils.serializeData(dataMap, Employee.class);

    }

    public void deleteEmployee() {

        int selectedIndex = employeesTableView.getSelectionModel().getSelectedIndex();
        Employee selectedEmployee = employeesTableView.getSelectionModel().getSelectedItem();

        if (selectedIndex >= 0) {

            ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_DELETE + selectedEmployee + "?");

            if (selectedButton.equals(ButtonType.OK)) {

                try {
                    employeesTableView.getItems().remove(selectedIndex);
                    employeesTableView.getSelectionModel().clearSelection();
                    employeesObservableList.remove(selectedEmployee);
                } catch (Exception e) {
                    displayGeneralDialog(GeneralUtils.UI_TITLE, GeneralUtils.UI_HEADER, "", Alert.AlertType.ERROR);
                }

                try {
                    DatabaseUtils.deleteEmployee(selectedEmployee.getId());
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.DELETED, "", 2);

                } catch (DatabaseException e) {
                    displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
                }

                ChangedData<Employee, Employee> changedData = new ChangedData<>(selectedEmployee, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), ChangeType.DELETE, LocalDateTime.now());

                try {
                    dataMap = FileUtils.deserializeData(Employee.class);
                } catch (DeserializationException e) {
                    GeneralUtils.logger.error("Unable to deserialize. Employee data is not available.");
                }

                dataMap.put(selectedEmployee, changedData);
                FileUtils.serializeData(dataMap, Employee.class);
            }
        } else {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, "Select an employee to delete.", Alert.AlertType.INFORMATION);
        }
    }

}
