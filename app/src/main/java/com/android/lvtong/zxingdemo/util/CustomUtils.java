package com.android.lvtong.zxingdemo.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * @author 22939
 */
public class CustomUtils {

    /**
     * 复制内容到剪切板
     *
     * @param context 上下文
     * @param copyStr 内容
     * @return 是否成功
     */
    public static boolean copy(Context context, String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
