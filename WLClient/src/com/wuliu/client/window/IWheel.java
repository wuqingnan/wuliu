package com.wuliu.client.window;

import java.util.List;

import kankan.wheel.widget.adapters.WheelViewAdapter;

public interface IWheel<T> {

	public void initData();

	public List<T> getLeftData();

	public List<T> getMiddleData(int leftIndex);

	public List<T> getRightData(int leftIndex, int middleIndex);
	
	public int getLeftIndex(String key);
	
	public int getMiddleIndex(String key);

	public int getRightIndex(String key);
	
	public WheelViewAdapter getLeftAdapter();
	
	public WheelViewAdapter getMiddleAdapter(int leftIndex);
	
	public WheelViewAdapter getRightAdapter(int leftIndex, int middleIndex);
	
	public String getResult(int left, int middle, int right);

	
}
