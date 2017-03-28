package com.cpm.dailyentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;
import com.cpm.GetterSetter.NavMenuItemGetterSetter;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.geotag.LocationActivity;
import com.cpm.xmlGetterSetter.MiddayStockInsertData;
import com.cpm.xmlGetterSetter.OpeningStockInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.POSM_MASTERGetterSetter;
import com.cpm.xmlGetterSetter.StockGetterSetter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StoreEntry extends AppCompatActivity implements OnClickListener{

	Button btnDeepfreez,btnOpeningStock,btnClosingStock,btnMiddayStock,btnPromotion,btnAsset,btnfoodstr,btnfacingcomp,btncalls;
   Button performance;
	GSKDatabase db;
	private SharedPreferences preferences;
	String store_cd,state_cd;
	
	boolean food_flag,user_flag=false;
	
	String user_type="",GEO_TAG=""/*,FIRST_VISIT=""*/;

	private ArrayList<StockGetterSetter> stockData = new ArrayList<StockGetterSetter>();
	
	HashMap<OpeningStockInsertDataGetterSetter, List<MiddayStockInsertData>> listDataChild;
	
	List<OpeningStockInsertDataGetterSetter> listDataHeader;
	ArrayList<POSM_MASTERGetterSetter> posmData = new ArrayList<POSM_MASTERGetterSetter>();
	
	LinearLayout layout_mid_close,gap_layout,gap_calls,food_layout;

	ValueAdapter adapter;

	RecyclerView recyclerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		setContentView(R.layout.menu_item_recycle_layout);


		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		db=new GSKDatabase(getApplicationContext());
		db.open();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);

		//state_cd = preferences.getString(CommonString1.KEY_STATE_CD, null);

		food_flag=preferences.getBoolean(CommonString1.KEY_FOOD_STORE, false);

		GEO_TAG=preferences.getString(CommonString1.KEY_GEO_TAG, null);
		//FIRST_VISIT=preferences.getString(CommonString1.KEY_FIRST_VISIT, null);



		user_type = preferences.getString(CommonString1.KEY_USER_TYPE, null);

		if(user_type!=null){/*
			if(user_type.equals("Merchandiser")){
				layout_mid_close.setVisibility(View.GONE);
				gap_layout.setVisibility(View.GONE);
				
				//layout_calls.setVisibility(View.GONE);
				btnMiddayStock.setVisibility(View.GONE);
				//gap_calls.setVisibility(View.GONE);
				
				performance.setVisibility(View.GONE);
				
				user_flag=true;
			}
		*/}
		
		/*if(food_flag){
			food_layout.setVisibility(View.VISIBLE);
			gap_calls.setVisibility(View.VISIBLE);
		}
		else{
			gap_calls.setVisibility(View.GONE);
		}*/


	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		recyclerView=(RecyclerView) findViewById(R.id.drawer_layout_recycle);

		adapter=new ValueAdapter(getApplicationContext(),getdata());
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
		
		validate2();
		
	}
	
	public void validate(){
		
		db.open();
		
		if(db.isClosingDataFilled(store_cd)){
			btnClosingStock.setBackgroundResource(R.drawable.closing_stock_done);
		}
		else{
			btnClosingStock.setBackgroundResource(R.drawable.closing_stock);
		}
		
		if(db.isCompetitionDataFilled(store_cd)){
			btnfacingcomp.setBackgroundResource(R.drawable.competition_done);
		}

		
		if(db.isMiddayDataFilled(store_cd)){
			btnMiddayStock.setBackgroundResource(R.drawable.mid_stock_done);
		}
		else{
			btnMiddayStock.setBackgroundResource(R.drawable.mid_stock);
		}


		if(db.isOpeningDataFilled(store_cd)){
			btnOpeningStock.setBackgroundResource(R.drawable.opening_stock_done);
		}
		else{
			btnOpeningStock.setBackgroundResource(R.drawable.opening_stock);
		}
		
		if(db.isAssetDataFilled(store_cd)){
			btnAsset.setBackgroundResource(R.drawable.asset_done);
		}
		if(db.isPromotionDataFilled(store_cd)){
			btnPromotion.setBackgroundResource(R.drawable.promotion);
		}
	}



	public void validate2() {
		posmData = db.getPOSMData(store_cd);

		if (posmData.size() > 0) {
			db.updateCoverageStatus(Integer.parseInt(store_cd), CommonString1.KEY_VALID);
		}

	}


	@Override
	public void onBackPressed() {

		finish();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		int id=view.getId();

		switch (id) {

		case R.id.openingstock:
			
			if(!db.isClosingDataFilled(store_cd)){

					
					Intent in1=new Intent(getApplicationContext(),PosmActivity.class);

					startActivity(in1);

					overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			}
			else{
				Toast.makeText(getApplicationContext(), "Data cannot be changed", Toast.LENGTH_SHORT).show();
			}
			
			
			break;

		case R.id.closingstock:
			
			stockData=db.getOpeningStock(store_cd);
			if((stockData.size()<=0) || (stockData.get(0).getOpen_stock_cold_room()==null) || (stockData.get(0).getOpen_stock_cold_room().equals(""))){
			
				Toast.makeText(getApplicationContext(),
						"First Fill Opening Stock and Midday Stock Data", Toast.LENGTH_SHORT).show();
			
		}
		else{
			stockData=db.getMiddayStock(store_cd);
			
			if((stockData.size()<=0) || (stockData.get(0).getMidday_stock()==null) || (stockData.get(0).getMidday_stock().equals(""))){
				
				Toast.makeText(getApplicationContext(),
						"First Fill Opening Stock and Midday Stock Data", Toast.LENGTH_SHORT).show();
				
				}
			else{
	
				Intent in2=new Intent(getApplicationContext(),ClosingStock.class);

				startActivity(in2);

				overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			}
		}

			
			break;


		case R.id.midstock:

			if(!db.isClosingDataFilled(store_cd)){
				
				Intent in3=new Intent(getApplicationContext(),MidDayStock.class);

				startActivity(in3);

				overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			}
			else{
				Toast.makeText(getApplicationContext(), "Data cannot be changed", Toast.LENGTH_SHORT).show();
			}
			
			

			break;
			
		case R.id.prommotion:

			Intent in4=new Intent(getApplicationContext(),WindowsActivity.class);

			startActivity(in4);

			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;
			
		case R.id.assets:

			Intent in5=new Intent(getApplicationContext(),AssetActivity.class);

			startActivity(in5);

			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;
			
		case R.id.foodstore:

			Intent in6=new Intent(getApplicationContext(),FoodStore.class);

			startActivity(in6);

			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;
			
		case R.id.facingcompetitor:

			Intent in7=new Intent(getApplicationContext(),FacingCompetitor.class);

			startActivity(in7);

			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;
			
		case R.id.calls:

			Intent in8=new Intent(getApplicationContext(),CallsActivity.class);

			startActivity(in8);

			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;
			
			
		case R.id.performance:
			
			Intent startPerformance = 	new Intent(StoreEntry.this,Performance.class);
			startActivity(startPerformance);
			 overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		}

	}

/*

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.save_database:

			AlertDialog.Builder builder1 = new AlertDialog.Builder(
					StoreEntry.this);
			builder1.setMessage(
					"Are you sure you want to take the backup of your data")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						@SuppressWarnings("resource")
						public void onClick(DialogInterface dialog,
								int id) {
							try {

								File file = new File(Environment
										.getExternalStorageDirectory(),
										"capital_backup");
								if (!file.isDirectory()) {
									file.mkdir();
								}

								File sd = Environment
										.getExternalStorageDirectory();
								File data = Environment
										.getDataDirectory();

								if (sd.canWrite()) {
									long date = System.currentTimeMillis(); 

									SimpleDateFormat sdf = new SimpleDateFormat("MMM/MM/dd");
									String dateString = sdf.format(date);   
									
									String currentDBPath = "//data//com.cpm.capitalfoods//databases//Capital_DATABASE";
									String backupDBPath = "capital_backup"
											+ dateString.replace('/', '-');

									String path=Environment.getExternalStorageDirectory().getPath();

									File currentDB = new File(data,
											currentDBPath);
									File backupDB = new File(path,
											backupDBPath);

									if (currentDB.exists()) {
										@SuppressWarnings("resource")
										FileChannel src = new FileInputStream(
												currentDB).getChannel();
										FileChannel dst = new FileOutputStream(
												backupDB).getChannel();
										dst.transferFrom(src, 0,
												src.size());
										src.close();
										dst.close();
									}
								}
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}

						}
					})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert1 = builder1.create();
			alert1.show();


			break;

			case android.R.id.home:

				// NavUtils.navigateUpFromSameTask(this);
				finish();

				overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

			break;

		}
		return super.onOptionsItemSelected(item);
	}
*/

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

	public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder>{

		private LayoutInflater inflator;

		List<NavMenuItemGetterSetter> data= Collections.emptyList();

		public ValueAdapter(Context context, List<NavMenuItemGetterSetter> data){

			inflator = LayoutInflater.from(context);
			this.data=data;

		}

		@Override
		public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

			View view=inflator.inflate(R.layout.custom_row,parent,false);

			MyViewHolder holder=new MyViewHolder(view);

			return holder;
		}

		@Override
		public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {

			final NavMenuItemGetterSetter current=data.get(position);

			//viewHolder.txt.setText(current.txt);

			viewHolder.icon.setImageResource(current.getIconImg());
			viewHolder.label.setText(current.getIconName());
			viewHolder.icon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if(current.getIconImg()==R.drawable.posm || current.getIconImg()==R.drawable.posm_done){

						if(!db.isClosingDataFilled(store_cd)){

							//if(db.getDFTypeUploadData(store_cd).size()>0){

							Intent in1=new Intent(StoreEntry.this,PosmActivity.class);

							startActivity(in1);

							overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

						}
						else{

							Snackbar.make(recyclerView, "Data cannot be changed", Snackbar.LENGTH_SHORT).show();

						}


					}
					if(current.getIconImg()==R.drawable.midday_stock || current.getIconImg()==R.drawable.midday_stock_done){

						if(!db.isClosingDataFilled(store_cd)){

							Intent in3=new Intent(getApplicationContext(),MidDayStock.class);

							startActivity(in3);

							overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
						}
						else{

							Snackbar.make(recyclerView, "Data cannot be changed", Snackbar.LENGTH_SHORT).show();

						}


					}
					if(current.getIconImg()==R.drawable.windows || current.getIconImg()==R.drawable.window_done){
						Intent in4=new Intent(getApplicationContext(),WindowsActivity.class);

						startActivity(in4);

						overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

					}
					if(current.getIconImg()==R.drawable.asset || current.getIconImg()==R.drawable.asset_done){
						Intent in5=new Intent(getApplicationContext(),AssetActivity.class);

						startActivity(in5);

						overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
					}


					if(current.getIconImg()==R.drawable.gps || current.getIconImg()==R.drawable.gps_done){


						if(GEO_TAG.equalsIgnoreCase(CommonString1.KEY_GEO_Y))
						{
							Toast.makeText(getApplicationContext(),"GoeTag Already Done",Toast.LENGTH_LONG).show();
						}

						else if(db.getGeotaggingData(store_cd).size()>0){

							 if(db.getGeotaggingData(store_cd).get(0).getGEO_TAG().equalsIgnoreCase(CommonString1.KEY_GEO_Y))
							{
								Toast.makeText(getApplicationContext(),"GoeTag Already Done",Toast.LENGTH_LONG).show();
							}
						}



						else
						{
							Intent in4=new Intent(getApplicationContext(),LocationActivity.class);

							startActivity(in4);

							overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
						}


					}


					if(current.getIconImg()==R.drawable.closing_stock || current.getIconImg()==R.drawable.closing_stock_done){

						if(db.isOpeningDataFilled(store_cd)){

							if(db.isMiddayDataFilled(store_cd)){

								Intent in2=new Intent(getApplicationContext(),ClosingStock.class);

								startActivity(in2);

								overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

							}
							else{

								Snackbar.make(recyclerView,"First fill Midday Stock Data",Snackbar.LENGTH_SHORT).show();
							}

						}else{

							Snackbar.make(recyclerView,"First fill Opening Stock data",Snackbar.LENGTH_SHORT).show();

						}

					}
					if(current.getIconImg()==R.drawable.storeprofile || current.getIconImg()==R.drawable.storeprofile_done){



						/*if(FIRST_VISIT.equalsIgnoreCase(CommonString1.KEY_FIRST_0))
						{
							Toast.makeText(getApplicationContext(),"Already Done",Toast.LENGTH_LONG).show();
						}
						else
						{

							Intent in5=new Intent(getApplicationContext(),STOREFIRSTTIMEActivity.class);

							startActivity(in5);


							overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
						}*/

					}

					if(current.getIconImg()==R.drawable.competition || current.getIconImg()==R.drawable.competition_done){

						Intent in7=new Intent(getApplicationContext(),CompetionMenuActivity.class);

						startActivity(in7);

						overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
					}

				}
			});

		}

		@Override
		public int getItemCount() {
			return data.size();
		}


		class MyViewHolder extends RecyclerView.ViewHolder{

			//TextView txt;
			ImageView icon;
			TextView label;

			public MyViewHolder(View itemView) {
				super(itemView);
				//txt=(TextView) itemView.findViewById(R.id.list_txt);
				icon=(ImageView) itemView.findViewById(R.id.list_icon);
				label=(TextView) itemView.findViewById(R.id.list_txt);
			}
		}

	}

	public List<NavMenuItemGetterSetter> getdata(){
		List<NavMenuItemGetterSetter> data=new ArrayList<>();

		int Stock, middayImg, closingImg, windows, assetImg, additionalImg, competitionImg, geotag;

		if(db.isClosingDataFilled(store_cd)){
			closingImg = R.drawable.closing_stock_done;
		}
		else{
			closingImg = R.drawable.closing_stock;
		}

		if(db.isMiddayDataFilled(store_cd)){
			middayImg = R.drawable.midday_stock_done;
		}
		else{
			middayImg = R.drawable.midday_stock;
		}

		if(db.isPOSMDataFilled(store_cd)){
			Stock = R.drawable.posm_done;
		}
		else{
			Stock = R.drawable.posm;
		}

		if(db.isAssetDataFilled(store_cd)){
			assetImg = R.drawable.asset_done;
		}
		else{
			assetImg = R.drawable.asset;

		}

		if(db.isPromotionDataFilled(store_cd)){
			windows = R.drawable.window_done;
		}
		else{
			windows = R.drawable.windows;

		}

		if(db.getFacingCompetitorData(store_cd).size()>0 && db.getCompetitionPOIData(store_cd).size()>0 && db.getwindowsData(store_cd).size()>0){
			competitionImg = R.drawable.competition_done;
		}
		else{
			competitionImg = R.drawable.competition;

		}

		if(db.getSFTData(store_cd).size()>0){
			additionalImg = R.drawable.storeprofile_done;
		}
		else{

			/*if(FIRST_VISIT.equalsIgnoreCase(CommonString1.KEY_FIRST_0))
			{
				additionalImg = R.drawable.storeprofile_done;
			}
			else
			{
				additionalImg = R.drawable.storeprofile;
			}*/

		}


		if(db.getGeotaggingData(store_cd).size()>0){

			if(db.getGeotaggingData(store_cd).get(0).getGEO_TAG().equalsIgnoreCase(CommonString1.KEY_GEO_Y)){

				geotag = R.drawable.gps_done;
			}

			else{

				geotag = R.drawable.gps;
			}


		}

		else{

			if(GEO_TAG.equalsIgnoreCase(CommonString1.KEY_GEO_Y))
			{
				geotag = R.drawable.gps_done;
			}
			else
			{
				geotag = R.drawable.gps;
			}


		}



		if(user_type.equals("Promoter")){
			int img[]={Stock, middayImg, windows, assetImg, closingImg, /*additionalImg,*/ competitionImg};
			for(int i=0;i<img.length;i++){

				NavMenuItemGetterSetter recData=new NavMenuItemGetterSetter();
				recData.setIconImg(img[i]);
				//recData.setIconName(text[i]);

				data.add(recData);
			}
		}
		else if(user_type.equals("Merchandiser")){
			//int img[]={Stock, windows, assetImg, additionalImg, competitionImg};
			int img[]={Stock,/* windows,*/ geotag /*,additionalImg*/};
			String text[] ={"POSM","Geo Tag"};

			for(int i=0;i<img.length;i++){

				NavMenuItemGetterSetter recData=new NavMenuItemGetterSetter();
				recData.setIconImg(img[i]);
				recData.setIconName(text[i]);

				data.add(recData);
			}
		}

		return  data;
	}

}
