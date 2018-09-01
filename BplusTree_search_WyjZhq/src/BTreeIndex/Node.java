package BTreeIndex;

import java.util.AbstractMap.SimpleEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import BTreeIndex.utils.ReadButtonType;

/**
 * BplusTree节点操作
 */
public class Node {

	/** 是否为叶子节点 */
	protected boolean isLeaf;

	/** 是否为根节点 */
	protected boolean isRoot;

	/** 父节点 */
	protected Node parent;

	/** 叶节点的前节点 */
	protected Node previous;

	/** 叶节点的后节点 */
	protected Node next;

	/** 节点的关键字 */
	protected List<Entry<Comparable, ArrayList<String>>> entries;

	/** 子节点 */
	protected List<Node> children;

	public Node(boolean isLeaf) {
		this.isLeaf = isLeaf;
		entries = new ArrayList<Entry<Comparable, ArrayList<String>>>();  //节点，存数据键值对

		if (!isLeaf) {
			children = new ArrayList<Node>();
		}
	}

	public Node(boolean isLeaf, boolean isRoot) {
		this(isLeaf);
		this.isRoot = isRoot;
	}


/**
 * BplusTree节点操作
 * 文件名查询 获得文件路径
 */
	public List<String> get(Comparable key) 
	{  	

		// 如果是叶子节点
		if (isLeaf) 
		{
			for (Entry<Comparable, ArrayList<String>> entry : entries) 
			{  //遍历叶子节点找到文档的路径
				if (entry.getKey().compareTo(key) == 0) 
				{
					// 返回找到的对象
					return entry.getValue();
				}
			}
			// 未找到所要查询的对象
			// 如果不是叶子节点
		} 
		else 
		{
			// 如果key小于等于节点最左边的key，沿第一个子节点继续搜索
			if (key.compareTo(entries.get(0).getKey()) <= 0) 
			{
				return children.get(0).get(key);     //取得节点中第1个元素进行get(key)
				// 如果key大于节点最右边的key，沿最后一个子节点继续搜索
			} 
			else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) 
			{
				return children.get(children.size() - 1).get(key);
				// 否则沿比key大的前一个子节点继续搜索
			} 
			else 
			{
				for (int i = 0; i < entries.size(); i++) 
				{  		
					if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) 
					{
						return children.get(i).get(key);			//介于之间，非叶子节点找到文件路径
					}
				}
			}
		}
		return null;
	}

	/**
	 * BplusTree节点操作
	 * 忽略大小写
	 * key：关键字
	 * tree： 查找所在树
	 */
	public List<String> getByVague(Comparable key, BplusTree tree) 
	{									 //查找的文件名	   选中的磁盘树			//■■■■■■■■模糊匹配■■■■从树起始头结点getHead
		Node head = tree.getHead(); 

		List<String> temppath = new ArrayList<>();//存放临时路径
		while (head != null) 
		{ 
			List<Entry<Comparable, ArrayList<String>>> entries = head.getEntries();//■■■■■■■■叶子节点，一组键值对■■■■Map■■■■
			for (Entry<Comparable, ArrayList<String>> entry : entries) 
			{ 															 //■■■comparable接口     entries2是键值对数组List
				String entrykey = entry.getKey().toString().toUpperCase();   //磁盘的当前文件名
				
				//统一为大写 从而忽略大小写处理
				if (entrykey.indexOf(key.toString().toUpperCase())>-1)//含有该关键字的即所求          //找到了所有含有该关键字的//根据文件名查找   说明所找的在路径中
				{
					temppath.addAll(entry.getValue());  //添加文件路径   
//					System.out.println("这里是模糊的结果"+temppath);
				}
			}
			head = head.getNext();
		}
		return temppath;

	}
	
	
	/**
	 * BplusTree节点操作
	 * 根据类型查询
	 * 忽略大小写
	 * key：文件类型 
	 * tree： 查找所在树
	 */
	public List<String> getByType(Comparable key, BplusTree tree) 
	{
		Node head = tree.getHead();
		// TODO Auto-generated method stub
		List<String> temppath = new ArrayList<>();
		while (head != null)
		{
			List<Entry<Comparable, ArrayList<String>>> entries = head.getEntries();
			for (Entry<Comparable, ArrayList<String>> entry : entries) 
			{
				// getKeyByType(key, temppath, entry);
				if (entry.getKey().toString().contains(".")) 
				{//将文件名的后缀与key的格式匹配          
				String[] keytype = entry.getKey().toString().split("\\."); //文件名获取到格式结尾	\\. 为.
				//if (keytype[keytype.length - 1].toUpperCase().indexOf(key.toString().toUpperCase()) >= 0) 
					if ((keytype[keytype.length - 1].toUpperCase()).equals(key.toString().toUpperCase())) 
					{
					// 返回找到的对象
					temppath.addAll(entry.getValue());
					}
				}
			}
			head = head.getNext();
		}
		if (temppath.size() != 0) {
			return temppath;
		} else {
			return null;
		}
	}

	/**
	 * BplusTree节点操作
	 * 根据按钮文件类型找  ReadButtonType是读取按钮类型格式的文件
	 * 忽略大小写
	 * key：文件类型 ：视频音乐图片文档
	 * tree： 查找所在树
	 */

	public List<String> getByButton(Comparable key, BplusTree tree) throws Exception 
	{
		String buttonType = key.toString();  //获取按键类型字符串
		List<String> temppath = new ArrayList<>();//存放结果的路径
		
		//去到按钮类型的地址查文件索引
		String fileContext = new ReadButtonType().ReadFile("E:\\fileIndex\\types\\" + buttonType + ".txt");
		List<String> listContext = Arrays.asList(fileContext.split(","));
		
		Node head = tree.getHead();
		while (head != null) 
		{
			List<Entry<Comparable, ArrayList<String>>> entries = head.getEntries();
			for (Entry<Comparable, ArrayList<String>> entry : entries) 
			{//内含格式
				String[] keytype = entry.getKey().toString().split("\\.");		
				if (listContext.contains(keytype[keytype.length - 1]))  //格式列表包含有文件后缀名   
				{
					temppath.addAll(entry.getValue());
				}			
			}
			//依次遍历查询
			head = head.getNext();
		}
		if (temppath.size() != 0) 
		{
			return temppath;
		} 
		else 
		{
			return null;
		}
	}

	
	/**
	 * BplusTree节点操作
	 * 将文件名和地址的插入到节点  entry
	 * 忽略大小写
	 * key：文件名称
	 * arrayList：路径list
	 * tree： 插入所在树
	 */
	public void insertOrUpdate(Comparable key, ArrayList<String> arrayList, BplusTree tree) {
		// 如果是叶子节点
		if (isLeaf) {
			// 不需要分裂，直接插入或更新
			if (contains(key) || entries.size() < tree.getOrder()) 
			{//每个节点的关键字数要小于阶数
				insertOrUpdate(key, arrayList);   //重载
				if (parent != null) {
					// 更新父节点
					parent.updateInsert(tree);
				}

				// 需要分裂
			} else {
				// 分裂成左右两个节点
				Node left = new Node(true);
				Node right = new Node(true);
				// 设置链接
				if (previous != null) {
					previous.setNext(left);
					left.setPrevious(previous);
				}
				if (next != null) {
					next.setPrevious(right);
					right.setNext(next);
				}
				if (previous == null) {
					tree.setHead(left);
				}

				left.setNext(right);
				right.setPrevious(left);
				previous = null;
				next = null;

				// 左右两个节点关键字长度
				int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
				int rightSize = (tree.getOrder() + 1) / 2;
				// 复制原节点关键字到分裂出来的新节点
				insertOrUpdate(key, arrayList);
				for (int i = 0; i < leftSize; i++) {
					left.getEntries().add(entries.get(i));
				}
				for (int i = 0; i < rightSize; i++) {
					right.getEntries().add(entries.get(leftSize + i));
				}

				// 如果不是根节点
				if (parent != null) {
					// 调整父子节点关系
					int index = parent.getChildren().indexOf(this);
					parent.getChildren().remove(this);
					left.setParent(parent);
					right.setParent(parent);
					parent.getChildren().add(index, left);
					parent.getChildren().add(index + 1, right);
					setEntries(null);
					setChildren(null);

					// 父节点插入或更新关键字
					parent.updateInsert(tree);
					setParent(null);
					// 如果是根节点
				} else {
					isRoot = false;
					Node parent = new Node(false, true);
					tree.setRoot(parent);
					left.setParent(parent);
					right.setParent(parent);
					parent.getChildren().add(left);
					parent.getChildren().add(right);
					setEntries(null);
					setChildren(null);

					// 更新根节点
					parent.updateInsert(tree);
				}

			}

			// 如果不是叶子节点
		} else {
			// 如果key小于等于节点最左边的key，沿第一个子节点继续搜索
			if (key.compareTo(entries.get(0).getKey()) <= 0) {
				children.get(0).insertOrUpdate(key, arrayList, tree);
				// 如果key大于节点最右边的key，沿最后一个子节点继续搜索
			} else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
				children.get(children.size() - 1).insertOrUpdate(key, arrayList, tree);
				// 否则沿比key大的前一个子节点继续搜索
			} else {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) {
						children.get(i).insertOrUpdate(key, arrayList, tree);
						break;
					}
				}
			}
		}
	}

	/** 
	 * 插入节点后中间节点的更新
	 */
	protected void updateInsert(BplusTree tree) {

		validate(this, tree);

		// 如果子节点数超出阶数，则需要分裂该节点
		if (children.size() > tree.getOrder()) {
			// 分裂成左右两个节点
			Node left = new Node(false);
			Node right = new Node(false);
			// 左右两个节点关键字长度
			int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
			int rightSize = (tree.getOrder() + 1) / 2;
			// 复制子节点到分裂出来的新节点，并更新关键字
			for (int i = 0; i < leftSize; i++) {
				left.getChildren().add(children.get(i));
				left.getEntries().add(new SimpleEntry(children.get(i).getEntries().get(0).getKey(), null));
				children.get(i).setParent(left);
			}
			for (int i = 0; i < rightSize; i++) {
				right.getChildren().add(children.get(leftSize + i));
				right.getEntries().add(new SimpleEntry(children.get(leftSize + i).getEntries().get(0).getKey(), null));
				children.get(leftSize + i).setParent(right);
			}

			// 如果不是根节点
			if (parent != null) {
				// 调整父子节点关系
				int index = parent.getChildren().indexOf(this);
				parent.getChildren().remove(this);
				left.setParent(parent);
				right.setParent(parent);
				parent.getChildren().add(index, left);
				parent.getChildren().add(index + 1, right);
				setEntries(null);
				setChildren(null);

				// 父节点更新关键字
				parent.updateInsert(tree);
				setParent(null);
				// 如果是根节点
			} else {
				isRoot = false;
				Node parent = new Node(false, true);
				tree.setRoot(parent);
				left.setParent(parent);
				right.setParent(parent);
				parent.getChildren().add(left);
				parent.getChildren().add(right);
				setEntries(null);
				setChildren(null);

				// 更新根节点
				parent.updateInsert(tree);
			}
		}
	}

	/** 
	 * 调整节点关键字 
	 * */
	protected static void validate(Node node, BplusTree tree) {

		// 如果关键字个数与子节点个数相同
		if (node.getEntries().size() == node.getChildren().size()) {
			for (int i = 0; i < node.getEntries().size(); i++) {
				Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
				if (node.getEntries().get(i).getKey().compareTo(key) != 0) {
					node.getEntries().remove(i);
					node.getEntries().add(i, new SimpleEntry(key, null));
					if (!node.isRoot()) {
						validate(node.getParent(), tree);
					}
				}
			}
			// 如果子节点数不等于关键字个数但仍大于M / 2并且小于M，并且大于2
		} else if (node.isRoot() && node.getChildren().size() >= 2 || node.getChildren().size() >= tree.getOrder() / 2
				&& node.getChildren().size() <= tree.getOrder() && node.getChildren().size() >= 2) {
			node.getEntries().clear();
			for (int i = 0; i < node.getChildren().size(); i++) {
				Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
				node.getEntries().add(new SimpleEntry(key, null));
				if (!node.isRoot()) {
					validate(node.getParent(), tree);
				}
			}
		}
	}

	/**
	 *  删除节点后中间节点的更新 
	 */
	protected void updateRemove(BplusTree tree) {

		validate(this, tree);

		// 如果子节点数小于M / 2或者小于2，则需要合并节点
		if (children.size() < tree.getOrder() / 2 || children.size() < 2) {
			if (isRoot) {
				// 如果是根节点并且子节点数大于等于2，OK
				if (children.size() >= 2) {
					return;
					// 否则与子节点合并
				} else {
					Node root = children.get(0);
					tree.setRoot(root);
					root.setParent(null);
					root.setRoot(true);
					setEntries(null);
					setChildren(null);
				}
			} else {
				// 计算前后节点
				int currIdx = parent.getChildren().indexOf(this);
				int prevIdx = currIdx - 1;
				int nextIdx = currIdx + 1;
				Node previous = null, next = null;
				if (prevIdx >= 0) {
					previous = parent.getChildren().get(prevIdx);
				}
				if (nextIdx < parent.getChildren().size()) {
					next = parent.getChildren().get(nextIdx);
				}

				// 如果前节点子节点数大于M / 2并且大于2，则从其处借补
				if (previous != null && previous.getChildren().size() > tree.getOrder() / 2
						&& previous.getChildren().size() > 2) {
					// 前叶子节点末尾节点添加到首位
					int idx = previous.getChildren().size() - 1;
					Node borrow = previous.getChildren().get(idx);
					previous.getChildren().remove(idx);
					borrow.setParent(this);
					children.add(0, borrow);
					validate(previous, tree);
					validate(this, tree);
					parent.updateRemove(tree);

					// 如果后节点子节点数大于M / 2并且大于2，则从其处借补
				} else if (next != null && next.getChildren().size() > tree.getOrder() / 2
						&& next.getChildren().size() > 2) {
					// 后叶子节点首位添加到末尾
					Node borrow = next.getChildren().get(0);
					next.getChildren().remove(0);
					borrow.setParent(this);
					children.add(borrow);
					validate(next, tree);
					validate(this, tree);
					parent.updateRemove(tree);

					// 否则需要合并节点
				} else {
					// 同前面节点合并
					if (previous != null && (previous.getChildren().size() <= tree.getOrder() / 2
							|| previous.getChildren().size() <= 2)) {

						for (int i = previous.getChildren().size() - 1; i >= 0; i--) {
							Node child = previous.getChildren().get(i);
							children.add(0, child);
							child.setParent(this);
						}
						previous.setChildren(null);
						previous.setEntries(null);
						previous.setParent(null);
						parent.getChildren().remove(previous);
						validate(this, tree);
						parent.updateRemove(tree);

						// 同后面节点合并
					} else if (next != null
							&& (next.getChildren().size() <= tree.getOrder() / 2 || next.getChildren().size() <= 2)) {

						for (int i = 0; i < next.getChildren().size(); i++) {
							Node child = next.getChildren().get(i);
							children.add(child);
							child.setParent(this);
						}
						next.setChildren(null);
						next.setEntries(null);
						next.setParent(null);
						parent.getChildren().remove(next);
						validate(this, tree);
						parent.updateRemove(tree);
					}
				}
			}
		}
	}
	/**
	 * BplusTree节点操作
	 * 忽略大小写
	 * key：要移出的文件路径名
	 * tree： 移出路径所在树
	 */
	public void remove(Comparable key, BplusTree tree) {
		// 如果是叶子节点
		if (isLeaf) {

			// 如果不包含该关键字，则直接返回
			if (!contains(key)) {
				return;
			}

			// 如果既是叶子节点又是跟节点，直接删除
			if (isRoot) {
				remove(key);
			} else {
				// 如果关键字数大于M / 2，直接删除
				if (entries.size() > tree.getOrder() / 2 && entries.size() > 2) {
					remove(key);
				} else {
					// 如果自身关键字数小于M / 2，并且前节点关键字数大于M / 2，则从其处借补
					if (previous != null && previous.getEntries().size() > tree.getOrder() / 2
							&& previous.getEntries().size() > 2 && previous.getParent() == parent) {
						int size = previous.getEntries().size();
						Entry<Comparable, ArrayList<String>> entry = previous.getEntries().get(size - 1);
						previous.getEntries().remove(entry);
						// 添加到首位
						entries.add(0, entry);
						remove(key);
						// 如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补
					} else if (next != null && next.getEntries().size() > tree.getOrder() / 2
							&& next.getEntries().size() > 2 && next.getParent() == parent) {
						Entry<Comparable, ArrayList<String>> entry = next.getEntries().get(0);
						next.getEntries().remove(entry);
						// 添加到末尾
						entries.add(entry);
						remove(key);
						// 否则需要合并叶子节点
					} else {
						// 同前面节点合并
						if (previous != null && (previous.getEntries().size() <= tree.getOrder() / 2
								|| previous.getEntries().size() <= 2) && previous.getParent() == parent) {
							for (int i = previous.getEntries().size() - 1; i >= 0; i--) {
								// 从末尾开始添加到首位
								entries.add(0, previous.getEntries().get(i));
							}
							remove(key);
							previous.setParent(null);
							previous.setEntries(null);
							parent.getChildren().remove(previous);
							// 更新链表
							if (previous.getPrevious() != null) {
								Node temp = previous;
								temp.getPrevious().setNext(this);
								previous = temp.getPrevious();
								temp.setPrevious(null);
								temp.setNext(null);
							} else {
								tree.setHead(this);
								previous.setNext(null);
								previous = null;
							}
							// 同后面节点合并
						} else if (next != null
								&& (next.getEntries().size() <= tree.getOrder() / 2 || next.getEntries().size() <= 2)
								&& next.getParent() == parent) {
							for (int i = 0; i < next.getEntries().size(); i++) {
								// 从首位开始添加到末尾
								entries.add(next.getEntries().get(i));
							}
							remove(key);
							next.setParent(null);
							next.setEntries(null);
							parent.getChildren().remove(next);
							// 更新链表
							if (next.getNext() != null) {
								Node temp = next;
								temp.getNext().setPrevious(this);
								next = temp.getNext();
								temp.setPrevious(null);
								temp.setNext(null);
							} else {
								next.setPrevious(null);
								next = null;
							}
						}
					}
				}
				parent.updateRemove(tree);
			}
			// 如果不是叶子节点
		} else {
			// 如果key小于等于节点最左边的key，沿第一个子节点继续搜索
			if (key.compareTo(entries.get(0).getKey()) <= 0) {
				children.get(0).remove(key, tree);
				// 如果key大于节点最右边的key，沿最后一个子节点继续搜索
			} else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
				children.get(children.size() - 1).remove(key, tree);
				// 否则沿比key大的前一个子节点继续搜索
			} else {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i + 1).getKey().compareTo(key) > 0) {
						children.get(i).remove(key, tree);
						break;
					}
				}
			}
		}
	}

	/** 判断当前节点是否包含该关键字 */
	protected boolean contains(Comparable key) {
		for (Entry<Comparable, ArrayList<String>> entry : entries) {
			if (entry.getKey().compareTo(key) == 0) {
				return true;
			}
		}
		return false;
	}

	/** 插入到当前节点的关键字中 */
	protected void insertOrUpdate(Comparable key, ArrayList<String> arrayList) {
		Entry<Comparable, ArrayList<String>> entry = new SimpleEntry<Comparable, ArrayList<String>>(key, arrayList);
		// 如果关键字列表长度为0，则直接插入
		 if (entries.size() == 0) {
			entries.add(entry);
			return;
		 }
		 // 否则遍历列表
		 for (int i = 0; i < entries.size(); i++) {
		 // 如果该关键字键值已存在，则更新
			if (entries.get(i).getKey().compareTo(key) == 0) {
				entries.get(i).setValue(arrayList);
				return;
				// 否则插入
			} else
			if (entries.get(i).getKey().compareTo(key) > 0) {
		 // 插入到链首
				if (i == 0) {
					entries.add(0, entry);
					return;
		 // 插入到中间
				} else {
					entries.add(i, entry);
					return;
				}
		 }

		 }
		// 插入到末尾
		entries.add(entries.size(), entry);
	}

	/** 删除节点 */
	protected void remove(Comparable key) {
		int index = -1;
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).getKey().compareTo(key) == 0) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			entries.remove(index);
		}
	}

	public Node getPrevious() {
		return previous;
	}

	public void setPrevious(Node previous) {
		this.previous = previous;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Entry<Comparable, ArrayList<String>>> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry<Comparable, ArrayList<String>>> entries) {
		this.entries = entries;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("isRoot: ");
		sb.append(isRoot);
		sb.append(", ");
		sb.append("isLeaf: ");
		sb.append(isLeaf);
		sb.append(", ");
		sb.append("keys: ");
		for (Entry entry : entries) {
			sb.append(entry.getKey());
			sb.append(", ");
		}
		sb.append(", ");
		return sb.toString();

	}

	public int size() {
		// TODO Auto-generated method stub

		return entries.size();

	}

	public Entry<Comparable, ArrayList<String>> entryAt(int index) {
		return entries.get(index);
	}

	public Node childAt(int index) {
		if (isLeaf())
			throw new UnsupportedOperationException("Leaf node doesn't have children.");
		return children.get(index);
	}


}

