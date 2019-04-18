package com.internshiporganizer.Entities;


public class Goal {
    private long id;
    private Internship internship;
    private Employee employee;
    private String title;
    private String description;
    private String place;
    private boolean completed;
    private String deadline;
    private int attachmentCount;

    public Goal() {
    }

    public Goal(Goal goal) {
        title = goal.getTitle();
        description = goal.getDescription();
        place = goal.getPlace();
        completed = goal.getCompleted();
        deadline = goal.getDeadline();
        internship = goal.getInternship();
        employee = goal.getEmployee();
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
}
