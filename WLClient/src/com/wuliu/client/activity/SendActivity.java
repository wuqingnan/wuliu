package com.wuliu.client.activity;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.wuliu.client.R;
import com.wuliu.client.WLApplication;
import com.wuliu.client.window.TimeWheel;
import com.wuliu.client.window.WheelWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SendActivity extends Activity {

	private static final String TAG = SendActivity.class.getSimpleName();
	
	private static final String KEY_BESPEAK = "bespeak";
	
	private static final int REQUEST_CODE_SEARCH = 0x010;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mGoodsType) {
				showTypeChooseDialog();
			} else if (view == mGoodsTraffic) {
				showTrafficChooseDialog();
			} else if (view == mSwapAddress) {
				swapAddress();
			} else if (view == mNextStep) {
				sendDetail();
			} else if (view == mClearTime) {
				mTakeTime.setText(null);
				updateClearState();
			} else if (view == mTakeTime) {
				showTimePicker();
			} else if (view == mAddressFrom) {
				searchAddress(true);
			} else if (view == mAddressTo) {
				searchAddress(false);
			}
		}
	};
	
	private WheelWindow.OnConfirmListener mConfirmListener = new WheelWindow.OnConfirmListener() {

		@Override
		public void onConfirm(String result) {
			mBespeakTime = Long.parseLong(result);
			SimpleDateFormat format = new SimpleDateFormat(getString(R.string.bespeak_time_format));
			mTakeTime.setText(format.format(new Date(mBespeakTime)));
			updateClearState();
			mWheelWindow = null;
		}
		
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.take_time_layout)
	LinearLayout mTakeTimeLayout;
	@InjectView(R.id.take_time)
	TextView mTakeTime;
	@InjectView(R.id.clear_time)
	ImageView mClearTime;
	@InjectView(R.id.goods_type)
	TextView mGoodsType;
	@InjectView(R.id.goods_name)
	EditText mGoodsName;
	@InjectView(R.id.goods_weight)
	EditText mGoodsWeight;
	@InjectView(R.id.goods_value)
	EditText mGoodsValue;
	@InjectView(R.id.goods_traffic)
	TextView mGoodsTraffic;
	@InjectView(R.id.address_from)
	TextView mAddressFrom;
	@InjectView(R.id.address_to)
	TextView mAddressTo;
	@InjectView(R.id.swap_address)
	ImageView mSwapAddress;
	@InjectView(R.id.next_step)
	Button mNextStep;

	private WheelWindow mWheelWindow;
	
	//0:省、1:市、2:区县、3:街道
	private String[] mFromInfos = new String[4];
	private String[] mToInfos = new String[4];
	
	private String[] mTypeList;
	private String[] mTrafficList;
	private int mTypeIndex;
	private int mTrafficIndex;
	private long mBespeakTime;
	private boolean mBespeak;
	private boolean mIsFrom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_send);
		handleIntent();
		initView();
		initData();
		initAddress();
	}

	private void handleIntent() {
		mBespeak = getIntent().getBooleanExtra(KEY_BESPEAK, false);
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mGoodsType.setOnClickListener(mOnClickListener);
		mGoodsTraffic.setOnClickListener(mOnClickListener);
		mSwapAddress.setOnClickListener(mOnClickListener);
		mNextStep.setOnClickListener(mOnClickListener);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mAddressFrom.setOnClickListener(mOnClickListener);
		mAddressTo.setOnClickListener(mOnClickListener);
		if (mBespeak) {
			mTakeTimeLayout.setVisibility(View.VISIBLE);
			mTakeTime.setOnClickListener(mOnClickListener);
			mClearTime.setOnClickListener(mOnClickListener);
		}
		mTitle.setText(mBespeak ? R.string.title_bespeak : R.string.title_send);
	}

	private void initData() {
		mTypeIndex = 0;
		mTrafficIndex = 0;
		mTypeList = getResources().getStringArray(R.array.goods_type_list);
		mTrafficList = getResources()
				.getStringArray(R.array.goods_traffic_list);
		updateType();
		updateTraffic();
	}
	
	private void initAddress() {
		LocationClient client = WLApplication.getLocationClient();
		if (client != null) {
			BDLocation location = client.getLastKnownLocation();
			if (location != null && location.hasAddr()) {
				mAddressFrom.setText(location.getAddrStr());
				mAddressTo.setText(location.getAddrStr());
				mFromInfos[0] = mToInfos[0] = location.getProvince();
				mFromInfos[1] = mToInfos[1] = location.getCity();
				mFromInfos[2] = mToInfos[2] = location.getDistrict();
				mFromInfos[3] = mToInfos[3] = location.getStreet();
			}
		}
	}

	private void updateType() {
		mGoodsType.setText(mTypeList[mTypeIndex]);
	}

	private void updateTraffic() {
		mGoodsTraffic.setText(mTrafficList[mTrafficIndex]);
	}
	
	private void sendDetail() {
		Intent intent = new Intent(this, SendDetailActivity.class);
		startActivity(intent);
	}
	
	private void searchAddress(boolean isFrom) {
		mIsFrom = isFrom;
		SearchActivity.startSearchActivity(SendActivity.this, isFrom, REQUEST_CODE_SEARCH, isFrom ? mFromInfos : mToInfos);
	}

	private void swapAddress() {
		CharSequence temp = mAddressFrom.getText();
		mAddressFrom.setText(mAddressTo.getText());
		mAddressTo.setText(temp);
	}
	
	private void showTypeChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(mTypeList, mTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mTypeIndex = which;
								dialog.dismiss();
								updateType();
							}
						}).setTitle("物品类型").create();
		dialog.show();
	}
	
	private void showTrafficChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setSingleChoiceItems(mTrafficList, mTrafficIndex,
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				mTrafficIndex = which;
				dialog.dismiss();
				updateTraffic();
			}
		}).setTitle("交通工具").create();
		dialog.show();
	}
	
	private void showTimePicker() {
		if (mWheelWindow == null) {
			mWheelWindow = new WheelWindow(getWindow().getDecorView(), mConfirmListener, new TimeWheel(this));
		}
		mWheelWindow.show();
	}
	
	private void updateClearState() {
		mClearTime.setVisibility(TextUtils.isEmpty(mTakeTime.getText()) ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWheelWindow != null && mWheelWindow.isShowing()) {
				mWheelWindow.dismiss();
				mWheelWindow = null;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 打开发货页面
	 * @param context
	 * @param bespeak
	 */
	public static void startSendActivity(Context context, boolean bespeak) {
		Intent intent = new Intent(context, SendActivity.class);
		intent.putExtra(KEY_BESPEAK, bespeak);
		context.startActivity(intent);
	}
}
