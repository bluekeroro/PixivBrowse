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
        List fragments = new ArrayList();
        fragments.add(PixivFragment.newInstance("daily"));
        fragments.add(PixivFragment.newInstance("weekly"));
        fragments.add(PixivFragment.newInstance("monthly"));
        return fragments;
    }

    @Override
    protected List<String> getTitles() {
        List list = new ArrayList();
        list.add("Daily");
        list.add("Weekly");
        list.add("Monthly");
        //could add more...
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
