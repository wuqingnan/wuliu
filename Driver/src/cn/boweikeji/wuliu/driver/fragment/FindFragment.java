package cn.boweikeji.wuliu.driver.fragment;

import m.framework.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.activity.CityListActivity;
import cn.boweikeji.wuliu.driver.activity.FindResultActivity;
import cn.boweikeji.wuliu.driver.bean.FindFilter;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.Util;

public class FindFragment extends BaseFragment {

	private static final String TAG = FindFragment.class.getSimpleName();
	
	private static final int REQUESTCODE_CHOOSE_START = 1 << 0;
	private static final int REQUESTCODE_CHOOSE_END = 1 << 1;

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mExchangeBtn) {
				exchange();
			} else if (view == mStart) {
				chooseCity(true);
			} else if (view == mEnd) {
				chooseCity(false);
			} else if (view == mTruckType) {
				truckType();
			} else if (view == mGoodsType) {
				goodsType();
			} else if (view == mFindBtn) {
				find();
			}
		}
	};

	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.btn_exchange)
	ImageButton mExchangeBtn;
	@InjectView(R.id.start)
	TextView mStart;
	@InjectView(R.id.end)
	TextView mEnd;
	@InjectView(R.id.truck_type)
	TextView mTruckType;
	@InjectView(R.id.goods_type)
	TextView mGoodsType;
	@InjectView(R.id.message_free)
	RadioGroup mMessageFree;
	@InjectView(R.id.btn_find)
	Button mFindBtn;

	private int mTruckTypeIndex;
	private String[] mTruckTypes;

	private int mGoodsTypeIndex;
	private String[] mGoodsTypes;
	
	private String mStartAddr;
	private String mEndAddr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_find, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_CHOOSE_START:
				mStartAddr = data.getStringExtra("city");
				updateAddress();
				break;
			case REQUESTCODE_CHOOSE_END:
				mEndAddr = data.getStringExtra("city");
				updateAddress();
				break;
			}
		}
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
		initData();
	}

	private void initTitle() {
		mTitle.setText(R.string.home_tab_find);
		mBack.setVisibility(View.GONE);
	}

	private void initView() {
		mStart.setOnClickListener(mOnClickListener);
		mEnd.setOnClickListener(mOnClickListener);
		mTruckType.setOnClickListener(mOnClickListener);
		mGoodsType.setOnClickListener(mOnClickListener);
		mExchangeBtn.setOnClickListener(mOnClickListener);
		mFindBtn.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		mTruckTypeIndex = Integer.parseInt(LoginManager.getInstance().getUserInfo().getTruck_type_code());
		mTruckTypes = getResources().getStringArray(R.array.truck_type_list);
		updateTruckType();

		mGoodsTypeIndex = 0;
		mGoodsTypes = getResources().getStringArray(R.array.goods_type_list);
		updateGoodsType();
		
		mMessageFree.check(R.id.message_free_all);
	}

	private void updateTruckType() {
		mTruckType.setText(mTruckTypes[mTruckTypeIndex]);
	}

	private void updateGoodsType() {
		mGoodsType.setText(mGoodsTypes[mGoodsTypeIndex]);
	}

	private void updateAddress() {
		mStart.setText(mStartAddr);
		mEnd.setText(mEndAddr);
	}
	
	private void exchange() {
		String addr = mStartAddr;
		mStartAddr = mEndAddr;
		mEndAddr = addr;
		updateAddress();
	}
	
	private void chooseCity(boolean start) {
		Intent intent = new Intent(getActivity(), CityListActivity.class);
		if (start) {
			startActivityForResult(intent, REQUESTCODE_CHOOSE_START);
		} else {
			startActivityForResult(intent, REQUESTCODE_CHOOSE_END);
		}
	}
	
	private void find() {
		if (validCheck()) {
			FindFilter filter = new FindFilter();
			filter.setStart_addr(mStartAddr);
			filter.setEnd_addr(mEndAddr);
			filter.setGoods_type_code(mGoodsTypeIndex);
			switch (mMessageFree.getCheckedRadioButtonId()) {
			case R.id.message_free_all:
				filter.setMess_fee(FindFilter.MESSAGE_FREE_ALL);
				break;
			case R.id.message_free_yes:
				filter.setMess_fee(FindFilter.MESSAGE_FREE_YES);
				break;
			case R.id.message_free_no:
				filter.setMess_fee(FindFilter.MESSAGE_FREE_NO);
				break;
			}
			filter.setTrunk_type_code(mTruckTypeIndex);
			FindResultActivity.startFindResultActivity(getActivity(), filter);
		}
	}
	
	private boolean validCheck() {
		if (TextUtils.isEmpty(mStartAddr) && TextUtils.isEmpty(mEndAddr)) {
			Util.showTips(getActivity(), getResources().getString(R.string.must_choose_one_addr));
			return false;
		} else if (mTruckTypeIndex == 0) {
			Util.showTips(getActivity(), getResources().getString(R.string.choose_truck_type));
			return false;
		}
		return true;
	}
	
	private void truckType() {
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
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

	private void goodsType() {
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setSingleChoiceItems(mGoodsTypes, mGoodsTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mGoodsTypeIndex = which;
								dialog.dismiss();
								updateGoodsType();
							}
						}).setTitle("货物类型").create();
		dialog.show();
	}
}
