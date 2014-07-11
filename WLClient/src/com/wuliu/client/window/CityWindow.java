package com.wuliu.client.window;

import com.wuliu.client.city.CityManager;

import android.view.View;

public class CityWindow extends WheelWindow {

	private String[] mLeft;
	private String[] mMiddle;
	private String[] mRight;
	
	public CityWindow(View anchor, OnConfirmListener listener) {
		super(anchor, listener);
	}

	@Override
	protected void initData() {
		CityManager.getInstance().getFirstLevel();
	}

	@Override
	protected String[] getLeftData() {
		mLeft = CityManager.getInstance().getFirstLevel();
		return mLeft;
	}

	@Override
	protected String[] getMiddleData(int leftIndex) {
		mMiddle = CityManager.getInstance().getSecondLevel(mLeft[leftIndex]);
		return mMiddle;
	}

	@Override
	protected String[] getRightData(int leftIndex, int middleIndex) {
		mRight = CityManager.getInstance().getThirdLevel(mLeft[leftIndex], mMiddle[middleIndex]);
		return mRight;
	}

	@Override
	protected String getResult(int left, int middle, int right) {
		return mLeft[left] + "|" + mMiddle[middle] + "|" + (mRight.length > 0 ? mRight[right] : "");
	}

}
