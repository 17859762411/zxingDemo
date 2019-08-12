package com.android.lvtong.zxingdemo.util;

import android.net.Uri;

/**
 * @author 22939
 */
public class NetworkUtils {

    private static final String BASE_URL = "mytest://com.android.lvtong.zxingdeno";

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    public static Uri buildUrl(String firstName, String lastName) {
        return Uri.parse(BASE_URL)
                  .buildUpon()
                  .appendQueryParameter(FIRST_NAME, firstName)
                  .appendQueryParameter(LAST_NAME, lastName)
                  .build();
    }
}
