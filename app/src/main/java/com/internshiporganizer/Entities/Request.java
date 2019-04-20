package com.internshiporganizer.Entities;

public class Request {
    private long id;
    private Internship internship;
    private Employee employee;
    private String title;
    private String description;
    private boolean completed;
    private String note;

    public Request(){
    }

    public Request(Request request) {
        title = request.getTitle();
        description = request.getDescription();
        completed = request.getCompleted();
        internship = request.getInternship();
        employee = request.getEmployee();
        note = request.getNote();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Internship getInternship() {
        return internship;
    }

    public void setInternship(Internship internship) {
        this.internship = internship;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
