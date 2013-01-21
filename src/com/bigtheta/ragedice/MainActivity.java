package com.bigtheta.ragedice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
        implements GameLogFragment.GameLogListener,
                   DiceDisplayFragment.DiceDisplayListener,
                   KSDescriptionFragment.KSDescriptionListener,
                   GestureDetector.OnGestureListener,
                   GestureDetector.OnDoubleTapListener {

    private GestureDetectorCompat m_gestureDetector;
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

        m_gestureDetector = new GestureDetectorCompat(this, this);
        m_gestureDetector.setOnDoubleTapListener(this);
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
                           R.color.yellow_die,
                           R.id.yellow_die, DieDescription.NUMERIC);
        new DieDescription(m_game, 1, 6, "alea_transface_colbg_",
                           R.color.red_die,
                           R.id.red_die, DieDescription.NUMERIC);
        new DieDescription(m_game, 1, 6, "ship_die_",
                           R.color.background,
                           R.id.ship_die, DieDescription.SHIP);
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
            Context context = getApplicationContext();
            CharSequence text = getResources().getText(R.string.toast_msg_cannot_undo);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.TOP, 0, 200);
            toast.show();
            return;
        }
        dr.delete();
        refreshDisplay();
    }

    public void rollDice(View view) {
        Player nextPlayer = Player.getNextPlayer(m_game.getId());
        DiceRoll dr = new DiceRoll(nextPlayer);
        refreshDisplay();
    }

    public void nextFragment() {
        Log.i("nextFragment", "triggered");
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.findFragmentByTag("glf") != null &&
            fm.findFragmentByTag("glf").isVisible()) {
            ft.replace(R.id.lower_ui_container, new KSDescriptionFragment(), "ksdf");
        } else if (fm.findFragmentByTag("ksdf") != null &&
            fm.findFragmentByTag("ksdf").isVisible()) {
            ft.replace(R.id.lower_ui_container, new GameLogFragment(), "glf");
        } else if (fm.findFragmentByTag("hgf") != null &&
            fm.findFragmentByTag("hgf").isVisible()) {
            ft.replace(R.id.lower_ui_container, new KSDescriptionFragment(), "ksdf");
        } else {
            throw new IllegalStateException("No fragment visible.");
        }
        ft.commit();
        refreshDisplay();
    }

    public void prevFragment() {
        Log.i("prevFragment", "triggered");
        nextFragment();
        refreshDisplay();
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

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.m_gestureDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.e("debug...","onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent start, MotionEvent finish,
                           float velocityX, float velocityY) {
        Log.e("debug...", "onFling: ");
        float deltaX = Math.abs(finish.getRawX() - start.getRawX());
        float deltaY = Math.abs(finish.getRawY() - start.getRawY());
        if (deltaX > deltaY) {
            if (finish.getRawX() < start.getRawX()) {
                nextFragment();
            } else {
                prevFragment();
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.e("debug...", "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        Log.e("debug...", "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.e("debug...", "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.e("debug...", "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.e("debug...", "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.e("debug...", "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.e("debug...", "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}

