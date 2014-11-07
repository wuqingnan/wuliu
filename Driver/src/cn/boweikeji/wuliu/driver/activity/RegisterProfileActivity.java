package cn.boweikeji.wuliu.driver.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WeakHandler;
import cn.boweikeji.wuliu.driver.utils.Util;
import cn.boweikeji.wuliu.driver.view.ClearEditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterProfileActivity extends BaseActivity {

	private static final String TAG = RegisterProfileActivity.class
			.getSimpleName();

	private static final String COUNTRY_CODE = "86";

	private static final long COOLDOWN_TIME = 60 * 1000;

	private static final int MSG_GET_VERIFICATION_CODE_ERROR = 1 << 0;
	private static final int MSG_GET_VERIFICATION_CODE_COMPLETE = 1 << 1;
	private static final int MSG_SUBMIT_VERIFICATION_CODE_ERROR = 1 << 2;
	private static final int MSG_SUBMIT_VERIFICATION_CODE_COMPLETE = 1 << 3;

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mCodeBtn) {
				getVerifyCode();
			} else if (view == mDriverType) {
				driverType();
			} else if (view == mAreaCode) {

			} else if (view == mIDImage) {

			} else if (view == mNextStep) {
				vertify();
			}
		}
	};

	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s != null && Util.isPhoneNumber(s.toString()) && !mCoolDown) {
				mCodeBtn.setEnabled(true);
			} else {
				mCodeBtn.setEnabled(false);
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

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.register_phone)
	ClearEditText mPhone;
	@InjectView(R.id.register_code)
	ClearEditText mCode;
	@InjectView(R.id.register_get_code)
	Button mCodeBtn;
	@InjectView(R.id.register_password)
	ClearEditText mPasswd;
	@InjectView(R.id.register_name)
	ClearEditText mName;
	@InjectView(R.id.register_driver_type)
	TextView mDriverType;
	@InjectView(R.id.register_company)
	ClearEditText mCompany;
	@InjectView(R.id.register_area_code)
	TextView mAreaCode;
	@InjectView(R.id.register_id)
	ClearEditText mIDNumber;
	@InjectView(R.id.register_id_front)
	TextView mIDImage;
	@InjectView(R.id.next_step)
	Button mNextStep;

	private boolean mCoolDown = false;

	private RegisterHandler mHandler = null;

	private ProgressDialog mProgressDialog;

	private String[] mDriverTypes;
	private int mDriverTypeIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_profile);
		initView();
		initData();
		mHandler = new RegisterHandler(this);
		SMSSDK.initSDK(this, "2efbb3982f2a", "5fadc7e323623a695f5fe6b26d5ed79f");
		SMSSDK.registerEventHandler(mEventHandler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.register);
		mBack.setOnClickListener(mOnClickListener);
		mCodeBtn.setOnClickListener(mOnClickListener);
		mDriverType.setOnClickListener(mOnClickListener);
		mAreaCode.setOnClickListener(mOnClickListener);
		mIDImage.setOnClickListener(mOnClickListener);
		mNextStep.setOnClickListener(mOnClickListener);
		mPhone.addTextChangedListener(mTextWatcher);
		mPasswd.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}

	private void initData() {
		mDriverTypeIndex = 0;
		mDriverTypes = getResources().getStringArray(R.array.driver_types);
		updateDriverType();
	}

	private void getVerifyCode() {
		mCoolDown = true;
		mCodeBtn.setEnabled(false);
		SMSSDK.getVerificationCode(COUNTRY_CODE, mPhone.getText().toString());
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mCoolDown = false;
				String phone = mPhone.getText().toString();
				if (phone != null && Util.isPhoneNumber(phone)) {
					mCodeBtn.setEnabled(true);
				}
			}
		}, COOLDOWN_TIME);
	}

	private void vertify() {
		next();
		if (validCheck()) {
			showProgressDialog();
			SMSSDK.submitVerificationCode(COUNTRY_CODE, mPhone.getText()
					.toString(), mCode.getText().toString());
		}
	}

	private void next() {
		RegisterTruckActivity.startRegisterInfoActivity(this, mPhone.getText()
				.toString());
	}

	private boolean validCheck() {
		String phone = mPhone.getText().toString();
		String code = mCode.getText().toString();

		if (phone == null || phone.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.phone_empty));
			return false;
		} else if (code == null || code.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.vertify_code_empty));
			return false;
		} else if (!Util.isPhoneNumber(phone)) {
			Util.showTips(this, getResources()
					.getString(R.string.phone_invalid));
			return false;
		} else if (!Util.isCodeValid(code)) {
			Util.showTips(this,
					getResources().getString(R.string.vertify_code_invalid));
			return false;
		}
		return true;
	}

	private void updateDriverType() {
		mDriverType.setText(mDriverTypes[mDriverTypeIndex]);
	}

	private void driverType() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(mDriverTypes, mDriverTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mDriverTypeIndex = which;
								dialog.dismiss();
								updateDriverType();
							}
						}).setTitle("货源").create();
		dialog.show();
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
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

	public static void startRegisterPhoneActivity(Context context) {
		context.startActivity(new Intent(context, RegisterProfileActivity.class));
	}

	private static class RegisterHandler extends
			WeakHandler<RegisterProfileActivity> {

		public RegisterHandler(RegisterProfileActivity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(RegisterProfileActivity t, Message msg) {
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
				t.hideProgressDialog();
				t.next();
				break;
			}
		}
	}

}
