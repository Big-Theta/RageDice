package com.bigtheta.ragedice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistogramRollsFragment extends Fragment {
    HistogramRollsListener m_callback;

    public interface HistogramRollsListener {
        public void onHistogramRollsSelected(int position);
        public View findViewById(int id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.histogram_rolls_layout, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_callback = (HistogramRollsListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                                         " must implement HistogramRollsListener");
        }
    }

    public void updateHistogram() {
        m_callback.findViewById(R.id.histogram_rolls_view).invalidate();
    }
}

