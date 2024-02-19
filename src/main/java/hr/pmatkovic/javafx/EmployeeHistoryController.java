package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.utils.FileUtils;
import hr.pmatkovic.utils.GeneralUtils;
import hr.pmatkovic.utils.StyleCellValue;
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
 * Employee history controller
 */

public class EmployeeHistoryController {

    @FXML
    private TextField employeePinTextField;
    @FXML
    private TextField employeeNameTextField;
    @FXML
    private TextField employeeSurnameTextField;
    @FXML
    private DatePicker employeeBirthdateDatePicker;
    @FXML
    private ComboBox<String> positionComboBox;
    @FXML
    private ComboBox<String> rankComboBox;
    @FXML
    private TextField changeTypeTextField;
    @FXML
    private TextField changedByTextField;
    @FXML
    private DatePicker timeOfChangeDatePicker;

    @FXML
    private TextField pinCurrentTextField;
    @FXML
    private TextField nameCurrentTextField;
    @FXML
    private TextField surnameCurrentTextField;
    @FXML
    private TextField birthdateCurrentTextField;
    @FXML
    private TextField positionCurrentTextField;
    @FXML
    private TextField rankCurrentTextField;
    @FXML
    private TextField salaryCurrentTextField;
    @FXML
    private TextField bonusCurrentTextField;
    @FXML
    private TextField performanceCurrentTextField;
    @FXML
    private TextField experienceCurrentTextField;

    @FXML
    private TextField pinOldTextField;
    @FXML
    private TextField nameOldTextField;
    @FXML
    private TextField surnameOldTextField;
    @FXML
    private TextField birthdateOldTextField;
    @FXML
    private TextField positionOldTextField;
    @FXML
    private TextField rankOldTextField;
    @FXML
    private TextField salaryOldTextField;
    @FXML
    private TextField bonusOldTextField;
    @FXML
    private TextField performanceOldTextField;
    @FXML
    private TextField experienceOldTextField;

    @FXML
    private TableView<Map.Entry<Employee, ChangedData<Employee, Employee>>> employeesHistoryTableView;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> employeePinTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> employeeNameTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> employeeSurnameTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> employeeBirthdateTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> employeePositionTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> employeeRankTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> changeTypeTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> changedByTableColumn;
    @FXML
    private TableColumn<Map.Entry<Employee, ChangedData<Employee, Employee>>, String> timeOfChangeTableColumn;

    @FXML
    private GridPane historyFieldsGridPane;

    private Map<Employee, ChangedData<Employee, Employee>> dataMap;

    public void initialize() {

        changeTypeTableColumn.setCellFactory(StyleCellValue::new);

        setColumnValueFactory(employeeNameTableColumn, e -> e.getValue().getChangedObject().getName());
        setColumnValueFactory(employeeSurnameTableColumn, e -> e.getValue().getChangedObject().getSurname());
        setColumnValueFactory(employeePinTableColumn, e -> e.getValue().getChangedObject().getPin().toString());
        setColumnValueFactory(employeeBirthdateTableColumn, e -> e.getValue().getChangedObject().getBirthdate().format(GeneralUtils.DATE_FORMATTER));
        setColumnValueFactory(employeePositionTableColumn, e -> e.getValue().getChangedObject().getPosition().toString());
        setColumnValueFactory(employeeRankTableColumn, e -> e.getValue().getChangedObject().getRank().toString());
        setColumnValueFactory(changedByTableColumn, e -> e.getValue().getChangedBy().toString());
        setColumnValueFactory(changeTypeTableColumn, e -> e.getValue().getChangeType().getString());
        setColumnValueFactory(timeOfChangeTableColumn, e -> e.getValue().getTimeOfChange().format(DATE_TIME_FORMATTER));

        employeePinTableColumn.setComparator(new NumbersComparator());
        employeeBirthdateTableColumn.setComparator(new DatesComparator());

        employeesHistoryTableView.setSortPolicy(tv -> {
            Comparator<Map.Entry<Employee, ChangedData<Employee, Employee>>> comparator = new HistoricDataSorter();
            FXCollections.sort(tv.getItems(), comparator);
            return true;
        });
        employeesHistoryTableView.getSortOrder().add(timeOfChangeTableColumn);
        employeesHistoryTableView.sort();

        employeesHistoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DecimalFormat df = new DecimalFormat("###.##");
                pinCurrentTextField.setText(newVal.getValue().getChangedObject().getPin().toString());
                nameCurrentTextField.setText(newVal.getValue().getChangedObject().getName());
                surnameCurrentTextField.setText(newVal.getValue().getChangedObject().getSurname());
                birthdateCurrentTextField.setText(newVal.getValue().getChangedObject().getBirthdate().format(DATE_FORMATTER));
                positionCurrentTextField.setText(newVal.getValue().getChangedObject().getPosition().toString());
                rankCurrentTextField.setText(newVal.getValue().getChangedObject().getRank().toString());
                salaryCurrentTextField.setText(newVal.getValue().getChangedObject().getSalary().toString());
                bonusCurrentTextField.setText(newVal.getValue().getChangedObject().getBonus().toString());
                performanceCurrentTextField.setText(df.format(newVal.getValue().getChangedObject().getPerformance()));
                experienceCurrentTextField.setText(newVal.getValue().getChangedObject().getExperience().toString());

                if (newVal.getValue().getChangeType().equals(ChangeType.EDIT)) {

                    System.out.println("test");
                    pinOldTextField.setText(newVal.getKey().getPin().toString());
                    nameOldTextField.setText(newVal.getKey().getName());
                    surnameOldTextField.setText(newVal.getKey().getSurname());
                    birthdateOldTextField.setText(newVal.getKey().getBirthdate().format(DATE_FORMATTER));
                    positionOldTextField.setText(newVal.getKey().getPosition().toString());
                    rankOldTextField.setText(newVal.getKey().getRank().toString());
                    salaryOldTextField.setText(newVal.getKey().getSalary().toString());
                    bonusOldTextField.setText(newVal.getKey().getBonus().toString());
                    performanceOldTextField.setText(df.format(newVal.getKey().getPerformance()));
                    experienceOldTextField.setText(newVal.getKey().getExperience().toString());
                }
                else {

                    clearTextFields(historyFieldsGridPane);
                }
            }
        });

        compareTextFields(pinCurrentTextField, pinOldTextField);
        compareTextFields(nameCurrentTextField, nameOldTextField);
        compareTextFields(surnameCurrentTextField, surnameOldTextField);
        compareTextFields(birthdateCurrentTextField, birthdateOldTextField);
        compareTextFields(positionCurrentTextField, positionOldTextField);
        compareTextFields(rankCurrentTextField, rankOldTextField);
        compareTextFields(salaryCurrentTextField, salaryOldTextField);
        compareTextFields(bonusCurrentTextField, bonusOldTextField);
        compareTextFields(performanceCurrentTextField, performanceOldTextField);
        compareTextFields(experienceCurrentTextField, experienceOldTextField);

        ObservableList<Map.Entry<Employee, ChangedData<Employee, Employee>>> list = FXCollections.observableArrayList();;

        try {
            dataMap = FileUtils.deserializeData(Employee.class);
            for (Map.Entry<Employee, ChangedData<Employee, Employee>> entry : dataMap.entrySet()) {
                list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }

        } catch (DeserializationException e) {
            displayGeneralDialog(HISTORY_TITLE, HISTORY_HEADER, "Employee history is not available.", Alert.AlertType.INFORMATION);
            logger.info("Unable to deserialize. Employee history is not available.");

        }

        employeesHistoryTableView.setItems(list);

        positionComboBox.setItems(FXCollections.observableArrayList(Position.getListOfStrings()));
        rankComboBox.setItems(FXCollections.observableArrayList(Rank.getListOfStrings()));

    }

}


