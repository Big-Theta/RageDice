package com.bigtheta.ragedice;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
        implements GameLogFragment.GameLogListener,
                   DiceDisplayFragment.DiceDisplayListener,
                   KSDescriptionFragment.KSDescriptionListener {

    private static SQLiteDatabase m_database;
    private MySQLiteHelper m_dbHelper;
    private static Game m_game;
    private FragmentManager fm;

    public static SQLiteDatabase getDatabase() {
        return m_database;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alternate);

        m_dbHelper = new MySQLiteHelper(this);
        // REMOVEME
        this.deleteDatabase("rage_dice.db");

        m_database = m_dbHelper.getWritableDatabase();
        m_game = new Game();

        new Player(m_game, 1, "player one");
        new Player(m_game, 2, "player two");
        new Player(m_game, 3, "player awesome (three)");
        new Player(m_game, 4, "player better than awesome (four)");
        Player.getPlayers(m_game.getId());

        new DieDescription(m_game, 1, 6, "alea_transface_colbg_",
                           getResources().getColor(R.color.yellow_die),
                           R.id.yellow_die, true);
        new DieDescription(m_game, 1, 6, "alea_transface_colbg_",
                           getResources().getColor(R.color.red_die),
                           R.id.red_die, true);
        fm = getSupportFragmentManager();

        //View mainView = (View)findViewById(R.id.activity_main_alt_view);
        //mainView.setBackgroundColor(getResources().getColor(R.color.background));
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

    protected void displayDiceRoll(Player nextPlayer, DiceRoll dr) {
        //FragmentManager fm = getSupportFragmentManager();
        //displayDiceRoll(dr);
        DiceDisplayFragment ddf = (DiceDisplayFragment)
                fm.findFragmentById(R.id.dice_fragment_ui);
        GameLogFragment glf = (GameLogFragment) fm.findFragmentById(R.id.game_log_fragment);
        ddf.displayDiceRoll(dr);
        glf.displayInfo(nextPlayer, dr);
        findViewById(R.id.histogram_view).invalidate();
        /*
        TextView tv = (TextView)findViewById(R.id.player_number);
        Player currentPlayer = Player.retrieve(dr.getPlayerId());
        tv.setText(currentPlayer.getPlayerName());

        Class<drawable> res = R.drawable.class;
        for (DieResult result : DieResult.getDieResults(dr)) {
            DieDescription dd = DieDescription.retrieve(result.getDieDescriptionId());
            ImageView iv = (ImageView)findViewById(dd.getImageViewResource());
            String description = dd.getBaseIdentifierName()
                               + Integer.toString(result.getDieResult());
            try {
                Field field = res.getField(description);
                iv.setImageResource(field.getInt(null));
            } catch (Exception err){
                Log.e("MainActivity::displayDiceRoll", err.getCause().getMessage());
            }
            iv.setBackgroundColor(dd.getBackgroundColor());
        }
        displayInfo();
        */
    }

    protected void displayInfo() {
        TextView tv = (TextView)findViewById(R.id.ksdescription_view);
        String info = "";
        info += "numDiceRolls: " + Integer.toString(DiceRoll.getNumDiceRolls());
        HashMap<Integer, Double> pmf = DieDescription.getPMF(m_game.getId());
        for (Integer observation : pmf.keySet()) {
            info += "\nObservation: " + Integer.toString(observation)
                  + " Probability: " + pmf.get(observation);
        }
        HashMap<Integer, Integer> dist = DiceRoll.getObservedRolls(m_game.getId());
        for (Integer observation : dist.keySet()) {
            info += "\nObservation: " + Integer.toString(observation)
                  + " Count: " + dist.get(observation);
        }
        double stat = DiceRoll.calculateKSTestStatistic(m_game.getId());
        info += "\nKS statistic: " + Double.toString(stat);
        // Probability that these are different distributions.
        info += "\nKS probability: " + Double.toString(DiceRoll.calculateKSPValue(m_game.getId()));
        info += "\nCentral Limit Theorem probability: " + Double.toString(DiceRoll.calculateCentralLimitProbabilityPValue(m_game.getId()));

        info += "\n=====\n";
        info += DieDescription.getKSDescription(m_game.getId());
        info += "\n=====\n";
        info += DieDescription.getCLTDescription(m_game.getId());

        tv.setText(info);
    }

    public void resetDiceRolls(View view) {
        DiceRoll.clear();
        displayInfo();
    }

    public void undoDiceRoll(View view) {
        DiceRoll dr = DiceRoll.getLastDiceRoll(m_game.getId());
        if (dr == null) {
            return;
        }
        dr.delete();
        dr = DiceRoll.getLastDiceRoll(m_game.getId());
        displayDiceRoll(Player.getLastPlayer(m_game.getId()), dr);
    }

    public void rollDice(View view) {
        Player nextPlayer = Player.getNextPlayer(m_game.getId());
        DiceRoll dr = new DiceRoll(nextPlayer);
        displayDiceRoll(nextPlayer, dr);
        //findViewById(R.id.)
        //displayInfo();
        /*
        FragmentManager fm = getSupportFragmentManager();
        //displayDiceRoll(dr);
        DiceDisplayFragment ddf = (DiceDisplayFragment)
                fm.findFragmentById(R.id.dice_fragment_ui);
        GameLogFragment glf = (GameLogFragment) fm.findFragmentById(R.id.game_log_fragment);
        ddf.displayDiceRoll(dr);
        glf.displayInfo(nextPlayer, dr);
        //displayInfo();
         *
         */
    }

    public void onDiceSelected(int position) {
    }

    public void onGameLogSelected(int position) {
    }

    public void onKSDescriptionSelected(int position) {
    }
    
    public static Game getGame() {
        return m_game;
    }
}

