module hr.pmatkovic.restaurantmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires javax.inject;
    requires java.sql;
    requires tornadofx.controls;

    opens hr.pmatkovic.javafx to javafx.fxml;
    exports hr.pmatkovic.entities.service;
    exports hr.pmatkovic.exceptions;
    exports hr.pmatkovic.javafx;
    exports hr.pmatkovic.utils;
}
