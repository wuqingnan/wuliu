package com.wuliu.client.activity;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.wuliu.client.R;
import com.wuliu.client.WLApplication;
import com.wuliu.client.bean.Area;
import com.wuliu.client.db.DBHelper;
import com.wuliu.client.window.AreaWheel;
import com.wuliu.client.window.IWheel;
import com.wuliu.client.window.WheelWindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity {

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
	
//	private OnGetPoiSearchResultListener mPoiListener = new OnGetPoiSearchResultListener(){  
//		@Override
//		public void onGetPoiDetailResult(PoiDetailResult arg0) {
//			
//		}
//		@Override
//		public void onGetPoiResult(PoiResult result) {
//			if (result != null) {
//				mPoiList = result.getAllPoi();
//				mAdapter.notifyDataSetChanged();
//			}
//		}  
//	};
	
//	private TextWatcher mWatcher = new TextWatcher() {
//		@Override
//		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//			
//		}
//		
//		@Override
//		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//				int arg3) {
//			
//		}
//		
//		@Override
//		public void afterTextChanged(Editable arg0) {
//			updateClearState();
//			search();
//		}
//	};
	
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
	
//	private SearchListAdapter mAdapter;
	
//	private PoiSearch mPoiSearch;
	
//	private List<PoiInfo> mPoiList;
	
	//0:省、1:市、2:区县、3:街道
	private String[] mSearchInfos;
	
	private boolean mIsFrom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		handleIntent();
		initView();
//		initSearch();
		initData();
	}
	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		mPoiSearch.destroy();
//	}

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
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mSearchArea.setOnClickListener(mOnClickListener);
		mSearchClear.setOnClickListener(mOnClickListener);
		mSearchConfirm.setOnClickListener(mOnClickListener);
//		mSearchInput.addTextChangedListener(mWatcher);
		mTitle.setText(R.string.title_search);
//		mAdapter = new SearchListAdapter();
//		mSearchList.setAdapter(mAdapter);
	}
	
//	private void initSearch() {
//		mPoiSearch = PoiSearch.newInstance();
//		mPoiSearch.setOnGetPoiSearchResultListener(mPoiListener);
//	}
	
	private void initData() {
		if (mSearchInfos != null) {
			mSearchArea.setText(mSearchInfos[2]);
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
	
//	private void search() {
//		String keyword = mSearchInput.getText().toString();
//		if (TextUtils.isEmpty(keyword)) {
//			
//		}
//		else {
//			Log.d(TAG, "shizy---keyword: " + keyword);
//			mPoiSearch.searchInCity((new PoiCitySearchOption())
//					.city("北京")  
//					.keyword(keyword)  
//					.pageNum(0));
//		}
//	}
	
	private void updateClearState() {
		mSearchClear.setVisibility(TextUtils.isEmpty(mSearchInput.getText()) ? View.GONE : View.VISIBLE);
	}
	
	public static void startSearchActivity(Activity activity, int requestCode, String[] infos) {
		Intent intent = new Intent(activity, SearchActivity.class);
		intent.putExtra(KEY_SEARCH_INFOS, infos);
		activity.startActivityForResult(intent, requestCode);
	}
	
//	private class SearchListAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			return mPoiList == null ? 0 : mPoiList.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return mPoiList.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup viewGroup) {
//			if (convertView == null) {
//				convertView = new TextView(SearchActivity.this);
//			}
//			((TextView)convertView).setText(mPoiList.get(position).address);
//			return convertView;
//		}
//		
//	}
}
