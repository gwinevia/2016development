package dash;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * RelationViewer.java
 *
 *
 * Created: Wed Aug 27 07:15:58 2003
 *
 * @author <a href="mailto:takagaki">Akira TAKAGAKI</a>
 * @version 1.0
 */
public class AgentRelationPanel extends JPanel implements TreeSelectionListener{
  private JTree            ag_relation_tree;
  private DefaultTreeModel ag_relation_tree_model;

  private String repository_dirpath = null;
  private JTextArea preview_area;

  public AgentRelationPanel() 
  {
  	super(new BorderLayout());
   JPanel relationpanel = new JPanel(new BorderLayout());
    ag_relation_tree_model = new DefaultTreeModel(new DefaultMutableTreeNode("repository"));
    ag_relation_tree = new JTree(ag_relation_tree_model);
    ag_relation_tree.setCellRenderer(new AgentRelationTreeCellRender());
    ag_relation_tree.addTreeSelectionListener(this);
    ag_relation_tree.setRootVisible(true);
    JScrollPane scrollpane = new JScrollPane();
    scrollpane.getViewport().setView(ag_relation_tree);
    relationpanel.add(scrollpane, BorderLayout.CENTER);
    add(relationpanel, BorderLayout.CENTER);
  }
  

  public void setPreviewArea(JTextArea pa)
  {
    preview_area = pa;
  }

  public void setRepositoryDirPath(String dirpath)
  {
    repository_dirpath = dirpath;
  }

  public String getRepositoryDirPath()
  {
    return repository_dirpath;
  }


  /**
   * タスク構造によるエージェント間の関係を表示
   *
   */
  public void showAgentRelation(){
    ag_relation_tree_model.setRoot(new DefaultMutableTreeNode("repository"));

   
    Vector taskinfo_set = new Vector();   // DashAgentTaskオブジェクトを格納するVector
    String[] agent_names =  (new File(repository_dirpath)).list();  // リポジトリ上の全エージェントファイル名
  
    for (int i=agent_names.length-1; i>=0; i--) {
      if (agent_names[i].endsWith("dash")) {
        DashAgentTask taskinfo = getTaskInformation(agent_names[i], getFileFromRepository(agent_names[i]));
        if (taskinfo != null) {
          taskinfo_set.add(taskinfo);
        }
      }
    }
   
    Vector tasktrees = getTaskTrees(taskinfo_set);    // タスク木構造群を取得

    Vector agenttasktree_data = new Vector();

    for (int i=0; i<tasktrees.size(); i++) {
      setAgentTaskTreeData((DashAgentTask)tasktrees.get(i), (DefaultMutableTreeNode)ag_relation_tree_model.getRoot());
    }
    ag_relation_tree_model.reload();
  }


  private class AgentRelationTreeCellRender extends JLabel implements TreeCellRenderer {

    public AgentRelationTreeCellRender()
    {
      setOpaque(true);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, 
                                                  boolean leaf, int row, boolean hasFocus)
    {
      setText(value.toString());

      if (selected) {
        setBackground(new Color(204, 204, 255));
      }
      else {
        setBackground(Color.white);
      }

      if (value.toString().equals("repository")) {
        setIcon(new ImageIcon("./src/dash/resources/dashRep.gif"));
      }
      else {
        setIcon(null);
      }

      return this;
    }
  }


  public void setAgentTaskTreeData(DashAgentTask agent_task, DefaultMutableTreeNode treenode)
  {
    String agent_name = agent_task.getAgentName();
    String task_name = agent_task.getTaskName();
    Vector childs = agent_task.getChilds();

    DefaultMutableTreeNode new_treenode = new DefaultMutableTreeNode(agent_name + " (" + task_name + ")");
    treenode.add(new_treenode);

    if (childs != null) {
      for (int i=0; i<childs.size(); i++) {
        Vector child_agents = (Vector)childs.get(i);
        for (int j=0; j<child_agents.size(); j++) {
          setAgentTaskTreeData((DashAgentTask)child_agents.get(j), new_treenode);
        }
      }
    }
  }


  /**
   * タスクの木構造の集合(Vector)を取得
   *
   * @param taskinfo_set a <code>Vector</code> value
   * @return a <code>Vector</code> value
   */
  private Vector getTaskTrees(Vector taskinfo_set)
  {
    Vector tasktrees = new Vector();

    for (int i=taskinfo_set.size()-1; i>=0; i--) {
      boolean canbe_subtask_flag = false;
      DashAgentTask agent_task = (DashAgentTask)taskinfo_set.get(i);
      taskinfo_set.remove(i);
      for (int j=0; j<taskinfo_set.size(); j++) {
        DashAgentTask agent_task_tmp = (DashAgentTask)taskinfo_set.get(j);
        if (agent_task_tmp.putChild(agent_task)) {
          canbe_subtask_flag = true;
          break;
        }
      }
      if (!canbe_subtask_flag) {
        for (int j=0; j<tasktrees.size(); j++) {
          DashAgentTask agent_task_tmp = (DashAgentTask)tasktrees.get(j);
          if (agent_task_tmp.putChild(agent_task)) {
            canbe_subtask_flag = true;
            break;
          }
        }
      }
      if (!canbe_subtask_flag) {
        tasktrees.add(agent_task);
      }
    }

    return tasktrees;
  }


  /**
   * 指定したエージェント知識記述プログラムの中身を取得
   *
   * @param filename a <code>String</code> value
   * @return a <code>String</code> value
   */
  private String getFileFromRepository(String filename)
  {
    StringBuffer content = new StringBuffer();
    try {
      String line;
      BufferedReader br = new BufferedReader(new FileReader(repository_dirpath + File.separator + filename));
      while ((line = br.readLine()) != null) {
        content.append(line + "\n");
      }
      br.close();
    }
    catch (IOException ioe) {
      System.out.println("エラー： そのようなファイルはありません。");
      ioe.printStackTrace();
    }

    return content.toString();
  }


  /**
   * タスク情報(DashAgentTaskオブジェクト)を取得
   *
   * @param agent_name a <code>String</code> value
   * @param content a <code>String</code> value
   * @return a <code>DashAgentTask</code> value
   */
  private DashAgentTask getTaskInformation(String agent_name, String content)
  {
    Vector dashorg_rules = getDashOrgRules(content);
    DashAgentTask agenttask = null;

    if (dashorg_rules != null) {
      for (int i=0; i<dashorg_rules.size(); i++) {
        Vector[] rule = (Vector[])dashorg_rules.get(i);
        Vector task = getOavAttrValue(getSpecifiedOAVdataFirst(rule[0], "task-check"), ":task");
        if (task != null) {
          Vector tmp;
          if ((tmp = getSpecifiedOAVdataAll(rule[1], "decompose")) != null) {
            agenttask = new DashAgentTask(agent_name, task, tmp, null); // task-check --> decompose の場合
            break;
          }
          else if ((tmp = getSpecifiedOAVdataFirst(rule[1], "bid")) != null) {
            agenttask = new DashAgentTask(agent_name, task, null, tmp); // task-check --> bid の場合
            break;
          }
        }
      }

      return agenttask;
    }
    else {
      return null;
    }
  }


  /**
   * 指定した名前のOAVデータを取得(1つだけ)
   *
   * @param oavs a <code>Vector</code> value
   * @param oavname a <code>String</code> value
   * @return a <code>Vector</code> value
   */
  private Vector getSpecifiedOAVdataFirst(Vector oavs, String oavname) 
  {
    Vector buffer = new Vector();
    int kakko_count = 0;
    boolean found_flag = false;

    for (int i=0; i<oavs.size(); i++) {
      if (found_flag) {
        if (((String)oavs.get(i)).equals("(")) {
          kakko_count++;
          buffer.add("(");
        }
        else if (((String)oavs.get(i)).equals(")")) {
          kakko_count--;
          buffer.add(")");
          if (kakko_count == 0) {
            break;
          }
        }
        else {
          buffer.add(oavs.get(i));
        }
      }
      else {
        if (((String)oavs.get(i)).equals(oavname)) {
          buffer.add("(");
          kakko_count++;
          buffer.add(oavname);
          found_flag = true;
        }
      }
    }

    if (buffer.isEmpty()) {
      return null;
    }
    else {
      return buffer;
    }
  }


  /**
   * 指定した名前のOAVデータを全て取得(OAVデータ(Vector)のVector)
   *
   * @param oavs a <code>Vector</code> value
   * @param oavname a <code>String</code> value
   * @return a <code>Vector</code> value
   */
  private Vector getSpecifiedOAVdataAll(Vector oavs, String oavname) 
  {
    Vector result = new Vector();
    Vector buffer = new Vector();
    int kakko_count = 0;
    boolean found_flag = false;

    for (int i=0; i<oavs.size(); i++) {
      if (found_flag) {
        if (((String)oavs.get(i)).equals("(")) {
          kakko_count++;
          buffer.add("(");
        }
        else if (((String)oavs.get(i)).equals(")")) {
          kakko_count--;
          buffer.add(")");
          if (kakko_count == 0) {
            result.add(buffer);
            buffer = new Vector();
            found_flag = false;
          }
        }
        else {
          buffer.add(oavs.get(i));
        }
      }
      else {
        if (((String)oavs.get(i)).equals(oavname)) {
          buffer.add("(");
          kakko_count++;
          buffer.add(oavname);
          found_flag = true;
        }
      }
    }

    if (result.isEmpty()) {
      return null;
    }
    else {
      return result;
    }
  }


  /**
   * OAVデータの指定した属性値を取得
   *
   * @param oav a <code>Vector</code> value
   * @param attrname a <code>String</code> value
   * @return a <code>Vector</code> value
   */
  private Vector getOavAttrValue(Vector oav, String attrname)
  {
    Vector buffer = new Vector();
    int kakko_count = 0;
    boolean found_flag = false;

    if (oav != null) {
      for (int i=0; i<oav.size(); i++) {
        if (found_flag) {
          if (((String)oav.get(i)).equals("(")) {
            kakko_count++;
            buffer.add("(");
          }
          else if (((String)oav.get(i)).equals(")")) {
            kakko_count--;
            buffer.add(")");
            if (kakko_count == 0) {
              break;
            }
          }
          else {
            buffer.add(oav.get(i));
          }
        }
        else {
          if (((String)oav.get(i)).equals(attrname)) {
            if (((String)oav.get(i+1)).equals("(")) {
              found_flag = true;
            }
            else {
              buffer.add(oav.get(i+1));
              break;
            }
          }
        }
      }
      return buffer;
    }
    else {
      return null;
    }
  }


  /**
   * Dash-Orgのルール群を取得(Vector
   *
   * @param content a <code>String</code> value
   * @return ルール群 ---- rules (Vector)
   *                        |
   *                        +-- rules(0) = rule (Array)
   *                        |    |
   *                        |    +-- rule[0] = conditions (Vector)
   *                        |    |     +-- conditions(1) = condition (Vector)
   *                        |    |     +-- ...
   *                        |    |
   *                        |    +-- rule[1] = actions (Vector)   
   *                        |          +-- actions(0) = action (Vector)
   *                        |          +-- ...
   *                        |
   *                        +-- ...
   */
  private Vector getDashOrgRules(String content)
  {
    int kakko_count = 0;
    String[] previous_tokens = new String[2];
    StringBuffer buffer = new StringBuffer();
    boolean dashorg_flag = false;
    Vector dashorg_rules = new Vector();
    Vector condition_buffer = new Vector();
    Vector action_buffer = new Vector();
    DashTokenizer tokenizer = new DashTokenizer(new BufferedReader(new StringReader(content)));
    boolean condition_flag = true;
    int token;
    try {
      // Dash-Org のルールセット部分まで読み飛ばす
      token_loop: while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
        if (!dashorg_flag) {
          switch (token) {
          case '(':
            buffer.delete(0, buffer.length());
            buffer.append("(");
            break;
          case StreamTokenizer.TT_WORD:
            if (buffer.toString().equals("(") && tokenizer.sval.equals("rule-set")) {
              buffer.append("rule-set");
            }
            else if (buffer.toString().equals("(rule-set") && tokenizer.sval.equals("Dash-Org")) {
              // Dash-Org の中の property と initial_facts を読み飛ばす
              for (int i=0; i<2; i++) {
                skip_loop: while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
                  switch (token) {
                  case '(':
                    kakko_count++;
                    break;
                  case ')':
                    kakko_count--;
                    if (kakko_count == 0) {
                      break skip_loop;
                    }
                    break;
                  }
                }
              }
              buffer.delete(0, buffer.length());
              dashorg_flag = true;
            }
            else {
              buffer.delete(0, buffer.length());
            }
            break;
          default:
            buffer.delete(0, buffer.length());
          }
        }
        else if (condition_flag) {
          switch (token) {
          case '(':
            if (!condition_buffer.isEmpty() && ((String)condition_buffer.lastElement()).equals("~")) {
              condition_buffer.setElementAt("~(", condition_buffer.size()-1);
            }
            else {
              condition_buffer.add("(");
            }
            kakko_count++;
            break;
          case ')':
            condition_buffer.add(")");
            kakko_count--;
            break;
          case '-':
            if (((String)condition_buffer.lastElement()).equals("-")) {
              condition_buffer.setElementAt("--", condition_buffer.size()-1);
            }
            else {
              condition_buffer.add("-");
            }
            break;
          case '>':
            if (((String)condition_buffer.lastElement()).equals("--")) {
              condition_buffer.remove(condition_buffer.size()-1);
              condition_flag = false;
            }
            else {
              condition_buffer.add(">");
            }
            break;
          case '\'':
          case '"':
            condition_buffer.add(tokenizer.sval);
            break;
          case StreamTokenizer.TT_NUMBER:
            condition_buffer.add(String.valueOf(tokenizer.nval));
            break;
          case StreamTokenizer.TT_WORD:
            if (condition_buffer.toString().equals("(") && tokenizer.sval.equals("rule")) {
              condition_buffer.add("rule");
            }
            else if (condition_buffer.toString().equals("(rule")) {
              condition_buffer.clear();
            }
            else {
              condition_buffer.add(tokenizer.sval);
            }
            break;
          default:
            condition_buffer.add(String.valueOf((char)token));
          }
        }
        else {
          switch (token) {
          case '(':
            action_buffer.add("(");
            kakko_count++;
            break;
          case ')':
            action_buffer.add(")");
            kakko_count--;
            if (kakko_count == 0) {
              dashorg_rules.add(new Vector[]{condition_buffer, action_buffer});
              condition_buffer = new Vector();
              action_buffer = new Vector();
              condition_flag = true;
            }
            else if (kakko_count < 0) {
              break token_loop;
            }
            break;
          case '\'':
          case '"':
            action_buffer.add(tokenizer.sval);
            break;
          case StreamTokenizer.TT_NUMBER:
            action_buffer.add(String.valueOf(tokenizer.nval));
            break;
          case StreamTokenizer.TT_WORD:
            action_buffer.add(tokenizer.sval);
            break;
          default:
            action_buffer.add(String.valueOf((char)token));
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    if (dashorg_flag) {
      return dashorg_rules;
    }
    else {
      return null;
    }
  }


  /**
   * Describe class <code>DashTokenizer</code> here.
   *
   */
  private class DashTokenizer extends StreamTokenizer
  {
    public DashTokenizer(BufferedReader br)
    {
      super(br);
      resetSyntax();
      wordChars('0', '9');
      wordChars('a', 'z');
      wordChars('A', 'Z');
      wordChars('_', '_');
      wordChars(':', ':');
      wordChars('?', '?');
      whitespaceChars(' ', ' ');
      whitespaceChars('\t', '\t');
      whitespaceChars('\n', '\n');
      whitespaceChars('\r', '\r');
      quoteChar('\'');
      quoteChar('"');
      parseNumbers();
      eolIsSignificant(false);
      slashStarComments(true);
      slashSlashComments(true);
    }
  }



  /**
   * エージェント毎にタスク情報を記録するためのクラス
   *
   */
  private class DashAgentTask
  {
    private String agent_name;            // エージェント名
    private Vector task;                  // 当該エージェントが実行するタスク
    private Vector decomposed_tasks;      // サブタスク
    private Vector decomposed_tasks_to;   // サブタスクの依頼先
    private Vector bid_task;              // 今のところ使う予定なし

    private Vector childs;                // タスク木構造の子となるエージェントを格納
                                          // ( (サブタスク1を実行する子エージェント), 
                                          //   (サブタスク2を実行する子エージェント),
                                          //    ...
                                          //   (サブタスクnを実行する子エージェント)
                                          // )

    public DashAgentTask(String agent_name, Vector task, Vector decompose_info, Vector bid_task)
    {
      this.agent_name = agent_name;
      this.task = task;
      if (decompose_info != null) {
        this.decomposed_tasks = new Vector();
        for (int i=0; i<decompose_info.size(); i++) {
          decomposed_tasks.add(getOavAttrValue((Vector)decompose_info.get(i), ":task"));
        }
        this.decomposed_tasks_to = new Vector();
        this.childs = new Vector();
        for (int i=0; i<decompose_info.size(); i++) {
          this.decomposed_tasks_to.add((String)getOavAttrValue((Vector)decompose_info.get(i), ":to").get(0));
          this.childs.add(new Vector());
        }
      }
      else {
        this.decomposed_tasks_to = null;
        this.childs = null;
      }
      this.bid_task = bid_task;
    }

    public String getAgentName()
    {
      return this.agent_name;
    }

    public String getDecomposedTaskTo(int index)
    {
      return (String)this.decomposed_tasks_to.get(index);
    }

    public Vector getTask()
    {
      return this.task;
    }

    public Vector getDecomposedTasks()
    {
      return this.decomposed_tasks;
    }

    public Vector getBidTask()
    {
      return this.bid_task;
    }

    public String getTaskName()
    {
      return (String)getOavAttrValue(this.task, ":name").get(0);
    }

    public String[] getDecomposedTaskNames()
    {
      Vector tasknames = new Vector();
      for (int i=0; i<decomposed_tasks.size(); i++) {
        tasknames.add((String)getOavAttrValue((Vector)decomposed_tasks.get(i), ":name").get(0));
      }
      return (String[])tasknames.toArray();
    }

    public boolean putChild(DashAgentTask agent_task)
    {
      String taskname = (String)getOavAttrValue(agent_task.getTask(), ":name").get(0);
      boolean result = false;

      if (this.decomposed_tasks != null) {
        for (int i=0; i<this.decomposed_tasks.size(); i++) {
          if (taskname.equals((String)getOavAttrValue((Vector)decomposed_tasks.get(i), ":name").get(0))) {
            ((Vector)childs.get(i)).add(agent_task);
            result = true;
            break;
          }
        }
        if (result == false) {
          childs_loop: for (int i=0; i<childs.size(); i++) {
            Vector child_agents = (Vector)childs.get(i);
            for (int j=0; j<child_agents.size(); j++) {
              if (((DashAgentTask)child_agents.get(j)).putChild(agent_task)) {
                result = true;
                break childs_loop;
              }
            }
          }
        }
      }

      return result;
    }

    public Vector getChilds()
    {
      return childs;
    }

    public String getTaskString()
    {
      StringBuffer buffer = new StringBuffer();
      for (int i=0; i<task.size(); i++) {
        buffer.append((String)task.get(i) + " ");
      }
      return buffer.toString().trim();
    }

    public Vector getDecomposedTaskStrings()
    {
      Vector result = new Vector();

      for (int i=0; i<decomposed_tasks.size(); i++) {
        Vector tmp_vct = (Vector)decomposed_tasks.get(i);
        StringBuffer buffer = new StringBuffer();
        for (int j=0; j<tmp_vct.size(); j++) {
          buffer.append((String)tmp_vct.get(j) + " ");
        }
        result.add(buffer.toString().trim());
      }
      return result;
    }

    public String getBidTaskString()
    {
      StringBuffer buffer = new StringBuffer();
      for (int i=0; i<bid_task.size(); i++) {
        buffer.append((String)bid_task.get(i) + " ");
      }
      return buffer.toString().trim();
    }
  }
	
	String oldfilename;
	 
  public void valueChanged(TreeSelectionEvent e){
  	//System.out.println("リストのチェンジ");
  	if (repository_dirpath==null) return;
    if (!repository_dirpath.endsWith(File.separator)) {
      repository_dirpath = repository_dirpath + File.separator;
    }
    
    String filename;
    if(ag_relation_tree.getSelectionPath()==null){
    	filename = oldfilename;
    	}else{
    	filename = repository_dirpath + ag_relation_tree.getSelectionPath().getLastPathComponent().toString().split("\\s", 2)[0];
 		oldfilename=filename;
        }
   
    // ファイルを開いて、テキストペインに読み取る
    try {
      FileReader fr = new FileReader(filename);
      BufferedReader br = new BufferedReader(fr);
      preview_area.read(br, null);
      preview_area.setCaretPosition(0);
      fr.close();
    }
    catch (Exception ee) {}
  }


  public String[] getSelectedFileNames()
  {
    TreePath[] treepath = ag_relation_tree.getSelectionPaths();
    String[] filenames = new String[treepath.length];

    for (int i=0; i<treepath.length; i++) {
      filenames[i] = treepath[i].getLastPathComponent().toString().split("\\s", 2)[0];
    }

    return filenames;
  }


  public static void main(String[] args)
  {
    JFrame relation_viewer = new JFrame();
    Container content_pane = relation_viewer.getContentPane();
    final AgentRelationPanel agent_relation_panel = new AgentRelationPanel();
    JButton show_button = new JButton("Show");
    agent_relation_panel.setRepositoryDirPath("e:\\idea\\scripts\\cnp");
    show_button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          agent_relation_panel.showAgentRelation();
        }
      });
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(show_button, BorderLayout.NORTH);
    panel.add(agent_relation_panel, BorderLayout.CENTER);
    content_pane.add(panel);
    relation_viewer.setTitle("Agent Relation Viewer (EXPERIMENTAL)");
    relation_viewer.setSize(300,400);
    relation_viewer.setLocation(100, 100);
    relation_viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //relation_viewer.show();
	  relation_viewer.setVisible(true);
  }

}