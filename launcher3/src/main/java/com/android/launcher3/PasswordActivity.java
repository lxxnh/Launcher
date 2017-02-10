package com.android.launcher3;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_private_pwd);
        mPwdEdit = (EditText) findViewById(R.id.pwd_edit);
        mPwdTitle = (TextView) findViewById(R.id.set_pwd_title);
        mPwdEdit.addTextChangedListener(this);
        initPwdTitle();
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
            mEdit = mSharedPrefs.edit();
            mEdit.putString(PRIVATE_PWD,editable.toString());
            mEdit.putBoolean(FIRST_SET_PRIVATE,false);
            mEdit.commit();
            //TODO
        }

    }
}
