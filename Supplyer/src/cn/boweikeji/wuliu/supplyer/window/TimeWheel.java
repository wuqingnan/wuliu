package cn.boweikeji.wuliu.supplyer.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.boweikeji.wuliu.supplyer.R;

import kankan.wheel.widget.adapters.WheelViewAdapter;

public class TimeWheel implements IWheel<String> {

	private Context mContext;
	
	private Calendar mCalendar;
	private List<String> mDays;
	private List<String> mAllHours;
	private List<String> mHoursForToday;
	private List<String> mAllMinutes;
	private List<String> mMinutesForNow;
	
	private boolean mToday;
	private boolean mNextHour;
	
	public TimeWheel(Context context) {
		mContext = context;
	}
	
	@Override
	public void initData() {
		mCalendar = Calendar.getInstance();
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		mDays = new ArrayList<String>();
		mDays.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.time_days)));
		if (hour >= 22 && ("" + hour + minute).compareTo("2250") > 0) {
			mToday = false;
			//删除今天
			mDays.remove(0);
		}
		else {
			mToday = true;
		}
		
		mNextHour = minute <= 50;
		
		mAllHours = new ArrayList<String>();
		mHoursForToday = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			String hourStr = (i < 10 ? "0" + i : i) + "点";
			mAllHours.add(hourStr);
			if (i >= hour + (mNextHour ? 1 : 2)) {
				mHoursForToday.add(hourStr);
			}
		}
		
		mAllMinutes = new ArrayList<String>();
		mMinutesForNow = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			String minuteStr = (i == 0 ? "00" : i * 10) + "分";
			mAllMinutes.add(minuteStr);
			if (i >= minute / 10 + (minute % 10 == 0 ? 0 : 1)) {
				mMinutesForNow.add(minuteStr);
			}
		}
	}

	@Override
	public List<String> getLeftData() {
		return mDays;
	}

	@Override
	public List<String> getMiddleData(int leftIndex) {
		if (mToday && leftIndex == 0) {
			return mHoursForToday;
		}
		return mAllHours;
	}

	@Override
	public List<String> getRightData(int leftIndex, int middleIndex) {
		if (mToday && leftIndex == 0 && mNextHour && middleIndex == 0) {
			return mMinutesForNow;
		}
		return mAllMinutes;
	}

	@Override
	public int getLeftIndex(String key) {
		return 0;
	}

	@Override
	public int getMiddleIndex(String key) {
		return 0;
	}

	@Override
	public int getRightIndex(String key) {
		return 0;
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
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mCalendar.getTimeInMillis());
		cal.add(Calendar.DATE, left + (mToday ? 0 : 1));
		
		List<String> hours = getMiddleData(left);
		cal.set(Calendar.HOUR_OF_DAY, middle + (hours.size() == 24 ? 0 : 24 - hours.size()));
		
		List<String> minutes = getRightData(left, middle);
		cal.set(Calendar.MINUTE, (right + (hours.size() == 6 ? 0 : 6 - minutes.size())) * 10);
		return "" + cal.getTimeInMillis();
	}

	private class DataAdapter extends ListWheelAdapter<String> {

		public DataAdapter(Context context, List<String> items) {
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
