package cn.boweikeji.wuliu.supplyer.activity;


import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.WeakHandler;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.event.LoginEvent;
import cn.boweikeji.wuliu.supplyer.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.utils.EncryptUtil;
import cn.boweikeji.wuliu.supplyer.utils.Util;
import cn.boweikeji.wuliu.supplyer.view.ClearEditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;







import com.loopj.android.http.JsonHttpResponseHandler;

import cn.boweikeji.wuliu.supplyer.R;
import de.greenrobot.event.EventBus;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity {

	private static final String TAG = RegisterActivity.class.getSimpleName();
	
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
			} else if (view == mUserType) {
				userType();
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
	@InjectView(R.id.register_phone)
	ClearEditText mPhone;
	@InjectView(R.id.register_code)
	ClearEditText mCode;
	@InjectView(R.id.register_get_code)
	LinearLayout mGetCode;
	@InjectView(R.id.register_countdown)
	TextView mCountDown;
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
	
	private boolean mIsCountDown = false;
	
	private RegisterHandler mHandler = null;
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		initData();
		mHandler = new RegisterHandler(this);
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
		mTitle.setText(R.string.register);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mGetCode.setOnClickListener(mOnClickListener);
		mUserType.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
		mPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mPhone.addTextChangedListener(mTextWatcher);
		mGetCode.setEnabled(false);
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
	 * 提交注册信息
	 */
	private void submit() {
		String phone = mPhone.getText().toString();
		String password = EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5);
		String id = mIDNumber.getText().toString();
		BaseParams params = new BaseParams();
		params.add("method", "registerGoodSupplyer");
		params.add("supplyer_type", "" + mUserTypeIndex);
		params.add("phone", phone);
		params.add("credit_level", "0");
		params.add("state", "0");
		params.add("card_id", (id == null || id.equals("")) ? BaseParams.PARAM_DEFAULT : id);
		params.add("passwd", password);
		AsyncHttp.get(Const.URL_REGISTER, params, mRequestHandler);
	}
	
	private void registerSuccess(JSONObject infos) {
		LoginManager.getInstance().setLogin(true);
		UserInfo userInfo = new UserInfo(infos);
		userInfo.setPasswd(EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5));
		LoginManager.getInstance().setUserInfo(userInfo);
		EventBus.getDefault().post(new LoginEvent());
		startActivity(new Intent(this, MainActivity.class));
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
					R.string.phone_invalid));
			return false;
		} else if (!Util.isCodeValid(code)) {
			Util.showTips(this, getResources().getString(
					R.string.register_code_invalid));
			return false;
		} else if (!Util.isPasswordValid(password)) {
			Util.showTips(this, getResources().getString(
					R.string.password_invalid));
			return false;
		} else if (id != null && !id.equals("") && !Util.isIDNumberValid(id)) {
			Util.showTips(this, getResources().getString(
					R.string.register_id_invalid));
			return false;
		}
		
		return true;
	}
	
	private void userType() {
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
				if (res == 2) {//成功
					registerSuccess(response.optJSONObject("supplyer"));
				} else {
					Util.showTips(this, msg);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.register_failed));
	}
	
	private static class RegisterHandler extends WeakHandler<RegisterActivity> {

		public RegisterHandler(RegisterActivity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(RegisterActivity t, Message msg) {
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
	
	public static void startRegisterActivity(Context context) {
		context.startActivity(new Intent(context, RegisterActivity.class));
	}
}
