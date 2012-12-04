package com.bigtheta.ragedice;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DiceRollDAO {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = {
			  MySQLiteHelper.COLUMN_ID,
			  MySQLiteHelper.COLUMN_ROLL_RESULT
	      };
	  
	  public DiceRollDAO(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public DiceRoll createDiceRoll(int rollResult) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_ROLL_RESULT, rollResult);
	    long insertId = database.insert(MySQLiteHelper.TABLE_DICE_ROLLS, null,
	        values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_DICE_ROLLS,
	        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    DiceRoll newDiceRoll = cursorToDiceRoll(cursor);
	    cursor.close();
	    return newDiceRoll;
	  }

	  public void deleteDiceRoll(DiceRoll roll) {
	    long id = roll.getId();
	    System.out.println("DiceRoll deleted with id: " + id);
	    database.delete(MySQLiteHelper.TABLE_DICE_ROLLS, MySQLiteHelper.COLUMN_ID
	        + " = " + id, null);
	  }
	  
	  public void deleteAllDiceRolls() {
		  database.delete(MySQLiteHelper.TABLE_DICE_ROLLS, null, null);
	  }

	  public int getCountForRoll(int roll) {
		  /*
		  String query = 
			  "SELECT count(" + dbHelper.COLUMN_ROLL_RESULT + " ) " +
			  "AS my_count " +
			  "FROM " + dbHelper.TABLE_DICE_ROLLS + " " +
			  "WHERE " + dbHelper.COLUMN_ROLL_RESULT + "=" + Integer.toString(roll);
		  */
		  return (int)DatabaseUtils.queryNumEntries(database, dbHelper.TABLE_DICE_ROLLS,
				  									dbHelper.COLUMN_ROLL_RESULT + "=" + Integer.toString(roll));
	  }
	  
	  public DiceRoll getLastDiceRoll() {
		  Cursor cursor = database.query(MySQLiteHelper.TABLE_DICE_ROLLS,
				  						 allColumns, null, null, null, null, null);
		  cursor.moveToPosition(cursor.getCount() - 1);
		  return cursorToDiceRoll(cursor);
	  }
	  
	  public List<DiceRoll> getAllDiceRolls() {
	    List<DiceRoll> diceRolls = new ArrayList<DiceRoll>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_DICE_ROLLS,
	    							   allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      DiceRoll roll = cursorToDiceRoll(cursor);
	      diceRolls.add(roll);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return diceRolls;
	  }

	  private DiceRoll cursorToDiceRoll(Cursor cursor) {
	    DiceRoll roll = new DiceRoll();
	    roll.setId(cursor.getLong(0));
	    roll.setRollResult(cursor.getInt(1));
	    return roll;
	  }
}
