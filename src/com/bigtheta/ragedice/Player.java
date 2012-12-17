package com.bigtheta.ragedice;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Player {
    private String[] tablePlayerColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_GAME_ID,
        MySQLiteHelper.COLUMN_PLAYER_NUMBER,
        MySQLiteHelper.COLUMN_PLAYER_NAME
    };

    long m_id;
    long m_gameId;
    int m_playerNumber;
    String m_playerName;

    public Player(SQLiteDatabase database, Game game, int playerNumber, String playerName) {
        m_gameId = game.getId();
        m_playerNumber = playerNumber;
        m_playerName = playerName;

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_ID, m_gameId);
        values.put(MySQLiteHelper.COLUMN_PLAYER_NUMBER, m_playerNumber);
        values.put(MySQLiteHelper.COLUMN_PLAYER_NAME, m_playerName);
        m_id = database.insert(MySQLiteHelper.TABLE_PLAYER, null, values);
    }

    public Player(SQLiteDatabase database, long id) {
        m_id = id;
        Cursor cursor = getCursor(database);
        m_gameId = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_GAME_ID));
        m_playerNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_NUMBER));
        m_playerName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_NAME));
        cursor.moveToFirst();
        cursor.close();
    }

    public long getId() {
        return m_id;
    }

    public long getGameId() {
        return m_gameId;
    }

    public int getPlayerNumber() {
        return m_playerNumber;
    }

    public String getPlayerName() {
        return m_playerName;
    }

    public static ArrayList<Player> getPlayers(SQLiteDatabase database, Game game) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_PLAYER,
                new String[] {MySQLiteHelper.COLUMN_ID},
                MySQLiteHelper.COLUMN_GAME_ID + " = " + game.getId(),
                null, null, null, null);
        ArrayList<Player> ret = new ArrayList<Player>();
        if (cursor.moveToFirst()) {
            do {
                ret.add(new Player(database, cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private Cursor getCursor(SQLiteDatabase database) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_PLAYER,
                tablePlayerColumns,
                MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

