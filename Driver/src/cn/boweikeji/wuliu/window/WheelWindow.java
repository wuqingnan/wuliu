package cn.boweikeji.wuliu.window;

import cn.boweikeji.wuliu.driver.R;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class WheelWindow {

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
					mOnConfirmListener.onConfirm(mIWheel.getResult(
							mLeft.getCurrentItem(), mMiddle.getCurrentItem(),
							mRight.getCurrentItem()));
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

	private IWheel mIWheel;

	private OnConfirmListener mOnConfirmListener;

	private int mLeftIndex;
	private int mMiddleIndex;

	public WheelWindow(View anchor, OnConfirmListener listener, IWheel wheel) {
		mAnchorView = anchor;
		mContext = anchor.getContext();
		mIWheel = wheel;
		wheel.initData();
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
		// mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// mWindow.setOutsideTouchable(true);
	}

	private void initWheelView() {
		mLeft.setVisibleItems(VISIBLE_ITEMS);
		mMiddle.setVisibleItems(VISIBLE_ITEMS);
		mRight.setVisibleItems(VISIBLE_ITEMS);
		mLeft.addScrollingListener(mScrollListener);
		mMiddle.addScrollingListener(mScrollListener);
		mConfirm.setOnClickListener(mOnClickListener);
		mLeft.setViewAdapter(mIWheel.getLeftAdapter());
		mMiddle.setViewAdapter(mIWheel.getMiddleAdapter(0));
		mRight.setViewAdapter(mIWheel.getRightAdapter(0, 0));
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
		mMiddle.setViewAdapter(mIWheel.getMiddleAdapter(mLeft.getCurrentItem()));
		mMiddle.setCurrentItem(0);
		updateRight();
	}

	private void updateRight() {
		mRight.setViewAdapter(mIWheel.getRightAdapter(mLeft.getCurrentItem(), mMiddle.getCurrentItem()));
		mRight.setCurrentItem(0);
	}

	public OnConfirmListener getOnConfirmListener() {
		return mOnConfirmListener;
	}

	public void setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
	}

	public void updateByInfo(String[] data) {
		if (data == null || data.length < 3) {
			return;
		}
		int leftIndex = mIWheel.getLeftIndex(data[0]);
		if (leftIndex >= 0) {
			mLeftIndex = leftIndex;
			mLeft.setCurrentItem(leftIndex);
			updateMiddle();
			int middleIndex = mIWheel.getMiddleIndex(data[1]);
			if (middleIndex >= 0) {
				mMiddleIndex = middleIndex;
				mMiddle.setCurrentItem(middleIndex);
				updateRight();
				int rightIndex = mIWheel.getRightIndex(data[2]);
				if (rightIndex >= 0) {
					mRight.setCurrentItem(rightIndex);
				}
			}
		}
	}
}
