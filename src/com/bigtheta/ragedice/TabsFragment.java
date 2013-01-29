package com.bigtheta.ragedice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabsFragment extends Fragment {
    private FragmentTabHost m_tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        m_tabHost = new FragmentTabHost(getActivity());
        m_tabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabs_content_container);

        //m_tabHost.addTab(m_tabHost.newTabSpec("glf").setIndicator("GLF"), GameLogFragment.class, null);
        m_tabHost.addTab(m_tabHost.newTabSpec("hgf").setIndicator(
                "Histogram", getResources().getDrawable(R.drawable.histo_tab_selected)), HistogramRollsFragment.class, null);
        m_tabHost.addTab(m_tabHost.newTabSpec("hptf").setIndicator(
                "Player Time Histogram"), HistogramPlayerTimeFragment.class, null);
        m_tabHost.addTab(m_tabHost.newTabSpec("ksdf").setIndicator(
                "Statistics", getResources().getDrawable(R.drawable.stats_tab_selected)), KSDescriptionFragment.class, null);
        return m_tabHost;
    }

    public void refreshDisplay() {
        long gameId = MainActivity.getGame().getId();
        FragmentManager fm = getChildFragmentManager();
        Fragment c_fragment = fm.findFragmentById(R.id.tabs_content_container);
        if (c_fragment == null) {
            throw new IllegalStateException("Tabs container contains no fragments.");
        } else {
            if (c_fragment instanceof KSDescriptionFragment) {
                ((KSDescriptionFragment)c_fragment).displayInfo(gameId);
            } else if (c_fragment instanceof GameLogFragment) {
                DiceRoll dr = DiceRoll.getLastDiceRoll(gameId);
                Player nextPlayer = Player.getLastPlayer(gameId);
                ((GameLogFragment)c_fragment).displayInfo(nextPlayer, dr);
            } else if (c_fragment instanceof HistogramRollsFragment) {
                ((HistogramRollsFragment)c_fragment).updateHistogram();
            } else if (c_fragment instanceof HistogramPlayerTimeFragment) {
                ((HistogramPlayerTimeFragment)c_fragment).updateHistogram();
            }
        }
    }

    public void nextTab() {
        m_tabHost.setCurrentTab(m_tabHost.getCurrentTab() + 1);
    }

    public void prevTab() {
        m_tabHost.setCurrentTab(m_tabHost.getCurrentTab() - 1);
    }
}
