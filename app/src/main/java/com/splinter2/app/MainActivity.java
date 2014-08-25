package com.splinter2.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
        MyLocationsFragment.OnFragmentInteractionListener,
        CompletedLocationsFragment.OnFragmentInteractionListener,
        TopLocationsFragment.OnFragmentInteractionListener
{
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String[] tabs = { "New", "Active", "Done" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        //set focus on active tab
        actionBar.setSelectedNavigationItem(1);

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onFragmentInteraction(Uri uri){
        String[] split = uri.toString().replaceFirst("^//", "").split("/");

        if (split[0].equals("coordinate") && split[1].equals("add")) {
            MyLocationsFragment fragment =
                    (MyLocationsFragment) getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:"+R.id.pager+":1");

            fragment.onRefresh();

            actionBar.setSelectedNavigationItem(1);
        }
    }

}
