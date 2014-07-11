package com.wuliu.client.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.wuliu.client.window.CityWindow;
import com.wuliu.client.window.TimeWindow;
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
	
	private static final String KEY_IS_FROM = "is_from";
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
	
	private OnGetPoiSearchResultListener mPoiListener = new OnGetPoiSearchResultListener(){  
		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {
			
		}
		@Override
		public void onGetPoiResult(PoiResult result) {
			if (result != null) {
				mPoiList = result.getAllPoi();
			}
		}  
	};
	
	private TextWatcher mWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			search();
		}
	};
	
	private WheelWindow.OnConfirmListener mConfirmListener = new WheelWindow.OnConfirmListener() {

		@Override
		public void onConfirm(String result) {
			mWheelWindow = null;
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
	
	private SearchListAdapter mAdapter;
	
	private PoiSearch mPoiSearch;
	
	private List<PoiInfo> mPoiList;
	
	private boolean mIsFrom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		handleIntent();
		initView();
		initSearch();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPoiSearch.destroy();
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
		mIsFrom = getIntent().getBooleanExtra(KEY_IS_FROM, false);
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mSearchArea.setOnClickListener(mOnClickListener);
		mSearchClear.setOnClickListener(mOnClickListener);
		mSearchConfirm.setOnClickListener(mOnClickListener);
		mSearchInput.addTextChangedListener(mWatcher);
		mTitle.setText(R.string.title_search);
		mAdapter = new SearchListAdapter();
		mSearchList.setAdapter(mAdapter);
	}
	
	private void initSearch() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(mPoiListener);
	}
	
	private void clearInput() {
		mSearchInput.setText(null);
	}
	
	private void confirm() {
		Intent intent = new Intent();
		intent.putExtra(KEY_RESULT, mSearchInput.getText());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void showAreaPicker() {
		if (mWheelWindow == null) {
			mWheelWindow = new CityWindow(getWindow().getDecorView(), mConfirmListener);
		}
		mWheelWindow.show();
	}
	
	private void search() {
		String keyword = mSearchInput.getText().toString();
		if (TextUtils.isEmpty(keyword)) {
			
		}
		else {
			mPoiSearch.searchInCity((new PoiCitySearchOption())
					.city("±±¾©")  
					.keyword(mSearchInput.getText().toString())  
					.pageNum(10));
		}
	}
	
	public static void startSearchActivity(Activity activity, boolean isFrom, int requestCode) {
		Intent intent = new Intent(activity, SearchActivity.class);
		intent.putExtra(KEY_IS_FROM, isFrom);
		activity.startActivityForResult(intent, requestCode);
	}
	
	private class SearchListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mPoiList == null ? 0 : mPoiList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mPoiList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			if (convertView == null) {
				convertView = new TextView(SearchActivity.this);
			}
			((TextView)convertView).setText(mPoiList.get(position).address);
			return convertView;
		}
		
	}
}
