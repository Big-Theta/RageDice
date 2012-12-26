package com.bigtheta.ragedice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigtheta.ragedice.R.drawable;

public class MainActivity extends Activity {
    private SQLiteDatabase m_database;
    private MySQLiteHelper m_dbHelper;
    private ArrayList<DieDescription> m_dieDescriptions;
    private Game m_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_dbHelper = new MySQLiteHelper(this);
        // REMOVEME
        this.deleteDatabase("rage_dice.db");

        m_database = m_dbHelper.getWritableDatabase();
        m_game = new Game(m_database);

        new Player(m_database, m_game, 1, "player one");
        new Player(m_database, m_game, 2, "player two");
        new Player(m_database, m_game, 3, "player awesome (three)");
        new Player(m_database, m_game, 4, "player better than awesome (four)");

        m_dieDescriptions = new ArrayList<DieDescription>();
        DieDescription yellowDie = new DieDescription(
                m_database, 1, 6, "alea_transface_colbg_", 0xFFFFFF00,
                R.id.yellow_die, true);
        m_dieDescriptions.add(yellowDie);
        DieDescription redDie = new DieDescription(
                m_database, 1, 6, "alea_transface_colbg_", 0xFFFF0000,
                R.id.red_die, true);
        m_dieDescriptions.add(redDie);

        View mainView = (View)findViewById(R.id.activity_main_view);
        mainView.setBackgroundColor(0xFF818181);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_database = m_dbHelper.getWritableDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    protected void displayDiceRoll(DiceRoll dr) {
        TextView tv = (TextView)findViewById(R.id.player_number);
        Player currentPlayer = new Player(m_database, dr.getPlayerId());
        tv.setText(currentPlayer.getPlayerName());

        Class<drawable> res = R.drawable.class;
        for (DieResult result : DieResult.getDieResults(m_database, dr)) {
            DieDescription dd = new DieDescription(m_database, result.getDieDescriptionId());
            ImageView iv = (ImageView)findViewById(dd.getImageViewResource());
            String description = dd.getBaseIdentifierName()
                               + Integer.toString(result.getDieResult());
            try {
                Field field = res.getField(description);
                iv.setImageResource(field.getInt(null));
                iv.setBackgroundColor(dd.getBackgroundColor());
            } catch (Exception err){
                Log.e("MainActivity::displayDiceRoll", err.getCause().getMessage());
            }
        }
        displayInfo();
    }

    protected void displayInfo() {
        TextView tv = (TextView)findViewById(R.id.debug_info);
        String info = "";
        info += "numDiceRolls: " + Integer.toString(DiceRoll.getNumDiceRolls(m_database));
        HashMap<Integer, Float> pmf = DieDescription.getPMF(m_dieDescriptions);
        for (Integer observation : pmf.keySet()) {
            info += "\nObservation: " + Integer.toString(observation)
                  + " Probability: " + pmf.get(observation);
        }
        HashMap<Integer, Integer> dist = DiceRoll.getObservedRolls(m_database);
        for (Integer observation : dist.keySet()) {
            info += "\nObservation: " + Integer.toString(observation)
                  + " Count: " + dist.get(observation);
        }

        tv.setText(info);
    }

    public void resetDiceRolls(View view) {
        DiceRoll.clear(m_database);
        displayInfo();
    }

    public void undoDiceRoll(View view) {
        DiceRoll dr = DiceRoll.getLastDiceRoll(m_database);
        dr.delete(m_database);
        dr = DiceRoll.getLastDiceRoll(m_database);
        displayDiceRoll(dr);
    }

    public void rollDice(View view) {
        Player nextPlayer = Player.getNextPlayer(m_database, m_game);
        DiceRoll dr = new DiceRoll(m_database, nextPlayer, m_dieDescriptions);
        displayDiceRoll(dr);
    }
}

