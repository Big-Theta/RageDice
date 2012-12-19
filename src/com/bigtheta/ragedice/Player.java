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
        Log.i("Player retrieving id", Long.toString(id));
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
        Log.i("Player::getLastPlayer", "...");
        if (!isEmpty(database)) {
            DiceRoll lastRoll = DiceRoll.getLastDiceRoll(database);
            if (lastRoll != null) {
                Log.i("Player::getLastPlayer", "branch 1");
                Log.i("Player::getLastPlayer playerId:", Long.toString(lastRoll.getPlayerId()));
                return new Player(database, lastRoll.getPlayerId());
            } else {
                Log.i("Player::getLastPlayer", "branch 2");
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

    public static Player getNextPlayer(SQLiteDatabase database, Game game) {
        Log.i("Player::getNextPlayer", "...");
        DiceRoll lastRoll = DiceRoll.getLastDiceRoll(database);
        if (lastRoll == null) {
            Log.i("Player::getNextPlayer branch 1", "...");
            Cursor cursor = database.query(
                    MySQLiteHelper.TABLE_PLAYER,
                    tablePlayerColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();
            return new Player(database, cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        } else if (!isEmpty(database)) {
            Log.i("Player::getNextPlayer branch 2", "entering");
            Player nextPlayer = null;
            Player lastPlayer = getLastPlayer(database);
            Log.i("Player::getNextPlayer branch 2 lastPlayer.getId()", Long.toString(lastPlayer.getId()));
            for (Player candidate : getPlayers(database, game.getId())) {
                Log.i("Player::getNextPlayer branch 2 candidate.getId()", Long.toString(candidate.getId()));
                Log.i("Player::getNextPlayer branch 2 lastPlayer.getId()", Long.toString(lastPlayer.getId()));
                if (nextPlayer != null) {
                    Log.i("Player::getNextPlayer branch 2 nextPlayer.getId()", Long.toString(nextPlayer.getId()));
                }
                if (nextPlayer == null) {
                    Log.i("first", "...");
                    nextPlayer = candidate;
                // Find minimum candidate that has greater playerNumber...
                } else if (candidate.getPlayerNumber() > lastPlayer.getPlayerNumber() &&
                           (nextPlayer.getPlayerNumber() <= lastPlayer.getPlayerNumber() ||
                            candidate.getPlayerNumber() < nextPlayer.getPlayerNumber())) {
                    Log.i("second", "...");
                    nextPlayer = candidate;
                // ... or else find the candidate with the lowest player number.
                } else if (candidate.getPlayerNumber() < lastPlayer.getPlayerNumber() &&
                           (nextPlayer.getPlayerNumber() == lastPlayer.getPlayerNumber() ||
                            candidate.getPlayerNumber() < nextPlayer.getPlayerNumber())) {
                    Log.i("third", "...");
                    nextPlayer = candidate;
                }
            }
            Log.i("Player::getNextPlayer branch 2", "leaving");
            Log.i("branch 2 nextPlayer.getId()", Long.toString(nextPlayer.getId()));
            return nextPlayer;
        } else {
            Log.i("branch 3", "...");
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

