package hr.pmatkovic.entities.staff;

import hr.pmatkovic.entities.general.EntityMarker;
import hr.pmatkovic.entities.general.Identifier;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a performance review class
 */

public class PerformanceReview extends Identifier implements EntityMarker {

    @Serial
    private static final long serialVersionUID = -6241806976090339788L;
    private Employee employee;
    private Employee manager;
    private Grade attendance;
    private Grade quality;
    private Grade achievements;
    private LocalDate reviewDate;

    public PerformanceReview(Employee employee, Employee manager, Grade attendance, Grade quality, Grade achievements, LocalDate reviewDate) {
        this(0l, employee, manager, attendance, quality, achievements, reviewDate);
    }

    public PerformanceReview(Long ID, Employee employee, Employee manager, Grade attendance, Grade quality, Grade achievements, LocalDate reviewDate) {
        super(ID);
        this.employee = employee;
        this.manager = manager;
        this.attendance = attendance;
        this.quality = quality;
        this.achievements = achievements;
        this.reviewDate = reviewDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public Grade getAttendance() {
        return attendance;
    }

    public void setAttendance(Grade attendance) {
        this.attendance = attendance;
    }

    public Grade getQuality() {
        return quality;
    }

    public void setQuality(Grade quality) {
        this.quality = quality;
    }

    public Grade getAchievements() {
        return achievements;
    }

    public void setAchievements(Grade achievements) {
        this.achievements = achievements;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceReview that = (PerformanceReview) o;
        return Objects.equals(employee, that.employee) && Objects.equals(manager, that.manager) && Objects.equals(attendance, that.attendance) && Objects.equals(quality, that.quality) && Objects.equals(achievements, that.achievements) && Objects.equals(reviewDate, that.reviewDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, manager, attendance, quality, achievements, reviewDate);
    }


    public Double calculatePerformance() {

        return (getAttendance().getNumber() + getQuality().getNumber() + getAchievements().getNumber())/ 3.0;

    }
}
