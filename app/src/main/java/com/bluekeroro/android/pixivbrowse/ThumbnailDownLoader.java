package com.bluekeroro.android.pixivbrowse;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by BlueKeroro on 2018/4/6.
 */
public class ThumbnailDownLoader<T> extends HandlerThread {
    private static final String TAG="ThumbnailDownLoader";
    private Boolean mHasQuit=false;
    private static final int MESSAGE_DOWNLOAD=0;
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap=new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownLoadListener<T> mThumbnailDownLoadListener;
    private LruCache<String,Bitmap> mLruCache;
    private FileUtils LocalCache;
    public interface ThumbnailDownLoadListener<T>{
        void onThumbnailDownLoaded(T target, Bitmap bitmap);
    }
    public void setThumbnailDownLoadListener(ThumbnailDownLoadListener<T> listener) {
        mThumbnailDownLoadListener=listener;
    }
    public ThumbnailDownLoader(Handler responseHandler,FileUtils LocalCache,LruCache<String,Bitmap> mLruCache,ConcurrentMap<T,String> mRequestMap) {
        super(TAG);
        this.LocalCache=LocalCache;
        mResponseHandler=responseHandler;
        this.mLruCache=mLruCache;
        this.mRequestMap=mRequestMap;
    }
    @Override
    public boolean quit() {
        mHasQuit=true;
        return super.quit();
    }
    public void queueThumbnail(final T target,final String url){
        String[] urlSplit=url.split("/");
        final String fileName=urlSplit[urlSplit.length-1];
        if(url==null){
            mRequestMap.remove(target);
            Log.i("test","没有url"+target.toString());
        }else if(mLruCache.get(url)!=null){
            final Bitmap bitmap=mLruCache.get(url);
            mRequestMap.put(target,url);
            Log.i("test","get from mLruCache"+target.toString());
            updateMainThread(target,url,bitmap);
        }else if(LocalCache.isFileExists(fileName)){
            /*final Bitmap bitmap=LocalCache.getBitmap(fileName);
            mRequestMap.put(target,url);
            Log.i("test","get from LocalCache"+target.toString());
            updateMainThread(target,url,bitmap);*/
            mRequestMap.put(target,url);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap=LocalCache.getBitmap(fileName);
                    mResponseHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mRequestMap.get(target)!=url||mHasQuit){
                                Log.i("test","mRequestMap.get(target)!=url");
                                return ;
                            }
                            mRequestMap.remove(target);
                            Log.i("test","mResponseHandler"+target.toString()+Thread.currentThread().toString());
                            mThumbnailDownLoadListener.onThumbnailDownLoaded(target,bitmap);
                        }
                    });
                }
            }).start();
        }else{
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget();
            Log.i("test","get from MessageQueue"+target.toString());
        }
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==MESSAGE_DOWNLOAD){
                    T target=(T)msg.obj;
                    Log.i(TAG,"Got a request for URL: "+mRequestMap.get(target)+" "+target.toString());
                    handleRequest(target);
                }
            }
        };
    }
    private void handleRequest(final T target){
        try {
            final String url=mRequestMap.get(target);
            if(url==null){
                return ;
            }
            byte[] bitmapBytes;
            final Bitmap bitmap;
            String[] urlSplit=url.split("/");
            String fileName=urlSplit[urlSplit.length-1];
            if(mLruCache.get(url)!=null){
                bitmap=mLruCache.get(url);
            }else if( LocalCache.isFileExists(fileName)){
                bitmap=LocalCache.getBitmap(fileName);
            }else{
                Log.i("test","try to got from MessageQueue"+target.toString()+Thread.currentThread().getName());
                bitmapBytes=new PixivGet().getUrlBytes(url);
                bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
                mLruCache.put(url,bitmap);
                LocalCache.saveBitmap(fileName,bitmap);
                Log.i(TAG,"Bitmap created"+mLruCache.size());
                Log.i("test","has got from MessageQueue"+target.toString()+Thread.currentThread().toString());
            }
            updateMainThread(target,url,bitmap);
        }catch (IOException ioe){
            Log.e("test","Error downloading image11111",ioe);
        }
    }
    public void updateMainThread(final T target, final String url,final Bitmap bitmap){
        Log.i("test","updateMainThread"+target.toString());
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mRequestMap.get(target)!=url||mHasQuit){
                    Log.i("test","mRequestMap.get(target)!=url");
                    return ;
                }
                mRequestMap.remove(target);
                Log.i("test","mResponseHandler"+target.toString()+Thread.currentThread().toString());
                mThumbnailDownLoadListener.onThumbnailDownLoaded(target,bitmap);
            }
        });
    }
    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        LocalCache.deleteFileByNumber();
    }
}
