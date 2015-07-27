package com.Tornike.Gryphone.Maps;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Tornike.Gryphone.Events.Students.StudentEventsActivity;
import com.Tornike.Gryphone.WebViewer.WebViewerActivity;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapItemizedOverlay extends ItemizedOverlay {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;

	public MapItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public MapItemizedOverlay(Drawable defaultMarker, Context mContext) {
		super(boundCenterBottom(defaultMarker));
		context = mContext;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public void editOverlay(int position, OverlayItem overlay) {
		if (mOverlays.size() >= position) {
			mOverlays.set(position, overlay);
			populate();
		}
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void clear() {
		mOverlays.clear();
	}

	@Override
	protected boolean onTap(int index) {
		final OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		int buildingIndx = 0;
		boolean skip = false;
		try{
			buildingIndx = Integer.parseInt(item.getSnippet());
		}catch(Exception e){
			skip = true;
		}
		if(!skip){
			final int indx = buildingIndx;
			TextView txtView = new TextView(context);
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			
			// View display 
			txtView.setLayoutParams(ll);
			txtView.setGravity(Gravity.CENTER_HORIZONTAL);
			txtView.setTextSize(20);
			txtView.setTextColor(Color.BLUE);
			txtView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, WebViewerActivity.class);
					intent.putExtra("url", buildingWebAdress[indx]);
					intent.putExtra("title", item.getTitle());
					context.startActivity(intent);
				}
			});
			
			// dialog stuff
			dialog.setTitle(item.getTitle());
			dialog.setMessage("For more information visit: ");

			// textview stuff
			SpannableString contentUnderline = new SpannableString(
					buildingWebAdress[buildingIndx]);
			contentUnderline.setSpan(new UnderlineSpan(), 0,
					contentUnderline.length(), 0);
			txtView.setText(contentUnderline);

			// show view
			dialog.setView(txtView);
		}else{
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
		}
		
		dialog.show();
		return true;
	}
	
	final CharSequence[] buildingWebAdress = {
			"http://www.uoguelph.ca/campus/map/athletics/",
			"http://www.uoguelph.ca/campus/map/animalscience/",
			"http://www.uoguelph.ca/campus/map/alexander/",
			"http://www.uoguelph.ca/campus/map/axelrod/",
			"http://www.uoguelph.ca/campus/map/biodiversity/",
			"http://www.uoguelph.ca/campus/map/blackwood/",
			"http://www.uoguelph.ca/campus/map/centralanimal/",
			"http://www.uoguelph.ca/campus/map/cropscience/",
			"http://www.uoguelph.ca/campus/map/dayhall/",
			"http://www.uoguelph.ca/campus/map/envbioann1/",
			"http://www.uoguelph.ca/campus/map/bovey/",
			"http://www.uoguelph.ca/campus/map/foodscience/",
			"http://www.uoguelph.ca/campus/map/graham/",
			"http://www.uoguelph.ca/campus/map/hutt/",
			"http://www.uoguelph.ca/campus/map/johnston/",
			"http://www.uoguelph.ca/campus/map/powell/",
			"http://www.uoguelph.ca/campus/map/la/",
			"http://www.uoguelph.ca/campus/map/macdonald/",
			"http://www.uoguelph.ca/campus/map/mackinnon/",
			"http://www.uoguelph.ca/campus/map/macnaughton/",
			"http://www.uoguelph.ca/campus/map/hafa/",
			"http://www.uoguelph.ca/campus/map/massey/",
			"http://www.uoguelph.ca/campus/map/maclachlan/",
			"http://www.uoguelph.ca/campus/map/csahs/",
			"http://www.uoguelph.ca/campus/map/library/",
			"http://www.uoguelph.ca/campus/map/msac/",
			"http://www.uoguelph.ca/campus/map/ovc/",
			"http://www.uoguelph.ca/campus/map/ahl/",
			"http://www.uoguelph.ca/campus/map/reynolds/",
			"http://www.uoguelph.ca/campus/map/richards/",
			"http://www.uoguelph.ca/campus/map/rozanski/",
			"http://www.uoguelph.ca/campus/map/sciencecomplex/",
			"http://www.uoguelph.ca/campus/map/transcaninst/",
			"http://www.uoguelph.ca/campus/map/thornbrough/",
			"http://www.uoguelph.ca/campus/map/vehicle/",
			"http://www.uoguelph.ca/campus/map/warmem/",
			"http://www.uoguelph.ca/campus/map/zavitz/",
			"http://www.uoguelph.ca/campus/map/zooann1/",
			"http://www.uoguelph.ca/campus/map/zooann2/" };
}
