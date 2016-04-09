package com.soshified.soshified.articles;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.soshified.soshified.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticlesActivity extends AppCompatActivity {

    @Bind(R.id.main_navigation_drawer)  DrawerLayout mDrawerLayout;
    @Bind(R.id.main_navigation_view) NavigationView mNavigationView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles_activity);

        ButterKnife.bind(this);

        // ToolBar/ActionBar, whatever the kids call it
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.news_title));

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Drawer Layout
        if (mDrawerLayout != null) {
            setupDrawer();
        }

        // Add Fragment
        ArticlesFragment articlesFragment = (ArticlesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (articlesFragment == null) {
            articlesFragment = ArticlesFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, articlesFragment)
                    .commit();
        }

        new ArticlesPresenter(articlesFragment);
    }

    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    //TODO Do stuff
                }
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
    }

//TODO Menu items
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.action_settings:
//                return true;
//            case android.R.id.home:
//                toggleDrawer();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
