package com.bigtheta.ragedice;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Player {
    private static String[] tablePlayerColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_GAME_ID,
        MySQLiteHelper.COLUMN_PLAYER_NUMBER,
        MySQLiteHelper.COLUMN_PLAYER_NAME
    };

    long m_id;
    long m_gameId;
    int m_playerNumber;
    String m_playerName;

    public Player(SQLiteDatabase database, Game game, int playerNumber,
                  String playerName) {
        m_gameId = game.getId();
        m_playerNumber = playerNumber;
        m_playerName = playerName;

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_ID, m_gameId);
        values.put(MySQLiteHelper.COLUMN_PLAYER_NUMBER, m_playerNumber);
        values.put(MySQLiteHelper.COLUMN_PLAYER_NAME, m_playerName);
        m_id = database.insert(MySQLiteHelper.TABLE_PLAYER, null, values);
    }

    public Player(SQLiteDatabase database, long id) {
        m_id = id;
        Cursor cursor = getCursor(database);
        m_gameId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_GAME_ID));
        m_playerNumber = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_NUMBER));
        m_playerName = cursor.getString(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_NAME));
        cursor.moveToFirst();
        cursor.close();
    }

    public void delete(SQLiteDatabase database) {
        database.delete(
                MySQLiteHelper.TABLE_PLAYER, MySQLiteHelper.COLUMN_ID + " = " + m_id, null);
    }

    public long getId() {
        return m_id;
    }

    public long getGameId() {
        return m_gameId;
    }

    public int getPlayerNumber() {
        return m_playerNumber;
    }

    public String getPlayerName() {
        return m_playerName;
    }

    public static Player getLastPlayer(SQLiteDatabase database) {
        if (!isEmpty(database)) {
            DiceRoll lastRoll = DiceRoll.getLastDiceRoll(database);
            if (lastRoll != null) {
                return new Player(database, lastRoll.getPlayerId());
            } else {
                Cursor cursor = database.query(
                        MySQLiteHelper.TABLE_PLAYER,
                        tablePlayerColumns,
                        null, null, null, null, null);
            	cursor.moveToLast();
            	return new Player(database, cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            }
        }
        return null;
    }
    
    public Player getNextPlayer(SQLiteDatabase database) {
    	DiceRoll lastRoll = DiceRoll.getLastDiceRoll(database);
    	if (lastRoll == null) {
            Cursor cursor = database.query(
                    MySQLiteHelper.TABLE_PLAYER,
                    tablePlayerColumns,
                    null, null, null, null, null);
        	cursor.moveToFirst();
        	return new Player(database, cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
    	} else if (!isEmpty(database)) {
	        Player nextPlayer = this;
	        for (Player candidate : getPlayers(database, m_gameId)) {
	            Log.i("getNextPlayer nextPlayer", Long.toString(nextPlayer.getPlayerNumber()));
	            Log.i("getNextPlayer candidate", Long.toString(candidate.getPlayerNumber()));
	            Log.i("getNextPlayer this", Long.toString(getPlayerNumber()));
	            // Find minimum candidate that has greater playerNumber...
	            if (nextPlayer.getPlayerNumber() == getPlayerNumber() ||
	                (candidate.getPlayerNumber() > getPlayerNumber() &&
	                 candidate.getPlayerNumber() < nextPlayer.getPlayerNumber())) {
	
	                Log.i("getNextPlayer branch 1", "1");
	                nextPlayer = candidate;
	            // ... or else find the candidate with the lowest player number.
	            } else if (nextPlayer.getPlayerNumber() <= getPlayerNumber() &&
	                       candidate.getPlayerNumber() < nextPlayer.getPlayerNumber()) {
	                Log.i("getNextPlayer branch 2", "2");
	                nextPlayer = candidate;
	            } else {
	                Log.i("getNextPlayer branch 3", "3");
	            }
	        }
	        Log.i("getNextPlayer returning", Long.toString(nextPlayer.getId()));
	        return nextPlayer;
	    } else {
	    	return null;
	    }
    }

    public static ArrayList<Player> getPlayers(SQLiteDatabase database,
                                               long gameId) {
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_PLAYER,
                new String[] {MySQLiteHelper.COLUMN_ID},
                MySQLiteHelper.COLUMN_GAME_ID + " = " + gameId,
                null, null, null, null);
        ArrayList<Player> ret = new ArrayList<Player>();
        if (cursor.moveToFirst()) {
            do {
                ret.add(new Player(database, cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private static boolean isEmpty(SQLiteDatabase database) {
        boolean retval;
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_PLAYER,
                tablePlayerColumns,
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
                MySQLiteHelper.TABLE_PLAYER,
                tablePlayerColumns,
                MySQLiteHelper.COLUMN_ID + " = " + m_id,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}

