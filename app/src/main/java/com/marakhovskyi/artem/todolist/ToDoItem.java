package com.marakhovskyi.artem.todolist;


import java.util.Date;

public class ToDoItem {
    public int id;
    public Date creationDate;
    public boolean isCompleted;
    public String title;
    public String details;

    public ToDoItem(int id, String title, String details, Date date, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.creationDate = date;
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return title;
    }
}

