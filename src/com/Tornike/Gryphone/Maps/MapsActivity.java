package com.Tornike.Gryphone.Maps;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Tornike.Gryphone.R;
import com.google.android.maps.*;

public class MapsActivity extends MapActivity {
	// View declarations
	MapView mapView;
	ImageView topButton;
	TextView topText;
	ProgressBar progBar;
	ImageButton startGpsBtn, buildingBtn;

	// Map stuff
	MyLocationListener locListener;
	List<Overlay> mapOverlays;
	Drawable currentLocMarker, buildingMarker, lastKnownLocMarker;
	MapItemizedOverlay currentLocationOverlay, buildingOverlay,
			lastKnownLocationOverlay;
	MapController mapController;
	GeoPoint UoGLocation = new GeoPoint((int) (43.530476E6),
			(int) (-80.226156E6));
	GeoPoint userLocation;

	// GPS Stuff
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
																		// meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1;// in
																// miliseconds
	LocationManager lm;

	// Dialog box
	AlertDialog.Builder gpsOnDialog;
	AlertDialog.Builder buildingListDialog;
	boolean dialogShown = false;

	// Variables
	boolean foundGps = false;
	static boolean gpsOn = false;

	final CharSequence[] buildingShortNames = { "AC", "ANNU", "ALEX", "AXEL",
			"BIO", "BWH", "CAF", "CRSC", "DH", "EBA", "ECB", "FS", "GRHM",
			"HUTT", "JHNH", "JTP", "LA", "MAC", "MACK", "MACN", "MACS", "MASS",
			"MCLN", "MINS", "MLIB", "MSAC", "OVC", "PAHL", "REYN", "RICH",
			"ROZH", "SCIE", "TCI", "THRN", "VSER", "WMEM", "ZAV", "ZOOA",
			"ZOOB" };

	final CharSequence[] buildingNames = { "Athletics Centre",
			"Animal Science & Nutrition", "Alexander Hall",
			"Axelrod Institute of Ichthyology",
			"Biodiversity Institute of Ontario", "Blackwood Hall",
			"Central Animal Facility", "Crop Science", "Day Hall",
			"Environmental Biology Annex #1",
			"Edmund C. Bovey Administrative Building", "Food Science",
			"Graham Hall", "H.L. Hutt Building", "Johnston Hall",
			"John T. Powell Building", "Landscape Architecture",
			"Macdonald Hall", "MacKinnon Building", "MacNaughton Building",
			"Macdonald Stewart Hall", "Massey Hall", "MacLachlan Building",
			"Macdonald Institute", "McLaughlin Library",
			"Macdonald Stewart Art Centre", "OVC Main Building",
			"OVC Pathobiology AHL", "Reynolds Building", "Richards Building",
			"Rozanski Hall", "Science Complex ", "TransCanada Institute",
			"Thornbrough Building", "Vehicle Services Building",
			"War Memorial Hall", "Zavitz Hall", "Zoology Annex #1",
			"Zoology Annex #2" };

	final GeoPoint[] buildingLoc = {
			new GeoPoint((int) (43.533247E6), (int) (-80.224641E6)),
			new GeoPoint((int) (43.529525E6), (int) (-80.22985E6)),
			new GeoPoint((int) (43.529379E6), (int) (-80.227691E6)),
			new GeoPoint((int) (43.529379E6), (int) (-80.227691E6)),
			new GeoPoint((int) (43.528167E6), (int) (-80.228994E6)),
			new GeoPoint((int) (43.532941E6), (int) (-80.227028E6)),
			new GeoPoint((int) (43.528879E6), (int) (-80.232736E6)),
			new GeoPoint((int) (43.53179E6), (int) (-80.22462E6)),
			new GeoPoint((int) (43.531905E6), (int) (-80.226856E6)),
			new GeoPoint((int) (43.527963E6), (int) (-80.228184E6)),
			new GeoPoint((int) (43.530528E6), (int) (-80.226202E6)),/*
																	 * ??????????
																	 * ???? -
																	 * Edmund C.
																	 * Bovey
																	 * Building
																	 */
			new GeoPoint((int) (43.529941E6), (int) (-80.23051E6)),
			new GeoPoint((int) (43.528177E6), (int) (-80.2279E6)),
			new GeoPoint((int) (43.530264E6), (int) (-80.227047E6)),
			new GeoPoint((int) (43.533064E6), (int) (-80.228552E6)),
			new GeoPoint((int) (43.53347E6), (int) (-80.223549E6)),
			new GeoPoint((int) (43.532477E6), (int) (-80.225097E6)),
			new GeoPoint((int) (43.534182E6), (int) (-80.230933E6)),
			new GeoPoint((int) (43.532585E6), (int) (-80.227157E6)),
			new GeoPoint((int) (43.530656E6), (int) (-80.227629E6)),
			new GeoPoint((int) (43.53425E6), (int) (-80.232612E6)),
			new GeoPoint((int) (43.531811E6), (int) (-80.228436E6)),
			new GeoPoint((int) (43.531261E6), (int) (-80.228613E6)),
			new GeoPoint((int) (43.534421E6), (int) (-80.232256E6)),
			new GeoPoint((int) (43.531555E6), (int) (-80.227618E6)),
			new GeoPoint((int) (43.532951E6), (int) (-80.23294E6)),
			new GeoPoint((int) (43.530159E6), (int) (-80.233369E6)),
			new GeoPoint((int) (43.530575E6), (int) (-80.2311E6)),
			new GeoPoint((int) (43.530794E6), (int) (-80.229053E6)),
			new GeoPoint((int) (43.531471E6), (int) (-80.225352E6)),
			new GeoPoint((int) (43.532235E6), (int) (-80.225805E6)),
			new GeoPoint((int) (43.530229E6), (int) (-80.228675E6)),
			new GeoPoint((int) (43.534275E6), (int) (-80.233892E6)),
			new GeoPoint((int) (43.530594E6), (int) (-80.225075E6)),
			new GeoPoint((int) (43.532994E6), (int) (-80.22581E6)),
			new GeoPoint((int) (43.532609E6), (int) (-80.231336E6)),
			new GeoPoint((int) (43.530991E6), (int) (-80.227194E6)),
			new GeoPoint((int) (43.530528E6), (int) (-80.226202E6)),/*
																	 * ??????????
																	 * ???? -
																	 * Zoology
																	 * Annex 1
																	 */
			new GeoPoint((int) (43.528309E6), (int) (-80.228538E6)) };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		// View stuff
		topText = (TextView) findViewById(R.id.top_bar_text);
		topText.setText("Map");
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		startGpsBtn = (ImageButton) findViewById(R.id.top_bar_gps_btn);
		progBar = (ProgressBar) findViewById(R.id.top_bar_progress_bar);
		buildingBtn = (ImageButton) findViewById(R.id.top_bar_building_btn);

		// View init
		progBar.setVisibility(progBar.INVISIBLE);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// Map initilization
		mapController = mapView.getController();
		mapController.setCenter(UoGLocation);
		mapController.setZoom(18);
		mapOverlays = mapView.getOverlays();
		currentLocMarker = this.getResources().getDrawable(
				R.drawable.current_loc);
		buildingMarker = MapsActivity.this.getResources().getDrawable(
				R.drawable.map_marker);
		lastKnownLocMarker = MapsActivity.this.getResources().getDrawable(
				R.drawable.last_known_loc);
		currentLocationOverlay = new MapItemizedOverlay(currentLocMarker, this);
		buildingOverlay = new MapItemizedOverlay(buildingMarker, this);
		lastKnownLocationOverlay = new MapItemizedOverlay(lastKnownLocMarker,
				this);
		locListener = new MyLocationListener();

		// GPS initilization
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gpsOn = false;
				finish();
			}
		});
		startGpsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progBar.setVisibility(progBar.VISIBLE);
				progBar.setAnimation(getSlideDownAnimation());
				gpsOn = true;
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						MINIMUM_TIME_BETWEEN_UPDATES,
						MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locListener);
			}
		});
		buildingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buildingListDialog.show();
			}
		});

		// Dialog init
		gpsOnDialog = new AlertDialog.Builder(this);
		gpsOnDialog.setMessage("This feature requires GPS. Enable it?");
		gpsOnDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
		gpsOnDialog.setNegativeButton("No", null);

		buildingListDialog = new AlertDialog.Builder(this);
		buildingListDialog.setTitle("Pick a building:");
		buildingListDialog.setItems(buildingNames,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						buildingOverlay.clear();
						OverlayItem oItem = new OverlayItem(buildingLoc[item],
								(String) (buildingNames[item] + " ("
										+ buildingShortNames[item] + ")"), ""
										+ item);
						if (buildingOverlay.size() == 1) {
							buildingOverlay.editOverlay(0, oItem);
						} else {
							buildingOverlay.addOverlay(oItem);
							mapOverlays.add(buildingOverlay);
						}
						mapController.setZoom(19);
						mapController.animateTo(buildingLoc[item]);
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gpsOn) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					MINIMUM_TIME_BETWEEN_UPDATES,
					MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locListener);
		} else {
			showCurrentLocation(false);
		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String buildingName = extras.getString("buildingName");
			int index = -1;

			for (int a = 0; a < buildingShortNames.length; a++) {
				if (buildingName.equals(buildingShortNames[a])) {
					index = a;
				}
			}
			if (index == -1) {
				Toast.makeText(this, "Building could not be found!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			buildingOverlay.clear();
			OverlayItem oItem = new OverlayItem(buildingLoc[index],
					(String) (buildingNames[index] + " ("
							+ buildingShortNames[index] + ")"), "" + index);
			if (buildingOverlay.size() == 1) {
				buildingOverlay.editOverlay(0, oItem);
			} else {
				buildingOverlay.addOverlay(oItem);
				mapOverlays.add(buildingOverlay);
			}
			mapController.setZoom(19);
			mapController.animateTo(buildingLoc[index]);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		/* GPS, as it turns out, consumes battery like crazy */
		lm.removeUpdates(locListener);
		// let gps on dialog ask again
		dialogShown = false;
	}

	protected void showCurrentLocation(boolean animateTo) {
		Location location = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			userLocation = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));
			OverlayItem item = new OverlayItem(userLocation, "You",
					"Your last known location");
			if (lastKnownLocationOverlay.size() != 0) {
				lastKnownLocationOverlay.editOverlay(0, item);
			} else {
				lastKnownLocationOverlay.addOverlay(item);
				mapOverlays.add(lastKnownLocationOverlay);
			}
			if (animateTo) {
				mapController.animateTo(userLocation);
			}

			// String message = String.format(
			// "Current Location \n Longitude: %1$s \n Latitude: %2$s",
			// location.getLongitude(), location.getLatitude());
			// Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG)
			// .show();
		}
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

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			Log.e("LOCATION", "Location Changed!");
			// refresh mapview
			mapView.invalidate();
			// Remove last known locs
			lastKnownLocationOverlay.clear();

			userLocation = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));
			OverlayItem item = new OverlayItem(userLocation, "You",
					"Your current location!");

			if (currentLocationOverlay.size() == 1) {
				currentLocationOverlay.editOverlay(0, item);
			} else {
				currentLocationOverlay.addOverlay(item);
				mapOverlays.add(currentLocationOverlay);
			}

			// take off loading
			if (progBar.getVisibility() == progBar.VISIBLE) {
				progBar.setAnimation(getSlideUpAnimation());
				progBar.setVisibility(progBar.INVISIBLE);
			}

			// String message = String.format(
			// "New Location \n Longitude: %1$s \n Latitude: %2$s",
			// location.getLongitude(), location.getLatitude());
			// Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG)
			// .show();
			// Log.e("GPS","Loc changed!");
		}

		public void onStatusChanged(String s, int status, Bundle b) {
			/* This is called when the GPS status alters */
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				Toast.makeText(MapsActivity.this,
						"Status Changed: Out of Service", Toast.LENGTH_SHORT)
						.show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Toast.makeText(MapsActivity.this,
						"Status Changed: Temporarily Unavailable",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

		public void onProviderDisabled(String s) {
			if (!dialogShown) {
				gpsOnDialog.show();
				dialogShown = true;
			}
		}

		public void onProviderEnabled(String s) {
			// Toast.makeText(MapsActivity.this,
			// "Provider enabled by the user. GPS turned on",
			// Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBackPressed() {
		gpsOn = false;
		super.onBackPressed();
	}
}