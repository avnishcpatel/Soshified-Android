package com.soshified.soshified.articles;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.soshified.soshified.R;
import com.soshified.soshified.data.source.ArticlesDataSource;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.data.source.local.LocalArticlesDataSource;
import com.soshified.soshified.data.source.remote.RemoteArticlesDataSource;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ArticlesActivity extends AppCompatActivity {

    @Bind(R.id.main_navigation_drawer)  DrawerLayout mDrawerLayout;
    @Bind(R.id.main_navigation_view) NavigationView mNavigationView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private ArticlesDataSource.Article_Type mCurrentType = ArticlesDataSource.Article_Type.News;
    private static ArticlesPresenter mPresenter;

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

        //TODO Get default type from shared prefs
        changeFragment(ArticlesDataSource.Article_Type.News);
    }

    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_item_news:
                    mCurrentType = ArticlesDataSource.Article_Type.News;
                    break;
                case R.id.navigation_item_style:
                    mCurrentType = ArticlesDataSource.Article_Type.Style;
                    break;

            }
            changeFragment(mCurrentType);
            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            return false;
        });
    }

    private void changeFragment(ArticlesDataSource.Article_Type source) {

        // Add Fragment
        ArticlesFragment articlesFragment = (ArticlesFragment) getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(source));

        if (articlesFragment == null) {
            articlesFragment = ArticlesFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, articlesFragment, String.valueOf(source))
                    .commit();
        }

        if(mPresenter == null) {
            ArticlesRepository articlesRepository = ArticlesRepository.getInstance(
                    RemoteArticlesDataSource.getInstance(), LocalArticlesDataSource.getInstance());

            mPresenter = new ArticlesPresenter(articlesRepository, articlesFragment);
        } else {
            mPresenter.setView(articlesFragment);
        }
        mPresenter.setSource(source);
    }

}
