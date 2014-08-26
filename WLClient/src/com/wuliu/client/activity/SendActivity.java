package com.wuliu.client.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.wuliu.client.R;
import com.wuliu.client.WLApplication;
import com.wuliu.client.bean.Order;
import com.wuliu.client.utils.Util;
import com.wuliu.client.window.TimeWheel;
import com.wuliu.client.window.WheelWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SendActivity extends Activity {

	private static final String TAG = SendActivity.class.getSimpleName();
	
	private static final String KEY_ORDER = "order";
	private static final String KEY_BESPEAK = "bespeak";
	
	private static final int REQUEST_CODE_SEARCH = 0x010;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mGoodsType) {
				showTypeChooseDialog();
			} else if (view == mGoodsValidTime) {
				showValidTimeChooseDialog();
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
	@InjectView(R.id.goods_weight_unit)
	RadioGroup mGoodsWeightUnit;
	@InjectView(R.id.goods_value)
	EditText mGoodsValue;
	@InjectView(R.id.goods_pay)
	EditText mGoodsPay;
	@InjectView(R.id.goods_valid_time)
	TextView mGoodsValidTime;
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
	private String[] mValidTimeList;
	private int mTypeIndex;
	private int mTrafficIndex;
	private int mValidTimeIndex;
	private long mBespeakTime;
	private boolean mBespeak;
	private boolean mIsFrom;
	private Order mOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_send);
		handleIntent();
		initView();
		initData();
		initAddress();
		changeOrder();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SEARCH) {
				if (data != null) {
					String[] infos = data.getStringArrayExtra(SearchActivity.KEY_RESULT);
					String address = "";
					if (infos != null) {
						for (int i = 0; i < infos.length; i++) {
							if (infos[i] != null) {
								address += infos[i] + " ";
							}
						}
					}
					if (mIsFrom) {
						mFromInfos = infos;
						mAddressFrom.setText(address);
					} else {
						mToInfos = infos;
						mAddressTo.setText(address);
					}
				}
			}
		}
	}
	
	private void handleIntent() {
		mOrder = (Order) getIntent().getSerializableExtra(KEY_ORDER);
		mBespeak = getIntent().getBooleanExtra(KEY_BESPEAK, false);
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mGoodsType.setOnClickListener(mOnClickListener);
		mGoodsValidTime.setOnClickListener(mOnClickListener);
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
		mValidTimeIndex = 0;
		
		mTypeList = getResources().getStringArray(R.array.goods_type_list);
		mTrafficList = getResources()
				.getStringArray(R.array.goods_traffic_list);
		mValidTimeList = getResources()
				.getStringArray(R.array.goods_valid_time_list);
		updateType();
		updateTraffic();
		updateValidTime();
		mGoodsWeightUnit.check(R.id.goods_weight_unit_kg);
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

	private void updateValidTime() {
		mGoodsValidTime.setText(mValidTimeList[mValidTimeIndex]);
	}
	
	private void updateTraffic() {
		mGoodsTraffic.setText(mTrafficList[mTrafficIndex]);
	}
	
	private void changeOrder() {
		if (mOrder != null) {//如果是通过修改订单的方式进入页面
			mTitle.setText(R.string.title_change_order);
			mGoodsName.setEnabled(false);
			mGoodsWeight.setEnabled(false);
			mGoodsValue.setEnabled(false);
			for (int i = 0; i < mGoodsWeightUnit.getChildCount(); i++) {
				mGoodsWeightUnit.getChildAt(i).setEnabled(false);
			}
			mGoodsType.setOnClickListener(null);
			mGoodsValidTime.setOnClickListener(null);
			mGoodsTraffic.setOnClickListener(null);
			
			mGoodsName.setText(mOrder.getGoodsName());
			mGoodsValue.setText(mOrder.getGoodsValue() + "");
			mTypeIndex = mOrder.getGoodsType();
			mTrafficIndex = mOrder.getTrunkType();
			mValidTimeIndex = mOrder.getValidTime();
			mGoodsPay.setText(mOrder.getPay() + "");
			mAddressFrom.setText(mOrder.getFromAddress());
			mAddressTo.setText(mOrder.getToAddress());
			int weight = mOrder.getWeight();
			if (weight < 1000) {
				mGoodsWeightUnit.check(R.id.goods_weight_unit_kg);
				mGoodsWeight.setText(weight + "");
			} else {
				mGoodsWeightUnit.check(R.id.goods_weight_unit_tons);
				mGoodsWeight.setText(String.format("%.2f", weight / 1000f));
			}
			updateType();
			updateTraffic();
			updateValidTime();
			mFromInfos = null;
			mToInfos = null;
		}
	}
	
	private void sendDetail() {
		if (checkValid()) {
			Order order = new Order();
			order.setGoodsType(mTypeIndex);
			order.setGoodsName(mGoodsName.getText().toString());
			if (mBespeak) {
				order.setBespeakTime(mTakeTime.getText().toString());
			}
			boolean tons = mGoodsWeightUnit.getCheckedRadioButtonId() == R.id.goods_weight_unit_tons;
			float weight = Float.parseFloat(mGoodsWeight.getText().toString());
			order.setWeight((int)(weight * (tons ? 1000 : 1)));
			order.setGoodsValue(Integer.parseInt(mGoodsValue.getText().toString()));
			order.setPay(Integer.parseInt(mGoodsPay.getText().toString()));
			order.setValidTime(mValidTimeIndex);
			order.setTrunkType(mTrafficIndex);
			order.setToAddress(mAddressTo.getText().toString());
			order.setFromAddress(mAddressFrom.getText().toString());
			LocationClient client = WLApplication.getLocationClient();
			if (client != null) {
				BDLocation location = client.getLastKnownLocation();
				order.setLat(location.getLatitude());
				order.setLon(location.getLongitude());
			}
			if (mOrder != null) {
				order.setGoodsCD(mOrder.getGoodsCD());
				order.setFromName(mOrder.getFromName());
				order.setFromPhone(mOrder.getFromPhone());
				order.setToName(mOrder.getToName());
				order.setToPhone(mOrder.getToPhone());
				order.setFree(mOrder.getFree());
				order.setRemarks(mOrder.getRemarks());
			}
			SendDetailActivity.startSendDetailActivity(this, order);
		}
	}
	
	private void searchAddress(boolean isFrom) {
		mIsFrom = isFrom;
		SearchActivity.startSearchActivity(SendActivity.this, REQUEST_CODE_SEARCH, isFrom ? mFromInfos : mToInfos);
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
	
	private void showValidTimeChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setSingleChoiceItems(mValidTimeList, mValidTimeIndex,
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				mValidTimeIndex = which;
				dialog.dismiss();
				updateValidTime();
			}
		}).setTitle("有效时间").create();
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

	private boolean checkValid() {
		String takeTime = mTakeTime.getText().toString();
		String goodsName = mGoodsName.getText().toString();
		String goodsWeight = mGoodsWeight.getText().toString();
		String goodsValue = mGoodsValue.getText().toString();
		String goodsPay = mGoodsPay.getText().toString();
		String addressFrom = mAddressFrom.getText().toString();
		String addressTo = mAddressTo.getText().toString();
		if (mBespeak && (takeTime == null || takeTime.equals(""))) {
			Util.showTips(this, getResources().getString(
					R.string.take_time_empty));
			return false;
		} else if (goodsName == null || goodsName.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.goods_name_empty));
			return false;
		} else if (goodsWeight == null || goodsWeight.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.goods_weight_empty));
			return false;
		} else if (goodsValue == null || goodsValue.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.goods_value_empty));
			return false;
		} else if (goodsPay == null || goodsPay.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.goods_pay_empty));
			return false;
		} else if (addressFrom == null || addressFrom.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.address_from_empty));
			return false;
		} else if (addressTo == null || addressTo.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.address_to_empty));
			return false;
		}
		
		if (!(Util.isInteger(goodsWeight) || Util.isDecimal(goodsWeight))) {
			Util.showTips(this, getResources().getString(
					R.string.goods_weight_error));
			return false;
		} else if (!Util.isInteger(goodsValue)) {
			Util.showTips(this, getResources().getString(
					R.string.goods_value_error));
			return false;
		} else if (!Util.isInteger(goodsPay)) {
			Util.showTips(this, getResources().getString(
					R.string.goods_value_error));
			return false;
		}
		
		return true;
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
	
	/**
	 * 打开发货页面
	 * @param context
	 * @param bespeak
	 */
	public static void startSendActivity(Context context, Order order) {
		Intent intent = new Intent(context, SendActivity.class);
		intent.putExtra(KEY_ORDER, order);
		context.startActivity(intent);
	}
}
