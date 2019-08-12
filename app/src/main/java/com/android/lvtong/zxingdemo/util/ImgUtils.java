package com.android.lvtong.zxingdemo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author 22939 图片工具类
 */
public class ImgUtils {

    public static Boolean saveImageToGallery(Context context, Bitmap bmp) {

        //首先保存图片 创建文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "zxingdemo");
        if (!appDir.exists()) {
            if (!appDir.mkdir()) {
                Toast.makeText(context, "创建目录失败", Toast.LENGTH_SHORT)
                     .show();
                return false;
            }
        }
        //图片文件名称
        String fileName = "qr_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.d("ImgUtils", "e:" + e);
            return false;
        }

        // 其次把文件插入到系统图库
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            Log.d("ImgUtils", "e:" + e);
            return false;
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT)
             .show();
        return true;
    }
}
