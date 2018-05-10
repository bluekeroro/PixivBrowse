package com.bluekeroro.android.pixivbrowse;

import android.net.Uri;

/**
 * Created by BlueKeroro on 2018/4/6.
 */
public class GalleryItem {
    private String caption;
    private String title;
    private String id;
    private String[] tags;
    private image_urls_class image_urls;
    private String width;
    private String height;
    private class image_urls_class{
        public String px_128x128;
        public String px_480mw;
    }
    public String  getPhotoUrl(){
        return image_urls.px_480mw;
    }
    public String getUrl() {
        //return image_urls.px_480mw;
        return image_urls.px_128x128;
    }
    @Override
    public String toString() {
        return caption;
    }

    public String getWidth() {
        return width;
    }

    public String getTitle() {
        return title;
    }

    public String getHeight() {
        return height;
    }

    public String getTags() {
        StringBuffer i=new StringBuffer("");
        for(String s:tags){
            i.append(s+",");
        }
        i.deleteCharAt(i.length()-1);
        return i.toString();
    }

    public String getId() {
        return id;
    }
}
