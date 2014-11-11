package cn.boweikeji.wuliu.driver.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AlphabetView extends View {

	private static final String TAG = AlphabetView.class.getSimpleName();

	private static final String[] ALPHABET_DEFAULT = { "#", "A", "B", "C", "D",
			"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private OnAlphabetViewTouchListener mOnAlphabetViewTouchListener;

	private int mTouchIndex = -1;
	private Paint mPaint = new Paint();
	private boolean mIsInTouch = false;

	/** 控件的宽度 **/
	private int mWidth;
	/** 控件的高度 **/
	private int mHeight;

	/** 绘制时，Y方向偏移量 **/
	private int mOffsetY;
	/** 绘制时，基线偏移量 **/
	private int mOffsetBaseLine;
	/** 分割后，每一块的高度 **/
	private int mSingleHeight;

	public AlphabetView(Context context) {
		super(context);
	}

	public AlphabetView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphabetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mWidth = getWidth();
		mHeight = getHeight();

		mSingleHeight = mHeight / ALPHABET_DEFAULT.length;
		mOffsetY = mHeight % ALPHABET_DEFAULT.length / 2;

		mPaint.setTextSize(mSingleHeight / 2);
		mPaint.setAntiAlias(true);
		mPaint.setFakeBoldText(true);

		FontMetrics fm = mPaint.getFontMetrics();
		mOffsetBaseLine = mSingleHeight / 2
				+ (int) Math.abs((fm.bottom - fm.top) / 2 + fm.top);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mIsInTouch) {
			canvas.drawColor(Color.parseColor("#C0F4F4F4"));
		}

		for (int i = 0; i < ALPHABET_DEFAULT.length; i++) {
			if (mIsInTouch) {
				mPaint.setColor(Color.parseColor("#209AE4"));
			} else {
				mPaint.setColor(Color.parseColor("#ABABAB"));
			}
			float xPos = mWidth / 2 - mPaint.measureText(ALPHABET_DEFAULT[i])
					/ 2;
			float yPos = mOffsetY + mSingleHeight * i + mOffsetBaseLine;
			canvas.drawText(ALPHABET_DEFAULT[i], xPos, yPos, mPaint);
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final int curIndex = ((int) event.getY() - mOffsetY) / mSingleHeight;

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mIsInTouch = true;
			if (mTouchIndex != curIndex && mOnAlphabetViewTouchListener != null) {
				if (curIndex >= 0 && curIndex < ALPHABET_DEFAULT.length) {
					mOnAlphabetViewTouchListener
							.OnAlphabetViewTouch(ALPHABET_DEFAULT[curIndex],
									MotionEvent.ACTION_DOWN);
					mTouchIndex = curIndex;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchIndex != curIndex && mOnAlphabetViewTouchListener != null) {
				if (curIndex >= 0 && curIndex < ALPHABET_DEFAULT.length) {
					mOnAlphabetViewTouchListener
							.OnAlphabetViewTouch(ALPHABET_DEFAULT[curIndex],
									MotionEvent.ACTION_MOVE);
					mTouchIndex = curIndex;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mIsInTouch = false;
			mTouchIndex = -1;
			if (mOnAlphabetViewTouchListener != null) {
				mOnAlphabetViewTouchListener.OnAlphabetViewTouch(null,
						MotionEvent.ACTION_UP);
			}
			invalidate();
			break;
		}
		return true;
	}

	public void setOnAlphabetViewTouchListener(
			OnAlphabetViewTouchListener listener) {
		mOnAlphabetViewTouchListener = listener;
	}

	public interface OnAlphabetViewTouchListener {

		public void OnAlphabetViewTouch(String letter, int action);

	}
}
