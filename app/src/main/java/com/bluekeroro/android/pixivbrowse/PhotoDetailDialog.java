package com.bluekeroro.android.pixivbrowse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by BlueKeroro on 2018/5/9.
 */
public class PhotoDetailDialog extends DialogFragment {
    private ImageView mPhotoImageView;
    private TextView mTitleTextView;
    private TextView mTagTextView;
    private TextView mWaitTextView;
    private byte[] bitmapBytes;
    private Bitmap bitmap;
    private Handler mHandler;
    private Thread mThread;
    private GalleryItem mGalleryItem;
    private FileUtils LocalCache;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo_details,null);
        mPhotoImageView=(ImageView) v.findViewById(R.id.PhotoImageView);
        mTagTextView=(TextView)v.findViewById(R.id.TagTextView);
        mWaitTextView=(TextView)v.findViewById(R.id.WaitTextView);
        mTagTextView.setText("TAG: "+mGalleryItem.getTags()+"\n"+mGalleryItem.getPhotoUrl());
        LocalCache=new FileUtils(getActivity());
        mHandler=new Handler();
        mThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url=mGalleryItem.getPhotoUrl();
                    String[] urlSplit=url.split("/");
                    String fileName=urlSplit[urlSplit.length-1];
                    if(LocalCache.isFileExists(fileName)){
                        bitmap=LocalCache.getBitmap(fileName);
                    }else{
                        bitmapBytes=new PixivGet().getUrlBytes(mGalleryItem.getPhotoUrl());
                        bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
                        LocalCache.saveBitmap(fileName,bitmap);
                    }
                } catch (IOException e) {
                    Log.e("test","Error downloading image11111",e);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                        mPhotoImageView.getWidth();
                        //drawable.setBounds(0,0,mPhotoImageView.getWidth(),0);
                        mPhotoImageView.setAdjustViewBounds(true);
                        mPhotoImageView.setImageDrawable(drawable);
                        mWaitTextView.setVisibility(View.GONE);
                        if(bitmap.getWidth()>mPhotoImageView.getWidth()){
                            Log.i("test","bitmap.getWidth()>mPhotoImageView.getWidth()"+true);
                        }else{
                            Log.i("test","bitmap.getWidth()>mPhotoImageView.getWidth()"+false);
                        }
                    }
                });
            }
        });
        mThread.start();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mGalleryItem.getTitle())
                .create();
    }

    public void setGalleryItem(GalleryItem galleryItem) {
        mGalleryItem = galleryItem;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mThread.interrupt();
    }
}
