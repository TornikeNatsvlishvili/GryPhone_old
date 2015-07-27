package com.Tornike.Gryphone.Library;

import com.Tornike.Gryphone.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LibraryActivity extends Activity {
	// Views
	ListView listView;
	RelativeLayout loadingBar;
	Button searchBtn;
	EditText searchQueryText;
	TextView topText;
	ImageView topButton;

	// ListView stuff
	static LibraryAdapter libraryAdapter;

	// Variabels
	boolean currentTaskEnd = true;
	boolean timingCorrection = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_layout);

		// View stuff initialize
		listView = (ListView) findViewById(R.id.library_list_view);
		searchBtn = (Button) findViewById(R.id.library_search_btn);
		searchQueryText = (EditText) findViewById(R.id.library_search_query);
		topText = (TextView) findViewById(R.id.top_bar_text);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		loadingBar = (RelativeLayout) findViewById(R.id.library_loadingbar);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("firstTime", true)) {
			// run your one time code
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTime", false);
			editor.commit();
			// libraryAdapter initialize
			libraryAdapter = new LibraryAdapter(this);
		}

		topText.setText("Library");
		listView.setAdapter(libraryAdapter);

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentTaskEnd = true;
				finish();
			}
		});

		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentTaskEnd) {
					if (!searchQueryText.getText().toString().equals("")) {
						// Show loading bar
						loadingBar.setVisibility(loadingBar.VISIBLE);
						loadingBar.setAnimation(getFadeInAnimation());
						// Start search
						new SearchLibraryTask().execute(searchQueryText
								.getText().toString());
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								searchQueryText.getWindowToken(), 0);
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Already Searching!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						LibraryActivity.this);

				// Format Title
				Spannable title = new SpannableString(libraryAdapter.getItem(
						arg2).getTitle());
				title.setSpan(new RelativeSizeSpan(1.2f), 0, title.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				title.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
						title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

				// Set title and message
				alert.setTitle(title);
				alert.setMessage(libraryAdapter.getItem(arg2)
						.toStringSpannable());
				alert.show();

			}
		});
	}

	protected class SearchLibraryTask extends
			AsyncTask<String, LibraryItem, String> {
		int loadedCounter = 0;

		@Override
		protected void onPreExecute() {
			libraryAdapter.clear();
			currentTaskEnd = false;
		}

		@Override
		protected String doInBackground(String... strings) {
			try {
				getHtmlAndSplitDays(formatNewsUrl(strings[0], 10));
			} catch (Exception e) {
				Log.e("Error", e.toString());
			}
			return strings[0];
		}

		@Override
		protected void onProgressUpdate(LibraryItem... values) {
			super.onProgressUpdate(values);
			if (!currentTaskEnd) {
				libraryAdapter.addItem(values[0]);
				timingCorrection = true;
			}
		}

		protected void onPostExecute(String result) {
			loadingBar.setAnimation(getFadeOutAnimation());
			loadingBar.setVisibility(loadingBar.INVISIBLE);
			currentTaskEnd = true;
		}

		// Download an html document form the internet
		public void getHtmlAndSplitDays(String url)
				throws ClientProtocolException, IOException {
			String result = "", line = null, stripedResult;
			Boolean startRealRead = false, endable = false;
			int firstIndex = 0, secondIndex = 0, endIndex = 0;

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(url.replace(" ", ""));
			HttpResponse response = httpClient.execute(httpGet, localContext);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			// if newsloadCount is under 10 keep going or if we are loading more
			// and newsloadcount is under
			// newMax
			while ((line = reader.readLine()) != null && loadedCounter <= 10) {
				// Start real reading when the first item is found
				if (line.contains("<TH> library </TH>")) {
					startRealRead = true;
				}
				if (line.contains("</TABLE>") && endable) {
					break;
				}
				if (startRealRead) {
					result += line + "\n";
					// Parse what you have so far, if enough for a day is found
					// then parse it
					stripedResult = result.replaceAll("\n", "");
					stripedResult = stripedResult.replaceAll("\r", "");

					firstIndex = stripedResult.indexOf("<TR>", secondIndex);
					secondIndex = stripedResult
							.indexOf("</TR>", firstIndex + 1);
					if (firstIndex != -1 && secondIndex != -1) {
						// Notify we have started reading real items
						endable = true;
						// Library item found
						// Seperate the item info
						String itemString = stripedResult.substring(firstIndex,
								secondIndex);
						// reset Result to nothing
						result = stripedResult.substring(secondIndex,
								stripedResult.length());
						parseItem(itemString);
						loadedCounter++;
					}
				}
			}
		}

		// Split up each news item for one day
		public void parseItem(String itemHtml) {
			String title, link, date;
			int titleLoc1, titleLoc2, dateLoc1, dateLoc2, linkLoc1, linkLoc2;
			linkLoc1 = itemHtml.indexOf("<A HREF=\"");
			linkLoc1 = itemHtml.indexOf("<A HREF=\"", linkLoc1 + 1);
			linkLoc2 = itemHtml.indexOf("\">", linkLoc1);
			titleLoc1 = linkLoc2 + 2;
			titleLoc2 = itemHtml.indexOf("</A>", titleLoc1);
			dateLoc1 = itemHtml.indexOf("<TD ALIGN=LEFT>", titleLoc2);
			dateLoc1 = itemHtml.indexOf("<TD ALIGN=LEFT>", dateLoc1 + 1);
			dateLoc2 = itemHtml.indexOf("</TD>", dateLoc1);

			link = "http://trellis3.tug-libraries.on.ca"
					+ itemHtml.substring(linkLoc1 + "<A HREF=\"".length(),
							linkLoc2);
			title = itemHtml.substring(titleLoc1, titleLoc2).replace("/", "");
			date = itemHtml.substring(dateLoc1 + "<TD ALIGN=LEFT>".length(),
					dateLoc2);
			if (date.equals("<PRE> </PRE>")) {
				date = "Unknown";
			}

			LibraryItem item = new LibraryItem();
			item.setTitle(title);
			item.setDate(date);
			item.setLink(link);

			getHtmlAndParseItemDescription(link, item);

			publishProgress(item);
			// publishProgress(new LibraryItem(title, author, date, location,
			// callNum, status, link));
			// Make sure the UI thread recieved the published Progress
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

		public void getHtmlAndParseItemDescription(String url, LibraryItem item) {
			String result = "", line = null, stripedResult;
			Boolean startRealRead = false;
			int firstIndex = 0, secondIndex = 0;
			BufferedReader reader = null;
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpGet httpGet = new HttpGet(url.replace(" ", ""));
				HttpResponse response = httpClient.execute(httpGet,
						localContext);

				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				line = reader.readLine();
			} catch (Exception e) {

			}

			while (line != null) {
				// Start real reading when the first item is found
				if (line.contains("<TH NOWRAP ALIGN=RIGHT VALIGN=TOP>")) {
					startRealRead = true;
				}
				if (startRealRead) {
					result += line + "\n";
					stripedResult = result.replaceAll("\n", "");
					stripedResult = stripedResult.replaceAll("\r", "");

					firstIndex = stripedResult.indexOf(
							"<TH NOWRAP ALIGN=RIGHT VALIGN=TOP>", secondIndex);
					secondIndex = stripedResult.indexOf("<BR><CENTER>",
							firstIndex + 1);
					if (firstIndex != -1 && secondIndex != -1) {
						// item info found
						// Seperate the item info
						String itemString = stripedResult.substring(firstIndex,
								secondIndex);
						// Cleaning
						itemString = itemString.replace(
								"<TH NOWRAP ALIGN=RIGHT VALIGN=TOP>", "\n");
						itemString = itemString.replace("<TR>", "");
						itemString = itemString.replace("<TD dir=\"ltr\">", "");
						itemString = itemString.replace("</TD>", "");
						itemString = itemString.replace("</TH>", "");
						itemString = itemString.replace("<TD>", "");
						itemString = itemString.replace("</TABLE>", "");
						itemString = itemString.replace("<BR>", "");
						itemString = itemString.replace("<CENTER>", "");
						itemString = itemString.replace("</TR>", "");
						itemString = itemString.replace("</A>", "");
						itemString = itemString.replace("<TD COLSPAN=2>", "");
						itemString = itemString.replace("<A name=\"D2\">", "");
						itemString = itemString.replace("<A name=\"D3\">", "");
						itemString = itemString.replace("<A name=\"D1\">", "");
						itemString = itemString.replace("<HR WIDTH=150>", "");

						int loc = itemString.indexOf("<A HREF=\"");
						while (loc != -1) {
							int loc2 = itemString.indexOf("\">", loc) + 2;
							itemString = itemString.replace(
									itemString.substring(loc, loc2), "");
							loc = itemString.indexOf("<A HREF=\"");
						}

						int loc3 = itemString.indexOf("Description: ")
								+ "Description: ".length();
						item.setDescription(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Author: ")
								+ "Author: ".length();
						item.setAuthor(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Publisher: ")
								+ "Publisher: ".length();
						item.setPublisher(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Subject(s): ")
								+ "Subject(s): ".length();
						item.setSubjects(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Series: ")
								+ "Series: ".length();
						item.setSeries(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("LOCATION:")
								+ "LOCATION:".length();
						item.setLocation(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Status:")
								+ "Status:".length();
						item.setStatus(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Call Number: ")
								+ "Call Number: ".length();
						item.setCallNum(itemString.substring(loc3,
								itemString.indexOf("\n", loc3)));
						loc3 = itemString.indexOf("Number of Items:")
								+ "Number of Items:".length();
						int tempLoc = itemString.indexOf("LOCATION", loc3);
						if (tempLoc != -1 && tempLoc < itemString.length()) {
							item.setNumItems(itemString.substring(loc3,
									itemString.indexOf("LOCATION", loc3)));
						} else {
							item.setNumItems(itemString.substring(loc3,
									itemString.length()));
						}

					}
				}
				try {
					line = reader.readLine();
				} catch (IOException e) {
					Log.e("ERROR", e.toString());
				}
			}
		}

	}

	// Format Url so that 9 is 09
	public String formatNewsUrl(String searchQuery, int loadAmt) {
		String formatedUrl, properSearchQuery, newPID = "";
		properSearchQuery = searchQuery.replace(" ", "+");
		// New PID
		for (int a = 0; a < 27; a++) {
			int rnd = (int) (Math.random() * 3) + 1;
			if (rnd == 3) {
				newPID += (char) ((int) (Math.random() * 9) + 48);
			} else if (rnd == 2) {
				newPID += (char) ((int) (Math.random() * 25) + 97);
			} else {
				newPID += (char) ((int) (Math.random() * 25) + 65);
			}
		}
		formatedUrl = "http://trellis3.tug-libraries.on.ca/cgi-bin/Pwebrecon.cgi?Search_Arg="
				+ properSearchQuery
				+ "&SL=Submit%26LOCA%3D%3DGuelph%7C0&Search_Code=TALL&PID="
				+ newPID + "&SEQ=20111113235043&CNT=25&HIST=1";
		return formatedUrl;
	}

	public Animation getFadeOutAnimation() {
		Animation temp = new AlphaAnimation(1,0);
		temp.setDuration(500);
		return temp;
	}

	public Animation getFadeInAnimation() {
		Animation temp = new AlphaAnimation(0, 1);
		temp.setDuration(500);
		return temp;
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		currentTaskEnd = true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("searchEntry", searchQueryText.getText()
				.toString());
		savedInstanceState.putInt("loading", loadingBar.getVisibility());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		searchQueryText.setText(savedInstanceState.getString("searchEntry"));
		loadingBar.setVisibility(savedInstanceState.getInt("loading"));
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchQueryText.getWindowToken(), 0);
	}
}
