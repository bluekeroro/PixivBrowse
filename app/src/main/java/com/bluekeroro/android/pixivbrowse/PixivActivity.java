package com.bluekeroro.android.pixivbrowse;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PixivActivity extends SingleFragmentActivity {
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
}
