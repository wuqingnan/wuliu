package cn.boweikeji.wuliu.view;

import cn.boweikeji.wuliu.supplyer.adapter.MenuAdapter;
import cn.boweikeji.wuliu.supplyer.event.LoginEvent;
import cn.boweikeji.wuliu.supplyer.event.LogoutEvent;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;

import cn.boweikeji.wuliu.supplyer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuView extends RelativeLayout {

	private static final String TAG = MenuView.class.getSimpleName();

	public static final int MENU_PROFILE = -1;
	public static final int MENU_ORDER = 0;
	public static final int MENU_ACTIVITY = 1;
	public static final int MENU_GUIDE = 2;
	public static final int MENU_INVITE = 3;
	public static final int MENU_SHARE = 4;
	public static final int MENU_SUGGEST = 5;
	public static final int MENU_SETTING = 6;

	private static final int[] ICONS = new int[] { R.drawable.ic_order,
			R.drawable.ic_activity, R.drawable.ic_guide, R.drawable.ic_invite,
			R.drawable.ic_share, R.drawable.ic_suggest, R.drawable.ic_setting };

	public static interface OnMenuClickListener {

		public void onMenuClick(int menu);

	}

	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mOnMenuClickListener != null) {
				mOnMenuClickListener.onMenuClick((int) id);
			}
		}
	};

	private TextView mPhone;
	private ListView mListView;
	private MenuAdapter mMenuAdapter;

	private OnMenuClickListener mOnMenuClickListener;

	public MenuView(Context context) {
		super(context);
		init();
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		initView();
		initHeader();
		initAdapter();
		updateInfo();
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.menu_layout, this);
		mListView = (ListView) findViewById(R.id.menu_list);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	private void initHeader() {
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.menu_header, null);
		mPhone = (TextView) view.findViewById(R.id.phone);
		mListView.addHeaderView(view);
	}

	private void initAdapter() {
		mMenuAdapter = new MenuAdapter(getContext(), ICONS, getResources()
				.getStringArray(R.array.menu_names));
		mListView.setAdapter(mMenuAdapter);
	}

	private void updateInfo() {
		if (LoginManager.getInstance().hasLogin()) {
			mPhone.setText(LoginManager.getInstance().getUserInfo().getPhone());
		} else {
			mPhone.setText(null);
		}
	}

	public OnMenuClickListener getOnMenuClickListener() {
		return mOnMenuClickListener;
	}

	public void setOnMenuClickListener(OnMenuClickListener listener) {
		mOnMenuClickListener = listener;
	}

	/**
	 * 处理登陆消息
	 * 
	 * @param event
	 */
	public void onEventMainThread(LoginEvent event) {
		updateInfo();
	}

	/**
	 * 处理注销消息
	 * 
	 * @param event
	 */
	public void onEventMainThread(LogoutEvent event) {
		updateInfo();
	}
}
