package com.android.lvtong.zxingdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lvtong.implementationlibary.CustomDialog;
import com.android.lvtong.implementationlibary.DialogUtil;
import com.android.lvtong.zxingdemo.util.ImgUtils;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.android.lvtong.zxingdemo.util.NetworkUtils.buildUrl;

/**
 * @author 22939
 */
public class QRcodeActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private int width;

    private Bitmap llBitmap;
    private LinearLayout llCenter;
    private TextView tvFirstName;
    private TextView tvLastName;

    private boolean hasPermission = true;

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
        ImgUtils.createImage(url, width, width);
    }

    private void initView() {
        //获取屏幕宽度
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay()
               .getMetrics(outMetrics);
        width = outMetrics.widthPixels;

        //设置ImageView的宽高
        tvFirstName = findViewById(R.id.tv_first_name);
        tvLastName = findViewById(R.id.tv_last_name);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showSaveDialog();
                return true;
            }
        });

        //获取布局的bitmap
        llCenter = findViewById(R.id.ll_center);
        LinearLayout.LayoutParams llParams =
                new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        llCenter.setLayoutParams(llParams);
    }

    private void showSaveDialog() {
        DialogUtil.showDialog(this, "确认保存？", null, new CustomDialog.OnCustomClickedListener() {
            @Override
            public void onPositiveButtonClicked(CustomDialog dialog) {
                llBitmap = ImgUtils.getBitmap(llCenter);
                if (hasPermission) {
                    if (!ImgUtils.saveImageToGallery(QRcodeActivity.this, llBitmap)) {
                        Toast.makeText(QRcodeActivity.this, "保存失败", Toast.LENGTH_SHORT)
                             .show();
                    }
                } else {
                    checkActivityPermission();
                }
            }

            @Override
            public void onNegativeButtonClicked(CustomDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    private void loadData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        firstName = bundle.getString("first_name");
        lastName = bundle.getString("last_name");
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
            tvFirstName.setText(firstName);
            tvLastName.setText(lastName);
        }
    }

    /** 首先检查权限 */
    private void checkActivityPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                DialogUtil.showDialog(this, "是否前往设置打开权限", "缺少必要权限将使部分功能无法使用",
                                      new CustomDialog.OnCustomClickedListener() {
                                          @Override
                                          public void onPositiveButtonClicked(CustomDialog dialog) {
                                              toSelfSetting(QRcodeActivity.this);
                                          }

                                          @Override
                                          public void onNegativeButtonClicked(CustomDialog dialog) {
                                              dialog.dismiss();
                                          }
                                      });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0x01);
            }
        }
    }

    private void toSelfSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        startActivity(intent);
    }

    /**
     * onRequestPermissionsResult方法重写，Toast显示用户是否授权
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
            @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StringBuilder requestPermissionsResult = new StringBuilder();
        if (requestCode == 0x01) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    requestPermissionsResult.append(permissions[i])
                                            .append(" 申请成功");
                } else {
                    hasPermission = false;
                    requestPermissionsResult.append(permissions[i])
                                            .append(" 申请失败");
                }
            }
        }
        Toast.makeText(this, requestPermissionsResult, Toast.LENGTH_SHORT)
             .show();
    }
}
