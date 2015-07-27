package com.Tornike.Gryphone.Events.Students;

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

import com.Tornike.Gryphone.GryphoneActivity;
import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.*;
import com.Tornike.Gryphone.Events.Campus.CampusEventsActivity;
import com.Tornike.Gryphone.Maps.MapsActivity;
import com.Tornike.Gryphone.WebViewer.WebViewerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StudentEventsActivity extends Activity {
	// INTERNET STUFF
	HttpClient httpClient = new DefaultHttpClient();
	HttpContext localContext = new BasicHttpContext();
	HttpGet httpGet;
	HttpResponse response;
	BufferedReader reader;

	// Views
	ListView eventList;
	RelativeLayout loadBar;
	TextView topText;
	ImageView topButton;

	// Calendar
	Calendar cal;

	// Adapter stuff
	StudentEventAdapter studentEventAdapter;

	// Variables
	String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	boolean cancel = false;
	boolean timingCorrection = false;
	static ArrayList<StudentEventItem> eventItemList = new ArrayList<StudentEventItem>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_student_layout);

		// Date Initialization
		cal = Calendar.getInstance(Locale.getDefault());

		// List stuff initialization
		studentEventAdapter = new StudentEventAdapter(this);

		// View initilization
		eventList = (ListView) findViewById(R.id.event_student_list_view);
		loadBar = (RelativeLayout) findViewById(R.id.event_student_loadingbar);
		topText = (TextView) findViewById(R.id.top_bar_text);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		topText.setText("Student Events");
		eventList.setAdapter(studentEventAdapter);

		eventList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(StudentEventsActivity.this,
						WebViewerActivity.class);
				intent.putExtra("url", studentEventAdapter.getItem(position)
						.getLink());
				intent.putExtra("title", studentEventAdapter.getItem(position)
						.getTitle());
				StudentEventsActivity.this.startActivity(intent);
			}
		});

		// download evenets
		new DownloadStudentEventsTask().execute(formatEventUrl());
	}

	protected class DownloadStudentEventsTask extends
			AsyncTask<String, StudentEventItem, String> {

		@Override
		protected void onPreExecute() {
			studentEventAdapter.clear();
			loadBar.setVisibility(loadBar.VISIBLE);
			loadBar.setAnimation(getFadeInAnimation());
		}

		@Override
		protected String doInBackground(String... params) {
			String temp = "";
			try {
				temp = getHtml(params[0]);
			} catch (Exception e) {
				Log.e("Error", "task error", e);
			}
			return "success";

		}

		protected void onProgressUpdate(StudentEventItem... values) {
			studentEventAdapter.addItem(values[0]);
			eventItemList.add(values[0]);
			timingCorrection = true;
		}

		protected void onPostExecute(String result) {
			loadBar.setAnimation(getFadeOutAnimation());
			loadBar.setVisibility(loadBar.INVISIBLE);
		}

		public String getHtml(String url) throws ClientProtocolException,
				IOException {
			String result = "", line;
			Boolean startRealRead = false;
			int loc1 = 0, loc2 = 0;

			httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet, localContext);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			while ((line = reader.readLine()) != null) {
				if (startRealRead) {
					if (line.contains("<tr align=\"center\" bgcolor=\"#EEEEEE\">")) {
						break;
					}
					result += line;
					loc1 = result.indexOf("<tr ", loc2 + 1);
					loc2 = result.indexOf("</tr>", loc1);
					if (loc1 != -1 && loc2 != -1) {
						parsePart(result.substring(loc1, loc2));
						result = result.substring(loc2, result.length());
					}
				}
				if (!startRealRead
						&& line.contains("<tr align=\"center\" bgcolor=\"#EEEEEE\">")) {
					startRealRead = true;
				}
			}
			result = result.replace("\n", "");
			result = result.replace("\t", "");
			result = result.replace("\r", "");
			return result;
		}

		public void parsePart(String itemString) {
			int temp1, temp2;
			String tempString;
			StudentEventItem eventItem = new StudentEventItem();

			temp1 = itemString.indexOf("bgcolor=\"");
			temp2 = itemString.indexOf("\">", temp1);
			tempString = itemString.substring(temp1 + 9, temp2);
			eventItem.setBackgroundColor(Color.parseColor("#" + tempString));

			temp1 = itemString.indexOf("bgcolor=\"", temp2);
			temp2 = itemString.indexOf("\">", temp1);
			tempString = itemString.substring(temp1 + 9, temp2);
			eventItem.setTypeColor(Color.parseColor("#" + tempString));

			temp1 = itemString.indexOf("<a href=\"");
			temp2 = itemString.indexOf("\">", temp1);
			eventItem.setLink("http://www.uoguelph.ca/studentaffairs/reg/"
					+ itemString.substring(temp1 + 9, temp2));

			temp1 = temp2;
			temp2 = itemString.indexOf("</a>", temp1);
			eventItem.setTitle(itemString.substring(temp1 + 2, temp2));

			temp1 = itemString.indexOf("align=\"center\">");
			temp2 = itemString.indexOf("<span class");
			eventItem.setDate(itemString.substring(temp1 + 15, temp2));

			temp1 = itemString.indexOf("<br>");
			temp2 = itemString.indexOf("</span>", temp1);
			eventItem.setTime(itemString.substring(temp1 + 4, temp2));

			temp1 = itemString.indexOf("<td width=\"100\">");
			temp2 = itemString.indexOf("</td>", temp1);
			tempString = itemString.substring(
					temp1 + "<td width=\"100\">".length(), temp2);
			if (tempString.contains("FULL")) {
				temp1 = tempString.indexOf("\">");
				temp2 = tempString.indexOf("</font>", temp1);
				eventItem
						.setEligibility(tempString.substring(temp1 + 2, temp2));
				temp1 = tempString.indexOf("\">", temp2);
				temp2 = tempString.indexOf(" on", temp1);
				eventItem.setAvailability(Integer.parseInt(tempString
						.substring(temp1 + 2, temp2)));
			} else {
				eventItem.setAvailability(Integer.parseInt(tempString.trim()));
				eventItem.setEligibility("Available");

			}

			publishProgress(eventItem);
		}
	}

	public String formatEventUrl() {
		String formatedUrl = "http://www.uoguelph.ca/studentaffairs/reg/index.cfm";
		return formatedUrl;
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
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int length = eventItemList.size();
		for (int a = 0; a < length; a++) {
			studentEventAdapter.addItem(eventItemList.get(a));
		}
	}
}