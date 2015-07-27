package com.Tornike.Gryphone.News;

import com.Tornike.Gryphone.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsActivity extends Activity {
	// INTERNET STUFF
	HttpClient httpClient = new DefaultHttpClient();
	HttpContext localContext = new BasicHttpContext();
	HttpGet httpGet;
	HttpResponse response;
	BufferedReader reader;

	// Views
	ListView listView;
	RelativeLayout progressBarLayout;
	TextView topText;
	ImageView topButton;

	// ListView stuff
	static NewsAdapter newsAdapter;

	// Variabels
	boolean currentTaskEnd = true;
	boolean timingCorrection = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_layout);

		// View stuff initialize
		topText = (TextView) findViewById(R.id.top_bar_text);
		listView = (ListView) findViewById(R.id.news_list_view);
		progressBarLayout = (RelativeLayout) findViewById(R.id.news_loadingbar);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);

		// Start Downloading news
		newsAdapter = new NewsAdapter(this);
		new DownloadNewsTask().execute();

		listView.setAdapter(newsAdapter);
		topText.setText("News");

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (newsAdapter.getItemViewType(arg2) == NewsAdapter.TYPE_ITEM) {
					if (!currentTaskEnd) {
						Toast.makeText(NewsActivity.this, "Still Loading...",
								Toast.LENGTH_SHORT).show();
						return;
					}
					Intent enlargeStoryIntent = new Intent(NewsActivity.this,
							NewsEnlargedActivity.class);
					ArrayList<String[]> list = new ArrayList<String[]>();
					int length = newsAdapter.getCount();
					for (int a = 0; a < length; a++) {
						if (newsAdapter.getItemViewType(a) == newsAdapter.TYPE_ITEM) {
							String[] temp = new String[3];
							temp[0] = newsAdapter.getItem(a).getTitle();
							temp[1] = newsAdapter.getItem(a).getDateDayWords()
									+ " "
									+ newsAdapter.getItem(a)
											.getDateMonthWords() + " "
									+ newsAdapter.getItem(a).getDateDay()
									+ ", "
									+ newsAdapter.getItem(a).getDateYear();
							temp[2] = newsAdapter.getItem(a).getStory();
							list.add(temp);
						}
					}
					String[] title = new String[list.size()];
					String[] date = new String[list.size()];
					String[] story = new String[list.size()];
					length = list.size();
					for (int a = 0; a < length; a++) {
						title[a] = list.get(a)[0];
						date[a] = list.get(a)[1];
						story[a] = list.get(a)[2];
						if (newsAdapter.getItem(arg2).getTitle()
								.equals(list.get(a)[0])) {
							enlargeStoryIntent.putExtra("index", a);
						}
					}
					enlargeStoryIntent.putExtra("title", title);
					enlargeStoryIntent.putExtra("date", date);
					enlargeStoryIntent.putExtra("story", story);
					startActivity(enlargeStoryIntent);
				}
			}
		});
	}

	protected class DownloadNewsTask extends AsyncTask<Void, NewsItem, Void> {

		@Override
		protected void onPreExecute() {
			newsAdapter.clear();
			currentTaskEnd = false;
			// Start Animations
			progressBarLayout.setVisibility(progressBarLayout.VISIBLE);
			progressBarLayout.setAnimation(getFadeInAnimation());
		}

		@Override
		protected Void doInBackground(Void... strings) {
			try {
				String temp = getHtml(formatNewsUrl());
				parseHtml(temp);
			} catch (Exception e) {
				Log.e("Error", e.toString());
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(NewsItem... values) {
			super.onProgressUpdate(values);
			// Add a seperator to the news listView if it is empty or
			// the item before it has a different day number
			if (newsAdapter.getCount() == 0
					|| newsAdapter.getItemViewType(newsAdapter.getCount() - 1) == NewsAdapter.TYPE_ITEM
					&& values[0].getDateDay() != newsAdapter.getItem(
							newsAdapter.getCount() - 1).getDateDay()) {
				String date = values[0].getDateDayWords() + ", "
						+ values[0].getDateMonthWords() + " "
						+ values[0].getDateDay();

				newsAdapter.addSeparatorItem(date);
				newsAdapter.addItem(values[0]);
			} else {
				newsAdapter.addItem(values[0]);
			}
			// adding to general list
			timingCorrection = true;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBarLayout.setAnimation(getFadeOutAnimation());
			progressBarLayout.setVisibility(progressBarLayout.INVISIBLE);
			currentTaskEnd = true;
		}

		// Download an html document form the internet
		public String getHtml(String url) throws ClientProtocolException,
				IOException {
			httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet, localContext);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			String result = "", line = "";
			boolean startRecord = false;
			while ((line = reader.readLine()) != null) {
				if (!startRecord && line.contains("<item>"))
					startRecord = true;
				if (startRecord)
					result += line;
			}
			return result;
		}

		// Split up each news item for one day
		public void parseHtml(String phrase) {
			int loc1 = 0, loc2 = 0, loc3 = 0, loc4 = 0, firstIndex = 0;
			Boolean eof = false, first = true;

			while (!eof) {
				loc1 = phrase.indexOf("<item>", loc2);
				loc2 = phrase.indexOf("</item>", loc1 + 1);
				if (loc1 == -1 || loc2 == -1 || loc1 == firstIndex) {
					break;
				}
				if (first) {
					first = false;
					firstIndex = loc1;
				}
				String itemString = phrase.substring(loc1 + 6, loc2);
				NewsItem tempItem = new NewsItem();
				loc3 = itemString.indexOf("<title>");
				loc4 = itemString.indexOf("</title>", loc3);
				tempItem.setTitle(itemString.substring(loc3 + 7, loc4));

				loc3 = itemString.indexOf("<description>");
				loc4 = itemString.indexOf("</description>", loc3);
				String tempDescription = itemString.substring(loc3 + 13, loc4);
				tempItem.setStory(formatDescription(tempDescription));

				loc3 = itemString.indexOf("<link>");
				loc4 = itemString.indexOf("</link>", loc3);
				tempItem.setURL(itemString.substring(loc3 + 6, loc4));

				loc3 = itemString.indexOf("<pubDate>");
				loc4 = itemString.indexOf("</pubDate>", loc3);
				tempItem.setNewsDate(itemString.substring(loc3 + 9, loc4));

				publishProgress(tempItem);
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
		}

		public String formatDescription(String tempDescription) {
			int temp1 = 0, temp2 = 0, temp3 = 0;
			String link, linkName;
			temp1 = tempDescription
					.indexOf("&lt;/p&gt;&lt;div class=\"feedflare\"&gt");
			tempDescription = tempDescription.substring(0, temp1);

			temp1 = 0;

			tempDescription = tempDescription.replace("&lt;", "<");
			tempDescription = tempDescription.replace("&gt;", ">");
			tempDescription = tempDescription.replace("&amp;", "&");
			tempDescription = tempDescription.replace("<em>", "<i>");
			tempDescription = tempDescription.replace("</em>", "</i>");
			return tempDescription;
		}
	}

	// Format Url so that 9 is 09
	public String formatNewsUrl() {
		String formatedUrl = "http://feeds.feedburner.com/uoguelph";
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
	}
}