package hr.pmatkovic.utils;

import hr.pmatkovic.entities.staff.Employee;
import hr.pmatkovic.exceptions.AuthenticationException;
import hr.pmatkovic.exceptions.DatabaseException;
import javafx.scene.control.Alert;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hr.pmatkovic.utils.ControllerUtils.*;
import static hr.pmatkovic.utils.GeneralUtils.DB_HEADER;
import static hr.pmatkovic.utils.FileUtils.getUsersData;

/**
 * A service class with authentication methods
 */

public final class Authenticator {

    private static String loggedInUser;

    public static Boolean isAuthenticated(String username, String password) throws NoSuchAlgorithmException, AuthenticationException {

        Map<String, String> mapOfUsernamesAndPasswords = getUsersData();

        if (mapOfUsernamesAndPasswords.containsKey(username)) {

            String hashedPassword = mapOfUsernamesAndPasswords.get(username);

            if (isValidPassword(password, hashedPassword)) {
                loggedInUser = username;
                return true;

            }
        }
        throw new AuthenticationException("Authentication failed. user: " + username + " password: " + password);
    }

    public static Boolean isValidPassword(String password, String hashedPassword) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte[] digest = md.digest();

        BigInteger bigInt = new BigInteger(1,digest);
        String hashText = bigInt.toString(16);

        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }

        return hashText.equals(hashedPassword);
    }

    public static Employee mapUsernameToEmployee(String username) {

        try {
            List<Employee> employees = DatabaseUtils.getEmployees(null);

            return employees.stream().filter(e -> e.getSurname().toLowerCase().contains(username.substring(1))).collect(Collectors.toList()).get(0);

        } catch (DatabaseException e) {

            displayGeneralDialog(GeneralUtils.DB_TITLE, DB_HEADER, GeneralUtils.DB_TEXT, Alert.AlertType.ERROR);
        }
        return null;
    }

    public static String getloggedInUser() {
            return loggedInUser;
    }
}

