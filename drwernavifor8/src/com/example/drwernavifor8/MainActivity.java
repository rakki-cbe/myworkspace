package com.example.drwernavifor8;



import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	/***Interface to communicate wirth other fragments***/
	OnHeadlineSelectedListener mCallback;
	public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String position);
    }
	/***Interface to communicate with other fragments***/
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
 
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        mTitle = "Employe Details";
        
        mPlanetTitles = new String[]{"Employee List", "Create New Employe", "Edit Employee"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        /***THis will be used to creatre a custom action bar **/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        /***THis will be used to creatre a custom action bar click listener **/
        View v =getSupportActionBar().getCustomView();
        ((Button)v.findViewById(R.id.smaple_click)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*****interface used to set the text into another fragment******/
				mCallback.onArticleSelected(" clicked the sample button");
			}
		});
        /***THis will be used to create a custom action bar click listener ends**/
        /***THis will be used to create a custom action bar ends **/
        // Set the adapter for the list view    
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
 
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
 
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            	getSupportActionBar().setTitle(mTitle);
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            	getSupportActionBar().setTitle(mTitle);
            }
        };
 
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Fragment_class fl =new Fragment_class();
        try {
            mCallback = (OnHeadlineSelectedListener) fl;
        } catch (ClassCastException e) {
            throw new ClassCastException(fl.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame,fl ).commit();
       
       }
 
    @SuppressLint("NewApi")
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         SearchView searchView =
                 (SearchView) menu.findItem(R.id.action_search).getActionView();
         searchView.setSearchableInfo(
                 searchManager.getSearchableInfo(getComponentName()));*/

        return true;
    }
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
    	
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
 
        return super.onOptionsItemSelected(item);
    }
 
    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
 
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        mCallback.onArticleSelected(mPlanetTitles[position]);

        }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
 
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

	
 
}