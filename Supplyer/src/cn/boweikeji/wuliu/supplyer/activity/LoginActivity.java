package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.event.LoginEvent;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.utils.EncryptUtil;
import cn.boweikeji.wuliu.utils.Util;

import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mRegister) {
				RegisterActivity.startRegisterActivity(LoginActivity.this);
			} else if (view == mLoginSubmit) {
				login();
			}
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_rightTxt)
	TextView mRegister;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.login_username)
	EditText mUserName;
	@InjectView(R.id.login_password)
	EditText mPassword;
	@InjectView(R.id.autologin)
	CheckBox mAutoLogin;
	@InjectView(R.id.forget_pass)
	TextView mForgetPass;
	@InjectView(R.id.login_submit)
	TextView mLoginSubmit;

	private ProgressDialog mProgressDialog;
	
	private UserInfo mUserInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private void init() {
		ButterKnife.inject(this);
		initTitle();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.login);
		mRegister.setVisibility(View.VISIBLE);
		mRegister.setText(R.string.register);
		mBack.setImageResource(R.drawable.ic_navi_back);
		mBack.setOnClickListener(mOnClickListener);
		mRegister.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		mUserName.setText(getLastUserName());
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mLoginSubmit.setOnClickListener(mOnClickListener);
	}

	/**
	 * 获取最后一次登陆的用户名
	 * 
	 * @return
	 */
	private String getLastUserName() {
		return null;
	}

	/**
	 * 登录
	 */
	private void login() {
		if (validCheck()) {
			showProgressDialog();
			if (mUserInfo == null) {
				mUserInfo = new UserInfo();
			}
			String supplyer_cd = mUserName.getText().toString();
			String passwd = EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5);
			mUserInfo.setSupplyer_cd(supplyer_cd);
			mUserInfo.setPasswd(passwd);
			LoginManager.getInstance().login(supplyer_cd, passwd, mRequestHandler);
		}
	}
	
	public void login(UserInfo info) {
		showProgressDialog();
		mUserInfo = info;
		mUserName.setText(info.getSupplyer_cd());
		LoginManager.getInstance().login(info.getSupplyer_cd(), info.getPasswd(), mRequestHandler);
	}
	
	/**
	 * 检测登陆信息合法性
	 * 
	 * @return
	 */
	private boolean validCheck() {
		boolean bRes = true;
		String username = mUserName.getText().toString();
		String password = mPassword.getText().toString();
		if (username == null || username.equals("")) {
			showTips(getResources().getString(
					R.string.login_input_username_empty));
			bRes = false;
		} else if (password == null || password.equals("")) {
			showTips(getResources().getString(
					R.string.login_input_password_empty));
			bRes = false;
		} else if (!Util.isUserValid(username)) {
			showTips(getResources().getString(
					R.string.phone_invalid));
			bRes = false;
		} else if (!Util.isPasswordValid(password)) {
			showTips(getResources().getString(
					R.string.password_invalid));
			bRes = false;
		}
		return bRes;
	}
	
	/**
	 * 显示提示
	 * 
	 * @param tips
	 */
	private void showTips(String tips) {
		Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();
	}
	
	private void saveAutoLogin(boolean autoLogin) {
		SharedPreferences preference = getSharedPreferences(Const.PREFERENCE_NAME, MODE_MULTI_PROCESS);
		Editor editor = preference.edit();
		editor.putBoolean(Const.KEY_AUTO_LOGIN, autoLogin);
		editor.commit();
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getString(R.string.requesting));
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {//成功
					LoginManager.getInstance().setLogin(true);
					mUserInfo.update(response.optJSONObject("supplyer"));
					LoginManager.getInstance().setUserInfo(mUserInfo);
					EventBus.getDefault().post(new LoginEvent());
					saveAutoLogin(mAutoLogin.isChecked());
					finish();
				} else {
					showTips(msg);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		showTips(getString(R.string.login_failed));
	}
	
	public static void startLoginActivity(Context context) {
		context.startActivity(new Intent(context, LoginActivity.class));
	}
	
}
