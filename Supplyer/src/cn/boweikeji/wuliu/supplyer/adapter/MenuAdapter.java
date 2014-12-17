package cn.boweikeji.wuliu.supplyer.adapter;

import cn.boweikeji.wuliu.supplyer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

	private int[] mIcons;
	private String[] mNames;
	
	private Context mContext;
	private LayoutInflater mInflater;

	public MenuAdapter(Context context, int[] icons, String[] names) {
		mIcons = icons;
		mNames = names;
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mIcons == null ? 0 : mIcons.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.menu_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		holder.mIconView.setImageResource(mIcons[position]);
		holder.mTitleView.setText(mNames[position]);
		return convertView;
	}

	static class ViewHolder {
		@InjectView(R.id.item_icon)
		ImageView mIconView;
		@InjectView(R.id.item_title)
		TextView mTitleView;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}

	}
}
