package cn.boweikeji.wuliu.driver.fragment;

import org.apache.http.Header;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.activity.FindResultActivity.FindAdapter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrderListFragment extends BaseFragment {

	private static final String TAG = OrderListFragment.class.getSimpleName();
	
	private static final String KEY_TYPE = "type";
	
	private static final int TYPE_DEFAULT = 0;
	
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
		}
	};
	
	private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
//			refresh();
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
//			loadFinish();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//			requestResult(null);
		};
	};
	
	private View mRootView;
	
	@InjectView(R.id.swiperefresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.listview)
	ListView mListView;
	@InjectView(R.id.emptyview)
	TextView mEmptyView;
	
	private View mFooter;
	
	private int mType;
	
	private boolean mMore = true;
	private boolean mLoading = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
        if (args != null) {
        	mType = args.getInt(KEY_TYPE, TYPE_DEFAULT);
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_order_list, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	private void init() {
		ButterKnife.inject(this, mRootView);
		initView();
	}
	
	private void initView() {
		mFooter = getActivity().getLayoutInflater().inflate(R.layout.load_layout, null);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
                R.color.color4);
		mListView.addFooterView(mFooter);
		mAdapter = new FindAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	public static OrderListFragment newInstance(int type) {
		OrderListFragment f = new OrderListFragment();
        Bundle b = new Bundle();
        b.putInt(KEY_TYPE, type);
        f.setArguments(b);
        return f;
    }
	
	public static class OrderListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
