package com.android.lvtong.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lvtong.zxingdemo.util.CustomUtils;
import com.android.lvtong.zxingdemo.zbar.CaptureActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sz.ucar.bwued.lib.forminput.UFormInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author 22939
 */
public class MainActivity extends AppCompatActivity {

    /** 扫描二维码 */
    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final int REQUEST_CODE_SCAN2 = 0x0001;
    private UFormInputLayout mFirstName;
    private UFormInputLayout mLastName;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loadData();
    }

    /** 初始化视图 */
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setTitle("欢迎使用");
        }
        tvResult = findViewById(R.id.tv_result);
        mFirstName = findViewById(R.id.first_name_input);
        mLastName = findViewById(R.id.last_name_input);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    goScan();
                }
            }
        });
    }

    /** 加载数据 */
    private void loadData() {
        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                System.out.println("uri整体" + uri.toString());
                String firstName = uri.getQueryParameter("firstName");
                mFirstName.setValue(firstName);
                String lastName = uri.getQueryParameter("lastName");
                mLastName.setValue(lastName);
            }
        }
    }

    /** 跳转到扫码界面 */
    private void goScan() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    /** 创建menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** menu点击事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_get_qr) {
            Bundle bundle = new Bundle();
            bundle.putString("first_name", mFirstName.getValue());
            bundle.putString("last_name", mLastName.getValue());
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, QRcodeActivity.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /** 数据返回 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String result = bundle.getString(CaptureActivity.EXTRA_STRING);
                        tvResult.setText(result);
                        CustomUtils.copy(this, result);
                    }
                }
                break;
            case REQUEST_CODE_SCAN2:
            default:
                break;
        }
    }

    /** 权限请求结果 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT)
                         .show();
                }
                break;
            default:
        }
    }
}
