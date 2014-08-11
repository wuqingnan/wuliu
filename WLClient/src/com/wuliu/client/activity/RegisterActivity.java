package com.wuliu.client.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.wuliu.client.Const;
import com.wuliu.client.R;
import com.wuliu.client.api.BaseParams;
import com.wuliu.client.utils.DeviceInfo;
import com.wuliu.client.utils.Util;
import com.wuliu.client.view.ClearEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

	private JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			registerResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
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
	private String[] mUserTypeValues;

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
	}

	private void initData() {
		mUserTypeIndex = 0;
		mUserTypes = getResources().getStringArray(R.array.user_types);
		mUserTypeValues = getResources().getStringArray(
				R.array.user_type_values);
		updateUserType();
	}

	private void updateUserType() {
		mUserType.setText(mUserTypes[mUserTypeIndex]);
	}

	private void getVerifyCode() {
		Toast.makeText(this, "验证码：1234", Toast.LENGTH_SHORT).show();
	}
	
	private void register() {
		if (validCheck()) {
			String phone = mPhone.getText().toString();
			String password = mPassword.getText().toString();
			String id = mIDNumber.getText().toString();
			
			AsyncHttpClient client = new AsyncHttpClient();
			BaseParams params = new BaseParams();
			params.add("method", "registerGoodSupplyer");
			params.add("device_no", DeviceInfo.getIMEI());
			params.add("suppler_cd", phone);
			params.add("suppler_name", phone);
			params.add("suppler_type", mUserTypeValues[mUserTypeIndex]);
			params.add("phone", phone);
			params.add("credit_level", "CL01");
			params.add("state", "0");
			params.add("card_id", (id == null || id.equals("")) ? "-9" : id);
			params.add("passwd", password);
			
			showProgressDialog();
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_REGISTER, params));
			client.get(Const.URL_REGISTER, params, mResponseHandler);
		}
	}
	
	private boolean validCheck() {
		String phone = mPhone.getText().toString();
		String code = mCode.getText().toString();
		String password = mPassword.getText().toString();
		String id = mIDNumber.getText().toString();
		
		if (phone == null || phone.equals("")) {
			showTips(getResources().getString(
					R.string.register_username_empty));
			return false;
		} else if (code == null || code.equals("")) {
			showTips(getResources().getString(
					R.string.register_code_empty));
			return false;
		} else if (password == null || password.equals("")) {
			showTips(getResources().getString(
					R.string.register_password_empty));
			return false;
		} else if (!Util.isPhoneNumber(phone)) {
			showTips(getResources().getString(
					R.string.register_username_invalid));
			return false;
		} else if (!Util.isCodeValid(code)) {
			showTips(getResources().getString(
					R.string.register_code_invalid));
			return false;
		} else if (!Util.isPasswordValid(password)) {
			showTips(getResources().getString(
					R.string.register_password_invalid));
			return false;
		} else if (id != null && !id.equals("") && !Util.isIDNumberValid(id)) {
			showTips(getResources().getString(
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
						}).setTitle("货源").create();
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
			mProgressDialog.hide();
		}
		mProgressDialog = null;
	}
	
	/**
	 * 显示提示
	 * 
	 * @param tips
	 */
	private void showTips(String tips) {
		Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();
	}
	
	private void registerResult(JSONArray response) {
		if (response != null && response.length() > 0) {
			try {
				JSONObject object = response.getJSONObject(0);
				int res = object.getInt("res");
				String msg = object.getString("msg");
				switch (res) {
				case 0://异常
				case 1://失败
					showTips(getString(R.string.register_failed));
					return;
				case 2://成功
					showTips(getString(R.string.register_success));
					finish();
					return;
				case 3://提示
					showTips(msg);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		showTips(getString(R.string.register_failed));
	}
	
}
