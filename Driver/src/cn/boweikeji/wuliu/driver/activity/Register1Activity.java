package cn.boweikeji.wuliu.driver.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WeakHandler;
import cn.boweikeji.wuliu.driver.bean.RegisterInfo;
import cn.boweikeji.wuliu.driver.bean.city.City;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.view.ClearEditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Register1Activity extends BaseActivity {

	private static final String TAG = Register1Activity.class
			.getSimpleName();

	private static final String COUNTRY_CODE = "86";

	private static final long COOLDOWN_INTERVAL = 1000;
	private static final long COOLDOWN_TIME = 60 * COOLDOWN_INTERVAL;

	private static final int MSG_GET_VERIFICATION_CODE_ERROR = 1 << 0;
	private static final int MSG_GET_VERIFICATION_CODE_COMPLETE = 1 << 1;
	private static final int MSG_SUBMIT_VERIFICATION_CODE_ERROR = 1 << 2;
	private static final int MSG_SUBMIT_VERIFICATION_CODE_COMPLETE = 1 << 3;
	
	private static final int REQUESTCODE_PICK = 1 << 0;
	private static final int REQUESTCODE_CAPTURE = 1 << 1;
	private static final int REQUESTCODE_CITYLIST = 1 << 2;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mGetCode) {
				getVerifyCode();
			} else if (view == mDriverType) {
				userType();
			} else if (view == mAreaCode) {
				chooseCity();
			} else if (view == mIDImage) {
				chooseIDImage();
			} else if (view == mNextStep) {
				vertify();
			}
		}
	};

	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s != null && Util.isPhoneNumber(s.toString()) && !mIsCountDown) {
				mGetCode.setEnabled(true);
			} else {
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
	ImageView mBack;
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
	ClearEditText mPasswd;
	@InjectView(R.id.register_name)
	ClearEditText mName;
	@InjectView(R.id.register_driver_type)
	TextView mDriverType;
	@InjectView(R.id.register_company_layout)
	LinearLayout mCompanyLayout;
	@InjectView(R.id.register_company)
	ClearEditText mCompany;
	@InjectView(R.id.register_area_code)
	TextView mAreaCode;
	@InjectView(R.id.register_id_front)
	ImageView mIDImage;
	@InjectView(R.id.next_step)
	Button mNextStep;

	private boolean mIsCountDown = false;

	private RegisterHandler mHandler = null;

	private ProgressDialog mProgressDialog;

	private String[] mDriverTypes;
	private int mDriverTypeIndex;
	
	private City mCity;
	
	/** 照片Uri */
	private Uri mPhotoUri;
	private String mPhotoPath;
	private Bitmap mPhoto;
	
	private RegisterInfo mRegInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register1);
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
		recyclePhoto();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_register1);
		mBack.setOnClickListener(mOnClickListener);
		mGetCode.setOnClickListener(mOnClickListener);
		mDriverType.setOnClickListener(mOnClickListener);
		mAreaCode.setOnClickListener(mOnClickListener);
		mIDImage.setOnClickListener(mOnClickListener);
		mNextStep.setOnClickListener(mOnClickListener);
		mPhone.addTextChangedListener(mTextWatcher);
		mPasswd.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mGetCode.setEnabled(false);
	}

	private void initData() {
		mDriverTypeIndex = 0;
		mDriverTypes = getResources().getStringArray(R.array.driver_types);
		updateDriverType();
	}

	private void getVerifyCode() {
		showCountDown();
		SMSSDK.getVerificationCode(COUNTRY_CODE, mPhone.getText().toString());
	}

	private void vertify() {
		if (validCheck()) {
			showProgressDialog();
			SMSSDK.submitVerificationCode(COUNTRY_CODE, mPhone.getText()
					.toString(), mCode.getText().toString());
		}
	}

	private void next() {
		Register2Activity.startRegister2Activity(this, mRegInfo);
	}

	private boolean validCheck() {
		String phone = mPhone.getText().toString();
		String code = mCode.getText().toString();
		String passwd = mPasswd.getText().toString();
		String name = mName.getText().toString();
		String company = mCompany.getText().toString();

		if (phone == null || phone.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.phone_empty));
			return false;
		} else if (code == null || code.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.vertify_code_empty));
			return false;
		} else if (passwd == null || passwd.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.password_empty));
			return false;
		} else if (name == null || name.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.name_empty));
			return false;
		} else if ((company == null || company.equals("")) && mDriverTypeIndex == 1) {
			Util.showTips(this,
					getResources().getString(R.string.company_empty));
			return false;
		} else if (mCity == null) {
			Util.showTips(this,
					getResources().getString(R.string.choose_city));
			return false;
		} else if (mPhotoPath == null || mPhotoPath.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.choose_register_image));
			return false;
		} else if (!Util.isPhoneNumber(phone)) {
			Util.showTips(this, getResources()
					.getString(R.string.phone_invalid));
			return false;
		} else if (!Util.isCodeValid(code)) {
			Util.showTips(this,
					getResources().getString(R.string.vertify_code_invalid));
			return false;
		} else if (!Util.isPasswordValid(passwd)) {
			Util.showTips(this,
					getResources().getString(R.string.password_invalid));
			return false;
		}
		
		if (mRegInfo == null) {
			mRegInfo = new RegisterInfo();
			mRegInfo.setPhone(phone);
			mRegInfo.setPasswd(passwd);
			mRegInfo.setDriver_name(name);
			mRegInfo.setDriver_type(mDriverTypeIndex);
			mRegInfo.setComp_name(company);
			mRegInfo.setArea_code(mCity.getCode());
			mRegInfo.setIDImagePath(mPhotoPath);
		}
		
		return true;
	}

	private void updateDriverType() {
		mDriverType.setText(mDriverTypes[mDriverTypeIndex]);
		switch (mDriverTypeIndex) {
		case 0:
		case 2:
			mCompanyLayout.setVisibility(View.GONE);
			break;
		case 1:
			mCompanyLayout.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void updateCity() {
		if (mCity != null) {
			mAreaCode.setText(mCity.getName());
		}
	}
	
	private void userType() {
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
						}).setTitle("类型").create();
		dialog.show();
	}
	
	private void chooseCity() {
		Intent intent = new Intent(this, CityListActivity.class);
		startActivityForResult(intent, REQUESTCODE_CITYLIST);
	}
	
	private void chooseIDImage() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setItems(R.array.choose_image_from, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int which) {
				switch (which) {
				case 0://相册
					pickImage();
					break;
				case 1://拍照
					startCamera();
					break;
				}
			}
		})
		.setTitle(R.string.choose_image).create();
		dialog.show();
	}

	/**
	 * 选择图片
	 */
	private void pickImage() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUESTCODE_PICK);
	}
	
	/**
	 * 开启照相机
	 */
	private void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
    		mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
    		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mPhotoUri);
    	} catch (Exception e) {}
		startActivityForResult(intent, REQUESTCODE_CAPTURE);
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
	
	/**
	 * 加载图片
	 * @param uri	图片本地路径
	 */
	@SuppressWarnings("deprecation")
	private void loadImage(Uri uri) {
		mPhotoPath = uri2path(uri);
		if (mPhotoPath == null) {
			return;
		}
		int pWidth = mIDImage.getWidth();
		int pHeight = mIDImage.getHeight();
		BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoPath, op);
        int xScale = op.outWidth / pWidth;
        int yScale = op.outHeight / pHeight;
        op.inSampleSize = xScale > yScale ? xScale : yScale;
        op.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, op);
        mIDImage.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        recyclePhoto();
        mPhoto = bitmap;
	}
	
	/**
	 * uri转本地路径
	 * @param uri
	 * @return
	 */
	private String uri2path(Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] proj = { MediaStore.Images.Media.DATA };  
		Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);  
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
		actualimagecursor.moveToFirst();  
		String path = actualimagecursor.getString(actual_image_column_index);
		return path;
	}
	
	/**
	 * 释放图片
	 */
	private void recyclePhoto() {
		if (mPhoto != null) {
        	if (!mPhoto.isRecycled()) {
        		mPhoto.recycle();
        	}
        	mPhoto = null;
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_CAPTURE:
				loadImage(mPhotoUri);
				break;
			case REQUESTCODE_PICK:
				loadImage(data.getData());
				break;
			case REQUESTCODE_CITYLIST:
				mCity = (City) data.getSerializableExtra("city");
				updateCity();
				break;
			}
		} else {
			switch (requestCode) {
			case REQUESTCODE_CAPTURE:
				Log.d(TAG, "onFailture Capture");
				if (mPhotoUri != null) {
					getContentResolver().delete(mPhotoUri, null, null);
				}
				break;
			case REQUESTCODE_PICK:
				break;
			}
		}
	}

	public static void startRegister1Activity(Context context) {
		context.startActivity(new Intent(context, Register1Activity.class));
	}

	private static class RegisterHandler extends
			WeakHandler<Register1Activity> {

		public RegisterHandler(Register1Activity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(Register1Activity t, Message msg) {
			switch (msg.what) {
			case MSG_GET_VERIFICATION_CODE_ERROR:
				Util.showTips(t, "获取失败");
				t.hideCountDown();
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
