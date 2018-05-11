package com.bluekeroro.android.pixivbrowse;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by BlueKeroro on 2018/5/11.
 */
public class SearchActivity extends SingleFragmentActivity {
    private static String word;
    public static Intent newIntent(Context context,String word){
        SearchActivity.word=word;
        return new Intent(context,SearchActivity.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.pixiv_activity_fragment;
    }

    @Override
    protected Fragment createFragment() {
        return SearchFragment.newInstance(word);
    }
}
