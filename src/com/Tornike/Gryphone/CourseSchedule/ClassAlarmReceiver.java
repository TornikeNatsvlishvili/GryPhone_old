package com.Tornike.Gryphone.CourseSchedule;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Settings.Constants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

public class ClassAlarmReceiver extends BroadcastReceiver {

	String classTitle = "", classLocation = "", classRoomNum = "";
	String scheduleNotifSyle = "";
	int classID = -1;
	String scheduleReminderMinsFormated = "";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		if (b != null) {
			classTitle = intent.getExtras().getString("className");
			classLocation = intent.getExtras().getString("classLoc");
			classRoomNum = intent.getExtras().getString("classRoomNum");
			classID = intent.getExtras().getInt("classID");
		}

		readSettings(context);

		if (!scheduleNotifSyle
				.equals(Constants.SETTING_SCHE_NOTIF_OPTIONS.disable.toString())) {
			NotificationManager manger = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(
					R.drawable.schedule_notification_ic_class,
					"Go to class!",
					System.currentTimeMillis());

			Intent recievingIntent = new Intent(context,
					CourseScheduleActivity.class);

			recievingIntent.putExtra("fromNotification", true);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					recievingIntent, 0);

			if (classRoomNum.equals("-1")) {
				notification.setLatestEventInfo(context, classTitle
						+ " class starts in "+scheduleReminderMinsFormated+"!", "Building: " + classLocation,
						contentIntent);
			} else {
				notification.setLatestEventInfo(context, classTitle
						+ " class started in "+scheduleReminderMinsFormated+"!", "Building: " + classLocation
						+ " @ Room: " + classRoomNum, contentIntent);
			}

			notification.flags = Notification.FLAG_INSISTENT
					| Notification.FLAG_AUTO_CANCEL;

			Uri sound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if (sound == null) {
				sound = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
			if (scheduleNotifSyle
					.equals(Constants.SETTING_SCHE_NOTIF_OPTIONS.sound_vibrate
							.toString())) {

				notification.sound = sound;
				notification.defaults = Notification.DEFAULT_LIGHTS
						| notification.DEFAULT_VIBRATE;
			}

			if (scheduleNotifSyle
					.equals(Constants.SETTING_SCHE_NOTIF_OPTIONS.vibrate
							.toString())) {

				notification.defaults = Notification.DEFAULT_LIGHTS
						| notification.DEFAULT_VIBRATE;
			}
			
			manger.notify(classID, notification);
		}

	}

	public void readSettings(Context c) {
		SharedPreferences settings;
		settings = c.getSharedPreferences(Constants.PREFS_NAME, 0);
		scheduleNotifSyle = settings.getString(
				Constants.SETTING_SCHEDULE_NOTFICATION,
				Constants.SETTING_SCHE_NOTIF_OPTIONS.vibrate.toString());
		int scheduleReminderMins = settings.getInt(
				Constants.SETTING_SCHEDULE_REMINDER, 10);
		if (scheduleReminderMins != 60) {
			scheduleReminderMinsFormated = scheduleReminderMins + " minutes";
		} else {
			scheduleReminderMinsFormated = "1 hour";
		}
	}
}