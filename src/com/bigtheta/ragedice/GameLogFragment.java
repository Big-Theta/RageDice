package com.bigtheta.ragedice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameLogFragment extends Fragment {
    GameLogListener m_callback;

    public interface GameLogListener {
        public void onGameLogSelected(int position);
        public View findViewById(int id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_layout, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_callback = (GameLogListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                                         " must implement GameLogListener");
        }
    }

    public void displayInfo(Player player, DiceRoll dr) {
        if (dr == null) {
            return;
        }
        TextView tv = (TextView) m_callback.findViewById(R.id.log_view);
        tv.append(player.getPlayerName() + " rolled " +
                    dr.getTotalResult() + "\n");


    }
}

