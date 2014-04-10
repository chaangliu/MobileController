package rc4;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class NewEnc {

	public static void start(String toBeEncryptedPath, String encryptedPath) throws IOException {
		// String inputStr =
		// "姓名:姑姑爱大雕号码:13812345678类型:INCOMING时间:2014-03-04 03:12:10";
		// String inputStr =
		// "1234567类型:INCOMING号码:13812345678时间:2014-03-04 03:12:10";
		// String inputStr =
		// "从文件中读取并解密的------->姓名:null   号码:83522971   类型:OUTGOING   时间:2014-03-04 05:26:42 姓名:null   号码:15651838550   类型:OUTGOING   时间:2014-03-04 05:02:19 姓名:null   号码:15651838550   类型:MISSED  时间:2014-03-04 05:26:42 姓名:null   号码:15651838550   类型:OUTGOING   时间:2014-03-04 05:02:19 姓名:null   号码:15651838550   类型:MISSED";
		String inputStr = OriginalText(toBeEncryptedPath);

		String key = "12345";
		String encryptedStr = RC4.encry_RC4_string(inputStr, key);
//		String EncryptedStrToHex = bin2hex(encryptedStr);
		OutputStream os;
		os = new FileOutputStream(encryptedPath);
		OutputStreamWriter osw = new OutputStreamWriter(os,
				Charset.forName("gbk"));
		try {
			osw.write(encryptedStr);
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String OriginalText(String destFile) throws IOException {
		String lines = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(destFile), "GBK"));
		String line = null;
		while ((line = br.readLine()) != null) {
			lines = lines + line;
		}
		br.close();
		return lines;
	}
	
}
