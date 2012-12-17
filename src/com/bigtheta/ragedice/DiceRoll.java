package com.bigtheta.ragedice;

public class DiceRoll {
    private String[] tableDiceRollColumns = {
        MySQLiteHelper.COLUMN_ID,
        MySQLiteHelper.COLUMN_ROLL_TOTAL,
        MySQLiteHelper.COLUMN_PLAYER_ID
    };
    long m_id;

    public DiceRoll(Player player) {
        m_id = cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID));
    }

    public DiceRoll(Cursor cursor) {
        DiceRoll roll = new DiceRoll();
        roll.setId(cursor.getLong(0));
        roll.setRollResult(cursor.getInt(1));
        return roll;
    }

    // XXX
    public DiceRoll createDiceRoll(int rollResult) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ROLL_RESULT, rollResult);
        long insertId = m_database.insert(MySQLiteHelper.TABLE_DICE_ROLLS, null,
                values);
        Cursor cursor = m_database.query(MySQLiteHelper.TABLE_DICE_ROLLS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        DiceRoll newDiceRoll = cursorToDiceRoll(cursor);
        cursor.close();

        return newDiceRoll;
    }

    // XXX
    public void deleteDiceRoll(DiceRoll roll) {
        long id = roll.getId();
        System.out.println("DiceRoll deleted with id: " + id);
        m_database.delete(MySQLiteHelper.TABLE_DICE_ROLLS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    // XXX
    public int getNumDiceRolls() {
        Cursor cursor = m_database.query(MySQLiteHelper.TABLE_DICE_ROLLS,
                allColumns, null, null, null, null, null);
        return cursor.getCount();
    }

    // XXX
    public DiceRoll getLastDiceRoll() {
        Cursor cursor = m_database.query(MySQLiteHelper.TABLE_DICE_ROLLS,
                allColumns, null, null, null, null, null);
        cursor.moveToPosition(cursor.getCount() - 1);
        return cursorToDiceRoll(cursor);
    }

    private long id;
    private long roll_result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRollResult() {
        return roll_result;
    }

    public void setRollResult(int result) {
        this.roll_result = result;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return "DiceRoll(" + Long.toString(id) + " : " + Long.toString(roll_result);
    }
}

