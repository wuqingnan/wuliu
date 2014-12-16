package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WeakHandler;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.utils.EncryptUtil;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.view.ClearEditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class ForgetPasswdActivity extends BaseActivity {

	private static final String TAG = ForgetPasswdActivity.class.getSimpleName();
	
	private static final String COUNTRY_CODE = "86";
	
	private static final long COOLDOWN_INTERVAL = 1000;
	private static final long COOLDOWN_TIME = 60 * COOLDOWN_INTERVAL;
	
	private static final int MSG_GET_VERIFICATION_CODE_ERROR = 1 << 0;
	private static final int MSG_GET_VERIFICATION_CODE_COMPLETE = 1 << 1;
	private static final int MSG_SUBMIT_VERIFICATION_CODE_ERROR = 1 << 2;
	private static final int MSG_SUBMIT_VERIFICATION_CODE_COMPLETE = 1 << 3;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mGetCode) {
				getVerifyCode();
			} else if (view == mSubmit) {
				register();
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
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s != null && Util.isPhoneNumber(s.toString()) && !mIsCountDown) {
				mGetCode.setEnabled(true);
			}
			else {
				mGetCode.setEnabled(false);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
	
	private EventHandler mEventHandler = new EventHandler() {
		@Override
		public void afterEvent(int event, int result, Object data) {
			super.afterEvent(event, result, data);
			Log.d(TAG, "shizy---afterEvent: " + event);
			Log.d(TAG, "shizy---afterEvent: " + result);
			Log.d(TAG, "shizy---afterEvent: " + data);
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					mHandler.sendEmptyMessage(MSG_GET_VERIFICATION_CODE_COMPLETE);
				} else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					mHandler.sendEmptyMessage(MSG_SUBMIT_VERIFICATION_CODE_COMPLETE);
				}
			} else if (result == SMSSDK.RESULT_ERROR) {
				if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					mHandler.sendEmptyMessage(MSG_GET_VERIFICATION_CODE_ERROR);
				} else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					mHandler.sendEmptyMessage(MSG_SUBMIT_VERIFICATION_CODE_ERROR);
				}
			}
		}
		
		@Override
		public void beforeEvent(int event, Object data) {
			super.beforeEvent(event, data);
			Log.d(TAG, "shizy---beforeEvent: " + event);
			Log.d(TAG, "shizy---beforeEvent: " + data);
		}
	};
	
	private CountDownTimer mCountDownTimer = new CountDownTimer(COOLDOWN_TIME, COOLDOWN_INTERVAL) {
		
		@Override
		public void onTick(long millisUntilFinished) {
			updateCountDown(millisUntilFinished);
		}
		
		@Override
		public void onFinish() {
			hideCountDown();
		}
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.phone)
	ClearEditText mPhone;
	@InjectView(R.id.code)
	ClearEditText mCode;
	@InjectView(R.id.get_code)
	LinearLayout mGetCode;
	@InjectView(R.id.countdown)
	TextView mCountDown;
	@InjectView(R.id.password1)
	ClearEditText mPassword1;
	@InjectView(R.id.password2)
	ClearEditText mPassword2;
	@InjectView(R.id.submit)
	Button mSubmit;

	private boolean mIsCountDown = false;
	
	private ForgetPasswdHandler mHandler = null;
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_passwd);
		initView();
		mHandler = new ForgetPasswdHandler(this);
		SMSSDK.initSDK(this, "2efbb3982f2a", "5fadc7e323623a695f5fe6b26d5ed79f");
		SMSSDK.registerEventHandler(mEventHandler);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideCountDown();
		SMSSDK.unregisterAllEventHandler();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.forget_pass);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mGetCode.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
		mPassword1.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mPassword2.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mPhone.addTextChangedListener(mTextWatcher);
		mGetCode.setEnabled(false);
	}

	private void getVerifyCode() {
		showCountDown();
		SMSSDK.getVerificationCode(COUNTRY_CODE, mPhone.getText().toString());
	}
	
	private void register() {
		if (validCheck()) {
			showProgressDialog();
			SMSSDK.submitVerificationCode(COUNTRY_CODE, mPhone.getText().toString(), mCode.getText().toString());
		}
	}
	
	/**
	 * 提交信息
	 */
	private void submit() {
		String phone = mPhone.getText().toString();
		String password = EncryptUtil.encrypt(mPassword1.getText().toString(), EncryptUtil.MD5);
		BaseParams params = new BaseParams();
		params.add("method", "changeDriverPwd");
		params.add("driver_cd", phone);
		params.add("is_need", "0");
		params.add("old_pwd", BaseParams.PARAM_DEFAULT);
		params.add("new_pwd", password);
		AsyncHttp.get(Const.URL_CHANGE_PASSWORD, params, mRequestHandler);
	}
	
	private boolean validCheck() {
		String phone = mPhone.getText().toString();
		String code = mCode.getText().toString();
		String password1 = mPassword1.getText().toString();
		String password2 = mPassword2.getText().toString();
		
		if (TextUtils.isEmpty(phone)) {
			Util.showTips(this, getResources().getString(
					R.string.phone_empty));
			return false;
		} else if (TextUtils.isEmpty(code)) {
			Util.showTips(this, getResources().getString(
					R.string.vertify_code_empty));
			return false;
		} else if (TextUtils.isEmpty(password1)) {
			Util.showTips(this, getResources().getString(
					R.string.password_empty));
			return false;
		} else if (!Util.isPhoneNumber(phone)) {
			Util.showTips(this, getResources().getString(
					R.string.phone_invalid));
			return false;
		} else if (!Util.isCodeValid(code)) {
			Util.showTips(this, getResources().getString(
					R.string.vertify_code_invalid));
			return false;
		} else if (!Util.isPasswordValid(password1)) {
			Util.showTips(this, getResources().getString(
					R.string.password_invalid));
			return false;
		} else if (TextUtils.isEmpty(password2) || !password1.equals(password2)) {
			Util.showTips(this, getResources().getString(
					R.string.password_not_same));
			return false;
		}
		
		return true;
	}
	
	private void showCountDown() {
		mIsCountDown = true;
		mGetCode.setEnabled(false);
		updateCountDown(COOLDOWN_TIME);
		mCountDown.setVisibility(View.VISIBLE);
		mCountDownTimer.cancel();
		mCountDownTimer.start();
	}
	
	private void hideCountDown() {
		mIsCountDown = false;
		mCountDown.setVisibility(View.GONE);
		mCountDownTimer.cancel();
		String phone = mPhone.getText().toString();
		if (phone != null && Util.isPhoneNumber(phone)) {
			mGetCode.setEnabled(true);
		}
	}
	
	private void updateCountDown(long millisUntilFinished) {
		String txt = String.format(getResources().getString(R.string.vertify_code_countdown), (int)(millisUntilFinished / 1000));
		mCountDown.setText(txt);
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
					finish();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.register_failed));
	}
	
	private static class ForgetPasswdHandler extends WeakHandler<ForgetPasswdActivity> {

		public ForgetPasswdHandler(ForgetPasswdActivity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(ForgetPasswdActivity t, Message msg) {
			switch (msg.what) {
			case MSG_GET_VERIFICATION_CODE_ERROR:
				Util.showTips(t, "获取失败");
				break;
			case MSG_GET_VERIFICATION_CODE_COMPLETE:
				Util.showTips(t, "获取成功，请等待短信");
				break;
			case MSG_SUBMIT_VERIFICATION_CODE_ERROR:
				t.hideProgressDialog();
				Util.showTips(t, "效验码验证失败！");
				break;
			case MSG_SUBMIT_VERIFICATION_CODE_COMPLETE:
				t.submit();
				break;
			}
		}
	}
	
	public static void startForgetPasswdActivity(Context context) {
		context.startActivity(new Intent(context, ForgetPasswdActivity.class));
	}
}
