package cn.boweikeji.wuliu.view;

import cn.boweikeji.wuliu.supplyer.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SendAddressItem extends RelativeLayout {

	public SendArrowItem mFromAddress;
	public SendArrowItem mToAddress;
	public ImageView mSwapView;

	public SendAddressItem(Context context) {
		super(context);
		initView(context);
	}

	public SendAddressItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SendAddressItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		inflate(context, R.layout.send_item_address, this);
		mFromAddress = (SendArrowItem) findViewById(R.id.from_address);
		mToAddress = (SendArrowItem) findViewById(R.id.to_address);
		mSwapView = (ImageView) findViewById(R.id.swap_address);
		mFromAddress.setName(R.string.label_from_address);
		mToAddress.setName(R.string.label_to_address);
		mFromAddress.hideArrow();
		mToAddress.hideArrow();
	}
	
	public void setOnClickListener(View.OnClickListener listener) {
		mFromAddress.setOnClickListener(listener);
		mToAddress.setOnClickListener(listener);
		mSwapView.setOnClickListener(listener);
	}
	
	public SendArrowItem getFromView() {
		return mFromAddress;
	} 
	
	public SendArrowItem getToView() {
		return mToAddress;
	}
	
	public ImageView getSwapView() {
		return mSwapView;
	}

	public String getFromAddress() {
		return mFromAddress.getValue();
	}
	
	public String getToAddress() {
		return mToAddress.getValue();
	}
	
	public void setFromAddress(String address) {
		mFromAddress.setValue(address);
	}
	
	public void setToAddress(String address) {
		mToAddress.setValue(address);
	}
	
}
