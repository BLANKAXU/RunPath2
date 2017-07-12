package com.example.blanka.runpath.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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

public class SignUp_Activity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_cancel;
    private Button btn_signup;
    private EditText edit_account;
    private EditText edit_password;
    private EditText edit_password2;
    private EditText edit_email;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);
        setTitle("注册");

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password2 = (EditText) findViewById(R.id.edit_password2);
        edit_email = (EditText) findViewById(R.id.edit_email);

        btn_cancel.setOnClickListener(this);
        btn_signup.setOnClickListener(this);

        edit_email.addTextChangedListener(new TextWatcher() {//验证邮箱格式是否正确
            @Override
            public void afterTextChanged(Editable s) {
                if (edit_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") && s.length() > 0){
                }else{
                    edit_email.setError("格式不正确");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                if(!checkOutText())
                {break;}
                queryServer();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }


    /**
     *
     * 进行服务器请求
     *
     * */
    private void queryServer() {
        showProgressDialog();
        String address = GetServerAddress()+"/RunPath/user/sign_up.jsp";
        RequestBody requestBody = new FormBody.Builder()
                .add("account", String.valueOf(edit_account.getText()))
                .add("password", String.valueOf(edit_password.getText()))
                .add("email", String.valueOf(edit_email.getText()))
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
                        Toast.makeText(SignUp_Activity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     *
     * 分析 JSON 数据
     *
     * */
    private void parseJson(String responseData) {
        try {
            JSONArray jasonArray = new JSONArray(responseData);
            JSONObject jasonObject = jasonArray.getJSONObject(0);
            boolean account_exist = jasonObject.getBoolean("account_exist");
            boolean signup_stat = jasonObject.getBoolean("signup_stat");
            if (signup_stat) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        closeProgressDialog();
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(SignUp_Activity.this);
                        dialog2.setMessage("注册成功");
                        dialog2.setCancelable(false);
                        dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialog2.show();
                    }
                });
            } else {
                if (account_exist) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(SignUp_Activity.this, "帐号已存在", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(SignUp_Activity.this, "注册错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 进度条操作
     *
     * */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在连接...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     *
     * 检查用户输入格式
     * 返回 true正确 false不正确
     *
     * */
    private boolean checkOutText() {
        if(edit_account.getText()==null || edit_account.getText().toString().trim().equals("")) {
            edit_account.setError("账号不能为空");
            return false;
        }
        if(edit_password.getText()==null || edit_password.getText().toString().trim().equals("")) {
            edit_password.setError("密码不能为空");
            return false;
        }
        if(edit_password2.getText()==null || edit_password2.getText().toString().trim().equals("")) {
            edit_password2.setError("确认你得密码");
            return false;
        }
        if(edit_email.getText()==null || edit_email.getText().toString().trim().equals("")) {
            edit_email.setError("邮箱不能为空");
            return false;
        }

        if(edit_account.getText().length() < 6 ) {
            edit_account.setError("账号太短");
            return false;
        }
        if(edit_password.getText().length() < 6 ) {
            edit_password.setError("密码太短");
            return false;
        }
        if(!edit_password.getText().toString().equals(edit_password2.getText().toString())){
            edit_password2.setError("两次密码不一致");
            return false;
        }
        return true;
    }

}
