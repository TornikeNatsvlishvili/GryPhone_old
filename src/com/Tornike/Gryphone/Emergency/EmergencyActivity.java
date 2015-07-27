package com.Tornike.Gryphone.Emergency;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.News.NewsAdapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmergencyActivity extends Activity {
	// List Adapter
	EmergencyListAdapter listAdapter;

	// Views
	ListView listView;
	TextView topText;
	ImageView topButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emergency_layout);
		
		// Emergencylistadapter initialize
		listAdapter = new EmergencyListAdapter(this);

		// View stuff initialize
		topText = (TextView) findViewById(R.id.top_bar_text);
		listView = (ListView) findViewById(R.id.emergency_list_view);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		listView.setAdapter(listAdapter);
		listAdapter.addItem("Emergency Maintenance", 
				"519-824-4120 ext 53854", "Monday - Friday 8:15am - 4:30pm");
		listAdapter.addItem("Campus Police",
				"519-824-4120 ext 52245", "After Hours");
		listAdapter.addItem("Fire Prevention Services",
				"519-824-4120 ext 2000", "");
		listAdapter.addItem("Environmental Health\nand Safety",
				"519-824-4120 ext 53282", "");
		topText.setText("Emergency");

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				call(arg2);
			}
		});
	}
	
	private void call(int index) {
	    try {
	    	String phoneNumS = listAdapter.phoneNumberList.get(index).replace(" ","");
	    	phoneNumS = phoneNumS.replace("-", "");
	    	phoneNumS = phoneNumS.replace("ext", ";");
	        Intent callIntent = new Intent(Intent.ACTION_DIAL);
	        callIntent.setData(Uri.parse("tel:"+phoneNumS));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException e) {
	        Log.e("Error", "Call Intent", e);
	    }
	}
}
