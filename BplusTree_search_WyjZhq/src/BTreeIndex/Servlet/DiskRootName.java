package BTreeIndex.Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

/**
 * 首页获得本地磁盘目录
 */
public class DiskRootName extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8"); 	
		File[] files = File.listRoots();       //files存储盘符名
		List<String> diskRootList = new ArrayList<String>();
		diskRootList.add("all");
		
		for(File file : files)
		{
			diskRootList.add(file.getPath().substring(0,1));   //此字符串的第一个字母 C
		}
		String json = JSONArray.fromObject(diskRootList).toString(); 
		
		try { //将盘符发给前端进行显示
				PrintWriter pw = response.getWriter(); 
				pw.write(json); 
				pw.flush(); 
				pw.close(); 
			} 
		catch (IOException e) 
		{ 
				e.printStackTrace();
		} 
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
