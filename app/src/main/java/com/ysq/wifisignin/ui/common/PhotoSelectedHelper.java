package com.ysq.wifisignin.ui.common;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.ysq.wifisignin.Application;

/**
 * 对返回图片的进行处理和解析
 * @author passerbyYSQ
 * @create 2020-05-10 13:42
 */
public class PhotoSelectedHelper {

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


}
