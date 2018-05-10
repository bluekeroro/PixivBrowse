package com.bluekeroro.android.pixivbrowse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by BlueKeroro on 2018/5/9.
 */
public class PhotoFragment extends Fragment {
    private String mPhotoUrl;
    private String mTitle;
    private String mTags;
    private ImageView mPhotoImageView;
    private TextView mTitleTextView;
    private TextView mTagTextView;
    private byte[] bitmapBytes;
    private Bitmap bitmap;
    private Handler mHandler;
    private Thread mThread;
    public static PhotoFragment newInstance(){
        return new PhotoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getActivity().getIntent();
        mPhotoUrl=i.getExtras().getString(PhotoActivity.GETPHOTOURL,null);
        mTitle=i.getExtras().getString(PhotoActivity.GETTITLE,null);
        mTags=i.getExtras().getString(PhotoActivity.GETTAGS,null);
        mHandler=new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_details,container,false);
        mPhotoImageView=(ImageView) v.findViewById(R.id.PhotoImageView);
        mTitleTextView=(TextView)v.findViewById(R.id.TitleTextView);
        mTagTextView=(TextView)v.findViewById(R.id.TagTextView);
        mTitleTextView.setText(mTitle);
        mTagTextView.setText(mTags);
        mThread=new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    bitmapBytes=new PixivGet().getUrlBytes(mPhotoUrl);
                    bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
                } catch (IOException e) {
                    Log.e("test","Error downloading image11111",e);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                        mPhotoImageView.setImageDrawable(drawable);
                    }
                });
            }
        });
        mThread.start();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mThread.interrupt();
    }
}
