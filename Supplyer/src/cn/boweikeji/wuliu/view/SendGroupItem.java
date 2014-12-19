package cn.boweikeji.wuliu.view;

import cn.boweikeji.wuliu.supplyer.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SendGroupItem extends LinearLayout {

	private TextView mItemName;
	private RadioGroup mRadioGroup;

	public SendGroupItem(Context context) {
		super(context);
		initView(context);
	}

	public SendGroupItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SendGroupItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		inflate(context, R.layout.send_item_group, this);
		mItemName = (TextView) findViewById(R.id.item_name);
		mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
	}

	public void setName(int resid) {
		mItemName.setText(resid);
	}
	
	public void setName(CharSequence text) {
		mItemName.setText(text);
	}
	
	public void check(int resid) {
		mRadioGroup.check(resid);
	}
	
	public int getCheckedRadioButtonId() {
		return mRadioGroup.getCheckedRadioButtonId();
	}
	
}
