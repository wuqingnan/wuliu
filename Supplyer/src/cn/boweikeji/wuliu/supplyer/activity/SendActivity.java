package cn.boweikeji.wuliu.supplyer.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import cn.boweikeji.wuliu.supplyer.bean.Order;
import cn.boweikeji.wuliu.supplyer.utils.Util;
import cn.boweikeji.wuliu.supplyer.window.TimeWheel;
import cn.boweikeji.wuliu.supplyer.window.WheelWindow;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import cn.boweikeji.wuliu.supplyer.R;

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

public class SendActivity extends BaseActivity {

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
			} else if (view == mGoodsValidTime) {
				showValidTimeChooseDialog();
			} else if (view == mTruckType) {
				truckType();
			} else if (view == mSwapAddress) {
				swapAddress();
			} else if (view == mNextStep) {
				sendDetail();
			} else if (view == mClearTime) {
				mPickTime.setText(null);
				updateClearState();
			} else if (view == mPickTime) {
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
			mPickTime.setText(format.format(new Date(mBespeakTime)));
			updateClearState();
			mWheelWindow = null;
		}
		
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.pick_time_layout)
	LinearLayout mPickTimeLayout;
	@InjectView(R.id.pick_time)
	TextView mPickTime;
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
	@InjectView(R.id.truck_type)
	TextView mTruckType;
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
	
	private String[] mGoodsTypes;
	private String[] mTruckTypes;
	private String[] mValidTimes;
	private int mGoodsTypeIndex;
	private int mTruckTypeIndex;
	private int mValidTimeIndex;
	private long mBespeakTime;
	private boolean mBespeak;
	private boolean mIsFrom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		handleIntent();
		initView();
		initData();
		initAddress();
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
		mBespeak = getIntent().getBooleanExtra(KEY_BESPEAK, false);
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mGoodsType.setOnClickListener(mOnClickListener);
		mGoodsValidTime.setOnClickListener(mOnClickListener);
		mTruckType.setOnClickListener(mOnClickListener);
		mSwapAddress.setOnClickListener(mOnClickListener);
		mNextStep.setOnClickListener(mOnClickListener);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mAddressFrom.setOnClickListener(mOnClickListener);
		mAddressTo.setOnClickListener(mOnClickListener);
		if (mBespeak) {
			mPickTimeLayout.setVisibility(View.VISIBLE);
			mPickTime.setOnClickListener(mOnClickListener);
			mClearTime.setOnClickListener(mOnClickListener);
		}
		mTitle.setText(mBespeak ? R.string.title_bespeak : R.string.title_send);
	}

	private void initData() {
		mGoodsTypeIndex = 0;
		mTruckTypeIndex = 0;
		mValidTimeIndex = 0;
		
		mGoodsTypes = getResources().getStringArray(R.array.goods_type_list);
		mTruckTypes = getResources()
				.getStringArray(R.array.truck_type_list);
		mValidTimes = getResources()
				.getStringArray(R.array.goods_valid_time_list);
		updateGoodsType();
		updateTruckType();
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

	private void updateGoodsType() {
		mGoodsType.setText(mGoodsTypes[mGoodsTypeIndex]);
	}

	private void updateValidTime() {
		mGoodsValidTime.setText(mValidTimes[mValidTimeIndex]);
	}
	
	private void updateTruckType() {
		mTruckType.setText(mTruckTypes[mTruckTypeIndex]);
	}
	
	private void sendDetail() {
		if (checkValid()) {
			Order order = new Order();
			order.setGoods_type_code(mGoodsTypeIndex);
			order.setGoods_name(mGoodsName.getText().toString());
			if (mBespeak) {
				order.setPick_time(mPickTime.getText().toString());
			}
			boolean tons = mGoodsWeightUnit.getCheckedRadioButtonId() == R.id.goods_weight_unit_tons;
			float weight = Float.parseFloat(mGoodsWeight.getText().toString());
			order.setWeight((int)(weight * (tons ? 1000 : 1)));
			order.setGoods_value(Integer.parseInt(mGoodsValue.getText().toString()));
			order.setGoods_cost(Integer.parseInt(mGoodsPay.getText().toString()));
			order.setValid_type(mValidTimeIndex);
			order.setTruck_type_code(mTruckTypeIndex);
			order.setEnd_addr(mAddressTo.getText().toString());
			order.setStart_addr(mAddressFrom.getText().toString());
			LocationClient client = WLApplication.getLocationClient();
			if (client != null) {
				BDLocation location = client.getLastKnownLocation();
				order.setGps_addr_w(location.getLatitude());
				order.setGps_addr_j(location.getLongitude());
			}
			SendDetailActivity.startSendDetailActivity(this, order);
		}
	}
	
	private void searchAddress(boolean isFrom) {
		mIsFrom = isFrom;
		SearchActivity.startSearchActivity(this, REQUEST_CODE_SEARCH, isFrom ? mFromInfos : mToInfos);
	}

	private void swapAddress() {
		CharSequence temp = mAddressFrom.getText();
		mAddressFrom.setText(mAddressTo.getText());
		mAddressTo.setText(temp);
	}
	
	private void showTypeChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(mGoodsTypes, mGoodsTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mGoodsTypeIndex = which;
								dialog.dismiss();
								updateGoodsType();
							}
						}).setTitle("物品类型").create();
		dialog.show();
	}
	
	private void showValidTimeChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setSingleChoiceItems(mValidTimes, mValidTimeIndex,
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
	
	private void showTimePicker() {
		if (mWheelWindow == null) {
			mWheelWindow = new WheelWindow(getWindow().getDecorView(), mConfirmListener, new TimeWheel(this));
		}
		mWheelWindow.show();
	}
	
	private void updateClearState() {
		mClearTime.setVisibility(TextUtils.isEmpty(mPickTime.getText()) ? View.GONE : View.VISIBLE);
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
		String pickTime = mPickTime.getText().toString();
		String goodsName = mGoodsName.getText().toString();
		String goodsWeight = mGoodsWeight.getText().toString();
		String goodsValue = mGoodsValue.getText().toString();
		String goodsPay = mGoodsPay.getText().toString();
		String addressFrom = mAddressFrom.getText().toString();
		String addressTo = mAddressTo.getText().toString();
		if (mBespeak && (pickTime == null || pickTime.equals(""))) {
			Util.showTips(this, getResources().getString(
					R.string.pick_time_empty));
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
		} else if (mTruckTypeIndex == 0) {
			Util.showTips(this, getResources().getString(
					R.string.trunk_type_error));
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
	
}
