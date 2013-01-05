package com.bigtheta.ragedice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.bigtheta.ragedice.R.drawable;

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

    public DieResult(DiceRoll diceRoll,
                     DieDescription dieDescription) {
        m_diceRollId = diceRoll.getId();
        m_dieDescriptionId = dieDescription.getId();
        m_dieResult = rollDie();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DICE_ROLL_ID, m_diceRollId);
        values.put(MySQLiteHelper.COLUMN_DIE_DESCRIPTION_ID, m_dieDescriptionId);
        values.put(MySQLiteHelper.COLUMN_DIE_RESULT, m_dieResult);

        m_id = MainActivity.getDatabase().insert(MySQLiteHelper.TABLE_DIE_RESULT, null, values);
    }

    private DieResult(long id) {
        m_id = id;
        Cursor cursor = getCursor();
        m_diceRollId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DICE_ROLL_ID));
        m_dieDescriptionId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DIE_DESCRIPTION_ID));
        m_dieResult = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DIE_RESULT));
        cursor.moveToFirst();
        cursor.close();
    }

    public static DieResult retrieve(long id) {
        return new DieResult(id);
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

    public int getImageResource() throws Exception {
        Class<drawable> res = R.drawable.class;
        int retval;
        DieDescription dd = DieDescription.retrieve(getDieDescriptionId());
        String description = dd.getBaseIdentifierName()
                           + Integer.toString(getDieResult());
        try {
            Field field = res.getField(description);
            retval = field.getInt(null);
        } catch (Exception err){
            Log.e("DieResult::getImageResource()", err.getCause().getMessage());
            throw err;
        }
        return retval;
    }

    private int rollDie() {
        Random rand = new Random();
        DieDescription dd = DieDescription.retrieve(m_dieDescriptionId);
        int numLowFace = dd.getNumLowFace();
        int numHighFace = dd.getNumHighFace();

        return rand.nextInt(numHighFace - numLowFace + 1) + numLowFace;
    }

    public static ArrayList<DieResult> getDieResults(DiceRoll dr) {
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_DIE_RESULT,
                new String[] {MySQLiteHelper.COLUMN_ID},
                MySQLiteHelper.COLUMN_DICE_ROLL_ID + " = " + dr.getId(),
                null, null, null, null);
        ArrayList<DieResult> ret = new ArrayList<DieResult>();
        if (cursor.moveToFirst()) {
            do {
                ret.add(new DieResult(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private Cursor getCursor() {
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_DIE_RESULT,
                tableDieResultColumns, MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

