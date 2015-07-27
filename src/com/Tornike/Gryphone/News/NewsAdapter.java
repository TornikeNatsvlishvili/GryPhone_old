package com.Tornike.Gryphone.News;

import com.Tornike.Gryphone.R;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	public static final int TYPE_ITEM = 0;
	public static final int TYPE_SEPARATOR = 1;
	public static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private ArrayList items = new ArrayList();
	Context mContext;
	private LayoutInflater mInflater;
	private TreeSet mSeparatorsSet = new TreeSet();
	
	public NewsAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(final NewsItem item) {
		items.add(item);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(final String item) {
		String[] temp = new String[2];
		temp[0] = item;
		temp[1] = "firstLoad";
		items.add(temp);
		// save separator position
		mSeparatorsSet.add(items.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return items.size();
	}

	public NewsItem getItem(int position) {
		return (NewsItem) items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		items.clear();
		mSeparatorsSet.clear();
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		int type = getItemViewType(position);

		if (v == null) {
			if (type == TYPE_ITEM) {
				v = mInflater.inflate(R.layout.news_list_row, null);
			} else if (type == TYPE_SEPARATOR) {
				v = mInflater.inflate(R.layout.news_list_seperator, null);
			}

		}
		if (type == TYPE_ITEM) {
			NewsItem newsItem = (NewsItem) items.get(position);
			if (newsItem != null) {
				TextView tt = (TextView) v
						.findViewById(R.id.news_list_row_title_text);
				ImageView iv = (ImageView) v
						.findViewById(R.id.news_list_row_icon);
				if (tt != null) {
					tt.setText(newsItem.getTitle());
				}
				if (iv != null) {
					iv.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.news_newsicon));
				}
			}
		} else if (type == TYPE_SEPARATOR) {
			String[] tempText = (String[]) items.get(position);
			String seperatorText = tempText[0];
			if (seperatorText != null) {
				TextView st = (TextView) v
						.findViewById(R.id.list_seperator_text);
				if (st != null) {
					st.setText(seperatorText);
				}
			}
		}

		return v;
	}
}