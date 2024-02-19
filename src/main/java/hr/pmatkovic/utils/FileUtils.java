package hr.pmatkovic.utils;

import hr.pmatkovic.entities.general.*;
import hr.pmatkovic.entities.inventory.*;
import hr.pmatkovic.entities.service.*;
import hr.pmatkovic.entities.staff.*;
import hr.pmatkovic.exceptions.IllegalEnumArgumentException;
import hr.pmatkovic.exceptions.DeserializationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static hr.pmatkovic.utils.GeneralUtils.*;

/**
 * A utility class containg methods that support file operations
 */

public class FileUtils {

    private static final int NUMBER_OF_ENTRIES_RESTAURANT = 4;
    private static final int NUMBER_OF_ENTRIES_EMPLOYEE = 9;
    private static final int NUMBER_OF_ENTRIES_PERFORMANCE_REVIEW = 8;
    private static final int NUMBER_OF_ENTRIES_FOOD = 8;
    private static final int NUMBER_OF_ENTRIES_DRINKS = 7;
    private static final int NUMBER_OF_ENTRIES_RESERVATION = 11;

    public static void main(String[] args) {

        RestaurantInfo restaurantInfo = getRestaurantInfo();
        List<Employee> employees = getEmployees();
        List<PerformanceReview> performanceReviews = getPerformanceReview(employees);
        List<InventoryItem> inventoryItems = getInventoryItems();
        List<Reservation> reservations = getReservations();

    }

    public static RestaurantInfo getRestaurantInfo() {

        Optional<RestaurantInfo> restaurantInfo = Optional.empty();

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/info.txt"))) {

            List<String> restaurantDat = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < restaurantDat.size(); i += NUMBER_OF_ENTRIES_RESTAURANT) {
                String name = restaurantDat.get(i);
                String address = restaurantDat.get(i + 1);
                String city = restaurantDat.get(i + 2);

                restaurantInfo = Optional.of(new RestaurantInfo(name, address, city));
            }

        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }

        return restaurantInfo.get();
    }

    public static List<Employee> getEmployees() {

        List<Employee> employees = new ArrayList<>();

        System.out.println("Reading employees from a file…");

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/employees.txt"))) {

            List<String> employeesDat = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < employeesDat.size(); i += NUMBER_OF_ENTRIES_EMPLOYEE) {

                Long id = Long.parseLong(employeesDat.get(i));
                String name = employeesDat.get(i + 1);
                String surname = employeesDat.get(i + 2);
                Long pin = Long.parseLong(employeesDat.get(i + 3));
                LocalDate birthdate = LocalDate.parse(employeesDat.get(i + 4), DATE_FORMATTER);
                Position position = Position.convertToEnum(Integer.parseInt(employeesDat.get(i + 5)));
                Double salary = Double.parseDouble(employeesDat.get(i + 6));
                Integer experience = Integer.parseInt(employeesDat.get(i + 7));

                DepartmentName departmentName = DepartmentName.convertPositionToDepartment(position);
                Rank rank = Rank.convertToEnum(position, experience);

                if (rank.equals(Rank.MANAGER) || rank.equals(Rank.GENERAL_MANAGER))
                    employees.add(new Manager(id, name, surname, pin, birthdate, departmentName, position, rank, salary, experience));
                else
                    employees.add(new RegularEmployee(id, name, surname, pin, birthdate, departmentName, position, rank, salary, experience));
            }
        } catch (IllegalArgumentException | IllegalEnumArgumentException e) {
            System.out.println("Illegal argument found.");
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }

        return employees;
    }

    public static List<PerformanceReview> getPerformanceReview(List<Employee> employees) {

        List<PerformanceReview> performanceReviews = new ArrayList<>();

        System.out.println("Reading performance reviews from a file…");

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/performance.txt"))) {

            List<String> performanceDat = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < performanceDat.size(); i += NUMBER_OF_ENTRIES_PERFORMANCE_REVIEW) {

                Long id = Long.parseLong(performanceDat.get(i + 0));
                Long idEmployee = Long.parseLong(performanceDat.get(i + 1));
                Long idManager = Long.parseLong(performanceDat.get(i + 2));
                Grade attendance = Grade.convertToEnum(Integer.parseInt(performanceDat.get(i + 3)));
                Grade workQuality = Grade.convertToEnum(Integer.parseInt(performanceDat.get(i + 4)));
                Grade achievements = Grade.convertToEnum(Integer.parseInt(performanceDat.get(i + 5)));
                LocalDate reviewDate = LocalDate.parse(performanceDat.get(i + 6), DATE_FORMATTER);

                Employee employee = employees.stream().filter(e -> e.getId().equals(idEmployee)).collect(Collectors.toList()).get(0);
                Manager manager = (Manager) employees.stream().filter(e -> e.getId().equals(idManager)).collect(Collectors.toList()).get(0);

                performanceReviews.add(new PerformanceReview(id, employee, manager, attendance, workQuality, achievements, reviewDate));
            }
        } catch (IllegalArgumentException | IllegalEnumArgumentException e) {
            System.out.println("Illegal argument found.");
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }

        return performanceReviews;
    }

    public static List<InventoryItem> getInventoryItems() {

        List<InventoryItem> inventoryItems = new ArrayList<>();

        System.out.println("Reading food from a file…");

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/food.txt"))) {

            List<String> foodDat = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < foodDat.size(); i += NUMBER_OF_ENTRIES_FOOD) {

                Long idFood = Long.parseLong(foodDat.get(i));
                String name = foodDat.get(i + 1);
                Double quantity = Double.parseDouble(foodDat.get(i + 2));
                Double pricePerItem = Double.parseDouble(foodDat.get(i + 3));
                LocalDate purchaseDate = LocalDate.parse(foodDat.get(i + 4), DATE_FORMATTER);
                Category category = Category.convertToEnum(Integer.parseInt(foodDat.get(i + 5)));
                Integer bestBefore = Integer.parseInt(foodDat.get(i + 6));

                InventoryItem item = InventoryItem.createInventoryItem(idFood, name, category, quantity, pricePerItem, bestBefore, purchaseDate);

                inventoryItems.add(item);
            }
        } catch (IllegalArgumentException | IllegalEnumArgumentException e) {
            System.out.println("Illegal argument found.");
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }

        System.out.println("Reading drinks from a file…");

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/drinks.txt"))) {

            List<String> drinksDat = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < drinksDat.size(); i += NUMBER_OF_ENTRIES_DRINKS) {

                Long idDrink = Long.parseLong(drinksDat.get(i));
                String name = drinksDat.get(i + 1);
                Double quantity = Double.parseDouble(drinksDat.get(i + 2));
                Double pricePerItem = Double.parseDouble(drinksDat.get(i + 3));
                LocalDate purchaseDate = LocalDate.parse(drinksDat.get(i + 4), DATE_FORMATTER);
                Category category = Category.convertToEnum(Integer.parseInt(drinksDat.get(i + 5)));

                InventoryItem item = InventoryItem.createInventoryItem(idDrink, name, category, quantity, pricePerItem, 0, purchaseDate);

                inventoryItems.add(item);
            }
        } catch (IllegalArgumentException | IllegalEnumArgumentException e) {
            System.out.println("Illegal argument found.");
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }

        return inventoryItems;
    }

    public static List<Reservation> getReservations() {

        List<Reservation> reservations = new ArrayList<>();

        System.out.println("Reading reservations from a file…");

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/reservations.txt"))) {

            List<String> reservationDat = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < reservationDat.size(); i += NUMBER_OF_ENTRIES_RESERVATION) {

                Long id = Long.parseLong(reservationDat.get(i + 0));
                Long idCustomer = Long.parseLong(reservationDat.get(i + 1));
                String name = reservationDat.get(i + 2);
                String surname = reservationDat.get(i + 3);
                Long pin = Long.parseLong(reservationDat.get(i + 4));
                LocalDate birthdate = LocalDate.parse(reservationDat.get(i + 5), DATE_FORMATTER);
                Boolean regularCustomer = Boolean.parseBoolean(reservationDat.get(i + 6));
                Boolean vipCustomer = Boolean.parseBoolean(reservationDat.get(i + 7));
                Integer table = Integer.parseInt(reservationDat.get(i + 8));
                LocalDateTime dateTime = LocalDateTime.parse(reservationDat.get(i + 9), DATE_TIME_FORMATTER);

                reservations.add(new Reservation(id, new Customer(idCustomer, name, surname, pin, birthdate, regularCustomer, vipCustomer), table, dateTime));
            }
        } catch (IllegalArgumentException | IllegalEnumArgumentException e) {
            System.out.println("Illegal argument found.");
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }
        return reservations;
    }


    public static void saveEmployees(List<Employee> employees) {

        System.out.println("Saving employees to a file…");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dat/employees.txt"))) {
            for (int i = 0; i < employees.size(); i++) {

                StringBuilder sb = new StringBuilder();
                sb.append(employees.get(i).getId() + "\n" + employees.get(i).getName() + "\n" + employees.get(i).getSurname() + "\n" + employees.get(i).getPin() + "\n" + employees.get(i).getBirthdate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")) + "\n" + employees.get(i).getPosition().getNumber() + "\n" + employees.get(i).getSalary() + "\n" + employees.get(i).getExperience() + "\n" + "#####" + "\n");
                writer.write(sb.toString());
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println("Problem saving file.");
            logger.error(e.getMessage(), e);
        }
    }

    public static Map<String, String> getUsersData() {

        Map<String, String> mapOfUsernamesAndPasswords = new HashMap<>();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader("dat/users.txt"))) {

            while ((line = reader.readLine()) != null) {
                String[] usersData = line.split(": ", 2);
                if (usersData.length > 1) {
                    String key = usersData[0];
                    String hash = usersData[1];
                    mapOfUsernamesAndPasswords.putIfAbsent(key, hash);
                }
            }
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            logger.error(e.getMessage(), e);
        }

        return mapOfUsernamesAndPasswords;
    }


    public static <T extends EntityMarker, S extends Employee> void serializeData(Map<T, ChangedData<T, S>> dataMap, Class<T> classType) {

        String pathAsString = getPath(classType);

        try {
            Path path = Paths.get(pathAsString);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
                ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(pathAsString));
                writer.writeObject(dataMap);
                System.out.println("Serialization complete.");
            }
            catch (IOException e) {
            System.out.println("Problem with serialization.");
            logger.error(e.getMessage(), e);
            }

    }

    public static <T extends EntityMarker, S extends Employee> Map<T, ChangedData<T, S>> deserializeData(Class<T> classType) throws DeserializationException {

        Map<T, ChangedData<T, S>> dataMap;
        String pathAsString = getPath(classType);

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(pathAsString))) {

            dataMap = (Map<T, ChangedData<T, S>>) reader.readObject();;
            System.out.println("Deserialization complete.");

            return dataMap;

        } catch (IOException e) {
            System.out.println("Problem with deserialization.");
            logger.error(e.getMessage(), e);
            throw new DeserializationException();

        } catch (ClassNotFoundException e) {
            System.out.println("Serialized object class can't be found.");
            logger.error(e.getMessage(), e);
            throw new DeserializationException();
        }
    }

    private static<T> String getPath(Class<T> classType) {

        final String LAST = "History.dat";
        String path = "";

        if (classType.equals(Employee.class))
            path = "dat/employees" + LAST;
        else if (classType.equals(PerformanceReview.class))
            path = "dat/performance" + LAST;
        else if (classType.equals(InventoryItem.class))
            path = "dat/inventory" + LAST;
        else if (classType.equals(Reservation.class))
            path = "dat/reservation" + LAST;

        return path;

    }
}


