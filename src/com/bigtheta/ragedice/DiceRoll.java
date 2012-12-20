package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.distribution.KolmogorovSmirnovDistribution;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DiceRoll {
    private static String[] tableDiceRollColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_PLAYER_ID
    };

    long m_id;
    long m_playerId;

    public DiceRoll(SQLiteDatabase database, Player player,
                    ArrayList<DieDescription> dieDescriptions) {
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
        cursor.close();
    }

    public void delete(SQLiteDatabase database) {
        database.delete(MySQLiteHelper.TABLE_DICE_ROLL,
                        MySQLiteHelper.COLUMN_ID + " = " + m_id, null);
    }

    public static void clear(SQLiteDatabase database) {
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

    public static int getNumDiceRolls(SQLiteDatabase database) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DICE_ROLL,
                                       null, null, null, null, null, null);
        return cursor.getCount();
    }

    public static HashMap<Integer, Integer> getObservedRolls(SQLiteDatabase database) {
        HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_DICE_ROLL,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DiceRoll roll = new DiceRoll(database, cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            int resultSum = 0;
            for (DieResult result : DieResult.getDieResults(database, roll)) {
                resultSum += result.getDieResult();
            }

            if (ret.containsKey(resultSum)) {
                ret.put(resultSum, ret.get(resultSum) + 1);
            } else {
                ret.put(resultSum, 1);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return ret;
    }

    /*
     * Determines how likely it is that the dice are fair.
     *
     * References:
     *     http://www.physics.csbsju.edu/stats/KS-test.html
     *    http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test
     */
    public double calculateKSTestStatistic(SQLiteDatabase database) {
        int numRolls = getNumDiceRolls(database);
        if (numRolls == 0) {
            return 0.0;
        }

        // Find the d statistic using the cff (cumulative fraction function).
        // The d statistic is the greatest deviation between the expected cff and
        // the observed cff.
        // Note: we could possibly make this statistic more extreme if we sorted the
        // observed data so that we evaluate all rolls that are less than the expected.
        double d = 0.0;
        double obs_cff = 0.0;
        double exp_cff = 0.0;
        //HashMap<Integer, Float> pmf = DieDescription.getPMF()

      //for (int i = 2; i <=12; i++) {
      //    obs_cff += getCountForRoll(i) / (double)numRolls;
      //    exp_cff += getExpectedCount(i) / (double)numRolls;
      //    if (Math.abs(exp_cff - obs_cff) > d) {
      //        d = Math.abs(exp_cff - obs_cff);
      //    }
      //}
        return -1.0;
    }

    public double calculateKSProbability(SQLiteDatabase database, double testStatistic) {
        int numRolls = getNumDiceRolls(database);
        KolmogorovSmirnovDistribution dist = new KolmogorovSmirnovDistribution(numRolls);
        return dist.cdf(testStatistic);
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

