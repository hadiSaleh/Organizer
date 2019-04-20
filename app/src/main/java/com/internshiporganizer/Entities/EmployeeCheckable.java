package com.internshiporganizer.Entities;

public class EmployeeCheckable extends Employee {
    private boolean checked;

    public EmployeeCheckable() {

    }

    public EmployeeCheckable(Employee employee) {
        this.setId(employee.getId());
        this.setFirstName(employee.getFirstName());
        this.setLastName(employee.getLastName());
        this.setEmail(employee.getEmail());
        this.setCity(employee.getCity());
        this.setAdministrator(employee.getAdministrator());
        this.checked = false;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
