package com.wuliu.client.window;

import java.util.Calendar;

import com.wuliu.client.R;

import android.util.Log;
import android.view.View;

public class TimeWindow extends WheelWindow {

	private Calendar mCalendar;
	private String[] mDays;
	private String[] mAllHours;
	private String[] mHoursForToday;
	private String[] mAllMinutes;
	private String[] mMinutesForNow;
	
	private boolean mToday;
	private boolean mNextHour;
	
	public TimeWindow(View anchor, OnConfirmListener listener) {
		super(anchor, listener);
	}

	@Override
	protected void initData() {
		mCalendar = Calendar.getInstance();
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		String[] days = getContext().getResources().getStringArray(R.array.time_days);
		if (hour >= 22 && ("" + hour + minute).compareTo("2250") > 0) {
			mToday = false;
			mDays = new String[2];
			System.arraycopy(days, 1, mDays, 0, mDays.length);
		}
		else {
			mToday = true;
			mDays = days;
		}
		
		mNextHour = minute <= 50;
		
		mAllHours = new String[24];
		for (int i = 0; i < 24; i++) {
			mAllHours[i] = (i < 10 ? "0" + i : i) + "µã";
		}
		mHoursForToday = new String[24 - hour - (mNextHour ? 1 : 2)];
		if (mHoursForToday.length > 0) {
			System.arraycopy(mAllHours, 24 - mHoursForToday.length, mHoursForToday, 0, mHoursForToday.length);
		}
		
		mAllMinutes = new String[6];
		for (int i = 0; i < 6; i++) {
			mAllMinutes[i] = (i == 0 ? "00" : i * 10) + "·Ö";
		}
		
		boolean flag = minute % 10 == 0;
		mMinutesForNow = new String[6 - minute / 10 - (minute % 10 == 0 ? 0 : 1)];
		if (mMinutesForNow.length > 0) {
			System.arraycopy(mAllMinutes, 6 - mMinutesForNow.length, mMinutesForNow, 0, mMinutesForNow.length);
		}
	}

	@Override
	protected String[] getLeftData() {
		return mDays;
	}

	@Override
	protected String[] getMiddleData(int leftIndex) {
		if (mToday && leftIndex == 0) {
			return mHoursForToday;
		}
		return mAllHours;
	}

	@Override
	protected String[] getRightData(int leftIndex, int middleIndex) {
		if (mToday && leftIndex == 0 && mNextHour && middleIndex == 0) {
			return mMinutesForNow;
		}
		return mAllMinutes;
	}

	@Override
	protected String getResult(int left, int middle, int right) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mCalendar.getTimeInMillis());
		cal.add(Calendar.DATE, left + (mToday ? 0 : 1));
		String[] hours = getMiddleData(left);
		cal.set(Calendar.HOUR, middle + (hours.length == 24 ? 0 : 24 - hours.length));

		String[] minutes = getRightData(left, middle);
		cal.set(Calendar.MINUTE, (right + (hours.length == 6 ? 0 : 6 - minutes.length)) * 10);
		return "" + cal.getTimeInMillis();
	}

}
