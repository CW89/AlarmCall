package com.cw.alarmcall;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cw.alarmcall.ListAdapter.List;

public class Info {

	public static final String TAG = Info.class.getSimpleName();
	public MainActivity mActivity;
	public View info = null;

	TimePickerDialog dialog;

	public int _id;
	public List _mData;

	private GregorianCalendar mCalendar;
	Util mUtil;

	public Info(MainActivity mActivity, int id, List mData) {
		super();
		this.mActivity = mActivity;
		this._id = id;
		this._mData = mData;

		onCreate();
	}

	public void onCreate() {

		Log.e(TAG, " onCreate ");
		Log.e(TAG, " name :  " + _mData.name);
		Log.e(TAG, " phone :  " + _mData.phonenum);
		mUtil = new Util(mActivity);
		onFindView();

		dialog.setTitle(_mData.name);
		dialog.show();
	}

	public void onFindView() {

		mCalendar = new GregorianCalendar();
		mCalendar.set(mCalendar.get(Calendar.YEAR),
				mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH),
				mCalendar.get(Calendar.HOUR_OF_DAY),
				mCalendar.get(Calendar.MINUTE));
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat CurMinuteFormat = new SimpleDateFormat("mm");
		int strCurHour = Integer.parseInt(CurHourFormat.format(date));
		int strCurMinute = Integer.parseInt(CurMinuteFormat.format(date));
		dialog = new TimePickerDialog(mActivity, listener, strCurHour,
				strCurMinute, false);

	}

	public void setJSONData(String name, String phone, String time) {
		JSONArray jArr = new JSONArray();
		JSONObject jObj = new JSONObject();
		int id = Const.alarmArrLength;

		try {
			jObj.put("id", id + "");
			jObj.put("name", name);
			jObj.put("phone", phone);
			jObj.put("time", time);
			Const.alarmArr.put(id, jObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mUtil.setFileWriteData(Const.alarmArr);
		JSONArray jArr2 = mUtil.getFileReadData();
		Const.alarmArrLength = Const.alarmArr.length();
		Log.e(TAG, " alarmArr : " + jArr2.toString());
		Alarm mAlarm = new Alarm(mActivity);
		mAlarm.setAlarm(id, name, phone, time);
	}

	private OnTimeSetListener listener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// 설정버튼 눌렀을 때

			mCalendar.set(mCalendar.get(Calendar.YEAR),
					mCalendar.get(Calendar.MONTH),
					mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute,0);
			Log.e(TAG, " mCalendar : " + mCalendar.getTime().toString());
			// setAlarm(mActivity,mCalendar);

			String time = mCalendar.getTimeInMillis() + "";

			long currentTime = System.currentTimeMillis();

			if (currentTime >= mCalendar.getTimeInMillis()) {
				Toast.makeText(mActivity, "시간을 다시 설정해 주세요.", Toast.LENGTH_SHORT)
						.show();
	
			} else {
//				Toast.makeText(mActivity, hourOfDay + "시 " + minute + "분",
//						Toast.LENGTH_SHORT).show();
				setJSONData(_mData.name, _mData.phonenum, time);

				onBackEvent();
			}
		}
	};

	public void onBackEvent() {
		if (info != null) {
			Log.e(TAG, "onBackEvent()  - cartpage != null");
			((ViewManager) info.getParent()).removeView(info);
			info = null;
		}
	}
}