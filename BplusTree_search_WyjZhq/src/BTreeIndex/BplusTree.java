package BTreeIndex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class BplusTree {

	/** 根节点 */
	protected Node root;

	/** 阶数，M值 */
	protected int order;

	/** 叶子节点的链表头 */
	protected Node head;

	public Node getHead() {
		return head;
	}

	public void setHead(Node head) {
		this.head = head;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<String> get(Comparable key) {
		return root.get(key);
	}

	public List<String> getByVague(Comparable key, BplusTree tree) {
		return root.getByVague(key, tree);
	}

	public List<String> getByType(Comparable key, BplusTree tree) {
		return root.getByType(key, tree);
	}

	public List<String> getByButton(Comparable key, BplusTree tree) throws Exception {
		return root.getByButton(key, tree);
	}

	public void remove(Comparable key) {
		root.remove(key, this);

	}

	public void insertOrUpdate(Comparable key, ArrayList<String> arrayList) {
		root.insertOrUpdate(key, arrayList, this);

	}

	public BplusTree(int order) 
	{
		if (order < 3) {
			System.out.print("order must be greater than 2");
			System.exit(0);
		}
		this.order = order;
		root = new Node(true, true);
		// head = root;
	}

	public void output() 
	{
		Queue<Node> queue = new LinkedList<Node>();
		queue.offer(head);
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			for (int i = 0; i < node.size(); ++i)
				System.out.print(node.entryAt(i) + " ");
			System.out.println();
			if (!node.isLeaf()) {
				for (int i = 0; i <= node.size(); ++i)
					queue.offer(node.childAt(i));
			}
		}
	}


	// private static List<String> dataList = new ArrayList<String>();
	// public static BplusTree tree = new BplusTree(3);
	// 测试
	// public static void main(String[] args) {
	// BplusTree tree = new BplusTree(3);
	// // Random random = new Random();
	// FileList test = new FileList();
	// File readIndex = new File("E:\\fileIndex");
	// Map<String, ArrayList<String>> map = new HashMap<String,
	// ArrayList<String>>();
	// String filePath = "G:" + File.separator;
	// // String filepath_temp = filePath;
	// // long current = System.currentTimeMillis();
	// if(readIndex.exists()&&readIndex.length()!=0)
	// {
	// map = test.readFileIndex("E:\\fileIndex");
	// }else
	// {
	// map = test.getFiles(filePath);
	// test.saveFileIndex("E:\\fileIndex", map);
	// try {
	// String line = System.getProperty("line.separator");
	// StringBuffer str = new StringBuffer();
	// FileWriter fw = new FileWriter("E:\\1.txt", true);
	// Set set = map.entrySet();
	// Iterator iter = set.iterator();
	// while (iter.hasNext()) {
	// Map.Entry entry = (Map.Entry) iter.next();
	// str.append(entry.getKey() + " : " +
	// entry.getValue()).append(line).append("\r\n");
	// }
	// fw.write(str.toString());
	// fw.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }for(
	// Entry<String, ArrayList<String>> entry:map.entrySet())
	// {
	// // System.out.println(entry.getValue());
	// tree.insertOrUpdate(entry.getKey(), entry.getValue());
	// // System.out.println(entry.getKey() + " ");
	// // System.out.println(entry.getValue());
	// }
	// // tree.output();
	//
	//
	// // for (int j = 0; j < 100000; j++) {
	// // for (int i = 0; i < 100; i++) {
	// // int randomNumber = random.nextInt(1000);
	// // tree.insertOrUpdate(randomNumber, randomNumber);
	//// }
	// //
	// // for (int i = 0; i < 100; i++) {
	// // int randomNumber = random.nextInt(1000);
	// // tree.remove(randomNumber);
	//// }
	//// }
	//
	// long duration = System.currentTimeMillis() - current;
	// System.out.println("time elpsed for duration: " + duration);
	// String search = "web.xml";
	// List<String> pathStringTest = new ArrayList<>();
	// pathStringTest = tree.get(search);
	// if (pathStringTest != null) {
	// for (String a : pathStringTest) {
	// System.out.println(a);
	// }
	// } else {
	// System.out.println("找不到！");
	// }
	// }

}