package com.wuliu.client.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.wuliu.client.R;
import com.wuliu.client.view.ClearEditText;

import android.app.Activity;
import android.app.AlertDialog;
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
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("method", "registerGoodSupplyer");
		params.add("device_no", "0000000000000000");
		params.add("suppler_cd", "15810759237");
		params.add("suppler_name", "15810759237");
		params.add("suppler_type", "SP01");
		params.add("phone", "15810759237");
		params.add("credit_level", "CL01");
		params.add("state", "0");
		params.add("card_id", "123456789012345678");
		params.add("passwd", "19851020");
		client.get("http://218.21.213.76:7201/bss/registerGoodSupplyer.action", params,  new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, "shizy---onSuccess");
				Log.d(TAG, "shizy---data: " + new String(arg2));
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Log.d(TAG, "shizy---onFailure");
			}
		});
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
	
}
