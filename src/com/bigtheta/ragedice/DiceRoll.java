package com.bigtheta.ragedice;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DiceRoll {
    private static String[] tableDiceRollColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_PLAYER_ID
    };

    long m_id;
    long m_playerId;

    public DiceRoll(SQLiteDatabase database, Player player,
                    ArrayList<DieDescription> dieDescriptions) {
        Log.i("DiceRoll created with id", Long.toString(player.getId()));
        m_playerId = player.getId();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PLAYER_ID, m_playerId);
        m_id = database.insert(MySQLiteHelper.TABLE_DICE_ROLL, null, values);

        for (DieDescription dd : dieDescriptions) {
            new DieResult(database, this, dd);
        }
    }

    public DiceRoll(SQLiteDatabase database, long id) {
        Log.i("DiceRoll retrieved with id", Long.toString(id));
        m_id = id;
        Cursor cursor = getCursor(database);
        m_playerId = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_ID));
        cursor.close();
    }


    public void delete(SQLiteDatabase database) {
        Log.i("DiceRoll::delete DiceRoll deleted with id", Long.toString(m_id));
        database.delete(MySQLiteHelper.TABLE_DICE_ROLL,
                        MySQLiteHelper.COLUMN_ID + " = " + m_id, null);
    }

    public static void clear(SQLiteDatabase database) {
        Log.i("DiceRoll::clear", "...");
        database.delete(MySQLiteHelper.TABLE_DICE_ROLL, null, null);
    }

    public static DiceRoll getLastDiceRoll(SQLiteDatabase database) {
        DiceRoll ret;
        if (!isEmpty(database)) {
            String query_str = "SELECT MAX(" + MySQLiteHelper.COLUMN_ID + ") "
                             + "AS _id FROM " + MySQLiteHelper.TABLE_DICE_ROLL;
            Cursor cursor = database.rawQuery(query_str, null);
            cursor.moveToFirst();
            ret = new DiceRoll(database, cursor.getLong(0));
            cursor.close();
        } else {
            ret = null;
        }
        return ret;
    }

    public long getId() {
        return m_id;
    }

    public long getPlayerId() {
        return m_playerId;
    }

    private static boolean isEmpty(SQLiteDatabase database) {
        boolean retval;
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_DICE_ROLL,
                tableDiceRollColumns,
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            retval = false;
        } else {
            retval = true;
        }
        cursor.close();
        return retval;
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

