package com.example.drwernavifor8;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_class extends android.support.v4.app.Fragment implements MainActivity.OnHeadlineSelectedListener {
	TextView tv;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root=inflater.inflate(R.layout.fragment_main, container,
				false);
		tv=(TextView) root.findViewById(R.id.updation);
		tv.setText("Default");
		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onArticleSelected(String position) {
		// TODO Auto-generated method stub
		tv.setText("OPtion "+position+" selected");
	}

	
	
}
