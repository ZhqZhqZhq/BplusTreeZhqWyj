package BTreeIndex.utils;

import java.io.File;
import java.util.ArrayList;

import BTreeIndex.BplusTree;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyAdapter;
import net.contentobjects.jnotify.JNotifyException;

//Jnotify文件监视
/**
 * Jnotify监控程序 采用多线程分别监控每个监控
 * 
 */
public class JNotifyMonitoring extends JNotifyAdapter implements Runnable {

	/** 被监视的目录 */
	// String path = REQUEST_BASE_PATH;
	/** 关注目录的事件 */
	int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
	/** 是否监视子目录*/
	boolean watchSubtree = true;
	/** 监听程序Id */
	public int watchID;
	
	/** 监控的目录 */
	private String jnotifyPath;
	 BplusTree treedisk;  //磁盘树
	 BplusTree treeindex; //全局树
	// private static Set<Thread> threadlis = ThreadCount.threadcount();

	public String getJnotifyPath() {
		return jnotifyPath;
	}
	/**
	 * 容器启动时启动监视程序 ，并实例化全局树
	 * 
	 */
	public JNotifyMonitoring(String path, BplusTree treeindex, BplusTree treedisk) 
	{					//          盘路径                                                 整体树                                         单个磁盘树
		this.jnotifyPath = path;
		this.treedisk = treedisk;  //磁盘树
		this.treeindex = treeindex;  //全局树
	}

	public void beginWatch() 
	{
	
		/** 添加到监视队列中 */
		try {
			// bplusTree = GenerateBtree.generateBtree();
			// 这里用的是JNotify的addWatch
			this.watchID = JNotify.addWatch(jnotifyPath, mask, watchSubtree, this);  //将被监听程序放入 
			System.err.println("-----------------------"+jnotifyPath.substring(0, 2) + " jNotify 监控 ------启动成功-----------");
		} catch (JNotifyException e) 
		{
			e.printStackTrace();
		}
		// 死循环，线程一直执行，休眠一分钟后继续执行，主要是为了让主线程一直执行
		// 休眠时间和监测文件发生的效率无关（就是说不是监视目录文件改变一分钟后才监测到，监测几乎是实时的，调用本地系统库）
		while (true) 
		{
			try {
				Thread.sleep(60000);
			} 
			catch (InterruptedException e) 
			{
			
			}
		}
	}

	
	
	
	
	/**
	 * 结束监听
	 */
	public void finishWatch() {
		try {
			boolean flag = JNotify.removeWatch(watchID);
			if (flag) 
			{
				System.err.println(jnotifyPath.substring(0, 2) + "jnotify ----------已停止监听-----------");
			}

		} catch (JNotifyException e) {
			e.printStackTrace();
		}

	}

	
	
//新建更新索引
	public void fileCreatedIndex(String rootPath, String name)   //文件名是很长一串   获取出具体文件名
	{	
//		System.out.println(name);
		File createFile = new File(rootPath + "\\" + name);     //得到文件名同名文件					
		String createFilePath = createFile.toString();
		
		ArrayList<String> treedisklist = new ArrayList<String>();
		ArrayList<String> treeindexlist = new ArrayList<String>();
		
		// 从磁盘树获得与新建文件的文件名相同的list集合     //根目录为CDEG 因此文件名是很长一串   获取出具体文件名                                  
		treedisklist = (ArrayList<String>) treedisk.get(getFileName(name)); //根据文件名得到同名文件地址-磁盘
		// 从全局树获得与新建文件的文件名相同的list集合
		treeindexlist = (ArrayList<String>) treeindex.get(getFileName(name));//得到同名文件地址-全局树	

		// 新建的文件名以前从来没有出现在磁盘树的key中 没有元素  size=0
		if (0 == treedisklist.size())    //原来的实体中没有这个地址即代表没有这个文件
		{
			treedisklist.add(createFilePath);   //将新建文件的地址加入到所查询的磁盘地址中
			treedisk.insertOrUpdate(getFileName(name), treedisklist);  //将此地址的新文件和路径实体插入到树中
		} 
		else 
		{
			// 重命名的文件名出现在磁盘树的key中
			treedisklist.add(createFilePath);  //重名新文件地址加入到文件地址中
		}
		//System.out.println("================================"+treedisklist);
		
		// 新建的文件名以前从来没有出现在全局树的key中
		if (0 == treeindexlist.size()) 
		{
			treeindexlist.add(createFilePath);
			treeindex.insertOrUpdate(getFileName(name), treeindexlist);
		} 
		else 
		{
			// 重命名的文件名出现在全局树的key中
			treeindexlist.add(createFilePath);
		}

//动态更新--插入文件索引完毕
	}
	
	
//重命名更新索引
//对象 引用改变实参 所以不用再添加即可改变值
	public void fileRenameIndex(String rootPath, String oldName, String newName)   //文件名是很长一串   获取出具体文件名
	{
		// 获得重命名前后的文件
		File newFile = new File(rootPath + "\\" + newName);
		File oldFile = new File(rootPath + "\\" + oldName);
		// 获得重命名前后的路径名
		String oldFilePath = oldFile.toString();
		String newFilePath = newFile.toString();

		// 获取与重命名之前的文件名相同的list集合   遍历节点
		ArrayList<String> treediskOldNamelist = (ArrayList<String>) treedisk.get(getFileName(oldName));		//获得地址
		ArrayList<String> treeindexOldNamelist = (ArrayList<String>) treeindex.get(getFileName(oldName));	//获得地址
		// 获取与重命名之后的文件名相同的list集合
		ArrayList<String> treediskNewNamelist = (ArrayList<String>) treedisk.get(getFileName(newName));    //获得地址
		ArrayList<String> treeindexNewNamelist = (ArrayList<String>) treeindex.get(getFileName(newName));	//获得地址

		//地址列表中移除旧地址
		treediskOldNamelist.remove(oldFilePath);   
		treeindexOldNamelist.remove(oldFilePath);

		// 重命名的文件名不在磁盘key中
		if (treediskNewNamelist == null) 
		{
			treediskNewNamelist = new ArrayList<String>();
			treediskNewNamelist.add(newFilePath);
			treedisk.insertOrUpdate(getFileName(newName), treediskNewNamelist);
		} 
		else //重命名的文件名出现在磁盘树的key中
		{			
			treediskNewNamelist.add(newFilePath);
		}
		
		// 重命名的文件名以前从来没有出现在全局树的key中
		if (treeindexNewNamelist == null) 
		{
			treeindexNewNamelist = new ArrayList<String>();
			treeindexNewNamelist.add(newFilePath);
			treeindex.insertOrUpdate(getFileName(newName), treeindexNewNamelist);
		} 
		else // 重命名的文件名出现在磁盘树的key中
		{	
			treediskNewNamelist.add(newFilePath);
		}
	}
	
	
//删除更新索引  无级联删除
	public void fileDeleteIndex(String rootPath, String name)   //文件名是很长一串   获取出具体文件名
	{
		//删除待删除的地址
		File deleteFile = new File(rootPath + "\\" + name);  //要删除的文件名+地址		
		String deletedPath = deleteFile.toString();  //该文件名的字符串
		
		// 从磁盘树获取与删除的文件名相同的list集合
		ArrayList<String> treediskDeletedlist = (ArrayList<String>) treedisk.get(getFileName(name));
		// 从全局树获取与删除的文件名相同的list集合
		ArrayList<String> treeindexDeletedlist = (ArrayList<String>) treeindex.get(getFileName(name));
		
		treediskDeletedlist.remove(deletedPath);
		treeindexDeletedlist.remove(deletedPath);	

	}
	
	
	
	

	//复写文件增删改重命名方法
	/**
	 * 当监听目录下一旦有新的文件被创建，则即触发该事件
	 * @param wd
	 *            监听线程id
	 * @param rootPath
	 *            监听目录
	 * @param name
	 *            文件名称
	 */
	public void fileCreated(int wd, String rootPath, String name)   //文件名是很长一串   获取出具体文件名
	{
		System.err.println(jnotifyPath + "fileCreated, the created file path is " + rootPath + "\\" + name);//文件创建

		String[] nameSplit = name.split("\\\\");  //split中要为正则表达式  转义
		String nameCreat = nameSplit[nameSplit.length-1];  //显示出具体的文件名
		System.out.println("---文件创建, 新建文件盘符及名字为： "+ rootPath.substring(0, 3) + " " +nameCreat);//文件创建
		
		fileCreatedIndex(rootPath, name); //增加文件提示索引更新
		System.out.println("---更新创建文件 " + nameCreat +" 索引成功");
	}

	public void fileRenamed(int wd, String rootPath, String oldName, String newName) 
	{
		System.err.println(jnotifyPath + "fileReNamed, the old file path is " +rootPath + "\\" + oldName+ ", and the new file path is " + rootPath + "\\" + newName);
		
		fileRenameIndex(rootPath, oldName, newName); //增加文件提示索引更新
		
		String[] oldNameSplit = oldName.split("\\\\");  //split中要为正则表达式  转义
		String[] newNameSplit = newName.split("\\\\");  //split中要为正则表达式  转义
		oldName = oldNameSplit[oldNameSplit.length-1];  //显示出具体的文件名
		newName = newNameSplit[newNameSplit.length-1];  //显示出具体的文件名
		System.out.println("---文件重命名, 重命名文件盘符及文件名变化为： "+ rootPath.substring(0, 3) + oldName +" ——> "+ newName);//文件创建
		
//			fileRenameIndex(rootPath, oldName, newName); //增加文件提示索引更新
		System.out.println("---更新重命名文件 " + oldName+" ——> "+newName +" 索引成功");
		
	}
	
	

	//删除
	public void fileDeleted(int wd, String rootPath, String name) 
	{	
		System.err.println(jnotifyPath + "fileDeleted , the deleted file path is " + rootPath + "\\" + name);

		fileDeleteIndex(rootPath, name);
		
		String[] nameSplit = name.split("\\\\");  //split中要为正则表达式  转义
		name = nameSplit[nameSplit.length-1];  //显示出具体的文件名
		System.out.println("---文件删除, 删除文件盘符及文件名为： "+ rootPath.substring(0, 3) + name);//文件创建

		System.out.println("---更新删除文件 " + name + " 索引成功");		
		
	}

	
	//根据文件路径获取文件名
	private String getFileName(String currentPathName) 
	{
		int i = currentPathName.lastIndexOf("\\");
		StringBuffer sb = new StringBuffer();
		for (int j = i + 1; j < currentPathName.length(); j++) 
		{
			sb.append(currentPathName.charAt(j));
		}
		return sb.toString();
	}

//线程run方法
	public void run() 
	{
		beginWatch();
	}
	
	
	
	
	
////测试监控器用例
//	
//	@Test
//	public void test() 
//	{
//		BplusTree test1 = new BplusTree(10);
//		BplusTree test2 = new BplusTree(10);
//		
//		Map<String, ArrayList<String>> testNode = new HashMap<String, ArrayList<String>>();  //节点  文件名+地址 键值对
//	
//	for(Map.Entry<String, ArrayList<String>> entry : testNode.entrySet())  //这个关系就是Map.Entry类型
//	{
//		System.out.println(entry.getKey()+"\n"+entry.getValue()+"\n");  //Node存的是文件名+路径		
//		//将索引读出插入测试		
//		test1.insertOrUpdate(entry.getKey(), entry.getValue());	//插入从而构建出树
//		test2.insertOrUpdate(entry.getKey(), entry.getValue());	//插入从而构建出树
//	}
//		
//		JNotifyMonitoring JNotifytest= new JNotifyMonitoring("E:\\fileIndex\\Test", test1 , test2);
//		
//		JNotifytest.beginWatch();
//	}
//
//	

	
}







