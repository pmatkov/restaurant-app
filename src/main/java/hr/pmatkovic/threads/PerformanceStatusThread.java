package hr.pmatkovic.threads;

import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.entities.staff.PerformanceReview;
import hr.pmatkovic.exceptions.DatabaseException;
import hr.pmatkovic.utils.DatabaseUtils;
import hr.pmatkovic.utils.GeneralUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.DB_TEXT;
import static hr.pmatkovic.utils.DatabaseUtils.updateEmployeePerformance;
import static hr.pmatkovic.utils.GeneralUtils.DATE_TIME_FORMATTER;

/**
 * A thread which displays performance change
 */

public class PerformanceStatusThread implements Runnable {

    private final TableView<Employee> employeesTableView;
    private final ObservableList<Employee> employeesObservableList;
    private final Label performanceUpdateLabel;

    private final Map<Long, Double> mapOfEmployeesBonuses = new HashMap<>();
    private List<PerformanceReview> performanceReviews = new ArrayList<>();
    private List<LocalDateTime> timeStamps = new ArrayList<>();
    private List<LocalDateTime> oldTimeStamps = new ArrayList<>();
    private Instant lastAccessTimestamp;

    public PerformanceStatusThread(TableView<Employee> employeesTableView, ObservableList<Employee> employeesObservableList, Label performanceUpdateLabel) {
        this.performanceUpdateLabel = performanceUpdateLabel;
        this.employeesObservableList = employeesObservableList;
        this.employeesTableView = employeesTableView;
    }

    @Override
    public void run() {

        try {
            synchronized (this) {

                timeStamps = DatabaseUtils.getTimeStamps();

                if (!timeStamps.equals(oldTimeStamps)) {

                    performanceReviews = DatabaseUtils.getPerformanceReviews(null);
                    oldTimeStamps = List.copyOf(timeStamps);
                    lastAccessTimestamp = Instant.now();
                }
                else
                    return;
            }

            if (!performanceReviews.isEmpty()) {

                for (PerformanceReview review : performanceReviews) {

                    mapOfEmployeesBonuses.put(review.getEmployee().getId(), review.calculatePerformance());
                }
                updateEmployeePerformance(mapOfEmployeesBonuses);

                employeesObservableList.forEach(e -> {
                    Long id = e.getId();
                    if (mapOfEmployeesBonuses.containsKey(id))
                       e.setPerformance(mapOfEmployeesBonuses.get(id));
                });

                employeesTableView.refresh();
                displayPerformanceChange(lastAccessTimestamp.atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMATTER));

                mapOfEmployeesBonuses.clear();
            }

        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);
        }
   }

    public void displayPerformanceChange(String time) {

        performanceUpdateLabel.setText(time);

        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(2), new KeyValue(performanceUpdateLabel.textFillProperty(), Color.RED)),
                new KeyFrame(javafx.util.Duration.seconds(4), new KeyValue(performanceUpdateLabel.textFillProperty(), Color.BLACK))
        );
        timeline.play();
    }
}
