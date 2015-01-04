package cn.boweikeji.wuliu.driver.fragment;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.activity.FindResultActivity;
import cn.boweikeji.wuliu.driver.bean.Area;
import cn.boweikeji.wuliu.driver.bean.FindFilter;
import cn.boweikeji.wuliu.driver.db.DBHelper;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.window.AreaWheel;
import cn.boweikeji.wuliu.window.IWheel;
import cn.boweikeji.wuliu.window.WheelWindow;

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
				showAreaPicker(true);
			} else if (view == mEnd) {
				showAreaPicker(false);
			} else if (view == mTruckType) {
				truckType();
			} else if (view == mGoodsType) {
				goodsType();
			} else if (view == mFindBtn) {
				find();
			}
		}
	};

	private WheelWindow.OnConfirmListener mConfirmListener = new WheelWindow.OnConfirmListener() {

		@Override
		public void onConfirm(String result) {
			Log.d(TAG, "shizy---result: " + result);
			mWheelWindow = null;
			String[] str = result.split("###");
			if (str == null || str.length < 3) {
				return;
			}
			if (mIsStart) {
				mStartInfos = str;
			} else {
				mEndInfos = str;
			}
			updateAddress();
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
	
	private WheelWindow mWheelWindow;

	private int mTruckTypeIndex;
	private String[] mTruckTypes;

	private int mGoodsTypeIndex;
	private String[] mGoodsTypes;
	
	private String[] mStartInfos;
	private String[] mEndInfos;
	private boolean mIsStart;
	
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
		mTruckTypeIndex = 0;
		mTruckTypes = getResources().getStringArray(R.array.truck_type_list);
		mTruckTypes[0] = "全部";
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
		mStart.setText(mStartInfos == null ? null : arrayToString(mStartInfos));
		mEnd.setText(mEndInfos == null ? null : arrayToString(mEndInfos));
	}
	
	private void exchange() {
		String[] temp = mStartInfos;
		mStartInfos = mEndInfos;
		mEndInfos = temp;
		updateAddress();
	}
	
	private void find() {
		if (validCheck()) {
			FindFilter filter = new FindFilter();
			filter.setStart_addr(mStart.getText().toString());
			filter.setEnd_addr(mEnd.getText().toString());
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
		if (mStartInfos == null && mEndInfos == null) {
			Util.showTips(getActivity(), getResources().getString(R.string.must_have_one_address));
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
	
	private void showAreaPicker(boolean start) {
		mIsStart = start;
		if (mWheelWindow == null) {
			IWheel<Area> wheel = null;
			try {
				DBHelper helper = ((WLApplication)getActivity().getApplication()).getHelper();
				wheel = new AreaWheel(getActivity(), helper.getAreaDao());
				mWheelWindow = new WheelWindow(getActivity().getWindow().getDecorView(), mConfirmListener, wheel);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		mWheelWindow.show();
		mWheelWindow.updateByInfo(start ? mStartInfos : mEndInfos);
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
        	if (!array[i].equals("请选择")) {
        		b.append(String.valueOf(array[i]));
        	}
        }
        return b.toString();
	}
}
