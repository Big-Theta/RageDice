package com.bigtheta.ragedice;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigtheta.ragedice.R.drawable;


public class DiceDisplayFragment extends Fragment  {
    DiceDisplayListener m_callback;

    public interface DiceDisplayListener {
        public void onDiceSelected(int position);
        public View findViewById(int id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dice_layout, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_callback = (DiceDisplayListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DiceDisplayListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void rollTest() {
        m_callback.onDiceSelected(0);
    }

    public void displayDiceRoll(DiceRoll dr) {
        //TextView tv = (TextView) m_callback.findViewById(R.id.player_number);
        //Player currentPlayer = Player.retrieve(dr.getPlayerId());
        //tv.setText(currentPlayer.getPlayerName());
        if (dr == null) {
            return;
        }

        Class<drawable> res = R.drawable.class;
        for (DieResult result : DieResult.getDieResults(dr)) {
            DieDescription dd = DieDescription.retrieve(result.getDieDescriptionId());
            ImageView iv = (ImageView)m_callback.findViewById(dd.getImageViewResource());
            try {
                iv.setImageResource(result.getImageResource());
                Log.e("res is: ", Integer.toString(result.getImageColorResource()));
                //getResources().getColor(result.getImageColorResource());
                iv.setBackgroundColor(getResources().getColor(result.getImageColorResource()));
            } catch (Exception err) {
                Log.e("MainActivity::displayDiceRoll", err.getCause().getMessage());
            }
        }
    }
}

