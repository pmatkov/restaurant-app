package hr.pmatkovic.entities.inventory;

import java.io.Serial;
import java.time.LocalDate;

/**
 * Represents a drink inventory item
 */

public class Drink extends InventoryItem {

    @Serial
    private static final long serialVersionUID = -8499568957637889080L;

    public Drink(Builder builder) {
        super(builder.ID, builder.name, builder.category, builder.quantity, builder.pricePerItem, builder.purchaseDate);
    }

    public static class Builder {
        private Long ID;
        private String name;
        private Category category;
        private Double quantity;
        private Double pricePerItem;
        private LocalDate purchaseDate;

        public Builder(Long ID, String name) {
            this.ID = ID;
            this.name = name;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder pricePerItem(Double pricePerItem) {
            this.pricePerItem = pricePerItem;
            return this;
        }

        public Builder purchaseDate(LocalDate purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Drink build() {
            return new Drink(this);
        }
    }

}

