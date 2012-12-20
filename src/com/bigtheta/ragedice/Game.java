package com.bigtheta.ragedice;

import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Game {
    private static String[] tableGameColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_RANDOM_NUMBER
    };

    long m_id;
    int m_randomNumber;

    public Game(SQLiteDatabase database) {
        Random rand = new Random();
        m_randomNumber = rand.nextInt();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_RANDOM_NUMBER, m_randomNumber);
        m_id = database.insert(MySQLiteHelper.TABLE_GAME, null, values);
    }

    public Game(SQLiteDatabase database, long id) {
        m_id = id;
        Cursor cursor = getCursor(database);
        m_randomNumber = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_RANDOM_NUMBER));
        cursor.moveToFirst();
        cursor.close();
    }

    public long getId() {
        return m_id;
    }

    public int getRandomNumber() {
        return m_randomNumber;
    }


    private Cursor getCursor(SQLiteDatabase database) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_GAME,
                tableGameColumns, MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

