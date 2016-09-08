package com.cw.alarmcall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	public static final String TAG = MainActivity.class.getSimpleName();

	public MainActivity mActivity;

	public static MainActivity mInstance;
	public ListView list;
	public ListAdapter listadapter = null;
	ArrayList<Contact> arContactList = new ArrayList<Contact>();

	private Button btn_init;
	private Button btn_move;
	private Button btn_gps;
	private Button btn_map;
	private Button btn_sms;

	Util mUtil;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mActivity = this;
		mInstance = this;
		recvIntent();
		Util.updateIconBadgeCount(this, 0);
		onFindView();
		onClickEvent();
		UpdateList();

		mUtil = new Util(mActivity);

		Const.alarmArr = mUtil.getFileReadData();
		Const.alarmArrLength = Const.alarmArr.length();
		getAfterAlarmRemove();
		Log.e(TAG, " alarmArr : " + Const.alarmArr.toString());
		Log.e(TAG, " length : " + Const.alarmArrLength);

		getDateCheck();

	}

	private void recvIntent() {
		Intent intent = getIntent();

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {

			Uri uri = intent.getData();

			String param1 = uri.getQueryParameter("param1");

			Log.e(TAG, " param1 : " + param1);

			if ("main".equals(param1)) {

			} else if ("scheme".equals(param1)) {
				movePage();
			}

		}

	}

	public static MainActivity getInstance() {
		return mInstance;
	}

	public void getAfterAlarmRemove() {
		long now = System.currentTimeMillis();

		for (int i = 0; i < Const.alarmArr.length(); i++) {
			try {
				JSONObject jObj = Const.alarmArr.getJSONObject(i);
				if (jObj != null) {

					if (now > Long.parseLong(jObj.getString("time"))) {
						Alarm mAlarm = new Alarm(mActivity);
						Log.e(TAG, " 제거 id : " + jObj.getInt("id"));
						mAlarm.releaseAlarm(jObj.getInt("id"));
						Const.alarmArr.put(i, null);
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void getDateCheck() {
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
		String strCurDay = CurDayFormat.format(date);
		Log.e(TAG, " strCurDay : " + strCurDay);

		JSONObject jObj = mUtil.getFileReadDate();

		String fileDay;

		try {
			fileDay = jObj.getString("date");

			Log.e(TAG, " fileDay : " + fileDay);

			if (!fileDay.equals(strCurDay)) {
				JSONObject jObj2 = new JSONObject();
				try {
					jObj2.put("date", strCurDay);
					mUtil.setFileWriteDate(jObj2);

					setAlarmInit();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject jObj2 = new JSONObject();
			try {
				jObj2.put("date", strCurDay);
				mUtil.setFileWriteDate(jObj2);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	public void UpdateList() {
		Init();
		arContactList = getContactList();

		for (int i = 0; i < arContactList.size(); i++) {

			listadapter.addItem(arContactList.get(i).name.toString(),
					arContactList.get(i).phonenum.toString());
			Log.e(TAG, " name :  " + arContactList.get(i).name);
			Log.e(TAG, " name :  " + arContactList.get(i).phonenum);
		}
		listadapter.dataChange();

	}

	public void onFindView() {
		list = (ListView) findViewById(R.id.list);
		listadapter = new ListAdapter(mActivity);
		list.setAdapter(listadapter);

		btn_init = (Button) findViewById(R.id.btn_init);
		btn_move = (Button) findViewById(R.id.btn_move);

		btn_gps = (Button) findViewById(R.id.btn_gps);
		btn_map = (Button) findViewById(R.id.btn_map);
		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_map.setEnabled(false);
	}

	public void movePage() {
		Intent intent = new Intent(this, SchemeActivity.class);
		startActivity(intent);
		overridePendingTransition(0, 0);
	}

	public void onClickEvent() {
		btn_init.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UpdateList();
				setAlarmInit();

			}
		});
		btn_move.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				movePage();
			}
		});
		btn_gps.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkEnableGPS();
			}
		});
		btn_map.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// locationFlag = true;
				if (locationFlag) {
					// 맵온
					moveGoogleMap();
				} else {
					Util.getToast(mActivity, "위치정보를 수집중입니다.");
				}
			}
		});
		btn_sms.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getList();
			}
		});
	}

	public void getList() {
		Intent intent = new Intent(Intent.ACTION_PICK);

		intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(intent, 0);
	}

	public void getSMS(String num, String body) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		Uri uri = Uri.parse("sms:" + num);

		intent.setData(uri);
		intent.putExtra("sms_body", body);

		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Cursor cursor = getContentResolver()
					.query(data.getData(),
							new String[] {
									ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
									ContactsContract.CommonDataKinds.Phone.NUMBER },
							null, null, null);
			cursor.moveToFirst();
			String name = cursor.getString(0);
			String number = cursor.getString(1);
			Util.getToast(mActivity, name + " / " + number);
			cursor.close();

			getSMS(number, " cw test ");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void moveGoogleMap() {
		Intent intent = new Intent(this, MapActivity.class);

		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);
		startActivity(intent);
		overridePendingTransition(0, 0);
	}

	public void checkEnableGPS() {

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.e(TAG, " GPS false");
			createGpsDisabledAlert();
		} else {
			getMyLocation();
		}
	}

	public void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("\nGPS가 켜져있지 않습니다. \nGPS를 활성화 하시습니까? \n")
				.setCancelable(false)
				.setPositiveButton("활성화",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								showGpsOptions();
							}
						})
				.setNegativeButton("닫기", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showGpsOptions() {
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	private LocationManager manager;
	private boolean locationFlag = false;

	private void getMyLocation() {
		if (manager == null) {
			manager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}

		// 10초 / 최소한 얼마만의 시간이 흐른후 위치정보를 받을건지 시간간격을 설정 설정하는 변수
		long minTime = 10000;

		// 거리는 0 / 얼마만의 거리가 떨어지면 위치정보를 받을건지 설정하는 변수
		float minDistance = 0;

		MyLocationListener listener = new MyLocationListener();

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, listener);

	}

	public double latitude;
	public double longitude;

	class MyLocationListener implements LocationListener {

		// 위치정보는 아래 메서드를 통해서 전달된다.
		@Override
		public void onLocationChanged(Location location) {

			Log.e(TAG, "onLocationChanged()가 호출되었습니다");

			if (!locationFlag) {

				latitude = location.getLatitude();
				longitude = location.getLongitude();
				String msg = "현재 위치 \n위도 : " + latitude + " \n경도 : "
						+ longitude;
				Util.getToast(mActivity, msg);

				locationFlag = true;
				btn_map.setEnabled(true);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}

	public void setAlarmInit() {
		for (int i = 0; i < Const.alarmArr.length(); i++) {
			try {
				JSONObject jObj = Const.alarmArr.getJSONObject(i);
				if (jObj != null) {
					Alarm mAlarm = new Alarm(mActivity);
					mAlarm.releaseAlarm(i);
					Const.alarmArr.put(i, null);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Const.alarmArr = new JSONArray();
		mUtil.setFileWriteData(Const.alarmArr);
		Const.alarmArr = mUtil.getFileReadData();
		Const.alarmArrLength = Const.alarmArr.length();
	}

	public void Init() {

		arContactList = new ArrayList<Contact>();

		listadapter = new ListAdapter(mActivity);
		list.setAdapter(listadapter);

	}

	private ArrayList<Contact> getContactList() {

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {

		ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보
															// 가져오는데 사용

				ContactsContract.CommonDataKinds.Phone.NUMBER, // 연락처

				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }; // 연락처
																		// 이름.

		String[] selectionArgs = null;

		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME

		+ " COLLATE LOCALIZED ASC";

		Cursor contactCursor = managedQuery(uri, projection, null,

		selectionArgs, sortOrder);

		ArrayList<Contact> contactlist = new ArrayList<Contact>();

		if (contactCursor.moveToFirst()) {

			do {

				String phonenumber = contactCursor.getString(1).replaceAll("-",
						"");

				phonenumber = phonenumber.replaceAll("[^0-9]", "");

				if (phonenumber.length() == 10) {

					phonenumber = phonenumber.substring(0, 3) + "-"

					+ phonenumber.substring(3, 6) + "-"

					+ phonenumber.substring(6);

				} else if (phonenumber.length() > 8) {

					phonenumber = phonenumber.substring(0, 3) + "-"

					+ phonenumber.substring(3, 7) + "-"

					+ phonenumber.substring(7);

				}

				Contact acontact = new Contact();

				acontact.setPhotoid(contactCursor.getLong(0));

				acontact.setPhonenum(phonenumber);

				acontact.setName(contactCursor.getString(2));

				contactlist.add(acontact);

			} while (contactCursor.moveToNext());

		}

		return contactlist;

	}

	public class Contact {
		long photoid;
		String phonenum;
		String name;

		public long getPhotoid() {
			return photoid;
		}

		public void setPhotoid(long photoid) {
			this.photoid = photoid;
		}

		public String getPhonenum() {
			return phonenum;
		}

		public void setPhonenum(String phonenum) {
			this.phonenum = phonenum;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@Override
	protected void onUserLeaveHint() {
		// 여기서 감지
		Log.d(TAG, "Home Button Touch");
		super.onUserLeaveHint();
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (intent != null) {
			Uri uriData = getIntent().getData();
			String photoNumber = uriData.getQueryParameter("param1 ");

			Log.e(TAG, " photoNumber : " + photoNumber);
		}
	}

}