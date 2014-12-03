package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.LoginEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.EncryptUtil;
import cn.boweikeji.wuliu.driver.utils.Util;

import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();
	
	public static final int REQUEST_CODE_REDIRECT = 999;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mRegister) {
				RegisterProfileActivity.startRegisterPhoneActivity(LoginActivity.this);
			} else if (view == mShowPass) {
				showPassword(mPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
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
	Button mRegister;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.login_username)
	EditText mUserName;
	@InjectView(R.id.login_password)
	EditText mPassword;
	@InjectView(R.id.login_show_pass)
	Button mShowPass;
	@InjectView(R.id.login_submit)
	TextView mLoginSubmit;

	private ProgressDialog mProgressDialog;
	
	private UserInfo mUserInfo;
	
	private Intent mRedirect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mRedirect = getIntent().getParcelableExtra("redirect");
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

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mRedirect = intent.getParcelableExtra("redirect");
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
		mBack.setImageResource(R.drawable.btn_title_back);
		mBack.setOnClickListener(mOnClickListener);
		mRegister.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		mUserName.setText(getLastUserName());
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mShowPass.setOnClickListener(mOnClickListener);
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
	 * 显示密码
	 * 
	 * @param bShow
	 */
	public void showPassword(boolean bShow) {
		String text = mPassword.getText().toString();
		if (bShow) {
			mPassword
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			mShowPass.setText(getResources().getString(R.string.hide));
		} else {
			mPassword.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			mShowPass.setText(getResources().getString(R.string.show));
		}
		mPassword.postInvalidate();
		if (text != null) {
			mPassword.setSelection(text.length());
		}
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
			String driver_cd = mUserName.getText().toString();
			String passwd = EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5);
			mUserInfo.setDriver_cd(driver_cd);
			mUserInfo.setPasswd(passwd);
			LoginManager.getInstance().login(driver_cd, passwd, mRequestHandler);
		}
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
			Util.showTips(this, getString(R.string.login_input_username_empty));
			bRes = false;
		} else if (password == null || password.equals("")) {
			Util.showTips(this, getString(R.string.login_input_password_empty));
			bRes = false;
		} else if (!Util.isUserValid(username)) {
			Util.showTips(this, getString(R.string.phone_invalid));
			bRes = false;
		} else if (!Util.isPasswordValid(password)) {
			Util.showTips(this, getString(R.string.password_invalid));
			bRes = false;
		}
		return bRes;
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
				Util.showTips(this, msg);
				if (res == 2) {//成功
					LoginManager.getInstance().setLogin(true);
					mUserInfo.update(response.optJSONObject("infos"));
					LoginManager.getInstance().setUserInfo(mUserInfo);
					EventBus.getDefault().post(new LoginEvent());
					if (mRedirect != null) {
						setResult(RESULT_OK, mRedirect);
					}
					finish();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.login_failed));
	}
	
	public static void startLoginActivity(Context context) {
		context.startActivity(new Intent(context, LoginActivity.class));
	}
	
	public static void startLoginActivity(Activity activity, Intent redirect) {
		Intent intent = new Intent(activity, LoginActivity.class);
		intent.putExtra("redirect", redirect);
		activity.startActivityForResult(intent, REQUEST_CODE_REDIRECT);
	}
}
