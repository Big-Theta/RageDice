package com.bigtheta.ragedice;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
        new Player(m_game, 3, "player three");
        new Player(m_game, 4, "player four");

        new DieDescription(m_game, 1, 6, "alea_transface_colbg_",
                           getResources().getColor(R.color.yellow_die),
                           R.id.yellow_die, true);
        new DieDescription(m_game, 1, 6, "alea_transface_colbg_",
                           getResources().getColor(R.color.red_die),
                           R.id.red_die, true);
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.lower_ui_container, new GameLogFragment(), "glf");
        ft.commit();
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
       
        // TODO These views need to exist somewhere.
        //findViewById(R.id.histogram_rolls_view).invalidate();
        //findViewById(R.id.histogram_player_time_view).invalidate();

    public void resetDiceRolls(View view) {
        DiceRoll.clear(m_game.getId());
        //displayInfo();
        refreshDisplay();
    }

    public void undoDiceRoll(View view) {
        DiceRoll dr = DiceRoll.getLastDiceRoll(m_game.getId());
        if (dr == null) {
            return;
        }
        dr.delete();
        refreshDisplay();
    }

    public void rollDice(View view) {
        Player nextPlayer = Player.getNextPlayer(m_game.getId());
        DiceRoll dr = new DiceRoll(nextPlayer);
        //displayDiceRoll(nextPlayer, dr);
        refreshDisplay();
    }
    
    public void nextFragment(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.findFragmentByTag("glf") != null &&
            fm.findFragmentByTag("glf").isVisible()) {
            ft.replace(R.id.lower_ui_container, new KSDescriptionFragment(), "ksdf");
        }else if (fm.findFragmentByTag("ksdf") != null &&
            fm.findFragmentByTag("ksdf").isVisible()) {
            ft.replace(R.id.lower_ui_container, new GameLogFragment(), "glf");
        }else if (fm.findFragmentByTag("hgf") != null &&
            fm.findFragmentByTag("hgf").isVisible()) {
            ft.replace(R.id.lower_ui_container, new KSDescriptionFragment(), "ksdf");
        }else {
            throw new IllegalStateException("No fragment visible.");
        }
        ft.commit();
    }
    
    public void refreshDisplay() {
        DiceRoll dr = DiceRoll.getLastDiceRoll(m_game.getId());
        Player nextPlayer = Player.getLastPlayer(m_game.getId());
        DiceDisplayFragment ddf = (DiceDisplayFragment)
        		fm.findFragmentById(R.id.dice_fragment_ui);
        GameLogFragment glf = (GameLogFragment) fm.findFragmentByTag("glf");
        KSDescriptionFragment ksdf = (KSDescriptionFragment) fm.findFragmentByTag("ksdf");
        
        if (ddf != null && ddf.isVisible()) {
            ddf.displayDiceRoll(dr);
        }
        if (glf != null && glf.isVisible()) {
            
            glf.displayInfo(nextPlayer, dr);
        } else if (ksdf != null && ksdf.isVisible()) {
            ksdf.displayInfo(m_game.getId());
        }
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

