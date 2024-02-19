package hr.pmatkovic.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

/**
 * Implementation of custom date and time picker
 */

public class CustomDateTimePicker extends DatePicker {
    public static final String DEFAULT_FORMAT = "dd.MM.yyyy. HH:mm";
    private DateTimeFormatter formatter;
    private ObjectProperty<LocalDateTime> dateTimeValue = new SimpleObjectProperty(LocalDateTime.now());
    private ObjectProperty<String> format = new SimpleObjectProperty<String>() {
        public void set(String newValue) {
            super.set(newValue);
            formatter = DateTimeFormatter.ofPattern(newValue);
        }
    };

    public void alignColumnCountWithFormat() {
        this.getEditor().setPrefColumnCount(this.getFormat().length());
    }

    public CustomDateTimePicker() {
        this.getStyleClass().add("datetime-picker");
        this.setFormat(DEFAULT_FORMAT);
        this.setConverter(new InternalConverter());
        this.alignColumnCountWithFormat();
        this.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null) {
                this.dateTimeValue.set(null);
            } else if (this.dateTimeValue.get() == null) {
                this.dateTimeValue.set(LocalDateTime.of(newValue, LocalTime.now()));
            } else {
                LocalTime time = this.dateTimeValue.get().toLocalTime();
                this.dateTimeValue.set(LocalDateTime.of(newValue, time));
            }
        });
        this.dateTimeValue.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LocalDate dateValue = newValue.toLocalDate();
                boolean forceUpdate = dateValue.equals(this.valueProperty().get());
                this.setValue(dateValue);
                if (forceUpdate) {
                    this.setConverter(new InternalConverter());
                }
            } else {
                this.setValue(null);
            }

        });
        this.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                this.simulateEnterPressed();
            }
        });
    }

    private void simulateEnterPressed() {
        this.getEditor().commitValue();
    }

    public LocalDateTime getDateTimeValue() {
        return (LocalDateTime)this.dateTimeValue.get();
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        this.dateTimeValue.set(dateTimeValue);
    }

    public ObjectProperty<LocalDateTime> dateTimeValueProperty() {
        return this.dateTimeValue;
    }

    public String getFormat() {
        return (String)this.format.get();
    }

    public ObjectProperty<String> formatProperty() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format.set(format);
        this.alignColumnCountWithFormat();
    }

    class InternalConverter extends StringConverter<LocalDate> {
        InternalConverter() {
        }

        public String toString(LocalDate object) {

            ValidationUtils.setDateValidationStatus(true);

            LocalDateTime value = CustomDateTimePicker.this.getDateTimeValue();
            try {
                   return value != null ? value.format(CustomDateTimePicker.this.formatter) : "";

                } catch (DateTimeParseException e) {
                    ValidationUtils.setDateValidationStatus(false);
                    return "";
                }
        }

        public LocalDate fromString(String value) {

            ValidationUtils.setDateValidationStatus(true);
            try {
                if (value != null && !value.isEmpty()) {
                    CustomDateTimePicker.this.dateTimeValue.set(LocalDateTime.parse(value, CustomDateTimePicker.this.formatter));
                    return (CustomDateTimePicker.this.dateTimeValue.get()).toLocalDate();
                } else {
                    CustomDateTimePicker.this.dateTimeValue.set(null);
                    return null;
                }

            } catch (DateTimeParseException e) {
                ValidationUtils.setDateValidationStatus(false);
                return null;
            }
        }
    }
}

