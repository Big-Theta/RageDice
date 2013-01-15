package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;

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

    // This opens an opportunity to break... if the database is cleared, this won't be.
    private static HashMap<Long, Player> cacheRetrieve;

    public Player(Game game, int playerNumber, String playerName) {
        m_gameId = game.getId();
        m_playerNumber = playerNumber;
        m_playerName = playerName;

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GAME_ID, m_gameId);
        values.put(MySQLiteHelper.COLUMN_PLAYER_NUMBER, m_playerNumber);
        values.put(MySQLiteHelper.COLUMN_PLAYER_NAME, m_playerName);
        m_id = MainActivity.getDatabase().insert(MySQLiteHelper.TABLE_PLAYER, null, values);
    }

    private Player(long id) {
        m_id = id;
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_PLAYER,
                tablePlayerColumns,
                MySQLiteHelper.COLUMN_ID + " = " + Long.toString(id),
                null, null, null, null);
        cursor.moveToFirst();
        m_gameId = cursor.getLong(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_GAME_ID));
        m_playerNumber = cursor.getInt(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_NUMBER));
        m_playerName = cursor.getString(
                cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_PLAYER_NAME));
        cursor.close();
    }

    public static Player retrieve(long playerId) {
        if (cacheRetrieve == null || !cacheRetrieve.containsKey(playerId)) {
            Player ret = new Player(playerId);
            if (cacheRetrieve == null) {
                cacheRetrieve = new HashMap<Long, Player>();
            }
            cacheRetrieve.put(playerId, ret);
            return ret;
        } else {
            return cacheRetrieve.get(playerId);
        }
    }

    public void delete() {
        MainActivity.getDatabase().delete(
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

    public void setTime() {
        
    }
    
    public static Player getLastPlayer(long gameId) {
        if (!isEmpty(gameId)) {
            DiceRoll lastRoll = DiceRoll.getLastDiceRoll(gameId);
            if (lastRoll != null) {
                return new Player(lastRoll.getPlayerId());
            } else {
                Cursor cursor = MainActivity.getDatabase().query(
                        MySQLiteHelper.TABLE_PLAYER,
                        tablePlayerColumns,
                        MySQLiteHelper.COLUMN_GAME_ID + " = " + Long.toString(gameId),
                        null, null, null, null);
                cursor.moveToLast();
                Player ret = new Player(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
                cursor.close();
                return ret;
            }
        }
        return null;
    }

    public static Player getNextPlayer(long gameId) {
        DiceRoll lastRoll = DiceRoll.getLastDiceRoll(gameId);
        Player nextPlayer;
        if (lastRoll == null) {
            Cursor cursor = MainActivity.getDatabase().query(
                    MySQLiteHelper.TABLE_PLAYER,
                    tablePlayerColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();
            nextPlayer = new Player(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            cursor.close();
        } else if (!isEmpty(gameId)) {
            nextPlayer = null;
            Player lastPlayer = getLastPlayer(gameId);
            for (Player candidate : getPlayers(gameId)) {
                if (nextPlayer == null) {
                    nextPlayer = candidate;
                // Find minimum candidate that has greater playerNumber...
                } else if (candidate.getPlayerNumber() > lastPlayer.getPlayerNumber() &&
                           (nextPlayer.getPlayerNumber() <= lastPlayer.getPlayerNumber() ||
                            candidate.getPlayerNumber() < nextPlayer.getPlayerNumber())) {
                    nextPlayer = candidate;
                // ... or else find the candidate with the lowest player number.
                } else if (candidate.getPlayerNumber() < lastPlayer.getPlayerNumber() &&
                           (nextPlayer.getPlayerNumber() == lastPlayer.getPlayerNumber() ||
                            candidate.getPlayerNumber() < nextPlayer.getPlayerNumber())) {
                    nextPlayer = candidate;
                }
            }
        } else {
            nextPlayer = null;
        }
        return nextPlayer;
    }

    public static ArrayList<Player> getPlayers(long gameId) {
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_PLAYER,
                new String[] {MySQLiteHelper.COLUMN_ID},
                MySQLiteHelper.COLUMN_GAME_ID + " = " + gameId,
                null, null, null, null);
        ArrayList<Player> ret = new ArrayList<Player>();
        if (cursor.moveToFirst()) {
            do {
                ret.add(new Player(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private static boolean isEmpty(long gameId) {
        boolean retval;
        Cursor cursor = MainActivity.getDatabase().query(
                MySQLiteHelper.TABLE_PLAYER,
                tablePlayerColumns,
                MySQLiteHelper.COLUMN_GAME_ID + " = " + Long.toString(gameId),
                null, null, null, null);
        if (cursor.getCount() > 0) {
            retval = false;
        } else {
            retval = true;
        }
        cursor.close();
        return retval;
    }
}

