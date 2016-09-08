package com.cw.alarmcall;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class Alarm {

	Context _context;

	public Alarm(Context context) {
		_context = context;
	}

	public String TAG = Alarm.class.getSimpleName();

	// 알람 등록
	public void setAlarm(int id, String name, String phone, String time) {
		Log.i(TAG, "setAlarm()");
		AlarmManager alarmManager = (AlarmManager) _context
				.getSystemService(Context.ALARM_SERVICE);


		// releaseAlarm(id);

		Intent intent = new Intent(_context, CallResultActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("id", id + "");
		intent.putExtra("name", name);
		intent.putExtra("phone", phone);
		PendingIntent pIntent = PendingIntent.getActivity(_context, id, intent,
				PendingIntent.FLAG_ONE_SHOT);

		alarmManager.set(AlarmManager.RTC, Long.parseLong(time), pIntent);
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

		}
	};

	public void releaseAlarm(int id) {
		Log.i(TAG, "releaseAlarm()");
		AlarmManager alarmManager = (AlarmManager) _context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(_context, CallResultActivity.class);

		PendingIntent pIntent = PendingIntent.getActivity(_context, id, intent,
				PendingIntent.FLAG_ONE_SHOT);
		
		alarmManager.cancel(pIntent);
		pIntent.cancel();

		// 주석을 풀면 먼저 실행되는 알람이 있을 경우, 제거하고
		// 새로 알람을 실행하게 된다. 상황에 따라 유용하게 사용 할 수 있다.
		// alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,
		// pIntent);
	}

	public int ChangeNumber(String phone) {
		int result = 0;
		String temp = phone.replace("/", "");
		temp = temp.replace("-", "");

		result = Integer.parseInt(temp);

		return result;
	}

}