package cn.boweikeji.wuliu.view;

import cn.boweikeji.wuliu.supplyer.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SendInputItem extends LinearLayout {

	private TextView mItemName;
	private EditText mItemValue;
	private TextView mItemUnit;

	public SendInputItem(Context context) {
		super(context);
		initView(context);
	}

	public SendInputItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SendInputItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		inflate(context, R.layout.send_item_input, this);
		mItemName = (TextView) findViewById(R.id.item_name);
		mItemValue = (EditText) findViewById(R.id.item_value);
		mItemUnit = (TextView) findViewById(R.id.item_unit);
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
	
	public void setUnit(int resid) {
		mItemUnit.setText(resid);
		mItemUnit.setVisibility(View.VISIBLE);
	}
	
	public void setUnit(CharSequence text) {
		mItemUnit.setText(text);
		mItemUnit.setVisibility(View.VISIBLE);
	}
	
	public String getValue() {
		return mItemValue.getText().toString();
	}
	
	public void setMaxLength(int maxLength) {
		mItemValue.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(maxLength)});
	}
	
	public void setInputType(int type) {
		mItemValue.setInputType(type);
	}
}
