package com.wuliu.client.window;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public abstract class WheelWindow {

	private static final String TAG = WheelWindow.class.getSimpleName();

	private static final int VISIBLE_ITEMS = 4;

	public static interface OnConfirmListener {

		public void onConfirm(String result);

	}

	private OnWheelScrollListener mScrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
			int index = wheel.getCurrentItem();
			if (wheel == mLeft) {
				mLeftIndex = index;
			} else if (wheel == mMiddle) {
				mMiddleIndex = index;
			}
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int index = wheel.getCurrentItem();
			if (wheel == mLeft) {
				if (index != mLeftIndex) {
					updateMiddle();
				}
			} else if (wheel == mMiddle) {
				if (index != mMiddleIndex) {
					updateRight();
				}
			}
		}
	};

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mConfirm) {
				if (mOnConfirmListener != null) {
					String result = getResult(mLeft.getCurrentItem(),
							mMiddle.getCurrentItem(), mRight.getCurrentItem());
					mOnConfirmListener.onConfirm(result);
				}
				dismiss();
			}
		}
	};

	private Context mContext;
	private PopupWindow mWindow;

	private View mAnchorView;

	@InjectView(R.id.wheel_left)
	WheelView mLeft;
	@InjectView(R.id.wheel_middle)
	WheelView mMiddle;
	@InjectView(R.id.wheel_right)
	WheelView mRight;
	@InjectView(R.id.wheel_confirm)
	ImageView mConfirm;

	private OnConfirmListener mOnConfirmListener;

	private int mLeftIndex;
	private int mMiddleIndex;

	public WheelWindow(View anchor, OnConfirmListener listener) {
		mAnchorView = anchor;
		mContext = anchor.getContext();
		initData();
		initWindow();
		initWheelView();
		setOnConfirmListener(listener);
	}

	private void initWindow() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.window_picker, null);
		ButterKnife.inject(this, view);
		mWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	private void initWheelView() {
		mLeft.setVisibleItems(VISIBLE_ITEMS);
		mMiddle.setVisibleItems(VISIBLE_ITEMS);
		mRight.setVisibleItems(VISIBLE_ITEMS);
		mLeft.addScrollingListener(mScrollListener);
		mMiddle.addScrollingListener(mScrollListener);
		mConfirm.setOnClickListener(mOnClickListener);
		mLeft.setViewAdapter(new DataAdapter(mContext, getLeftData()));
		mMiddle.setViewAdapter(new DataAdapter(mContext, getMiddleData(0)));
		mRight.setViewAdapter(new DataAdapter(mContext, getRightData(0, 0)));
	}

	public void show() {
		mWindow.showAtLocation(mAnchorView, Gravity.BOTTOM, 0, 0);
	}

	public void dismiss() {
		mWindow.dismiss();
	}

	public boolean isShowing() {
		return mWindow.isShowing();
	}

	public Context getContext() {
		return mContext;
	}

	private void updateMiddle() {
		mMiddle.setViewAdapter(new DataAdapter(mContext, getMiddleData(mLeft
				.getCurrentItem())));
		mMiddle.setCurrentItem(0);
		updateRight();
	}

	private void updateRight() {
		mRight.setViewAdapter(new DataAdapter(mContext, getRightData(
				mLeft.getCurrentItem(), mMiddle.getCurrentItem())));
		mRight.setCurrentItem(0);
	}

	public OnConfirmListener getOnConfirmListener() {
		return mOnConfirmListener;
	}

	public void setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
	}

	protected abstract void initData();

	protected abstract String[] getLeftData();

	protected abstract String[] getMiddleData(int leftIndex);

	protected abstract String[] getRightData(int leftIndex, int middleIndex);

	protected abstract String getResult(int left, int middle, int right);

	private class DataAdapter extends ArrayWheelAdapter<String> {

		public DataAdapter(Context context, String[] items) {
			super(context, items);
			setTextSize(getContext().getResources().getDimensionPixelSize(
					R.dimen.text_size_wheel_window));
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.DEFAULT_BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			return super.getItem(index, cachedView, parent);
		}
	}

}
