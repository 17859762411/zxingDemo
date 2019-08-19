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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.android.lvtong.zxingdemo.util.NetworkUtils.buildUrl;

/**
 * @author 22939
 */
public class QRcodeActivity extends AppCompatActivity {

    private static final int REQUEST_1 = 0x01;
    private String firstName;
    private String lastName;
    private int width;
    private Bitmap llBitmap;
    private Bitmap ivBitmap;
    private LinearLayout llCenter;
    private TextView tvFirstName;
    private TextView tvLastName;
    private ImageView ivCenter;
    private boolean hasPermission = true;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
        loadData();
        /*图像生成*/
        ivBitmap = ImgUtils.createImage(url, width, width);
        if (ivBitmap == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT)
                 .show();
        } else {
            ivCenter.setImageBitmap(ivBitmap);
        }
    }

    private void initView() {
        intHeader();
        /*获取屏幕宽度*/
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay()
               .getMetrics(outMetrics);
        width = outMetrics.widthPixels;

        /*设置ImageView的宽高*/
        tvFirstName = findViewById(R.id.tv_first_name);
        tvLastName = findViewById(R.id.tv_last_name);
        ivCenter = findViewById(R.id.imageView);
        ivCenter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showSaveDialog();
                return true;
            }
        });

        /*获取布局的bitmap*/
        llCenter = findViewById(R.id.ll_center);
        LinearLayout.LayoutParams llParams =
                new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        llCenter.setLayoutParams(llParams);
    }

    private void loadData() {
        /*字段取值赋值*/
        Bundle bundle = getIntent().getBundleExtra("bundle");
        switch (bundle.getInt("type")) {
            case 0:
                tvFirstName.setVisibility(View.VISIBLE);
                tvLastName.setVisibility(View.VISIBLE);
                firstName = bundle.getString("first_name");
                lastName = bundle.getString("last_name");
                if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
                    tvFirstName.setText(firstName);
                    tvLastName.setText(lastName);
                } else {
                    tvFirstName.setText(R.string.nullString);
                    tvLastName.setText(R.string.nullString);
                }
                url = buildUrl(firstName, lastName).toString();
                break;
            case 1:
                tvFirstName.setVisibility(View.GONE);
                tvLastName.setVisibility(View.GONE);
                url = bundle.getString("string");
                break;
            default:
        }
    }

    private void intHeader() {
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setTitle("欢迎使用");
        }
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
        if (requestCode == REQUEST_1) {
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
