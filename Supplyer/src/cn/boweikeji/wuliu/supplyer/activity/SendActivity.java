package cn.boweikeji.wuliu.supplyer.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import cn.boweikeji.wuliu.supplyer.bean.Order;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;

import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.view.SendAddressItem;
import cn.boweikeji.wuliu.view.SendArrowItem;
import cn.boweikeji.wuliu.view.SendInputItem;
import cn.boweikeji.wuliu.window.TimeWheel;
import cn.boweikeji.wuliu.window.WheelWindow;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
			} else if (view == mValidTime) {
				showValidTimeChooseDialog();
			} else if (view == mTruckType) {
				truckType();
			} else if (view == mNextStep) {
				sendDetail();
			} else if (view == mPickTime) {
				showTimePicker();
			} else if (view == mAddress.getSwapView()) {
				swapAddress();
			} else if (view == mAddress.getFromView()) {
				searchAddress(true);
			} else if (view == mAddress.getToView()) {
				searchAddress(false);
			}
		}
	};
	
	private WheelWindow.OnConfirmListener mConfirmListener = new WheelWindow.OnConfirmListener() {

		@Override
		public void onConfirm(String result) {
			mBespeakTime = Long.parseLong(result);
			SimpleDateFormat format = new SimpleDateFormat(getString(R.string.bespeak_time_format));
			mPickTime.setValue(format.format(new Date(mBespeakTime)));
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
	SendArrowItem mPickTime;
	@InjectView(R.id.goods_type)
	SendArrowItem mGoodsType;
	@InjectView(R.id.goods_name)
	SendInputItem mGoodsName;
	@InjectView(R.id.goods_weight)
	SendInputItem mGoodsWeight;
	@InjectView(R.id.goods_value)
	SendInputItem mGoodsValue;
	@InjectView(R.id.goods_pay)
	SendInputItem mGoodsPay;
	@InjectView(R.id.valid_time)
	SendArrowItem mValidTime;
	@InjectView(R.id.truck_type)
	SendArrowItem mTruckType;
	@InjectView(R.id.address)
	SendAddressItem mAddress;
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
					if (mIsFrom) {
						mFromInfos = infos;
					} else {
						mToInfos = infos;
					}
					updateAddress();
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
		mValidTime.setOnClickListener(mOnClickListener);
		mTruckType.setOnClickListener(mOnClickListener);
		mNextStep.setOnClickListener(mOnClickListener);
		mMenuBtn.setImageResource(R.drawable.ic_navi_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mAddress.setOnClickListener(mOnClickListener);
		if (mBespeak) {
			mPickTimeLayout.setVisibility(View.VISIBLE);
			mPickTime.setOnClickListener(mOnClickListener);
		}
		mTitle.setText(mBespeak ? R.string.title_bespeak : R.string.title_send);
		initLabel();
		initUnit();
		initInput();
	}
	
	private void initLabel() {
		mPickTime.setName(R.string.label_pick_time);
		mGoodsType.setName(R.string.label_goods_type);
		mGoodsName.setName(R.string.label_goods_name);
		mGoodsWeight.setName(R.string.label_goods_weight);
		mGoodsValue.setName(R.string.label_goods_value);
		mGoodsPay.setName(R.string.label_goods_pay);
		mValidTime.setName(R.string.label_goods_valid_time);
		mTruckType.setName(R.string.label_truck_type);
	}
	
	private void initUnit() {
		mGoodsWeight.setUnit(R.string.ton);
		mGoodsValue.setUnit(R.string.yuan);
		mGoodsPay.setUnit(R.string.yuan);
	}
	
	private void initInput() {
		mGoodsName.setMaxLength(16);
		mGoodsWeight.setMaxLength(4);
		mGoodsValue.setMaxLength(9);
		mGoodsPay.setMaxLength(9);
		mGoodsWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
		mGoodsValue.setInputType(InputType.TYPE_CLASS_NUMBER);
		mGoodsPay.setInputType(InputType.TYPE_CLASS_NUMBER);
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
	}
	
	private void initAddress() {
		LocationClient client = WLApplication.getLocationClient();
		if (client != null) {
			BDLocation location = client.getLastKnownLocation();
			if (location != null && location.hasAddr()) {
				mFromInfos[0] = mToInfos[0] = location.getProvince();
				mFromInfos[1] = mToInfos[1] = location.getCity();
				mFromInfos[2] = mToInfos[2] = location.getDistrict();
				mFromInfos[3] = mToInfos[3] = location.getStreet();
				updateAddress();
			}
		}
	}

	private void updateGoodsType() {
		mGoodsType.setValue(mGoodsTypes[mGoodsTypeIndex]);
	}

	private void updateValidTime() {
		mValidTime.setValue(mValidTimes[mValidTimeIndex]);
	}
	
	private void updateTruckType() {
		mTruckType.setValue(mTruckTypes[mTruckTypeIndex]);
	}
	
	private void sendDetail() {
		if (checkValid()) {
			Order order = new Order();
			order.setGoods_type_code(mGoodsTypeIndex);
			order.setGoods_name(mGoodsName.getValue());
			if (mBespeak) {
				order.setPick_time(mPickTime.getValue());
			}
			int weight = Integer.parseInt(mGoodsWeight.getValue());
			String value = mGoodsValue.getValue();
			String pay = mGoodsPay.getValue();
			order.setWeight(weight);
			order.setGoods_value(TextUtils.isEmpty(value) ? -9 : Integer.parseInt(value));
			order.setGoods_cost(TextUtils.isEmpty(pay) ? -9 : Integer.parseInt(pay));
			order.setValid_type(mValidTimeIndex);
			order.setTruck_type_code(mTruckTypeIndex);
			order.setEnd_addr(mAddress.getToAddress());
			order.setStart_addr(mAddress.getFromAddress());
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
		String[] infos = mFromInfos;
		mFromInfos = mToInfos;
		mToInfos = infos;
		updateAddress();
	}
	
	private void updateAddress() {
		mAddress.setFromAddress(arrayToString(mFromInfos));
		mAddress.setToAddress(arrayToString(mToInfos));
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
		String pickTime = mPickTime.getValue();
		String goodsName = mGoodsName.getValue();
		String goodsWeight = mGoodsWeight.getValue();
		String fromAddress = mAddress.getFromAddress();
		if (mBespeak && (TextUtils.isEmpty(pickTime))) {
			Util.showTips(this, getResources().getString(
					R.string.pick_time_empty));
			return false;
		} else if (TextUtils.isEmpty(goodsName)) {
			Util.showTips(this, getResources().getString(
					R.string.goods_name_empty));
			return false;
		} else if (TextUtils.isEmpty(goodsWeight)) {
			Util.showTips(this, getResources().getString(
					R.string.goods_weight_empty));
			return false;
		} else if (TextUtils.isEmpty(fromAddress)) {
			Util.showTips(this, getResources().getString(
					R.string.address_from_empty));
			return false;
		} else if (mValidTimeIndex == 0) {
			Util.showTips(this, getResources().getString(
					R.string.valid_time_error));
			return false;
		} else if (mGoodsTypeIndex == 0) {
			Util.showTips(this, getResources().getString(
					R.string.goods_type_error));
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
		}
		return true;
	}
	
	private String arrayToString(String[] array) {
		if (array == null) {
			return null;
		}
        int iMax = array.length - 1;
        if (iMax == -1) {
        	return null;
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
    		b.append(String.valueOf(array[i]));
        }
        return b.toString();
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
