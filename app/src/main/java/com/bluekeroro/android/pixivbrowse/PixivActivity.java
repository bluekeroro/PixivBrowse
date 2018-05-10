package com.bluekeroro.android.pixivbrowse;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;
import java.util.List;

public class PixivActivity extends TabActivity{
    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(PixivFragment.newInstance("daily"));
        fragments.add(PixivFragment.newInstance("weekly"));
        fragments.add(PixivFragment.newInstance("monthly"));
        fragments.add(PixivFragment.newInstance("rookie"));
        /*fragments.add(PixivFragment.newInstance("original"));
        fragments.add(PixivFragment.newInstance("male"));
        fragments.add(PixivFragment.newInstance("female"));*/
        return fragments;
    }

    @Override
    protected List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("Daily");
        list.add("Weekly");
        list.add("Monthly");
        list.add("Rookie");
        /*list.add("Original");
        list.add("Male");
        list.add("Female");*/
        //could add more...
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }


}
/*public class PixivActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context){
        return new Intent(context,PixivActivity.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.pixiv_activity_fragment;
    }

    @Override
    protected Fragment createFragment() {
        return PixivFragment.newInstance();
    }
}*/
