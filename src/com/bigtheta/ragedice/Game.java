package com.bigtheta.ragedice;

import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

public class Game {
    private static String[] tableGameColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_RANDOM_NUMBER
    };

    long m_id;
    int m_randomNumber;

    public Game() {
        Random rand = new Random();
        m_randomNumber = rand.nextInt();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_RANDOM_NUMBER, m_randomNumber);
        m_id = MainActivity.getDatabase().insert(MySQLiteHelper.TABLE_GAME, null, values);
    }

    private Game(long id) {
        m_id = id;
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_GAME,
                tableGameColumns,
                MySQLiteHelper.COLUMN_ID + " = " + Long.toString(m_id),
                null, null, null, null);
        cursor.moveToFirst();
        m_randomNumber = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_RANDOM_NUMBER));
        cursor.moveToFirst();
        cursor.close();
    }

    public static Game retrieve(long id) {
        return new Game(id);
    }

    public static Game getLastGame() {
        Game game;
        String query_str = "SELECT MAX(" + MySQLiteHelper.COLUMN_ID + ") "
                         + "AS _id FROM " + MySQLiteHelper.TABLE_GAME;
        try {
            Cursor cursor = MainActivity.getDatabase().rawQuery(query_str, null);
            cursor.moveToFirst();
            game = Game.retrieve(cursor.getLong(0));
            cursor.close();
        } catch (SQLiteException err) {
            return null;
        }
        return game;
    }

    public long getId() {
        return m_id;
    }

    public int getRandomNumber() {
        return m_randomNumber;
    }
}

