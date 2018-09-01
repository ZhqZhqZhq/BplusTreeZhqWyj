
package BTreeIndex.utils;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

//读取本地文件格式
public class ReadButtonType {

	//若有异常则抛给Node.getbutton
	public String ReadFile(String readPath) throws Exception
	{
		BufferedReader bufr = null;
		String laststr = "";
		
		FileInputStream fileInputStream = new FileInputStream(readPath);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
		
		bufr = new BufferedReader(inputStreamReader);
		String tempString = null;
		
		while ((tempString = bufr.readLine()) != null) 
		{
			laststr += tempString;
		}

		bufr.close();

		return laststr;
	}
}
