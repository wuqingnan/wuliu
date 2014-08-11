package com.wuliu.client.view;

import com.wuliu.client.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class ClearEditText extends EditText implements OnFocusChangeListener,
		TextWatcher {

	/**
	 * ɾ����ť������
	 */
	private Drawable mClearDrawable;
	private Context mContext;
	/**
	 * �ؼ��Ƿ��н���
	 */
	private boolean hasFoucs;

	public ClearEditText(Context context) {
		this(context, null);
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}
	
	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = getResources().getDrawable(
					R.drawable.btn_edittext_clear);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		// Ĭ����������ͼ��
		setClearIconVisible(false);
		// ���ý���ı�ļ���
		setOnFocusChangeListener(this);
		// ����������������ݷ����ı�ļ���
		addTextChangedListener(this);
		int right = mContext.getResources().getDimensionPixelSize(R.dimen.edittext_drawable_padding);
		setPadding(getPaddingLeft(), getPaddingTop(), (int) right,
				getPaddingBottom());
		// ����Ϊ����
		setSingleLine();
		setMaxEms(32);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				boolean touchable = event.getX() > (getWidth()
						- getTotalPaddingRight() - 10)
						&& (event.getX() < getWidth());

				if (touchable) {
					this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * ��ClearEditText���㷢���仯��ʱ���ж������ַ��������������ͼ�����ʾ������
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		EditText _v = (EditText) v;
		if (!hasFocus) {
			setClearIconVisible(false);
			// ʧȥ����
			_v.setHint(_v.getTag().toString());
		} else {
			String hint = _v.getHint().toString();
			_v.setTag(hint);
			_v.setHint("");
			setClearIconVisible(getText().length() > 0);
		}
	}

	/**
	 * �������ͼ�����ʾ�����أ�����setCompoundDrawablesΪEditText������ȥ
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/**
	 * ��������������ݷ����仯��ʱ��ص��ķ���
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
