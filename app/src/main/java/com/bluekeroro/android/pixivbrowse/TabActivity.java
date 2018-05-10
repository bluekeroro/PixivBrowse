package com.bluekeroro.android.pixivbrowse;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;


import java.util.List;

/**
 * Created by BlueKeroro on 2018/5/10.
 */
public abstract class TabActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SimpleFragmentPagerAdapter pagerAdapter;
    private Object mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_top);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);//保持加载未显示的两个页卡
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    protected abstract List<Fragment> getFragments();//返回tab对应的fragment
    protected abstract List<String> getTitles();//头部显示的title列表


    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {


        public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragments().get(position);
        }

        @Override
        public int getCount() {
            return getFragments().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (getTitles()!=null&&!getTitles().isEmpty()){
                return getTitles().get(position);
            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentItem=object;
        }
    }

    public Object getCurrentItem() {
        return mCurrentItem;
    }
}
