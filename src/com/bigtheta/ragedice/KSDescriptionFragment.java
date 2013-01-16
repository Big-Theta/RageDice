package com.bigtheta.ragedice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KSDescriptionFragment extends Fragment {
	KSDescriptionListener mCallback;
	
	public interface KSDescriptionListener {
		public void onKSDescriptionSelected(int position);
		public View findViewById(int id);
	}
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                         Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ksdescription_layout, container, false);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	mCallback = (KSDescriptionListener) activity;
        } catch (ClassCastException e) {
        	throw new ClassCastException(activity.toString()
        			+ " must implement KSDescriptionListener");
        }
    }
    
    public void displayInfo(String info) {
    	TextView tv = (TextView) mCallback.findViewById(R.id.ksdescription_view);
    	tv.setText(info);
    	
    	
    }
}
