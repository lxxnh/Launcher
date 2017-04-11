package com.android.launcher3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ComponentInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.launcher3.compat.UserHandleCompat;

import java.util.ArrayList;

/**
 * Created by zhongwenguang on 2/10/17.
 */

public class PasswordActivity extends Activity implements TextWatcher{

    private EditText mPwdEdit;
    private TextView mPwdTitle;
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEdit;
    private final static String FIRST_SET_PRIVATE = "first_set_private";
    private final static String PRIVATE_PWD = "private_pwd";
    private final static String MATCH_NUMBERS = "^[0-9]*$";
    private final static int VALID_PWD_LENGTH = 4;
    private String mPkgName;
    private String mClassName;
    private ArrayList<ItemInfo> mList;
    private int mAllowChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_private_pwd);
        mPwdEdit = (EditText) findViewById(R.id.pwd_edit);
        mPwdTitle = (TextView) findViewById(R.id.set_pwd_title);
        mPwdEdit.addTextChangedListener(this);
        initPwdTitle();
        mPkgName = getIntent().getStringExtra(PrivateDropTarget.PACKAGE_NAME);
        mClassName = getIntent().getStringExtra(PrivateDropTarget.CLASS_NAME);
        mAllowChangePwd = getIntent().getIntExtra(Utilities.CHANGE_PWD_FLAG,0);
    }

    public void initPwdTitle() {
        mSharedPrefs = this.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        if(mSharedPrefs.getBoolean(FIRST_SET_PRIVATE,true)) {
            mPwdTitle.setText(R.string.private_set_pwd);
        } else {
            mPwdTitle.setText(R.string.private_input_pwd);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!charSequence.toString().matches(MATCH_NUMBERS)) {
            Toast.makeText(this,R.string.pwd_warning,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //1.密码长4位，且都是数字
        //2.第一次设置密码，直接startIntent
        //3.不是第一次，输入密码错误
        //             密码输入正确，startIntent
        //4.重置密码

        if (editable.toString().length() == VALID_PWD_LENGTH && editable.toString().matches(MATCH_NUMBERS)) {
            String pwd = mSharedPrefs.getString(PRIVATE_PWD, "");
            if(pwd.isEmpty()) {
                if (mAllowChangePwd == -1) {
                    Toast.makeText(this, R.string.reset_private_pwd_successfully, Toast.LENGTH_SHORT).show();
                }
                editSharedPrefs(editable);
                startIntent();
                finish();
                return;
            } else if(pwd.equals(editable.toString())) {

                if(mAllowChangePwd == Utilities.ALLOW_CHANGE_PWD) {
                    mPwdTitle.setText(R.string.reset_private_pwd);
                    clearSharedPrefs(editable);
                    editable.clear();
                    mAllowChangePwd = -1;
                    return;
                }
                startIntent();
                finish();
                return;
            } else {
                Toast.makeText(this, R.string.pwd_wrong, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void startIntent() {
        if (mPkgName != null) {
            int index = mPkgName.indexOf("/");
            String pkgName = mPkgName.substring(0, index);
            ComponentName name = ComponentName.unflattenFromString(mPkgName);
            LauncherAppState.getInstance().getIconCache().updatePrivateApp(mPkgName, pkgName, mClassName);//此处是更新数据库
            LauncherAppState.getInstance().getModel().mBgAllAppsList.privatedPackage(pkgName, UserHandleCompat.myUserHandle());//TODO 将对应的app从AllAppList中移除
            LauncherAppState.getInstance().getModel().getCallback().bindAppsRemoved(LauncherAppState.getInstance().getModel().mBgAllAppsList.privated);//TODO 这个remove才是useful方法
        }
        Intent intent = new Intent(this, PrivateFolderActivity.class);
        this.startActivity(intent);
        finish();

    }


    public void editSharedPrefs(Editable editable){
        mEdit = mSharedPrefs.edit();
        mEdit.putString(PRIVATE_PWD,editable.toString());
        mEdit.putBoolean(FIRST_SET_PRIVATE,false);
        mEdit.commit();
    }

    public void clearSharedPrefs(Editable editable){
        mEdit = mSharedPrefs.edit();
        mEdit.putString(PRIVATE_PWD,"");
        mEdit.commit();
    }

}
