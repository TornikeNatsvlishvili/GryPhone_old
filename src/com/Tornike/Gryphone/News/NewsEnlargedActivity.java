package com.Tornike.Gryphone.News;

import java.util.ArrayList;

import com.Tornike.Gryphone.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsEnlargedActivity extends Activity {
	// Pager
	NewsPagerAdapter pagerAdapter;
	TextView topText, topNewsCounterTxt;
	ImageView topButton;
	ViewPager newsPager;
	ImageButton nextNewsBtn, lastNewsBtn;
	ArrayList<TextView> pagerTextViews = new ArrayList<TextView>();

	// Menu
	Menu newsMenu;

	// Variables
	int currentNewsCounter = 0;
	int totalNews = 0;
	boolean zoomedIn = false;
	ArrayList<String[]> newsItems = new ArrayList<String[]>();
	boolean alreadySet = false;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_enlarged_layout);

		// Pager
		pagerAdapter = new NewsPagerAdapter(this);
		newsPager = (ViewPager) findViewById(R.id.news_pager);
		newsPager.setAdapter(pagerAdapter);

		// View init
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		topText = (TextView) findViewById(R.id.top_bar_text);
		nextNewsBtn = (ImageButton) findViewById(R.id.top_bar_next);
		lastNewsBtn = (ImageButton) findViewById(R.id.top_bar_last);
		topNewsCounterTxt = (TextView) findViewById(R.id.top_bar_page_number_text);

		topText.setText("News");
		topNewsCounterTxt.setText("Loading...");

		// give last btn disabled look
		lastNewsBtn.setEnabled(false);
		lastNewsBtn.getBackground().setColorFilter(Color.argb(200, 0, 0, 0),
				PorterDuff.Mode.SRC_ATOP);

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		nextNewsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentNewsCounter++;
				newsPager.setCurrentItem(currentNewsCounter, true);
				topNewsCounterTxt.setText("News Item: "
						+ (currentNewsCounter + 1) + "/" + totalNews);
			}
		});

		lastNewsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentNewsCounter--;
				newsPager.setCurrentItem(currentNewsCounter, true);
				topNewsCounterTxt.setText("News Item: "
						+ (currentNewsCounter + 1) + "/" + totalNews);
			}
		});
		newsPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				currentNewsCounter = newsPager.getCurrentItem();
				topNewsCounterTxt.setText("News Item: "
						+ (currentNewsCounter + 1) + "/" + totalNews);
				if (currentNewsCounter == newsPager.getAdapter().getCount() - 1) {
					nextNewsBtn.setEnabled(false);
					nextNewsBtn.getBackground().setColorFilter(
							Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				} else {
					nextNewsBtn.setEnabled(true);
					nextNewsBtn.getBackground().setColorFilter(
							Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				}
				if (currentNewsCounter == 0) {
					lastNewsBtn.setEnabled(false);
					lastNewsBtn.getBackground().setColorFilter(
							Color.argb(200, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				} else {
					lastNewsBtn.setEnabled(true);
					lastNewsBtn.getBackground().setColorFilter(
							Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
				}
				zoomedIn = false;
				pagerTextViews.get(currentNewsCounter * 3).setTextSize(20);
				pagerTextViews.get((currentNewsCounter * 3) + 1).setTextSize(8);
				pagerTextViews.get((currentNewsCounter * 3) + 2)
						.setTextSize(12);
				if (newsMenu != null) {
					newsMenu.getItem(0).setIcon(
							NewsEnlargedActivity.this.getResources()
									.getDrawable(R.drawable.menu_enlarge));
					newsMenu.getItem(0).setTitle("Enlarge Text");
				}
			}
		});
		// get news list
		getNewsList();
	}

	public void getNewsList() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String[] title = new String[newsItems.size()];
			String[] date = new String[newsItems.size()];
			String[] story = new String[newsItems.size()];
			int index = extras.getInt("index");
			title = extras.getStringArray("title");
			date = extras.getStringArray("date");
			story = extras.getStringArray("story");
			if (title != null && date != null && story != null) {
				new LoadPagerTask().execute(title, date, story,
						new String[] { index + "" });
			}
		}
	}

	protected class LoadPagerTask extends AsyncTask<String[], String, Void> {
		int index, length;

		@Override
		protected Void doInBackground(String[]... params) {
			length = params[0].length;
			index = Integer.parseInt(params[3][0]);
			for (int a = 0; a < length; a++) {
				this.publishProgress(params[0][a], params[1][a], params[2][a]);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			pagerAdapter.addItem(getPagerItem(values[0], values[1], values[2]));
		}

		@Override
		protected void onPostExecute(Void result) {
			totalNews = length;
			topNewsCounterTxt.setText("News Item: 1/" + totalNews);
			if(!alreadySet){
				newsPager.setCurrentItem(index,false);
			}else{
				newsPager.setCurrentItem(currentNewsCounter,false);
			}
		}
	}

	public LinearLayout getPagerItem(String title, String date, String story) {
		LinearLayout layout = (LinearLayout) ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.news_pager_layout, null);

		final TextView newsTitleTxt = (TextView) layout
				.findViewById(R.id.news_title_text);
		final TextView newsDateTxt = (TextView) layout
				.findViewById(R.id.news_date_text);
		final TextView newsStoryTxt = (TextView) layout
				.findViewById(R.id.news_story_text);

		newsTitleTxt.setText(title);
		newsDateTxt.setText(date);
		newsStoryTxt.setText(Html.fromHtml(story));
		newsStoryTxt.setMovementMethod(LinkMovementMethod.getInstance());

		// Add views to list
		pagerTextViews.add(newsTitleTxt);
		pagerTextViews.add(newsDateTxt);
		pagerTextViews.add(newsStoryTxt);
		return layout;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news_menu, menu);
		newsMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.news_menu_text_size:
			if (!zoomedIn) {
				pagerTextViews.get(currentNewsCounter * 3).setTextSize(25);
				pagerTextViews.get((currentNewsCounter * 3) + 1)
						.setTextSize(13);
				pagerTextViews.get((currentNewsCounter * 3) + 2)
						.setTextSize(16);
				item.setIcon(this.getResources().getDrawable(
						R.drawable.menu_reduce));
				item.setTitle("Reduce Text Size");
			} else {
				pagerTextViews.get(currentNewsCounter * 3).setTextSize(20);
				pagerTextViews.get((currentNewsCounter * 3) + 1).setTextSize(8);
				pagerTextViews.get((currentNewsCounter * 3) + 2)
						.setTextSize(12);
				item.setIcon(this.getResources().getDrawable(
						R.drawable.menu_enlarge));
				item.setTitle("Enlarge Text");
			}
			zoomedIn = !zoomedIn;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("currentItem", newsPager.getCurrentItem());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentNewsCounter = savedInstanceState.getInt("currentItem");
		alreadySet= true;
	}
}