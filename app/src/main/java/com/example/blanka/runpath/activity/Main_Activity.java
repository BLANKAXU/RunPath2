package com.example.blanka.runpath.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.blanka.runpath.fragment.Main_Fragment;
import com.example.blanka.runpath.R;
import com.example.blanka.runpath.fragment.Store_Fragment;
import com.example.blanka.runpath.fragment.User_Fragment;

import java.util.ArrayList;
import java.util.List;


public class Main_Activity extends AppCompatActivity implements View.OnClickListener {
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private static final String TAG = "Main_Activity";
    private static String[] PERMISSIONS_CONTACT = new String[]{//所需权限
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };

    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_three;
    private TextView tv_four;
    private TextView tv_five;

    private FragmentManager fragmentManager;
    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragments = new ArrayList<>();
    private Main_Fragment main_fragment = new Main_Fragment();


    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View mView = findViewById(R.id.content);
        /**
         * 检查权限
         */
        if (Build.VERSION.SDK_INT >= 23) {//判断为安卓6.0以上 授予权限
            showContacts(mView);
        }
        /**
         * 去状态栏
         * */
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        tv_one = (TextView) findViewById(R.id.tv_one);
        tv_two = (TextView) findViewById(R.id.tv_two);
        tv_three = (TextView) findViewById(R.id.tv_three);
        tv_four = (TextView) findViewById(R.id.tv_four);
        tv_five = (TextView) findViewById(R.id.tv_five);
        fragmentManager = getSupportFragmentManager();

        tv_one.setOnClickListener(this);
        tv_two.setOnClickListener(this);
        tv_three.setOnClickListener(this);
        tv_four.setOnClickListener(this);
        tv_five.setOnClickListener(this);

        // “内存重启”时调用
        if (savedInstanceState != null) {
            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT, 0);
            //注意，添加顺序要跟下面添加的顺序一样
            fragments.removeAll(fragments);
            fragments.add(fragmentManager.findFragmentByTag(0 + ""));
            fragments.add(fragmentManager.findFragmentByTag(1 + ""));
            fragments.add(fragmentManager.findFragmentByTag(2 + ""));

            //恢复fragment页面
            restoreFragment();
        } else {
            fragments.add(main_fragment);
            fragments.add(new User_Fragment());
            fragments.add(new Store_Fragment());

            showFragment();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT, currentIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_one:
                currentIndex = 2;
                tv_one.setTextColor(Color.parseColor("#2ed373"));
                tv_two.setTextColor(Color.parseColor("#8a000000"));
                tv_three.setTextColor(Color.parseColor("#8a000000"));
                tv_four.setTextColor(Color.parseColor("#8a000000"));
                tv_five.setTextColor(Color.parseColor("#8a000000"));
                break;
            case R.id.tv_two:
                currentIndex = 2;
                tv_two.setTextColor(Color.parseColor("#2ed373"));
                tv_one.setTextColor(Color.parseColor("#8a000000"));
                tv_three.setTextColor(Color.parseColor("#8a000000"));
                tv_four.setTextColor(Color.parseColor("#8a000000"));
                tv_five.setTextColor(Color.parseColor("#8a000000"));
                break;
            case R.id.tv_three:
                currentIndex = 0;
                tv_three.setTextColor(Color.parseColor("#2ed373"));
                tv_two.setTextColor(Color.parseColor("#8a000000"));
                tv_one.setTextColor(Color.parseColor("#8a000000"));
                tv_four.setTextColor(Color.parseColor("#8a000000"));
                tv_five.setTextColor(Color.parseColor("#8a000000"));
                break;
            case R.id.tv_four:
                currentIndex = 2;
                tv_four.setTextColor(Color.parseColor("#2ed373"));
                tv_two.setTextColor(Color.parseColor("#8a000000"));
                tv_three.setTextColor(Color.parseColor("#8a000000"));
                tv_one.setTextColor(Color.parseColor("#8a000000"));
                tv_five.setTextColor(Color.parseColor("#8a000000"));
                break;
            case R.id.tv_five:
                currentIndex = 1;
                tv_five.setTextColor(Color.parseColor("#2ed373"));
                tv_two.setTextColor(Color.parseColor("#8a000000"));
                tv_three.setTextColor(Color.parseColor("#8a000000"));
                tv_four.setTextColor(Color.parseColor("#8a000000"));
                tv_one.setTextColor(Color.parseColor("#8a000000"));
                break;
        }
        showFragment();
    }

    /**
     * 使用show() hide()切换页面
     * 显示fragment
     */
    private void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //如果之前没有添加过
        if (!fragments.get(currentIndex).isAdded()) {
            transaction
                    .hide(currentFragment)
                    .add(R.id.content, fragments.get(currentIndex), "" + currentIndex);//第三个参数为添加当前的fragment时绑定一个tag
        } else {
            transaction
                    .hide(currentFragment)
                    .show(fragments.get(currentIndex));
        }
        transaction.commit();
        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);
    }

    /**
     * 恢复fragment
     */
    private void restoreFragment() {
        FragmentTransaction mBeginTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < fragments.size(); i++) {
            if (i == currentIndex) {
                mBeginTransaction.show(fragments.get(i));
            } else {
                mBeginTransaction.hide(fragments.get(i));
            }
        }
        mBeginTransaction.commit();
        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);

    }


    /**
     * 6.0系统授权
     */
    public void showContacts(View v) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions(v);

        } else {

            Log.i(TAG,
                    "Contact permissions have already been granted. Displaying contact details.");

        }
    }

    private void requestContactsPermissions(View v) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                ) {
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");
            Snackbar.make(v, "permission_contacts_rationale", Snackbar.LENGTH_INDEFINITE)//
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(Main_Activity.this, PERMISSIONS_CONTACT,
                                            0);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, 0);
        }
    }

}
