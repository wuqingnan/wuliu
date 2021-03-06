package cn.boweikeji.wuliu.view;

import cn.boweikeji.wuliu.supplyer.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SendArrowItem extends LinearLayout {

	private TextView mItemName;
	private TextView mItemValue;

	public SendArrowItem(Context context) {
		super(context);
		initView(context);
	}

	public SendArrowItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SendArrowItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		inflate(context, R.layout.send_item_arrow, this);
		mItemName = (TextView) findViewById(R.id.item_name);
		mItemValue = (TextView) findViewById(R.id.item_value);
	}

	public void setName(int resid) {
		mItemName.setText(resid);
	}
	
	public void setName(CharSequence text) {
		mItemName.setText(text);
	}
	
	public void setValue(int resid) {
		mItemValue.setText(resid);
	}
	
	public void setValue(CharSequence text) {
		mItemValue.setText(text);
	}
	
	public String getValue() {
		return mItemValue.getText().toString();
	}
	
	public void hideArrow() {
		mItemValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	}
	
	public void showArrow() {
		mItemValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.arrow_right);
	}
}
