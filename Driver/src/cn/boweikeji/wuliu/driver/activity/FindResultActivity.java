package cn.boweikeji.wuliu.driver.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.FindFilter;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.utils.Util;
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

public class FindResultActivity extends BaseActivity {

	private static final String TAG = FindResultActivity.class.getSimpleName();
	
	private static final String KEY_FILTER = "filter";
	
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
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			loadFinish();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
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
			}
		}
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.swiperefresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.listview)
	ListView mListView;
	@InjectView(R.id.emptyview)
	TextView mEmptyView;
	
	private View mFooter;
	private FindAdapter mAdapter;
	
	private FindFilter mFilter;
	
	private boolean mMore;
	private boolean mLoading = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_result);
		init();
	}
	
	private void init() {
		ButterKnife.inject(this);
		initTitle();
		initView();
		initData();
	}
	
	private void initTitle() {
		mTitle.setText(R.string.title_find_result);
		mBack.setImageResource(R.drawable.btn_title_back);
		mBack.setOnClickListener(mOnClickListener);
	}
	
	private void initView() {
		mFooter = getLayoutInflater().inflate(R.layout.load_layout, null);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
                R.color.color4);
		mListView.addFooterView(mFooter);
		mListView.setFooterDividersEnabled(false);
		mAdapter = new FindAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.removeFooterView(mFooter);
		mListView.setOnScrollListener(mOnScrollListener);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mFilter = (FindFilter) intent.getSerializableExtra(KEY_FILTER);
		refresh();
	}
	
    private void refresh() {
    	if (mLoading) {
    		return;
    	}
    	mMore = true;
    	mFilter.setPage_num(1);
    	mEmptyView.setVisibility(View.GONE);
    	mSwipeRefreshLayout.setEnabled(false);
    	mSwipeRefreshLayout.setRefreshing(true);
    	loadData();
    }
    
    private void loadData() {
    	AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = mFilter.getFindParams();
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_FIND, params));
		client.get(Const.URL_FIND, params, mRequestHandler);
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
				if (res == 2) {//成功
					int pageCount = response.getInt("pagetotalnum");
					if (pageCount <= 0) {
						mEmptyView.setVisibility(View.VISIBLE);
					}
					if (mFilter.getPage_num() >= pageCount) {
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
							order.setIs_order(temp.optString("is_order"));
							data.add(order);
						}
						if (mFilter.getPage_num() == 1) {
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
					mFilter.setPage_num(mFilter.getPage_num() + 1);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
    
    private void loadFinish() {
    	mSwipeRefreshLayout.setEnabled(true);
    	mSwipeRefreshLayout.setRefreshing(false);
    }
    
	public static void startFindResultActivity(Context context, FindFilter filter) {
		Intent intent = new Intent(context, FindResultActivity.class);
		intent.putExtra(KEY_FILTER, filter);
		context.startActivity(intent);
	}
	
	public static class FindAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context mContext;
		private List<Order> mData;
		
		public FindAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.find_result_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Order order = mData.get(position);
			holder.refresh(order);
			return convertView;
		}
		
	}
	
	public static class ViewHolder {
		
		@InjectView(R.id.item_name)
		TextView mOrderName;
		
		public ViewHolder(View parent) {
			ButterKnife.inject(this, parent);
		}
		
		public void refresh(Order order) {
			if (order != null) {
				mOrderName.setText(order.getGoods_name());
			}
		}
		
	}
}
