package com.bigtheta.ragedice;

import java.util.Random;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void rollSingleDie(ImageView die) {
    	Random rand = new Random();
    	
    	switch (rand.nextInt(6) + 1) {
    	case 1:
    		die.setImageResource(R.drawable.alea_1);
    		break;
    	case 2:
    		die.setImageResource(R.drawable.alea_2);
    		break;
    	case 3:
    		die.setImageResource(R.drawable.alea_3);
    		break;
    	case 4:
    		die.setImageResource(R.drawable.alea_4);
    		break;
    	case 5:
    		die.setImageResource(R.drawable.alea_5);
    		break;
   		default:
    		die.setImageResource(R.drawable.alea_6);
    		break;
    	}
    }
    
    public void rollDice(View view) {
    	ImageView iv1 = (ImageView)findViewById(R.id.dice_result_1);
    	ImageView iv2 = (ImageView)findViewById(R.id.dice_result_2);
    	
    	//iv1.setColorFilter(0xCD0000, PorterDuff.Mode.LIGHTEN);
    	//iv1.setColorFilter(0xCD0000, PorterDuff.Mode.DARKEN);
    	rollSingleDie(iv1);
    	rollSingleDie(iv2);
    }
}
