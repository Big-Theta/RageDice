package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

@SuppressLint("UseSparseArrays")
public class DieDescription {
    private static String[] tableDieDescriptionColumns = {
        MySQLiteHelper.COLUMN_GAME_ID,
        MySQLiteHelper.COLUMN_NUM_LOW_FACE,
        MySQLiteHelper.COLUMN_NUM_HIGH_FACE,
        MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME,
        MySQLiteHelper.COLUMN_BACKGROUND_COLOR,
        MySQLiteHelper.COLUMN_IMAGE_VIEW_RESOURCE,
        MySQLiteHelper.COLUMN_IS_NUMERIC
    };

    long m_id;
    long m_gameId;
    int m_numLowFace;
    int m_numHighFace;
    String m_baseIdentifierName;
    int m_backgroundColor;
    int m_imageViewResource;
    boolean m_isNumeric;

    // This opens an oportunity to break... if the database is cleared, this won't be.
    private static HashMap<Long, ArrayList<DieDescription> > cacheRetrieveAll = null;
    private static HashMap<Long, HashMap<Integer, Double> > cacheGetPMF = null;

    public DieDescription(Game game, int numLowFace, int numHighFace,
                          String baseIdentifierName,
                          int backgroundColor, int imageViewResource,
                          boolean isNumeric) {
        m_numLowFace = numLowFace;
        m_numHighFace = numHighFace;
        m_baseIdentifierName = baseIdentifierName;
        m_backgroundColor = backgroundColor;
        m_imageViewResource = imageViewResource;
        m_isNumeric = isNumeric;

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_ID, game.getId());
        values.put(MySQLiteHelper.COLUMN_NUM_LOW_FACE, m_numLowFace);
        values.put(MySQLiteHelper.COLUMN_NUM_HIGH_FACE, m_numHighFace);
        values.put(MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME,
                   m_baseIdentifierName);
        values.put(MySQLiteHelper.COLUMN_BACKGROUND_COLOR, m_backgroundColor);
        values.put(MySQLiteHelper.COLUMN_IMAGE_VIEW_RESOURCE,
                   m_imageViewResource);
        values.put(MySQLiteHelper.COLUMN_IS_NUMERIC, m_isNumeric);
        m_id = MainActivity.getDatabase().insert(MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                                                 null, values);
    }

    private DieDescription(long id) {
        m_id = id;
        Cursor cursor = getCursor();
        m_gameId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_GAME_ID));
        m_numLowFace = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_NUM_LOW_FACE));
        m_numHighFace = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_NUM_HIGH_FACE));
        m_baseIdentifierName = cursor.getString(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME));
        m_backgroundColor = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_BACKGROUND_COLOR));
        m_imageViewResource = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_IMAGE_VIEW_RESOURCE));
        m_isNumeric = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_IS_NUMERIC)) == 0 ? false : true;

        cursor.close();
    }

    public static DieDescription retrieve(long id) {
        return new DieDescription(id);
    }

    public static ArrayList<DieDescription> retrieveAll(long gameId) {
        if (cacheRetrieveAll == null || !cacheRetrieveAll.containsKey(gameId)) {
            ArrayList<DieDescription> ret = new ArrayList<DieDescription>();
            Cursor cursor = MainActivity.getDatabase().query(
                    MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                    new String[] {MySQLiteHelper.COLUMN_ID},
                    MySQLiteHelper.COLUMN_GAME_ID + " = " + gameId,
                    null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ret.add(DieDescription.retrieve(cursor.getLong(0)));
                cursor.moveToNext();
            }
            cursor.close();
            if (cacheRetrieveAll == null) {
                cacheRetrieveAll = new HashMap<Long, ArrayList<DieDescription> >();
            }
            cacheRetrieveAll.put(gameId, ret);
            return ret;
        } else {
            return cacheRetrieveAll.get(gameId);
        }
    }

    public long getId() {
        return m_id;
    }

    public long getGameId() {
        return m_gameId;
    }

    public int getNumLowFace() {
        return m_numLowFace;
    }

    public int getNumHighFace() {
        return m_numHighFace;
    }

    public String getBaseIdentifierName() {
        return m_baseIdentifierName;
    }

    public int getBackgroundColor() {
        return m_backgroundColor;
    }

    public int getImageViewResource() {
        return m_imageViewResource;
    }

    public boolean getIsNumeric() {
        return m_isNumeric;
    }

    public static String getKSDescription(long gameId) {
        String update = new String();
        update += "This test uses the Kolmogorov-Smirnov test to determine "
                + "how likely it is that this collection of dice rolls came "
                + "from a fair distribution. The KS statistic is the maximum "
                + "difference between the observed and the expected cumulative "
                + "fraction function (cff). Currently, this value is ";
        update += Double.toString(DiceRoll.calculateKSTestStatistic(gameId));
        update += ". As the maximum difference between the two cffs becomes small, "
                + "the likelyhood that the observed dice rolls were produced by 'fair' "
                + "dice becomes large... unless we used a biased random number generator.";
        return update;
    }

    /*
     * Calculates the expected distribution for each dice result.
     */
    public static HashMap<Integer, Double> getPMF(long gameId) {
        if (cacheGetPMF == null || !cacheGetPMF.containsKey(gameId)) {
            ArrayList<DieDescription> descriptions = retrieveAll(gameId);
            HashMap<Integer, Double> ret = new HashMap<Integer, Double>();

            HashMap<Integer, Integer> nonNormed = getNonNormedPMF(descriptions);
            double totalPossibilities = (double)getNumPossibilities(descriptions);
            for (Integer observation : nonNormed.keySet()) {
                ret.put(observation, nonNormed.get(observation) / totalPossibilities);
            }
            if (cacheGetPMF == null) {
                cacheGetPMF = new HashMap<Long, HashMap<Integer, Double> >();
            }
            cacheGetPMF.put(gameId, ret);
            return ret;
        } else {
            return cacheGetPMF.get(gameId);
        }
    }

    private static int getNumPossibilities(ArrayList<DieDescription> descriptions) {
        int count = 1;
        for (DieDescription description : descriptions) {
            if (description.getIsNumeric()) {
                count *= description.getNumHighFace() - description.getNumLowFace() + 1;
            }
        }
        return count;
    }

    public static HashMap<Integer, Integer> getNonNormedPMF(ArrayList<DieDescription> descriptions) {
        final HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
        // Need a recursion because we don't know how many dice there are, or what their number
        // of faces are. Thus, this recursion is a sort of arbitrarily nested loop with
        // backtracking.
        class Recursor {
            public void recurse(ArrayList<DieDescription> remainingDescriptions,
                                int currentObservation) {
                if (remainingDescriptions.isEmpty()) {
                    if (ret.containsKey(currentObservation)) {
                        ret.put(currentObservation, ret.get(currentObservation) + 1);
                    } else {
                        ret.put(currentObservation, 1);
                    }
                } else {
                    DieDescription currentDescription = remainingDescriptions.get(
                            remainingDescriptions.size() - 1);
                    ArrayList<DieDescription> copy = new ArrayList<DieDescription>(
                            remainingDescriptions);
                    copy.remove(copy.size() - 1);
                    for (int faceVal = currentDescription.getNumLowFace();
                         faceVal <= currentDescription.getNumHighFace(); faceVal++) {
                        recurse(copy, currentObservation + faceVal);
                    }
                }
            }
        }
        Recursor recursor = new Recursor();
        recursor.recurse(descriptions, 0);
        return ret;
    }

    private Cursor getCursor() {
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                tableDieDescriptionColumns,
                MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

