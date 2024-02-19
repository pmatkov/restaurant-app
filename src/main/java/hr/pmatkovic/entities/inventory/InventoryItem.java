package hr.pmatkovic.entities.inventory;

import hr.pmatkovic.entities.general.Identifier;
import hr.pmatkovic.entities.general.EntityMarker;

import java.io.Serial;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a base class for all inventory items
 */

public abstract class InventoryItem extends Identifier implements EntityMarker {

    @Serial
    private static final long serialVersionUID = -7346112390536818122L;
    private String name;
    private Category category;
    private Double quantity;
    private Double pricePerItem;
    private LocalDate purchaseDate;
    private boolean propertyChanged = false;

    public InventoryItem(Long ID, String name, Category category, Double quantity, Double pricePerItem, LocalDate purchaseDate) {
        super(ID);
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.purchaseDate = purchaseDate;
        this.propertyChanged = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(Double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public boolean isPropertyChanged() {
        return propertyChanged;
    }

    public void setPropertyChanged(boolean propertyChanged) {
        this.propertyChanged = propertyChanged;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(name, that.name) && Objects.equals(category, that.category) && Objects.equals(quantity, that.quantity) && Objects.equals(pricePerItem, that.pricePerItem) && Objects.equals(purchaseDate, that.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, quantity, pricePerItem, purchaseDate);
    }

    public static InventoryItem createInventoryItem(Long id, String name, Category category, Double quantity, Double pricePerItem, Integer bestBefore, LocalDate purchaseDate) {

        if (category != null && Category.isDrink(category))
            return new Drink.Builder(id, name).quantity(quantity).pricePerItem(pricePerItem).purchaseDate(purchaseDate).category(category).build();
        else
            return new Food(id, name, category, quantity, pricePerItem, bestBefore, purchaseDate);

    }

    public static InventoryItem createInventoryItem(Long id, String name, Category category, Double quantity, Double pricePerItem, Integer bestBefore, LocalDate purchaseDate, LocalDate expiryDate) {

        if (category != null && Category.isDrink(category))
            return new Drink.Builder(id, name).quantity(quantity).pricePerItem(pricePerItem).purchaseDate(purchaseDate).category(category).build();
        else
            return new Food(id, name, category, quantity, pricePerItem, bestBefore, purchaseDate, expiryDate);


    }

    public static LocalDate calculateExpiryDate(LocalDate purchaseDate, Integer bestBefore) {

        if (purchaseDate != null && bestBefore != null)
            return purchaseDate.plusDays(bestBefore);
        else
            return null;
    }

    public static void setExpiryDates(List<InventoryItem> items) {

        for (InventoryItem it : items) {
            if (it instanceof Food && ((Food) it).getExpiryDate().equals(LocalDate.of(2023, 1, 1)))
                ((Food) it).setExpiryDate(calculateExpiryDate(it.getPurchaseDate(), ((Food) it).getBestBefore()));

        }
    }

    public static List<InventoryItem> findSoonToExpire(List<InventoryItem> items) {

        Map<InventoryItem, LocalDate> mapItemsDates = new HashMap<>();

        for (InventoryItem item : items) {

            if (item instanceof Food) {

                LocalDate date = calculateExpiryDate(item.getPurchaseDate(), ((Food) item).getBestBefore());
                mapItemsDates.put(item, date);
            }
        }

        return mapItemsDates.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}

