package com.bigtheta.ragedice;

import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DieResult {
    private String[] tableDieResultColumns = {
        MySQLiteHelper.COLUMN_DICE_ROLL_ID,
        MySQLiteHelper.COLUMN_DIE_DESCRIPTION_ID,
        MySQLiteHelper.COLUMN_DIE_RESULT
    };

    long m_id;
    long m_diceRollId;
    long m_dieDescriptionId;
    int m_dieResult;

    public DieResult(SQLiteDatabase database, DiceRoll diceRoll, DieDescription dieDescription) {
        m_diceRollId = diceRoll.getId();
        m_dieDescriptionId = dieDescription.getId();
        m_dieResult = rollDie();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NUM_LOW_FACE, m_numLowFace);
        values.put(MySQLiteHelper.COLUMN_NUM_HIGH_FACE, m_numHighFace);
        values.put(MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME, m_baseIdentifierName);
        values.put(MySQLiteHelper.COLUMN_BACKGROUND_COLOR, m_backgroundColor);
        long insertId = database.insert(MySQLiteHelper.TABLE_DIE_DESCRIPTION, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                tableDieDescriptionColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);

        m_id = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID));

        cursor.moveToFirst();
        cursor.close();
    }

    public DieResult(Cursor cursor) {
        
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        m_id = id;
    }

    private int rollDie() {
        Random rand = new Random();
        int numLowFace = m_dieDescription.getNumLowFace;
        int numHighFace = m_dieDescription.getNumHighFace;

        return rand.nextInt(numHighFace - numLowFace + 1) + numLowFace;
    }
}

