package com.android.lvtong.zxingdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static com.android.lvtong.zxingdemo.util.NetworkUtils.buildUrl;

/**
 * @author 22939
 */
public class QRcodeActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private ImageView mImageView;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.backup_32);
            actionBar.setElevation(0);
        }

        initView();
        loadData();
        String url = buildUrl(firstName, lastName).toString();
        createImage(url);
    }

    private void initView() {
        //获取屏幕宽度
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay()
               .getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        //设置ImageView的宽高
        mImageView = findViewById(R.id.imageView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        mImageView.setLayoutParams(params);
    }

    private void loadData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        firstName = bundle.getString("first_name");
        lastName = bundle.getString("last_name");
    }

    public void createImage(String url) {

        int w = width;
        int h = width;
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            //显示到我们的ImageView上面
            mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
