package com.cw.alarmcall;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @version : 1.0
 * @author : kim chang wan (cwank89@gmail.com)
 */
public class ListAdapter extends BaseAdapter {
	private MainActivity mActivity;
	private ArrayList<List> mListData = new ArrayList<List>();
	private LinearLayout listView_ly;
	private LinearLayout itemList_ly;

	public int ID;

	private static final String TAG = ListAdapter.class.getSimpleName();

	private class ViewHolder {

		public TextView name;
		public TextView phonenum;

	}

	public class List {

		public String name;
		public String phonenum;

	}

	public ListAdapter(MainActivity mainActivity) {
		super();
		this.mActivity = mainActivity;

	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItem(String name, String phonenum) {
		List addInfo = null;
		addInfo = new List();

		addInfo.name = name;
		addInfo.phonenum = phonenum;

		mListData.add(addInfo);

	}

	public void remove(int position) {

		Log.e("remove", "" + position);

		mListData.remove(position);

	}

	public void dataChange() {
		notifyDataSetChanged();

	}

	public List getData(int position) {
		List list = mListData.get(position);

		return list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Log.e(TAG, " position : " + position);

		if (convertView == null) {
			holder = new ViewHolder();

			LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_listview, null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 250);

			listView_ly = (LinearLayout) convertView
					.findViewById(R.id.listView_ly);
			listView_ly.setLayoutParams(params);

			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phonenum = (TextView) convertView
					.findViewById(R.id.phonenum);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		listView_ly = (LinearLayout) convertView.findViewById(R.id.listView_ly);
		listView_ly.setTag(position);
		listView_ly.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ID = (Integer) v.getTag();
				Log.e(TAG, "ID : " + ID);
				List mData = mListData.get(ID);
				Log.e(TAG, " current Name : " + mData.name);
				Log.e(TAG, " current PhoneNum : " + mData.phonenum);

				
				Info mInfo = new Info(mActivity,ID,mData);
			}
		});

		List mData = mListData.get(position);

		holder.name.setText(mData.name);
		holder.phonenum.setText(mData.phonenum);
		// holder.url.setText(mData.url);

		return convertView;
	}

}