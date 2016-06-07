package com.soshified.soshified.articles;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.soshified.soshified.R;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.data.source.local.LocalArticlesDataSource;
import com.soshified.soshified.data.source.remote.RemoteArticlesDataSource;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.soshified.soshified.data.source.ArticlesRepository.ARTICLE_TYPE_NEWS;
import static com.soshified.soshified.data.source.ArticlesRepository.ARTICLE_TYPE_STYLE;
import static com.soshified.soshified.data.source.ArticlesRepository.ARTICLE_TYPE_SUBS;

public class ArticlesActivity extends AppCompatActivity {

    @Bind(R.id.main_navigation_drawer)  DrawerLayout mDrawerLayout;
    @Bind(R.id.main_navigation_view) NavigationView mNavigationView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private int mCurrentType = ARTICLE_TYPE_NEWS;
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
        changeFragment(ARTICLE_TYPE_NEWS);
    }

    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_item_news:
                    mCurrentType = ARTICLE_TYPE_NEWS;
                    break;
                case R.id.navigation_item_style:
                    mCurrentType = ARTICLE_TYPE_STYLE;
                    break;

            }
            changeFragment(mCurrentType);
            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            return false;
        });
    }

    private void changeFragment(int source) {

        // Add Fragment
        ArticlesFragment articlesFragment = (ArticlesFragment) getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(source));

        if (articlesFragment == null) {
            articlesFragment = ArticlesFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, articlesFragment, String.valueOf(source))
                    .addToBackStack(String.valueOf(source))
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
