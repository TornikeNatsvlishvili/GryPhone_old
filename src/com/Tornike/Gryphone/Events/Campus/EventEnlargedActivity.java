package com.Tornike.Gryphone.Events.Campus;

import java.util.ArrayList;
import java.util.Calendar;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.Campus.CampusEventsActivity.DownloadEventTask;
import com.Tornike.Gryphone.News.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventEnlargedActivity extends Activity {
	// Views
	TextView topText, topEventCounter;
	ImageButton nextEventBtn, lastEventBtn;
	ImageView topButton;

	// Pager
	static CampusEventPagerAdapter pageAdapter;
	ViewPager viewPager;

	// Variables
	int currentPage = 0;
	int totalPages;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_campus_enlarged_layout);

		// Initialize Views
		topText = (TextView) findViewById(R.id.top_bar_text);
		topEventCounter = (TextView) findViewById(R.id.top_bar_page_number_text);
		nextEventBtn = (ImageButton) findViewById(R.id.top_bar_next);
		lastEventBtn = (ImageButton) findViewById(R.id.top_bar_last);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		viewPager = (ViewPager) findViewById(R.id.campus_event_pager);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("firstTimeSecondOrder", true)) {
			// run your one time code
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTimeSecondOrder", false);
			editor.commit();
			// Pager init
			pageAdapter = new CampusEventPagerAdapter(this);
			// Get event data
			recieveData();
		}

		viewPager.setAdapter(pageAdapter);

		// View Init
		topText.setText("Events");

		// top bar button to go back to events
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lastEventBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				currentPage--;
				viewPager.setCurrentItem(currentPage, true);
			}
		});

		nextEventBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentPage++;
				viewPager.setCurrentItem(currentPage, true);
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				currentPage = viewPager.getCurrentItem();
				topEventCounter.setText("Event\nItem: " + (currentPage + 1)
						+ "/" + totalPages);
				if (viewPager.getCurrentItem() == 0) {
					lastEventBtn.setEnabled(false);
					lastEventBtn.getBackground().setColorFilter(
							Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				} else {
					lastEventBtn.setEnabled(true);
					lastEventBtn.getBackground().setColorFilter(
							Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				}
				if (viewPager.getCurrentItem() == pageAdapter.getCount() - 1) {
					nextEventBtn.setEnabled(false);
					nextEventBtn.getBackground().setColorFilter(
							Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				} else {
					nextEventBtn.setEnabled(true);
					nextEventBtn.getBackground().setColorFilter(
							Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		configureTopViews();
	}

	public void recieveData() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int amountOfEvents = extras.getInt("size");
			totalPages = amountOfEvents;
			for (int a = 0; a < amountOfEvents; a++) {
				CampusEventItem tempItem = new CampusEventItem();
				tempItem.setTitle(extras.getString("title" + a));
				tempItem.setURL(extras.getString("url" + a));
				tempItem.setDescription(extras.getString("description" + a));
				tempItem.setEventDate(extras.getIntArray("date" + a));
				pageAdapter.addItem(getPagerItem(tempItem), tempItem);
			}
		}
	}

	public RelativeLayout getPagerItem(CampusEventItem eventItem) {
		RelativeLayout layout = (RelativeLayout) ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.events_campus_enlarged_pager_layout, null);

		TextView titleTxt = (TextView) layout
				.findViewById(R.id.event_campus_title_text);
		TextView dateTxt = (TextView) layout
				.findViewById(R.id.event_campus_date_text);
		TextView descriptionTxt = (TextView) layout
				.findViewById(R.id.event_campus_description_text);
		Button addEventBtn = (Button) layout
				.findViewById(R.id.event_campus_add_btn);

		titleTxt.setText(eventItem.getTitle());
		dateTxt.setText(eventItem.getDateDayWords() + " "
				+ eventItem.getDateMonthWords() + " " + eventItem.getDateDay()
				+ ", " + eventItem.getDateYear());
		descriptionTxt.setText(Html.fromHtml(eventItem.getDescription()));

		addEventBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int[] time = parseTime(pageAdapter.getEventItem(currentPage)
						.getDescription());
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, pageAdapter.getEventItem(currentPage)
						.getDateYear());
				cal.set(Calendar.MONTH, pageAdapter.getEventItem(currentPage)
						.getDateMonth() - 1);
				cal.set(Calendar.DAY_OF_MONTH,
						pageAdapter.getEventItem(currentPage).getDateDay());
				if (time[0] != -1) {
					cal.set(Calendar.HOUR_OF_DAY, time[0]);
					cal.set(Calendar.MINUTE, time[1]);
				}

				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				if (time[0] == -1) {
					intent.putExtra("allDay", true);
				} else {
					intent.putExtra("beginTime", cal.getTimeInMillis());
					if (time.length == 3) {
						intent.putExtra("endTime", cal.getTimeInMillis()
								+ time[2] * 60 * 1000);
					} else {
						intent.putExtra("endTime",
								cal.getTimeInMillis() + 60 * 60 * 1000);
					}
				}
				intent.putExtra("title", pageAdapter.getEventItem(currentPage)
						.getTitle());
				intent.putExtra(
						"description",
						Html.fromHtml(
								pageAdapter.getEventItem(currentPage)
										.getDescription()).toString());
				intent.putExtra(
						"eventLocation",
						parseLocation(Html.fromHtml(
								pageAdapter.getEventItem(currentPage)
										.getDescription()).toString()));
				startActivity(intent);
			}
		});
		return layout;
	}

	public int[] parseTime(String phrase) {
		int[] result = new int[3];
		int timeHour = 0, timeMinute = 0, timeStripStart, timeStripEnd, amPmStart;
		String time, pmAm;

		// Time: Noon to 9:00am OR Time: Noon
		if (phrase.contains("Time: Noon")) {
			if (phrase.contains(" to ")) {
				result[0] = 12;
				result[1] = 0;
				timeStripStart = phrase.indexOf("Noon to ")
						+ "Noon to ".length();
				timeStripEnd = phrase.indexOf(".m.", timeStripStart)
						+ ".m.".length();
				time = phrase.substring(timeStripStart, timeStripEnd);
				timeHour = 0;
				timeMinute = 0;
				pmAm = "";
				if (time.contains(":")) {
					int semiLoc, spaceLoc;
					semiLoc = time.indexOf(":");
					timeHour = Integer.parseInt(time.substring(0, semiLoc)
							.replaceAll(" ", ""));
					timeMinute = Integer.parseInt(time.substring(semiLoc + 1,
							time.indexOf(" ", semiLoc)));
				} else {
					int hourEndLoc = time.indexOf(".m.") - 2;
					timeHour = Integer.parseInt(time.substring(0, hourEndLoc)
							.replaceAll(" ", ""));
				}
				amPmStart = time.indexOf(".m.") - 1;
				pmAm = time.substring(amPmStart, time.length());
				if (pmAm.equals("p.m.")) {
					timeHour += 12;
				}
				result[2] = 60 * (timeHour - result[0])
						+ (timeMinute - result[1]);
				return new int[] { 12, 00, result[2] };
			}
			return new int[] { 12, 00 };
			// Time: 9:00am to 10:00 am OR Time: 9:00 am
		} else if (phrase.contains("Time:")) {
			phrase = phrase.substring(phrase.indexOf("Time:"), phrase.indexOf("</p>",phrase.indexOf("Time:")));
			timeStripStart = phrase.indexOf("Time:") + "Time:".length();
			timeStripEnd = phrase.indexOf(".m.") + ".m.".length();
			time = phrase.substring(timeStripStart, timeStripEnd);
			timeHour = 0;
			timeMinute = 0;
			pmAm = "";
			if (time.contains(":")) {
				int semiLoc, spaceLoc;
				semiLoc = time.indexOf(":");
				timeHour = Integer.parseInt(time.substring(0, semiLoc)
						.replaceAll(" ", ""));
				timeMinute = Integer.parseInt(time.substring(semiLoc + 1,
						time.indexOf(" ", semiLoc)));
			} else {
				int hourEndLoc = time.indexOf(".m.") - 2;
				timeHour = Integer.parseInt(time.substring(0, hourEndLoc)
						.replaceAll(" ", ""));
			}
			amPmStart = time.indexOf(".m.") - 1;
			pmAm = time.substring(amPmStart, time.length());
			if (pmAm.equals("p.m.")) {
				timeHour += 12;
			}
			if (phrase.contains(" to ")) {
				result[0] = timeHour;
				result[1] = timeMinute;
				timeStripStart = phrase.indexOf(" to ") + " to ".length();
				timeStripEnd = phrase.indexOf(".m.", timeStripStart)
						+ ".m.".length();
				time = phrase.substring(timeStripStart, timeStripEnd);
				timeHour = 0;
				timeMinute = 0;
				pmAm = "";
				if (time.contains(":")) {
					int semiLoc, spaceLoc;
					semiLoc = time.indexOf(":");
					timeHour = Integer.parseInt(time.substring(0, semiLoc)
							.replaceAll(" ", ""));
					timeMinute = Integer.parseInt(time.substring(semiLoc + 1,
							time.indexOf(" ", semiLoc)));
				} else {
					int hourEndLoc = time.indexOf(".m.") - 2;
					timeHour = Integer.parseInt(time.substring(0, hourEndLoc)
							.replaceAll(" ", ""));
				}
				amPmStart = time.indexOf(".m.") - 1;
				pmAm = time.substring(amPmStart, time.length());
				if (pmAm.equals("p.m.")) {
					timeHour += 12;
				}
				result[2] = 60 * (timeHour - result[0])
						+ (timeMinute - result[1]);
				return result;
			} else {
				return new int[] { timeHour, timeMinute };
			}
		} else {
			return new int[] { -1 };
		}
	}

	// Location: Macdonald Stewart Art Centre
	public String parseLocation(String phrase) {
		if (phrase.contains("Location:")) {
			String loc = phrase.substring(phrase.indexOf("Location:")
					+ "Location:".length());
			return loc;
		} else {
			return "Not Specified";
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("currentPage", viewPager.getCurrentItem());
		// etc.
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int currentItem = savedInstanceState.getInt("currentPage");
		viewPager.setCurrentItem(currentItem);
		totalPages = pageAdapter.getCount();
	}

	public void configureTopViews() {
		topEventCounter.setText("Event\nItem: " + (currentPage + 1) + "/"
				+ totalPages);
		if (viewPager.getCurrentItem() == 0) {
			lastEventBtn.setEnabled(false);
			lastEventBtn.getBackground().setColorFilter(
					Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
		} else {
			lastEventBtn.setEnabled(true);
			lastEventBtn.getBackground().setColorFilter(Color.argb(0, 0, 0, 0),
					PorterDuff.Mode.SRC_ATOP);
		}
		if (viewPager.getCurrentItem() == pageAdapter.getCount() - 1) {
			nextEventBtn.setEnabled(false);
			nextEventBtn.getBackground().setColorFilter(
					Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
		} else {
			nextEventBtn.setEnabled(true);
			nextEventBtn.getBackground().setColorFilter(Color.argb(0, 0, 0, 0),
					PorterDuff.Mode.SRC_ATOP);
		}
	}
}
