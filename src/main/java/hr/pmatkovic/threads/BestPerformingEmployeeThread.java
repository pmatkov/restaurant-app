package hr.pmatkovic.threads;

import hr.pmatkovic.entities.general.*;
import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.exceptions.DatabaseException;
import hr.pmatkovic.exceptions.DepartmentCreationException;
import hr.pmatkovic.utils.DatabaseUtils;
import hr.pmatkovic.utils.GeneralUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.DB_TEXT;

/**
 * A thread which displays the best performing employee
 */

public class BestPerformingEmployeeThread implements Runnable {

    private final Label bestEmployeeLabel;
    private List<Employee> employees;

    public BestPerformingEmployeeThread(Label bestEmployeeLabel) {

        this.bestEmployeeLabel = bestEmployeeLabel;
    }

    @Override
    public void run() {
        try {

            synchronized (this) {
                employees = DatabaseUtils.getEmployeeWithPerformanceData();
            }
            Organisation<Department> organisation = new Organisation<>(new Bar(employees), new Kitchen(employees), new Floor(employees));
            Optional<Employee> employee = organisation.findBestPerformingEmployee(employees);
            employee.ifPresent(e -> displayBestPerformingEmployee(e.toString()));

        } catch (DatabaseException e) {
            displayGeneralDialog(GeneralUtils.DB_TITLE, GeneralUtils.DB_HEADER, DB_TEXT, Alert.AlertType.ERROR);

        } catch (DepartmentCreationException e) {
            System.out.println("Unable to create department.");
        }

    }

    public void displayBestPerformingEmployee(String name) {

        bestEmployeeLabel.setText(name);

        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(2), new KeyValue(bestEmployeeLabel.textFillProperty(), Color.CORNFLOWERBLUE)),
                new KeyFrame(javafx.util.Duration.seconds(4), new KeyValue(bestEmployeeLabel.textFillProperty(), Color.BLACK))
        );
        timeline.play();
    }
}
