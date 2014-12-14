package cn.boweikeji.wuliu.supplyer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.activity.MainActivity;
import cn.boweikeji.wuliu.supplyer.activity.OrderDetailActivity;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.Order;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.utils.Util;

import com.loopj.android.http.JsonHttpResponseHandler;

import cn.boweikeji.wuliu.supplyer.R;

public class OrderActivity extends BaseActivity {

	private static final String TAG = OrderActivity.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			}
		}
	};

	private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			refresh();
		}
	};

	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (id >= 0) {
				Order order = (Order) mAdapter.getItem((int) id);
				OrderDetailActivity.startOrderDetailActivity(
						OrderActivity.this, order.getGoods_cd());
			}
		}
	};

	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {

		public void onFinish() {
			loadFinish();
		};

		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			requestResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (view.getLastVisiblePosition() == view.getCount() - 1) {
				loadMore();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.order_count)
	TextView mOrderCount;
	@InjectView(R.id.swiperefresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.listview)
	ListView mListView;
	@InjectView(R.id.emptyview)
	TextView mEmptyView;

	private View mFooter;

	private int mPage;
	private boolean mMore;
	private boolean mLoading;

	private OrderAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		init();
	}

	private void init() {
		ButterKnife.inject(this);
		initTitle();
		initView();
		initData();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_order_list);
		mBack.setImageResource(R.drawable.ic_navi_back);
		mBack.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		mFooter = getLayoutInflater().inflate(R.layout.load_layout, null);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color1,
				R.color.color2, R.color.color3, R.color.color4);
		mListView.addFooterView(mFooter);
		mAdapter = new OrderAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.removeFooterView(mFooter);
		mListView.setOnScrollListener(mOnScrollListener);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	private void initData() {
		refresh();
	}

	private void refresh() {
		if (mLoading) {
			return;
		}
		mMore = true;
		mPage = 1;
		mEmptyView.setVisibility(View.GONE);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setRefreshing(true);
		loadData();
	}

	private void loadData() {
		mLoading = true;
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "getAllMySupplys");
		params.add("supplyer_cd", info.getSupplyer_cd());
		params.add("passwd", info.getPasswd());
		params.add("page_num", "" + mPage);
		AsyncHttp.get(Const.URL_ORDER_LIST, params, mRequestHandler);
	}

	private void loadMore() {
		if (!mMore) {
			return;
		}
		if (mLoading) {
			return;
		}
		loadData();
	}

	private void requestResult(JSONObject response) {
		mLoading = false;
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {// 成功
					int pageCount = response.getInt("pagetotalnum");
					if (pageCount <= 0) {
						mEmptyView.setVisibility(View.VISIBLE);
					}
					if (mPage >= pageCount) {
						mMore = false;
					}
					int totalnum = response.getInt("totalnum");
					JSONArray infos = response.optJSONArray("infos");
					if (infos != null && infos.length() > 0) {
						JSONObject temp = null;
						Order order = null;
						List<Order> data = new ArrayList<Order>();
						for (int i = 0; i < infos.length(); i++) {
							temp = infos.optJSONObject(i);
							order = new Order();
							order.setState(temp.optInt("state"));
							order.setGoods_cd(temp.optString("goods_cd"));
							order.setGoods_name(temp.optString("goods_name"));
							order.setCreate_date(temp.optString("create_date"));
							data.add(order);
						}
						Collections.sort(data, new Comparator<Order>() {
							@Override
							public int compare(Order lhs, Order rhs) {
								return rhs.getCreate_date().compareTo(
										lhs.getCreate_date());
							}
						});
						if (mPage == 1) {
							mAdapter.setData(data);
						} else {
							mAdapter.addData(data);
						}
						if (totalnum > 0) {
							mOrderCount.setText("共有" + totalnum + "个订单");
							mOrderCount.setVisibility(View.VISIBLE);
						} else {
							mOrderCount.setVisibility(View.GONE);
						}
					}
					if (mMore) {
						if (mListView.getFooterViewsCount() <= 0) {
							mListView.addFooterView(mFooter, null, false);
						}
					} else {
						if (mListView.getFooterViewsCount() > 0) {
							mListView.removeFooterView(mFooter);
						}
					}
					mPage++;
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (mAdapter.getCount() <= 0) {
			mEmptyView.setVisibility(View.VISIBLE);
			mOrderCount.setVisibility(View.GONE);
		}
		Util.showTips(this, getString(R.string.request_failed));
	}

	private void loadFinish() {
		mSwipeRefreshLayout.setEnabled(true);
		mSwipeRefreshLayout.setRefreshing(false);
	}

	public static class OrderAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;
		private List<Order> mData = new ArrayList<Order>();
		private String[] mStateName;
		private int[] mStateValue;

		public OrderAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mStateName = context.getResources().getStringArray(
					R.array.order_state_name);
			mStateValue = context.getResources().getIntArray(
					R.array.order_state_value);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setData(List<Order> data) {
			if (mData != null) {
				mData.clear();
				mData = null;
			}
			mData = data;
			notifyDataSetChanged();
		}

		public void addData(List<Order> data) {
			if (mData == null) {
				mData = data;
			} else {
				mData.addAll(data);
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.order_list_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Order order = mData.get(position);
			holder.refresh(order,
					mStateName[getStateIndexByValue(order.getState())]);
			return convertView;
		}

		private int getStateIndexByValue(int value) {
			int index = 0;
			for (int i = 0; i < mStateValue.length; i++) {
				if (mStateValue[i] == value) {
					index = i;
					break;
				}
			}
			return index;
		}
	}

	public static class ViewHolder {

		@InjectView(R.id.item_name)
		TextView mOrderName;
		@InjectView(R.id.item_code)
		TextView mOrderCode;
		@InjectView(R.id.item_date)
		TextView mOrderDate;
		@InjectView(R.id.item_info)
		TextView mOrderInfo;

		public ViewHolder(View parent) {
			ButterKnife.inject(this, parent);
		}

		public void refresh(Order order, String info) {
			if (order != null) {
				mOrderName.setText(order.getGoods_name());
				mOrderCode
						.setText(String.format("订单号：%s", order.getGoods_cd()));
				mOrderDate.setText(order.getCreate_date());
				mOrderInfo.setText(info);
			}
		}

	}
	
	public static void startOrderActivity(Context context) {
		context.startActivity(new Intent(context, OrderActivity.class));
	}
}
