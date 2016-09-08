package com.cw.alarmcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class CallResultActivity extends Activity {

	public static String TAG = CallResultActivity.class.getSimpleName();
	private String _id;
	private String _name;
	private String _phonenum;
	CallResultActivity mActivity;
	private AlertDialog.Builder alertDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mActivity = this;

		settingActivity();




		getRingerMode(this);
		recvIntent();
		Util.updateIconBadgeCount(mActivity, 1);

		createDialog();
		wakeScreen();
		removeAlarm();

	}
	public void settingActivity() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
	public void getRingerMode(Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		switch (audioManager.getRingerMode()) {
		case AudioManager.RINGER_MODE_VIBRATE:
			// 진동

			vibe.vibrate(1000);
			break;
		case AudioManager.RINGER_MODE_NORMAL:
			// 소리
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
			vibe.vibrate(1000);
			break;
		case AudioManager.RINGER_MODE_SILENT:
			// 무음
			break;
		}

	}

	

	public void wakeScreen() {
		int defTimeOut = Settings.System.getInt(getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, 15000);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				PushWakeLock.releaseCpuLock();
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, defTimeOut);
	}

	public void removeAlarm() {
		Util mUtil = new Util(this);
		Const.alarmArr = mUtil.getFileReadData();

		Log.e("d", " alarmArr : " + Const.alarmArr.toString());
		try {
			Const.alarmArr.put(Integer.parseInt(_id), null);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mUtil.setFileWriteData(Const.alarmArr);
		Const.alarmArr = mUtil.getFileReadData();
		Const.alarmArrLength = Const.alarmArr.length();
		Alarm mAlarm = new Alarm(mActivity);
		mAlarm.releaseAlarm(Integer.parseInt(_id));
	}

	public void recvIntent() {
		Intent intent = getIntent();

		_id = intent.getExtras().getString("id");
		_name = intent.getExtras().getString("name");
		_phonenum = intent.getExtras().getString("phone");
	}

	public void createDialog() {
		String title = "알림";
		String msg = " \n" + _name + " \n" + _phonenum + " \n";
		alertDialog = new AlertDialog.Builder(this);

		alertDialog.setPositiveButton("닫기",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						PushWakeLock.releaseCpuLock();
						Util.updateIconBadgeCount(mActivity, 0);
						mActivity.finish();
						mActivity.overridePendingTransition(0, 0);

					}
				});

		alertDialog.setNegativeButton("통화",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(Intent.ACTION_DIAL, Uri
								.parse("tel:" + _phonenum));
						startActivity(i);
						PushWakeLock.releaseCpuLock();

						Util.updateIconBadgeCount(mActivity, 0);
						mActivity.finish();
						mActivity.overridePendingTransition(0, 0);

					}
				});
		alertDialog.setIcon(R.drawable.app_icon);
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	@Override
	protected void onUserLeaveHint() {
		// 여기서 감지
		Log.d(TAG, "Home Button Touch");
		super.onUserLeaveHint();

		mActivity.finish();
	}

	public void onBackPressed() {
		mActivity.finish();
	}
}
