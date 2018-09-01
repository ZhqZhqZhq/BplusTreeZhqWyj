package BTreeIndex.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/*
 * 路径下所有的目录及其文件获取操作
 */
public class FileList {
	//全盘树 文件名+路径
	Map<String, ArrayList<String>> listmap = new HashMap<String, ArrayList<String>>();  //ArrayList动态数组
	HashSet<String> indexset = new HashSet<String>(); //存某路径下的文件名
	
	/*
	 * 通过递归得到某一路径下所有的目录及其文件
	 * 返回Map形式的键值对
	 * filePath：路径
	 */
	public Map<String, ArrayList<String>> getFiles(String filePath) 
	{
		// IdentityHashMap<String, Object> listmap = new IdentityHashMap<String,
		// Object>();
		File file = new File(filePath);
		if (file.exists() && file.canRead()) 
		{
			File[] files = file.listFiles();              //所有文件的完整路径  即盘符+名字组成的字符串file数组  含有子文件夹中文件
			 if (files == null || files.length == 0) 
			 {
			 // System.out.println("文件夹是空的!");
				return null;
			 } 
			 else 
			 {
				for (File file2 : files) 
				{
					ArrayList<String> pathlist = new ArrayList<String>();

					if (file2.isDirectory()) 
					{
						if (indexset.contains(file2.getName())) 
						{   //获取文件名字
							listmap.get(file2.getName()).add(file2.getAbsolutePath());
						} 
						else 
						{
							indexset.add(file2.getName());
							pathlist.add(file2.getAbsolutePath());
							listmap.put(file2.getName(), pathlist);  //将文件名和路径添加到map
							// System.out.println("文件夹:" + file2.getAbsolutePath());
						}
						getFiles(file2.getAbsolutePath());
					} 
					else 
					{
						if (indexset.contains(file2.getName())) 
						{
							listmap.get(file2.getName()).add(file2.getAbsolutePath());
						} else {
							indexset.add(file2.getName());
							pathlist.add(file2.getAbsolutePath());
							listmap.put(file2.getName(), pathlist); 

						}
					}
				}
			}
		}
		return listmap;
	}

	
	/**
	 * (无参数重载)
	 * 通过递归得到所有盘符路径下的目录及其文件  键值对形式  
	 */
	public Map<String, ArrayList<String>> getFiles() 
	{
		// IdentityHashMap<String, Object> listmap = new IdentityHashMap<String,
		// Object>();
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) 
		{			//roots是盘符
			File file = new File(roots[i].getPath());      //file是盘符路径
			if (file.exists() && file.canRead()) 
			{     //目录存在，能否被读
				File[] files = file.listFiles();       //listFiles()方法是返回C盘下所有文件和目录的绝对路径，返回的是File数组    files是所有文件的 地址
				if (files == null || files.length == 0) 
				{
					// System.out.println("文件夹是空的!");
					return null;
				} else {
					for (File file2 : files)  //遍历目录里的文件
					{    
						ArrayList<String> pathlist = new ArrayList<String>();
						if (file2.isDirectory()) 
						{   //java中的isDirectory()是检查一个对象是否是文件夹。//得到文件夹内所有   递归得到地址串
							if (indexset.contains(file2.getName())) 
							{
								listmap.get(file2.getName()).add(file2.getAbsolutePath()); //同名文件在路径后添加路径
							} else {
								indexset.add(file2.getName()); //.File.getName() 方法返回的路径名的名称序列的最后一个名字，表示此抽象路径名的文件或目录的名称。
								pathlist.add(file2.getAbsolutePath());
								listmap.put(file2.getName(), pathlist);  //字符串数组，存路径    存文件夹下所有文件名字+路径
								// System.out.println("文件夹:" +
								// file2.getAbsolutePath());
							}
							getFiles(file2.getAbsolutePath());   //递归出所有文件夹内容到listmap中
						} 
						else 
						{
							if (indexset.contains(file2.getName())) 
							{
								listmap.get(file2.getName()).add(file2.getAbsolutePath());
							} 
							else 
							{
								indexset.add(file2.getName());
								pathlist.add(file2.getAbsolutePath());
								listmap.put(file2.getName(), pathlist);
								// System.out.println("文件:" +
								// file2.getAbsolutePath());
							}
						}
					}
				}
			}
		}

		return listmap;
	}


	/**
	 * 实现将索引文件保存在某处
	 * fileAddress：保存目的地址
	 * fileMap：文件名和路径的键值对形式保存
	 */
	public void saveFileIndex(String fileAddress, Map<String, ArrayList<String>> fileMap) 
	{
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(new File(fileAddress)));
			output.writeObject(fileMap);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 读取保存在某处的索引文件
	 * fileAddress：读取索引文件地址
	 * 文件名和路径的键值对形式获取
	 */
	public Map<String, ArrayList<String>> readFileIndex(String fileAddress) 
	{
		ObjectInputStream input = null;
		Object obj = null;
		Map<String, ArrayList<String>> listmap = new HashMap<String, ArrayList<String>>();
		try {
			input = new ObjectInputStream(new FileInputStream(new File(fileAddress)));
			obj = input.readObject();
			if (obj == null) 
			{
				System.out.println("索引文件为空");
				input.close();
				return null;
			}
			if (obj != null) 
			{
				Map<String, ArrayList<String>> readMap = (Map<String, ArrayList<String>>) obj;
				//System.out.println("Object Description:");
				for (Entry<String, ArrayList<String>> entry : readMap.entrySet()) 
				{
					listmap.put(entry.getKey(), entry.getValue());
				}
				input.close();

			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return listmap;
	}
}