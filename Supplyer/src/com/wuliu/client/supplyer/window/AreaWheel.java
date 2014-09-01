package com.wuliu.client.supplyer.window;

import java.util.List;

import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.bean.Area;
import com.wuliu.client.supplyer.dao.AreaDao;

public class AreaWheel implements IWheel<Area> {

	private Context mContext;
	private AreaDao mAreaDao;
	
	private List<Area> mLeftData;
	private List<Area> mMiddleData;
	private List<Area> mRightData;
	
	public AreaWheel(Context context, AreaDao dao) {
		mContext = context;
		mAreaDao = dao;
	}
	
	@Override
	public void initData() {
		mLeftData = mAreaDao.listByParentId(0);
	}

	@Override
	public List<Area> getLeftData() {
		return mLeftData;
	}

	@Override
	public List<Area> getMiddleData(int leftIndex) {
		mMiddleData = null;
		if (mLeftData != null && mLeftData.size() > leftIndex) {
			mMiddleData = mAreaDao.listByParentId(mLeftData.get(leftIndex).getId());
		}
		return mMiddleData;
	}

	@Override
	public List<Area> getRightData(int leftIndex, int middleIndex) {
		mRightData = null;
		if (mMiddleData != null && mMiddleData.size() > middleIndex) {
			mRightData = mAreaDao.listByParentId(mMiddleData.get(middleIndex).getId());
		}
		return mRightData;
	}

	@Override
	public int getLeftIndex(String key) {
		return getIndex(mLeftData, key);
	}

	@Override
	public int getMiddleIndex(String key) {
		return getIndex(mMiddleData, key);
	}

	@Override
	public int getRightIndex(String key) {
		return getIndex(mRightData, key);
	}
	
	private int getIndex(List<Area> data, String key) {
		if (data != null && key != null) {
			for (int i = 0; i < data.size(); i++) {
				if (key.equals(data.get(i).getName())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public WheelViewAdapter getLeftAdapter() {
		return new DataAdapter(mContext, getLeftData());
	}

	@Override
	public WheelViewAdapter getMiddleAdapter(int leftIndex) {
		return new DataAdapter(mContext, getMiddleData(leftIndex));
	}

	@Override
	public WheelViewAdapter getRightAdapter(int leftIndex, int middleIndex) {
		return new DataAdapter(mContext, getRightData(leftIndex, middleIndex));
	}

	@Override
	public String getResult(int left, int middle, int right) {
		StringBuffer sb = new StringBuffer();
		if (mLeftData != null && mLeftData.size() > left) {
			sb.append(mLeftData.get(left));
		}
		sb.append("###");
		if (mMiddleData != null && mMiddleData.size() > middle) {
			sb.append(mMiddleData.get(middle));
		}
		sb.append("###");
		if (mRightData != null && mRightData.size() > right) {
			sb.append(mRightData.get(right));
		}
		return sb.toString();
	}

	private class DataAdapter extends ListWheelAdapter<Area> {

		public DataAdapter(Context context, List<Area> items) {
			super(context, items);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.DEFAULT_BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			return super.getItem(index, cachedView, parent);
		}
	}
	
}
