package com.cw.alarmcall;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Util {
	
	public static String TAG = Util.class.getSimpleName();
	/**
	 * 파일 저장
	 */
	public static final String FILE_NAME = "admin.json";
	public static final String DATE_NAME = "admin_date.json";
	public static Context context;
	public Util(Context context) {
		super();
		this.context = context;
	}
	public static void getToast(Context mContext, String msg) {
		Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/**
	 * 뱃지 카운트 증가 
	 * 
	 * @param context
	 */
	public static void updateIconBadgeCount(Context context, int count)
	{
		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
		// 패키지 네임과 클래스 네임 설정
		
		String packageName = ((Activity)context).getComponentName().getPackageName();
		String className = packageName +"."+MainActivity.class.getSimpleName();
		

		className = MainActivity.class.getName();
		Log.e(TAG, "  packageName : " + packageName );
		Log.e(TAG, "  className : " + className );
		
		intent.putExtra("badge_count_package_name", packageName);
		intent.putExtra("badge_count_class_name", className);

		// 업데이트 카운트
		intent.putExtra("badge_count", count);
		// Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
	    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
	        intent.setFlags(0x00000020);
	    }
		context.sendBroadcast(intent);
	}
	
	public static void setFileWriteData(JSONArray jArr) {
		JSONArray arr = new JSONArray();
		try {

			arr = jArr;

			File file = new File(context.getFilesDir(), FILE_NAME);

			Log.e(TAG, "Path : " + context.getFilesDir());
			if (!file.exists()) {
				file.createNewFile();
				file.mkdirs();
			}
			PrintWriter fw = new PrintWriter(file);

			fw.write(arr.toString());
			Log.e(TAG, "write : " + arr.toString() + "    ");

			fw.close();
		} catch (Exception e) {
			Log.e(TAG, "message : " + e);
		}
	}

	/**
	 * 파일 읽기
	 */
	public static JSONArray getFileReadData() {
		JSONArray arr = new JSONArray();
		try {
			File file = new File(context.getFilesDir(), FILE_NAME);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String strConcat = "";
			String str = "";
			while ((str = br.readLine()) != null) {
				strConcat += str + "\n";
			}

			arr = new JSONArray(strConcat);

			br.close();

		} catch (Exception e) {
			Log.e(TAG, "message : " + e);

		}

		return arr;
	}
	
	
	public static void setFileWriteDate(JSONObject jObj) {
		
		try {



			File file = new File(context.getFilesDir(), DATE_NAME);

			Log.e(TAG, "Path : " + context.getFilesDir());
			if (!file.exists()) {
				file.createNewFile();
				file.mkdirs();
			}
			PrintWriter fw = new PrintWriter(file);

			fw.write(jObj.toString());
			Log.e(TAG, "write : " + jObj.toString() + "    ");

			fw.close();
		} catch (Exception e) {
			Log.e(TAG, "message : " + e);
		}
	}


	public static JSONObject getFileReadDate() {
		JSONObject jObj = new JSONObject();
		try {
			File file = new File(context.getFilesDir(), DATE_NAME);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String strConcat = "";
			String str = "";
			while ((str = br.readLine()) != null) {
				strConcat += str + "\n";
			}

			jObj = new JSONObject(strConcat);

			br.close();

		} catch (Exception e) {
			Log.e(TAG, "message : " + e);

		}

		return jObj;
	}
}