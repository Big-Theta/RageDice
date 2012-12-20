package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@SuppressLint("UseSparseArrays")
public class DieDescription {
    private static String[] tableDieDescriptionColumns = {
        MySQLiteHelper.COLUMN_NUM_LOW_FACE,
        MySQLiteHelper.COLUMN_NUM_HIGH_FACE,
        MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME,
        MySQLiteHelper.COLUMN_BACKGROUND_COLOR,
        MySQLiteHelper.COLUMN_IMAGE_VIEW_RESOURCE,
        MySQLiteHelper.COLUMN_IS_NUMERIC
    };

    long m_id;
    int m_numLowFace;
    int m_numHighFace;
    String m_baseIdentifierName;
    int m_backgroundColor;
    int m_imageViewResource;
    boolean m_isNumeric;

    public DieDescription(SQLiteDatabase database, int numLowFace, int
                          numHighFace, String baseIdentifierName,
                          int backgroundColor, int imageViewResource,
                          boolean isNumeric) {
        m_numLowFace = numLowFace;
        m_numHighFace = numHighFace;
        m_baseIdentifierName = baseIdentifierName;
        m_backgroundColor = backgroundColor;
        m_imageViewResource = imageViewResource;
        m_isNumeric = isNumeric;

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NUM_LOW_FACE, m_numLowFace);
        values.put(MySQLiteHelper.COLUMN_NUM_HIGH_FACE, m_numHighFace);
        values.put(MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME,
                   m_baseIdentifierName);
        values.put(MySQLiteHelper.COLUMN_BACKGROUND_COLOR, m_backgroundColor);
        values.put(MySQLiteHelper.COLUMN_IMAGE_VIEW_RESOURCE,
                   m_imageViewResource);
        values.put(MySQLiteHelper.COLUMN_IS_NUMERIC, m_isNumeric);
        m_id = database.insert(MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                               null, values);
    }

    public DieDescription(SQLiteDatabase database, long id) {
        m_id = id;
        Cursor cursor = getCursor(database);
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

    public long getId() {
        return m_id;
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

    public static HashMap<Integer, Float> getPMF(ArrayList<DieDescription> descriptions) {
        HashMap<Integer, Float> ret = new HashMap<Integer, Float>();

        HashMap<Integer, Integer> nonNormed = getNonNormedPMF(descriptions);
        float totalPossibilities = (float)getNumPossibilities(descriptions);
        for (Integer observation : nonNormed.keySet()) {
            ret.put(observation, nonNormed.get(observation) / totalPossibilities);
        }
        return ret;
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

    private static HashMap<Integer, Integer> getNonNormedPMF(ArrayList<DieDescription> descriptions) {
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

    private Cursor getCursor(SQLiteDatabase database) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                tableDieDescriptionColumns,
                MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

