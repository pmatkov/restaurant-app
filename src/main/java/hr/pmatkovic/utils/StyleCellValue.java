package hr.pmatkovic.utils;

import hr.pmatkovic.entities.general.ChangeType;
import hr.pmatkovic.entities.general.ChangedData;
import hr.pmatkovic.entities.service.Reservation;
import hr.pmatkovic.entities.service.ReservationType;
import hr.pmatkovic.entities.staff.Employee;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

import java.util.Map;

/**
 * A utility class for styling text in table cells
 */


public class StyleCellValue<S, T> extends TableCell<S, T>{

    public StyleCellValue(TableColumn<S, T> entryStringTableColumn) {}

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (!isEmpty()) {

            S rowData = getTableRow().getItem();

            if (rowData instanceof Map.Entry) {

                switch (ChangeType.convertToEnum((String)item)) {
                    case ADD:
                        updateStyle("-fx-text-fill: cornflowerblue;");
                        break;
                    case EDIT:
                        updateStyle("-fx-text-fill: grey;");
                        break;
                    case DELETE:
                        updateStyle("-fx-text-fill: red;");
                        break;
                    default:
                        setStyle("");
                        break;
                }
            }
            if (rowData instanceof Reservation)
            {
                switch (ReservationType.convertToEnum((String)item)) {
                    case WEB:
                        updateStyle("-fx-text-fill: cornflowerblue;");
                        break;
                    default:
                        setStyle("");
                        break;
                }
            }
            setText((String)item);
        }
        else {
            setStyle("");
            setText("");
        }
    }

    private void updateStyle(String style) {
        setStyle(style);
        TableRow<S> row = getTableRow();
        row.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle("");
            } else {
                setStyle(style);
            }
        });
    }

}
