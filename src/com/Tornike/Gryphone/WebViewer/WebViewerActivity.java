package com.Tornike.Gryphone.WebViewer;

import com.Tornike.Gryphone.GryphoneActivity;
import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.Campus.CampusEventItem;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.*;

public class WebViewerActivity extends Activity {
	// Views
	WebView webView;
	ProgressBar loading;
	TextView topTitle;
	ImageView topButton;
	
	// Variables
	Boolean startedLoading = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_viewer_layout);

		webView = (WebView) findViewById(R.id.web_viewer_view);
		loading = (ProgressBar) findViewById(R.id.web_viewer_loading);
		topTitle = (TextView) findViewById(R.id.top_bar_text);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);  
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setInitialScale(100);
		
		// So links are handled by this webview not the android internet
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (!startedLoading) {
					loading.setVisibility(loading.VISIBLE);
					loading.setAnimation(getSlideDownAnimation());
					startedLoading = true;
				}

				if (progress == 100) {
					loading.setAnimation(getSlideUpAnimation());
					loading.setVisibility(loading.INVISIBLE);
					startedLoading = false;
				}
			}
		});
		getWindowTitle();
		webView.loadUrl(recieveUrl());
	}
	
	public void getWindowTitle(){
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			topTitle.setText(extras.getString("title"));
		}else{
			topTitle.setText("Internet");			
		}
	}
	
	public String recieveUrl() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			return extras.getString("url");
		}
		return "ERROR";
	}

	public Animation getSlideUpAnimation() {
		Animation temp = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f);
		temp.setDuration(1000);
		return temp;
	}

	public Animation getSlideDownAnimation() {
		Animation temp = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		// set animation durations
		temp.setDuration(1000);
		return temp;
	}
	
	@Override
	public void onBackPressed() {
		if(webView.canGoBack()){
			webView.goBack();
			return;
		}else{
			finish();
			return;
		}
	}
}
