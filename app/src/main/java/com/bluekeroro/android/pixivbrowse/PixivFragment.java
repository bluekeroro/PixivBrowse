package com.bluekeroro.android.pixivbrowse;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by BlueKeroro on 2018/4/5.
 */
public class PixivFragment extends Fragment {
    private static final String DIALOG_PHOTO="DialogPhoto";
    private RecyclerView mPhotoRecyclerView;
    private TextView mTextView;
    private List<GalleryItem> mItems=new ArrayList<>();
    public static PixivFragment newInstance(){
        return new PixivFragment();
    }
    private static final String TAG="PhotoGalleryFragment";
    private ThumbnailDownLoader<PhotoHolder> mThumbnailDownLoader;
    private ThumbnailDownLoader<PhotoHolder> mThumbnailDownLoader1;
    private ThumbnailDownLoader<PhotoHolder> mThumbnailDownLoader2;
    private PhotoAdapter mPhotoAdapter;
    private static final String ITEMPOSITION="ItemPosition";
    private int position;
    private LruCache<String,Bitmap> mLruCache;
    private FileUtils LocalCache;
    private ConcurrentMap<PhotoHolder,String> mRequestMap;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //================================================
        long maxMemory=Runtime.getRuntime().maxMemory();
        mLruCache=new LruCache<String,Bitmap>((int)(maxMemory/8)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        //==========================================
        LocalCache=new FileUtils(getActivity());
        mRequestMap=new ConcurrentHashMap<>();
        //===========================================
        Handler responseHandler=new Handler();
        mThumbnailDownLoader=new ThumbnailDownLoader<>(responseHandler,LocalCache,mLruCache,mRequestMap);
        mThumbnailDownLoader.setThumbnailDownLoadListener(
                new ThumbnailDownLoader.ThumbnailDownLoadListener<PhotoHolder>(){
                    @Override
                    public void onThumbnailDownLoaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                        photoHolder.bindGalleryDrawable(drawable);
                        Log.i("test","绑定获取视图"+photoHolder.toString());
                    }
                });
        mThumbnailDownLoader.start();
        mThumbnailDownLoader.getLooper();
        mThumbnailDownLoader1=new ThumbnailDownLoader<>(responseHandler,LocalCache,mLruCache,mRequestMap);
        mThumbnailDownLoader1.setThumbnailDownLoadListener(
                new ThumbnailDownLoader.ThumbnailDownLoadListener<PhotoHolder>(){
                    @Override
                    public void onThumbnailDownLoaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                        photoHolder.bindGalleryDrawable(drawable);
                        Log.i("test","绑定获取视图"+photoHolder.toString());
                    }
                });
        mThumbnailDownLoader1.start();
        mThumbnailDownLoader1.getLooper();
        mThumbnailDownLoader2=new ThumbnailDownLoader<>(responseHandler,LocalCache,mLruCache,mRequestMap);
        mThumbnailDownLoader2.setThumbnailDownLoadListener(
                new ThumbnailDownLoader.ThumbnailDownLoadListener<PhotoHolder>(){
                    @Override
                    public void onThumbnailDownLoaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                        photoHolder.bindGalleryDrawable(drawable);
                        Log.i("test","绑定获取视图"+photoHolder.toString());
                    }
                });
        mThumbnailDownLoader2.start();
        mThumbnailDownLoader2.getLooper();
        //===========================================

        position=0;
        if(savedInstanceState!=null){
            position=savedInstanceState.getInt(ITEMPOSITION);
        }
        Log.i(TAG,"Background thread started");
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GridLayoutManager layoutManager=(GridLayoutManager)mPhotoRecyclerView.getLayoutManager();
        position=layoutManager.findFirstVisibleItemPosition();
        outState.putInt(ITEMPOSITION,position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView=(RecyclerView)v.findViewById(R.id.fragment_photo_recycler_view);
        mTextView=(TextView)v.findViewById(R.id.fragment_photo_loading);
        mTextView.setVisibility(View.INVISIBLE);
        ViewTreeObserver observer=mPhotoRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),mPhotoRecyclerView.getWidth()/300));
                mPhotoRecyclerView.scrollToPosition(position);
                mPhotoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateItem();
            }
        });
        setupAdapter();
        mPhotoRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1)){
                    Log.i("onScrollStateChanged","onScrollStateChanged"+"bottom");
                    //updateItem();
                }
            }
        });
        return v;
    }
    private class GetItemsTask extends AsyncTask<Void,Void,List<GalleryItem>>{
        private String mode;
        public GetItemsTask(String mode){
            this.mode=mode;
        }
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if(mode==null){
                return new PixivGet().getPhotos("monthly");
            }else{
                return new PixivGet().getPhotos(mode);
            }
        }
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            if(mTextView!=null){
                mTextView.setVisibility(View.INVISIBLE);
            }
            mItems=galleryItems;
            setupAdapter();
        }
    }
    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;
        private TextView mItemRankTextView;
        private TextView mItemTitleTextView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView=(ImageView)itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            mItemRankTextView=(TextView) itemView.findViewById(R.id.fragment_photo_gallery_rank);
            mItemTitleTextView=(TextView) itemView.findViewById(R.id.fragment_photo_gallery_title);
            mItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    PhotoDetailDialog dialog=new PhotoDetailDialog();
                    dialog.setGalleryItem(mGalleryItem);
                    dialog.show(manager,DIALOG_PHOTO);
                    //Intent i=PhotoActivity.newIntent(getActivity(),mGalleryItem);
                    //startActivity(i);
                }
            });
        }
        public void bindGalleryDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }
        public void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem=galleryItem;
            int rank=this.getLayoutPosition()+1;
            mItemRankTextView.setText(""+rank);
            mItemTitleTextView.setText(galleryItem.getTitle());
        }
    }
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems=galleryItems;
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view=inflater.inflate(R.layout.gallery_item,parent,false);
            return new PhotoHolder(view);
        }
        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem=mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
            if(mRequestMap.get(holder)!=galleryItem.getUrl()){
                Drawable placeholder=getResources().getDrawable(R.drawable.bill_up_close);
                holder.bindGalleryDrawable(placeholder);
                Log.i("test","绑定默认视图加载图片"+holder.toString());
            }
            switch((int)(Math.random()*3)){
                case 0:
                    mThumbnailDownLoader.queueThumbnail(holder,galleryItem.getUrl());
                    break;
                case 1:
                    mThumbnailDownLoader1.queueThumbnail(holder,galleryItem.getUrl());
                    break;
                case 2:
                    mThumbnailDownLoader2.queueThumbnail(holder,galleryItem.getUrl());
                    break;
            }
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(mPhotoAdapter=new PhotoAdapter(mItems));
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownLoader.quit();
        Log.i(TAG,"Background thread destroyed");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownLoader.clearQueue();
    }


    private void updateItem(){
        if(mTextView!=null){
            mTextView.setVisibility(View.VISIBLE);
        }
        //String query=QueryPreferences.getStoredQuery(getActivity());
        new GetItemsTask(null).execute();
    }
}