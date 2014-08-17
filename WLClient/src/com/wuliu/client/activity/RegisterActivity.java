package com.wuliu.client.activity;


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
import com.wuliu.client.api.BaseParams;
import com.wuliu.client.bean.UserInfo;
import com.wuliu.client.utils.DeviceInfo;
import com.wuliu.client.utils.EncryptUtil;
import com.wuliu.client.utils.Util;
import com.wuliu.client.view.ClearEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private static final String TAG = RegisterActivity.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mUserType) {
				showTypeChooseDialog();
			} else if (view == mCodeBtn) {
				getVerifyCode();
			} else if (view == mSubmit) {
				register();
			}
		}
	};

	private JsonHttpResponseHandler mRegisterHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			registerResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			registerResult(null);
		};
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.register_phone)
	ClearEditText mPhone;
	@InjectView(R.id.register_code)
	ClearEditText mCode;
	@InjectView(R.id.register_get_code)
	Button mCodeBtn;
	@InjectView(R.id.register_password)
	ClearEditText mPassword;
	@InjectView(R.id.register_id)
	ClearEditText mIDNumber;
	@InjectView(R.id.register_user_type)
	TextView mUserType;
	@InjectView(R.id.register_submit)
	Button mSubmit;

	private int mUserTypeIndex;
	private String[] mUserTypes;

	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		initData();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.register);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mCodeBtn.setOnClickListener(mOnClickListener);
		mUserType.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}

	private void initData() {
		mUserTypeIndex = 0;
		mUserTypes = getResources().getStringArray(R.array.user_types);
		updateUserType();
	}

	private void updateUserType() {
		mUserType.setText(mUserTypes[mUserTypeIndex]);
	}

	private void getVerifyCode() {
		Toast.makeText(this, "��֤�룺1234", Toast.LENGTH_SHORT).show();
	}
	
	private void register() {
		if (validCheck()) {
			showProgressDialog();
			
			String phone = mPhone.getText().toString();
			String password = EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5);
			String id = mIDNumber.getText().toString();
			
			AsyncHttpClient client = new AsyncHttpClient();
			BaseParams params = new BaseParams();
			params.add("method", "registerGoodSupplyer");
			params.add("suppler_cd", phone);
			params.add("suppler_type", "" + mUserTypeIndex);
			params.add("phone", phone);
			params.add("credit_level", "0");
			params.add("state", "0");
			params.add("card_id", (id == null || id.equals("")) ? BaseParams.PARAM_DEFAULT : id);
			params.add("passwd", password);
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_REGISTER, params));
			client.get(Const.URL_REGISTER, params, mRegisterHandler);
		}
	}
	
	private void login() {
		hideProgressDialog();
		Intent intent = new Intent();
		UserInfo info = new UserInfo();
		String phone = mPhone.getText().toString();
		String password = EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5);
		info.setUserName(phone);
		info.setPassword(password);
		intent.putExtra("userinfo", info);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private boolean validCheck() {
		String phone = mPhone.getText().toString();
		String code = mCode.getText().toString();
		String password = mPassword.getText().toString();
		String id = mIDNumber.getText().toString();
		
		if (phone == null || phone.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.register_username_empty));
			return false;
		} else if (code == null || code.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.register_code_empty));
			return false;
		} else if (password == null || password.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.register_password_empty));
			return false;
		} else if (!Util.isPhoneNumber(phone)) {
			Util.showTips(this, getResources().getString(
					R.string.register_username_invalid));
			return false;
		} else if (!Util.isCodeValid(code)) {
			Util.showTips(this, getResources().getString(
					R.string.register_code_invalid));
			return false;
		} else if (!Util.isPasswordValid(password)) {
			Util.showTips(this, getResources().getString(
					R.string.register_password_invalid));
			return false;
		} else if (id != null && !id.equals("") && !Util.isIDNumberValid(id)) {
			Util.showTips(this, getResources().getString(
					R.string.register_id_invalid));
			return false;
		}
		
		return true;
	}
	
	private void showTypeChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(mUserTypes, mUserTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mUserTypeIndex = which;
								dialog.dismiss();
								updateUserType();
							}
						}).setTitle("��Դ").create();
		dialog.show();
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
	
	private void registerResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {//�ɹ�
					login();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.register_failed));
	}
	
}
