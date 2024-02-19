package hr.pmatkovic.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static hr.pmatkovic.utils.GeneralUtils.logger;

public class Main extends Application {


    public static final String TITLE = "Restaurant Manager";
    public static final Integer LOGIN_WIDTH = 400;
    public static final Integer LOGIN_HEIGHT = 300;
    public static final Integer MAIN_WIDTH = 850;
    public static final Integer MAIN_HEIGHT = 650;

    private static Stage loginStage;
    private static Stage mainStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {

        loginStage = stage;

        displayLoginStage();
    }

    public static void displayLoginStage() {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginScreen.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);
            loginStage.setTitle(TITLE);
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            System.out.println("Problem loading fxml file.");
            logger.error("Problem loading fxml file.", e);
        }
    }

    public static void displayMainStage(String fxmlFile) {

        loginStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));

        try {
            Parent root = fxmlLoader.load();
            mainStage = new Stage();
            mainStage.setTitle(TITLE);
            mainStage.setScene(new Scene(root, MAIN_WIDTH, MAIN_HEIGHT));
            mainStage.getScene().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("styles.css")).toString());
            mainStage.show();
        } catch (IOException e) {
            System.out.println("Problem loading fxml file.");
            logger.error("Problem loading fxml file.", e);
        }

    }

    public static void changeSceneContent(String fxmlFile) {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));

        try {
            Parent root = fxmlLoader.load();
            mainStage.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Problem loading fxml file.");
            logger.error("Problem loading fxml file.", e);
        }
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static Boolean isMainStage() {
        return mainStage.isShowing();
    }

}