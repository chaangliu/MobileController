//����Service���������Ĺ�����ʵ������PhoneBook��CallLog�����ϴ����ֵ���UploadUtils.java�е�uploadFile������ʵ�֡�


package com.system.mobilecontroller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

public class UploadThread implements Runnable {
	  /* �ϴ��ļ���Server�ķ��� */
	
	  String tarFile ; 
	  public void setTar(String tarFile)
	  {
		  this.tarFile = tarFile ; 
	  }
	  public void run()
	  {
		String srcPath = Environment.getExternalStorageDirectory().getPath()+"/Android/" + tarFile ;
		String actionUrl = "http://192.168.23.1:8080/upload_file_service/upload.jsp";
	    String uploadUrl = "http://192.168.23.1:8080/upload_file_service/UploadServlet";
	    String end = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "******";
	    try
	    {
	      URL url = new URL(uploadUrl);
	      HttpURLConnection httpURLConnection = (HttpURLConnection) url
	          .openConnection();
	      
	      
	      httpURLConnection.setDoInput(true);
	      httpURLConnection.setDoOutput(true);
	      httpURLConnection.setUseCaches(false);
	      httpURLConnection.setRequestMethod("POST");
	      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
	      httpURLConnection.setRequestProperty("Charset", "UTF-8");
	      httpURLConnection.setRequestProperty("Content-Type",
	          "multipart/form-data;boundary=" + boundary);
	      
//	      HTTP�ͻ����������������������������������ʱ�����ָ���������ͣ�һ����GET����POST����
//	      ���б�Ҫ���ͻ����򻹿���ѡ��������������ͷ��      
	      
//	      �������̹�������Ҫ��������ϴ��ļ���Multipart/form-data���ϴ��ļ���һ�ַ�ʽ��
//	      Multipart/form-data��ʵ����������ñ��ϴ��ļ��ķ�ʽ��������龳�ǣ���д�ʼ�ʱ�����ʼ�����Ӹ���������ͨ��ʹ�ñ���ӣ�
//	      Ҳ������multipart/form-data��ʽ�ϴ�����������
	      
//	      ��boundary���������������в�ͬ�������ݵġ������еı����� 2 �������ݣ��á�boundary��������
//	      ��boundary��һ����ϵͳ�����������Ҳ���Լ򵥵��á�-------------�������档

	      DataOutputStream dos = new DataOutputStream(httpURLConnection
	          .getOutputStream());
	      dos.writeBytes(twoHyphens + boundary + end);
	      //--******
	      
//	      ����lastIndexOf("/")�ѹ�궨λ�����һ��"/"ǰ�棨�������λ�����ڵ�����λ�ã�һ�����֣�������+1�Ƶ�/�ĺ���һλ��
//	      ����subString��ȡ��/��һλ�����������ַ���
	      dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
	              + srcPath.substring(srcPath.lastIndexOf("/") + 1)
	              + "\"" + end);
	      //filename = \" " xx " \"
	      
	      dos.writeBytes(end);//��htmlЭ���У��� ��\r\n�� ���У������� ��\n����

	      FileInputStream fis = new FileInputStream(srcPath);//��FileInputStream����
	      byte[] buffer = new byte[8192]; // 8k
	      int count = 0;
	      while ((count = fis.read(buffer)) != -1)
	      {
	        dos.write(buffer, 0, count); //��DataOutputStreamд

	      }
	      fis.close();

	      dos.writeBytes(end);
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
	      dos.flush();
	      //�����У�--*******--�����У�

	      InputStream is = httpURLConnection.getInputStream();
	      InputStreamReader isr = new InputStreamReader(is, "utf-8");
	      BufferedReader br = new BufferedReader(isr);
	      String result = br.readLine();

//	      Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	      dos.close();
	      is.close();

	    } catch (Exception e)
	    {
	      e.printStackTrace();
	    }

	  }

}
