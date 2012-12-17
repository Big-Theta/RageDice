package com.bigtheta.ragedice;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DiceRoll {
    private String[] tableDiceRollColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_PLAYER_ID
    };

    long m_id;
    long m_playerId;

    public DiceRoll(SQLiteDatabase database, Player player, ArrayList<DieDescription> dieDescriptions) {
        m_playerId = player.getId();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PLAYER_ID, m_playerId);
        m_id = database.insert(MySQLiteHelper.TABLE_DICE_ROLL, null, values);

        for (DieDescription dd : dieDescriptions) {
            new DieResult(database, this, dd);
        }
    }

    public DiceRoll(SQLiteDatabase database, long id) {
        m_id = id;
        Cursor cursor = getCursor(database);
        m_playerId = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_ID));
        cursor.moveToFirst();
        cursor.close();
    }


    public void delete(SQLiteDatabase database) {
        System.out.println("DiceRoll deleted with id: " + m_id);
        database.delete(MySQLiteHelper.TABLE_DICE_ROLL,
                        MySQLiteHelper.COLUMN_ID + " = " + m_id, null);
    }


    public DiceRoll getLastDiceRoll(SQLiteDatabase database) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DICE_ROLL,
                tableDiceRollColumns, null, null, null, null, null);
        // Should this be cursor.getCount() - 1?
        return new DiceRoll(database, (long)cursor.getCount());
    }

    public long getId() {
        return m_id;
    }

    private Cursor getCursor(SQLiteDatabase database) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_DICE_ROLL,
                tableDiceRollColumns, MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

