package com.wuliu.client.api;

import com.loopj.android.http.RequestParams;
import com.wuliu.client.utils.DeviceInfo;

public class BaseParams extends RequestParams {

	public static final String PARAM_DEFAULT = "-9";
	
	public BaseParams() {
		add("device_no", DeviceInfo.getIMEI());
	}
	
}
