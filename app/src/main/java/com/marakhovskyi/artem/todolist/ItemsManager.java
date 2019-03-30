package com.marakhovskyi.artem.todolist;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemsManager {

    private final DBHelper db;

    public ItemsManager(DBHelper db) {
        this.db = db;
    }

    public List<ToDoItem> getItems() {
        Cursor cursor = getCursorForTable("items", "creation_date desc");

        if (cursor.getCount() == 0)
            return new ArrayList<ToDoItem>();

        ArrayList<ToDoItem> result = new ArrayList<ToDoItem>();

        int idIdx = cursor.getColumnIndex("id");
        int detailsIdx = cursor.getColumnIndex("details");
        int titleIdx = cursor.getColumnIndex("title");
        int creationDateIdx = cursor.getColumnIndex("creation_date");
        int isCompletedIdx = cursor.getColumnIndex("is_completed");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIdx);
                String details = cursor.getString(detailsIdx);
                String title = cursor.getString(titleIdx);
                long creationDate = cursor.getLong(creationDateIdx);
                int isCompleted = cursor.getInt(isCompletedIdx);

                ToDoItem item = new ToDoItem(
                        id,
                        title,
                        details,
                        new Date(creationDate),
                        isCompleted == 1
                );

                result.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return result;
    }


    public ToDoItem getItem(int id) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "select id, title, details, creation_date, is_completed" +
                        " from items where id="+String.valueOf(id), null);

        int idIdx = cursor.getColumnIndex("id");
        int detailsIdx = cursor.getColumnIndex("details");
        int titleIdx = cursor.getColumnIndex("title");
        int creationDateIdx = cursor.getColumnIndex("creation_date");
        int isCompletedIdx = cursor.getColumnIndex("is_completed");
        if (cursor.moveToFirst()) {
            String details = cursor.getString(detailsIdx);
            String title = cursor.getString(titleIdx);
            long creationDate = cursor.getLong(creationDateIdx);
            int isCompleted = cursor.getInt(isCompletedIdx);

            ToDoItem item = new ToDoItem(
                    id,
                    title,
                    details,
                    new Date(creationDate),
                    isCompleted == 1
            );
            return item;
        }

        return null;
    }

    private Cursor getCursorForTable(String table, String orderBy) {
        return db.getReadableDatabase()
                .query(
                        table,
                        null,
                        "is_completed = 0",
                        null,
                        null,
                        null,
                        orderBy,
                        null);
    }

    public void upsert(ToDoItem toDoItem) {

        ContentValues cv = new ContentValues();
        long millisicondsFrom1970 = toDoItem.creationDate.getTime();
        cv.put("creation_date", millisicondsFrom1970);
        cv.put("title", toDoItem.title);
        cv.put("details", toDoItem.details);
        cv.put("is_completed", toDoItem.isCompleted);

        if (toDoItem.id == 0) {
            db.getWritableDatabase().insert("items", null, cv);
        } else {
            cv.put("id", toDoItem.id );
            db.getWritableDatabase().update(
                 "items",
                 cv,
                 "id = "+String.valueOf(toDoItem.id),
                 null);
        }
    }
}