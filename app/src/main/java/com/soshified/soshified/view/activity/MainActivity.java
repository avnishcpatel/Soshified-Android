package com.soshified.soshified.view.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.soshified.soshified.R;
import com.soshified.soshified.view.fragment.NewsListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

    @Bind(R.id.main_progress) ProgressBar mProgress;
    @Bind(R.id.main_navigation_view) NavigationView mNavigationView;
    @Bind(R.id.main_navigation_drawer)  DrawerLayout mNavigationDrawer;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.news_title));

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mainActivity = this;

        changeFragment(new NewsListFragment());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                toggleDrawer();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleDrawer() {
        if(mNavigationDrawer.isDrawerOpen(Gravity.LEFT)) {
            mNavigationDrawer.closeDrawer(Gravity.LEFT);
        } else {
            mNavigationDrawer.openDrawer(Gravity.LEFT);
        }
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    public void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    public void toggleProgress(){
        if(mProgress.getVisibility() == View.VISIBLE)
            mProgress.setVisibility(View.GONE);
        else
            mProgress.setVisibility(View.VISIBLE);
    }
}
