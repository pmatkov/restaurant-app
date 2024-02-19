package hr.pmatkovic.javafx;

import hr.pmatkovic.exceptions.AuthenticationException;
import hr.pmatkovic.utils.Authenticator;
import hr.pmatkovic.utils.GeneralUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.security.NoSuchAlgorithmException;

import static hr.pmatkovic.javafx.Main.displayMainStage;
import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.authLogger;

/**
 * Login screen controller
 */

public final class LoginScreenController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButtom;

    public void initialize() {

        loginButtom.disableProperty().bind(usernameTextField.textProperty().isEmpty().or(passwordTextField.textProperty().isEmpty()));

    }

    public void authenticate() throws NoSuchAlgorithmException {

        try {

            if (Authenticator.isAuthenticated(usernameTextField.getText(), passwordTextField.getText())) {
                displayMainStage("mainScreen.fxml");

            }
        }
        catch (AuthenticationException e){
            displayGeneralDialog(GeneralUtils.AUTHENTICATION_TITLE, GeneralUtils.AUTHENTICATION_HEADER, GeneralUtils.AUTHENTICATION_TEXT, Alert.AlertType.ERROR);

            usernameTextField.clear();
            passwordTextField.clear();

            authLogger.error("", e);
        }
    }

    public void exitApplication() {

       Platform.exit();

    }
}
