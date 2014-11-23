package cn.boweikeji.wuliu.driver.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.activity.OrderDetailActivity;
import cn.boweikeji.wuliu.driver.activity.FindResultActivity.FindAdapter;
import cn.boweikeji.wuliu.driver.activity.FindResultActivity.ViewHolder;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.OrderEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.Util;
import de.greenrobot.event.EventBus;
import android.content.Context;
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

public class OrderListFragment extends BaseFragment {

	private static final String TAG = OrderListFragment.class.getSimpleName();

	public static final String KEY_TYPE = "type";

	public static final int TYPE_SELECT = 0;
	public static final int TYPE_SELECTED = 1;
	public static final int TYPE_COMPLETED = 2;
	public static final int TYPE_CANCEL = 3;

	private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			 refresh();
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
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
		}
	};
	
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (id >= 0) {
				Order order = (Order) mAdapter.getItem((int)id);
				OrderDetailActivity.startOrderDetailActivity(getActivity(), order.getGoods_cd());
			}
		}
	};
	
	private View mRootView;

	@InjectView(R.id.swiperefresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.listview)
	ListView mListView;
	@InjectView(R.id.emptyview)
	TextView mEmptyView;

	private View mFooter;
	private OrderListAdapter mAdapter;

	private int mType;
	private int mPage;

	private boolean mMore = true;
	private boolean mLoading = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "shizy---OrderListFragment.onCreate");
		Bundle args = getArguments();
		if (args != null) {
			mType = args.getInt(KEY_TYPE, TYPE_SELECT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_order_list, null);
		return mRootView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "shizy---OrderListFragment.onActivityCreated");
		init();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initView();
		initData();
		EventBus.getDefault().register(this);
	}

	private void initView() {
		mFooter = getActivity().getLayoutInflater().inflate(R.layout.load_layout, null);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
                R.color.color4);
		mListView.addFooterView(mFooter);
		mAdapter = new OrderListAdapter(getActivity(), mType);
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
    	AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BDLocation location = WLApplication.getLocationClient().getLastKnownLocation();
		BaseParams params = new BaseParams();
		params.add("method", "getMyRecords");
		params.add("page_num", "" + mPage);
		switch (mType) {
		case TYPE_SELECT:
			params.add("state", "0");
			break;
		case TYPE_SELECTED:
			params.add("state", "1");
			break;
		case TYPE_COMPLETED:
			params.add("state", "9");
			break;
		case TYPE_CANCEL:
			params.add("state", "8");
			break;
		}
		if (location == null) {
			params.add("gps_j", BaseParams.PARAM_DEFAULT);
			params.add("gps_w", BaseParams.PARAM_DEFAULT);
		} else {
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
		}
		if (LoginManager.getInstance().hasLogin()) {
			UserInfo info = LoginManager.getInstance().getUserInfo();
			params.add("driver_cd", info.getDriver_cd());
			params.add("passwd", info.getPasswd());
		} else {
			params.add("driver_cd", BaseParams.PARAM_DEFAULT);
			params.add("passwd", BaseParams.PARAM_DEFAULT);
		}
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_ORDER_LIST, params));
		client.get(Const.URL_ORDER_LIST, params, mRequestHandler);
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
    	if (!isAdded()) {
    		return;
    	}
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(getActivity(), msg);
				if (res == 2) {//成功
					int pageCount = response.getInt("pagetotalnum");
					if (pageCount <= 0) {
						mEmptyView.setVisibility(View.VISIBLE);
					}
					if (mPage >= pageCount) {
						mMore = false;
					}
					JSONArray infos = response.optJSONArray("infos");
					if (infos != null && infos.length() > 0) {
						JSONObject temp = null;
						Order order = null;
						List<Order> data = new ArrayList<Order>();
						for (int i = 0; i < infos.length(); i++) {
							temp = infos.optJSONObject(i);
							order = new Order();
							order.setCreate_date(temp.optString("create_date"));
							order.setGoods_cd(temp.optString("goods_cd"));
							order.setGoods_name(temp.optString("goods_name"));
							if (mType == TYPE_SELECT) {
								order.setIs_order(Integer.parseInt(temp.optString("is_order")));
								order.setDistance(Double.parseDouble(temp.optString("distance")));
							} else if (mType == TYPE_COMPLETED) {
								order.setIs_ticked(Integer.parseInt(temp.optString("is_ticked")));
							} else if (mType == TYPE_CANCEL) {
								order.setState(Integer.parseInt(temp.optString("state")));
							}
							data.add(order);
						}
						if (mPage == 1) {
							mAdapter.setData(data);
						} else {
							mAdapter.addData(data);
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
		}
		Util.showTips(getActivity(), getString(R.string.request_failed));
	}
    
    private void loadFinish() {
    	mSwipeRefreshLayout.setEnabled(true);
    	mSwipeRefreshLayout.setRefreshing(false);
    }
    
    public void onEventMainThread(OrderEvent event) {
    	refresh();
    }
	
	public static class OrderListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context mContext;
		private List<Order> mData;
		private int mType;

		public OrderListAdapter(Context context, int type) {
			mType = type;
			mContext = context;
			mInflater = LayoutInflater.from(context);
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
		public int getCount() {
			return mData == null ? 0 : mData.size();
		}

		@Override
		public Order getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.order_list_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Order order = mData.get(position);
			holder.refresh(order, mType);
			return convertView;
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
		@InjectView(R.id.item_bespeak)
		TextView mBespeak;

		public ViewHolder(View parent) {
			ButterKnife.inject(this, parent);
		}

		public void refresh(Order order, int type) {
			if (order != null) {
				mOrderName.setText(order.getGoods_name());
				mOrderCode.setText(String.format("订单号：%s", order.getGoods_cd()));
				mOrderDate.setText(order.getCreate_date());
			}
			switch (type) {
			case TYPE_SELECT:
				mBespeak.setVisibility(View.VISIBLE);
				mOrderInfo.setVisibility(View.VISIBLE);
				mBespeak.setText(order.isOrder() ? "预约单" : "实时单");
				mOrderInfo.setText(String.format("距离\n%d米", (int)order.getDistance()));
				break;
			case TYPE_SELECTED:
				mBespeak.setVisibility(View.GONE);
				mOrderInfo.setVisibility(View.GONE);
				break;
			case TYPE_COMPLETED:
				mBespeak.setVisibility(View.GONE);
				mOrderInfo.setVisibility(View.VISIBLE);
				mOrderInfo.setText(order.isTicked() ? "已评价" : "待评价");
				break;
			case TYPE_CANCEL:
				mBespeak.setVisibility(View.GONE);
				mOrderInfo.setVisibility(View.VISIBLE);
				mOrderInfo.setText("" + order.getState());
				break;
			}
		}

	}
}
