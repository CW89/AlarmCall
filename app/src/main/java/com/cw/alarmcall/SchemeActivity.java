package com.cw.alarmcall;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SchemeActivity extends Activity {

	public static String TAG = SchemeActivity.class.getSimpleName();

	private Button btn_addShortCut;
	SchemeActivity mActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scheme);
		Log.e(TAG, " onCreate() ");
		mActivity = this;

		btn_addShortCut = (Button) findViewById(R.id.btn_addShortCut);
		btn_addShortCut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// addShortcut();
				addCurrentPageShortcut();
			}
		});
	}

	private void addShortcut() {
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		shortcutIntent.setClassName(this, getClass().getName());
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getResources().getString(R.string.app_name));
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.ic_launcher));
		intent.putExtra("duplicate", false);
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

		sendBroadcast(intent);
	}

	private void addCurrentPageShortcut() {
		final Intent shortcutIntent = new Intent(this, SchemeActivity.class);

		final Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// Sets the custom shortcut's title
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.shortcut));
		// Set the custom shortcut icon
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.ic_launcher));
		// add the shortcut
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(intent);

	}

	
}
