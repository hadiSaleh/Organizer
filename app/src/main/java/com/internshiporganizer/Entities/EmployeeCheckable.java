package com.internshiporganizer.Entities;

public class EmployeeCheckable extends Employee {
    private boolean checked;

    public boolean getChecked(){
        return checked;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
    }
}
