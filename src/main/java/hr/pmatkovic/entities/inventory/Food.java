package hr.pmatkovic.entities.inventory;

import java.time.LocalDate;

/**
 * Represents a food inventory item
 */

public class Food extends InventoryItem {

    private Integer bestBefore;
    private LocalDate expiryDate;

    public Food(Long ID, String name, Category category, Double quantity, Double pricePerItem, Integer bestBefore, LocalDate purchaseDate) {
        this(ID, name, category, quantity, pricePerItem, bestBefore, purchaseDate, InventoryItem.calculateExpiryDate(purchaseDate, bestBefore));

    }

    public Food(Long ID, String name, Category category, Double quantity, Double pricePerItem, Integer bestBefore, LocalDate purchaseDate, LocalDate expiryDate) {
        super(ID, name, category, quantity, pricePerItem, purchaseDate);
        this.bestBefore = bestBefore;
        this.expiryDate = expiryDate;
    }

    public Integer getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(Integer bestBefore) {
        this.bestBefore = bestBefore;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
