package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.utils.FileUtils;
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
 * Performance history controller
 */


public class PerformanceHistoryController {

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
    private TextField attendanceCurrentTextField;
    @FXML
    private TextField qualityCurrentTextField;
    @FXML
    private TextField achievementsCurrentTextField;
    @FXML
    private TextField managerCurrentTextField;
    @FXML
    private TextField reviewDateCurrentTextField;
    
    @FXML
    private TextField nameOldTextField;
    @FXML
    private TextField surnameOldTextField;
    @FXML
    private TextField attendanceOldTextField;
    @FXML
    private TextField qualityOldTextField;
    @FXML
    private TextField achievementsOldTextField;
    @FXML
    private TextField managerOldTextField;
    @FXML
    private TextField reviewDateOldTextField;

    @FXML
    private GridPane historyFieldsGridPane;

    @FXML
    private TableView<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>> performanceHistoryTableView;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> employeeNameTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> employeeSurnameTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> attendanceTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> qualityTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> achievementsTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> managerTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> changeTypeTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> changedByTableColumn;
    @FXML
    private TableColumn<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>, String> timeOfChangeTableColumn;

    private Map<PerformanceReview, ChangedData<PerformanceReview, Employee>> dataMap;

    public void initialize() {

        changeTypeTableColumn.setCellFactory(StyleCellValue::new);

        setColumnValueFactory(employeeNameTableColumn, e -> e.getValue().getChangedObject().getEmployee().getName());
        setColumnValueFactory(employeeSurnameTableColumn, e -> e.getValue().getChangedObject().getEmployee().getSurname());
        setColumnValueFactory(attendanceTableColumn, e -> e.getValue().getChangedObject().getAttendance().toString());
        setColumnValueFactory(qualityTableColumn, e -> e.getValue().getChangedObject().getQuality().toString());
        setColumnValueFactory(achievementsTableColumn, e -> e.getValue().getChangedObject().getAchievements().toString());
        setColumnValueFactory(managerTableColumn, e -> e.getValue().getChangedObject().getManager().toString());
        setColumnValueFactory(changedByTableColumn, e -> e.getValue().getChangedBy().toString());
        setColumnValueFactory(changeTypeTableColumn, e -> e.getValue().getChangeType().getString());
        setColumnValueFactory(timeOfChangeTableColumn, e -> e.getValue().getTimeOfChange().format(DATE_TIME_FORMATTER));

        performanceHistoryTableView.setSortPolicy(tv -> {
            Comparator<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>> comparator = new HistoricDataSorter();
            FXCollections.sort(tv.getItems(), comparator);
            return true;
        });
        performanceHistoryTableView.getSortOrder().add(timeOfChangeTableColumn);
        performanceHistoryTableView.sort();

        performanceHistoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DecimalFormat df = new DecimalFormat("###.##");
                nameCurrentTextField.setText(newVal.getValue().getChangedObject().getEmployee().getName());
                surnameCurrentTextField.setText(newVal.getValue().getChangedObject().getEmployee().getSurname());
                attendanceCurrentTextField.setText(newVal.getValue().getChangedObject().getAttendance().toString());
                qualityCurrentTextField.setText(newVal.getValue().getChangedObject().getQuality().toString());
                achievementsCurrentTextField.setText(newVal.getValue().getChangedObject().getAchievements().toString());
                managerCurrentTextField.setText(newVal.getValue().getChangedObject().getManager().toString());
                reviewDateCurrentTextField.setText(newVal.getValue().getChangedObject().getReviewDate().format(DATE_FORMATTER));

                if (newVal.getValue().getChangeType().equals(ChangeType.EDIT)) {
                    nameOldTextField.setText(newVal.getKey().getEmployee().getName());
                    surnameOldTextField.setText(newVal.getKey().getEmployee().getSurname());
                    attendanceOldTextField.setText(newVal.getKey().getAttendance().toString());
                    qualityOldTextField.setText(newVal.getKey().getQuality().toString());
                    achievementsOldTextField.setText(newVal.getKey().getAchievements().toString());
                    managerOldTextField.setText(newVal.getKey().getManager().toString());
                    reviewDateOldTextField.setText(newVal.getKey().getReviewDate().format(DATE_FORMATTER));
                }
                else {
                    clearTextFields(historyFieldsGridPane);
                }
            }
        });

        compareTextFields(nameCurrentTextField, nameOldTextField);
        compareTextFields(surnameCurrentTextField, surnameOldTextField);
        compareTextFields(attendanceCurrentTextField, attendanceOldTextField);
        compareTextFields(qualityCurrentTextField, qualityOldTextField);
        compareTextFields(achievementsCurrentTextField, achievementsOldTextField);
        compareTextFields(managerCurrentTextField, managerOldTextField);
        compareTextFields(reviewDateCurrentTextField, reviewDateOldTextField);

        ObservableList<Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>>> list = FXCollections.observableArrayList();;

        try {
            dataMap = FileUtils.deserializeData(PerformanceReview.class);
            for (Map.Entry<PerformanceReview, ChangedData<PerformanceReview, Employee>> entry : dataMap.entrySet()) {
                list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }

        } catch (DeserializationException e) {
            displayGeneralDialog(HISTORY_TITLE, HISTORY_HEADER, "Performance history is not available.", Alert.AlertType.INFORMATION);
            logger.info("Unable to deserialize. Performance history is not available.");

        }

        performanceHistoryTableView.setItems(list);

        positionComboBox.setItems(FXCollections.observableArrayList(Position.getListOfEnumValues()));
        rankComboBox.setItems(FXCollections.observableArrayList(Rank.getListOfEnumValues()));

    }


}


