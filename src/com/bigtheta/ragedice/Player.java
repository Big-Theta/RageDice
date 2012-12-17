package com.bigtheta.ragedice;

public class Player {
    private String[] tablePlayerColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_PLAYER_NUMBER,
        MySQLiteHelper.COLUMN_PLAYER_NAME
    };
    long m_id;

    public Player(SQLiteDatabase database, DiceDAO dao, Game game) {
        m_dao = dao;
        setGame(game);

        m_database = database;
        m_id = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return 
    }

    public void setGame(Game game) {
        
    }

    public long getPlayerNumber() {
        return m_playerNumber;
    }

    public void setPlayerNumber(long playerNumber) {
        m_playerNumber = playerNumber;
    }

    public String getPlayerName() {
        return m_playerName;
    }

    public void setPlayerName(String playerName) {
        m_playerName = playerName;
    }
}

