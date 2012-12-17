package com.bigtheta.ragedice;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DieDescription {
    private String[] tableDieDescriptionColumns = {
        MySQLiteHelper.COLUMN_NUM_LOW_FACE,
        MySQLiteHelper.COLUMN_NUM_HIGH_FACE,
        MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME,
        MySQLiteHelper.COLUMN_BACKGROUND_COLOR
    };

    long m_id;
    int m_numLowFace;
    int m_numHighFace;
    String m_baseIdentifierName;
    int m_backgroundColor;

    public DieDescription(SQLiteDatabase database, int numLowFace, int numHighFace,
                          String baseIdentifierName, int backgroundColor) {
        m_numLowFace = numLowFace;
        m_numHighFace = numHighFace;
        m_baseIdentifierName = baseIdentifierName;
        m_backgroundColor = backgroundColor;

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

    public DieDescription(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID));
        m_numLowFace = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_NUM_LOW_FACE));
        m_numHighFace = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_NUM_HIGH_FACE));
        m_baseIdentifierName = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_BASE_IDENTIFIER_NAME));
        m_backgroundColor = cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_BACKGROUND_COLOR));
    }

    public DieDescription(long id) {
        Cursor cursor = m_database.query(MySQLiteHelper.TABLE_DIE_DESCRIPTION,
                tableDieDescriptionColumns, MySQLiteHelper.COLUMN_ID + " = " + id,
                null, null, null, null);


    }

    public long getId() {
        return m_id;
    }

    public int getNumLowFace() {
        return m_numLowFace();
    }

    public int getNumHighFace() {
        return m_numHighFace();
    }

    public int getBackgroundColor() {
        return m_backgroundColor();
    }
}

