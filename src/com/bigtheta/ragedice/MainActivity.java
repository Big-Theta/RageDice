package com.bigtheta.ragedice;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

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
    	TextView tv1 = (TextView)findViewById(R.id.dice_result_1);
    	tv1.setText(Integer.toString(rand.nextInt(6) + 1));
    	
    	TextView tv2 = (TextView)findViewById(R.id.dice_result_2);
    	tv2.setText(Integer.toString(rand.nextInt(6) + 1));
    }
}
