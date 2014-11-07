package cn.boweikeji.wuliu.driver.activity;

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
import cn.boweikeji.wuliu.driver.utils.EncryptUtil;
import cn.boweikeji.wuliu.driver.utils.Util;
import cn.boweikeji.wuliu.driver.view.ClearEditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterTruckActivity extends BaseActivity {
	
	private static final String TAG = RegisterTruckActivity.class.getSimpleName();
	
	private static final String KEY_PHONE = "phone";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mTruckType) {
				truckType();
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
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.register_truck_type)
	TextView mTruckType;
	@InjectView(R.id.register_truck_no)
	ClearEditText mTruckNumber;
	@InjectView(R.id.register_truck_load)
	ClearEditText mTruckLoad;
	@InjectView(R.id.register_recommend_no)
	ClearEditText mRecommendNumber;
	@InjectView(R.id.register_remark)
	ClearEditText mRemark;
	@InjectView(R.id.register_submit)
	Button mSubmit;

	private int mTruckTypeIndex;
	private String[] mTruckTypes;
	
	private RegisterInfo mRegisterInfo;
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_truck);
		initView();
		initData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.register);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mTruckType.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		mTruckTypeIndex = 0;
		mTruckTypes = getResources().getStringArray(R.array.goods_traffic_list);
		updateTruckType();
	}

	private void updateTruckType() {
		mTruckType.setText(mTruckTypes[mTruckTypeIndex]);
	}

	private void register() {
		if (validCheck()) {
			showProgressDialog();
			updateLoadImage();
		}
	}
	
	private void updateLoadImage() {
		
	}
	
	/**
	 * 提交注册信息
	 */
	private void submit() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = mRegisterInfo.getRegisterParams();
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_REGISTER, params));
		client.get(Const.URL_REGISTER, params, mRequestHandler);
	}
	
	private void login() {
//		hideProgressDialog();
//		Intent intent = new Intent();
//		UserInfo info = new UserInfo();
//		String phone = mPhone.getText().toString();
//		String password = EncryptUtil.encrypt(mPassword.getText().toString(), EncryptUtil.MD5);
////		info.setSupplyer_cd(phone);
//		info.setPasswd(password);
//		intent.putExtra("userinfo", info);
//		setResult(RESULT_OK, intent);
//		finish();
	}
	
	private boolean validCheck() {
		String tNumber = mTruckNumber.getText().toString();
		String tLoad = mTruckLoad.getText().toString();
		String rNumber = mRecommendNumber.getText().toString();
		String remark = mRemark.getText().toString();
		
		if (mTruckTypeIndex == 0) {
			Util.showTips(this, getResources().getString(
					R.string.choose_truck_type));
			return false;
		} else if (tNumber == null || tNumber.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.truck_number_empty));
			return false;
		} else if (tLoad == null || tLoad.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.truck_load_empty));
			return false;
		}
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
					login();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.register_failed));
	}
	
	public static void startRegisterInfoActivity(Context context, String phone) {
		Intent intent = new Intent(context, RegisterTruckActivity.class);
		intent.putExtra(KEY_PHONE, phone);
		context.startActivity(intent);
	}
	
}
