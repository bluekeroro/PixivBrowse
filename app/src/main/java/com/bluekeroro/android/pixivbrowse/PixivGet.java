package com.bluekeroro.android.pixivbrowse;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BlueKeroro on 2018/4/6.
 */
public class PixivGet {
    private static final String TAG="PixivGet";
    //https://api.imjad.cn/pixiv/v1/?type=rank&content=illust&mode=monthly&per_page=20&page=1
    private static final String RANK ="rank";
    private static final String SEARCH ="search";
    private static final String GET_MODE="monthly";
    static Date mNowDate=new Date();
    static SimpleDateFormat sDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    private static final Uri ENDPOINT= Uri.parse("https://api.imjad.cn/pixiv/v1/")
                                                .buildUpon()
                                                .build();
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()+": with"+urlSpec);
            }
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead=in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlString)throws IOException{
        return new String(getUrlBytes(urlString));
    }
    private List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> items=new ArrayList<>();
        try {
            String jsonString= getUrlString(url);
            parseItemsByGson(items,jsonString);
            Log.i(TAG,"Received JSON: "+jsonString);
        }catch (IOException ioe){
            Log.e(TAG,"Failed to fetch items",ioe);
        }
        return items;
    }
    private void parseItemsByGson(List<GalleryItem> items,String jsonString){
        Gson gson=new Gson();
        JsonObject jsonBody=gson.fromJson(jsonString,JsonObject.class);
        JsonArray responseJsonArray=jsonBody.getAsJsonArray("response");
        if(responseJsonArray.size()==0){
            return ;
        }
        JsonObject responseJsonObject=responseJsonArray.get(0).getAsJsonObject();
        JsonArray worksJsonArray=responseJsonObject.getAsJsonArray("works");
        if(worksJsonArray!=null){
            for(int i=0;i<worksJsonArray.size();i++){
                JsonObject photoJsonObject=worksJsonArray.get(i).getAsJsonObject().getAsJsonObject("work");
                GalleryItem item=gson.fromJson(photoJsonObject,GalleryItem.class);
                items.add(item);
            }
        }else{
            for(int i=0;i<responseJsonArray.size();i++){
                JsonObject photoJsonObject=responseJsonArray.get(i).getAsJsonObject();
                GalleryItem item=gson.fromJson(photoJsonObject,GalleryItem.class);
                items.add(item);
            }
        }

    }
    private String buildUri(String getMode){
        Uri.Builder uriBuilder=ENDPOINT.buildUpon()
                .appendQueryParameter("type", RANK)
                .appendQueryParameter("content","illust")
                .appendQueryParameter("per_page","3000")
                .appendQueryParameter("page","1")
                .appendQueryParameter("date",sDateFormat.format(new Date(mNowDate.getTime()-(long)2*24*60*60*1000)))
                .appendQueryParameter("mode",getMode);
        return uriBuilder.build().toString();
    }
    public List<GalleryItem> getPhotos(String getMode){
        String url;
        if(getMode==null){
            url=buildUri(GET_MODE);
        }else{
            url=buildUri(getMode);
        }
        return downloadGalleryItems(url);
    }
    //https://api.imjad.cn/pixiv/v1/?type=search&word=%E7%99%BE%E5%90%88&mode=tag
    private String buildSearchUri(String word){
        Uri.Builder uriBuilder=ENDPOINT.buildUpon()
                .appendQueryParameter("type", SEARCH)
                .appendQueryParameter("word", word)
                .appendQueryParameter("per_page","3000")
                .appendQueryParameter("page","1")
                .appendQueryParameter("mode","tag");
        return uriBuilder.build().toString();
    }
    public List<GalleryItem> getSearchPhotos(String word){
        String url;
        if(word==null){
            url=buildUri(GET_MODE);
        }else{
            url=buildSearchUri(word);
        }
        return downloadGalleryItems(url);
    }

}
