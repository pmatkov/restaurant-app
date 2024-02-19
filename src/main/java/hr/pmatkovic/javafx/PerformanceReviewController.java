package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.exceptions.DatabaseException;
import hr.pmatkovic.exceptions.DeserializationException;
import hr.pmatkovic.utils.*;
import javafx.beans.property.SimpleObjectProperty;
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

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.ControllerUtils.setDateConverter;
import static hr.pmatkovic.utils.ValidationUtils.*;

/**
 * Performance review controller
 */

public class PerformanceReviewController {

    @FXML
    private ComboBox<Employee> employeeComboBox;
    @FXML
    private ComboBox<Grade> attendanceComboBox;
    @FXML
    private ComboBox<Grade> qualityComboBox;
    @FXML
    private ComboBox<Grade> achievementsComboBox;
    @FXML
    private ComboBox<Employee> managerComboBox;
    @FXML
    private DatePicker reviewDateDatePicker;

    @FXML
    private ComboBox<Employee> editEmployeeComboBox;
    @FXML
    private ComboBox<Position> editEmployeePositionComboBox;
    @FXML
    private ComboBox<Rank> editEmployeeRankComboBox;
    @FXML
    private ComboBox<Grade>  editAttendanceComboBox;
    @FXML
    private ComboBox<Grade>  editQualityComboBox;
    @FXML
    private ComboBox<Grade>  editAchievementsComboBox;
    @FXML
    private ComboBox<Employee> editManagerComboBox;
    @FXML
    private ComboBox<Position> editManagerPositionComboBox;
    @FXML
    private DatePicker editReviewDateDatePicker;

    @FXML
    private TableView<PerformanceReview> performanceReviewsTableView;
    @FXML
    private TableColumn<PerformanceReview, String> employeeTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> employeePositionTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> employeeRankTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> attendanceTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> qualityTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> achievementsTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> overallTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> managerTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> managerPositionTableColumn;
    @FXML
    private TableColumn<PerformanceReview, String> reviewDateTableColumn;

    @FXML
    private GridPane inputFieldsGridPane;
    @FXML
    private Button button;
    @FXML
    private Label modelUpdateLabel;

    private List<PerformanceReview> performanceReviews;
    private List<PerformanceReview> filteredPerformanceReviews;
    private List<Employee> employees;

    private ObservableList<PerformanceReview> performanceReviewsObservableList;
    private Map<PerformanceReview, ChangedData<PerformanceReview, Employee>> dataMap = new HashMap<>();

    public void initialize() {

        try {
            performanceReviews = DatabaseUtils.getPerformanceReviews(null);
            employees = DatabaseUtils.getEmployees(null);

        } catch (DatabaseException e) {

            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }

        performanceReviewsObservableList = FXCollections.observableArrayList(performanceReviews);

        setColumnValueFactory(employeeTableColumn, p -> p.getEmployee().toString());
        setColumnValueFactory(employeePositionTableColumn, p -> p.getEmployee().getPosition().toString());
        setColumnValueFactory(employeeRankTableColumn, p -> p.getEmployee().getRank().toString());
        setColumnValueFactory(attendanceTableColumn, p -> p.getAttendance().toString());
        setColumnValueFactory(qualityTableColumn, p -> p.getQuality().toString());
        setColumnValueFactory(achievementsTableColumn, p -> p.getAchievements().toString());
        overallTableColumn.setCellValueFactory(cellData -> {

            PerformanceReview review = cellData.getValue();
            Double performance = review.calculatePerformance();
            review.getEmployee().setPerformance(performance);
            review.getEmployee().setPerformanceReviewed(true);

            DecimalFormat df = new DecimalFormat("###.##");

            return new SimpleObjectProperty<>(df.format(performance));
        });

        setColumnValueFactory(reviewDateTableColumn, e -> e.getReviewDate().format(GeneralUtils.DATE_FORMATTER));
        setColumnValueFactory(managerTableColumn, p -> p.getManager().toString());
        setColumnValueFactory(managerPositionTableColumn, p -> p.getManager().getPosition().toString());

        performanceReviewsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DecimalFormat df = new DecimalFormat("###.##");
                editEmployeeComboBox.getSelectionModel().select(newVal.getEmployee());
                editEmployeePositionComboBox.getSelectionModel().select(newVal.getEmployee().getPosition());
                editEmployeeRankComboBox.getSelectionModel().select(newVal.getEmployee().getRank());
                editAttendanceComboBox.getSelectionModel().select(newVal.getAttendance());
                editQualityComboBox.getSelectionModel().select(newVal.getQuality());
                editAchievementsComboBox.getSelectionModel().select(newVal.getAchievements());
                editManagerComboBox.getSelectionModel().select(newVal.getManager());
                editManagerPositionComboBox.getSelectionModel().select(newVal.getManager().getPosition());
                editReviewDateDatePicker.setValue(newVal.getReviewDate());

                button.setText("Edit");
            }
            else {

                ControllerUtils.clearInputFields(inputFieldsGridPane);
                button.setText("Add");
            }

        });

        setTextComboBox(employeeComboBox);
        setTextComboBox(attendanceComboBox);
        setTextComboBox(qualityComboBox);
        setTextComboBox(achievementsComboBox);
        setTextComboBox(managerComboBox);
        setTextComboBox(editEmployeeComboBox);
        setTextComboBox(editEmployeePositionComboBox);
        setTextComboBox(editEmployeeRankComboBox);
        setTextComboBox(editAttendanceComboBox);
        setTextComboBox(editQualityComboBox);
        setTextComboBox(editAchievementsComboBox);
        setTextComboBox(editManagerComboBox);
        setTextComboBox(editManagerPositionComboBox);

        clearSelectionComboBox(employeeComboBox);
        clearSelectionComboBox(attendanceComboBox);
        clearSelectionComboBox(qualityComboBox);
        clearSelectionComboBox(achievementsComboBox);
        clearSelectionComboBox(managerComboBox);
        clearSelectionComboBox(editEmployeeComboBox);
        clearSelectionComboBox(editEmployeePositionComboBox);
        clearSelectionComboBox(editEmployeeRankComboBox);
        clearSelectionComboBox(editAttendanceComboBox);
        clearSelectionComboBox(editQualityComboBox);
        clearSelectionComboBox(editAchievementsComboBox);
        clearSelectionComboBox(editManagerComboBox);
        clearSelectionComboBox(editManagerPositionComboBox);

        ObservableList<Employee> addComboBoxList = FXCollections.observableArrayList(findEmployeesWithoutReview(employees, performanceReviewsObservableList));

        performanceReviewsObservableList.addListener((ListChangeListener<PerformanceReview>) change -> addComboBoxList.setAll(findEmployeesWithoutReview(employees, performanceReviewsObservableList)));

        button.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Edit")) {
                editEmployeeComboBox.setDisable(true);
            } else {
                if (!addComboBoxList.isEmpty()) {
                    editEmployeeComboBox.setItems(addComboBoxList);
                    editEmployeeComboBox.setDisable(false);
                }
                else{
                    editEmployeeComboBox.setDisable(true);
                }
            }
        });

        List<Employee> employeesWithPerformanceReview = performanceReviews.stream().map(PerformanceReview::getEmployee).collect(Collectors.toList());
        Set<Employee> managersWithPerformanceReview = performanceReviews.stream().map(PerformanceReview::getManager).collect(Collectors.toSet());

        employeeComboBox.setItems(FXCollections.observableArrayList(employeesWithPerformanceReview));
        attendanceComboBox.setItems(FXCollections.observableArrayList(Grade.getListOfEnumValues()));
        qualityComboBox.setItems(FXCollections.observableArrayList(Grade.getListOfEnumValues()));
        achievementsComboBox.setItems(FXCollections.observableArrayList(Grade.getListOfEnumValues()));
        managerComboBox.setItems(FXCollections.observableArrayList(managersWithPerformanceReview));

        editEmployeeComboBox.setItems(FXCollections.observableArrayList(findEmployeesWithoutReview(employees, performanceReviewsObservableList)));
        editEmployeePositionComboBox.setItems(FXCollections.observableArrayList(Position.getListOfEnumValues()));
        editEmployeeRankComboBox.setItems(FXCollections.observableArrayList(Rank.getListOfEnumValues()));
        editAttendanceComboBox.setItems(FXCollections.observableArrayList(Grade.getListOfEnumValues()));
        editQualityComboBox.setItems(FXCollections.observableArrayList(Grade.getListOfEnumValues()));
        editAchievementsComboBox.setItems(FXCollections.observableArrayList(Grade.getListOfEnumValues()));
        editManagerComboBox.setItems(FXCollections.observableArrayList(employees.stream().dropWhile(e -> Position.isRegularEmployee(e.getPosition())).collect(Collectors.toList())));
        editManagerPositionComboBox.setItems(FXCollections.observableArrayList(Position.getListOfEnumValues()));

        editEmployeeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editEmployeePositionComboBox.setValue(newValue.getPosition());
                editEmployeeRankComboBox.setValue(newValue.getRank());
            }
        });

        editManagerComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editManagerPositionComboBox.setValue(newValue.getPosition());
            }
        });

        attendanceTableColumn.setComparator(new NumbersComparator());
        qualityTableColumn.setComparator(new NumbersComparator());
        achievementsTableColumn.setComparator(new NumbersComparator());
        reviewDateTableColumn.setComparator(new DatesComparator());

        clearRowSelectionIfSelected(performanceReviewsTableView, inputFieldsGridPane, button);
        clearRowSelectionIfOutside(Main.getMainStage().getScene(), performanceReviewsTableView, inputFieldsGridPane, button);

        setDateConverter(reviewDateDatePicker);
        setDateConverter(editReviewDateDatePicker);

        performanceReviewsTableView.setItems(performanceReviewsObservableList);
    }

    public void filterPerformanceReviews() {

        Employee employee = employeeComboBox.getSelectionModel().getSelectedItem();
        Grade attendance = attendanceComboBox.getSelectionModel().getSelectedItem();
        Grade quality = qualityComboBox.getSelectionModel().getSelectedItem();
        Grade achievements = achievementsComboBox.getSelectionModel().getSelectedItem();
        Employee manager = managerComboBox.getSelectionModel().getSelectedItem();
        LocalDate reviewDate = reviewDateDatePicker.getValue();

        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        if (dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, dateInput.get(), Alert.AlertType.ERROR);
            return;
        }

        PerformanceReview review = new PerformanceReview(employee, manager, attendance, quality, achievements, reviewDate);

        try {
            filteredPerformanceReviews = DatabaseUtils.getPerformanceReviews(review);

        } catch (DatabaseException e) {

            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }

        performanceReviewsTableView.setItems(FXCollections.observableArrayList(filteredPerformanceReviews));
    }

    public void updatePerformanceReview() {

        Employee employee = editEmployeeComboBox.getSelectionModel().getSelectedItem();
        Grade attendance = editAttendanceComboBox.getSelectionModel().getSelectedItem();
        Grade quality = editQualityComboBox.getSelectionModel().getSelectedItem();
        Grade achievements = editAchievementsComboBox.getSelectionModel().getSelectedItem();
        Employee manager = editManagerComboBox.getSelectionModel().getSelectedItem();
        LocalDate reviewDate = editReviewDateDatePicker.getValue();

        Optional<String> emptyInput = checkIfEmptyInput(Map.of("Employee", Optional.ofNullable(employee), "Attendance", Optional.ofNullable(attendance), "Quality", Optional.ofNullable(quality), "Achievements", Optional.ofNullable(achievements),"Manager", Optional.ofNullable(manager), "Review date", Optional.ofNullable(reviewDate)));

        Optional<String> dateInput = getDateValidationStatus() ? Optional.empty() : Optional.of(GeneralUtils.INPUT_DATE);

        String errors = Stream.of(emptyInput, dateInput)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n"));

        if (emptyInput.isPresent() || dateInput.isPresent()) {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, errors, Alert.AlertType.ERROR);
            return;
        }

        PerformanceReview selectedReview = performanceReviewsTableView.getSelectionModel().getSelectedItem();
        PerformanceReview review = new PerformanceReview(employee, manager, attendance, quality, achievements, reviewDate);

        ChangeType change = button.getText().equals("Add") ? ChangeType.ADD : ChangeType.EDIT;

        try {
            switch (change) {
                case ADD -> {
                    DatabaseUtils.addPerformanceReview(review);
                    performanceReviewsObservableList.add(review);
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.ADDED, "", 2);
                }
                case EDIT -> {
                    review.setId(selectedReview.getId());

                    if (review.equals(selectedReview)) {
                        displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, GeneralUtils.INPUT_NO_CHANGE, Alert.AlertType.INFORMATION);
                        return;
                    }
                    else {
                        ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_CHANGE + "review for " + selectedReview.getEmployee() + "?");
                        if (selectedButton.equals(ButtonType.OK)) {
                            DatabaseUtils.editPerformanceReview(review);
                            performanceReviewsObservableList.replaceAll(r -> (r.getId().equals(selectedReview.getId()) ? review : r));
                            ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.CHANGED, "", 2);
                        }
                    }
                }
            }
        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }

        ChangedData<PerformanceReview, Employee> changedData = new ChangedData<>(review, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), change, LocalDateTime.now());

        try {
            dataMap = FileUtils.deserializeData(PerformanceReview.class);
        } catch (DeserializationException e) {
            GeneralUtils.logger.error("Unable to deserialize. Performance review data is not available.");
        }

        dataMap.put(selectedReview, changedData);

        FileUtils.serializeData(dataMap, PerformanceReview.class);

    }

    public void deletePerformanceReview() {

        int selectedIndex = performanceReviewsTableView.getSelectionModel().getSelectedIndex();
        PerformanceReview selectedReview = performanceReviewsTableView.getSelectionModel().getSelectedItem();

        if (selectedIndex >= 0) {

            ButtonType selectedButton = displayConfirmationDialog(GeneralUtils.CONFIRM_TITLE, GeneralUtils.CONFIRM_HEADER, GeneralUtils.CONFIRM_DELETE + "review for " + selectedReview.getEmployee() + "?");

            if (selectedButton.equals(ButtonType.OK))  {

                try {
                    performanceReviewsTableView.getItems().remove(selectedIndex);
                    performanceReviewsTableView.getSelectionModel().clearSelection();
                    performanceReviewsObservableList.remove(selectedReview);
                } catch (Exception e) {
                    displayGeneralDialog(GeneralUtils.UI_TITLE, GeneralUtils.UI_HEADER, "", Alert.AlertType.ERROR);
                }

                try {
                    DatabaseUtils.deletePerformanceReview(selectedReview.getId());
                    ControllerUtils.displayChangeLabel(modelUpdateLabel, GeneralUtils.DELETED, "", 2);

                } catch (DatabaseException e) {
                    displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
                }

                ChangedData<PerformanceReview, Employee> changedData = new ChangedData<>(selectedReview, Authenticator.mapUsernameToEmployee(Authenticator.getloggedInUser()), ChangeType.DELETE, LocalDateTime.now());

                try {
                    dataMap = FileUtils.deserializeData(PerformanceReview.class);
                } catch (DeserializationException e) {
                    GeneralUtils.logger.error("Unable to deserialize. Performance review data is not available.");
                }

                dataMap.put(selectedReview, changedData);
                FileUtils.serializeData(dataMap, PerformanceReview.class);
            }
        }

        else {
            displayGeneralDialog(GeneralUtils.INPUT_TITLE, GeneralUtils.INPUT_HEADER, "Select a review to delete.", Alert.AlertType.INFORMATION);
        }
    }

    private Set<Employee> findEmployeesWithoutReview(List<Employee> employees, ObservableList<PerformanceReview> reviews) {

        Set<Employee> allEmployeeSet = employees.stream().dropWhile(e -> Position.isGeneralManager(e.getPosition())).collect(Collectors.toSet());
        Set<Employee> tableViewEmployeeSet = reviews.stream().map(PerformanceReview::getEmployee).collect(Collectors.toSet());

        allEmployeeSet.removeAll(tableViewEmployeeSet);

        return allEmployeeSet;
    }


}
