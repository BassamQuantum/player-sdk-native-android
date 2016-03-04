package com.kaltura.playersdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by itayi on 3/5/15.
 */
public class Utilities {

    private static final String TAG = "Utilities";

    public static boolean doesPackageExist(String targetPackage, Context context){
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);

        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage)){
                return true;
            }
        }

        return false;
    }

    public static String readAssetToString(Context context, String asset) {
        try {
            InputStream assetStream = context.getAssets().open(asset);
            return fullyReadInputStream(assetStream, 1024*1024).toString();
        } catch (IOException e) {
            Log.e(TAG, "Failed reading asset " + asset, e);
            return null;
        }
    }

    @NonNull
    public static ByteArrayOutputStream fullyReadInputStream(InputStream inputStream, int byteLimit) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte data[] = new byte[1024];
        int count;
        
        while ((count = inputStream.read(data)) != -1) {
            int maxCount = byteLimit - bos.size();
            if (count > maxCount) {
                bos.write(data, 0, maxCount);
                break;
            } else {
                bos.write(data, 0, count);
            }
        }
        bos.flush();
        bos.close();
        inputStream.close();
        return bos;
    }

    public static Uri stripLastPathSegment(Uri uri) {
        String path = uri.getPath();
        path = stripLastPathSegment(path);
        return uri.buildUpon().path(path).clearQuery().fragment(null).build();
    }

    @NonNull
    public static String stripLastPathSegment(String path) {
        path = path.substring(0, path.lastIndexOf('/', path.length() - 2));
        return path;
    }

    public static String loadStringFromURL(Uri url, int byteLimit) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream is = conn.getInputStream();
        return fullyReadInputStream(is, byteLimit).toString();
    }
}
