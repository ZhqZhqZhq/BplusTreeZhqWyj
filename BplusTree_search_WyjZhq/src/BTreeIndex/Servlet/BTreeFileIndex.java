package BTreeIndex.Servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BTreeIndex.BplusTree;
import BTreeIndex.utils.JNotifyMonitoring;
import BTreeIndex.utils.FileList;
import BTreeIndex.utils.GenerateBtree;
import net.sf.json.JSONArray;


/**
 * 首页跳转主Servlet所在类  提供index.html搜索的各种action支持 
 * 
 */
public class BTreeFileIndex extends HttpServlet {
	private static final long serialVersionUID = 1L;  //用来表明类的不同版本间的兼容性

//	System.out.println("---开始生成全局磁盘树...");
	static BplusTree treeindex = new GenerateBtree().generateBtree("No");  //生成全局树 索引存放 所有磁盘的树
//	System.out.println("---全局磁盘树生成完毕...");
	
	static Map<String, BplusTree> treemap = new HashMap<>();  //存放所有磁盘树
	static BplusTree[] tree;
	static {

		//创建线程池，将拥有runnable的run方法的任务提交给线程池，execute执行
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  //创建线程池
		
		File[] files = File.listRoots();  //该方法返回指示可用的文件系统的盘符路径数组 C： D： E： G：
		JNotifyMonitoring[] threadGroup = new JNotifyMonitoring[files.length];  //建立4个监视程序
		tree = new BplusTree[files.length];    //创建盘符 4棵树
		
		for (int i = 0; i < files.length; i++) 
		{//将每棵树及总树 读取出放入监控   传的是对象即引用
			System.out.println("---开始生成第" + (i+1) + "棵磁盘树,共" + files.length + "棵磁盘树");
			tree[i] = new GenerateBtree().conditionTree(files[i].getPath(), "No"); //生成4个单个磁盘树   file是磁盘数组
			treemap.put(files[i].getPath(), tree[i]);     //4棵数都存入一个Map中  按照 磁盘路径--树对应存储
			threadGroup[i] = new JNotifyMonitoring(files[i].getPath() + File.separator, treeindex, tree[i]);   //File.separator 跨平台分隔符
			System.out.println("---已将第"+ (i+1) +"个磁盘加入监控，共" + files.length + "个磁盘");
			cachedThreadPool.execute(threadGroup[i]);          //线程池中提交任务  运行run方法  run方法依次开动了4个盘符的监控程序
		}
	}

    
	/**
	 * 首页跳转主Servlet 获取各种首页发送的属性参数
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		request.setCharacterEncoding("utf-8");  //使用utf解码
		response.setContentType("text/html;charset=utf-8");//发往前台用utf-8
			
		String filename = new String(request.getParameter("content"));  
		String filetype = new String(request.getParameter("filetype"));
		String reindex =request.getParameter("reindex"); //是否重建索引
		String root = request.getParameter("root");  //磁盘根目录
//方式选择	String keyWords = request.getParameter("content");  //关键字查询
		String way = request.getParameter("way");  //类型查询或关键字查询
		String save = request.getParameter("save");  //保存索引
				
		for (String r : treemap.keySet())  //获取全部的Key值   即 key = 磁盘路径
		{
			if (r.indexOf(root) > -1) 
			{
				root = r;  //找到对应磁盘路径
			}
		}
//		System.out.println("------------------"+root);  // E:\
		
		List<String> resultPath = new ArrayList<>();  //返回的查询结果路径  封装为Json发往前台

		//测试获取的值
//		System.out.println(filetype);
//		System.out.println(filename);
		
		//搜索名称不为空
		if (!filename.isEmpty()) 
		{
			//关键字查询
			if ("key".equals(way)) 
			{
				// 关键字匹配+重建索引
				if ("Yes".equals(reindex)) 
				{
					//关键字匹配  重建索引  盘符
					if (root.equals("all")) 
					{
						//生成全局树索引
						treeindex = new GenerateBtree().generateBtree(reindex);  
						resultPath = treeindex.getByVague(filename, treeindex);					
					} 
					else 
					{
						//重建单磁盘索引，关键字匹配
						BplusTree entryvlue = new GenerateBtree().conditionTree(root, reindex);  
						resultPath = entryvlue.getByVague(filename, entryvlue);
						
					}
				} 
				else 
				{  //关键字匹配  不重建索引  盘符
					if (root.equals("all")) 
					{
						//无盘符   不重建索引  关键字匹配
						resultPath = treeindex.getByVague(filename, treeindex);						
					} 
					else 
					{   
						BplusTree entryvlue = treemap.get(root);    //根据键路径名获得树实体
						resultPath = entryvlue.getByVague(filename, entryvlue);
					}
				}
			} 
			else  //不进行关键字查询  则按照类型查询
			{
				if ("type".equals(way))  
				{
					//不关键字查询  重建索引
					if ("Yes".equals(reindex))  
					{
						//不关键字查询  重建索引  有盘符
						if (root.equals("all")) 
						{	
							treeindex = new GenerateBtree().generateBtree(reindex);
							resultPath = treeindex.getByType(filename, treeindex);
							
						} 
						else  //不关键字   重建  无盘符 按照类型查询
						{
							//重建单个磁盘
							BplusTree entryvlue = new GenerateBtree().conditionTree(root, reindex);  
							resultPath = entryvlue.getByType(filename, entryvlue);		
						}
						// response.getWriter().write(JSONArray.fromObject(resultPath).toString());
					}
					else
					{
						if (root.equals("all")) 
						{	
							resultPath = treeindex.getByType(filename, treeindex);							
						} 
						else 
						{
							BplusTree entryvlue = treemap.get(root);
							resultPath = entryvlue.getByType(filename, entryvlue);
						}
					}
				} 
				else
				{ //不关键字不类型 提示至少选择一种查询
					response.getWriter().write("请选择   \"关键字\"或者 \"类型查询\" 进行查找 ");
				}
			}
		}
	
		//搜索名为空 按照影视 图片 音乐 文档 搜索
		if (!filetype.isEmpty()) //类型查询   
		{
			if (root.equals("all")) 
			{   
				try {
					resultPath = treeindex.getByButton(filetype, treeindex);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			else 
			{
				//找到对应磁盘
				BplusTree entryvlue = treemap.get(root);
				try {
					resultPath = entryvlue.getByButton(filetype, entryvlue);
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}	
		}
	
		
		//保存索引默认在某处	 前台发送时，自动置空，因此没有逻辑问题
		if (save != null) 
		{
			//保存单磁盘树索引
			for (Entry<String, BplusTree> entry : treemap.entrySet()) //得到所有文件名和树   entry是单个磁盘路径名和树
			{
				System.out.println("---正在保存 "+entry.getKey().substring(0,2)+" 索引......");
				FileList fileList = new FileList();                   //得到树中的所有文件名和路径
				Map<String, ArrayList<String>> fileMap = GenerateBtree.getMap(entry.getValue()); //从磁盘路径-树   得到文件名和路径的map键值对
				fileList.saveFileIndex("E:\\fileIndex\\"+entry.getKey().substring(0,1) + "index", fileMap);   //把4棵树的文件名和路径map放入filesist中
			}  
			
			//保存全局树
			Map<String, ArrayList<String>> fileAllMap = GenerateBtree.getMap(treeindex); //从磁盘路径-树   得到文件名和路径的map键值对
			FileList fileList = new FileList();  //得到树中的所有文件名和路径
			System.out.println("---正在保存 全局 索引......");
			fileList.saveFileIndex("E:\\fileIndex\\overAllIndex", fileAllMap);  //磁盘路径名+index  某磁盘名和其中文件路径的map
			resultPath.add("OK");
		}
		
	
		response.getWriter().write(JSONArray.fromObject(resultPath).toString());

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
