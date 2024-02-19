package hr.pmatkovic.javafx;

import hr.pmatkovic.utils.Authenticator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

import static hr.pmatkovic.javafx.Main.*;
import static hr.pmatkovic.utils.GeneralUtils.logger;

/**
 * Login label controller
 */

public class LoginLabelController implements Initializable {

    @FXML
    private Label loginLabel;
    @FXML
    private Label logutLabel;

    private void init() {

        logutLabel.setOnMouseEntered(event -> {
            logutLabel.setUnderline(true);
            logutLabel.setTextFill(Color.RED);
        });
        logutLabel.setOnMouseExited(event -> {
            logutLabel.setUnderline(false);
            logutLabel.setTextFill(Color.BLACK);
        });

        try {
            String loggedInUser = Authenticator.getloggedInUser();
            loginLabel.setText(loggedInUser);

        } catch (NullPointerException e) {
            System.out.println("Unable to read logged in user.");
            logger.error(e.getMessage(), e);
        }
    }

    public void logout() {

        getMainStage().close();
        displayLoginStage();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }

}
