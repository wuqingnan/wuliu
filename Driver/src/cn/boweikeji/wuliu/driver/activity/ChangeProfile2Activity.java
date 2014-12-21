package cn.boweikeji.wuliu.driver.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.RegisterInfo;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.LoginEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.view.ClearEditText;

import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ChangeProfile2Activity extends BaseActivity {

	private static final String TAG = ChangeProfile2Activity.class
			.getSimpleName();

	private static final String KEY_INFO = "info";

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mTruckType) {
				truckType();
			} else if (view == mRule) {
				showRule();
			} else if (view == mSubmit) {
				submit();
			}
		}
	};

	private JsonHttpResponseHandler mUploadHandler = new JsonHttpResponseHandler() {

		public void onFinish() {

		};

		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			Log.d(TAG, "shizy---onSuccess");
			uploadResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			uploadResult(null);
			Log.d(TAG, "shizy---onFailure");
		};

	};

	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {

		public void onFinish() {
			hideProgressDialog();
		};

		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			requestResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.register_truck_type)
	TextView mTruckType;
	@InjectView(R.id.register_truck_no)
	ClearEditText mTruckNumber;
	@InjectView(R.id.register_truck_load)
	ClearEditText mTruckLoad;
	@InjectView(R.id.register_remark)
	ClearEditText mRemark;
	@InjectView(R.id.register_accept)
	CheckBox mAccept;
	@InjectView(R.id.register_rule)
	TextView mRule;
	@InjectView(R.id.register_submit)
	Button mSubmit;

	private int mTruckTypeIndex;
	private String[] mTruckTypes;

	private ProgressDialog mProgressDialog;

	private RegisterInfo mRegInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_profile2);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_change_profile2);
		mBack.setOnClickListener(mOnClickListener);
		mTruckType.setOnClickListener(mOnClickListener);
		mRule.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		Intent intent = getIntent();
		mRegInfo = (RegisterInfo) intent.getSerializableExtra(KEY_INFO);

		mTruckTypes = getResources().getStringArray(R.array.truck_type_list);

		UserInfo userInfo = LoginManager.getInstance().getUserInfo();
		mTruckTypeIndex = userInfo.getTruck_type_code();
		mTruckNumber.setText(userInfo.getTruck_no());
		mTruckLoad.setText("" + userInfo.getLoad_weight());
		updateTruckType();
	}

	private void updateTruckType() {
		mTruckType.setText(mTruckTypes[mTruckTypeIndex]);
	}

	private void showRule() {
		WebViewActivity.startWebViewActivity(this,
				getString(R.string.title_rule), Const.URL_RULES);
	}

	private void submit() {
		if (validCheck()) {
			showProgressDialog();
			if (!TextUtils.isEmpty(mRegInfo.getIDImagePath())) {
				new ImageTask(this, mRegInfo.getIDImagePath()).execute();
			} else {
				changeProfile();
			}
		}
	}

	private void uploadImage(String path) {
		Log.d(TAG, "shizy---path: " + path);
		try {
			if (path != null) {
				BaseParams params = new BaseParams();
				params.put("myFile", new File(path));
				params.add("remark", Const.NULL);
				AsyncHttp.post(Const.URL_UPLOAD_IMAGE, params, mUploadHandler);
				return;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		hideProgressDialog();
		Util.showTips(this,
				getResources().getString(R.string.upload_id_image_fail));
	}

	/**
	 * 提交信息
	 */
	private void changeProfile() {
		BaseParams params = mRegInfo.getChangeProfileParams();
		AsyncHttp.get(Const.URL_CHANGE_PROFILE, params, mRequestHandler);
	}

	private boolean validCheck() {
		String tNumber = mTruckNumber.getText().toString();
		String tLoad = mTruckLoad.getText().toString();
		String remark = mRemark.getText().toString();

		if (mTruckTypeIndex == 0) {
			Util.showTips(this,
					getResources().getString(R.string.choose_truck_type));
			return false;
		} else if (tNumber == null || tNumber.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.truck_number_empty));
			return false;
		} else if (tLoad == null || tLoad.equals("")) {
			Util.showTips(this,
					getResources().getString(R.string.truck_load_empty));
			return false;
		}

		if (!(Util.isInteger(tLoad) || Util.isDecimal(tLoad))) {
			Util.showTips(this,
					getResources().getString(R.string.truck_load_invalid));
			return false;
		}

		if (!mAccept.isChecked()) {
			Util.showTips(this, getResources().getString(R.string.accept_rules));
			return false;
		}

		mRegInfo.setTrunk_no(tNumber);
		mRegInfo.setLoad_weight(Float.parseFloat(tLoad));
		mRegInfo.setTruck_type_code(mTruckTypeIndex);
		mRegInfo.setRemark(remark);

		return true;
	}

	private void truckType() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(mTruckTypes, mTruckTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mTruckTypeIndex = which;
								dialog.dismiss();
								updateTruckType();
							}
						}).setTitle("车辆类型").create();
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

	private void changeSuccess() {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {// 成功
					changeSuccess();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.register_failed));
	}

	private void uploadResult(JSONObject response) {
		Log.d(TAG, "shizy---uploadResult");
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {// 成功
					mRegInfo.setCard_photo(response.optString("attachment_id"));
					changeProfile();
				} else {
					Util.showTips(this, msg);
					hideProgressDialog();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		hideProgressDialog();
		Util.showTips(this, getString(R.string.upload_id_image_fail));
	}

	public static void startChangeProfile2Activity(Context context,
			RegisterInfo info) {
		Intent intent = new Intent(context, ChangeProfile2Activity.class);
		intent.putExtra(KEY_INFO, info);
		context.startActivity(intent);
	}

	public static class ImageTask extends AsyncTask<Void, Integer, String> {

		private static final int WIDTH = 960;
		private static final int HEIGHT = 640;

		private WeakReference<ChangeProfile2Activity> mReference;
		private String mPath;

		public ImageTask(ChangeProfile2Activity activity, String path) {
			mReference = new WeakReference<ChangeProfile2Activity>(activity);
			mPath = path;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(mPath, op);
			int xScale = op.outWidth / WIDTH;
			int yScale = op.outHeight / HEIGHT;
			op.inSampleSize = xScale > yScale ? xScale : yScale;
			op.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(mPath, op);
			byte[] data = compressImage(bitmap);
			String filePath = saveImage(data);
			if (bitmap != null) {
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
				bitmap = null;
			}
			return filePath;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ChangeProfile2Activity activity = mReference.get();
			if (activity != null) {
				activity.uploadImage(result);
			}
		}

		private byte[] compressImage(Bitmap image) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) {
				baos.reset();
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);
				options -= 10;// 每次都减少10
			}
			Log.d(TAG, "shizy---options: " + options);
			byte[] data = null;
			try {
				data = baos.toByteArray();
				baos.close();
				baos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return data;
		}

		private String saveImage(byte[] data) {
			try {
				Context context = mReference.get();
				if (context != null) {
					String filePath = context.getFilesDir().getPath()
							+ "/card.jpg";
					FileOutputStream fos = new FileOutputStream(filePath);
					fos.write(data);
					fos.close();
					fos = null;
					return filePath;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
