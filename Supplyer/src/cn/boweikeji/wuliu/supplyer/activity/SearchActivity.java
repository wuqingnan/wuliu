package cn.boweikeji.wuliu.supplyer.activity;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import cn.boweikeji.wuliu.supplyer.bean.Area;
import cn.boweikeji.wuliu.supplyer.db.DBHelper;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.window.AreaWheel;
import cn.boweikeji.wuliu.window.IWheel;
import cn.boweikeji.wuliu.window.WheelWindow;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends BaseActivity {

	private static final String TAG = SearchActivity.class.getSimpleName();
	
	private static final String KEY_SEARCH_INFOS = "search_infos";
	
	public static final String KEY_RESULT = "result";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mSearchArea) {
				showAreaPicker();
			} else if (view == mSearchClear) {
				clearInput();
			} else if (view == mSearchConfirm) {
				confirm();
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
			System.arraycopy(str, 0, mSearchInfos, 0, 3);
			initData();
		}
		
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.search_area)
	TextView mSearchArea;
	@InjectView(R.id.search_input)
	EditText mSearchInput;
	@InjectView(R.id.search_clear)
	ImageView mSearchClear;
	@InjectView(R.id.search_confirm)
	Button mSearchConfirm;
	@InjectView(R.id.search_list)
	ListView mSearchList;
	
	private WheelWindow mWheelWindow;
	
	//0:省、1:市、2:区县、3:街道
	private String[] mSearchInfos;
	
	private boolean mIsFrom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		handleIntent();
		initView();
		initData();
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
	
	private void handleIntent() {
		Intent intent = getIntent();
		mSearchInfos = intent.getStringArrayExtra(KEY_SEARCH_INFOS);
		if (mSearchInfos == null) {
			mSearchInfos = new String[4];
		}
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mMenuBtn.setImageResource(R.drawable.ic_navi_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mSearchArea.setOnClickListener(mOnClickListener);
		mSearchClear.setOnClickListener(mOnClickListener);
		mSearchConfirm.setOnClickListener(mOnClickListener);
		mTitle.setText(R.string.title_search);
	}
	
	private void initData() {
		if (mSearchInfos != null) {
			if (!TextUtils.isEmpty(mSearchInfos[2]) && !mSearchInfos[2].equals("请选择")) {
				mSearchArea.setText(mSearchInfos[2]);
			} else {
				mSearchArea.setText(mSearchInfos[1]);
			}
			mSearchInput.setText(mSearchInfos[3]);
		}
		updateClearState();
	}
	
	private void clearInput() {
		mSearchInput.setText(null);
	}
	
	private void confirm() {
		Intent intent = new Intent();
		mSearchInfos[3] = mSearchInput.getText().toString();
		if (mSearchInfos[2].equals("请选择")) {
			mSearchInfos[2] = "";
		}
		intent.putExtra(KEY_RESULT, mSearchInfos);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void showAreaPicker() {
		if (mWheelWindow == null) {
			IWheel<Area> wheel = null;
			try {
				DBHelper helper = ((WLApplication)getApplication()).getHelper();
				wheel = new AreaWheel(this, helper.getAreaDao());
				mWheelWindow = new WheelWindow(getWindow().getDecorView(), mConfirmListener, wheel);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		mWheelWindow.show();
		mWheelWindow.updateByInfo(mSearchInfos);
	}
	
	private void updateClearState() {
		mSearchClear.setVisibility(TextUtils.isEmpty(mSearchInput.getText()) ? View.GONE : View.VISIBLE);
	}
	
	public static void startSearchActivity(Activity activity, int requestCode, String[] infos) {
		Intent intent = new Intent(activity, SearchActivity.class);
		intent.putExtra(KEY_SEARCH_INFOS, infos);
		activity.startActivityForResult(intent, requestCode);
	}
	
}
