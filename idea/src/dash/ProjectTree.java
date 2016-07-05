package dash;

import javax.swing.JTree;
import javax.swing.tree.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.datatransfer.*;

/**
 * <p>タイトル:プロジェクトの内容を表すツリーを作成 </p>
 * <p>説明:プロジェクトの内容を表すツリーを作成 </p>
 * <p>著作権: Copyright (c) 2003</p>
 * <p>会社名:cosmos </p>
 * @author nakagawa
 * @version 1.0
 */

public class ProjectTree extends JTree {


  private Project prj = null;
  private Hashtable htBPFileInfo = null;
  private Hashtable htRsetFileInfo = null;
  public JPanel TreePanel = null;
  //public JScrollPane TreePanel = null;
  // ファイルセパレータの取得
  final String separator = System.getProperty("file.separator");

  final int DPX_FILE = 0;
  final int FOLDER = 1;
  final int DASH_FILE = 2;
  final int RSET_FILE = 3;
  final int JAVA_FILE = 4;
  final int OTHER_FILE = 5;


  /** 変更が加えられたファイル名を保持するベクター */
  /** IdeaMainFrame.java のTextChangeメソッドで、変更ファイル名が追加される　*/
  public Vector vecChangeFileName = new Vector();
  private DirectoryNode SelectedNode = null;
  private Vector vecSubFolderInfo = new Vector();

  private Vector vecUsedBPFile = new Vector();
  private Vector vecUsedRsetFile = new Vector();
  JScrollPane scrollPane = null;



  /****************************************************************************
   * コンストラクタ
   * @param projectFile プロジェクトファイル名
   * @return なし
   ****************************************************************************/
  public ProjectTree(String projectFile) {
	this.setRootVisible(true);

	FileReader f_in;
	BufferedReader b_in;
	String sLine = "";

	this.setCellRenderer(new MyCellRenderer());
	DefaultTreeModel treeModel = null;
	DirectoryNode rootTreeNode = null;
	if(projectFile == null || projectFile.equals("")){
	  //this.add(new JPanel());
	  DirectoryNodeInfo dni = new DirectoryNodeInfo("ﾌﾟﾛｼﾞｪｸﾄを作成又は開いてください...",0,"", false);
	  rootTreeNode = new DirectoryNode(dni);
	  treeModel = new DefaultTreeModel(rootTreeNode);

	  scrollPane = new JScrollPane();

	  scrollPane.getViewport().setView(this);
	  scrollPane.setPreferredSize(new Dimension(250, 100));

	  TreePanel = new JPanel(new BorderLayout());
	  TreePanel.add(scrollPane,BorderLayout.CENTER);

	}
	else {
	  //System.out.println(projectFile.substring(projectFile.lastIndexOf(File.separator)+1));
	  prj = new Project (projectFile );

	  String rootName = projectFile.substring(projectFile.lastIndexOf(File.separator)+1);
	  String root_path = projectFile.substring(0,projectFile.lastIndexOf(File.separator)+1);

	  htBPFileInfo = new Hashtable();
	  createBPFileInfo (new File(root_path),htBPFileInfo, root_path);

	  htRsetFileInfo = new Hashtable();
	  createRsetFileInfo (new File(root_path),htRsetFileInfo, root_path);

	  createSubDirectoryInfo();

	  DirectoryNodeInfo dni = new DirectoryNodeInfo(rootName,0,root_path, false);
	  rootTreeNode = new DirectoryNode(dni);
	  vecUsedBPFile.clear();
	  vecUsedRsetFile.clear();
	  ProcessFileList (rootTreeNode,root_path);

	  for (Enumeration emu = htBPFileInfo.elements(); emu.hasMoreElements(); ) {
		String javafilename = (String)emu.nextElement();
		if (vecUsedBPFile.indexOf(javafilename) == -1 ) {
		  File file = new File (javafilename);
		  DirectoryNodeInfo jdni = new DirectoryNodeInfo(file.getName(),4,file.getAbsolutePath(), false);
		  DirectoryNode jchild_node = new DirectoryNode(jdni);
		  rootTreeNode.add(jchild_node);
		}
	  }


	  treeModel = new DefaultTreeModel(rootTreeNode);
	  /*
	  rootTreeNode = new DefaultMutableTreeNode(rootName);

	  //読み込み処理
	  try {
		f_in = new FileReader(projectFile);
		b_in = new BufferedReader(f_in);
		while((sLine = b_in.readLine()) != null) {
		  if (sLine.toLowerCase().endsWith(".dash")) {

			MyNodeInfo info = new MyNodeInfo (sLine,1,"");
			rootTreeNode.add(new MyNode(info));

			//rootTreeNode.add(new DefaultMutableTreeNode(sLine));
		  }

		  //System.out.println(sLine);
		}
		b_in.close();
		treeModel = new DefaultTreeModel(rootTreeNode);

	  } catch(Exception ex) {
		System.out.println(ex);
		return ;
	  }
	  */
	}
	this.setModel(treeModel);

	scrollPane = new JScrollPane();

	scrollPane.getViewport().setView(this);
	scrollPane.setPreferredSize(new Dimension(250, 100));

	TreePanel = new JPanel(new BorderLayout());
	TreePanel.add(scrollPane,BorderLayout.CENTER);
	//TreePanel = new JScrollPane(this);

  }

  /** Returns The selected node */
  public DirectoryNode getSelectedNode() {
	return SelectedNode;
  }

 /* 各項目には基本的にラベルを表示するため、ラベルを継承している */
 class MyCellRenderer extends JLabel implements TreeCellRenderer {
   public MyCellRenderer(){
	 setOpaque(true);
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value,
	  boolean selected, boolean expanded, boolean leaf,
	 int row, boolean hasFocus){


	 // 各項目で表示されるテキスト
	 setText(value.toString());
	 final int DPX_FILE = 0;
	 final int FOLDER = 1;
	 final int DASH_FILE = 2;
	 final int RSET_FILE = 3;
	 final int JAVA_FILE = 4;
	 final int OTHER_FILE = 5;


	 if (vecChangeFileName.indexOf(value.toString()) != -1 ) {
	   setFont(new Font("Dialog",Font.ITALIC,12));
	   int DirectoryKind = getDirectoryKind (value);
	   switch(DirectoryKind ) {
		 case DPX_FILE:
		 case FOLDER:
		   if (expanded){
			 setIcon(getImageIcon("resources/openfolder.gif"));
		   }else{
			 setIcon(getImageIcon("resources/closefolder.gif"));
		   }
		   break;
		 case DASH_FILE:
		   setIcon(getImageIcon("resources/dashfile2.gif"));
		   break;
		 case RSET_FILE:
		   setIcon(getImageIcon("resources/rsetfile2.gif"));
		   break;
		 case JAVA_FILE:
		   setIcon(getImageIcon("resources/bp2.gif"));
		   break;
		 case OTHER_FILE:
		   setIcon(getImageIcon("resources/etc2.gif"));
		   break;
	   }
	 }
	 else {
	   setFont(new Font("Dialog",Font.PLAIN,12));
	   int DirectoryKind = getDirectoryKind (value);
	   switch(DirectoryKind ) {
		 case DPX_FILE:
		 case FOLDER:
		   if (expanded){
			 setIcon(getImageIcon("resources/openfolder.gif"));
		   }else{
			 setIcon(getImageIcon("resources/closefolder.gif"));
		   }
		   break;
		 case DASH_FILE:
		   setIcon(getImageIcon("resources/dashfile.gif"));
		   break;
		 case RSET_FILE:
		   setIcon(getImageIcon("resources/rsetfile.gif"));
		   break;
		 case JAVA_FILE:
		   setIcon(getImageIcon("resources/bp.gif"));
		   break;
		 case OTHER_FILE:
		   setIcon(getImageIcon("resources/etc.gif"));
		   break;
	   }

	 }
	 if (selected){
	   // 背景色を青に設定
	   //setBackground(Color.blue);
	   setBackground(new Color(206,221,255));
	   // 文字色を白に設定
	   setForeground(Color.black);
	 }else{
	   // 背景色を白に設定
	   setBackground(Color.white);
	   // 文字色を黒に設定
	   setForeground(Color.black);
	 }

	 // 項目が枝か葉か
	 if (leaf){

	   if (vecChangeFileName.indexOf(value.toString()) != -1 ) {
		 setForeground(Color.blue);
	   }

	   // 選択されているかどうか
	   if (selected){
		 // アイコンを追加して
		 //setIcon(new ImageIcon("./img/btn.png"));
	   }else{
		 // アイコンは無し
		 //setIcon(null);
	   }
	   if (value.toString().equals("ﾌﾟﾛｼﾞｪｸﾄを作成又は開いてください...")) {
		 setIcon (null );
	   }
	   if (value.toString().toLowerCase().endsWith(".dpx")) {
		 setIcon(getImageIcon("resources/closefolder.gif"));
	   }
	 }else{
	   // 展開されているかどうか
	   //if (expanded){
	   //  setIcon(getImageIcon("resources/openfolder.gif"));
	   //}else{
	   //  setIcon(getImageIcon("resources/closefolder.gif"));
	   //}

	   if (value.toString().equals("ﾌﾟﾛｼﾞｪｸﾄを作成又は開いてください...")) {
		 setIcon (null );
	   }
	 }

	 return this;
   }

   protected int getDirectoryKind(Object value) {
	 try {
	   DefaultMutableTreeNode node =
			   (DefaultMutableTreeNode)value;
	   DirectoryNodeInfo nodeInfo = (DirectoryNodeInfo) (node.getUserObject());

	   int kind = nodeInfo.getKind();
	   return kind;
	 }
	 catch(Exception e) {
	   return 0;
	 }
   }

   protected boolean isFolder(Object value) {
	 try {
	   DefaultMutableTreeNode node =
			   (DefaultMutableTreeNode)value;
	   DirectoryNodeInfo nodeInfo = (DirectoryNodeInfo) (node.getUserObject());

	   int kind = nodeInfo.getKind();
	   if (kind == 0 || kind == 1) {
		   return true;
	   }
	   return false;
	 }
	 catch(Exception e) {
	   return false;
	 }
   }


 }
 private ImageIcon getImageIcon(String path) {
   java.net.URL url = this.getClass().getResource(path);
   return new ImageIcon(url);

 }

  public boolean openProjectFile(String projectFile) {
	this.setRootVisible(true);

	FileReader f_in;
	BufferedReader b_in;
	String sLine = "";

	if(projectFile == null || projectFile.equals(""))
		return false;

	//System.out.println(projectFile.substring(projectFile.lastIndexOf(File.separator)+1));

	DefaultTreeModel treeModel = null;

	if(projectFile.equals(" ")) {
	  DirectoryNodeInfo dni = new DirectoryNodeInfo("ﾌﾟﾛｼﾞｪｸﾄを作成又は開いてください...",0,"", false);
	  DirectoryNode rootTreeNode = new DirectoryNode(dni);
	  treeModel = new DefaultTreeModel(rootTreeNode);
	  return false;
	}
	else {

	  prj = new Project (projectFile );
	  // ファイルセパレータの取得
	  final String separator = System.getProperty("file.separator");

	  String rootName = projectFile.substring(projectFile.lastIndexOf(File.separator)+1);
	  String root_path = projectFile.substring(0,projectFile.lastIndexOf(File.separator)+1);

	  htBPFileInfo = new Hashtable();
	  createBPFileInfo (new File(root_path),htBPFileInfo, root_path);

	  htRsetFileInfo = new Hashtable();
	  createRsetFileInfo (new File(root_path),htRsetFileInfo, root_path);

	  createSubDirectoryInfo();

	  DirectoryNodeInfo dni = new DirectoryNodeInfo(rootName,0,root_path, false);
	  DirectoryNode rootTreeNode = new DirectoryNode(dni);
	  vecUsedBPFile.clear();
	  vecUsedRsetFile.clear();
	  ProcessFileList (rootTreeNode,root_path);
	  for (Enumeration emu = htBPFileInfo.elements(); emu.hasMoreElements(); ) {
		String javafilename = (String)emu.nextElement();
		if (vecUsedBPFile.indexOf(javafilename) == -1 ) {
		  File file = new File (javafilename);
		  DirectoryNodeInfo jdni = new DirectoryNodeInfo(file.getName(),4,file.getAbsolutePath(), false);
		  DirectoryNode jchild_node = new DirectoryNode(jdni);
		  rootTreeNode.add(jchild_node);
		}
	  }


	  treeModel = new DefaultTreeModel(rootTreeNode);


	  //ProcessFileList(this, root_path);
	  /*
	  String rootName = projectFile.substring(projectFile.lastIndexOf(File.separator)+1);
	  DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(rootName);

	  //読み込み処理
	  try {
		f_in = new FileReader(projectFile);
		b_in = new BufferedReader(f_in);
		while((sLine = b_in.readLine()) != null) {
		  rootTreeNode.add(new DefaultMutableTreeNode(sLine));

		  //System.out.println(sLine);
		}
		b_in.close();

		treeModel = new DefaultTreeModel(rootTreeNode);

	  } catch(Exception ex) {
		System.out.println(ex);
		return false;
	  }
	  */
	}
	this.setModel(treeModel);
	this.repaint();
	//this.setModel(treeModel);
	return true;

	/*
	JScrollPane scrollPane = new JScrollPane();
	scrollPane.getViewport().setView(this);
	scrollPane.setPreferredSize(new Dimension(200, 150));
	TreePanel = new JPanel(new BorderLayout());
	TreePanel.add(scrollPane,BorderLayout.CENTER);
	*/

	/*
	//TreePanel = new JPanel(new BorderLayout());
	TreePanel.removeAll();
	TreePanel.add(scrollPane,BorderLayout.CENTER);
	this.repaint();
	TreePanel.repaint();
	*/

  }
  /**
   * あえてObjectのcloneを呼んでいる。
   * コンストラクタで生成した方が例外処理もなく簡単だが、cloneのテストなので・・・。
   */
  public Object clone(){
	ProjectTree clone = null;
	try{
	  clone = (ProjectTree)super.clone();
	}catch(CloneNotSupportedException e){}
	return clone;
  }

  public void setExpandedState (TreePath path, boolean state) {
	super.setExpandedState(path, state);
  }


  // ファイルリストの処理
  protected void ProcessFileList(DirectoryNode parent_node, String parent_nodestr)
  {
	// ファイルセパレータの取得
	final String separator = System.getProperty("file.separator");
	File directory = new File(parent_nodestr);
	String file_list[] = directory.list();
	file_list = SortFileList(parent_nodestr,file_list);

	// 子ノードの追加
	for(int i=0;i<file_list.length;i++)
	{

	  if (file_list[i].equals("aaa") ) {
		int a = 1;
	  }
	  File file = new File(parent_nodestr + separator + file_list[i]);
	  if(!file.isDirectory())
	  {
		if (!file_list[i].toUpperCase().endsWith(".DPX") ) {
		  //if (prj.getFileNames().indexOf(file_list[i]) != -1 ) {
		  String s = file.getAbsolutePath();
		  //String s2 = prj.getFileNamesWithPath();
		  if (prj.getFileNamesWithPath().indexOf(file.getAbsolutePath()) != -1 ) {
			int filekind = 0;
			if (file_list[i].toUpperCase().endsWith(".DASH") ) {
			  filekind = 2;
			}
			else if (file_list[i].toUpperCase().endsWith(".RSET") ) {
			  filekind = 3;
			}
			else if (file_list[i].toUpperCase().endsWith(".JAVA") ) {
			  filekind = 4;
			}
			else  {
			  filekind = 5;
			}


						boolean relationed = false;
						if (filekind == 3 ) {
				for (Enumeration emu = htRsetFileInfo.keys(); emu.hasMoreElements();) {
				  String key = (String)emu.nextElement();
				  String fileNameWithPath = (String)htRsetFileInfo.get(key);
				  if (fileNameWithPath.toLowerCase().endsWith(File.separator+file_list[i].toLowerCase()) ) {
					relationed = true;
				  }
				}
						}


						if (!relationed ) {
				DirectoryNodeInfo dni = new DirectoryNodeInfo(file_list[i],filekind,file.getAbsolutePath(), false);
				DirectoryNode child_node = new DirectoryNode(dni);
				parent_node.add(child_node);
	            
				for (Enumeration emu = htRsetFileInfo.keys(); emu.hasMoreElements();) {
				  String key = (String)emu.nextElement();
				  if (key.startsWith(file_list[i] + "_") ) {
					String rsetfilename = (String)htRsetFileInfo.get(key);
					if (rsetfilename != null ) {
					  file = new File (rsetfilename);
					  DirectoryNodeInfo jdni = new DirectoryNodeInfo(file.getName(),3,file.getAbsolutePath(), true);
					  DirectoryNode jchild_node = new DirectoryNode(jdni);
					  child_node.add(jchild_node);
					  vecUsedRsetFile.addElement(rsetfilename);
	
				  }
	
				}
				}

				for (Enumeration emu = htBPFileInfo.keys(); emu.hasMoreElements();) {
				  String key = (String)emu.nextElement();
				  if (key.startsWith(file_list[i] + "_") ) {
					String javafilename = (String)htBPFileInfo.get(key);
					if (javafilename != null ) {
					  file = new File (javafilename);
					  DirectoryNodeInfo jdni = new DirectoryNodeInfo(file.getName(),4,file.getAbsolutePath(), true);
					  DirectoryNode jchild_node = new DirectoryNode(jdni);
					  child_node.add(jchild_node);
					  vecUsedBPFile.addElement(javafilename);

					}

				  }
				}
						}

			/*
			String s = file_list[i];
			String javafilename = (String)htBPFileInfo.get(file_list[i]);
			if (javafilename != null ) {
			  file = new File (javafilename);
			  DirectoryNodeInfo jdni = new DirectoryNodeInfo(file.getName(),4,file.getAbsolutePath());
			  DirectoryNode jchild_node = new DirectoryNode(jdni);
			  child_node.add(jchild_node);
			  vecUsedBPFile.addElement(javafilename);
			}
			*/
		  }
		}
	  }
	  else {
		if (!file_list[i].toUpperCase().equals("BAK") ) {
		  String path = file.getAbsolutePath();
		  if (vecSubFolderInfo.indexOf(path) != -1 )  {
			DirectoryNodeInfo dni = new DirectoryNodeInfo(file_list[i],1,file.getAbsolutePath(), false);
			DirectoryNode child_node = new DirectoryNode(dni);
			parent_node.add(child_node);
			ProcessFileList(child_node,file.getAbsolutePath());
		  }
		}
	  }

	}
  }
  // ファイルリストのソート
  protected String[] SortFileList(String path,String list[])
  {
	Vector directory_list,file_list;

	directory_list = new Vector();
	file_list = new Vector();

	// ファイルとディレクトリの分別
	for(int i=0;i<list.length;i++)
	{
	  File file = new File(path + separator + list[i]);
	  if(file.isFile())
	  {
		file_list.addElement(new String(list[i]));
	  }
	  else if(file.isDirectory())
	  {
		directory_list.addElement(new String(list[i]));
	  }
	}

	// Vector配列→String配列への変換＆ソート
	String file_name_list[] = new String[file_list.size()];
	file_list.copyInto(file_name_list);
	Arrays.sort(file_name_list);

	String directory_name_list[] = new String[directory_list.size()];
	directory_list.copyInto(directory_name_list);
	Arrays.sort(directory_name_list);

	// ソート済み配列をコピーする(マージ)
	String return_list[] = new String[list.length];

	for(int i=0;i<directory_name_list.length;i++)
	{
	  return_list[i] = directory_name_list[i];
	}

	int offset = directory_name_list.length;
	for(int i=0;i<file_name_list.length;i++)
	{
	  return_list[i+offset] = file_name_list[i];
	}

	return(return_list);
  }

  public void createBPFileInfo(File f, Hashtable htFileTable, String root_path ) {
	File current_dir = new File(f,".");
	String file_list[] = current_dir.list();
	String wkroot_path = root_path;

	if (!wkroot_path.endsWith(File.separator) ) {
	  wkroot_path += File.separator;
	}

	for (int i=0; i<file_list.length; i++ ) {
	  File current_file = new File(f,file_list[i]);
	  if (current_file.isDirectory()){
		createBPFileInfo(current_file, htFileTable, root_path);
	  }
	  else {
		String parentPath = current_file.getParent();
		if (!parentPath.endsWith(File.separator) ) {
		  parentPath += File.separator;
		}
		if (current_file.getName().toUpperCase().endsWith("JAVA_INF") &&
			parentPath.equals(wkroot_path + "java_" + File.separator)){
		  try {
			FileReader f_in;
			BufferedReader b_in;
			String sLine;

			b_in = new BufferedReader(new InputStreamReader(
													new FileInputStream(current_file.getAbsolutePath()),
													"JISAutoDetect"));

			int inf_kind = 0;
			String javafilename = "";
			while ((sLine = b_in.readLine()) != null)
			{
			  if (sLine.equals("[path]")) {
				inf_kind = 1;//Path情報
			  }
			  else if (sLine.equals("[relation dash file]")) {
				inf_kind = 2;//関連Dashファイル情報
			  }
			  else {
				if (inf_kind == 1 ) {
				  if (sLine.equals("current")) {
					String path
						= current_file.getAbsolutePath().substring(0,current_file.getAbsolutePath().lastIndexOf(File.separator)+1);
					String filename = current_file.getName().substring(0,current_file.getName().indexOf("_"));
					javafilename = path + filename;
				  }
				  else {
					javafilename = sLine;//current_file.getAbsolutePath().substring(0,current_file.getAbsolutePath().indexOf("_"));
				  }
				  htFileTable.put(javafilename,javafilename);
				}
				else {
				  htFileTable.put(sLine + "_" + javafilename ,javafilename);
				}

			  }
			}
			b_in.close();

		  } catch(Exception ex) { }

		}
	  }
	}
  }


  public void createRsetFileInfo(File f, Hashtable htFileTable, String root_path ) {
	File current_dir = new File(f,".");
	String file_list[] = current_dir.list();
	String wkroot_path = root_path;

	if (!wkroot_path.endsWith(File.separator) ) {
	  wkroot_path += File.separator;
	}

	for (int i=0; i<file_list.length; i++ ) {
	  File current_file = new File(f,file_list[i]);
	  if (current_file.isDirectory()){
		createRsetFileInfo(current_file, htFileTable, root_path);
	  }
	  else {
		String parentPath = current_file.getParent();
		if (!parentPath.endsWith(File.separator) ) {
		  parentPath += File.separator;
		}
		if (current_file.getName().toUpperCase().endsWith("RSET_INF") &&
			parentPath.equals(wkroot_path + "rset_" + File.separator)){
		  try {
			FileReader f_in;
			BufferedReader b_in;
			String sLine;

			b_in = new BufferedReader(new InputStreamReader(
													new FileInputStream(current_file.getAbsolutePath()),
													"JISAutoDetect"));

			int inf_kind = 0;
			String rsetfilename = current_file.getName().substring(0,current_file.getName().indexOf("_"));
			while ((sLine = b_in.readLine()) != null)
			{
			  if (sLine.equals("[path]")) {
				inf_kind = 1;//Path情報
			  }
			  else if (sLine.equals("[relation dash file]")) {
				inf_kind = 2;//関連Dashファイル情報
			  }
			  else {
				if (inf_kind == 1 ) {
					rsetfilename = sLine;
				}
				else {
				  htFileTable.put(sLine + "_" + rsetfilename ,rsetfilename);
				}

			  }
			}
			b_in.close();

		  } catch(Exception ex) { }

		}
	  }
	}
  }


  public void createSubDirectoryInfo( ) {
	vecSubFolderInfo.clear();
	String directoryInfoFile
		= prj.getProjectFileNameWithPath().substring(
		0,prj.getProjectFileNameWithPath().toLowerCase().lastIndexOf (".dpx")+1) + "directoryinfo";

	try {
	  FileReader f_in;
	  BufferedReader b_in;
	  String sLine;

	  b_in = new BufferedReader(new InputStreamReader(
		  new FileInputStream(directoryInfoFile),
		  "JISAutoDetect"));

	  while ((sLine = b_in.readLine()) != null) {
		String SubDirectoryPath = "";
		SubDirectoryPath = prj.getProjectPath() + sLine;
		vecSubFolderInfo.addElement(SubDirectoryPath);
	  }
	  b_in.close();

	} catch(Exception ex) { }

  }


}

class DirectoryNode  extends DefaultMutableTreeNode  {

  // ファイルセパレータの取得
  final String separator = System.getProperty("file.separator");

  public DirectoryNode( DirectoryNodeInfo info) {
	super(info);
  }

  public void add(DefaultMutableTreeNode child) {
	super.add(child);
	//System.out.println(child + " added to " + this);

	DirectoryNodeInfo childPI = (DirectoryNodeInfo) ((DirectoryNode) child).getUserObject();

	DirectoryNodeInfo oldParent = childPI.getParent();
	//if (parent != null) oldParent.remove(childPI);

	DirectoryNodeInfo newParent = (DirectoryNodeInfo) getUserObject();

	newParent.add(childPI);
  }

  public void remove(DefaultMutableTreeNode child) {
	super.remove(child);
	//System.out.println(child + " removed from " + this);

	DirectoryNodeInfo childPI = (DirectoryNodeInfo) ((DirectoryNode) child).getUserObject();

	DirectoryNodeInfo ParentPI = (DirectoryNodeInfo) getUserObject();
	if (parent != null) ParentPI.remove(childPI);
  }



}

class DirectoryNodeInfo implements Transferable, Serializable {

  final public static DataFlavor INFO_FLAVOR =
	new DataFlavor(DirectoryNodeInfo.class, "Node Information");

  static DataFlavor flavors[] = {INFO_FLAVOR };

  private Vector Children = null;
  private String Name = null;
  private String FilePath = null;
  private int Kind = 0;
  private boolean Relation = false;

  private DirectoryNodeInfo Parent = null;

  public DirectoryNodeInfo(String name, int kind, String filepath, boolean relation) {
   Children = new Vector();
   Name = name;
   FilePath = filepath;
   Kind = kind;
   Relation = relation;
 }

 public String getName() {
   return Name;
 }

 public int getKind() {
   return Kind;
 }

 public boolean isRelation() {
   return Relation;
 }

 public String getPath() {
   return FilePath;
 }

 public void add(DirectoryNodeInfo info) {
   info.setParent(this);
   Children.add(info);
 }

 public void remove(DirectoryNodeInfo info) {
   info.setParent(null);
   Children.remove(info);
 }

 public DirectoryNodeInfo getParent() {
   return Parent;
 }

 public void setParent(DirectoryNodeInfo parent) {
   Parent = parent;
 }

 public Vector getChildren() {
   return Children;
 }

 public Object clone() {
   return new DirectoryNodeInfo(Name, Kind, FilePath, Relation);
 }

 public String toString() {
   return Name;
 }

 // --------- Transferable --------------

 public boolean isDataFlavorSupported(DataFlavor df) {
   return df.equals(INFO_FLAVOR);
 }

 // implements Transferable interface
 public Object getTransferData(DataFlavor df)
	 throws UnsupportedFlavorException, IOException {
   if (df.equals(INFO_FLAVOR)) {
	 return this;
   }
   else throw new UnsupportedFlavorException(df);
 }

 // implements Transferable interface
 public DataFlavor[] getTransferDataFlavors() {
   return flavors;
 }

 // --------- Serializable --------------

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	out.defaultWriteObject();
  }

  private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException {
	in.defaultReadObject();
  }


}






