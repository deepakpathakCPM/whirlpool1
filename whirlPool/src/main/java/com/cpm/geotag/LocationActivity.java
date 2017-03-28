package com.cpm.geotag;



/*
public class LocationActivity 

{}*/


import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;


import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cpm.Constants.CommonString1;
import com.cpm.Constants.TMSCommonString;
import com.cpm.GetterSetter.Storenamebean;
import com.cpm.capitalfoods.R;


import com.cpm.database.GSKDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class LocationActivity extends AppCompatActivity implements  OnMapReadyCallback,ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	private static final String TAG = "LocationActivity";
	protected static final String PHOTO_TAKEN = "photo_taken";
	LocationManager locationManager;
	Geocoder geocoder;
	//TextView locationText;
	MapView map;
	protected Button _button;
	protected Button _buttonsave;
	public Camera camera;
	File file;
	protected ImageView _image;
	protected TextView _field;
	Button imagebtn;
	//MapController mapController;
	//GeoPoint point;
	protected boolean _taken;
	Button capture_1;
	/*Button capture_2;
	Button capture_3;*/
	public String text;
	public View view;
	Location location;
	GeotaggingBeans data = new GeotaggingBeans();
	private LocationManager locmanager = null;
	protected String diskpath = "";
	protected String _path;
	/*protected String _path1;
	protected String _path2;*/
	boolean enabled;
	protected String _pathforcheck = "";
	/*protected String _path1forcheck = "";
	protected String _path2forcheck = "";*/
	//GSKDatabase db;
	private TextView mstorestatus;
	public static ArrayList<Storenamebean> storedetails = new ArrayList<Storenamebean>();

	//String storename;
	String storeid;
	String status;
	//String storeaddress = "";
	String storelatitude = "0";
	String storelongitude = "0";
	protected int resultCode;
	public BitmapDrawable bitmapDrawable;
	int abc;
	int abd;
	int abe;
	private GoogleMap  mMap;
	TextView Stroename;
	static Editor e1, e2, e3;
	double lat;
	double longitude;
	private Bitmap mBubbleIcon, mShadowIcon;
	public ProgressBar mprogress;
	double latitude1;
	double longitude1;
	private GoogleApiClient mGoogleApiClient;
	LocationRequest mLocationRequest;
	String store_cd,visit_date,username,intime,WINDOWS_CD;
	 Marker currLocationMarker;
	 LatLng latLng;
	 GoogleMap mGoogleMap;
	 private boolean mRequestingLocationUpdates = false;
	 private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	 private static int UPDATE_INTERVAL = 1000; // 10 sec
	    private static int FATEST_INTERVAL = 500; // 5 sec
	    private static int DISPLACEMENT = 10; // 10 meters
	    Location mLastLocation;
	    private SharedPreferences preferences = null;
	    String currentdate;
	    
	    
	/*class MapOverlay extends ViewOverlay {
		
	
		*//*public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(point, screenPts);

			// ---add the marker---
			mprogress.setVisibility(View.INVISIBLE);
			Bitmap mBubbleIcon = BitmapFactory.decodeResource(getResources(),
					R.drawable.pointer);
			canvas.drawBitmap(mBubbleIcon, screenPts.x - mBubbleIcon.getWidth()
					/ 2, screenPts.y - mBubbleIcon.getHeight(), null);
			return true;
		}*//*

		*//*public void onMapReady(GoogleMap arg0) {
			// TODO Auto-generated method stub
			
		}*//*
	}
*/
	//*//** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpslocationscreen);
		
		 preferences = PreferenceManager.getDefaultSharedPreferences(this);
		 currentdate = preferences.getString(TMSCommonString.KEY_ISD_DATE, "");




		storeid = preferences.getString(CommonString1.KEY_STORE_CD, null);
		visit_date = preferences.getString(CommonString1.KEY_DATE, null);
		username= preferences.getString(CommonString1.KEY_USERNAME, null);
		intime= preferences.getString(CommonString1.KEY_STORE_IN_TIME, "");

		//STORE_TYPE_CD= preferences.getString(CommonString1.KEY_STORE_TYPE_CD, "");
		//STATE_CD= preferences.getString(CommonString1.KEY_STATE_CD, "");

		
		_image = (ImageView) findViewById(R.id.image);

		_buttonsave = (Button) findViewById(R.id.savedetails);
		capture_1 = (Button) findViewById(R.id.StoreFront);
		/*capture_2 = (Button) findViewById(R.id.captureimageinside);
		capture_3 = (Button) findViewById(R.id.captureimageinside2);*/
	//	Stroename = (TextView) findViewById(R.id.dyanmic_storename);
	//	locationText = (TextView) findViewById(R.id.lblLocationInfo1);
		
		//mstorestatus = (TextView)findViewById(R.id.address1);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

		//AppCompatActivity activity = (AppCompatActivity) getApplicationContext();
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//for crate home button







		//mprogress = (ProgressBar)findViewById(R.id.progressBar1);

		//map = (MapView) this.findViewById(R.id.mapview);
		
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
		
		//map.setBuiltInZoomControls(true);
		//map.setStreetView(true);
		GSKDatabase data1 = new GSKDatabase(getApplicationContext());
		data1.open();
		storedetails = new ArrayList<Storenamebean>();
		//storedetails = data1.getstorename();
		//data1.close();
		
		/*if(storedetails.get(0).getStatus().equalsIgnoreCase("p"))
		{
			
			mstorestatus.setVisibility(View.VISIBLE);
			locationText.setVisibility(View.VISIBLE);
		}
		if(storedetails.get(0).getStatus().equalsIgnoreCase("N"))
		{
			
			mstorestatus.setVisibility(View.INVISIBLE);
			locationText.setVisibility(View.INVISIBLE);
		}*/
		
		
//		storename = storedetails.get(0).getStorename();
//		storeid =  storedetails.get(0).getStoreid();
//		storeaddress = storedetails.get(0).getStoreAddress();
//		storelatitude = storedetails.get(0).getLatitude();
//		storelongitude = storedetails.get(0).getLongitude();
		
		//Intent intent = getIntent();
		//storename = intent.getStringExtra("StoreName");
		//storeid = intent.getStringExtra("Storeid");
		//storeaddress = intent.getStringExtra("Storeaddress");
		
		/*storelatitude = intent.getStringExtra("storelatitude");
		storelongitude = intent.getStringExtra("storelongitude");*/
		
		//storelatitude = "28.526338934898376";
	    //storelongitude = "77.27933943271637";
		ImageView img = new ImageView(getApplicationContext());

		//mapController = mMap.getController();
		//mapController.setZoom(15);

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
		geocoder = new Geocoder(this);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		
		 if (checkPlayServices()) {

	            // Building the GoogleApi client
	            buildGoogleApiClient();
	        }	
		
		
		
		 if (checkPlayServices()) {

	            // Building the GoogleApi client
	            buildGoogleApiClient();

	            createLocationRequest();
	        }
		
		// mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		
		
		if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {

			int latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
			int longi = (int) (Double.parseDouble(storelongitude) * 1000000);

		//	point = new GeoPoint(latiti, longi);
			//mapController.setCenter(point);
			//MapOverlay mapOverlay = new MapOverlay();
			//List<Overlay> listOfOverlays = map.getOverlays();
			//listOfOverlays.clear();
			//listOfOverlays.add(mapOverlay);

		}

		//Stroename.setText(storename);
		//locationText.setText(storeaddress);

		_pathforcheck = storeid+ "front.jpg";
	/*	_path1forcheck = storeid + "inside.jpg";
		_path2forcheck = storeid + "inside1.jpg";*/
		
		
		_buttonsave.setOnClickListener(new OnClickListener() {
			
			/*@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		}; {*/

			public void onClick(View v) {
				// TODO Auto-generated method stub



if(_pathforcheck!=null)

{
				
				if (!(storelatitude.equals("0"))
						&& !(storelongitude.equals("0"))) {
					lat = Double.parseDouble(storelatitude);
					longitude = Double.parseDouble(storelongitude);
					
					

				} else {
					lat = data.getLatitude();
					longitude = data.getLongitude();

				}

				if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

					 status = "Y";
					GSKDatabase data = new GSKDatabase(
							getApplicationContext());
					data.open();
					//data.updateGeoTagStatus(storeid, status, lat, longitude);

					data.updateOutTime(status, store_cd,visit_date);

					data.InsertStoregeotagging(storeid, lat, longitude, _pathforcheck,status);
					data.close();
					if (isNetworkOnline() == true) {

						Intent intent2 = new Intent(LocationActivity.this,
								UploadGeotaggingActivity.class);

						startActivity(intent2);

						LocationActivity.this.finish();

					}


					
					/*else {

						// display toast
						AlertDialog.Builder builder = new AlertDialog.Builder(v
								.getContext());

						builder.setMessage(
								"Network Not Available.Data is saved locally.")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {

												*//*Intent intent = new Intent(
														LocationActivity.this,
														MainMenuActivity.class);

												startActivity(intent);*//*

												LocationActivity.this.finish();

											}
										});
						AlertDialog alert = builder.create();
						alert.show();

					}*/

				} 
				
				
				/*else if (!(data.getLatitude() == 0)
						&& !(data.getLongitude() == 0)) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							LocationActivity.this);
					builder.setMessage(
							"Do you want to save geo location without store images.")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

										//	SavePartialData(lat, longitude);

										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();

					alert.show();

				}*/
				/*else if (!(storelatitude.equals("0"))&& !(storelongitude.equals("0"))) {

					if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)
							) {

						String status = "Y";
						GSKDatabase data = new GSKDatabase(
								getApplicationContext());
						data.open();
						data.updateGeoTagStatus(storeid, status, lat, longitude);
						data.InsertStoregeotagging(storeid, lat, longitude,
								_pathforcheck);
						data.close();
						if (isNetworkOnline() == true) {

							Intent intent1 = new Intent(LocationActivity.this,
									UploadGeotaggingActivity.class);

							startActivity(intent1);

							LocationActivity.this.finish();

						}
						else {

							AlertDialog.Builder builder = new AlertDialog.Builder(
									v.getContext());

							builder.setMessage(
									"Network Not Available.Data is saved locally.")
									.setCancelable(false)
									.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {

													*//*Intent intent = new Intent(
															LocationActivity.this,
															MainMenuActivity.class);

													startActivity(intent);
*//*
													LocationActivity.this
															.finish();

												}
											});
							AlertDialog alert = builder.create();
							alert.show();

						}

					} 
					else {

						LayoutInflater inflater = getLayoutInflater();
						View layout = inflater
								.inflate(
										R.layout.toastview,
										(ViewGroup) findViewById(R.id.toast_layout_root));

						ImageView image = (ImageView) layout
								.findViewById(R.id.image);
						image.setImageResource(R.drawable.danger_icon);
						TextView text = (TextView) layout
								.findViewById(R.id.text);
						text.setText("Please Capture  store images.");

						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setView(layout);
						toast.show();

					}
				}*/ else {

					AlertDialog.Builder builder = new AlertDialog.Builder(v
							.getContext());

					builder.setMessage("Please take Store Front image")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();

				}

			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(v
						.getContext());

				builder.setMessage("Please take Store Front image")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}




			}







		});
		

		
		/* * locationManager =
		 * (LocationManager)this.getSystemService(LOCATION_SERVICE);
		 * 
		 * geocoder = new Geocoder(this);
		 * 
		 * Location location =
		 * locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
		 

		capture_1.setOnClickListener(new ButtonClickHandler());
		//capture_2.setOnClickListener(new ButtonClickHandler());
		//capture_3.setOnClickListener(new ButtonClickHandler());

		
	//	  latitude1 = location.getLatitude();
	 //      longitude1 = location.getLongitude();

		locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);


		if (!enabled) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					LocationActivity.this);

			// Setting Dialog Title
			alertDialog.setTitle("GPS IS DISABLED...");

			// Setting Dialog Message
			alertDialog.setMessage("Click ok to enable GPS.");

			// Setting Positive "Yes" Button
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
					});

			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to invoke NO event

							dialog.cancel();
						}
					});

			// Showing Alert Message
			alertDialog.show();

		}


	}



	//@Override
	/*protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//locationManager.removeUpdates(this);
	}*/

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 10, this);
		
		/*checkPlayServices();
		
		 if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
	            startLocationUpdates();
	        }*/
		
		
		if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

			capture_1.setBackgroundResource(R.drawable.camera_done);

		}
		/*if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

			capture_2.setBackgroundResource(R.drawable.active_camera);

		}
		if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

			capture_3.setBackgroundResource(R.drawable.active_camera);

		}*/




	}

	/*@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
*/
	/*public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		
		 * Log.d(TAG, "onLocationChanged with location " + location.toString());
		 * // Displays lat, long, altitude and bearing text =
		 * String.format("Lat:\t %f\t\t\t\tLong:\t %f", location.getLatitude(),
		 * location.getLongitude()); this.locationText.setText(text);
		 

	}*/

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void gobacktogeoselection() {
		Intent intent = new Intent(LocationActivity.this, GeoTagging.class);

		startActivity(intent);
		this.finish();

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	protected void startCameraActivity() {
		Log.i("MakeMachine", "startCameraActivity()");
		 file = new File(diskpath);
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);

	}

	public boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("MakeMachine", "resultCode: " + resultCode);
		switch (resultCode) {
		case 0:
			Log.i("MakeMachine", "User cancelled");
			break;

		case -1:
			onPhotoTaken();

			if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

				capture_1.setBackgroundResource(R.drawable.camera_done);

			}
			/*if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

				capture_2.setBackgroundResource(R.drawable.active_camera);

			}
			if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

				capture_3.setBackgroundResource(R.drawable.active_camera);

			}*/

			break;
		}
	}

	protected void onPhotoTaken() {

		Log.i("MakeMachine", "onPhotoTaken");
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap bitmap = BitmapFactory.decodeFile(diskpath, options);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i("MakeMachine", "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(this.PHOTO_TAKEN)) {
			onPhotoTaken();
			if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

				capture_1.setBackgroundResource(R.drawable.camera);

			}
			/*if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

				capture_2.setBackgroundResource(R.drawable.active_camera);

			}
			if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

				capture_3.setBackgroundResource(R.drawable.active_camera);

			}*/

		}

		if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

			capture_1.setBackgroundResource(R.drawable.camera);

		}
		/*if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

			capture_2.setBackgroundResource(R.drawable.active_camera);

		}
		if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

			capture_3.setBackgroundResource(R.drawable.active_camera);

		}*/

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(this.PHOTO_TAKEN, _taken);
	}






	public class ButtonClickHandler implements OnClickListener {
		LocationActivity loc = new LocationActivity();

		
		
		public void onClick(View view) {

			
			
			if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {

				if (view.getId() == R.id.StoreFront) {

					diskpath = CommonString1.FILE_PATH+ storeid + "front.jpg";
					_path = storeid + "front.jpg";


					Log.i("MakeMachine", "ButtonClickHandler.onClick()");

					abc = 03;

					startCameraActivity();

				}
				/*if (view.getId() == R.id.captureimageinside) {
					Log.i("MakeMachine", "ButtonClickHandler.onClick()");
					diskpath = Environment.getExternalStorageDirectory()
							+ "/VisitingCrads/" + storeid + "inside.jpg";
					//_path1 = storeid + "inside.jpg";

					abd = 01;

					startCameraActivity();

				}*/
				/*if (view.getId() == R.id.captureimageinside2) {

					Log.i("MakeMachine", "ButtonClickHandler.onClick()");
					diskpath = Environment.getExternalStorageDirectory()
							+ "/VisitingCrads/" + storeid + "inside1.jpg";
					_path2 = storeid + "inside1.jpg";

					abe = 02;

					startCameraActivity();

				}*/

			} 
			else if (data.getLatitude() == 0 && data.getLongitude() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());

				builder.setMessage("Wait For Geo Location")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();

			}

			else {
				if (view.getId() == R.id.StoreFront) {

					diskpath = CommonString1.FILE_PATH+ storeid + "front.jpg";
					_path = storeid + "front.jpg";

					Log.i("MakeMachine", "ButtonClickHandler.onClick()");

					abc = 03;

					startCameraActivity();

				}
				/*if (view.getId() == R.id.captureimageinside) {
					Log.i("MakeMachine", "ButtonClickHandler.onClick()");
					diskpath = Environment.getExternalStorageDirectory()
							+ "/VisitingCrads/" + storeid + "inside.jpg";
					_path1 = storeid + "inside.jpg";

					abd = 01;

					startCameraActivity();

				}
				if (view.getId() == R.id.captureimageinside2) {

					Log.i("MakeMachine", "ButtonClickHandler.onClick()");
					diskpath = Environment.getExternalStorageDirectory()
							+ "/VisitingCrads/" + storeid + "inside1.jpg";
					_path2 = storeid + "inside1.jpg";

					abe = 02;

					startCameraActivity();

				}*/

			}

		}
	}

	public void SavePartialData(Double lat, Double longitude) {

		String status = "P";
		GSKDatabase data = new GSKDatabase(getApplicationContext());
		data.open();
		//data.updateGeoTagStatus(storeid, status, lat, longitude);
		//data.InsertStoregeotagging(storeid, lat, longitude, _pathforcheck);
		data.close();
		/*Intent intent = new Intent(LocationActivity.this, MainMenuActivity.class);

		startActivity(intent);*/
		/*Intent intent1 = new Intent(LocationActivity.this,
				UploadGeotaggingActivity.class);
		startActivity(intent1);*/
		LocationActivity.this.finish();

	}

	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		// TODO Auto-generated method stub
		
        mMap = googleMap;
       
        
        
       mMap.setMyLocationEnabled(true);
       mMap.getUiSettings().setCompassEnabled(true);
       mMap.getUiSettings().setZoomControlsEnabled(true);
       mMap.getMaxZoomLevel();
       mMap.getMinZoomLevel();
       mMap.getUiSettings();
       mMap.animateCamera(CameraUpdateFactory.zoomIn());
       mMap.animateCamera(CameraUpdateFactory.zoomOut());
       mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
       
     
       
      // buildGoogleApiClient();

    // mGoogleApiClient.connect(); 
     
		
       
       
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	public void onConnected(Bundle bundle) {
      
        
        
         mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
         
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
        	 latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); 
            
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


		try {
			// This gets a list of addresses
			
			/* * List<Address> addresses =
			 * geocoder.getFromLocation(location.getLatitude(),
			 * location.getLongitude(), 10); for (Address address : addresses) {
			 * // this.locationText.append("\n" + address.getAddressLine(0)); }
*/			 

			// Convert latitude and longitude into int that the GeoPoint
			// constructor can understand
			int latiti;
			int longi;
			if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
				latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
				longi = (int) (Double.parseDouble(storelongitude) * 1000000);

			}
			
			else {
				data.setLatitude((mLastLocation.getLatitude()));
				data.setLongitude((mLastLocation.getLongitude()));

				latiti = (int) (mLastLocation.getLatitude() * 1000000);
				longi = (int) (mLastLocation.getLongitude() * 1000000);

				
				/* latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
			        MarkerOptions markerOptions = new MarkerOptions();
			        markerOptions.position(latLng);
			        markerOptions.title("Current Position");
			        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
			        currLocationMarker = mMap.addMarker(markerOptions);	*/
				
			      /*  if (mRequestingLocationUpdates) {
			            startLocationUpdates();
			        }*/
				
				
				
			}

		//	point = new GeoPoint(latiti, longi);
		//	mapController.animateTo(point);
		//	MapOverlay mapOverlay = new MapOverlay();
		//	List<Overlay> listOfOverlays = map.getOverlays();
		//	listOfOverlays.clear();
		//	listOfOverlays.add(mapOverlay);

		}
		catch (Exception e) {
			Log.e("LocateMe", "Could not get Geocoder data", e);
		}

    }
	
	

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 protected synchronized void buildGoogleApiClient() {
	        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
	    }	 
	 
	
	
	 @Override
	    protected void onPause() {
	        super.onPause();
	        stopLocationUpdates();
	    }
	 
	 protected void stopLocationUpdates() {
		 LocationServices.FusedLocationApi.removeLocationUpdates(
				 mGoogleApiClient, this);
	    }
	 
	 protected void startLocationUpdates() {

	        LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);

	    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		 mLastLocation = location;
		
	}
	
	private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

	
	 protected void createLocationRequest() {
	        mLocationRequest = new LocationRequest();
	        mLocationRequest.setInterval(UPDATE_INTERVAL);
	        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
	    }
	 
	 protected void onStart() {

		 if (mGoogleApiClient != null) {
			 mGoogleApiClient.connect();
	        }
	        super.onStart();
	        
	    }
	
	 protected void onStop() {
	        
	        if (mGoogleApiClient.isConnected()) {
				mGoogleApiClient.disconnect();
	        }
	        super.onStop();
	    } 
	 
	 
	 
	 
	 public void onBackPressed() {
			// TODO Auto-generated method stub

			LocationActivity.this.finish();
		}



}