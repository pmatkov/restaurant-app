package hr.pmatkovic.javafx;

import java.io.IOException;

/**
 * Menu controller
 */

public class MenuController {

    public void displayEmployees() throws IOException {
        Main.changeSceneContent("employee.fxml");
    }

    public void displayEmployeesHistory() throws IOException {
        Main.changeSceneContent("employeeHistory.fxml");
    }

    public void displayPerformanceReview() throws IOException {
        Main.changeSceneContent("performanceReview.fxml");
    }

    public void displayPerformanceHistory() throws IOException {
        Main.changeSceneContent("performanceHistory.fxml");
    }

    public void displayInventory() throws IOException {
        Main.changeSceneContent("inventory.fxml");
    }

    public void displayInventoryHistory() throws IOException {
        Main.changeSceneContent("inventoryHistory.fxml");
    }

    public void displayReservations() throws IOException {
        Main.changeSceneContent("reservation.fxml");
    }

    public void displayReservationsHistory() throws IOException {
        Main.changeSceneContent("reservationHistory.fxml");
    }
}
