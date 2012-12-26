package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DieResult {
    private static String[] tableDieResultColumns = {
        MySQLiteHelper.COLUMN_DICE_ROLL_ID,
        MySQLiteHelper.COLUMN_DIE_DESCRIPTION_ID,
        MySQLiteHelper.COLUMN_DIE_RESULT
    };

    long m_id;
    long m_diceRollId;
    long m_dieDescriptionId;
    int m_dieResult;

    public DieResult(SQLiteDatabase database, DiceRoll diceRoll,
                     DieDescription dieDescription) {
        m_diceRollId = diceRoll.getId();
        m_dieDescriptionId = dieDescription.getId();
        m_dieResult = rollDie(database);

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DICE_ROLL_ID, m_diceRollId);
        values.put(MySQLiteHelper.COLUMN_DIE_DESCRIPTION_ID, m_dieDescriptionId);
        values.put(MySQLiteHelper.COLUMN_DIE_RESULT, m_dieResult);

        m_id = database.insert(MySQLiteHelper.TABLE_DIE_RESULT, null, values);
    }

    public DieResult(SQLiteDatabase database, long id) {
        m_id = id;
        Cursor cursor = getCursor(database);
        m_diceRollId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DICE_ROLL_ID));
        m_dieDescriptionId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DIE_DESCRIPTION_ID));
        m_dieResult = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DIE_RESULT));
        cursor.moveToFirst();
        cursor.close();
    }

    public long getId() {
        return m_id;
    }

    public long getDieDescriptionId() {
        return m_dieDescriptionId;
    }

    public int getDieResult() {
        return m_dieResult;
    }

    private int rollDie(SQLiteDatabase database) {
        Random rand = new Random();
        DieDescription dd = new DieDescription(database, m_dieDescriptionId);
        int numLowFace = dd.getNumLowFace();
        int numHighFace = dd.getNumHighFace();

        return rand.nextInt(numHighFace - numLowFace + 1) + numLowFace;
    }

    public static ArrayList<DieResult> getDieResults(SQLiteDatabase database,
                                                     DiceRoll dr) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_DIE_RESULT,
                new String[] {MySQLiteHelper.COLUMN_ID},
                MySQLiteHelper.COLUMN_DICE_ROLL_ID + " = " + dr.getId(),
                null, null, null, null);
        ArrayList<DieResult> ret = new ArrayList<DieResult>();
        if (cursor.moveToFirst()) {
            do {
                ret.add(new DieResult(database, cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private Cursor getCursor(SQLiteDatabase database) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_DIE_RESULT,
                tableDieResultColumns, MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

