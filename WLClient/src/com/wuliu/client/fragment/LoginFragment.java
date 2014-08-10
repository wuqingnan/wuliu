package com.wuliu.client.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wuliu.client.Const;
import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;
import com.wuliu.client.activity.RegisterActivity;
import com.wuliu.client.api.BaseParams;
import com.wuliu.client.utils.DeviceInfo;
import com.wuliu.client.utils.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends BaseFragment {

	private static final String TAG = LoginFragment.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity())
							.onClickTitle(LoginFragment.this);
				}
			} else if (view == mRegister) {
				register();
			} else if (view == mShowPass) {
				showPassword(mPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			} else if (view == mLoginSubmit) {
				login();
			}
		}
	};
	
	private JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			loginResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			loginResult(null);
		};
	};

	private View mRootView;

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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_login, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		InputMethodManager manager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(mRootView.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_login);
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
	 * 注册
	 */
	private void register() {
		getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
	}

	/**
	 * 登录
	 */
	private void login() {
		if (validCheck()) {
			String username = mUserName.getText().toString();
			String password = mPassword.getText().toString();
			
			AsyncHttpClient client = new AsyncHttpClient();
			BaseParams params = new BaseParams();
			params.add("method", "loginCheck");
			params.add("device_no", DeviceInfo.getIMEI());
			params.add("suppler_cd", username);
			params.add("passwd", password);
			
			showProgressDialog();
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_LOGIN, params));
			client.get(Const.URL_LOGIN, params, mResponseHandler);
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
			showTips(getResources().getString(
					R.string.login_input_username_empty));
			bRes = false;
		} else if (password == null || password.equals("")) {
			showTips(getResources().getString(
					R.string.login_input_password_empty));
			bRes = false;
		} else if (!Util.isUserValid(username)) {
			showTips(getResources().getString(
					R.string.login_input_username_invalid));
			bRes = false;
		} else if (!Util.isPasswordValid(password)) {
			showTips(getResources().getString(
					R.string.login_input_password_invalid));
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
		Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT).show();
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage(getString(R.string.requesting));
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.hide();
		}
		mProgressDialog = null;
	}
	
	private void loginResult(JSONArray response) {
		if (response != null && response.length() > 0) {
			try {
				JSONObject object = response.getJSONObject(0);
				int res = object.getInt("res");
				String msg = object.getString("msg");
				switch (res) {
				case 1://失败
					showTips(getString(R.string.login_failed));
					return;
				case 2://成功
					showTips(getString(R.string.login_success));
					((MainActivity)getActivity()).loginSuccess(mUserName.getText().toString());
					return;
				case 3://提示
					showTips(msg);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		showTips(getString(R.string.login_failed));
	}
}
