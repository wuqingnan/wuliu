package cn.boweikeji.wuliu.driver.bean;

import java.io.Serializable;

import cn.boweikeji.wuliu.driver.api.BaseParams;

public class RegisterInfo implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -3889870888210489041L;
	
	
	public BaseParams getRegisterParams() {
		BaseParams params = new BaseParams();
		params.add("method", "registerDriver");
		
		return params;
	}

}
