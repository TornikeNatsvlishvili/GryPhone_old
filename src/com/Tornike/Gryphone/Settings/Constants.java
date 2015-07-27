package com.Tornike.Gryphone.Settings;

public class Constants {
	// Setting Names
	public static final String PREFS_NAME = "GryPhonePrefs";
	
	public static final String SETTING_SCHEDULE_NOTFICATION = "setting_sched_notif";
	public static final String SETTING_SCHEDULE_REMINDER = "setting_sched_reminder";
	public static final String SETTING_SCHEDULE_FIRST_USE = "setting_sched_first_use";

	public static final String SETTING_MAIN_INTERNET_WARNING = "main_internet_warn";
	
	public static final String SETTING_GENERAL_FIRST_RUN = "general_first_run";
	public static final String SETTING_MOST_CURRENT_VERSION = "current_version";
	
	public static final String SETTING_LAST_GLOBAL_MESSAGE_SHOWN = "last_global_message_shown";

	public static enum SETTING_SCHE_NOTIF_OPTIONS {
		sound_vibrate, vibrate, none, disable;
		
		public static int getIndex(String i){
			int c = 0;
			for(SETTING_SCHE_NOTIF_OPTIONS item:SETTING_SCHE_NOTIF_OPTIONS.values()){
				if(item.toString().equals(i)){
					return c; 
				}
				c++;
			}
			return -1;
		}
	};
}
