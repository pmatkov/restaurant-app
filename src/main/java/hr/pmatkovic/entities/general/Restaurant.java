package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.PerformanceReview;
import hr.pmatkovic.entities.inventory.InventoryItem;
import hr.pmatkovic.entities.service.Reservation;
import hr.pmatkovic.entities.staff.Employee;

import java.util.List;

/**
 * Represents a wrapper for all entities within a restaurant
 */

public class Restaurant {

    private RestaurantInfo restaurantInfo;
    private Integer capacity;
    private Organisation organisation;
    private List<Employee> employees;
    private List<PerformanceReview> performanceReviews;
    private List<InventoryItem> inventoryItems;
    private List<Reservation> reservations;

    public Restaurant(RestaurantInfo restaurantInfo, Integer capacity, Organisation organisation, List<Employee> employees, List<PerformanceReview> performanceReviews, List<InventoryItem> inventoryItems, List<Reservation> reservations) {
        this.restaurantInfo = restaurantInfo;
        this.capacity = capacity;
        this.organisation = organisation;
        this.employees = employees;
        this.performanceReviews = performanceReviews;
        this.inventoryItems = inventoryItems;
        this.reservations = reservations;
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }
}
