package hr.pmatkovic.entities.general;

import hr.pmatkovic.entities.staff.Employee;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a generic entity for tracking business changes, including inventory items, employees and reservations
 *  @param <T> changed object (eg. inventory item, employee or reservation)
 * @param <S>  employee authorized for this change
 */

public class ChangedData<T extends EntityMarker, S extends Employee> implements Serializable {

    @Serial
    private static final long serialVersionUID = 8171692634883333376L;
    private T changedObject;
    private S changedBy;
    private ChangeType changeType;
    private LocalDateTime timeOfChange;

    public ChangedData(T changedObject, S changedBy, ChangeType changeType, LocalDateTime timeOfChange) {
        this.changedObject = changedObject;
        this.changedBy = changedBy;
        this.changeType = changeType;
        this.timeOfChange = timeOfChange;
    }

    public T getChangedObject() {
        return changedObject;
    }

    public void setChangedObject(T changedObject) {
        this.changedObject = changedObject;
    }

    public S getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(S changedBy) {
        this.changedBy = changedBy;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getTimeOfChange() {
        return timeOfChange;
    }

    public void setTimeOfChange(LocalDateTime timeOfChange) {
        this.timeOfChange = timeOfChange;
    }

}
