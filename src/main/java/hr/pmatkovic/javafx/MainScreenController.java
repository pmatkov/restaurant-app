package hr.pmatkovic.javafx;

import hr.pmatkovic.entities.general.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import static hr.pmatkovic.utils.FileUtils.*;

/**
 * Main screen controller
 */

public class MainScreenController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label cityLabel;

    private void init() {

        RestaurantInfo restaurantInfo = getRestaurantInfo();

        nameLabel.setText(restaurantInfo.name());
        addressLabel.setText(restaurantInfo.address());
        cityLabel.setText(restaurantInfo.city());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }

}
