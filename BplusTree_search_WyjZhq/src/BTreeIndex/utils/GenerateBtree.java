package BTreeIndex.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import BTreeIndex.BplusTree;
import BTreeIndex.Node;


/**
 * 生成索引的Servlet 创建和操作索引   服务器启动时会加载
 * 若索引文件存在则读取，不存在则生成
 */
public class GenerateBtree 
{
	private static String overAllIndexPath = "E:\\fileIndex\\overAllIndex";   //全局索引存放地址

	// 需要生成的索引树
	private BplusTree treedisk = new BplusTree(10);  //单个磁盘树
	private static BplusTree treeindex = new BplusTree(10);  //全局树

	private static List<String> rootstr = new ArrayList<>();



	static 
	{
		//预加载树到内存中
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) 
		{
			rootstr.add(roots[i].getPath());  //存放盘符路径
		}
	}

	/**
	 * 生成全局索引树
	 * index：是否重建索引
	 * 
	 */
	public BplusTree generateBtree(String index) 
	{  //index 是否重建索引标志
		 //bplusTree = new BplusTree(15);
		FileList fileList = new FileList();  //文件名列表
		File fileIndex = new File(overAllIndexPath); //索引存放位置
		Map<String, ArrayList<String>> fileNode = new HashMap<String, ArrayList<String>>();     //fileNode 子节点 文件名+路径
		//long current = System.currentTimeMillis();

		//如果重建索引，重新读取磁盘文件，并将索引文件保存到磁盘
		if ("Yes".equals(index)) 
		{
			fileNode = fileList.getFiles();
			fileList.saveFileIndex(overAllIndexPath, fileNode);
		} 
		else 
		{
			//默认传入index是no，不选择重建索引，则读取索引文件
			//fileIndex.length()表示文件的字节数
			if (fileIndex.exists() && fileIndex.length() != 0) 
			{
				fileNode = fileList.readFileIndex(overAllIndexPath);
			} 
			else 
			{
				//无索引文件时用重建索引
				fileNode = fileList.getFiles();  //得到文件名+路径map  得到所有磁盘文件的树
				fileList.saveFileIndex(overAllIndexPath, fileNode); //存储索引
			}
		}

		// 建树
		for (Entry<String, ArrayList<String>> entry : fileNode.entrySet())  //索引中的实体存入树中
		{
			// System.out.println(entry.getValue());
			treeindex.insertOrUpdate(entry.getKey(), entry.getValue());  //treeindex是建立好的B+树  将获得的文件——路径map存入B+树中
		}
		return treeindex;
	}

	/**
	 * 生成单个磁盘索引树
	 * root：磁盘路径
	 * index：是否重建索引
	 */
	public BplusTree conditionTree(String root, String index) {  //磁盘路径  和   默认"NO"

		String diskindexpath = "";
		String rootpath = null;
		FileList fileList = new FileList();
		Map<String, ArrayList<String>> fileNode = new HashMap<String, ArrayList<String>>();   //引索

		// 确定磁盘索引文件存在的位置
		for (String r : rootstr)   //r  C：
		{
			if (r.indexOf((root.toUpperCase())) > -1) 
			{
				diskindexpath = "E:\\fileIndex\\"+r.substring(0,1)+ "index";
				rootpath = r;  //找到对应的检索磁盘
			}
		}
		File diskindex = new File(diskindexpath);//通过将给定路径名字符串转换成抽象路径名来创建一个新 File 实例  在这个位置建立一个文件，存放单个磁盘的引索
		if ("Yes".equals(index)) 
		{//重新引索
			fileNode = fileList.getFiles(rootpath);
			fileList.saveFileIndex(diskindexpath, fileNode); //存放单磁盘引索
		} 
		else 
		{
			if (diskindex.exists() && diskindex.length() != 0) 
			{
				fileNode = fileList.readFileIndex(diskindexpath);
			} 
			else 
			{
				fileNode = fileList.getFiles(rootpath);           //获取单个磁盘的文件map
				fileList.saveFileIndex(diskindexpath, fileNode);
			}

		}
		// 建树
		for (Entry<String, ArrayList<String>> entry : fileNode.entrySet()) 
		{
			treedisk.insertOrUpdate(entry.getKey(), entry.getValue());
		}

		return treedisk;
	}

	
	/**
	 * 获取索引树中的文件名和路径
	 * tree：索引树
	 */
	 //所有的数据项都是存在叶子节点，而叶子节点之间有指向。所以遍历b+树只需要遍历叶子节点
	public static Map<String, ArrayList<String>> getMap(BplusTree tree) 
	{
		Map<String, ArrayList<String>> listmap = new HashMap<String, ArrayList<String>>();
		Node head = tree.getHead();
		while (head != null) 
		{
			List<Entry<Comparable, ArrayList<String>>> list = head.getEntries();//得到节点里的名字和路径
			Iterator<Entry<Comparable, ArrayList<String>>> iterator = list.iterator();

			while (iterator.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iterator.next();
				String key = entry.getKey().toString();
				ArrayList<String> value = (ArrayList<String>) entry.getValue();
				listmap.put(key, value); //文件名和路径
			}
			head = head.getNext();
		}
		return listmap;
	}
	
	
	
	//使用Junit4测试
//	@Test
//	public void test()
//	{
//////测试获取盘符的结果
////		File[] roots = File.listRoots();  
////		System.out.println(roots.toString());  //[Ljava.io.File;@5e8c92f4
////		System.out.println(roots[0].getPath().toString());  //C:\
////		System.out.println(roots[2].toString());  			//E:\
////
//////测试生成 得到总树查找文件
/////*		BplusTree Test_treeindex =  new GenerateBtree().generateBtree("Yes");
////		
////		System.out.println(Test_treeindex.getByVague("filenema",Test_treeindex));
////		
////		//测试生成 得到单磁盘树查找文件
////		BplusTree Test_treedisk = new GenerateBtree().conditionTree("C:\\\\", "Yes");
////		System.out.println(Test_treedisk.getByVague("filenema",Test_treedisk));
////*/		
////		
//////测试读取已存在索引节点实体
////		FileList fileList = new FileList();  //递归得到文件名+路径名
////		Map<String, ArrayList<String>> testNode = new HashMap<String, ArrayList<String>>();  //节点  文件名+地址 键值对
////		testNode = fileList.readFileIndex("E:\\fileIndex\\Eindex\\indexTest");	  //读取的是键值对形式
////		
//////测试保存索引
////		fileList.saveFileIndex("E:\\fileIndex\\Index\\", testNode);//磁盘路径名+index  某磁盘名和其中文件路径的map
////				
////		
//////搭建临时测试树
////		BplusTree inserttestTree = new BplusTree(10);
////		
////		for(Map.Entry<String, ArrayList<String>> entry : testNode.entrySet())  //这个关系就是Map.Entry类型
////		{
////			System.out.println(entry.getKey()+"\n"+entry.getValue()+"\n");  //Node存的是文件名+路径		
////			//将索引读出插入测试		
////			inserttestTree.insertOrUpdate(entry.getKey(), entry.getValue());	//插入从而构建出树						
////		}
////		
////		
//////从树中得到节点  文件名+地址键值对
////		Map<String, ArrayList<String>> treemaptest = GenerateBtree.getMap(inserttestTree); //从磁盘路径-树   得到文件名和路径的map键值对
////		System.out.println("如下是插入后的树内容输出");	
////		for(Map.Entry<String, ArrayList<String>> entrytreeins : treemaptest.entrySet())  //这个关系就是Map.Entry类型
////		{
////			System.out.println("树节点： "+entrytreeins.getKey()+"\n"+entrytreeins.getValue()+"\n");  //Node存的是文件名+路径
////		}
////		
////		
//////测试指定树关键字模糊搜索
////		System.out.println("按照指定树模糊查找");
////		List<String> resultPath = inserttestTree.getByVague("新建", inserttestTree);
////		System.out.println(resultPath);
////		
////		
//////测试指定树类型查找
////		System.out.println("按照指定树类型查找");
////		resultPath = inserttestTree.getByType("jpg", inserttestTree);
////		System.out.println(resultPath);				
////	
////	
//	
////测试compable接口类    键值对形式可以直接用compable 
////Arrays.asList()把数组转换成集合用
//		ArrayList<String> testArr= new ArrayList<String>();
//		testArr.add("fskj");
//		testArr.add("fafd");
//		testArr.add("fdfj");
//	
//		Map<String, ArrayList<String>> testmap= new HashMap<String, ArrayList<String>>();
//		
//		testmap.put("AHCD", testArr);
//		testmap.put("ASCD", testArr);
//		testmap.put("AGBC", testArr);
//		//Set转List
//		List<Entry<Comparable, ArrayList<String>>> list = new ArrayList(testmap.entrySet());//得到节点里的名字和路径
//		Iterator<Entry<Comparable, ArrayList<String>>> iterator = list.iterator();
//
//		while (iterator.hasNext()) 
//		{
//			Map.Entry entry = iterator.next();
//			String key = entry.getKey().toString();
//			ArrayList<String> value = (ArrayList<String>) entry.getValue();
//			System.out.println(key + value);
//		}
//		//compable强制排序 ok		
//	}
	
	
	
}






