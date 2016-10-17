# DVM.java
DASH Virtual Machineの実装．ワークプレースとも呼ぶ．  

- public void showViewer ()
	- Viewerの作成
- public void closeViewer ()
- public NewifItface getNewIf ()
- public void RmiModule_ReCreate()
- public DVM(String name, File msgfile, boolean useNewif, boolean useViewer, File dashdir)
	- コンストラクタ

```java
    if (noGUI)
      newif = new NewifDummy();
    else {
      if (!DashMode.equals("on") ) {
        newif = new NewIf2(comInt.getDVMname(), this, msgfile, dashdir);
      } else {
        newif = new Newif(comInt.getDVMname(), this, msgfile, dashdir);
      }
    }
```

ここでは```newif = new Newif(comInt.getDVMname(), this, msgfile, dashdir);```が実行される．

- void startVM()
	- 起動
- public void run()
	- メッセージ待ちのスレッド．DVMはメッセージが届くのを待つ．届いたら，エージェントに渡す．

```java
	String[] from = comInt.getDVMname().split("_"); // DASH or IDEA ?
```

DASHとIDEAでDVMnameが異なるため，その違いを利用して以下のように場合分けする．  

```java
        // (3)存在するエージェント
        if (receiver != null) {
          newif.replaceConsole();
          if (msg.performative.equals(DashMessage.KILLFORCE) &&
              msg.from.equals(DashMessage.IF) &&
              msg.content == null)
              ; //(@see DashAgent.run())
          else {
            if(from.length > 1){
              viewer.showMsg(msg); // DASH
            } else
              newif.showMsg(msg); // IDEA
            
            if (viewer != null ) {
              viewer.showMsg(msg);		// ADD COSMOS
            }
            newif.ViewerShowMsg(msg);
          }
          receiver.putMsg(msg);
          continue;
        }
```

- void finalizeDVM()
	- 終了処理
- private void processDVMmsg(DashMessage msg)
	- DVM宛のメッセージを処理する
- private void loadLocalClassData(DashMessage msg)
	- _getBytecodeを受信したときの処理．バイトコードを読み込み，_putBytecodeにして返す．
- private void getLocalRuleset(DashMessage msg)
	- _getRulesetを受信したときの処理．ルールセットのファイルを読み込み，_putRulesetにして返す．
- void addLoadQueue(File file)
	- Creater()にエージェントを生成するよう指示を出す．loadAgent()で読み込む．
- void loadAgent(File file)
	- エージェントファイルを読み込む．Creater.run()が呼び出す．
- String createAgent(String cname, String description, String filename, String facts, String origin, String oldname, ps.AgentProgram program, ps.WorkMem workmem)
	- エージェントを生成する
- void killAgent(String name, DashMessage yuigon)
	- エージェントを消滅させる
- void stopAgent(String name)
	- エージェントを消滅させる．
	- Newif.killAgent()で呼び出す．
- private String newName(String cname)
	- ワークプレースに生成されたエージェントの名前を決める
- private String currentTime()
	- 現在の時刻を，YYYYMMDDhhmmssx形式で返す
- public DashMessage sendMessageFromUser(DashMessage msg, String perf, String diag, String to, String arrival, String content, Hashtable other)
	- NewifなどのインタフェースあるいはDVMから，メッセージを作って(Createrの)送信キューに入れる
- void sendMessageFromUser(DashMessage msg)
	- Creater.run()から呼ばれる
- String sendMessageFromAgent(OAVdata oav, String name)
	- エージェントからメッセージを作って送信キューに入れる
- long moveAgent(String arrival, String cname, String name, ps.AgentProgram program, ps.WorkMem workmem, String filename, String origin)
	- (move)により呼び出される．エージェントを移動する．
	- _moveというメッセージを生成先のDVMに届ける．DVM.processDVMmsg()で処理される．
- long instantiateAgent(OAVdata oav, String name, String description, String filename, String origin)
	- (instantiate :into)により呼び出される．エージェントをインスタンシエートする．
	- _createInstanceというメッセージを生成先のDVMに届ける．
	- DVM.processDVMmsg()で処理される．
- long sendInstantiate(OAVdata oav, String name)
	- (instantiate :from :name)により呼び出される
	- エージェントをインスタンシエートする
	- _instantiateというメッセージをリポジトリに届ける
- boolean checkDVM(String dvmname)
	- dvmnameで指定されたDVMが存在するかを調べる
- public Vector lookup(String[][] selector)
	- selectorで指定されたエージェントの情報を返す
- private void setupClassLoader()
	- クラスローダの準備
- byte[] loadClassData(String classname, String origin) throws Exception
	- バイトコードを返す
- private byte[] loadLocalClassData(String classname) throws Exception
	- クラスファイルからクラスを読み込み，バイトコードを返す
- private void setTempClassName(String string)
- private byte[] loadRemoteClassData(String classname, String origin) throws Exception
	- 他のDVMに_getBytecodeを送信して_putBytecodeを返してもらい，その中に格納されたバイトコードを返す
- private DashMessage waitReply(DashMessage msg)
	- _putBytecode, _putRulesetを待つ
- private void putReply(DashMessage msg)
	- _putBytecode, _putRulesetが届いたときに呼ばれる
- public String getRuleset(String filename, String env)
	- エージェントから要求されたルールセットのファイルを読みこみ，返す
- public void createFileList(File f, Hashtable htFileTable, String DefaultDir )
	- 指定したディレクトリ以下に含まれる全てのファイルの一覧を作成する
- private String getLocalRuleset(String filename, Hashtable htFileList)
	- dash.loadpathで指定されているディレクトリからルールセットのファイルを読みこみ、返す
- private String getRemoteRuleset(String filename, String env)
	- 他のDVMに_getRulesetを送信して_putRulesetを返してもらい，その中に格納されたルールセットを返す
- boolean notifyAlarm(String agname, String id, String oav)
	- エージェントagnameにアラームoavを伝える
- String getFullname()
	- 名前を返す
- void openInspector(String agname)
	- インスペクタを表示する
- public boolean isRtype()
	- リポジトリ型DVMならtrueを返す
- boolean hasAgent(String agname)
	- エージェントが居ればtrueを返す
- int numberOfAgent()
	- エージェントの個数を返す
- void println(String s)
	- Newifに表示する
- void printlnE(String s)
	- Newifに表示する
- void settextOnNewif(String agent, String s)
	- Newifに設定する
- public String getDVMname()
	- ホスト名を含む環境名を返す
- void showAgentScript(String agname)
- public void loadProject(File file)
	- プロジェクトファイル(*.prj)を読み込む
- String setAlarm(String agname, boolean repeat, long time, String oav)
	- アラームをセットする
- String[] cancelAlarm(String agname, String alarmID)
	- アラームをキャンセルする
- public void setParentFrame (IdeaMainFrame parentframe)
- public IdeaMainFrame getParentFrame ()
- public void setWpIndex (int wpIndex)
- public void setWpTab (JTabbedPane wpTab )
- public static void main(String args[])
- public String getProjectPath()
- public String getTempClassName()
- public String[] getUserClasspath()