package com.ysq.wifisignin.ui.common;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;

import com.ysq.wifisignin.Application;

import java.io.File;

/**
 * 对返回图片的进行处理和解析
 * @author passerbyYSQ
 * @create 2020-05-10 13:42
 */
public class PhotoSelectedHelper {

    // 获取drawable的本地路径
    public static Uri getDrawableUri(@DrawableRes int drawable) {
        Resources res = Application.getInstance().getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(drawable) + "/" +
                res.getResourceTypeName(drawable) + "/" +
                res.getResourceEntryName(drawable));
        return uri;
    }

    public static String parseImgUri(Uri imgUri) {
        return Build.VERSION.SDK_INT >= 19 ?
                handleImgOnKitKat(imgUri) : handleImgBeforeKitKat(imgUri);
    }

    @RequiresApi(19)
    private static String handleImgOnKitKat(Uri imgUri) {
        String imgPath = null;
        Uri uri = imgUri;
        if (DocumentsContract.isDocumentUri(Application.getInstance(), uri)) {
            // 如果是Document类型的Uri，则通过Document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImgPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imgPath = getImgPath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的uri，则使用普通方式处理
            imgPath = getImgPath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }
        return imgPath;
    }

    private static String handleImgBeforeKitKat(Uri imgUri) {
        return getImgPath(imgUri, null);
    }

    private static String getImgPath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实图片的路径
        Cursor cursor = Application.getInstance().getContentResolver()
                .query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
