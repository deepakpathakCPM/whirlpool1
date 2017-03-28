package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.MappingPromotionGetterSetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyEntryScreen extends AppCompatActivity implements OnItemClickListener,LocationListener{

	Button pjpdeviation,callcycle;
	GSKDatabase database;
	ArrayList<JourneyPlanGetterSetter> jcplist;
	ArrayList<CoverageBean> coverage;
	private SharedPreferences preferences;
	private String date,store_intime,store_id;
	ListView lv;
	//String store_cd;
	 String store_cd;
	private SharedPreferences.Editor editor = null;

	private Dialog dialog;

	String storeVisited=null;

	public static String currLatitude = "0.0";
	public static String currLongitude = "0.0";
	boolean coverageflag= true;
	String user_type;
	boolean result_flag=false,leaveflag=false;
	CardView cardView;
	LinearLayout parent_linear,nodata_linear;

	MappingPromotionGetterSetter lgs = null;
	List<MappingPromotionGetterSetter> datalist=new ArrayList<MappingPromotionGetterSetter>();
	String window_cd,window;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storelistlayout);
		lv=(ListView) findViewById(R.id.list);
		nodata_linear = (LinearLayout) findViewById(R.id.no_data_lay);
		parent_linear = (LinearLayout) findViewById(R.id.parent_linear);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		database = new GSKDatabase(this);
		database.open();


		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		date = preferences.getString(CommonString1.KEY_DATE, null);
		store_intime = preferences.getString(CommonString1.KEY_STORE_IN_TIME, "");

		store_id = preferences.getString(CommonString1.KEY_STORE_CD, "");
		editor = preferences.edit();


		user_type = preferences.getString(CommonString1.KEY_USER_TYPE, null);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		jcplist=database.getJCPData(date);
		coverage=database.getCoverageData(date);

		if(jcplist.size()>0){

			setCheckOutData();

			lv.setAdapter(new MyAdapter());
			lv.setOnItemClickListener(this);
		}
		else{
			lv.setVisibility(View.GONE);
			parent_linear.setBackgroundColor((getResources().getColor(R.color.grey_light)));
			nodata_linear.setVisibility(View.VISIBLE);
		}

	}

	public void setCheckOutData(){

		for(int i=0;i<jcplist.size();i++){
			String storeCd=jcplist.get(i).getStore_cd().get(0);
			//String FIRST_VISIT=jcplist.get(i).getFIRST_VISIT().get(0);
			String state_cd=jcplist.get(i).getSTATE_CD().get(0);

			if(!jcplist.get(i).getCheckOutStatus().get(0)
					.equals(CommonString1.KEY_C) && !jcplist.get(i).getCheckOutStatus().get(0)
					.equals(CommonString1.KEY_VALID)){
				
				if( database.isOpeningDataFilled(storeCd) && database.getwindowsData(storeCd).size()>0 ){

					boolean flag=true;
////////?????????
					//getCompetitionPromotionData

					/*if(FIRST_VISIT.equalsIgnoreCase(CommonString1.KEY_FIRST_1)){

					if(database.getSFTData(storeCd).size()>0 ){

							flag=true;

					}
					else{
						flag=false;
					}
					}*/

					/*if(flag){

						if(user_type.equals("Promoter")){

							if(database.isClosingDataFilled(storeCd)  && database.isMiddayDataFilled(storeCd)){

								flag=true;

							}
							else{
								flag=false;
							}

						}

					}*/


					/*if(flag){

						if(database.getAssetCategoryData(storeCd).size()>0 ) {

							if (database.isAssetDataFilled(storeCd)) {

								flag=true;
							}
							else {

								flag=false;
							}
						}

					}*/

					if(flag){
						database.updateStoreStatusOnCheckout(storeCd, date, CommonString1.KEY_VALID);
						jcplist=database.getJCPData(date);
					}

				}

				/*{
					*//*boolean flag=false;
					boolean notAll=false;
					
					boolean closingnmidday_flag=false;
					boolean promotion_flag=false;
					boolean assest_flag=false;
					boolean food_flag=false;
					
					
					if(!user_type.equals("Merchandiser")){
						
						closingnmidday_flag=true;
						
						if(database.isClosingDataFilled(storeCd)  && database.isMiddayDataFilled(storeCd) ){
							flag=true;
						}
						else{
							flag=false;
						}
							
					}
					
					if(database.getCallsData(storeCd).size()>0){
						flag=true;
					}
					else{
						flag=false;
					}
					

					if(database.getPromotionBrandData(storeCd).size()>0 ){

						notAll=false;
						
						promotion_flag=true;
						
						if(database.isPromotionDataFilled(storeCd)){
							
							if(!closingnmidday_flag){
								flag=true;
							}
							
						}
						else{
							flag=false;
						}

					}
					else{
						notAll=true;
					}

					if(database.getAssetBrandData(storeCd).size()>0 ){
						notAll=false;
						
						if(database.isAssetDataFilled(storeCd)){
							if(!promotion_flag){
								flag=true;
							}
						}
						else{
							flag=false;
						}
					}
					else{
						notAll=true;
					}

					if(jcplist.get(i).getCategory_type().get(0).equals("Food")){

						notAll=false;
						
						if(database.isFoodDataFilled(storeCd)){
							flag=true;
						}
						else{
							flag=false;
						}
					}
					else{
						notAll=true;
					}
					
					if(notAll || flag){
						 database.updateStoreStatusOnCheckout(storeCd, date, CommonString1.KEY_VALID);
						 jcplist=database.getJCPData(date);
					}
*//*
					
					boolean flag=true;
					
					if(!user_type.equals("Merchandiser")){
						if(database.isClosingDataFilled(storeCd)  && database.isMiddayDataFilled(storeCd) ){
							flag=true;
						}
						else{
							flag=false;
						}
					}
					
					if(flag)
					if(database.getPromotionBrandData(storeCd).size()>0){
						if(database.isPromotionDataFilled(storeCd)){
							flag=true;
						}
						else{
							flag=false;
						}
					}
					
					if(flag)
						if(database.getAssetBrandData(storeCd).size()>0 ){
							if(database.isAssetDataFilled(storeCd)){
								flag=true;
							}
							else{
								flag=false;
							}
						}
					
					if(flag)
						if(jcplist.get(i).getCategory_type().get(0).equals("Food")){
							if(database.isFoodDataFilled(storeCd)){
								flag=true;
							}
							else{
								flag=false;
							}
						}
					
					if(flag)
						if(!user_type.equals("Merchandiser")){
							if(database.isCallsDataFilled(storeCd)){
								flag=true;
							}
							else{
								flag=false;
							}
						}
					
					
					if(flag){
						*//*if(ismncavl && isasstavl && ispromotavl && isfoodavl){
							
						}*//*
						
						 database.updateStoreStatusOnCheckout(storeCd, date, CommonString1.KEY_VALID);
						 jcplist=database.getJCPData(date);
						
						//Toast.makeText(getApplicationContext(), "Checked",  Toast.LENGTH_SHORT).show();
					}
					else{
						//Toast.makeText(getApplicationContext(), "Not Checked",  Toast.LENGTH_SHORT).show();
					}

				}*/
				
				
			}

		}

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jcplist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.storelistrow, null);

				holder.storename = (TextView) convertView
						.findViewById(R.id.tvstorename);
				holder.city = (TextView) convertView
						.findViewById(R.id.tvcity);
				holder.keyaccount = (TextView) convertView
						.findViewById(R.id.tvkeyaccount);
				holder.img = (ImageView) convertView
						.findViewById(R.id.img);

				holder.checkout = (Button) convertView
						.findViewById(R.id.chkout);

				holder.checkinclose = (ImageView) convertView
						.findViewById(R.id.closechkin);


				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.checkout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					AlertDialog.Builder builder = new AlertDialog.Builder(
							DailyEntryScreen.this);
					builder.setMessage("Are you sure you want to Checkout")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog, int id) {
							if (CheckNetAvailability()) {

								editor = preferences.edit();

								editor.putString(CommonString1.KEY_STORE_CD, jcplist.get(position).getStore_cd().get(0));

								editor.putString(
										CommonString1.KEY_STORE_NAME,
										jcplist.get(position)
										.getStore_name().get(0));

								editor.commit();

								Intent i = new Intent(
										DailyEntryScreen.this,
										CheckOutStoreActivity.class);
								startActivity(i);
							} else {

								Snackbar.make(lv, "No Network", Snackbar.LENGTH_SHORT)
										.setAction("Action", null).show();

								//Toast.makeText(DailyEntryScreen.this, "No Network", Toast.LENGTH_SHORT).show();

							}

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

				}
			});

			String storecd=jcplist.get(position).getStore_cd().get(0);
			ArrayList<CoverageBean> coverageBean =database.getCoverageSpecificData(storecd);
			
			if (jcplist.get(position).getUploadStatus().get(0).equals(CommonString1.KEY_D))
			{
				holder.img.setVisibility(View.VISIBLE);
				holder.img.setBackgroundResource(R.drawable.tick_d);
				holder.checkout.setVisibility(View.INVISIBLE);
				holder.checkinclose.setVisibility(View.INVISIBLE);
			}
			else if(preferences.getString(CommonString1.KEY_STOREVISITED_STATUS+storecd, "").equals("No")){
				holder.img.setBackgroundResource(R.drawable.leave_tick);
				holder.checkout.setVisibility(View.INVISIBLE);
				holder.checkinclose.setVisibility(View.INVISIBLE);

			}
			else if ((jcplist.get(position).getCheckOutStatus().get(0)
					.equals(CommonString1.KEY_C))) {

				holder.img.setVisibility(View.INVISIBLE);
				holder.checkinclose.setBackgroundResource(R.drawable.tick_c);
				holder.checkinclose.setVisibility(View.VISIBLE);
				holder.checkout.setVisibility(View.INVISIBLE);

			}
			else if (coverageBean.size()>0 && (coverageBean.get(position).getStatus().equalsIgnoreCase(CommonString1.KEY_VALID))) {

				holder.checkout.setBackgroundResource(R.drawable.checkout);
				holder.checkout.setVisibility(View.VISIBLE);
				holder.checkout.setEnabled(true);

			}



			/*else if ((jcplist.get(position).getCheckOutStatus().get(0)
					.equals(CommonString1.KEY_INVALID))) {


					holder.checkout.setVisibility(View.INVISIBLE);
					holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
					//	holder.checkinclose.setEnabled(false);
					//holder.checkinclose.setBackgroundResource(R.drawable.close);
					//.checkinclose.setVisibility(View.VISIBLE);
					holder.checkinclose.setVisibility(View.VISIBLE);

			}*/
			else if (coverageBean.size()>0 && (coverage.get(position).getCoveargestatus().equalsIgnoreCase(CommonString1.KEY_INVALID))) {

				if (coverage.size() > 0) {

					int i;

					for (i = 0; i < coverage.size(); i++) {


						if (coverage.get(i).getInTime() != null) {

							if (coverage.get(i).getOutTime() == null) {


								if (storecd.equals(coverage.get(i).getStoreId())) {

								/*Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT)
										.setAction("Action", null).show();
								//enteryflag =false;*/

									holder.img.setVisibility(View.INVISIBLE);
									holder.checkout.setEnabled(false);
									holder.checkout.setVisibility(View.INVISIBLE);
									holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
									//	holder.checkinclose.setEnabled(false);
									//holder.checkinclose.setBackgroundResource(R.drawable.close);
									//.checkinclose.setVisibility(View.VISIBLE);
									holder.checkinclose.setVisibility(View.VISIBLE);


								}
								break;
							}


						}


					}


				}
			}

			else {
				holder.checkout.setEnabled(false);
				holder.checkout.setVisibility(View.INVISIBLE);
				//holder.img.setVisibility(View.INVISIBLE);


				holder.checkinclose.setEnabled(false);
				holder.checkinclose.setVisibility(View.INVISIBLE);

			}



			holder.storename.setText(jcplist.get(position).getStore_name().get(0));
			holder.city.setText(jcplist.get(position).getCity().get(0));
			holder.keyaccount.setText(jcplist.get(position).getKey_account().get(0));
		

			/*if (list.get(position).getStatus().equalsIgnoreCase(CommonString1.STORE_STATUS_LEAVE)) {
				holder.imgtick.setVisibility(View.VISIBLE);
				holder.imgtick.setBackgroundResource(R.drawable.leave_tick);
			}*/

			return convertView;
		}

		private class ViewHolder {
			TextView storename, city,keyaccount;
			ImageView img,checkinclose;

			Button checkout;
		}


	}

	@Override
	public void onBackPressed() {
		/*Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		startActivity(i);*/

		finish();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub

		store_cd=jcplist.get(position).getStore_cd().get(0);
		final String upload_status=jcplist.get(position).getUploadStatus().get(0);
		final String checkoutstatus=jcplist.get(position).getCheckOutStatus().get(0);
		final String STORETYPE_CD=jcplist.get(position).getSTORETYPE_CD().get(0);
		final String STATE_CD=jcplist.get(position).getSTATE_CD().get(0);
		final String GeoTag=jcplist.get(position).getGEO_TAG().get(0);

		//final String FIRST_VISIT=jcplist.get(position).getFIRST_VISIT().get(0);


		editor = preferences.edit();

		//editor.putString(CommonString1.KEY_FIRST_VISIT, FIRST_VISIT);

		editor.putString(CommonString1.KEY_GEO_TAG, GeoTag);
		editor.putString(CommonString1.KEY_STORE_TYPE_CD, STORETYPE_CD);
		editor.putString(CommonString1.KEY_STATE_CD, STATE_CD);
		editor.commit();







		if(upload_status.equals(CommonString1.KEY_D)){

			Snackbar.make(lv, "All Data Uploaded", Snackbar.LENGTH_SHORT)
					.setAction("Action", null).show();


			//Toast.makeText(getApplicationContext(), "All Data Uploaded", Toast.LENGTH_SHORT).show();
		}
		else if (((checkoutstatus.equals(CommonString1.KEY_C)))) {

			Snackbar.make(lv, "Store already checked out", Snackbar.LENGTH_SHORT)
					.setAction("Action", null).show();

			//Toast.makeText(getApplicationContext(), "Store Checkout", Toast.LENGTH_SHORT).show();
		}else if(preferences.getString(CommonString1.KEY_STOREVISITED_STATUS+store_cd, "").equals("No")){

			Snackbar.make(lv, "Store Already Closed", Snackbar.LENGTH_SHORT)
					.setAction("Action", null).show();

			//Toast.makeText(getApplicationContext(), "Store Already Closed", Toast.LENGTH_SHORT).show();
		}
		else{
			/*if(jcplist.get(position).getCategory_type().get(0).equals("Food")){
				editor.putBoolean(CommonString1.KEY_FOOD_STORE, true);
			}
			else{
				editor.putBoolean(CommonString1.KEY_FOOD_STORE, false);
			}

			editor.commit();*/

			/*if(preferences.getString(CommonString1.KEY_STOREVISITED_STATUS, "").equals("Yes")){


				if(!preferences.getString(CommonString1.KEY_STOREVISITED, "").equals(store_cd)){

					Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT)
							.setAction("Action", null).show();

					//Toast.makeText(getApplicationContext(), "Please checkout from current store", Toast.LENGTH_SHORT).show();
				}
				else{

				*//*	editor.putString(CommonString1.KEY_STORE_CD, store_cd+"");
					editor.putString(CommonString1.KEY_STORE_NAME, jcplist.get(position).getStore_name().get(0));
					editor.commit();*//*

					showMyDialog(store_cd,jcplist.get(position).getStore_name().get(0),"No","",jcplist.get(position).getCheckOutStatus().get(0));

				}

			}*/
			//else {

				// PUT IN PREFERENCES
				/*editor = preferences.edit();
				editor.putString(CommonString1.KEY_STORE_CD, store_cd);
				editor.putString(CommonString1.KEY_STORE_NAME, jcplist.get(position).getStore_name().get(0));
				editor.putString(CommonString1.KEY_VISIT_DATE, jcplist.get(position).getVISIT_DATE().get(0));
				editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");

				editor.commit();*/



			if(!setcheckedmenthod(store_cd)){

				boolean enteryflag = true;

				if(coverage.size()>0) {

					int i;

					for (i = 0; i < coverage.size(); i++) {


						if (coverage.get(i).getInTime() != null) {

							if (coverage.get(i).getOutTime() == null) {
								if (!store_cd.equals(coverage.get(i).getStoreId())) {

									Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT)
											.setAction("Action", null).show();
									enteryflag =false;
								}
								break;
							}



						}
			/*else
			{

			}*/

					}


				}

				if(enteryflag){
					showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));


				}

			}
			else {

				Snackbar.make(lv, "Data already filled ", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			}













			/*if(!setcheckedmenthod(store_cd)) {

				if (coverage.size() > 0) {
					for (int i = 0; i < coverage.size(); i++) {
						if (coverage.get(0).getStoreId().equals(store_cd)) {
							coverageflag = false;
							break;
						}

					}

				}

				 else
				{
					showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));
				}


			}

			 else {

				 Snackbar.make(lv, "Data already filled ", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
			 }
*/


				/*if(!coverageflag)
				{

					showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));


				}

				else
				{

					Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT)
							.setAction("Action", null).show();



				}*/


			
			}

		//}



		//Toast.makeText(getApplicationContext(), store_name, Toast.LENGTH_SHORT).show();
		/*	Intent in=new Intent(getApplicationContext(),StoreEntry.class);

		startActivity(in);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);*/


	}




	public String getCurrentTimeX() {

		Calendar m_cal = Calendar.getInstance();
		int hour = m_cal.get(Calendar.HOUR_OF_DAY);
		int min = m_cal.get(Calendar.MINUTE);

		String intime = "";

		if (hour == 0) {
			intime = "" + 12 + ":" + min + " AM";
		} else if (hour == 12) {
			intime = "" + 12 + ":" + min + " PM";
		} else {

			if (hour > 12) {
				hour = hour - 12;
				intime = "" + hour + ":" + min + " PM";
			} else {
				intime = "" + hour + ":" + min + " AM";
			}
		}
		return intime;
	}

	public String getCurrentTime() {

		Calendar m_cal = Calendar.getInstance();

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String cdate = formatter.format(m_cal.getTime());

       /* String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);*/

		return cdate;

	}

	public boolean CheckNetAvailability() {

		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED
				|| connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			// we are connected to a network
			connected = true;
		}
		return connected;
	}

	void showMyDialog(final String storeCd, final String storeName, final String status, final String visitDate, final String checkout_status){
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogbox);
		// dialog.setTitle("About Android Dialog Box");


		RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// find which radio button is selected
				if(checkedId == R.id.yes) {
					/*Toast.makeText(getApplicationContext(), "choice: Yes", 
								Toast.LENGTH_SHORT).show();*/
					editor = preferences.edit();

					editor.putString(CommonString1.KEY_STOREVISITED, storeCd);
					editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");
					editor.putString(CommonString1.KEY_LATITUDE, currLatitude);
					editor.putString(CommonString1.KEY_LONGITUDE, currLongitude);
					
					editor.putString(CommonString1.KEY_STORE_NAME, storeName);
					editor.putString(CommonString1.KEY_STORE_CD, storeCd);


					
					if(!visitDate.equals("")){
						editor.putString(CommonString1.KEY_VISIT_DATE, visitDate);
					}
				
					if(status.equals("Yes")){
						editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");
					}
					
					database.updateStoreStatusOnCheckout(storeCd, date, CommonString1.KEY_INVALID);
					
					editor.commit();
					
					if (store_intime.equalsIgnoreCase("")) {

						SharedPreferences.Editor editor = preferences.edit();
						editor.putString(CommonString1.KEY_STORE_IN_TIME,
								getCurrentTime());
						//editor.putString(CommonString1.KEY_STOREVISITED, store_id);
						editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");

						editor.commit();

					}

					dialog.cancel();

					/*if(coverage.size()>0)
					{
						for(int i=0;i<coverage.size();i++)
						{
							if(coverage.get(0).getStoreId().equals(store_cd_JCP))
							{
								coverageflag= false;
								break;
							}
						}

					}*/

					/*if(coverageflag)
					{
						Intent in  = new Intent(DailyEntryScreen.this, StoreimageActivity.class);
						startActivity(in);

						overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
					}
						else
					{
						Intent in=new Intent(DailyEntryScreen.this,StoreEntry.class);
						startActivity(in);
					}
*/

					boolean flag=true;

					if(coverage.size()>0) {


						for (int i = 0; i < coverage.size(); i++) {


							if (store_cd.equals(coverage.get(i).getStoreId())) {

								flag=false;
								break;
							}


						}



					}



					if(flag==true)
					{
						Intent in  = new Intent(DailyEntryScreen.this, StoreimageActivity.class);
						startActivity(in);

						overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
					}
					else
					{

						Intent in=new Intent(DailyEntryScreen.this,StoreEntry.class);
						startActivity(in);

					}






					//finish();

				} else if(checkedId == R.id.no) {
					/*Toast.makeText(getApplicationContext(), "choice: No", 
								Toast.LENGTH_SHORT).show();*/

				
					dialog.cancel();
					
					if(checkout_status.equals(CommonString1.KEY_INVALID) || checkout_status.equals(CommonString1.KEY_VALID)){
						AlertDialog.Builder builder = new AlertDialog.Builder(DailyEntryScreen.this);
						builder.setMessage(CommonString1.DATA_DELETE_ALERT_MESSAGE)
						.setCancelable(false) 
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								
								UpdateData(storeCd);
								
								SharedPreferences.Editor editor = preferences
										.edit();
								editor.putString(CommonString1.KEY_STORE_CD, storeCd);
								editor.putString(
										CommonString1.KEY_STORE_IN_TIME,
										"");
								editor.putString(
										CommonString1.KEY_STOREVISITED,
										"");
								editor.putString(
										CommonString1.KEY_STOREVISITED_STATUS,
										"");
								editor.putString(
										CommonString1.KEY_LATITUDE, "");
								editor.putString(
										CommonString1.KEY_LONGITUDE, "");

								editor.commit();


								Intent in  = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
								startActivity(in);
								
							}
						})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
							
								
								dialog.cancel();
							}
						});
						AlertDialog alert = builder.create();

						alert.show();
					}
					else{
						UpdateData(storeCd);

						SharedPreferences.Editor editor = preferences
								.edit();
						editor.putString(CommonString1.KEY_STORE_CD, storeCd);
						editor.putString(
								CommonString1.KEY_STORE_IN_TIME,
								"");
						editor.putString(
								CommonString1.KEY_STOREVISITED,
								"");
						editor.putString(
								CommonString1.KEY_STOREVISITED_STATUS,
								"");
						editor.putString(
								CommonString1.KEY_LATITUDE, "");
						editor.putString(
								CommonString1.KEY_LONGITUDE, "");

						editor.commit();
						
						Intent in  = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
						startActivity(in);
					}
					
					

					

					//finish();
				} 
			}

		});

		/*RadioButton yes = (RadioButton)  dialog.findViewById(R.id.yes);
		RadioButton no = (RadioButton)  dialog.findViewById(R.id.no);*/

		dialog.show();
	}



	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		currLatitude = Double.toString(location.getLatitude());
		currLongitude = Double.toString(location.getLongitude());
	}



	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	public void UpdateData(String storeCd) {

		database.open();
		database.deleteSpecificStoreData(storeCd);


		/*database.updateStoreStatusOnLeave(store_cd, visit_date,
				CommonString1.KEY_N);*/

		database.updateStoreStatusOnCheckout(storeCd, jcplist.get(0).getVISIT_DATE().get(0),
				"N");

		/*Intent in  = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
		startActivity(in);*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.empty_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if(id==android.R.id.home){

			// NavUtils.navigateUpFromSameTask(this);
			finish();

			overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

		}

		return super.onOptionsItemSelected(item);
	}






	public boolean setcheckedmenthod(String store_cd)
	{


		for(int i=0;i<coverage.size();i++) {




			if (store_cd.equals(coverage.get(i).getStoreId())) {

				if(coverage.get(i).getOutTime()!=null)
				{
					result_flag=true;

					break;
				}


				//	Snackbar.make(lv, " Data Filled", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
				//

			}

			else
			{
				//	showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));

				result_flag=false;

				//	break;
			}


		}


		return result_flag;
	}









}
