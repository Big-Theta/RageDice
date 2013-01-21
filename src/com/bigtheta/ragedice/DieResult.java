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
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_DIE_RESULT,
                tableDieResultColumns, MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();        
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

        String description;
        if (dd.getDisplayType().equals(DieDescription.NUMERIC)) {
            Log.e("getImageResource", "1");
            description = dd.getBaseIdentifierName()
                        + Integer.toString(getDieResult());
        } else if (dd.getDisplayType().equals(DieDescription.SHIP)) {
            Log.e("getImageResource", "2");
            if (getDieResult() <= 3) {
                description = dd.getBaseIdentifierName() + "robber";
            } else {
                description = dd.getBaseIdentifierName() + "castle";
            }
        } else {
            Log.e("getImageResource", "3");
            Log.e("DieResult.getImageResource()", "error... displayType is: " + dd.getDisplayType());
            description = "";
        }

        try {
            Field field = res.getField(description);
            retval = field.getInt(null);
        } catch (Exception err){
            Log.e("DieResult::getImageResource()", err.getCause().getMessage());
            throw err;
        }
        return retval;
    }

    public int getImageColorResource() {
        DieDescription dd = DieDescription.retrieve(getDieDescriptionId());
        String type = dd.getDisplayType();
        if (type.equals(DieDescription.NUMERIC)) {
            return dd.getBackgroundColorResource();
        } else if (type.equals(DieDescription.SHIP)) {
            if (getDieResult() <= 3) {
                return R.color.ship_die_castle_blue;
            } else if (getDieResult() == 4) {
                return R.color.ship_die_castle_blue;
            } else if (getDieResult() == 5) {
                return R.color.ship_die_castle_green;
            } else if (getDieResult() == 6) {
                return R.color.ship_die_castle_yellow;
            } else {
                Log.e("DieResult.getImageColor()", "getDieResult not recognized: " +
                                                   Integer.toString(getDieResult()));
                return -1;
            }
        }
        Log.e("DieResult.getImageColor()", "type not found: " + type);
        return -1;
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
}

