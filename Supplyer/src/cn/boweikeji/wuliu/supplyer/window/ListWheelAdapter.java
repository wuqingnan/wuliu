package cn.boweikeji.wuliu.supplyer.window;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class ListWheelAdapter<T> extends AbstractWheelTextAdapter {

	private List<T> items;
	
	public ListWheelAdapter(Context context, List<T> items) {
        super(context);
        this.items = items;
    }
	
	@Override
	public int getItemsCount() {
		return items == null ? 0 : items.size();
	}

	public void setItems(List<T> items) {
		this.items = items;
		notifyDataChangedEvent();
	}
	
	@Override
	protected CharSequence getItemText(int index) {
		if (index >= 0 && index < items.size()) {
            T item = items.get(index);
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
	}

	
}
