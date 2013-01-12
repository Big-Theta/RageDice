package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.distribution.KolmogorovSmirnovDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DiceRoll {
    private static String[] tableDiceRollColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_PLAYER_ID
    };

    long m_id;
    long m_playerId;

    // This opens an oportunity to break... if the database is cleared, this won't be.
    private static HashMap<Long, HashMap<Integer, Integer> > cacheGetObservedRolls = null;

    public DiceRoll(Player player) {
        m_playerId = player.getId();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PLAYER_ID, m_playerId);
        m_id = MainActivity.getDatabase().insert(MySQLiteHelper.TABLE_DICE_ROLL, null, values);

        for (DieDescription dd : DieDescription.retrieveAll(Player.retrieve(m_playerId).getGameId())) {
            new DieResult(this, dd);
        }

        long key = Player.retrieve(m_playerId).getGameId();
        if (cacheGetObservedRolls != null && cacheGetObservedRolls.containsKey(key)) {
            int result = getTotalResult();
            HashMap<Integer, Integer> toUpdate = cacheGetObservedRolls.get(key);
            if (!toUpdate.containsKey(result)) {
                toUpdate.put(result, 1);
            } else {
                toUpdate.put(result, toUpdate.get(result) + 1);
            }
        }
    }

    private DiceRoll(long id) {
        m_id = id;
        Cursor cursor = getCursor();
        m_playerId = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_ID));
        cursor.close();
    }

    public static DiceRoll retrieve(long id) {
        return new DiceRoll(id);
    }

    public int getTotalResult() {
        Integer result = 0;
        for (DieResult dr : DieResult.getDieResults(this)) {
            result += dr.getDieResult();
        }
        return result;
    }

    public void delete() {
        long key = Player.retrieve(m_playerId).getGameId();
        if (cacheGetObservedRolls != null && cacheGetObservedRolls.containsKey(key)) {
            int result = getTotalResult();
            HashMap<Integer, Integer> toUpdate = cacheGetObservedRolls.get(key);
            Log.i("removing... result", Integer.toString(result));
            Log.i("removing... old observations", Integer.toString(toUpdate.get(result)));
            toUpdate.put(result, toUpdate.get(result) - 1);
        } else {
            Log.i("didn't contain key.", "...");
        }

        MainActivity.getDatabase().delete(
                MySQLiteHelper.TABLE_DICE_ROLL,
                MySQLiteHelper.COLUMN_ID + " = " + m_id, null);
    }

    public static void clear() {
        cacheGetObservedRolls = null;
        MainActivity.getDatabase().delete(MySQLiteHelper.TABLE_DICE_ROLL, null, null);
    }

    public static DiceRoll getLastDiceRoll(long gameId) {
        DiceRoll ret;
        if (!isEmpty()) {
            String query_str = "SELECT MAX(" + MySQLiteHelper.COLUMN_ID + ") "
                             + "AS _id FROM " + MySQLiteHelper.TABLE_DICE_ROLL;
            try {
                Cursor cursor = MainActivity.getDatabase().rawQuery(query_str, null);
                cursor.moveToFirst();
                ret = DiceRoll.retrieve(cursor.getLong(0));
                cursor.close();
            } catch (SQLiteException err) {
                return null;
            }
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

    public static int getNumDiceRolls() {
        Cursor cursor = MainActivity.getDatabase().query(MySQLiteHelper.TABLE_DICE_ROLL,
                                                         null, null, null, null, null, null);
        return cursor.getCount();
    }

    public static HashMap<Integer, Integer> getObservedRolls(long gameId) {
        if (cacheGetObservedRolls == null || !cacheGetObservedRolls.containsKey(gameId)) {
            HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
            Cursor cursor = MainActivity.getDatabase().query(
                    MySQLiteHelper.TABLE_DICE_ROLL,
                    null, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                DiceRoll roll = new DiceRoll(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
                int result = roll.getTotalResult();

                if (ret.containsKey(result)) {
                    ret.put(result, ret.get(result) + 1);
                } else {
                    ret.put(result, 1);
                }
                cursor.moveToNext();
            }
            cursor.close();
            if (cacheGetObservedRolls == null) {
                cacheGetObservedRolls = new HashMap<Long, HashMap<Integer, Integer> >();
            }
            cacheGetObservedRolls.put(gameId, ret);
            return ret;
        } else {
            return cacheGetObservedRolls.get(gameId);
        }
    }

    /*
     * Determines how likely it is that the dice are fair.
     *
     * References:
     *     http://www.physics.csbsju.edu/stats/KS-test.html
     *    http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test
     */
    public static double calculateKSTestStatistic(long gameId) {
        int numRolls = getNumDiceRolls();
        if (numRolls == 0) {
            return 0.0;
        }

        // Find the d statistic using the cff (cumulative fraction function).
        // The d statistic is the greatest deviation between the expected cff and
        // the observed cff.
        // Note: we could possibly make this statistic more extreme if we sorted the
        // observed data so that we evaluate all rolls that are less than the expected.
        double d = 0.0;
        double delta = 1.0 / numRolls;
        double exp_cff = 0.0;
        double obs_cff = 0.0;

        HashMap<Integer, Double> pmf = DieDescription.getPMF(gameId);
        HashMap<Integer, Integer> observed = getObservedRolls(gameId);

        for (Integer result : pmf.keySet()) {
            exp_cff = exp_cff + delta * (pmf.get(result) * numRolls);
            if (observed.containsKey(result)) {
                obs_cff = obs_cff + delta * (observed.get(result));
            }
            d = Math.max(d, Math.abs(exp_cff - obs_cff));
        }
        return d;
    }

    public static double calculateKSPValue(long gameId) {
        double testStatistic = calculateKSTestStatistic(gameId);
        int numRolls = getNumDiceRolls();
        if (numRolls > 0) {
            KolmogorovSmirnovDistribution dist = new KolmogorovSmirnovDistribution(numRolls);
            return 1.0 - dist.cdf(testStatistic);
        } else {
            return 1.0;
        }
    }

    public static SummaryStatistics getObservedSummaryStatistics(long gameId) {
        SummaryStatistics retval = new SummaryStatistics();
        HashMap<Integer, Integer> observedRolls = DiceRoll.getObservedRolls(gameId);
        for (Integer key : observedRolls.keySet()) {
            for (int i = 0; i < observedRolls.get(key); i++) {
                retval.addValue(key);
            }
        }
        return retval;
    }

    public static SummaryStatistics getExpectedSummaryStatistics(long gameId) {
        SummaryStatistics retval = new SummaryStatistics();
        ArrayList<DieDescription> descriptions = DieDescription.retrieveAll(gameId);
        HashMap<Integer, Integer> nonNormedPMF = DieDescription.getNonNormedPMF(descriptions);
        for (Integer key : nonNormedPMF.keySet()) {
            for (int i = 0; i < nonNormedPMF.get(key); i++) {
                retval.addValue(key);
            }
        }
        return retval;
    }

    /*
     * See http://en.wikipedia.org/wiki/Central_limit_theorem
     * (X_bar - mu) / (sigma/sqrt(n)) ~~ Norm(0, 1)
     */
    public static double calculateCentralLimitProbabilityPValue(long gameId) {
        Log.i(" > calculateCentralLimitProbability()", "...");
        SummaryStatistics observedSummaryStatistics = getObservedSummaryStatistics(gameId);
        if (getNumDiceRolls() < 4) {
            Log.i(" < calculateCentralLimitProbability()", "...");
            return 1.0;
        }
        SummaryStatistics expectedSummaryStatistics = getExpectedSummaryStatistics(gameId);

        double X_bar = observedSummaryStatistics.getMean();
        double mu = expectedSummaryStatistics.getMean();
        double sigma = expectedSummaryStatistics.getStandardDeviation();
        double statistic = Math.abs((X_bar - mu) / (sigma / Math.sqrt(getNumDiceRolls())));
        Log.i("stat is", Double.toString(statistic));
        Log.i("getN() is", Long.toString(observedSummaryStatistics.getN()));
        Log.i("getNumDiceRolls() is", Integer.toString(getNumDiceRolls()));
        NormalDistribution standardNormal = new NormalDistribution();
        double pValue = 1.0 - standardNormal.cumulativeProbability(-statistic, statistic);
        Log.i(" < calculateCentralLimitProbability()", "...");
        return pValue;
    }

    private static boolean isEmpty() {
        boolean retval;
        Cursor cursor = MainActivity.getDatabase().query(
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

    private Cursor getCursor() {
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_DICE_ROLL,
                tableDiceRollColumns, MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

