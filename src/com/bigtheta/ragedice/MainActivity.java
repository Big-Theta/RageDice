package com.bigtheta.ragedice;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	int[] mDiceImgs;
	int mNumPlayers;
	int mPlayerNum;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDiceImgs = new int[6];
        mDiceImgs[0] = R.drawable.alea_1_transbg;
        mDiceImgs[1] = R.drawable.alea_2_transbg;
        mDiceImgs[2] = R.drawable.alea_3_transbg;
        mDiceImgs[3] = R.drawable.alea_4_transbg;
        mDiceImgs[4] = R.drawable.alea_5_transbg;
        mDiceImgs[5] = R.drawable.alea_6_transbg;
        mNumPlayers = 2;
        mPlayerNum = 1;
        // Initialize the dice images
        rollDice(null);
    	ImageView red_die = (ImageView)findViewById(R.id.red_die);
    	red_die.setBackgroundColor(0xFFFF0000);
    	ImageView yellow_die = (ImageView)findViewById(R.id.yellow_die);
    	yellow_die.setBackgroundColor(0xFFFFFF00);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void rollSingleDie(ImageView die) {
    	Random rand = new Random();
    	die.setImageResource(mDiceImgs[rand.nextInt(6)]);
    }
    
    public void nextPlayer() {
    	if (mNumPlayers > 1) {
    		mPlayerNum %= mNumPlayers;
    		mPlayerNum++;
    		TextView player = (TextView)findViewById(R.id.player_number);
    		player.setText(Integer.toString(mPlayerNum));
    	}
    }
    
    public void rollDice(View view) {
    	nextPlayer();
    	ImageView red_die = (ImageView)findViewById(R.id.red_die);
    	ImageView yellow_die = (ImageView)findViewById(R.id.yellow_die);
    	rollSingleDie(red_die);
    	rollSingleDie(yellow_die);
    }
}
