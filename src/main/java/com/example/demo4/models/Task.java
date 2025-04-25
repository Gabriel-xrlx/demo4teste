package com.example.demo4.models;

import java.time.LocalDate;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;

    public Task() {
        this.status = "A Fazer"; // Valor padrão conforme seu SQL
    }
    public Task(String title, String description, LocalDate dueDate) {
        this(title, description, dueDate, "A Fazer");
    }
    public Task(String title, String description, LocalDate dueDate, String status) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = description; // Pode ser null
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description; // Pode ser null
    }

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
    }

    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(dueDate, task.dueDate) &&
                Objects.equals(status, task.status);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, dueDate, status);
    }

    @Override
    public String toString() {
        return title +" - "+ description +" - " + status + " (Entrega: " + dueDate + ")";
    }
}
