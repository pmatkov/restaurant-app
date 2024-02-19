package hr.pmatkovic.utils;

import hr.pmatkovic.entities.general.DepartmentName;
import hr.pmatkovic.entities.inventory.*;
import hr.pmatkovic.entities.service.Customer;
import hr.pmatkovic.entities.service.Reservation;
import hr.pmatkovic.entities.service.ReservationType;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.DatabaseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static hr.pmatkovic.utils.GeneralUtils.DB_HEADER;
import static hr.pmatkovic.utils.GeneralUtils.dbLogger;

/**
 * A utility class containing methods that support database operations
 */

public class DatabaseUtils {

    private static final String DB_PROPERTIES = "dat/database.properties";

    private static Connection connectToDB() throws SQLException, IOException {

        Properties properties = new Properties();
        properties.load(new FileReader(DB_PROPERTIES));

        return DriverManager.getConnection(properties.getProperty("databaseUrl"), properties.getProperty("username"), properties.getProperty("password"));
    }

    public static List<Employee> getEmployees(Employee employee) throws DatabaseException {

        List<Employee> listOfEmployees = new ArrayList<>();

        try (Connection connection = connectToDB()) {
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM EMPLOYEE WHERE 1 = 1");
            if (Optional.ofNullable(employee).isPresent()) {
                if (!Optional.ofNullable(employee.getName()).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND NAME LIKE '%").append(employee.getName()).append("%'");
                }
                if (!Optional.ofNullable(employee.getSurname()).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND SURNAME LIKE '%").append(employee.getSurname()).append("%'");
                }
                if (!Optional.ofNullable(employee.getPin()).map(String::valueOf).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND PIN LIKE '%").append(employee.getPin()).append("%'");
                }
                if (!Optional.ofNullable(employee.getBirthdate()).map(LocalDate::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND BIRTHDATE = '").append(employee.getBirthdate()).append("'");
                }
                if (!Optional.ofNullable(employee.getPosition()).map(Position::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(POSITION) = '").append(employee.getPosition().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(employee.getRank()).map(Rank::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(RANK) = '").append(employee.getRank().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(employee.getSalary()).map(String::valueOf).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND SALARY LIKE '%").append(employee.getSalary()).append("%'");
                }
                if (!Optional.ofNullable(employee.getExperience()).map(String::valueOf).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND EXPERIENCE LIKE '%").append(employee.getExperience()).append("%'");
                }
            }

            else {
                sqlQuery = new StringBuilder("SELECT * FROM EMPLOYEE");
            }

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery(sqlQuery.toString());

            while (resultSet.next()) {

                Employee newEmployee = mapResultToEmployee(resultSet);

                listOfEmployees.add(newEmployee);
            }
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return listOfEmployees;
    }

    public static Employee getEmployeeById(Long id) throws DatabaseException {

        Employee employee = null;

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM EMPLOYEE WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                employee = mapResultToEmployee(resultSet);

            }
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }

        return employee;
    }

    public static List<Employee> getEmployeeWithPerformanceData() throws DatabaseException {

        List<Employee> listOfEmployees = new ArrayList<>();

        try (Connection connection = connectToDB()) {

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery("SELECT * FROM EMPLOYEE");

            while (resultSet.next()) {

                Employee employee = mapResultToEmployee(resultSet);
                Double performance = Double.parseDouble(resultSet.getString("performance"));
                Boolean performanceReviewed = Boolean.parseBoolean(resultSet.getString("performance_reviewed"));
                employee.setPerformance(performance);
                employee.setPerformanceReviewed(performanceReviewed);

                listOfEmployees.add(employee);
            }

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }

        return listOfEmployees;
    }

    public static void addEmployee(Employee employee) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO EMPLOYEE(name, surname, pin, birthdate, department_name, position, rank, salary, experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            setParameterValuesForEmployee(employee, preparedStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void editEmployee(Employee employee) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE EMPLOYEE SET name = ?, surname = ?, pin = ?, birthdate = ?, department_name = ?, position = ?, rank = ?, salary = ?, experience = ? WHERE id = ?");

            setParameterValuesForEmployee(employee, preparedStatement);
            preparedStatement.setLong(10, employee.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void deleteEmployee(Long id) throws DatabaseException {

        try (Connection connection = connectToDB()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM EMPLOYEE WHERE id = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void updateEmployeeBonus(List<Employee> employees) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            for (Employee e : employees) {

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE EMPLOYEE SET bonus = ?, bonus_reviewed = ? WHERE id = ?");

                preparedStatement.setDouble(1, e.getBonus());
                preparedStatement.setBoolean(2, e.getBonusReviewed());
                preparedStatement.setLong(3, e.getId());
                preparedStatement.executeUpdate();
            }
        }catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }


    public static void updateEmployeePerformance(Map<Long, Double> mapOfEmployeesBonuses) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            if (mapOfEmployeesBonuses != null && !mapOfEmployeesBonuses.isEmpty()) {

                for (Long key : mapOfEmployeesBonuses.keySet()) {

                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE EMPLOYEE SET performance = ?, performance_reviewed = ? WHERE id = ?");

                    preparedStatement.setDouble(1, mapOfEmployeesBonuses.get(key));
                    preparedStatement.setBoolean(2, true);
                    preparedStatement.setLong(3, key);
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }


    private static Employee mapResultToEmployee(ResultSet resultSet) throws SQLException{

        Long employeeId = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String surname = resultSet.getString("surname");
        Long pin = Long.parseLong(resultSet.getString("pin"));
        LocalDate birthdate = resultSet.getObject("birthdate", LocalDate.class);
        Position position = Position.convertToEnum(resultSet.getString("position"));
        Rank rank = Rank.convertToEnum(resultSet.getString("rank"));
        DepartmentName department = DepartmentName.convertPositionToDepartment(position);
        Double salary = Double.parseDouble(resultSet.getString("salary"));
        Integer experience = Integer.parseInt(resultSet.getString("experience"));

        return Employee.createEmployee(employeeId, name, surname, pin, birthdate, department, position, rank, salary, experience);
    }

    private static void setParameterValuesForEmployee(Employee employee, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, employee.getName());
        preparedStatement.setString(2, employee.getSurname());
        preparedStatement.setLong(3, employee.getPin());
        preparedStatement.setDate(4, Date.valueOf(employee.getBirthdate()));
        preparedStatement.setString(5, employee.getDepartment().getString());
        preparedStatement.setString(6, employee.getPosition().toString());
        preparedStatement.setString(7, employee.getRank().toString());
        preparedStatement.setDouble(8, employee.getSalary());
        preparedStatement.setInt(9, employee.getExperience());
    }

    public static List<PerformanceReview> getPerformanceReviews(PerformanceReview review) throws DatabaseException {

        List<PerformanceReview> listOfReviews = new ArrayList<>();

        try (Connection connection = connectToDB()) {
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM PERFORMANCE_REVIEW WHERE 1 = 1");
            if (Optional.ofNullable(review).isPresent()) {
                if ((!Optional.ofNullable(review.getEmployee()).map(Employee::getId)
                        .map(String::valueOf).map(String::isBlank).orElse(true))) {
                    sqlQuery.append(" AND EMPLOYEE_ID = '").append(review.getEmployee().getId()).append("'");
                }
                if ((!Optional.ofNullable(review.getManager()).map(Employee::getId)
                        .map(String::valueOf).map(String::isBlank).orElse(true))) {
                    sqlQuery.append(" AND MANAGER_ID = '").append(review.getManager().getId()).append("'");
                }
                if (!Optional.ofNullable(review.getAttendance()).map(String::valueOf).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(ATTENDANCE) = '").append(review.getAttendance().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(review.getQuality()).map(String::valueOf).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(QUALITY) = '").append(review.getQuality().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(review.getAchievements()).map(String::valueOf).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(ACHIEVEMENTS) = '").append(review.getAchievements().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(review.getReviewDate()).map(LocalDate::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND REVIEW_DATE = '").append(review.getReviewDate()).append("'");
                }
            }

            else {
                sqlQuery = new StringBuilder("SELECT * FROM PERFORMANCE_REVIEW");
            }

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery(sqlQuery.toString());

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long employeeId = resultSet.getLong("employee_id");
                Long managerId = resultSet.getLong("manager_id");
                Grade attendance = Grade.convertToEnum(resultSet.getString("attendance"));
                Grade quality = Grade.convertToEnum(resultSet.getString("quality"));
                Grade achievements = Grade.convertToEnum(resultSet.getString("achievements"));
                LocalDate reviewDate = resultSet.getObject("review_date", LocalDate.class);

                Employee employee = getEmployeeById(employeeId);
                Employee manager = getEmployeeById(managerId);
                review = new PerformanceReview(id, employee, manager, attendance, quality, achievements, reviewDate);

                listOfReviews.add(review);
            }
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return listOfReviews;
    }

    public static void addPerformanceReview(PerformanceReview review) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO PERFORMANCE_REVIEW(employee_id, manager_id, attendance, quality, achievements, review_date, time_of_change) VALUES (?, ?, ?, ?, ?, ?, ?)");

            setParameterValuesForPerformanceReview(review, preparedStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void editPerformanceReview(PerformanceReview review) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE PERFORMANCE_REVIEW SET employee_id = ?, manager_id = ?, attendance = ?, quality = ?, achievements = ?, review_date = ?, time_of_change = ? WHERE id = ?");

            setParameterValuesForPerformanceReview(review, preparedStatement);
            preparedStatement.setLong(8, review.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void deletePerformanceReview(Long id) throws DatabaseException {


        try (Connection connection = connectToDB()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM PERFORMANCE_REVIEW WHERE id = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    private static void setParameterValuesForPerformanceReview(PerformanceReview review, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1, review.getEmployee().getId());
        preparedStatement.setLong(2, review.getManager().getId());
        preparedStatement.setString(3, review.getAttendance().toString());
        preparedStatement.setString(4, review.getQuality().toString());
        preparedStatement.setString(5, review.getAchievements().toString());
        preparedStatement.setDate(6, Date.valueOf(review.getReviewDate()));
        preparedStatement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

    }

    public static List<InventoryItem> getInventoryItems(InventoryItem item) throws DatabaseException {

        List<InventoryItem> listOfInventoryItems = new ArrayList<>();

        try (Connection connection = connectToDB()) {
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM INVENTORY WHERE 1 = 1");
            if (Optional.ofNullable(item).isPresent()) {
                if (!Optional.ofNullable(item.getName())
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND NAME LIKE '%").append(item.getName()).append("%'");
                }
                if (!Optional.ofNullable(item.getCategory()).map(Category::toString)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(CATEGORY) = '").append(item.getCategory().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(item.getQuantity()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND QUANTITY LIKE '%").append(item.getQuantity()).append("%'");
                }
                if (!Optional.ofNullable(item.getPricePerItem()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND PRICE_PER_ITEM LIKE '%").append(item.getPricePerItem()).append("%'");
                }
                if (item instanceof Food) {
                    if (!Optional.ofNullable(((Food)item).getBestBefore()).map(String::valueOf)
                            .map(String::isBlank).orElse(true)) {
                        sqlQuery.append(" AND BEST_BEFORE LIKE '%").append(((Food) item).getBestBefore()).append("%'");
                    }
                }
                if (!Optional.ofNullable(item.getPurchaseDate()).map(LocalDate::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND PURCHASE_DATE = '").append(item.getPurchaseDate()).append("'");
                }
                if (item instanceof Food) {
                    if (!Optional.ofNullable(((Food)item).getExpiryDate()).map(LocalDate::toString)
                            .map(String::isBlank).orElse(true)) {
                        sqlQuery.append(" AND EXPIRY_DATE = '").append(((Food) item).getExpiryDate()).append("'");
                    }
                }
            }

            else {
                sqlQuery = new StringBuilder("SELECT * FROM INVENTORY");
            }

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery(sqlQuery.toString());

            while (resultSet.next()) {

                InventoryItem newItem = mapResultToInventoryItem(resultSet);
                listOfInventoryItems.add(newItem);
            }
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return listOfInventoryItems;
    }

    public static void addInventoryItem(InventoryItem item) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO INVENTORY(name, category, quantity, price_per_item, best_before, purchase_date, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?)");

            setParameterValuesForInventoryItem(item, preparedStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }


    public static void editInventoryItem(InventoryItem item) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE INVENTORY SET name = ?, category = ?, quantity = ?, price_per_item = ?, best_before = ?, purchase_date = ?, expiry_date = ? WHERE id = ?");

            setParameterValuesForInventoryItem(item, preparedStatement);
            preparedStatement.setLong(8, item.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void updateInventoryItemExpiry(List<InventoryItem> items) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            for (InventoryItem it : items) {

                if (it instanceof Food) {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE INVENTORY SET expiry_date = ? WHERE id = ?");

                    preparedStatement.setDate(1, Date.valueOf(((Food)it).getExpiryDate()));
                    preparedStatement.setLong(2, it.getId());
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    private static InventoryItem mapResultToInventoryItem(ResultSet resultSet) throws SQLException{

        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Category category = Category.convertToEnum(resultSet.getString("category"));
        Double quantity = Double.parseDouble(resultSet.getString("quantity"));
        Double pricePerItem = Double.parseDouble(resultSet.getString("price_per_item"));
        Integer bestBefore = Integer.parseInt(resultSet.getString("best_before"));
        LocalDate purchaseDate = resultSet.getObject("purchase_date", LocalDate.class);
        LocalDate expiryDate = resultSet.getObject("expiry_date", LocalDate.class);

        return InventoryItem.createInventoryItem(id, name, category, quantity, pricePerItem, bestBefore, purchaseDate, expiryDate);
    }

    private static void setParameterValuesForInventoryItem(InventoryItem item, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, item.getName());
        preparedStatement.setString(2, item.getCategory().toString());
        preparedStatement.setDouble(3, item.getQuantity());
        preparedStatement.setDouble(4, item.getPricePerItem());

        if (item instanceof Food){
            preparedStatement.setInt(5, ((Food)item).getBestBefore());
            preparedStatement.setDate(7, Date.valueOf(((Food)item).getExpiryDate()));
        }
        else {
            preparedStatement.setInt(5, 0);
            preparedStatement.setDate(7, Date.valueOf(LocalDate.of(2023, 1, 1)));
        }
        preparedStatement.setDate(6, Date.valueOf(item.getPurchaseDate()));
    }


    public static List<Customer> getCustomers(Customer customer) throws DatabaseException {

        List<Customer> listOfCustomers = new ArrayList<>();

        try (Connection connection = connectToDB()) {
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM CUSTOMER WHERE 1 = 1");
            if (Optional.ofNullable(customer).isPresent()) {
                if (!Optional.ofNullable(customer.getName())
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND NAME LIKE '%").append(customer.getName()).append("%'");
                }
                if (!Optional.ofNullable(customer.getSurname())
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND SURNAME LIKE '%").append(customer.getSurname()).append("%'");
                }
                if (!Optional.ofNullable(customer.getEmail())
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND EMAIL LIKE '%").append(customer.getEmail()).append("%'");
                }
                if (!Optional.ofNullable(customer.getRegularCustomer()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(REGULAR) = '").append(customer.getRegularCustomer().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(customer.getVipCustomer()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(VIP) = '").append(customer.getVipCustomer().toString().toLowerCase()).append("'");
                }
            }
            else {
                sqlQuery = new StringBuilder("SELECT * FROM CUSTOMER");
            }

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery(sqlQuery.toString());

            while (resultSet.next()) {
                Customer newCustomer = mapResultToCustomer(resultSet);
                listOfCustomers.add(newCustomer);
            }
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return listOfCustomers;
    }

    public static Customer getCustomerByID(Connection connection, Long id) throws DatabaseException {

        Customer customer = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM CUSTOMER WHERE id = ?");) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                customer = mapResultToCustomer(resultSet);
            }

        } catch (SQLException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return customer;
    }

    private static Customer mapResultToCustomer(ResultSet resultSet) throws SQLException{

        Long idCustomer = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String surname = resultSet.getString("surname");
        String email = resultSet.getString("email");
        Boolean regular = resultSet.getBoolean("regular");
        Boolean vip = resultSet.getBoolean("vip");

        return new Customer(idCustomer, name, surname, email, regular, vip);
    }

    private static void setParameterValuesForCustomer(Customer customer, PreparedStatement preparedStatement) throws SQLException {

        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getSurname());
        preparedStatement.setString(3, customer.getEmail());
        preparedStatement.setBoolean(4, customer.getRegularCustomer());
        preparedStatement.setBoolean(5, customer.getVipCustomer());
    }

    public static List<Reservation> getReservations(Reservation reservation) throws DatabaseException {

        List<Reservation> listOfReservations = new ArrayList<>();

        try (Connection connection = connectToDB()) {
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM RESERVATION WHERE 1 = 1");
            if (Optional.ofNullable(reservation).isPresent()) {
                if (!Optional.ofNullable(reservation.getCustomer().getId()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND CUSTOMER_ID = '").append(reservation.getCustomer().getId()).append("'");
                }
                if (!Optional.ofNullable(reservation.getReservationType()).map(ReservationType::toString)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(RESERVATION_TYPE) = '").append(reservation.getReservationType().toString().toLowerCase()).append("'");
                }
                if (!Optional.ofNullable(reservation.getNumberOfGuests()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND GUESTS LIKE '%").append(reservation.getNumberOfGuests()).append("%'");
                }
                if (!Optional.ofNullable(reservation.getReservationDateTime()).map(LocalDateTime::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND DATE_TIME = '").append(reservation.getReservationDateTime()).append("'");
                }
            }

            else {
                sqlQuery = new StringBuilder("SELECT * FROM RESERVATION");
            }

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery(sqlQuery.toString());

            while (resultSet.next()) {
                Reservation newReservation = mapResultToReservation(connection, resultSet);
                listOfReservations.add(newReservation);
            }
        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return listOfReservations;
    }

    public static List<Reservation> getReservationsFromCustomers(Map<List<Customer>, Reservation> mapOfCustomers) throws DatabaseException {

        List<Reservation> listOfReservations = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();
        Reservation reservation = null;
        boolean appended = false;

        for (Map.Entry<List<Customer>, Reservation> entry : mapOfCustomers.entrySet()) {
            customers = entry.getKey();
            reservation = entry.getValue();
        }
        StringBuilder idList = new StringBuilder();

        for (int i = 0; i < customers.size(); i++) {
            idList.append(customers.get(i).getId());
            if (i < customers.size() - 1) {
                idList.append(", ");
            }
        }

        try (Connection connection = connectToDB()) {

            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM RESERVATION WHERE 1 = 1");
            if (!idList.isEmpty())
                sqlQuery.append(" AND ID IN (").append(idList).append(")");

            if (Optional.ofNullable(reservation).isPresent()) {
                if (!Optional.ofNullable(reservation.getReservationType()).map(ReservationType::toString)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND LOWER(RESERVATION_TYPE) = '").append(reservation.getReservationType().toString().toLowerCase()).append("'");
                    appended = true;
                }
                if (!Optional.ofNullable(reservation.getNumberOfGuests()).map(String::valueOf)
                        .map(String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND GUESTS LIKE '%").append(reservation.getNumberOfGuests()).append("%'");
                    appended = true;
                }
                if (!Optional.ofNullable(reservation.getReservationDateTime()).map(LocalDateTime::toString).map(
                        String::isBlank).orElse(true)) {
                    sqlQuery.append(" AND DATE_TIME = '").append(reservation.getReservationDateTime()).append("'");
                    appended = true;
                }
            }

            if (idList.isEmpty() && !appended)
                sqlQuery = new StringBuilder("SELECT * FROM RESERVATION WHERE 1 = 0");

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery(sqlQuery.toString());

            while (resultSet.next()) {
                Reservation newReservation = mapResultToReservation(connection, resultSet);
                listOfReservations.add(newReservation);
            }

            } catch (SQLException | IOException e) {
                dbLogger.error(DB_HEADER, e);
                throw new DatabaseException(DB_HEADER, e);
            }

        return listOfReservations;
    }

    public static void addReservation(Reservation reservation) throws DatabaseException {

        String queryCustomer = "INSERT INTO CUSTOMER(name, surname, email, regular, vip) VALUES (?, ?, ?, ?, ?)";
        String queryReservation = "INSERT INTO RESERVATION(customer_id, reservation_type, guests, date_time) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectToDB()) {

             PreparedStatement customerPreparedStatement = connection.prepareStatement(queryCustomer, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement reservationPreparedStatement = connection.prepareStatement(queryReservation);

            connection.setAutoCommit(false);
            setParameterValuesForCustomer(reservation.getCustomer(), customerPreparedStatement);
            customerPreparedStatement.executeUpdate();

            ResultSet generatedKey = customerPreparedStatement.getGeneratedKeys();

            if (generatedKey.next()) {
                long id = generatedKey.getLong("id");

                reservationPreparedStatement.setLong(1, id);
                reservationPreparedStatement.setString(2, reservation.getReservationType().toString());
                reservationPreparedStatement.setInt(3, reservation.getNumberOfGuests());
                reservationPreparedStatement.setTimestamp(4, Timestamp.valueOf(reservation.getReservationDateTime()));
                reservationPreparedStatement.executeUpdate();

            }
            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void editReservation(Reservation reservation) throws DatabaseException {

        String queryCustomer = "UPDATE CUSTOMER SET name = ?, surname = ?, email = ?, regular = ?, vip = ? WHERE id = ?";
        String queryReservation = "UPDATE RESERVATION SET customer_id = ?, reservation_type = ?, guests = ?, date_time = ? WHERE id = ?";

        try (Connection connection = connectToDB()) {

            PreparedStatement customerPreparedStatement = connection.prepareStatement(queryCustomer);
            PreparedStatement reservationPreparedStatement = connection.prepareStatement(queryReservation);

            connection.setAutoCommit(false);

            setParameterValuesForCustomer(reservation.getCustomer(), customerPreparedStatement);
            customerPreparedStatement.setLong(6, reservation.getCustomer().getId());
            customerPreparedStatement.executeUpdate();

            setParameterValuesForReservation(reservation, reservationPreparedStatement);
            reservationPreparedStatement.setLong(5, reservation.getId());
            reservationPreparedStatement.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    public static void deleteReservation(Reservation reservation) throws DatabaseException {

        try (Connection connection = connectToDB()) {

            PreparedStatement customerPreparedStatement = connection.prepareStatement("DELETE FROM CUSTOMER WHERE id = ?");
            PreparedStatement reservationPreparedStatement = connection.prepareStatement("DELETE FROM RESERVATION WHERE id = ?");

            connection.setAutoCommit(false);

            customerPreparedStatement.setLong(1, reservation.getCustomer().getId());
            customerPreparedStatement.executeUpdate();

            reservationPreparedStatement.setLong(1, reservation.getId());
            reservationPreparedStatement.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
    }

    private static void setParameterValuesForReservation(Reservation reservation, PreparedStatement preparedStatement) throws SQLException {

        preparedStatement.setLong(1, reservation.getCustomer().getId());
        preparedStatement.setString(2, reservation.getReservationType().toString());
        preparedStatement.setInt(3, reservation.getNumberOfGuests());
        preparedStatement.setTimestamp(4, Timestamp.valueOf(reservation.getReservationDateTime()));
    }


    private static Reservation mapResultToReservation(Connection connection, ResultSet resultSet) throws SQLException{

        Long id = resultSet.getLong("id");
        Long idCustomer = resultSet.getLong("customer_id");
        ReservationType reservationType = ReservationType.convertToEnum(resultSet.getString("reservation_type"));
        Integer numberOfGuests = resultSet.getInt("guests");
        LocalDateTime reservationDateTime = resultSet.getObject("date_time", LocalDateTime.class);

        Customer customer = getCustomerByID(connection, idCustomer);

        return new Reservation(id, customer, reservationType, numberOfGuests, reservationDateTime);
    }

    public static Optional<Reservation> getLastReservation() {

        Optional<Reservation> reservation = Optional.empty();

        try (Connection connection = connectToDB()) {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM RESERVATION ORDER BY id DESC LIMIT 1");

            if (resultSet.next())
                reservation = Optional.of(mapResultToReservation(connection, resultSet));

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }
        return reservation;

    }

    public static Integer checkNumberOfReservations() {

        int count = 0;

        try (Connection connection = connectToDB()) {

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery("SELECT COUNT(*) FROM RESERVATION");

            if (resultSet.next())
                count = resultSet.getInt(1);

        } catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }

        return count;

    }

    public static List<LocalDateTime> getTimeStamps() throws DatabaseException {

        List<LocalDateTime> listOfTimeStamps = new ArrayList<>();

        try (Connection connection = connectToDB()) {

            Statement query = connection.createStatement();
            ResultSet resultSet = query.executeQuery("SELECT TIME_OF_CHANGE FROM PERFORMANCE_REVIEW " );

            while (resultSet.next()) {
                LocalDateTime timeStamp = resultSet.getObject("time_of_change", LocalDateTime.class);
                listOfTimeStamps.add(timeStamp);
            }
        }catch (SQLException | IOException e) {
            dbLogger.error(DB_HEADER, e);
            throw new DatabaseException(DB_HEADER, e);
        }

        return listOfTimeStamps;
    }


}
