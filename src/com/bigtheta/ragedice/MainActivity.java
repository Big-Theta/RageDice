package com.bigtheta.ragedice;

import java.util.Random;

import android.app.Activity;
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

    public void rollDice(View view) {
    	Random rand = new Random();
    	ImageView iv1 = (ImageView)findViewById(R.id.dice_result_1);
    	
    	Integer imageSelect;
    	
    	imageSelect = rand.nextInt(6) + 1;
    	switch (imageSelect) {
    	case 1:
    		iv1.setImageResource(R.drawable.alea_1);
    		break;
    	case 2:
    		iv1.setImageResource(R.drawable.alea_2);
    		break;
    	case 3:
    		iv1.setImageResource(R.drawable.alea_3);
    		break;
    	case 4:
    		iv1.setImageResource(R.drawable.alea_4);
    		break;
    	case 5:
    		iv1.setImageResource(R.drawable.alea_5);
    		break;
   		default:
    		iv1.setImageResource(R.drawable.alea_6);
    		break;
    	}
    	
    	ImageView iv2 = (ImageView)findViewById(R.id.dice_result_2);
    	imageSelect = rand.nextInt(6) + 1;
    	switch (imageSelect) {
    	case 1:
    		iv2.setImageResource(R.drawable.alea_1);
    		break;
    	case 2:
    		iv2.setImageResource(R.drawable.alea_2);
    		break;
    	case 3:
    		iv2.setImageResource(R.drawable.alea_3);
    		break;
    	case 4:
    		iv2.setImageResource(R.drawable.alea_4);
    		break;
    	case 5:
    		iv2.setImageResource(R.drawable.alea_5);
    		break;
   		default:
    		iv2.setImageResource(R.drawable.alea_6);
    		break;
    	}
    }
}
