package com.bigtheta.ragedice;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KSDescriptionFragment extends Fragment {
    KSDescriptionListener m_callback;

    public interface KSDescriptionListener {
        public void onKSDescriptionSelected(int position);
        public View findViewById(int id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        //FragmentManager fm = getActivity().getSupportFragmentManager();
        return inflater.inflate(R.layout.ksdescription_layout, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        displayInfo(MainActivity.getGame().getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_callback = (KSDescriptionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement KSDescriptionListener");
        }
    }

    public void displayInfo(long gameId) {
        TextView tv = (TextView) m_callback.findViewById(R.id.ksdescription_view);
        String info = "";
        if (DiceRoll.getNumDiceRolls() < 4) {
            info = "Not enough rolls have been made to calculate statistics.";
        } else {
            info += "\nThe probability the dice are fair based on the Kolmogorov-Smirnov distribution: " + Double.toString(DiceRoll.calculateKSPValue(gameId));
            info += "\n";
            info += DieDescription.getKSDescription(gameId);

            info += "\n\nThe probability the dice are fair based on the central limit theorem: " + Double.toString(DiceRoll.calculateCentralLimitProbabilityPValue(gameId));
            info += "\n";
            info += DieDescription.getCLTDescription(gameId);
        }
        tv.setText(info);
    }
}
