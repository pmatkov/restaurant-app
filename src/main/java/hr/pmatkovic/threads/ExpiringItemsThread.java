package hr.pmatkovic.threads;

import hr.pmatkovic.entities.inventory.InventoryItem;
import hr.pmatkovic.exceptions.DatabaseException;
import hr.pmatkovic.exceptions.DepartmentCreationException;
import hr.pmatkovic.utils.DatabaseUtils;
import hr.pmatkovic.utils.GeneralUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

import static hr.pmatkovic.entities.inventory.InventoryItem.findSoonToExpire;
import static hr.pmatkovic.utils.ControllerUtils.displayGeneralDialog;
import static hr.pmatkovic.utils.GeneralUtils.DB_TEXT;

/**
 * A thread which displays a list of expiring items
 */

public class ExpiringItemsThread implements Runnable {

    ListView<InventoryItem> soonToExpireListView;

    private List<InventoryItem> soonToExpireItems = new ArrayList<>();
    private ObservableList<InventoryItem> itemsObservableList;
    private boolean initialized = false;
    private boolean changed = false;

    public ExpiringItemsThread(ListView<InventoryItem> soonToExpireListView, ObservableList<InventoryItem> itemsObservableList) {
        this.soonToExpireListView = soonToExpireListView;
        this.itemsObservableList = itemsObservableList;
    }

    @Override
    public void run() {

        if (!initialized) {
            itemsObservableList.addListener((ListChangeListener<InventoryItem>) change -> {
                if (change.next()) {
                    changed = true;
                }
            });
        }

        if (!initialized || changed) {

            soonToExpireItems = findSoonToExpire(FXCollections.observableArrayList(itemsObservableList));

            soonToExpireListView.getItems().clear();
            soonToExpireListView.setItems(FXCollections.observableArrayList(soonToExpireItems));
            initialized = true;
            changed = false;

        }
    }
}
