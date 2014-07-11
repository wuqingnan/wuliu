package com.wuliu.client.window;

import android.view.View;

public class CityWindow extends WheelWindow {

	public CityWindow(View anchor, OnConfirmListener listener) {
		super(anchor, listener);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected String[] getLeftData() {
		return null;
	}

	@Override
	protected String[] getMiddleData(int leftIndex) {
		return null;
	}

	@Override
	protected String[] getRightData(int leftIndex, int middleIndex) {
		return null;
	}

	@Override
	protected String getResult(int left, int middle, int right) {
		return null;
	}

}
