package com.bigtheta.ragedice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabsFragment extends Fragment {
    private FragmentTabHost m_tabHost;
    TabsFragmentListener m_callback;
    boolean m_isTablet;

    public interface TabsFragmentListener {
        //public void onGameLogSelected(int position);
        public View findViewById(int id);
        public FragmentManager getSupportFragmentManager();
        public void manageAds(boolean display);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_callback = (TabsFragmentListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                                         " must implement TabsFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        m_isTablet = true;
        m_tabHost = new FragmentTabHost(getActivity());
        m_tabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabs_content_container);
        if (m_callback.getSupportFragmentManager().findFragmentById(R.id.dice_fragment_ui) == null) {
            m_isTablet = false;
            m_tabHost.addTab(m_tabHost.newTabSpec("ddf").setIndicator(
                    "Dice", getResources().getDrawable(R.drawable.dice_tab_selected)), DiceDisplayFragment.class, null);
        }
        m_tabHost.addTab(m_tabHost.newTabSpec("hgf").setIndicator(
                "Histogram", getResources().getDrawable(R.drawable.histo_tab_selected)), HistogramRollsFragment.class, null);
        m_tabHost.addTab(m_tabHost.newTabSpec("hptf").setIndicator(
                "Player Time Histogram", getResources().getDrawable(R.drawable.histo_tab_selected)), HistogramPlayerTimeFragment.class, null);
        m_tabHost.addTab(m_tabHost.newTabSpec("ksdf").setIndicator(
                "Statistics", getResources().getDrawable(R.drawable.stats_tab_selected)), KSDescriptionFragment.class, null);
        return m_tabHost;
    }

    public void refreshDisplay() {
        Log.e("> refreshDisplay", "m_isTablet: " + Boolean.toString(m_isTablet));
        long gameId = MainActivity.getGame().getId();
        FragmentManager fm = getChildFragmentManager();
        Fragment c_fragment = fm.findFragmentById(R.id.tabs_content_container);
        if (c_fragment == null) {
            throw new IllegalStateException("Tabs container contains no fragments.");
        } else {
            if (c_fragment instanceof DiceDisplayFragment) {
                ((DiceDisplayFragment)c_fragment).displayDiceRoll(DiceRoll.getLastDiceRoll(gameId));
                m_callback.manageAds(true);
            } else if (c_fragment instanceof KSDescriptionFragment) {
                ((KSDescriptionFragment)c_fragment).displayInfo(gameId);
                m_callback.manageAds(true);
            } else if (c_fragment instanceof GameLogFragment) {
                DiceRoll dr = DiceRoll.getLastDiceRoll(gameId);
                Player nextPlayer = Player.getLastPlayer(gameId);
                ((GameLogFragment)c_fragment).displayInfo(nextPlayer, dr);
                m_callback.manageAds(true);
            } else if (c_fragment instanceof HistogramRollsFragment) {
                ((HistogramRollsFragment)c_fragment).updateHistogram();
                if (m_isTablet) {
                    m_callback.manageAds(true);
                } else {
                    m_callback.manageAds(false);
                }
            } else if (c_fragment instanceof HistogramPlayerTimeFragment) {
                ((HistogramPlayerTimeFragment)c_fragment).updateHistogram();
                if (m_isTablet) {
                    m_callback.manageAds(true);
                } else {
                    m_callback.manageAds(false);
                }
            }
        }
    }

    public void nextTab() {
        m_tabHost.setCurrentTab(m_tabHost.getCurrentTab() + 1);
    }

    public void prevTab() {
        m_tabHost.setCurrentTab(m_tabHost.getCurrentTab() - 1);
    }

    public void setTab(int tabNumber) {
        m_tabHost.setCurrentTab(tabNumber);
    }

    public int getTabNumber() {
        return m_tabHost.getCurrentTab();
    }
}
