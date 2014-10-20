package cn.boweikeji.wuliu.supplyer.api;

import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;

import com.loopj.android.http.RequestParams;

public class BaseParams extends RequestParams {

	public static final String PARAM_DEFAULT = "-9";
	
	public BaseParams() {
		add("device_no", DeviceInfo.getIMEI());
	}
	
}
