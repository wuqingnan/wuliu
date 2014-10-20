package cn.boweikeji.wuliu.driver.api;

import cn.boweikeji.wuliu.driver.utils.DeviceInfo;

import com.loopj.android.http.RequestParams;

public class BaseParams extends RequestParams {

	public static final String PARAM_DEFAULT = "-9";
	
	public BaseParams() {
		add("device_no", DeviceInfo.getIMEI());
	}
	
}
