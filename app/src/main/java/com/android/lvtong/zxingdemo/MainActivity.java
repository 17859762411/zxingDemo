package com.android.lvtong.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import androidx.appcompat.widget.PopupMenu;
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
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private UFormInputLayout mFirstName;
    private UFormInputLayout mLastName;
    private EditText etString;
    private TextView tvResultLabel;
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
        tvResultLabel = findViewById(R.id.tv_result_label);
        tvResult = findViewById(R.id.tv_result);
        mFirstName = findViewById(R.id.first_name_input);
        mLastName = findViewById(R.id.last_name_input);
        etString = findViewById(R.id.et_string);
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
                String firstName = uri.getQueryParameter(FIRST_NAME);
                mFirstName.setValue(firstName);
                String lastName = uri.getQueryParameter(LAST_NAME);
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
            showPopUpMenu();
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
                        Uri uri = data.getData();
                        if (uri != null) {
                            String firstName = uri.getQueryParameter(FIRST_NAME);
                            mFirstName.setValue(firstName);
                            String lastName = uri.getQueryParameter(LAST_NAME);
                            mLastName.setValue(lastName);
                        }
                        String result = bundle.getString(CaptureActivity.EXTRA_STRING);
                        tvResult.setText(result);
                        CustomUtils.copy(this, result);

                        Uri uri1 = Uri.parse(result);
                        if (uri1 != null) {
                            String firstName = uri1.getQueryParameter(FIRST_NAME);
                            String lastName = uri1.getQueryParameter(LAST_NAME);
                            if (TextUtils.isEmpty(firstName) | TextUtils.isEmpty(lastName)) {
                                Toast.makeText(this, "二维码中缺少所需参数", Toast.LENGTH_SHORT)
                                     .show();
                            } else {
                                tvResult.setVisibility(View.VISIBLE);
                                tvResultLabel.setVisibility(View.VISIBLE);
                                mFirstName.setValue(firstName);
                                mLastName.setValue(lastName);
                            }
                        }
                    }
                }
                break;
            case REQUEST_CODE_SCAN2:
            default:
                break;
        }
    }

    public void showPopUpMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_get_qr));
        popup.getMenuInflater()
             .inflate(R.menu.second_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_parameter:
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("type", 0);
                        bundle1.putString("first_name", mFirstName.getValue());
                        bundle1.putString("last_name", mLastName.getValue());
                        Intent intent1 = new Intent();
                        intent1.setClass(MainActivity.this, QRcodeActivity.class);
                        intent1.putExtra("bundle", bundle1);
                        startActivity(intent1);
                        break;
                    case R.id.item_string:
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("type", 1);
                        bundle2.putString("string", etString.getText()
                                                            .toString());
                        Intent intent2 = new Intent();
                        intent2.setClass(MainActivity.this, QRcodeActivity.class);
                        intent2.putExtra("bundle", bundle2);
                        startActivity(intent2);
                        break;
                    default:
                }
                return true;
            }
        });
        popup.show();
    }

    /** 权限请求结果 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goScan();
            } else {
                Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT)
                     .show();
            }
        }
    }
}
