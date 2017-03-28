package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;

import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;

import com.cpm.xmlGetterSetter.AssetInsertdataGetterSetter;
import com.cpm.xmlGetterSetter.CallsGetterSetter;
import com.cpm.xmlGetterSetter.CompetitionPromotionGetterSetter;
import com.cpm.xmlGetterSetter.DeepFreezerTypeGetterSetter;
import com.cpm.xmlGetterSetter.FacingCompetitorGetterSetter;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.FoodStoreInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.MappingAssetGetterSetter;
import com.cpm.xmlGetterSetter.STOREFIRSTIMEGetterSetter;
import com.cpm.xmlGetterSetter.PromotionInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;
import com.cpm.xmlGetterSetter.windowsChildData;
import com.cpm.xmlHandler.FailureXMLHandler;

public class CheckoutNUpload extends Activity{

	ArrayList<JourneyPlanGetterSetter> jcplist;
	GSKDatabase database;
	private SharedPreferences preferences;
	private String username, visit_date, store_id, store_intime, store_out_time,date,prev_date,result;

	private Dialog dialog;
	private ProgressBar pb;
	private TextView percentage, message;
	private Data data;
	public static String currLatitude = "0.0";
	public static String currLongitude = "0.0";
	ArrayList<CoverageBean> coverageBean;
	
	private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
	private int factor, k;
	String app_ver;
	String datacheck = "";
	String[] words;
	String validity, storename;
	int mid;
	String sod = "";
	String total_sku = "";
	String sku = "";
	String sos_data = "";
	String category_data = "";
	String errormsg = "",status;
	boolean up_success_flag=true;
	static int counter = 1;
	
	String Path;
	boolean image_valid;
	
	private ArrayList<DeepFreezerTypeGetterSetter> deepfreezerData = new ArrayList<DeepFreezerTypeGetterSetter>();
	private ArrayList<FacingCompetitorGetterSetter> facingCompetitorData = new ArrayList<FacingCompetitorGetterSetter>();
	private ArrayList<STOREFIRSTIMEGetterSetter> STOREDETAIL = new ArrayList<STOREFIRSTIMEGetterSetter>();
	private ArrayList<FoodStoreInsertDataGetterSetter> foodStoredata = new ArrayList<FoodStoreInsertDataGetterSetter>();
	private ArrayList<CallsGetterSetter> callsData = new ArrayList<CallsGetterSetter>();
	private FailureGetterSetter failureGetterSetter = null;

	private ArrayList<AssetInsertdataGetterSetter> assetInsertdata = new ArrayList<AssetInsertdataGetterSetter>();
	private ArrayList<PromotionInsertDataGetterSetter> promotionData = new ArrayList<PromotionInsertDataGetterSetter>();
	//private ArrayList<FoodStoreInsertDataGetterSetter> foodStoredata = new ArrayList<FoodStoreInsertDataGetterSetter>();
	private ArrayList<StockNewGetterSetter> stockData = new ArrayList<StockNewGetterSetter>();
	private ArrayList<StockNewGetterSetter> stockImgData = new ArrayList<StockNewGetterSetter>();
	ArrayList<STOREFIRSTIMEGetterSetter> poiData=new ArrayList<STOREFIRSTIMEGetterSetter>();
	ArrayList<STOREFIRSTIMEGetterSetter> competitionpoiData=new ArrayList<STOREFIRSTIMEGetterSetter>();
	ArrayList<FacingCompetitorGetterSetter> facingcompetition=new ArrayList<FacingCompetitorGetterSetter>();
	ArrayList<CompetitionPromotionGetterSetter> promotioncompetitionData = new ArrayList<CompetitionPromotionGetterSetter>();
	private ArrayList<STOREFIRSTIMEGetterSetter> poiInsertdata = new ArrayList<STOREFIRSTIMEGetterSetter>();
	private ArrayList<windowsChildData> WindowsData = new ArrayList<windowsChildData>();

	private ArrayList<MappingAssetGetterSetter> WindowsDataChecklist = new ArrayList<MappingAssetGetterSetter>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.checkout_n_upload);

		database = new GSKDatabase(this);
		database.open();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		username = preferences.getString(CommonString1.KEY_USERNAME, "");
		app_ver = preferences.getString(CommonString1.KEY_VERSION, "");
		/*store_intime = preferences
				.getString(CommonString1.KEY_STORE_IN_TIME, "");*/
		visit_date = preferences.getString(CommonString1.KEY_DATE, null);
		currLatitude = preferences.getString(CommonString1.KEY_LATITUDE, "0.0");
		currLongitude = preferences
				.getString(CommonString1.KEY_LONGITUDE, "0.0");

		Path= CommonString1.FILE_PATH;
		
		if(!isCheckoutDataExist()){
			new UploadTask(this).execute();
		}

	}

	public boolean isCheckoutDataExist(){

		boolean flag=false;

		jcplist=database.getAllJCPData();

		for(int i=0;i<jcplist.size();i++){

			if(!jcplist.get(i).getVISIT_DATE().get(0).equals(visit_date)){
				
				prev_date=jcplist.get(i).getVISIT_DATE().get(0);
				
				if(jcplist.get(i).getCheckOutStatus().get(0).equals(CommonString1.KEY_VALID)){
					
					store_id = jcplist.get(i).getStore_cd().get(0);

					coverageBean = database.getCoverageSpecificData(store_id);
					
					if(coverageBean.size()>0){

						flag=true;
						
						username = coverageBean.get(0).getUserId();
						store_intime = coverageBean.get(0).getInTime();
						store_out_time = coverageBean.get(0).getOutTime();
						if(store_out_time ==null || store_out_time.equals("")){
							store_out_time = getCurrentTime();
						}
						date = coverageBean.get(0).getVisitDate();
						
						new BackgroundTask(this).execute();
					}

				}

			}
		}

		return flag;
	}


	private class BackgroundTask extends AsyncTask<Void, Data, String> {
		private Context context;

		BackgroundTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom);
			dialog.setTitle("Uploading Checkout Data");
			dialog.setCancelable(false);
			dialog.show();
			pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
			percentage = (TextView) dialog.findViewById(R.id.percentage);
			message = (TextView) dialog.findViewById(R.id.message);

		}

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {

				//String result = "";
				data = new Data();

				data.value = 20;
				data.name = "Checked out Data Uploading";
				publishProgress(data);


				String onXML = "[STORE_CHECK_OUT_STATUS][USER_ID]"
						+ username
						+ "[/USER_ID]" + "[STORE_ID]"
						+ store_id
						+ "[/STORE_ID][LATITUDE]"
						+ currLatitude
						+ "[/LATITUDE][LOGITUDE]"
						+ currLongitude
						+ "[/LOGITUDE][CHECKOUT_DATE]"
						+ date
						+ "[/CHECKOUT_DATE][CHECK_OUTTIME]"
						+ store_out_time
						+ "[/CHECK_OUTTIME][CHECK_INTIME]"
						+ store_intime
						+ "[/CHECK_INTIME][CREATED_BY]"
						+ username
						+ "[/CREATED_BY][/STORE_CHECK_OUT_STATUS]";


				final String sos_xml = "[DATA]" + onXML
						+ "[/DATA]";

				SoapObject request = new SoapObject(
						CommonString1.NAMESPACE,
						"Upload_Store_ChecOut_Status");
				request.addProperty("onXML", sos_xml);
				/*request.addProperty("KEYS", "CHECKOUT_STATUS");
			request.addProperty("USERNAME", username);*/
				//request.addProperty("MID", mid);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				envelope.setOutputSoapObject(request);

				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						CommonString1.URL);

				androidHttpTransport.call(
						CommonString1.SOAP_ACTION+"Upload_Store_ChecOut_Status",
						envelope);
				Object result = (Object) envelope.getResponse();


				if (result.toString().equalsIgnoreCase(
						CommonString1.KEY_NO_DATA)) {
					return "Upload_Store_ChecOut_Status";
				}

				if (result.toString().equalsIgnoreCase(
						CommonString1.KEY_FAILURE)) {
					return "Upload_Store_ChecOut_Status";
				}

				// for failure



				data.value = 100;
				data.name = "Checkout Done";
				publishProgress(data);

				database.updateCoverageStoreOutTime(store_id,date , store_out_time, CommonString1.KEY_C);
				
				if (result.toString()
						.equalsIgnoreCase(CommonString1.KEY_SUCCESS_chkout)) {

					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(CommonString1.KEY_STOREVISITED, "");
					editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "");
					editor.putString(CommonString1.KEY_STORE_IN_TIME, "");
					editor.putString(CommonString1.KEY_LATITUDE, "");
					editor.putString(CommonString1.KEY_LONGITUDE, "");
					editor.commit();

					database.updateStoreStatusOnCheckout(store_id, visit_date,
							CommonString1.KEY_C);

				} else {
					if (result.toString().equalsIgnoreCase(
							CommonString1.KEY_FALSE)) {

						return CommonString1.METHOD_Checkout_StatusNew;
					}

					// for failure

				}
				return CommonString1.KEY_SUCCESS;

			} catch (MalformedURLException e) {

				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_EXCEPTION, "download", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						message.showMessage();
					}
				});

			} catch (IOException e) {
				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_SOCKETEXCEPTION,
						"socket", e);
				// counter++;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						message.showMessage();
						// TODO Auto-generated method stub
						/*
						 * if (counter < 10) { new
						 * BackgroundTask(CheckOutUploadActivity
						 * .this).execute(); } else { message.showMessage();
						 * counter =1; }
						 */
					}
				});
			} catch (Exception e) {
				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_EXCEPTION, "download", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});
			}

			return "";
		}

		@Override
		protected void onProgressUpdate(Data... values) {
			// TODO Auto-generated method stub

			pb.setProgress(values[0].value);
			percentage.setText(values[0].value + "%");
			message.setText(values[0].name);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			dialog.dismiss();

			if (result.equals(CommonString1.KEY_SUCCESS)) {

				new UploadTask(CheckoutNUpload.this).execute();
				
			} else if (!result.equals("")) {
				/*AlertMessage message = new AlertMessage(
					CheckOutStoreActivity.this, CommonString1.ERROR + result, "success", null);
			message.showMessage();*/

				Toast.makeText(getApplicationContext(), "Network Error Try Again", Toast.LENGTH_SHORT).show();
				finish();

			}

		}

	}

	class Data {
		int value;
		String name;
	}
	
	public String getCurrentTime() {

		Calendar m_cal = Calendar.getInstance();

		String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

		return intime;

	}

	
	public String UploadImage(String path,String folder_path) throws Exception {

		errormsg = "";
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(Path + path, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;

		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(
				Path + path, o2);

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
		byte[] ba = bao.toByteArray();
		String ba1 = Base64.encodeBytes(ba);

		SoapObject request = new SoapObject(CommonString1.NAMESPACE,
				CommonString1.METHOD_UPLOAD_IMAGE);

		String[] split = path.split("/");
		String path1 = split[split.length - 1];

		request.addProperty("img", ba1);
		request.addProperty("name", path1);
		request.addProperty("FolderName", folder_path);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				CommonString1.URL);

		androidHttpTransport
				.call(CommonString1.SOAP_ACTION_UPLOAD_IMAGE,
						envelope);
		Object result = (Object) envelope.getResponse();

		if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {

			if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
				return CommonString1.KEY_FALSE;
			}

			SAXParserFactory saxPF = SAXParserFactory.newInstance();
			SAXParser saxP = saxPF.newSAXParser();
			XMLReader xmlR = saxP.getXMLReader();

			// for failure
			FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
			xmlR.setContentHandler(failureXMLHandler);

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result.toString()));
			xmlR.parse(is);

			failureGetterSetter = failureXMLHandler
					.getFailureGetterSetter();

			if (failureGetterSetter.getStatus().equalsIgnoreCase(
					CommonString1.KEY_FAILURE)) {
				errormsg = failureGetterSetter.getErrorMsg();
				return CommonString1.KEY_FAILURE;
			}
		} else {
			new File(Path + path).delete();
			SharedPreferences.Editor editor = preferences
					.edit();
			editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "");
			editor.commit();
		}

		return result.toString();
	}

	private class UploadTask extends AsyncTask<Void, Data, String> {
		private Context context;

		UploadTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_upload);
			dialog.setTitle("Uploading Data");
			dialog.setCancelable(false);
			dialog.show();
			pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
			percentage = (TextView) dialog.findViewById(R.id.percentage);
			message = (TextView) dialog.findViewById(R.id.message);
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {

				data = new Data();

				/*HttpParams myParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(myParams, 10000);
				HttpConnectionParams.setSoTimeout(myParams, 10000);
				HttpClient httpclient = new DefaultHttpClient();
				InputStream inputStream = null;*/

				coverageBeanlist = database.getCoverageData(prev_date);

				if (coverageBeanlist.size() > 0) {

					if (coverageBeanlist.size() == 1) {
						factor = 50;
					} else {

						factor = 100 / (coverageBeanlist.size());
					}
				}

				for (int i = 0; i < coverageBeanlist.size(); i++) {



					if (!coverageBeanlist.get(i).getStatus()
							.equalsIgnoreCase(CommonString1.KEY_D) && !coverageBeanlist.get(i).getStatus()
							.equalsIgnoreCase(CommonString1.KEY_CHECK_IN)) {



						if (!coverageBeanlist.get(i).getStatus()
								.equalsIgnoreCase(CommonString1.KEY_D)) {


							String onXML = "[DATA][USER_DATA][STORE_CD]"
									+ coverageBeanlist.get(i).getStoreId()
									+ "[/STORE_CD]" + "[VISIT_DATE]"
									+ coverageBeanlist.get(i).getVisitDate()
									+ "[/VISIT_DATE][LATITUDE]"
									+ "0.0"//coverageBeanlist.get(i).getLatitude()
									+ "[/LATITUDE][APP_VERSION]"
									+ app_ver
									+ "[/APP_VERSION][LONGITUDE]"
									+ "0.0"//coverageBeanlist.get(i).getLongitude()
									+ "[/LONGITUDE][IN_TIME]"
									+ coverageBeanlist.get(i).getInTime()
									+ "[/IN_TIME][OUT_TIME]"
									+ coverageBeanlist.get(i).getOutTime()
									+ "[/OUT_TIME][UPLOAD_STATUS]"
									+ "N"
									+ "[/UPLOAD_STATUS][USER_ID]" + username
									+ "[/USER_ID][IMAGE_URL]" + coverageBeanlist.get(i).getImage()
									+ "[/IMAGE_URL][REASON_ID]"
									+ coverageBeanlist.get(i).getReasonid()
									+ "[/REASON_ID][REASON_REMARK]"
									+ coverageBeanlist.get(i).getRemark()
									+ "[/REASON_REMARK][/USER_DATA][/DATA]";


							SoapObject request = new SoapObject(
									CommonString1.NAMESPACE,
									CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE);
							request.addProperty("onXML", onXML);

							SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
									SoapEnvelope.VER11);
							envelope.dotNet = true;
							envelope.setOutputSoapObject(request);

							HttpTransportSE androidHttpTransport = new HttpTransportSE(
									CommonString1.URL);

							androidHttpTransport.call(
									CommonString1.SOAP_ACTION+ CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE,
									envelope);
							Object result = (Object) envelope.getResponse();

							datacheck = result.toString();
							datacheck = datacheck.replace("\"", "");
							words = datacheck.split("\\;");
							validity = (words[0]);

							if (validity
									.equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
								database.updateCoverageStatus(coverageBeanlist
										.get(i).getMID(), CommonString1.KEY_P);

								database.updateStoreStatusOnLeave(
										coverageBeanlist.get(i).getStoreId(),
										coverageBeanlist.get(i).getVisitDate(),
										CommonString1.KEY_P);
							} else {



								if (!result.toString().equalsIgnoreCase(
										CommonString1.KEY_SUCCESS)) {

									return CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE;
								}

								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_FALSE)) {
									return CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE;
								}
								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_FAILURE)) {
									return CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE;
								}

							}

							mid = Integer.parseInt((words[1]));



							data.value =30;
							data.name = "Uploading";

							publishProgress(data);

							String final_xml = "";


							// stock data

							final_xml = "";
							onXML = "";
							stockData = database.getStockUpload(coverageBeanlist.get(i).getStoreId());

							if (stockData.size()>0) {

								for (int j = 0; j < stockData.size(); j++) {

								/*String actual_listed;
								if(stockData.get(j).getActual_listed().equalsIgnoreCase("yes")){
									actual_listed="1";
								}else{
									actual_listed="0";
								}*/

									onXML = "[STOCK_DATA][MID]"
											+ mid
											+ "[/MID]"
										/*+ "[BRAND_CD]"
										+ stockData.get(j).getBrand_cd()
										+ "[/BRAND_CD]"*/

											+ "[CREATED_BY]"
											+ username
											+ "[/CREATED_BY]"
											+ "[OPENING_TOTAL_STOCK]"
											+ stockData.get(j).getOpenning_total_stock()
											+ "[/OPENING_TOTAL_STOCK]"
											+ "[FACING]"
											+ stockData.get(j).getOpening_facing()
											+ "[/FACING]"
											+ "[STOCK_UNDER_DAYS]"
											+ stockData.get(j).getStock_under45days()
											+ "[/STOCK_UNDER_DAYS]"
											+ "[EXPRIY_DATE]"
											+ stockData.get(j).getDate()
											+ "[/EXPRIY_DATE]"
											+ "[SKU_CD]"
											+ stockData.get(j).getSku_cd()
											+ "[/SKU_CD]"




											+ "[/STOCK_DATA]";

									final_xml = final_xml + onXML;

								}

								final String sos_xml = "[DATA]" + final_xml
										+ "[/DATA]";

								request = new SoapObject(
										CommonString1.NAMESPACE,
										CommonString1.METHOD_UPLOAD_XML);
								request.addProperty("XMLDATA", sos_xml);
								request.addProperty("KEYS", "STOCK_DATA");
								request.addProperty("USERNAME", username);
								request.addProperty("MID", mid);

								envelope = new SoapSerializationEnvelope(
										SoapEnvelope.VER11);
								envelope.dotNet = true;
								envelope.setOutputSoapObject(request);

								androidHttpTransport = new HttpTransportSE(
										CommonString1.URL);

								androidHttpTransport.call(
										CommonString1.SOAP_ACTION+ CommonString1.METHOD_UPLOAD_XML,
										envelope);
								result = (Object) envelope.getResponse();


								if (!result.toString().equalsIgnoreCase(
										CommonString1.KEY_SUCCESS)) {

									return "STOCK_DATA";
								}


								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_NO_DATA)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_FAILURE)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}


							}


							data.value =50;
							data.name = "STOCK_DATA";

							publishProgress(data);




//							uploading windows data

							final_xml = "";
							onXML = "";
							WindowsData = database.getwindowsUpload(coverageBeanlist.get(i).getStoreId());

							if (WindowsData.size()>0) {

								for (int j = 0; j < WindowsData.size(); j++) {

									onXML = "[WINDOWS_DATA][MID]"
											+ mid
											+ "[/MID]"

											+ "[CREATED_BY]"
											+ username
											+ "[/CREATED_BY]"
											+ "[WINDOW_CD]"
											+ WindowsData.get(j).getWINDOW_CD()
											+ "[/WINDOW_CD]"



											+ "[REASON]"
											+ WindowsData.get(j).getRemarks()
											+ "[/REASON]"

											+ "[KEY_ID]"
											+ WindowsData.get(j).getHeaderRefId()
											+ "[/KEY_ID]"


											+ "[STATUS_CD]"
											+ WindowsData.get(j).getSTATUS_CD()
											+ "[/STATUS_CD]"


											+ "[IMAGE_URL]"
											+ WindowsData.get(j).getImage()
											+ "[/IMAGE_URL]"
											+ "[/WINDOWS_DATA]";

									final_xml = final_xml + onXML;

								}

								final String sos_xml = "[DATA]" + final_xml
										+ "[/DATA]";

								request = new SoapObject(
										CommonString1.NAMESPACE,
										CommonString1.METHOD_UPLOAD_XML);
								request.addProperty("XMLDATA", sos_xml);
								request.addProperty("KEYS", "WINDOWS_DATA");
								request.addProperty("USERNAME", username);
								request.addProperty("MID", mid);

								envelope = new SoapSerializationEnvelope(
										SoapEnvelope.VER11);
								envelope.dotNet = true;
								envelope.setOutputSoapObject(request);

								androidHttpTransport = new HttpTransportSE(
										CommonString1.URL);

								androidHttpTransport.call(
										CommonString1.SOAP_ACTION+ CommonString1.METHOD_UPLOAD_XML,
										envelope);
								result = (Object) envelope.getResponse();


								if (!result.toString().equalsIgnoreCase(
										CommonString1.KEY_SUCCESS)) {

									return "WINDOWS_DATA";
								}


								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_NO_DATA)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_FAILURE)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

							}


							data.value =60;
							data.name = "WINDOWS_DATA";

							publishProgress(data);






//							uploading STORE_DETAIL data

							final_xml = "";
							onXML = "";
							STOREDETAIL = database.getSTOREDETAILUpload(coverageBeanlist.get(i).getStoreId());

							if (STOREDETAIL.size()>0) {

								for (int j = 0; j < STOREDETAIL.size(); j++) {

									onXML = "[STORE_DETAIL_DATA][MID]"
											+ mid
											+ "[/MID]"

											+ "[CREATED_BY]"
											+ username
											+ "[/CREATED_BY]"

											+ "[STORE_CD]"
											+ STOREDETAIL.get(j).getSTORE_ID()
											+ "[/STORE_CD]"

											+ "[NAME]"
											+ STOREDETAIL.get(j).getName()
											+ "[/NAME]"

											+ "[CONTACT]"
											+ STOREDETAIL.get(j).getContact()
											+ "[/CONTACT]"

											+ "[ADDRESS]"
											+ STOREDETAIL.get(j).getAddress()
											+ "[/ADDRESS]"

											+ "[/STORE_DETAIL_DATA]";

									final_xml = final_xml + onXML;

								}

								final String sos_xml = "[DATA]" + final_xml
										+ "[/DATA]";

								request = new SoapObject(
										CommonString1.NAMESPACE,
										CommonString1.METHOD_UPLOAD_XML);
								request.addProperty("XMLDATA", sos_xml);
								request.addProperty("KEYS", "STORE_DETAIL_DATA");
								request.addProperty("USERNAME", username);
								request.addProperty("MID", mid);

								envelope = new SoapSerializationEnvelope(
										SoapEnvelope.VER11);
								envelope.dotNet = true;
								envelope.setOutputSoapObject(request);

								androidHttpTransport = new HttpTransportSE(
										CommonString1.URL);

								androidHttpTransport.call(
										CommonString1.SOAP_ACTION+ CommonString1.METHOD_UPLOAD_XML,
										envelope);
								result = (Object) envelope.getResponse();


								if (!result.toString().equalsIgnoreCase(
										CommonString1.KEY_SUCCESS)) {

									return "STORE_DETAIL_DATA";
								}


								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_NO_DATA)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_FAILURE)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

							}


							data.value =65;
							data.name = "STORE_DETAIL_DATA";

							publishProgress(data);











							///// checklistDAta





							final_xml = "";
							onXML = "";
							WindowsDataChecklist = database.getCheckListUpload(coverageBeanlist.get(i).getStoreId());

							if (WindowsDataChecklist.size()>0) {

								for (int j = 0; j < WindowsDataChecklist.size(); j++) {

									onXML = "[WINDOWS_CHECKLIST_DATA][MID]"
											+ mid
											+ "[/MID]"

											+ "[CREATED_BY]"
											+ username
											+ "[/CREATED_BY]"
											+ "[WINDOW_CD]"
											+ WindowsDataChecklist.get(j).getWindows_cd()
											+ "[/WINDOW_CD]"

											+ "[KEY_ID]"
											+ WindowsDataChecklist.get(j).getRefId()
											+ "[/KEY_ID]"



											+ "[CHECKLIST_ID]"
											+ WindowsDataChecklist.get(j).getCHECKLIST_ID().get(0)
											+ "[/CHECKLIST_ID]"

											+ "[STATUS]"
											+ WindowsDataChecklist.get(j).getStatus()
											+ "[/STATUS]"

											+ "[/WINDOWS_CHECKLIST_DATA]";

									final_xml = final_xml + onXML;

								}

								final String sos_xml = "[DATA]" + final_xml
										+ "[/DATA]";

								request = new SoapObject(
										CommonString1.NAMESPACE,
										CommonString1.METHOD_UPLOAD_XML);
								request.addProperty("XMLDATA", sos_xml);
								request.addProperty("KEYS", "WINDOWS_CHECKLIST");
								request.addProperty("USERNAME", username);
								request.addProperty("MID", mid);

								envelope = new SoapSerializationEnvelope(
										SoapEnvelope.VER11);
								envelope.dotNet = true;
								envelope.setOutputSoapObject(request);

								androidHttpTransport = new HttpTransportSE(
										CommonString1.URL);

								androidHttpTransport.call(
										CommonString1.SOAP_ACTION+ CommonString1.METHOD_UPLOAD_XML,
										envelope);
								result = (Object) envelope.getResponse();


								if (!result.toString().equalsIgnoreCase(
										CommonString1.KEY_SUCCESS)) {

									return "WINDOWS_CHECKLIST";
								}

								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_NO_DATA)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

								if (result.toString().equalsIgnoreCase(
										CommonString1.KEY_FAILURE)) {
									return CommonString1.METHOD_UPLOAD_XML;
								}

							}


							data.value =70;
							data.name = "WINDOWS_CHECKLIST_DATA";

							publishProgress(data);



							if (coverageBeanlist.get(i).getImage() != null && !coverageBeanlist.get(i).getImage().equals("")) {

								if (new File(CommonString1.FILE_PATH + coverageBeanlist.get(i).getImage()).exists()) {

									result = UploadImage(coverageBeanlist.get(i).getImage(),"StockImages");



									if (!result
											.toString()
											.equalsIgnoreCase(
													CommonString1.KEY_SUCCESS)) {

										return "StoreImages";
									}

									else if (result
											.toString()
											.equalsIgnoreCase(
													CommonString1.KEY_FALSE)) {

										return "StoreImages";
									} else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

										return "StoreImages"
												+ "," + errormsg;
									}

									runOnUiThread(new Runnable() {

										public void run() {

											message.setText("Store Image Uploaded");
										}
									});

								}
							}


							data.value =80;
							data.name = "StoreImages";

							publishProgress(data);


							if (WindowsData.size()>0) {

								for (int j = 0; j < WindowsData.size(); j++) {

									if (WindowsData.get(j).getImage() != null && !WindowsData.get(j).getImage().equals("")) {

										if (new File(CommonString1.FILE_PATH + WindowsData.get(j).getImage()).exists()) {

											result = UploadImage(WindowsData.get(j).getImage(),"WindowImages");



											if (!result
													.toString()
													.equalsIgnoreCase(
															CommonString1.KEY_SUCCESS)) {

												return "WindowImages";
											}

											else if (result
													.toString()
													.equalsIgnoreCase(
															CommonString1.KEY_FALSE)) {

												return "WindowImages";
											} else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

												return "WindowImages"
														+ "," + errormsg;
											}

											runOnUiThread(new Runnable() {

												public void run() {

													message.setText("Windows Image Uploaded");
												}
											});

										}
									}
								}
							}

							data.value =90;
							data.name = "WindowImages";

							publishProgress(data);



							// SET COVERAGE STATUS

							final_xml = "";
							onXML = "";

							onXML = "[COVERAGE_STATUS][STORE_ID]"
									+ coverageBeanlist.get(i).getStoreId()
									+ "[/STORE_ID]"
									+ "[VISIT_DATE]"
									+ coverageBeanlist.get(i).getVisitDate()
									+ "[/VISIT_DATE]"
									+ "[USER_ID]"
									+ coverageBeanlist.get(i).getUserId()
									+ "[/USER_ID]"
									+ "[STATUS]"
									+ CommonString1.KEY_D
									+ "[/STATUS]"
									+ "[/COVERAGE_STATUS]";

							final_xml = final_xml + onXML;

							final String sos_xml = "[DATA]" + final_xml
									+ "[/DATA]";

							SoapObject request1 = new SoapObject(
									CommonString1.NAMESPACE,
									CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS);
							request1.addProperty("onXML", sos_xml);


							SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(
									SoapEnvelope.VER11);
							envelope1.dotNet = true;
							envelope1.setOutputSoapObject(request1);

							HttpTransportSE androidHttpTransport1 = new HttpTransportSE(
									CommonString1.URL);

							androidHttpTransport1.call(
									CommonString1.SOAP_ACTION+ CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS,
									envelope1);
							Object result1 = (Object) envelope1.getResponse();


							if (result1.toString().equalsIgnoreCase(
									CommonString1.KEY_SUCCESS)) {

								database.open();

								database.updateCoverageStatus(coverageBeanlist.get(i)
										.getMID(), CommonString1.KEY_D);
								database.updateStoreStatusOnLeave(coverageBeanlist.get(i)
										.getStoreId(), coverageBeanlist.get(i)
										.getVisitDate(), CommonString1.KEY_D);

							}


							if (!result1
									.toString()
									.equalsIgnoreCase(
											CommonString1.KEY_SUCCESS)) {

								return "COVERAGE_STATUS";
							}


							if (result1.toString().equalsIgnoreCase(
									CommonString1.KEY_NO_DATA)) {
								return CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS;
							}

							if (result1.toString().equalsIgnoreCase(
									CommonString1.KEY_FAILURE)) {
								return CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS;
							}

							data.value =factor*(i+1);
							data.name = "COVERAGE_STATUS";

							publishProgress(data);



						}

					}

				}


			}

			catch (MalformedURLException e) {

				up_success_flag=false;

				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_EXCEPTION, "download", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});

			} catch (IOException e) {

				up_success_flag=false;

				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						message.showMessage();

					}
				});
			}

			catch (Exception e) {

				up_success_flag=false;

				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_EXCEPTION, "download", e);

				e.getMessage();
				e.printStackTrace();
				e.getCause();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});
			}
			if(	up_success_flag==true){
				return CommonString1.KEY_SUCCESS;
			}
			else{
				return CommonString1.KEY_FAILURE;
			}


		}

		@Override
		protected void onProgressUpdate(Data... values) {
			// TODO Auto-generated method stub

			pb.setProgress(values[0].value);
			percentage.setText(values[0].value + "%");
			message.setText(values[0].name);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			dialog.dismiss();

			if (result.contains(CommonString1.KEY_SUCCESS)) {


					AlertMessage message = new AlertMessage(
							CheckoutNUpload.this,
							AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
					message.showMessage();

				//new UploadImageTask(CheckoutNUpload.this).execute();

					database.deleteAllTables();
			}
			else {

				AlertMessage message = new AlertMessage(
						CheckoutNUpload.this, "Error in uploading :" + result, "success", null);
				message.showMessage();
			}


		}
	}


/*
	private class UploadImageTask extends AsyncTask<Void, Void, String> {
		private Context context;

		UploadImageTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_upload);
			dialog.setTitle("Uploading Image");
			dialog.setCancelable(false);
			dialog.show();
			pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
			percentage = (TextView) dialog.findViewById(R.id.percentage);
			message = (TextView) dialog.findViewById(R.id.message);
		}

		@Override
		protected String doInBackground(Void... params)
		 */
/*{
			// TODO Auto-generated method stub

			try {

				coverageBeanlist=db.getCoverageData(prev_date);
				if(coverageBeanlist.size()>0){
					for(int i=0;i<coverageBeanlist.size();i++){

						status=coverageBeanlist.get(i).getStatus();
						if(status.equals(CommonString1.STORE_STATUS_LEAVE)){

							String path=coverageBeanlist.get(i).getImage();
							if(path!=null && !path.equals("")){
								result = UploadImage(path,"StoreImages");
							}
							else{
								result = CommonString1.KEY_SUCCESS;
							}

						}

						assetInsertdata = db.getAssetUpload(coverageBeanlist.get(i).getStoreId());

						if (assetInsertdata.size()>0) {

							for (int j = 0; j < assetInsertdata.size(); j++) {

								if (assetInsertdata.get(j).getImg() != null
										&& !assetInsertdata.get(j)
										.getImg().equals("")) {

									if (new File(
											Environment.getExternalStorageDirectory() + "/Mondelez_Images/"

													+ assetInsertdata.get(j).getImg())
											.exists()) {

										result = UploadImage(assetInsertdata.get(j).getImg(), "AssetImages");


										if (result
												.toString()
												.equalsIgnoreCase(
														CommonString1.KEY_FALSE)) {

											return "Asset Images";
										} else if (result
												.equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

											return "Asset Images"
													+ "," + errormsg;
										}

										runOnUiThread(new Runnable() {

											public void run() {


												message.setText("Asset Images Uploaded");
											}
										});



									}
								}

							}

						}

						//promotionData = db.getPromotionUpload(coverageBeanlist.get(i).getStoreId());

						if (promotionData.size()>0) {

							for (int j = 0; j < promotionData.size(); j++) {

								if (promotionData.get(j).getImg() != null
										&& !promotionData.get(j)
										.getImg().equals("")) {

									if (new File(
											Environment.getExternalStorageDirectory() + "/Mondelez_Images/"

													+ promotionData.get(j).getImg())
											.exists()) {

										result = UploadImage(promotionData.get(j).getImg(), "PromotionImages");


										if (result
												.toString()
												.equalsIgnoreCase(
														CommonString1.KEY_FALSE)) {

											return "Asset Images";
										} else if (result
												.equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

											return "Asset Images"
													+ "," + errormsg;
										}

										runOnUiThread(new Runnable() {

											public void run() {


												message.setText("Asset Images Uploaded");
											}
										});



									}

								}

							}

						}

						poiInsertdata = db.getPOIData(coverageBeanlist.get(i).getStoreId());

						if (poiInsertdata.size()>0) {

							for (int j = 0; j < poiInsertdata.size(); j++) {

								if (poiInsertdata.get(j).getImage() != null
										&& !poiInsertdata.get(j)
										.getImage().equals("")) {

									if (new File(
											Environment.getExternalStorageDirectory() + "/Mondelez_Images/"

													+ poiInsertdata.get(j).getImage())
											.exists()) {

										result = UploadImage(poiInsertdata.get(j).getImage(), "POIImages");



										if (result
												.toString()
												.equalsIgnoreCase(
														CommonString1.KEY_FALSE)) {

											return "POI Images";
										} else if (result
												.equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

											return "POI Images"
													+ "," + errormsg;
										}

										runOnUiThread(new Runnable() {

											public void run() {

												message.setText("POI Images Uploaded");
											}
										});

									}

								}

							}

						}

						stockImgData = db.getHeaderStockImageData(coverageBeanlist.get(i).getStoreId(), prev_date);

						if (stockImgData.size()>0) {

							for (int j = 0; j < stockImgData.size(); j++) {

								if (stockImgData.get(j).getImg_cam() != null
										&& !stockImgData.get(j)
										.getImg_cam().equals("")) {

									if (new File(
											Environment.getExternalStorageDirectory() + "/Mondelez_Images/"

													+ stockImgData.get(j).getImg_cam())
											.exists()) {

										result =UploadImage(stockImgData.get(j).getImg_cam(),"StockImages");


										if (result
												.toString()
												.equalsIgnoreCase(
														CommonString1.KEY_FALSE)) {

											return "Stock Images";
										} else if (result
												.equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

											return "Stock Images"
													+ "," + errormsg;
										}

										runOnUiThread(new Runnable() {

											public void run() {

												message.setText("Stock Images Uploaded");
											}
										});

									}

								}

							}

						}

						return CommonString1.KEY_SUCCESS;
					}
				}






			} catch (MalformedURLException e) {

				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_EXCEPTION, "socket_uploadimagesall", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});

			} catch (IOException e) {
				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_SOCKETEXCEPTION,
						"socket_uploadimagesall", e);
				counter++;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						message.showMessage();
						// TODO Auto-generated method stub
						*//*
*/
/*
						 * if (counter < 3) { new
						 * UploadTask(UploadAllImageActivity.this).execute(); }
						 * else { message.showMessage(); counter = 1; }
						 *//*
*/
/*
					}
				});
			} catch (Exception e) {
				final AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_EXCEPTION, "socket_uploadimagesall", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();

					}
				});
			}


			return "";
		}*//*


		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			dialog.dismiss();

			if (result.equals(CommonString1.KEY_SUCCESS)) {
				db.open();
				db.deleteAllTables();

				AlertMessage message = new AlertMessage(
						CheckoutNUpload.this,
						AlertMessage.MESSAGE_UPLOAD_IMAGE, "success", null);
				message.showMessage();

			} else if (!result.equals("")) {
				AlertMessage message = new AlertMessage(
						CheckoutNUpload.this, result, "success", null);
				message.showMessage();
			}
		}



		public String UploadImage(String path, String folder_name) throws Exception {

			errormsg = "";
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(Path + path, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 1024;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;

			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(
					Path + path, o2);

			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			byte[] ba = bao.toByteArray();
			String ba1 = Base64.encodeBytes(ba);

			SoapObject request = new SoapObject(CommonString1.NAMESPACE,
					CommonString1.METHOD_UPLOAD_IMAGE);

			String[] split = path.split("/");
			String path1 = split[split.length - 1];

			request.addProperty("img", ba1);
			request.addProperty("name", path1);
			request.addProperty("FolderName", folder_name);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					CommonString1.URL);

			androidHttpTransport
					.call(CommonString1.SOAP_ACTION_UPLOAD_IMAGE,
							envelope);
			Object result = (Object) envelope.getResponse();

			if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {

				if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
					return CommonString1.KEY_FALSE;
				}

				SAXParserFactory saxPF = SAXParserFactory.newInstance();
				SAXParser saxP = saxPF.newSAXParser();
				XMLReader xmlR = saxP.getXMLReader();

				// for failure
				FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
				xmlR.setContentHandler(failureXMLHandler);

				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(result.toString()));
				xmlR.parse(is);

				failureGetterSetter = failureXMLHandler
						.getFailureGetterSetter();

				if (failureGetterSetter.getStatus().equalsIgnoreCase(
						CommonString1.KEY_FAILURE)) {
					errormsg = failureGetterSetter.getErrorMsg();
					return CommonString1.KEY_FAILURE;
				}
			} else {
				new File(Path + path).delete();
			}

			return "";
		}

	}
*/



}
