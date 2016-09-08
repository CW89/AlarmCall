package com.cw.alarmcall;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Util mUtil = new Util(context);

			Const.alarmArr = mUtil.getFileReadData();
			long now = System.currentTimeMillis();
//
//			for (int i = 0; i < Const.alarmArr.length(); i++) {
//				try {
//					JSONObject jObj = Const.alarmArr.getJSONObject(i);
//					if (jObj != null) {
//
//						if (now > Long.parseLong(jObj.getString("time"))) {
//							Alarm mAlarm = new Alarm(context);
//
//							mAlarm.releaseAlarm(jObj.getInt("id"));
//							Const.alarmArr.put(i, null);
//						}
//
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			// Toast toast = Toast.makeText(context, "" +
			// Const.alarmArr.toString(),
			// Toast.LENGTH_SHORT);
			// toast.show();

			for (int i = 0; i < Const.alarmArr.length(); i++) {
				try {
					JSONObject jObj = Const.alarmArr.getJSONObject(i);
					if (jObj != null) {
						if (now > Long.parseLong(jObj.getString("time"))) {
							Alarm mAlarm = new Alarm(context);

							mAlarm.releaseAlarm(jObj.getInt("id"));
							Const.alarmArr.put(i, null);
						} else {
							Alarm mAlarm = new Alarm(context);

							mAlarm.setAlarm(jObj.getInt("id"),
									jObj.getString("name"),
									jObj.getString("phone"),
									jObj.getString("time"));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mUtil.setFileWriteData(Const.alarmArr);
			Const.alarmArr = mUtil.getFileReadData();
			Const.alarmArrLength = Const.alarmArr.length();
		}
	}

}