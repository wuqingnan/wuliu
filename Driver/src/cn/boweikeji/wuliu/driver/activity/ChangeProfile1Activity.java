package cn.boweikeji.wuliu.driver.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WeakHandler;
import cn.boweikeji.wuliu.driver.bean.RegisterInfo;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.bean.city.City;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
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

public class ChangeProfile1Activity extends BaseActivity {

	private static final String TAG = ChangeProfile1Activity.class
			.getSimpleName();

	private static final int REQUESTCODE_PICK = 1 << 0;
	private static final int REQUESTCODE_CAPTURE = 1 << 1;
	private static final int REQUESTCODE_CITYLIST = 1 << 2;

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mDriverType) {
				userType();
			} else if (view == mAreaCode) {
				chooseCity();
			} else if (view == mIDImage) {
				chooseIDImage();
			} else if (view == mNextStep) {
				next();
			}
		}
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
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
	@InjectView(R.id.register_id)
	ClearEditText mIDNumber;
	@InjectView(R.id.register_id_front)
	ImageView mIDImage;
	@InjectView(R.id.next_step)
	Button mNextStep;

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
		setContentView(R.layout.activity_change_profile1);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recyclePhoto();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_change_profile1);
		mBack.setOnClickListener(mOnClickListener);
		mDriverType.setOnClickListener(mOnClickListener);
		mAreaCode.setOnClickListener(mOnClickListener);
		mIDImage.setOnClickListener(mOnClickListener);
		mNextStep.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		mDriverTypes = getResources().getStringArray(R.array.driver_types);
		
		UserInfo userInfo = LoginManager.getInstance().getUserInfo();
		mDriverTypeIndex = userInfo.getTruck_type_code();
		updateDriverType();
	}

	private void next() {
		if (validCheck()) {
			ChangeProfile2Activity.startChangeProfile2Activity(this, mRegInfo);
		}
	}

	private boolean validCheck() {
		String name = mName.getText().toString();
		String company = mCompany.getText().toString();
		String idNumber = mIDNumber.getText().toString();

		if (name == null || name.equals("")) {
			Util.showTips(this, getResources().getString(R.string.name_empty));
			return false;
		} else if ((company == null || company.equals(""))
				&& mDriverTypeIndex == 1) {
			Util.showTips(this, getResources()
					.getString(R.string.company_empty));
			return false;
		} else if (mCity == null) {
			Util.showTips(this, getResources().getString(R.string.choose_city));
			return false;
		} else if (idNumber == null || idNumber.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.id_number_empty));
			return false;
		} else if (!Util.isIDNumberValid(idNumber)) {
			Util.showTips(this,
					getResources().getString(R.string.id_number_invalid));
			return false;
		}

		if (mRegInfo == null) {
			mRegInfo = new RegisterInfo();
			mRegInfo.setDriver_name(name);
			mRegInfo.setDriver_type(mDriverTypeIndex);
			mRegInfo.setComp_name(company);
			mRegInfo.setArea_code(mCity.getCode());
			mRegInfo.setCard_id(idNumber);
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
				.setItems(R.array.choose_image_from,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int which) {
								switch (which) {
								case 0:// 相册
									pickImage();
									break;
								case 1:// 拍照
									startCamera();
									break;
								}
							}
						}).setTitle(R.string.choose_image).create();
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
			mPhotoUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new ContentValues());
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mPhotoUri);
		} catch (Exception e) {
		}
		startActivityForResult(intent, REQUESTCODE_CAPTURE);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 *            图片本地路径
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
		mIDImage.setBackgroundDrawable(new BitmapDrawable(getResources(),
				bitmap));
		recyclePhoto();
		mPhoto = bitmap;
	}

	/**
	 * uri转本地路径
	 * 
	 * @param uri
	 * @return
	 */
	private String uri2path(Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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

	public static void startChangeProfile1Activity(Context context) {
		context.startActivity(new Intent(context, ChangeProfile1Activity.class));
	}

}
