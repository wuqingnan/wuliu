package com.wuliu.client.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.wuliu.client.R;
import com.wuliu.client.WLApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SendActivity extends Activity {

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
			} else if (view == mCost) {
				sendDetail();
			}
		}
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
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
	@InjectView(R.id.cost)
	Button mCost;

	private String[] mTypeList;
	private String[] mTrafficList;
	private int mTypeIndex;
	private int mTrafficIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_send);
		initView();
		initData();
		initAddress();
	}

	private void initView() {
		ButterKnife.inject(this);
		mGoodsType.setOnClickListener(mOnClickListener);
		mGoodsTraffic.setOnClickListener(mOnClickListener);
		mSwapAddress.setOnClickListener(mOnClickListener);
		mCost.setOnClickListener(mOnClickListener);
		mTitle.setText(R.string.title_send);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
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

}
