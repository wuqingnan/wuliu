package cn.boweikeji.wuliu.driver.event;

public class OrderEvent extends BaseEvent {

	private String mGoodsCD;
	
	public OrderEvent(String goods_cd) {
		mGoodsCD = goods_cd;
	}
	
}
