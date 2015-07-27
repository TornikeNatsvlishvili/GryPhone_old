package com.Tornike.Gryphone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.Tornike.Gryphone.Bus.BusActivity;
import com.Tornike.Gryphone.CourseSchedule.CourseScheduleActivity;
import com.Tornike.Gryphone.Emergency.EmergencyActivity;
import com.Tornike.Gryphone.Events.Campus.CampusEventsActivity;
import com.Tornike.Gryphone.Events.Students.StudentEventsActivity;
import com.Tornike.Gryphone.Library.LibraryActivity;
import com.Tornike.Gryphone.Maps.MapsActivity;
import com.Tornike.Gryphone.News.NewsActivity;
import com.Tornike.Gryphone.Notes.NoteActivity;
import com.Tornike.Gryphone.Settings.Constants;
import com.Tornike.Gryphone.Settings.SettingActivity;
import com.Tornike.Gryphone.WebViewer.WebViewerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GryphoneActivity extends Activity implements OnClickListener {
	// ANDROID
	Resources res; // Resource object to get Drawables

	// Views
	GridView mainGrid;
	Gallery mainGallery;
	LinearLayout moreOptions;
	Button campusEvents, studentEvents, courseLink, webAdvisor, gryphMail, csaBtn;

	// Dialog
	AlertDialog aboutAlert, emailTypeDialog, whatsNewDialog, supportMeDialog, globalMessageDialog;

	// Grid vars
	MainGridAdapter gridAdapter;

	// Variables
	Intent intent;
	static boolean slidingBoxOpened = false;
	boolean lastBackPress = false;
	boolean isNetworkAvailable = false;
	boolean hasInternetDialogBeenOpen = false;
	boolean firstRun = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Adapter initilize
		gridAdapter = new MainGridAdapter(this);

		// View initialize
		mainGrid = (GridView) findViewById(R.id.main_grid);
		moreOptions = (LinearLayout) findViewById(R.id.main_sliding_content);
		campusEvents = (Button) findViewById(R.id.main_campus_eventsbtn);
		studentEvents = (Button) findViewById(R.id.main_student_eventsbtn);
		courseLink = (Button) findViewById(R.id.main_courselinkBtn);
		webAdvisor = (Button) findViewById(R.id.main_webadvisorBtn);
		gryphMail = (Button) findViewById(R.id.main_gryphmailBtn);
		csaBtn = (Button) findViewById(R.id.main_csaBtn);

		courseLink.setOnClickListener(this);
		webAdvisor.setOnClickListener(this);
		gryphMail.setOnClickListener(this);
		csaBtn.setOnClickListener(this);

		mainGrid.setAdapter(gridAdapter);

		mainGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				// if (position == 4) {
				// Toast.makeText(GryphoneActivity.this, "Coming Soon!",
				// Toast.LENGTH_SHORT).show();
				// return;
				// }

				// if no internet
				if (!isNetworkAvailable && position != 7 && position != 8 && position != 9 && position != 10) {

					Toast.makeText(GryphoneActivity.this, "Requires internet connection!", Toast.LENGTH_SHORT).show();
					return;
				}

				// Close any opened boxes
				if (slidingBoxOpened == true) {
					slidingBoxOpened = false;
					moreOptions.setVisibility(View.INVISIBLE);
				}
				// Grid listeners
				if (position == 0) {
					intent = new Intent(GryphoneActivity.this, NewsActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 1) {
					slidingBoxOpened = true;
					moreOptions.setVisibility(View.VISIBLE);
					moreOptions.setAnimation(getSlideUpAnimation());
				} else if (position == 2) {
					intent = new Intent(GryphoneActivity.this, MapsActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 3) {
					Intent intent = new Intent(GryphoneActivity.this, CourseScheduleActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 4) {
					intent = new Intent(GryphoneActivity.this, BusActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 5) {
					Intent intent = new Intent(GryphoneActivity.this, WebViewerActivity.class);
					intent.putExtra("url", "http://www.uoguelph.ca/directory/");
					intent.putExtra("title", "Directory");
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 6) {
					intent = new Intent(GryphoneActivity.this, LibraryActivity.class);
					GryphoneActivity.this.startActivity(intent);
					// } else if (position == 9) {
					// // food menu
				} else if (position == 7) {
					intent = new Intent(GryphoneActivity.this, NoteActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 8) {
					intent = new Intent(GryphoneActivity.this, EmergencyActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 9) {
					intent = new Intent(GryphoneActivity.this, SettingActivity.class);
					GryphoneActivity.this.startActivity(intent);
				} else if (position == 10) {
					createAbout();
				}
			}
		});
		// disable scrolling
		mainGrid.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					isNetworkAvailable = isNetworkAvailable();

					if (slidingBoxOpened) {
						slidingBoxOpened = false;
						moreOptions.setAnimation(getSlideDownAnimation());
						moreOptions.setVisibility(View.INVISIBLE);
					}
				}
				return false;
			}
		});

		campusEvents.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(GryphoneActivity.this, CampusEventsActivity.class);
				GryphoneActivity.this.startActivity(intent);

				slidingBoxOpened = false;
				moreOptions.setAnimation(getSlideDownAnimation());
				moreOptions.setVisibility(View.INVISIBLE);
			}
		});

		studentEvents.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(GryphoneActivity.this, StudentEventsActivity.class);
				GryphoneActivity.this.startActivity(intent);

				slidingBoxOpened = false;
				moreOptions.setAnimation(getSlideDownAnimation());
				moreOptions.setVisibility(View.INVISIBLE);
			}
		});

		// Routine
		isNetworkAvailable = isNetworkAvailable();
		loadDefaultSettings();

		// Add items
		gridAdapter.addItem("News", R.drawable.main_news, false);
		gridAdapter.addItem("Events", R.drawable.main_events, false);
		gridAdapter.addItem("Map", R.drawable.main_map, false);
		gridAdapter.addItem("Schedule", R.drawable.main_schedule, false);
		gridAdapter.addItem("Buses", R.drawable.main_bus, false);
		gridAdapter.addItem("Directory", R.drawable.main_directory, false);
		gridAdapter.addItem("Library", R.drawable.main_library, false);
		// gridAdapter.addItem("Food Menu", R.drawable.main_food_menu, false);
		gridAdapter.addItem("Notes", R.drawable.main_notes, false);
		gridAdapter.addItem("Emergency", R.drawable.main_emergency, false);
		gridAdapter.addItem("Settings", R.drawable.main_options, false);
		gridAdapter.addItem("About", R.drawable.main_about, false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		isNetworkAvailable = isNetworkAvailable();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (aboutAlert != null) {
			aboutAlert.dismiss();
		}
		if (emailTypeDialog != null) {
			emailTypeDialog.dismiss();
		}
		if (whatsNewDialog != null) {
			whatsNewDialog.dismiss();
		}
		if (supportMeDialog != null) {
			supportMeDialog.dismiss();
		}
	}

	public Animation getSlideUpAnimation() {
		Animation temp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		temp.setDuration(500);
		temp.setInterpolator(new AccelerateInterpolator(1.0f));
		return temp;
	}

	public Animation getSlideDownAnimation() {
		Animation temp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		// set animation durations
		temp.setDuration(500);
		temp.setInterpolator(new AccelerateInterpolator(1.0f));
		return temp;
	}

	@Override
	public void onBackPressed() {
		if (slidingBoxOpened) {
			slidingBoxOpened = false;
			moreOptions.setAnimation(getSlideDownAnimation());
			moreOptions.setVisibility(View.INVISIBLE);
			return;
		} else {
			finish();
			return;
		}
	}

	public void createAbout() {
		AlertDialog.Builder aboutMessage = new AlertDialog.Builder(this);
		String phrase = "<b>GryPhone</b><br>" + "<b><font color=\"#ff0000\">Version: " + GryphoneActivity.this.getString(R.string.version) + "</font></b><br>" 
				+ "Created by Tornike Natsvlishvili<br><br>"
				+ "Thanks to NEXTbus for bus predictions.<br><br>"
				+ "Thanks to Double J-Design for some icons.<br><br>" + "Thanks to the University of Guelph for some of the information displayed.<br><br>"
				+ "Thanks to University of Guelph Athletic department for the Gryphon icon.<br><br>" + "<b>Tornike Natsvlishvili © 2012</b>";
		Spannable sPhrase = (Spannable) Html.fromHtml(phrase);
		phrase = Html.fromHtml(phrase).toString();
		aboutMessage.setMessage(sPhrase);
		aboutAlert = aboutMessage.create();

		aboutAlert.setButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// No action Required
			}
		});

		aboutAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Facebook", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				try {
					GryphoneActivity.this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/326804520685540")));
				} catch (Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/GryPhone/326804520685540")));
				}
			}
		});
		aboutAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Twitter", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=tornikenatsvli"));
					startActivity(intent);
				} catch (Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/tornikenatsvli")));
				}
			}
		});
		aboutAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "E-mail Me", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final CharSequence[] items = { "Question", "Suggestion", "Talk" };

				AlertDialog.Builder builder = new AlertDialog.Builder(GryphoneActivity.this);
				builder.setTitle("Topic");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("text/plain");
						i.putExtra(Intent.EXTRA_EMAIL, new String[] { "tornikenatsvlishvilideveloper@gmail.com" });
						if (item == 0) {
							i.putExtra(Intent.EXTRA_SUBJECT, "GryPhone - " + GryphoneActivity.this.getString(R.string.version) + " - Question");
						} else if (item == 1) {
							i.putExtra(Intent.EXTRA_SUBJECT, "GryPhone - " + GryphoneActivity.this.getString(R.string.version) + " - Suggestion");
						} else {
							i.putExtra(Intent.EXTRA_SUBJECT, "GryPhone - " + GryphoneActivity.this.getString(R.string.version) + " - Talk");
						}
						GryphoneActivity.this.startActivity(i);
					}
				});
				emailTypeDialog = builder.create();
				emailTypeDialog.show();
			}
		});

		aboutAlert.show();
		TextView temp = (TextView) aboutAlert.findViewById(android.R.id.message);
		temp.setTextSize(12);
		temp.setGravity(Gravity.CENTER);
	}

	public void loadDefaultSettings() {
		// Get Version info
		String currentAppVersion;
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			currentAppVersion = pInfo.versionName;
		} catch (NameNotFoundException e) {
			currentAppVersion = getString(R.string.version);
		}

		// Setting Init
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		editor = settings.edit();

		// Check if its first app launch
		// firstRun = settings.getBoolean(Constants.SETTING_GENERAL_FIRST_RUN,
		// true);
		String lastRunVersion = settings.getString(Constants.SETTING_MOST_CURRENT_VERSION, "setting_not_found");

		double lastRunVersionDouble = 0;
		double appVersionDouble = 0;
		if (!lastRunVersion.equals("setting_not_found")) {
			try {
				lastRunVersionDouble = Double.parseDouble(lastRunVersion);
				appVersionDouble = Double.parseDouble(currentAppVersion);
			} catch (NumberFormatException e) {
				// Do nothing bad version format
			}
		}

		// Load defaults since this will be the very first launch
		if (appVersionDouble > lastRunVersionDouble || lastRunVersion.equals("setting_not_found")) {
			firstRun = true;

			AlertDialog.Builder whatsNewMessage = new AlertDialog.Builder(this);
			String phrase = "<font color=\"#ff0000\"><b>GryPhone</b></font><br>" + "Version: " + GryphoneActivity.this.getString(R.string.version)
					+ "<br><br><font color=\"#00ffff\">Changes in this version:</font>" + "<br>----------------------" + "<br>! Fixed campus events crash"
					+ "<br>! Fixed add event parsing" + "<br>! Fixed bus routes" + "<br><br><font color=\"#00ffff\">Coming Soon:</font>"
					+ "<br>-----------------------" + "<br>1) Athletics! Go Gryphs :)" + "<br>2) Send me your suggestions from the 'About' tab!"
					+ "<br><br><font color=\"#00ffff\"><b>Please Rate/Review app on the Market! Enjoy :)</b></font>"
					+ "<br><br>"
					+ "<br><br>Tornike Natsvlishvili © 2012";
			whatsNewMessage.setMessage(Html.fromHtml(phrase));
			whatsNewMessage.setTitle("Update!");
			whatsNewMessage.setNegativeButton("Follow me on Twitter :)", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=tornikenatsvli"));
						startActivity(intent);
					} catch (Exception e) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/tornikenatsvli")));
					}
					
					if (!isNetworkAvailable) {
						openNoInternetMessage();
					}
				}
			});

			whatsNewDialog = whatsNewMessage.create();
			whatsNewDialog.show();
			((TextView) whatsNewDialog.findViewById(android.R.id.message)).setTextSize(12);

			editor.putString(Constants.SETTING_MOST_CURRENT_VERSION, currentAppVersion);

			// Set this to today so user doesn't get the update! message and
			// another one
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
			editor.putString(Constants.SETTING_LAST_GLOBAL_MESSAGE_SHOWN, dateFormatter.format(new Date()));

			editor.commit();
		} else {
			if (!isNetworkAvailable) {
				openNoInternetMessage();
			}
		}

		if (firstRun) {
			// Set Default Settings
			editor.putBoolean(Constants.SETTING_GENERAL_FIRST_RUN, false);
			editor.putString(Constants.SETTING_SCHEDULE_NOTFICATION, Constants.SETTING_SCHE_NOTIF_OPTIONS.vibrate.toString());
			editor.putBoolean(Constants.SETTING_MAIN_INTERNET_WARNING, true);
			editor.putInt(Constants.SETTING_SCHEDULE_REMINDER, 10);

			editor.commit();
		}

		if (!firstRun) {
			new GetWWMessage(this).execute();
		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public void openNoInternetMessage() {
		final SharedPreferences settings;
		final SharedPreferences.Editor editor;
		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		editor = settings.edit();

		boolean shouldShow = settings.getBoolean(Constants.SETTING_MAIN_INTERNET_WARNING, true);

		if (!isNetworkAvailable && !hasInternetDialogBeenOpen && shouldShow) {
			AlertDialog.Builder wifiOnDialog = new AlertDialog.Builder(this);

			final CheckBox dontShowAgain = new CheckBox(this);
			dontShowAgain.setText(" Don't show again");
			LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
			linearLayout.setOrientation(1);
			linearLayout.addView(dontShowAgain);
			linearLayout.setPadding(15, 0, 0, 0);

			wifiOnDialog.setTitle("No Network Connection");
			wifiOnDialog.setView(linearLayout);
			wifiOnDialog.setMessage("Cannot connect to Internet. Please check your connection settings and try again.");
			wifiOnDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dontShowAgain.isChecked()) {
						editor.putBoolean(Constants.SETTING_MAIN_INTERNET_WARNING, false);
						editor.commit();
					}
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
			});
			wifiOnDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dontShowAgain.isChecked()) {
						editor.putBoolean(Constants.SETTING_MAIN_INTERNET_WARNING, false);
						editor.commit();
					}
				}
			});
			wifiOnDialog.show();
			hasInternetDialogBeenOpen = true;
		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		Intent intent = new Intent(GryphoneActivity.this, WebViewerActivity.class);

		switch (viewId) {
		case R.id.main_sliding_content:
			// So uyou can't click behind the slideup
			break;
		case R.id.main_campus_eventsbtn:
			intent = new Intent(GryphoneActivity.this, CampusEventsActivity.class);
			GryphoneActivity.this.startActivity(intent);

			slidingBoxOpened = false;
			moreOptions.setAnimation(getSlideDownAnimation());
			moreOptions.setVisibility(View.INVISIBLE);
			break;
		case R.id.main_student_eventsbtn:
			intent = new Intent(GryphoneActivity.this, StudentEventsActivity.class);
			GryphoneActivity.this.startActivity(intent);

			slidingBoxOpened = false;
			moreOptions.setAnimation(getSlideDownAnimation());
			moreOptions.setVisibility(View.INVISIBLE);
			break;
		case R.id.main_courselinkBtn:
			intent.putExtra("url", "https://sso.identity.uoguelph.ca/amserver/UI/Login?goto=https%3A%2F%2Fapps.identity.uoguelph.ca%3A443%2Fsso_d2l%2Flogin");
			intent.putExtra("title", "CourseLink");
			GryphoneActivity.this.startActivity(intent);
			break;
		case R.id.main_webadvisorBtn:
			intent.putExtra("url", "https://webadvisor.uoguelph.ca/WebAdvisor/WebAdvisor?TOKENIDX=86953431&SS=LGRQ");
			intent.putExtra("title", "WebAdvisor");
			GryphoneActivity.this.startActivity(intent);
			break;
		case R.id.main_gryphmailBtn:
			intent.putExtra("url", "https://mail.uoguelph.ca/zimbra/?client=mobile");
			intent.putExtra("title", "GryphMail");
			GryphoneActivity.this.startActivity(intent);
			break;
		case R.id.main_csaBtn:
			intent.putExtra("url", "http://www.csaonline.ca/");
			intent.putExtra("title", "CSA");
			GryphoneActivity.this.startActivity(intent);
			break;
		}
	}

	// Get a world wide message from a unique url
	private class GetWWMessage extends AsyncTask<Void, Void, String[]> {
		Context activityContext;

		public GetWWMessage(Context context) {
			activityContext = context;
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String text = "";
			try {
				URL url = new URL("http://pastebin.com/raw.php?i=GNbn8DKt");

				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String str;
				while ((str = in.readLine()) != null) {
					text += str;
				}
				in.close();
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
			return text.split("%%");
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result.length != 3)
				return;

			SharedPreferences settings;
			SharedPreferences.Editor editor;
			settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			editor = settings.edit();

			// Check if this message is differnet then the last one
			if (settings.getString(Constants.SETTING_LAST_GLOBAL_MESSAGE_SHOWN, "").equals(result[0] + "%%" + result[1] + "%%" + result[2]))
				return;

			String message = result[2];
			String title = result[1];
			int type = Integer.parseInt(result[0].replace("\n", ""));

			AlertDialog.Builder globalMessage = new AlertDialog.Builder(activityContext);
			globalMessage.setMessage(Html.fromHtml(message));
			globalMessage.setTitle(title);

			globalMessageDialog = globalMessage.create();

			if (type == 1) {
				globalMessageDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
			} else if (type == 2) {
				globalMessageDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Rate/Comment", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.Tornike.Gryphone"));
						startActivity(intent);
					}
				});
			} else if (type == 3) {
				globalMessageDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Facebook", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						try {
							activityContext.getPackageManager().getPackageInfo("com.facebook.katana", 0);
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/326804520685540")));
						} catch (Exception e) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/GryPhone/326804520685540")));
						}
					}
				});
				globalMessageDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Twitter", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=tornikenatsvli"));
							startActivity(intent);
						} catch (Exception e) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/tornikenatsvli")));
						}
					}
				});
			}

			globalMessageDialog.show();
			((TextView) globalMessageDialog.findViewById(android.R.id.message)).setTextSize(12);

			// Save the last time a message was shown
			editor.putString(Constants.SETTING_LAST_GLOBAL_MESSAGE_SHOWN, result[0] + "%%" + result[1] + "%%" + result[2]);
			editor.commit();
		}
	}
}
