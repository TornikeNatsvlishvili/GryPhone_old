package com.Tornike.Gryphone.Events.Campus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Bus.BusActivity;
import com.Tornike.Gryphone.Events.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CampusEventsActivity extends Activity {
	// Views
	GridView gridView;
	RelativeLayout progressBarLayout;
	TextView topText;
	ImageView topButton;
	TextView curMonthTxt;
	ImageButton nextMonthBtn, lastMonthBtn;

	// Calendar
	Calendar cal;

	// Adapter stuff
	static CampusEventAdapter eventAdapter;

	// Variables
	String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	int currentMonth, currentYear, currentDay;
	int realMonth, realYear;
	boolean timingCorrection = false;
	boolean taskRunning = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_campus_layout);

		// Date Initialization
		cal = Calendar.getInstance(Locale.getDefault());
		currentMonth = cal.get(Calendar.MONTH);
		currentYear = cal.get(Calendar.YEAR);
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		realMonth = currentMonth;
		realYear = currentYear;

		// List stuff initialization

		// View initilization
		gridView = (GridView) findViewById(R.id.events_grid_view);
		progressBarLayout = (RelativeLayout) findViewById(R.id.event_campus_loadingbar);
		curMonthTxt = (TextView) findViewById(R.id.top_bar_page_number_text);
		topText = (TextView) findViewById(R.id.top_bar_text);
		nextMonthBtn = (ImageButton) findViewById(R.id.top_bar_next);
		lastMonthBtn = (ImageButton) findViewById(R.id.top_bar_last);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);

		// Download events
		eventAdapter = new CampusEventAdapter(this, R.layout.events_grid_item);
		new DownloadEventTask().execute(formatEventUrl(currentYear,
				currentMonth));

		topText.setText("Events");
		gridView.setAdapter(eventAdapter);

		// View listeners
		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CampusEventDay eventDay = eventAdapter.getItem(arg2);
				Intent enlargeEventIntent = new Intent(
						CampusEventsActivity.this, EventEnlargedActivity.class);
				enlargeEventIntent.putExtra("size",
						eventDay.getAmountOfEventItems());
				for (int a = 0; a < eventDay.getAmountOfEventItems(); a++) {
					CampusEventItem tempItem = eventDay.getEventItem(a);
					enlargeEventIntent.putExtra("title" + a,
							tempItem.getTitle());
					enlargeEventIntent.putExtra("date" + a,
							tempItem.getEventDate());
					enlargeEventIntent.putExtra("description" + a,
							tempItem.getDescription());
					enlargeEventIntent.putExtra("url" + a, tempItem.getURL());
				}
				startActivity(enlargeEventIntent);
			}
		});

		nextMonthBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				eventAdapter.clear();
				currentMonth++;
				if (currentMonth == 12) {
					currentMonth = 0;
					currentYear++;
				}
				curMonthTxt.setText(months[currentMonth] + ",\n"
						+ (currentYear + ""));
				new DownloadEventTask().execute(formatEventUrl(currentYear,
						currentMonth));
				lastMonthBtn.setEnabled(true);
				lastMonthBtn.getBackground().setColorFilter(
						Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
			}
		});

		lastMonthBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				eventAdapter.clear();
				currentMonth--;
				if (currentMonth == -1) {
					currentMonth = 11;
					currentYear--;
				}
				curMonthTxt.setText(months[currentMonth] + ",\n"
						+ (currentYear + ""));
				new DownloadEventTask().execute(formatEventUrl(currentYear,
						currentMonth));
				if (realYear == currentYear && realMonth == currentMonth) {
					lastMonthBtn.setEnabled(false);
					lastMonthBtn.getBackground().setColorFilter(
							Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				} else {
					lastMonthBtn.setEnabled(true);
					lastMonthBtn.getBackground().setColorFilter(
							Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (taskRunning) {
			progressBarLayout.setAnimation(getFadeOutAnimation());
			progressBarLayout.setVisibility(progressBarLayout.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		configureTopViews();

		// One load per resume
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("firstTimeSecondOrder", true);
		editor.commit();
	}

	protected class DownloadEventTask extends
			AsyncTask<String, CampusEventDay, String> {
		@Override
		protected void onPreExecute() {
			eventAdapter.clear();
			progressBarLayout.setVisibility(progressBarLayout.VISIBLE);
			progressBarLayout.setAnimation(getFadeInAnimation());
			taskRunning = true;
		}

		@Override
		protected String doInBackground(String... params) {
			String temp = "";
			try {
				temp = getPureHtml(params[0]);
			} catch (Exception e) {
				Log.e("Error", e.toString());
			}
			return params[0];
		}

		@Override
		protected void onProgressUpdate(CampusEventDay... values) {
			super.onProgressUpdate(values);
			eventAdapter.addItem(values[0]);
			timingCorrection = true;
		}

		protected void onPostExecute(String result) {
			if (eventAdapter.getCount() == 0) {
				Toast.makeText(CampusEventsActivity.this,
						"No Events this month!", Toast.LENGTH_LONG).show();
			}
			progressBarLayout.setAnimation(getFadeOutAnimation());
			progressBarLayout.setVisibility(progressBarLayout.INVISIBLE);
			taskRunning = false;
		}

		public String getPureHtml(String url) throws ClientProtocolException,
				IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet, localContext);
			String result = "";

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = null;
			Boolean startRealRead = false;
			int firstIndex = 0, secondIndex = 0;
			while ((line = reader.readLine()) != null) {
				// Start real reading when the first item is found
				if (!startRealRead
						&& line.contains("<h2 class=\"date-header\">")) {
					startRealRead = true;
				}
				if (startRealRead) {
					result += line;
					// Parse what you have so far, if enough for a day is found
					// then parse it
					// Find the day indexes so we can cut it out
					firstIndex = result.indexOf("<h2 class=\"date-header\">",
							secondIndex);
					secondIndex = result.indexOf("<h2 class=\"date-header\">",
							firstIndex + 1);

					if (firstIndex != -1 && secondIndex != -1) {
						// full day info is found
						// Seperate the day's info
						String dayString = result.substring(firstIndex,
								secondIndex);
						// reset Result to nothing
						result = result.substring(secondIndex, result.length());
						parseDay(dayString);
					}
					// If ending can't be found it means it is the final end and
					// search for this string instead
					if (firstIndex != -1 && secondIndex == -1) {
						secondIndex = result.indexOf(
								"<div class=\"clear\"></div>", firstIndex + 1);
						if (secondIndex == -1) {
							continue;
						} else {
							// Seperate the day's info
							String dayString = result.substring(firstIndex,
									secondIndex);
							parseDay(dayString);
							break;
						}
					}
				}
			}
			return result;
		}

		public void parseDay(String phrase) {
			int loc = 0, loc2 = 0, loc3 = 0, loc4 = 0, loc5 = 0, loc6 = 0, monthNum = 0;
			String tempD, tempM, tempY, date, stripResultDay;
			Boolean eof = false;
			CampusEventDay dayItems = new CampusEventDay();

			// Remove newlines,tabs..etc
			stripResultDay = phrase.replace("\n", "");
			stripResultDay = stripResultDay.replace("\t", "");
			stripResultDay = stripResultDay.replace("\r", "");
			stripResultDay = removeRandomSpaces(stripResultDay);

			// Parse Date
			loc = stripResultDay.indexOf("<h2 class=\"date-header\">")
					+ "<h2 class=\"date-header\">".length();
			loc2 = stripResultDay.indexOf("</h2>", loc + 1);
			date = stripResultDay.substring(loc, loc2);

			// EACH LOOP FOR ONE NEWS ITEM
			while (!eof) {
				CampusEventItem tempItem = new CampusEventItem();
				tempItem.setEventDate(date);
				// check if the event already took place
				if (realMonth == currentMonth
						&& tempItem.getDateDay() < currentDay) {
					return;
				}

				// URL
				loc = stripResultDay.indexOf(
						"<a class=\"permalink url\" href=\"", loc4)
						+ "<a class=\"permalink url\" href=\"".length();
				loc2 = stripResultDay.indexOf("\"><span class=\"summary\">",
						loc);
				// END Parsing
				if (loc < loc6) {
					eof = true;
					break;
				}
				tempItem.setURL(stripResultDay.substring(loc, loc2));
				// Title
				loc3 = loc2 + "\"><span class=\"summary\">".length();
				loc4 = stripResultDay.indexOf("</span>", loc3 + 1);
				String tempTitle = stripResultDay.substring(loc3, loc4);
				while (tempTitle.startsWith(" ")) {
					tempTitle = tempTitle.substring(1);
				}
				tempItem.setTitle(tempTitle);
				// Description
				loc5 = stripResultDay.indexOf(
						"<div class=\"entry-body description\">", loc4 + 1)
						+ "<div class=\"entry-body description\">".length();
				loc6 = stripResultDay.indexOf("</div>", loc5 + 1);
				String temp = stripResultDay.substring(loc5, loc6);
				tempItem.setDescription(temp);

				dayItems.addEventItem(tempItem);
			}
			publishProgress(dayItems);
			while (!timingCorrection) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			timingCorrection = false;
		}
	}

	// Format Url so that 9 is 09
	public String formatEventUrl(int year, int month) {
		String formatedUrl;
		int tempMonth = month + 1;
		if (tempMonth < 10) {
			formatedUrl = "http://www.uoguelph.ca/events/" + year + "/0"
					+ tempMonth + "/";
		} else {
			formatedUrl = "http://www.uoguelph.ca/events/" + year + "/"
					+ tempMonth + "/";
		}
		// Log.e("------------",formatedUrl);
		return formatedUrl;
	}

	public String removeRandomSpaces(String phrase) {
		boolean eof = false;
		String formatted = phrase.trim();
		for (int a = 0; a < formatted.length(); a++) {
			if (formatted.charAt(a) == '>') {
				int indx1 = a + 1;
				int indx2 = formatted.indexOf("<", a);
				if (indx2 != -1) {
					boolean erase = true;
					for (int b = indx1; b < indx2; b++) {
						if (formatted.charAt(b) != ' ') {
							erase = false;
							break;
						}
					}
					if (erase) {
						formatted = formatted.substring(0, indx1)
								+ formatted
										.substring(indx2, formatted.length());
					}
				}
			}
		}
		return formatted.toString();
	}

	public Animation getFadeOutAnimation() {
		Animation temp = new AlphaAnimation(1, 0);
		temp.setDuration(500);
		return temp;
	}

	public Animation getFadeInAnimation() {
		Animation temp = new AlphaAnimation(0, 1);
		temp.setDuration(500);
		return temp;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putIntArray("currentDate", new int[] { currentDay,
				currentMonth, currentYear });
		// etc.
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int[] dates = savedInstanceState.getIntArray("currentDate");
		currentDay = dates[0];
		currentMonth = dates[1];
		currentYear = dates[2];
		configureTopViews();
	}

	public void configureTopViews() {
		if (realYear == currentYear && realMonth == currentMonth) {
			lastMonthBtn.setEnabled(false);
			lastMonthBtn.getBackground().setColorFilter(
					Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
		} else {
			lastMonthBtn.setEnabled(true);
			lastMonthBtn.getBackground().setColorFilter(Color.argb(0, 0, 0, 0),
					PorterDuff.Mode.SRC_ATOP);
		}
		nextMonthBtn.setEnabled(true);
		nextMonthBtn.getBackground().setColorFilter(Color.argb(0, 0, 0, 0),
				PorterDuff.Mode.SRC_ATOP);
		curMonthTxt.setText(months[currentMonth] + ",\n" + currentYear);
	}
}