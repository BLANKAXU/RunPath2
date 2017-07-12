package com.example.blanka.runpath.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blanka.runpath.R;
import com.example.blanka.runpath.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.blanka.runpath.util.HttpUtil.GetServerAddress;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login;
    private Button btn_signup;
    private EditText edit_account;
    private EditText edit_password;
    private ProgressDialog progressDialog;
    private CheckBox checkB_showpsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        setTitle("登录");

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_password = (EditText) findViewById(R.id.edit_password);
        checkB_showpsw = (CheckBox) findViewById(R.id.checkB_showpsw);

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        checkB_showpsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if(edit_account.getText()==null || edit_account.getText().toString().trim().equals("")) {
                    edit_account.setError("请输入账号");
                    break;
                }
                if(edit_password.getText()==null || edit_password.getText().toString().trim().equals("")) {
                    edit_password.setError("密码不能为空");
                    break;
                }
                queryServer();
                break;
            case R.id.btn_signup:
                Intent intent = new Intent(this, SignUp_Activity.class);
                startActivity(intent);
                break;
            case R.id.checkB_showpsw:
                if(checkB_showpsw.isChecked()){
                    edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else
                {
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
        }
    }

    private void queryServer() {
        showProgressDialog();
        String address = GetServerAddress()+"/RunPath/user/login.jsp";
        RequestBody requestBody = new FormBody.Builder()
                .add("account", String.valueOf(edit_account.getText()))
                .add("password", String.valueOf(edit_password.getText()))
                .build();
        HttpUtil.sendOkHttpRequestPost(address, requestBody, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parseJson(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(Login_Activity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void parseJson(String responseData) {
        try {
            JSONArray jasonArray = new JSONArray(responseData);
            JSONObject jasonObject = jasonArray.getJSONObject(0);
            boolean Login_Stat = jasonObject.getBoolean("login_stat");
            if (Login_Stat) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(Login_Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("user",edit_account.getText().toString());
                        editor.apply();
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(Login_Activity.this, "帐号密码错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在登录...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
