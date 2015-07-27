package com.Tornike.Gryphone.Bus;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.Campus.CampusEventPagerAdapter;
import com.Tornike.Gryphone.News.*;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.PictureDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class BusActivity extends Activity {
	// INTERNET STUFF
	HttpClient httpClient = new DefaultHttpClient();
	HttpContext localContext = new BasicHttpContext();
	HttpGet httpGet;
	HttpResponse response;
	BufferedReader reader;

	// Timer
	MyCount timer;

	// Settings
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	// Views
	ProgressBar loadingBar;
	ImageButton busRouteBtn;
	Gallery stopList;
	TextView timeRemainingTxt;
	ImageView topButton;
	TextView topText;
	ImageView busGpsLoc;
	WebView busImgWebView;

	// Calendar
	Calendar cal;

	// ListView stuff
	BusAdapter stopListAdapter;

	// Map image
	Bitmap routeMap;
	Bitmap stopOverlay;
	// Route Dialog List
	final CharSequence[] routeNames = { "1A - College Edinburgh",
			"1B - College Edinburgh", "2A - West Loop", "2B - West Loop",
			"3A - East Loop", "3B - East Loop", "04 - York",
			"05 - South Gordon", "06 - Harvard Ironwood",
			"07 - Kortright Downey", "08 - Stone Road Mall",
			"09 - West End Community Centre", "10 - Imperial",
			"11 - Willow West", "12 - General Hospital",
			"13 - Victoria Road Recreation Centre", "14 - Grange",
			"15 - University College", "16 - Hanlon Industrial",
			"20 - Northwest Industrial", "50 - Stone", "56 - Victoria Express",
			"57 - Harvard", "58 - Edinburgh" };
	final String[] tags = { "1a", "1b", "2a", "2b", "3a", "3b", "04", "05",
			"06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
			"20", "50", "56", "57", "58" };

	// Variabels
	List<String> busLink = new ArrayList<String>();
	boolean currentTaskEnd = true;
	boolean timingCorrection = false;
	String currentRoute = "1a";
	String[] amPm = { "AM", "PM" };
	boolean doneLoading = false;
	boolean errorHappened = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_layout);

		// Calendar
		cal = Calendar.getInstance();

		// View stuff initialize
		loadingBar = (ProgressBar) findViewById(R.id.bus_loadingbar);
		timeRemainingTxt = (TextView) findViewById(R.id.bus_time_text);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		busRouteBtn = (ImageButton) findViewById(R.id.top_bar_route_btn);
		stopList = (Gallery) findViewById(R.id.bus_stop_list);
		topText = (TextView) findViewById(R.id.top_bar_text);
		busGpsLoc = (ImageView) findViewById(R.id.bus_gps_image);
		busImgWebView = (WebView) findViewById(R.id.bus_webview);

		// stopAdapter initialize
		stopListAdapter = new BusAdapter(this);
		stopList.setAdapter(stopListAdapter);

		// Loading map
		routeMap = Bitmap.createBitmap(650, 650, Config.ARGB_8888);
		Canvas canvas = new Canvas(routeMap);
		Paint pt = new Paint();
		pt.setColor(Color.RED);
		pt.setTextSize(25);
		canvas.drawText("Loading...", 280, 320, pt);
		busGpsLoc.setImageBitmap(routeMap);

		// Load last bus
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		// Load the last viewed route
		if ((currentRoute = prefs.getString("lastBusViewed", "1A")) != null) {
			topText.setText("Bus Route - " + currentRoute.toUpperCase());
		} else {
			editor.putString("lastBusViewed", currentRoute);
			editor.commit();
		}

		stopList.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int clickPosition, long arg3) {
				int finalPixelX, finalPixelY;
				stopOverlay = Bitmap.createBitmap(650, 650, Config.ARGB_8888);
				int index = getRouteIndex(currentRoute);
				finalPixelX = BusStopCoords[index][clickPosition][0];
				finalPixelY = BusStopCoords[index][clickPosition][1];
				Bitmap cs = Bitmap.createBitmap(650, 650,
						Bitmap.Config.ARGB_8888);

				Canvas comboImage = new Canvas(stopOverlay);
				Paint blackPaint = new Paint();
				blackPaint.setStyle(Paint.Style.FILL);
				blackPaint.setColor(Color.BLACK);

				comboImage.drawLine(0, finalPixelY, 650, finalPixelY,
						blackPaint);
				comboImage.drawLine(finalPixelX, 0, finalPixelX, 650,
						blackPaint);
				comboImage.drawRect(finalPixelX - 3, finalPixelY - 3,
						finalPixelX + 3, finalPixelY + 3, blackPaint);
				comboImage.save();

				comboImage = new Canvas(cs);
				comboImage.drawBitmap(routeMap, new Matrix(), null);
				comboImage.drawBitmap(stopOverlay, new Matrix(), null);
				comboImage.save();
				busGpsLoc.setImageBitmap(cs);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// top gryph button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		busRouteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder routeListDialog = new AlertDialog.Builder(
						BusActivity.this);
				routeListDialog.setTitle("Pick a route:");
				routeListDialog.setItems(routeNames,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								currentRoute = (String) tags[item];
								topText.setText("Bus Route - "
										+ currentRoute.toUpperCase());

								// Save Last Route Viewed
								editor.putString("lastBusViewed", currentRoute);
								editor.commit();

								if (timer != null) {
									timer.cancel();
								}
								timeRemainingTxt.setText("Select bus stop!");

								// Start Downloading bus Schedule
								new DownloadBusStopTask().execute();

								// Load map
								busImgWebView
										.loadUrl(formatBusImageUrl(currentRoute));
								stopOverlay = Bitmap.createBitmap(10, 10,
										Config.ARGB_8888);
								routeMap = Bitmap.createBitmap(650, 650,
										Config.ARGB_8888);
								Canvas canvas = new Canvas(routeMap);
								Paint pt = new Paint();
								pt.setColor(Color.RED);
								pt.setTextSize(25);
								canvas.drawText("Loading...", 280, 320, pt);
								busGpsLoc.setImageBitmap(routeMap);
							}
						});
				routeListDialog.show();
			}
		});

		stopList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int clickPosition, long arg3) {
				if (!currentTaskEnd) {
					Toast.makeText(BusActivity.this, "Currently loading!",
							Toast.LENGTH_SHORT).show();
				} else {
					stopListAdapter.setHighlighted(clickPosition);
					new DownloadBusTimeEstTask().execute(busLink
							.get(clickPosition));
				}
			}
		});
		busImgWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					doneLoading = true;
				} else {
					doneLoading = false;
				}
			}
		});
		busImgWebView.setPictureListener(new PictureListener() {
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				if (picture != null && doneLoading && !errorHappened) {
					try {
						routeMap = pictureDrawable2Bitmap(new PictureDrawable(
								picture));
						if (routeMap == null) {
							errorHappened = true;
							routeMap = Bitmap.createBitmap(650, 650,
									Config.ARGB_8888);
							Canvas canvas = new Canvas(routeMap);
							Paint pt = new Paint();
							pt.setColor(Color.RED);
							pt.setTextSize(25);
							canvas.drawText("Error...", 280, 320, pt);
							busGpsLoc.setImageBitmap(routeMap);
							return;
						}
						routeMap = Bitmap.createBitmap(routeMap, 8, 68, 650,
								650);

						if (stopOverlay != null) {
							Bitmap cs = Bitmap.createBitmap(650, 650,
									Bitmap.Config.ARGB_8888);

							Canvas comboImage = new Canvas(cs);
							comboImage.drawBitmap(routeMap, new Matrix(), null);
							comboImage.drawBitmap(stopOverlay, new Matrix(),
									null);
							comboImage.save();
							busGpsLoc.setImageBitmap(cs);
						} else {
							busGpsLoc.setImageBitmap(routeMap);
						}
					} catch (Exception e) {
						Log.e("ERROR", "Bus Track Bitmap", e);
					}
				}
			}
		});
		busImgWebView.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView webview, String url) {
				Picture picture = webview.capturePicture();
			}
		});
		busImgWebView.getSettings().setJavaScriptEnabled(true);
		busImgWebView.loadUrl(formatBusImageUrl(currentRoute));

		// Start Downloading bus Schedule
		new DownloadBusStopTask().execute();
	}

	private static Bitmap pictureDrawable2Bitmap(PictureDrawable pictureDrawable) {
		try {
			Bitmap bitmap = Bitmap.createBitmap(
					pictureDrawable.getIntrinsicWidth(),
					pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawPicture(pictureDrawable.getPicture());
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}

	protected class DownloadBusStopTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onPreExecute() {
			currentTaskEnd = false;
			stopListAdapter.clear();
			busLink.clear();
			timeRemainingTxt.setText("Select bus stop!");
			// Start Animations
			loadingBar.setVisibility(loadingBar.VISIBLE);
			loadingBar.setAnimation(getFadeInAnimation());
		}

		@Override
		protected Void doInBackground(Void... strings) {
			try {
				getHtml(formatBusUrl(currentRoute));
			} catch (Exception e) {
				Log.e("Error", e.toString());
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			stopListAdapter.addItem(Html.fromHtml(values[0]).toString());
			busLink.add(values[1]);
			timingCorrection = true;
		}

		protected void onPostExecute(Void result) {
			if (stopListAdapter.getCount() == 0) {
				timeRemainingTxt
						.setText("Error loading, check your internet connection!");
				if (timer != null) {
					timer.cancel();
				}
			}
			loadingBar.setAnimation(getFadeOutAnimation());
			loadingBar.setVisibility(loadingBar.INVISIBLE);
			currentTaskEnd = true;
		}

		// Download an html document form the internet
		public void getHtml(String url) throws Exception {
			httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet, localContext);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			String line, result = "";
			while ((line = reader.readLine()) != null) {
				result += line;
			}
			int loc1 = 0, loc2 = 0;
			while ((loc1 = result.indexOf("<a href='", loc2)) != -1) {
				loc1 += 9;
				loc2 = result.indexOf("'", loc1 + 1);
				String link = "http://www.nextbus.com/wireless/"
						+ result.substring(loc1, loc2);
				loc2 += 2;
				loc1 = result.indexOf("</a>", loc1);

				String title = result.substring(loc2, loc1);
				String[] output = { title, link };
				publishProgress(output);
			}
		}
	}

	protected class DownloadBusTimeEstTask extends
			AsyncTask<String, String, Void> {
		String id;

		@Override
		protected void onPreExecute() {
			if (loadingBar.getVisibility() != loadingBar.VISIBLE) {
				loadingBar.setVisibility(loadingBar.VISIBLE);
				loadingBar.setAnimation(getFadeInAnimation());
			}
			currentTaskEnd = false;
		}

		@Override
		protected Void doInBackground(String... strings) {
			id = strings[0];
			getTimeEstimate(strings[0]);
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (timer != null) {
				timer.cancel();
			}
			if (values[0].toLowerCase().contains("arriving")) {
				timeRemainingTxt.setText("Arriving to stop!");
			} else if (values[0].toLowerCase().contains("departing")) {
				timeRemainingTxt.setText("Leaving the bus stop!");
			} else if (values[0].toLowerCase().contains("no current")) {
				timeRemainingTxt.setText("No current prediction!");
			} else {
				int hr, min, sec, AMPM;
				cal = Calendar.getInstance();
				hr = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
				// sec = cal.get(Calendar.SECOND);
				AMPM = cal.get(Calendar.AM_PM);
				// timeRemainingTxt.setText(values[0] + " minutes as of " + hr
				// + ":" + min + ":" + sec + " " + amPm[AMPM]);
				// timeRemainingTxt.setText(values[0] + " minutes as of " + hr
				// + ":" + min + " " + amPm[AMPM]);
				timer = new MyCount(
						Integer.parseInt(values[0]) * 60 * 1000 + 30000, 1000,
						id);
				timer.start();
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			if (loadingBar.getVisibility() != loadingBar.INVISIBLE) {
				loadingBar.setAnimation(getFadeOutAnimation());
				loadingBar.setVisibility(loadingBar.INVISIBLE);
			}
			currentTaskEnd = true;
		}

		public void getTimeEstimate(String link) {
			try {
				httpGet = new HttpGet(link);
				response = httpClient.execute(httpGet, localContext);
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

				String line, result = "";
				boolean noPrediction = false;
				while ((line = reader.readLine()) != null) {
					if (line.contains("38px")) {
						result += line;
						break;
					} else if (line.contains("No current prediction")
							|| line.contains("No prediction for selected route")) {
						noPrediction = true;
						break;
					}
				}
				if (noPrediction) {
					publishProgress("No current prediction");
				}
				int loc1 = result.indexOf("\">") + 2;
				int loc2 = result.indexOf("</sp");
				publishProgress(result.substring(loc1, loc2).replace("&nbsp;",
						""));
			} catch (Exception e) {
				Log.e("Error", "getTimeEstimate", e);
			}
		}
	}

	public String formatBusUrl(String tag) {
		tag = tag.toLowerCase();
		if (tag.equals("1a") || tag.equals("1b") || tag.equals("2a")
				|| tag.equals("2b") || tag.equals("3a")) {
			return "http://www.nextbus.com/wireless/miniStop.shtml?a=guelph&r="
					+ tag + "&d=0" + tag + "_loop";
		}
		return "http://www.nextbus.com/wireless/miniStop.shtml?a=guelph&r="
				+ tag + "&d=" + tag + "_loop";
	}

	public String formatBusImageUrl(String tag) {
		tag = tag.toLowerCase();
		return "http://www.nextbus.com/wireless/mapMedium.jsp?a=guelph&r="
				+ tag;
	}

	public String getOnlyNumerics(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer strBuff = new StringBuffer();
		char c;

		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			if (Character.isDigit(c)) {
				strBuff.append(c);
			}
		}
		return strBuff.toString();
	}

	public int getRouteIndex(String name) {
		int curRoute = Integer.parseInt(getOnlyNumerics(name));
		if (curRoute == 1) {
			curRoute = 0;
			if (name.toLowerCase().contains("b")) {
				curRoute++;
			}
			return curRoute;
		} else if (curRoute == 2) {
			curRoute = 2;
			if (name.toLowerCase().contains("b")) {
				curRoute++;
			}
			return curRoute;
		} else if (curRoute == 3) {
			curRoute = 4;
			if (name.toLowerCase().contains("b")) {
				curRoute++;
			}
			return curRoute;
		} else if (curRoute == 4) {
			return 6;
		} else if (curRoute == 5) {
			return 7;
		} else if (curRoute == 6) {
			return 8;
		} else if (curRoute == 7) {
			return 9;
		} else if (curRoute == 8) {
			return 10;
		} else if (curRoute == 9) {
			return 11;
		} else if (curRoute == 10) {
			return 12;
		} else if (curRoute == 11) {
			return 13;
		} else if (curRoute == 12) {
			return 14;
		} else if (curRoute == 13) {
			return 15;
		} else if (curRoute == 14) {
			return 16;
		} else if (curRoute == 15) {
			return 17;
		} else if (curRoute == 16) {
			return 18;
		} else if (curRoute == 20) {
			return 19;
		} else if (curRoute == 50) {
			return 20;
		} else if (curRoute == 56) {
			return 21;
		} else if (curRoute == 57) {
			return 22;
		} else if (curRoute == 58) {
			return 23;
		}
		return -1;
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

	public class MyCount extends CountDownTimer {
		boolean colonOn = true;
		String busId;
		long timeLeft = 0;

		public MyCount(long millisInFuture, long countDownInterval, String id) {
			super(millisInFuture, countDownInterval);
			busId = id;
			timeLeft = millisInFuture;
		}

		@Override
		public void onFinish() {
			timeRemainingTxt.setText("Arrived At Stop!");
			if (currentTaskEnd) {
				new DownloadBusTimeEstTask().execute(busId);
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timeLeft = millisUntilFinished;
			int minutes = (int) (millisUntilFinished / 1000 / 60);
			if (minutes == 0)
				minutes = 1;
			if (colonOn) {
				timeRemainingTxt.setText("Arrival  " + minutes + " minutes");
				colonOn = false;
			} else {
				timeRemainingTxt.setText("Arrival: " + minutes + " minutes");
				colonOn = true;
			}

		}
	}

	int BusStopCoords[][][] = {
			/* 1A */{ { 349, 233 }, { 314, 251 }, { 378, 284 }, { 477, 338 },
					{ 549, 387 }, { 576, 412 }, { 603, 439 }, { 563, 493 },
					{ 528, 529 }, { 474, 537 }, { 409, 509 }, { 315, 449 },
					{ 257, 423 }, { 173, 425 }, { 145, 454 }, { 74, 424 },
					{ 47, 397 }, { 58, 344 }, { 98, 304 }, { 190, 210 },
					{ 259, 141 }, { 329, 113 }, { 364, 147 } },
			/* 1B */{ { 349, 233 }, { 362, 145 }, { 333, 116 }, { 265, 132 },
					{ 190, 208 }, { 114, 284 }, { 47, 351 }, { 66, 413 },
					{ 124, 433 }, { 155, 443 }, { 179, 419 }, { 211, 386 },
					{ 273, 437 }, { 344, 452 }, { 405, 505 }, { 472, 534 },
					{ 526, 524 }, { 563, 487 }, { 603, 436 }, { 567, 400 },
					{ 549, 384 }, { 517, 362 }, { 470, 331 }, { 430, 304 },
					{ 368, 277 }, },
			/* 2A */{ { 459, 261 }, { 470, 298 }, { 490, 335 }, { 499, 344 },
					{ 529, 373 }, { 553, 397 }, { 600, 412 }, { 543, 502 },
					{ 511, 536 }, { 309, 476 }, { 289, 527 }, { 270, 547 },
					{ 252, 565 }, { 233, 541 }, { 209, 515 }, { 173, 496 },
					{ 144, 479 }, { 131, 472 }, { 108, 436 }, { 141, 423 },
					{ 126, 396 }, { 123, 364 }, { 108, 344 }, { 127, 317 },
					{ 98, 289 }, { 75, 309 }, { 47, 284 }, { 63, 261 },
					{ 90, 234 }, { 118, 205 }, { 157, 165 }, { 181, 142 },
					{ 200, 124 }, { 239, 85 }, { 269, 110 }, { 290, 131 },
					{ 318, 159 }, { 358, 199 }, { 384, 225 }, { 418, 242 },
					{ 436, 264 }, },
			/* 2B */{ { 452, 268 }, { 428, 256 }, { 416, 240 }, { 391, 235 },
					{ 380, 222 }, { 363, 205 }, { 352, 194 }, { 325, 168 },
					{ 306, 148 }, { 286, 128 }, { 269, 112 }, { 230, 92 },
					{ 201, 121 }, { 177, 144 }, { 161, 159 }, { 137, 184 },
					{ 114, 207 }, { 86, 235 }, { 65, 256 }, { 47, 283 },
					{ 78, 306 }, { 99, 290 }, { 124, 314 }, { 111, 347 },
					{ 123, 370 }, { 126, 402 }, { 139, 420 }, { 110, 431 },
					{ 130, 470 }, { 156, 484 }, { 209, 513 }, { 228, 534 },
					{ 254, 558 }, { 270, 542 }, { 289, 522 }, { 308, 475 },
					{ 502, 537 }, { 515, 524 }, { 532, 507 }, { 552, 486 },
					{ 579, 458 }, { 590, 407 }, { 550, 396 }, { 538, 383 },
					{ 498, 345 }, { 487, 333 }, { 464, 302 }, { 458, 284 }, },
			/* 3A */
			{ { 323, 421 }, { 292, 415 }, { 279, 397 }, { 252, 405 },
					{ 238, 415 }, { 226, 424 }, { 201, 416 }, { 168, 403 },
					{ 143, 378 }, { 126, 374 }, { 108, 343 }, { 115, 315 },
					{ 86, 309 }, { 47, 274 }, { 81, 228 }, { 95, 214 },
					{ 117, 191 }, { 155, 154 }, { 181, 129 }, { 190, 106 },
					{ 162, 77 }, { 185, 65 }, { 210, 90 }, { 251, 130 },
					{ 286, 166 }, { 301, 181 }, { 327, 206 }, { 353, 221 },
					{ 373, 200 }, { 394, 180 }, { 426, 147 }, { 527, 137 },
					{ 559, 168 }, { 573, 209 }, { 579, 261 }, { 548, 293 },
					{ 534, 307 }, { 512, 328 }, { 485, 361 }, { 603, 486 },
					{ 546, 545 }, { 470, 581 }, { 426, 568 }, { 412, 554 },
					{ 369, 512 }, { 356, 500 }, { 332, 465 }, },
			/* 3B */{ { 314, 427 }, { 294, 467 }, { 317, 478 }, { 349, 507 },
					{ 358, 517 }, { 392, 550 }, { 420, 576 }, { 472, 593 },
					{ 542, 559 }, { 603, 497 }, { 484, 358 }, { 511, 330 },
					{ 535, 306 }, { 555, 286 }, { 584, 257 }, { 576, 205 },
					{ 562, 164 }, { 525, 127 }, { 482, 85 }, { 423, 139 },
					{ 395, 167 }, { 366, 196 }, { 342, 221 }, { 318, 199 },
					{ 291, 172 }, { 266, 147 }, { 231, 112 }, { 201, 83 },
					{ 171, 53 }, { 151, 72 }, { 181, 100 }, { 165, 123 },
					{ 139, 149 }, { 105, 183 }, { 77, 211 }, { 57, 231 },
					{ 47, 292 }, { 77, 303 }, { 106, 321 }, { 93, 347 },
					{ 124, 378 }, { 128, 382 }, { 142, 396 }, { 176, 428 },
					{ 192, 425 }, { 214, 429 }, { 228, 419 }, { 267, 401 },
					{ 288, 427 }, },
			/* 4 */{ { 47, 461 }, { 106, 491 }, { 143, 480 }, { 191, 469 },
					{ 199, 466 }, { 223, 448 }, { 264, 406 }, { 285, 384 },
					{ 310, 359 }, { 337, 332 }, { 367, 302 }, { 383, 284 },
					{ 461, 206 }, { 500, 169 }, { 522, 159 }, { 560, 197 },
					{ 586, 223 }, { 603, 268 }, { 552, 249 }, { 512, 229 },
					{ 497, 214 }, { 461, 206 }, { 406, 261 }, { 386, 281 },
					{ 351, 316 }, { 297, 370 }, { 258, 410 }, { 233, 436 },
					{ 195, 467 }, { 147, 479 }, { 112, 489 }, },
			/* 5 */{ { 79, 102 }, { 47, 132 }, { 55, 147 }, { 76, 156 },
					{ 108, 185 }, { 117, 195 }, { 150, 228 }, { 178, 254 },
					{ 230, 272 }, { 220, 297 }, { 257, 316 }, { 315, 348 },
					{ 357, 376 }, { 373, 391 }, { 389, 406 }, { 429, 432 },
					{ 447, 412 }, { 485, 398 }, { 503, 419 }, { 535, 408 },
					{ 568, 375 }, { 603, 425 }, { 590, 458 }, { 553, 496 },
					{ 531, 519 }, { 516, 539 }, { 508, 548 }, { 491, 538 },
					{ 475, 493 }, { 450, 487 }, { 440, 459 }, { 420, 435 },
					{ 393, 408 }, { 371, 387 }, { 361, 377 }, { 342, 364 },
					{ 314, 346 }, { 290, 330 }, { 253, 313 }, { 240, 283 },
					{ 186, 261 }, { 166, 241 }, { 122, 198 }, { 110, 186 },
					{ 77, 156 }, { 59, 147 }, { 47, 132 }, },
			/* 6 */{ { 427, 47 }, { 389, 90 }, { 475, 135 }, { 436, 171 },
					{ 353, 244 }, { 348, 305 }, { 321, 331 }, { 199, 325 },
					{ 198, 410 }, { 233, 428 }, { 236, 473 }, { 273, 528 },
					{ 325, 569 }, { 372, 603 }, { 398, 561 }, { 358, 504 },
					{ 317, 460 }, { 204, 412 }, { 182, 389 }, { 175, 354 },
					{ 208, 321 }, { 251, 277 }, { 353, 313 }, { 357, 246 },
					{ 396, 206 }, { 467, 163 }, { 464, 128 }, },
			/* 7 */{ { 405, 47 }, { 443, 117 }, { 548, 174 }, { 528, 217 },
					{ 503, 277 }, { 456, 326 }, { 392, 387 }, { 309, 424 },
					{ 272, 462 }, { 222, 498 }, { 154, 514 }, { 101, 533 },
					{ 127, 603 }, { 212, 600 }, { 197, 552 }, { 212, 505 },
					{ 274, 466 }, { 312, 427 }, { 405, 383 }, { 460, 327 },
					{ 508, 278 }, { 531, 219 }, { 545, 170 }, { 501, 141 },
					{ 434, 111 }, },
			/* 8 */{ { 337, 48 }, { 323, 87 }, { 358, 112 }, { 394, 189 },
					{ 342, 229 }, { 322, 249 }, { 363, 309 }, { 328, 346 },
					{ 350, 395 }, { 387, 435 }, { 314, 508 }, { 335, 576 },
					{ 398, 598 }, { 424, 560 }, { 413, 519 }, { 387, 505 },
					{ 363, 475 }, { 371, 454 }, { 365, 407 }, { 333, 375 },
					{ 305, 347 }, { 258, 302 }, { 226, 235 }, { 233, 195 },
					{ 244, 173 }, { 276, 131 }, { 286, 93 }, },
			/* 9 */{ { 596, 113 }, { 547, 176 }, { 513, 221 }, { 480, 263 },
					{ 461, 288 }, { 417, 345 }, { 403, 363 }, { 317, 505 },
					{ 299, 548 }, { 208, 503 }, { 153, 467 }, { 95, 463 },
					{ 47, 517 }, { 129, 519 }, { 199, 508 }, { 232, 520 },
					{ 285, 547 }, { 319, 509 }, { 406, 361 }, { 441, 316 },
					{ 481, 264 }, { 517, 218 }, { 547, 178 }, { 557, 142 }, },
			/* 10 */{ { 603, 147 }, { 544, 150 }, { 510, 157 }, { 464, 188 },
					{ 439, 205 }, { 418, 220 }, { 387, 234 }, { 357, 264 },
					{ 318, 303 }, { 288, 333 }, { 242, 325 }, { 199, 365 },
					{ 124, 364 }, { 89, 356 }, { 59, 386 }, { 60, 447 },
					{ 47, 503 }, { 122, 473 }, { 196, 425 }, { 233, 394 },
					{ 288, 336 }, { 328, 296 }, { 358, 265 }, { 386, 237 },
					{ 444, 203 }, { 463, 191 }, { 506, 160 }, { 556, 152 }, },
			/* 11 */{ { 603, 277 }, { 535, 309 }, { 486, 256 }, { 464, 271 },
					{ 444, 285 }, { 415, 307 }, { 378, 333 }, { 363, 328 },
					{ 289, 287 }, { 259, 280 }, { 204, 226 }, { 155, 249 },
					{ 110, 294 }, { 70, 334 }, { 47, 361 }, { 54, 368 },
					{ 86, 410 }, { 153, 424 }, { 211, 370 }, { 249, 331 },
					{ 264, 316 }, { 291, 290 }, { 370, 339 }, { 445, 333 },
					{ 463, 321 }, { 506, 291 }, { 556, 283 }, },
			/* 12 */{ { 445, 602 }, { 395, 588 }, { 384, 543 }, { 383, 466 },
					{ 347, 430 }, { 302, 386 }, { 272, 356 }, { 304, 316 },
					{ 331, 289 }, { 294, 243 }, { 343, 193 }, { 364, 172 },
					{ 366, 141 }, { 308, 84 }, { 255, 47 }, { 253, 124 },
					{ 229, 176 }, { 202, 209 }, { 239, 246 }, { 261, 271 },
					{ 237, 298 }, { 241, 327 }, { 269, 355 }, { 310, 395 },
					{ 346, 431 }, { 385, 470 }, { 382, 541 }, { 402, 603 }, },
			/* 13 */{ { 232, 603 }, { 165, 504 }, { 179, 351 }, { 192, 331 },
					{ 232, 372 }, { 273, 411 }, { 304, 442 }, { 325, 426 },
					{ 358, 392 }, { 374, 375 }, { 338, 291 }, { 409, 218 },
					{ 427, 185 }, { 376, 149 }, { 332, 163 }, { 317, 128 },
					{ 364, 81 }, { 488, 49 }, { 471, 74 }, { 425, 47 },
					{ 359, 82 }, { 306, 136 }, { 255, 89 }, { 205, 80 },
					{ 193, 197 }, { 186, 261 }, { 178, 339 }, { 162, 501 },
					{ 189, 583 }, },
			/* 14 */{ { 76, 603 }, { 139, 560 }, { 160, 537 }, { 196, 500 },
					{ 235, 461 }, { 308, 413 }, { 281, 363 }, { 353, 319 },
					{ 411, 236 }, { 417, 169 }, { 429, 106 }, { 483, 47 },
					{ 545, 108 }, { 574, 187 }, { 483, 189 }, { 437, 184 },
					{ 410, 205 }, { 361, 307 }, { 330, 338 }, { 294, 379 },
					{ 305, 413 }, { 240, 452 }, { 204, 490 }, { 169, 526 },
					{ 149, 546 }, },
			/* 15 */{ { 603, 115 }, { 563, 160 }, { 514, 210 }, { 468, 256 },
					{ 432, 294 }, { 372, 355 }, { 331, 397 }, { 197, 535 },
					{ 148, 520 }, { 104, 493 }, { 47, 440 }, { 72, 399 },
					{ 164, 306 }, { 224, 347 }, { 304, 375 }, { 334, 330 },
					{ 320, 280 }, { 342, 251 }, { 385, 269 }, { 466, 262 },
					{ 534, 193 }, },
			/* 16 */{ { 133, 47 }, { 143, 84 }, { 161, 123 }, { 173, 134 },
					{ 204, 165 }, { 229, 190 }, { 271, 231 }, { 306, 249 },
					{ 362, 280 }, { 405, 310 }, { 417, 321 }, { 433, 337 },
					{ 459, 364 }, { 479, 385 }, { 488, 413 },{498,449}, { 487, 463 },
					{ 462, 514 }, { 481, 543 }, { 456, 569 }, { 406, 555 },
					{ 376, 523 }, { 360, 498 }, { 331, 479 }, { 314, 499 },
					{ 332, 522 }, { 360, 554 }, { 387, 569 }, { 395, 576 },
					{ 407, 588 }, { 430, 603 }, { 456, 577 }, { 489, 547 },
					{ 520, 509 }, { 508, 487 }, { 496, 475 }, { 497, 441 },
					{ 489, 412 }, { 480, 385 }, { 460, 363 }, { 434, 337 },
					{ 415, 317 }, { 404, 307 }, { 386, 295 }, { 360, 277 },
					{ 337, 262 }, { 301, 246 }, { 273, 231 }, { 232, 191 },
					{ 219, 177 }, { 177, 137 }, { 165, 125 }, { 143, 84 },
					{ 141, 92 }, },
			/* 20 */{ { 603, 281 }, { 537, 245 }, { 464, 291 }, { 417, 279 },
					{ 390, 305 }, { 359, 337 }, { 330, 366 }, { 314, 381 },
					{ 299, 394 }, { 296, 400 }, { 235, 456 }, { 184, 427 },
					{ 160, 401 }, { 143, 383 }, { 134, 374 }, { 79, 376 },
					{ 51, 401 }, { 47, 370 }, { 70, 348 }, { 87, 331 },
					{ 69, 390 }, { 89, 410 }, { 112, 409 }, { 139, 383 },
					{ 154, 369 }, { 187, 336 }, { 196, 327 }, { 169, 297 },
					{ 154, 282 }, { 164, 240 }, { 182, 223 }, { 213, 194 },
					{ 241, 223 }, { 270, 251 }, { 301, 282 }, { 324, 305 },
					{ 348, 329 }, { 362, 335 }, { 392, 305 }, { 401, 296 },
					{ 417, 280 }, { 466, 310 }, { 512, 307 }, { 524, 299 },
					{ 557, 279 }, { 581, 276 }, },
			/* 50 */{ { 517, 57 }, { 527, 206 }, { 446, 289 }, { 368, 366 },
					{ 308, 429 }, { 200, 540 }, { 139, 603 }, { 145, 490 },
					{ 122, 406 }, { 159, 358 }, { 229, 385 }, { 366, 377 },
					{ 480, 261 }, },
			/* 56 */{ { 47, 275 }, { 234, 132 }, { 350, 224 }, { 493, 368 },
					{ 556, 431 }, { 585, 459 }, { 603, 502 }, { 546, 478 },
					{ 512, 444 }, { 473, 412 }, { 330, 518 }, { 287, 476 },
					{ 256, 445 }, { 241, 431 }, { 211, 411 }, { 170, 383 },
					{ 135, 360 }, { 79, 336 }, },
			/* 57 */{ { 419, 56 }, { 412, 141 }, { 530, 202 }, { 477, 251 },
					{ 365, 354 }, { 356, 435 }, { 283, 531 }, { 159, 580 },
					{ 129, 550 }, { 120, 501 }, { 165, 456 }, { 223, 396 },
					{ 292, 324 }, { 388, 226 }, },
			/* 58 */{ { 238, 56 }, { 247, 191 }, { 174, 266 }, { 103, 337 },
					{ 151, 525 }, { 294, 556 }, { 390, 603 }, { 478, 513 },
					{ 522, 404 }, { 547, 312 }, { 466, 258 }, { 341, 204 }, }, };
}