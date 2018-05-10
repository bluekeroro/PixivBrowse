package com.bluekeroro.android.pixivbrowse;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by BlueKeroro on 2018/5/9.
 */
public class PhotoActivity extends SingleFragmentActivity {
    public static final String GETPHOTOURL="getPhotoUrl";
    public static final String GETTITLE="getTitle";
    public static final String GETTAGS="getTags";

    public static Intent newIntent(Context context,GalleryItem mGalleryItem){
        Intent i=new Intent(context,PhotoActivity.class);
        i.putExtra(GETPHOTOURL,mGalleryItem.getPhotoUrl());
        i.putExtra(GETTITLE,mGalleryItem.getTitle());
        i.putExtra(GETTAGS,mGalleryItem.getTags());
        return i;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.photo_activity_fragment;
    }
    @Override
    protected Fragment createFragment() {
        return PhotoFragment.newInstance();
    }
}
