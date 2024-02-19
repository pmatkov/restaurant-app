package hr.pmatkovic.entities.staff;

import hr.pmatkovic.entities.general.EntityMarker;
import hr.pmatkovic.entities.general.DepartmentName;
import hr.pmatkovic.entities.general.Person;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a base employee class
 */

public abstract class Employee extends Person implements EntityMarker {

    @Serial
    private static final long serialVersionUID = -6282837579206209654L;
    private DepartmentName department;
    private Position position;
    private Rank rank;
    private Double salary;
    private Double bonus;
    private Boolean bonusReviewed;
    private Integer experience;
    private LocalDate employmentStartDate;
    private Double performance;
    private Boolean performanceReviewed;

    private static Long maxID = 0L;

    public Employee(Long ID, String name, String surname, Long pin, LocalDate birthdate, DepartmentName departmentName, Position position, Rank rank, Double salary,Integer experience, Double performance) {
        super(ID, name, surname, pin, birthdate);
        this.department = departmentName;
        this.position = position;
        this.rank = rank;
        this.salary = salary;
        this.bonus = 0.0;
        this.bonusReviewed = false;
        this.experience = experience;
        this.performance = performance;
        this.performanceReviewed = false;

        if (ID.compareTo(maxID) > 0)
            maxID = ID;

    }

    public DepartmentName getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentName department) {
        this.department = department;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public Boolean getBonusReviewed() {
        return bonusReviewed;
    }

    public void setBonusReviewed(Boolean bonusReviewed) {
        this.bonusReviewed = bonusReviewed;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public LocalDate getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(LocalDate employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public Double getPerformance() {
        return performance;
    }

    public void setPerformance(Double performance) {
        this.performance = performance;
    }

    public Boolean getPerformanceReviewed() {
        return performanceReviewed;
    }

    public void setPerformanceReviewed(Boolean performanceReviewed) {
        this.performanceReviewed = performanceReviewed;
    }

    public static Long getMaxID()
    {
        return maxID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;

        return Objects.equals(getName(), employee.getName()) && Objects.equals(getSurname(), employee.getSurname()) && Objects.equals(getPin(), employee.getPin()) && Objects.equals(getBirthdate(), employee.getBirthdate()) && Objects.equals(getPosition(), employee.getPosition()) && Objects.equals(getRank(), employee.getRank()) && Objects.equals(getSalary(), employee.getSalary()) && Objects.equals(getExperience(), employee.getExperience());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPin());
    }

    @Override
    public String toString() {
        return getName() + " " + getSurname();
    }


    public static Employee createEmployee(Long id, String name, String surname, Long pin, LocalDate birthdate, DepartmentName department, Position position, Rank rank, Double salary, Integer experience) {

        if (position != null && (Position.isGeneralManager(position) || Position.isManager(position)))
            return new Manager(id, name, surname, pin, birthdate, department, position, rank, salary, experience);
        else
            return new RegularEmployee(id, name, surname, pin, birthdate, department, position, rank, salary, experience);
    }

    public static Employee createEmployee(Long id, String name, String surname, Long pin, LocalDate birthdate, DepartmentName department, Position position, Rank rank, Double salary, Integer experience, Double performance) {

        if (position != null && (Position.isGeneralManager(position) || Position.isManager(position)))
            return new Manager(id, name, surname, pin, birthdate, department, position, rank, salary, experience, performance);
        else
            return new RegularEmployee(id, name, surname, pin, birthdate, department, position, rank, salary, experience, performance);
    }

    public static void setEmployeesBonus(List<Employee> employees) {

        Optional<Double> bonus = Optional.empty();

        for (Employee emp : employees) {
            if (emp instanceof RegularEmployee) {
                bonus = ((RegularEmployee)emp).calculateBonus(emp);

            } else if (emp instanceof Manager){
                bonus = ((Manager)emp).calculateBonus(emp);
            }
            bonus.ifPresent(emp::setBonus);
            emp.setBonusReviewed(true);
        }
    }


}
