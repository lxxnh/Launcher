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
    private ArrayList<ItemInfo> mList;

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
        if(editable.toString().length() == VALID_PWD_LENGTH && editable.toString().matches(MATCH_NUMBERS)) {
            String pwd = mSharedPrefs.getString(PRIVATE_PWD,"");
            if (pwd.isEmpty() || pwd.equals(editable.toString())) {
                editSharedPrefs(editable);
                LauncherAppState.getInstance().getIconCache().updatePrivateApp(mPkgName);//此处是更新数据库
                Intent intent = new Intent(this, PrivateFolderActivity.class);
                this.startActivity(intent);
                finish();
            } else {
                Toast.makeText(this,R.string.pwd_wrong,Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void editSharedPrefs(Editable editable){
        mEdit = mSharedPrefs.edit();
        mEdit.putString(PRIVATE_PWD,editable.toString());
        mEdit.putBoolean(FIRST_SET_PRIVATE,false);
        mEdit.commit();
    }

}
