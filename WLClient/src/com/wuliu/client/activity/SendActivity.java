package com.wuliu.client.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SendActivity extends Activity {

	private static final String TAG = SendActivity.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mGoodsType) {
				showTypeChooseDialog();
			} else if (view == mGoodsTraffic) {
				showTrafficChooseDialog();
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
	}

	private void initView() {
		ButterKnife.inject(this);
		mGoodsType.setOnClickListener(mOnClickListener);
		mGoodsTraffic.setOnClickListener(mOnClickListener);
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

	private void updateType() {
		mGoodsType.setText(mTypeList[mTypeIndex]);
	}

	private void updateTraffic() {
		mGoodsTraffic.setText(mTrafficList[mTrafficIndex]);
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
