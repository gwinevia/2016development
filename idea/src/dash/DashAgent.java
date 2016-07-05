package dash;

import ps.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * DASHのエージェントを実現するクラス。
 * 旧DASHでいうところのCM_J & TPM。
 */
public class DashAgent implements Runnable, ps.Controller {

	public Clipman cm = null;
	public Pipeman pm = null;
	/** DVM */
	//private DVM dvm;
	// COSMOS publicに変更
	public DVM dvm;

	/** エージェントのクラス名(ファイル名など) */
	private String cname;

	/** エージェントの出身 */
	private String origin;

	/** エージェント名 */
	private String name;

	/** スクリプト */
	private String script;

	/** ファイル名 */
	private String filename;

	/** プロダクションシステム */
	//private ProdSys prodsys;
	public ProdSys prodsys;

	/** エージェントのスレッド */
	private Thread thread;

	/** メッセージキュー */
	private Vector msgQueue;

	/** ベースプロセス(ない場合はnull) */
	//private DashBP baseProcess;
	public DashBP baseProcess;

	/** ベースプロセスを実行するスレッド(ない場合はnull) */
	private Thread bpThread;

	/**
	 * クラスローダ。ベースプロセスやメソッドを読み込む。
	 * バイトコードは、変数originの値として格納されているDVMから読み込まれる。
	 * 例えば、リポジトリから生成した場合はリポジトリから読み込む。
	 */
	private ClassLoader classLoader;

	/**
	 * 移動した後で移動先のDVMから来た_createdInstance
	 */
	private DashMessage yuigon;

	/** 移動エージェントなら(一回でも移動したなら)true */
	private boolean moved;

	private boolean virtual_bp; // added by takagaki
	/**
	 * コンストラクタ
	 * @param cname エージェントのクラス名
	 * @param name DVMが決めたエージェント名
	 * @param script スクリプト "(agent .. ～ )"。
	 * 移動して来たエージェントならnull。
	 * @param filename ディレクトリを含まないファイル名 "Sample.dash"
	 * @param dvm DVM
	 * @param facts 0個以上のOAVデータを要素に持つリスト
	 * @param origin 格納されていたリポジトリ名あるいは最初に生成されたワークプレース名。
	 * @param program 移動して来たエージェントならAgentProgram。
	 * そうでないならnull。
	 * @param workmem 移動して来たエージェントならWorkMem。
	 * そうでないならnull。
	 */
	DashAgent(
		String cname,
		String name,
		String script,
		String filename,
		DVM dvm,
		String facts,
		String origin,
		ps.AgentProgram program /*boolean isMobile*/
	, ps.WorkMem workmem) {
		this.cname = cname;
		this.name = name;
		this.dvm = dvm;
		this.script = script;
		this.filename = filename;
		this.origin = origin;

		this.cm = Clipman.getInstance();
		this.pm = Pipeman.getInstance();

		if (System.getProperty("language") == null) {
			System.setProperty("language", "japanese");
		}

		msgQueue = new Vector();

		moved = (program != null);

		prodsys = new ProdSys(this, filename, script, program, workmem);
		//prodsys.workmem.
		initFact(facts, (program != null));
		classLoader = new DashClassLoader(dvm, origin);

		//result = true;
		// (Rule-set ...)を作成する
		prodsys.updateRulesetFact();

		// 静的includeの実行(移動後ではない場合)。
		try {
			if (!moved)
				prodsys.includeRuleset(dvm, origin);
		} catch (PSException e) {
			result = false;
			if (System.getProperty("language").equals("japanese")) {
				printlnE("推論部エラー:" + e.printString());
			} else {
				printlnE("Reasoning part error:" + e.printString());
			}
		} catch (DashException e) {
			result = false;
			if (System.getProperty("language").equals("japanese")) {
				printlnE("DASHエラー:" + e.printString());
			} else {
				printlnE("DASH error:" + e.printString());
			}
		}

	}

	/**
	 * ワーキングメモリに、
	 * (Status ...)、(Members ...)、(Msg ...)の
	 * 3つのファクトを順に追加する。
	 * その後、factsで指定されたファクトを追加する。
	 * factsは
	 * (ルールセット名 OAV OAV ... ルールセット名 OAV OAV ...)
	 * @param facts インスタンシエート時に指定されることがある。
	 */
	private void initFact(String facts, boolean isMobile) {
		String status =
			"(Status"
				+ " :cname "
				+ cname
				+ " :name "
				+ name
				+ " :environment "
				+ dvm.getFullname()
				+ " :origin "
				+ origin
				+ ")";
		String members =
			"(Members"
				+ " :manager "
				+ (dvm.isRtype() ? "null" : DashMessage.IF)
				+ ")";
		String p =
			(isMobile ? "__INIT_R" : (dvm.isRtype() ? "__INIT_C" : "__INIT_I"));
		String msg = "(Msg" + " :performative " + p + " :content (INIT)" + ")";
		prodsys.addInitFact(OAVdata.fromString(status, false));
		prodsys.addInitFact(OAVdata.fromString(members, false));
		prodsys.addInitFact(OAVdata.fromString(msg, false));

		if (facts != null) {
			prodsys.addFacts(facts);
			/*
			OAVdata temp = OAVdata.fromString("(temp :facts "+facts+")", false);
			TafList list = (TafList)temp.getValue(":facts");
			for (int i=0; i<list.size(); i++) {
			  OAVdata fact = (OAVdata)list.getValue(i);
			  prodsys.addFactToDefaultWM(fact, false);
			}
			*/
		}
	}

	/**
	 * (property ..)から、
	 * 機能名(function :word 機能名)と(comment :text コメント)を探し、
	 * [0]に機能名を、[1]にコメントをセットして返す。
	 * 記述されていない場合、""をセットする。
	 */
	public String[] getFunctionAndComment() {
		return prodsys.getFunctionAndComment();
	}

	/**
	 * エージェントの動作を開始する。
	 */
	void startAgent(ThreadGroup threadGroup) {
		thread = new Thread(threadGroup, this, name);
		prodsys.println("***** Thread Start");
		thread.start();
	}

	/**
	 * エージェントのメインループ。
	 */
	public boolean result = true;
	public void run() {
		//result = true;
		// (Rule-set ...)を作成する
		prodsys.updateRulesetFact();

		// 静的includeの実行(移動後ではない場合)。
		try {
			if (!moved)
				prodsys.includeRuleset(dvm, origin);
		} catch (PSException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("推論部エラー:" + e.printString());
			} else {
				printlnE("Reasoning part error:" + e.printString());
			}
		} catch (DashException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("DASHエラー:" + e.printString());
			} else {
				printlnE("DASH error:" + e.printString());
			}
		}

		// 最初の起動。
		boolean notend = true;
		try {
			notend = prodsys.start(null);
		} catch (PSException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("推論部エラー:" + e.printString());
			} else {
				printlnE("Reasoning part error:" + e.printString());
			}
		} catch (DashException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("DASHエラー:" + e.printString());
			} else {
				printlnE("DASH error:" + e.printString());
			}
		}

		// メッセージ・イベント待ちループ
		while (notend) {
			DashMessage msg = waitMsg();
			//add uchiya
			//delアイコン使用時のkill処理（追加）
			if (killflag) {
				//System.out.println(name+"でkillflagなのでエージェントを終了します");
				break;
			} else if (msg == null) {
				break;
			}
			// NewifからのKillの処理(旧)
			if (msg.performative.equals(DashMessage.KILLFORCE)
				&& msg.from.equals(DashMessage.IF)
				&& msg.content == null) {
				//System.out.println(name+"にkillforceメッセージが届いてます");
				break;
			}
			OAVdata oav = OAVdata.fromString(msg.toString(), false);
			try {
				if (msg.performative.equals(DashMessage.DEDUCTIONSTART)) {
					// added by takagaki
					notend = prodsys.start(null); // added by takagaki
				} // added by takagaki
				else { // modified by takagaki
					notend = prodsys.start(oav);
				} // added by takagaki
			} catch (PSException e) {
				if (System.getProperty("language").equals("japanese")) {
					printlnE("推論部エラー:" + e.printString());
				} else {
					printlnE("Reasoning part error:" + e.printString());
				}
			} catch (DashException e) {
				if (System.getProperty("language").equals("japanese")) {
					printlnE("DASHエラー:" + e.printString());
				} else {
					printlnE("DASH error:" + e.printString());
				}
			}
		}
		// 終了処理
		if (baseProcess != null) {
			baseProcess.finalizeBP(); // ベースプロセスを終了させる。
			baseProcess = null;
		}
		prodsys.stopPrep(); // 終了準備
		prodsys.stop();
		prodsys = null;

		//System.out.println(name+"スレッド終了");
		if (!killflag)
			dvm.killAgent(name, yuigon); // DVMが持つ情報を消す。
	}

	/**
	 * メッセージキューのメッセージを取り出し、返す。
	 * キューが空の場合、メッセージが来るまでブロックする。
	 */
	DashMessage waitMsg() {
		synchronized (msgQueue) {
			if (msgQueue.isEmpty())
				try {
					msgQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    //add uchiya
		    //delアイコン使用時にkillflag=trueになる
			if (killflag) {
				return null;
			}
		}
		return (DashMessage) msgQueue.remove(0);
	}

	/**
	 * メッセージキューのメッセージIDがinReplyToであるメッセージを取り出し返す。
	 * キューが空の場合、メッセージが来るまでブロックする。
	 */
	DashMessage waitMsg(long inReplyTo) {
		DashMessage msg = null;
		synchronized (msgQueue) {
			Loop : while (true) {
				try {
					if (msgQueue.isEmpty())
						msgQueue.wait();
					for (int i = 0; i < msgQueue.size(); i++) {
						msg = (DashMessage) msgQueue.elementAt(i);
						if (msg.inReplyTo == inReplyTo) {
							msgQueue.removeElementAt(i);
							break Loop;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return msg;
	}

	/**
	 * メッセージキューのメッセージを全て取り出し、OAVdataに変換し、
	 * Vectorとして返す。
	 * メッセージがない場合、nullを返す。
	 */
	public Vector fetchAll() {
		synchronized (msgQueue) {
			if (msgQueue.isEmpty())
				return null;
			Vector v = new Vector();
			while (!msgQueue.isEmpty()) {
				DashMessage msg = (DashMessage) msgQueue.remove(0);
				OAVdata oav = OAVdata.fromString(msg.toString(), false);
				v.add(oav);
			}
			return v;
		}
	}

	/**
	 * メッセージをメッセージキューに入れる。
	 * このメソッドは、
	 * ・dash.DVM.run()を実行するスレッドと、
	 * ・dash.DashAgent.raiseEvent()を実行するスレッドと、
	 * ・dash.AlarmManager#Alarm.run()を実行するスレッド
	 * で実行される。
	 */
	void putMsg(DashMessage msg) {
		synchronized (msgQueue) {
			msgQueue.addElement(msg);
			msgQueue.notify();
		}
	}

	/**
	 * メッセージを他のエージェントに送信する。
	 */
	String sendMsg(OAVdata oav) {
		return dvm.sendMessageFromAgent(oav, name);
	}

	/**
	 * アクションinstantiateの実装。
	 * インスタンシエートする。
	 * インスタンシエート先のDVMに_createInstanceを送り、
	 * _createdInstanceが返るのを待つ。
	 */
	public String instantiate(OAVdata oav) {
		if (oav.getValue(":into") != null && dvm.isRtype())
			return instantiateInto(oav); // (instantiate :into       :fact)
		else if (!dvm.isRtype())
			return instantiateFrom(oav); // (instantiate :from :name :fact)
		else
			return "FALSE";
	}

	/**
	 * リポジトリエージェントのアクション(instantiate :into :fact)を実行する。
	 * インスタンシエート先のDVMに_createInstanceを送り、
	 * _createdInstanceが返るのを待つ。
	 * @see move()
	 */
	private String instantiateInto(OAVdata oav) {
		// 送る
		long inReplyTo =
			dvm.instantiateAgent(oav, name, script, filename, origin);

		// 待つ
		DashMessage msg = waitMsg(inReplyTo); // _createdInstance

		// チェック
		String result = null;
		if (msg.isNoSuchAgentOrDvm())
			result = "FALSE";
		else {
			OAVdata reply = OAVdata.fromString(msg.toString(), false);
			TafList list = (TafList) reply.getValue(":content");
			TafString tafstr = (TafString) list.getValue(0);
			result = tafstr.getString();
		}
		return result;
	}

	/**
	 * ワークプレースエージェントのアクション
	 * (instantiate :from :name :fact)を実行する。
	 * リポジトリに_instanteateを送り、
	 * _createInstanceが返るのを待つ。
	 */
	private String instantiateFrom(OAVdata oav) {
		// 送る
		long inReplyTo = dvm.sendInstantiate(oav, name);

		// 待つ
		DashMessage msg = waitMsg(inReplyTo); // _createInstance
		if (msg.isNoSuchAgentOrDvm())
			return "FALSE";

		// 作る
		String cname = oav.getValueString(":name");
		String description = msg.getOtherAttributes(":description");
		String filename = msg.getOtherAttributes(":filename");
		String facts = oav.getValueString(":facts").toString();
		String origin = msg.getOtherAttributes(":origin");
		String newname =
			dvm.createAgent(
				cname,
				description,
				filename,
				facts,
				origin,
				null,
				null,
				null);

		return newname;
	}

	/**
	 * アクションmoveの実装
	 * @param dvmname 移動先DVM名
	 * @see instantiateInto()
	 */
	public boolean move(String dvmname) {
		// 移動先が同じ環境なら、移動しない。
		if (dvmname.equals(dvm.getDVMname())) {
			println(name + ": move failed(same env. dest=" + dvmname + ".)");
			return false;
		}

		// 移動先が存在しないなら、移動しない。
		if (!dvm.checkDVM(dvmname)) {
			println(name + ": move failed(not exists. dest=" + dvmname + ".)");
			return false;
		}

		// 送る
		long inReplyTo =
			dvm.moveAgent(
				dvmname,
				cname,
				name,
				prodsys.getProgram(),
				prodsys.getWorkMemForMove(),
				filename,
				origin);

		// 待つ
		DashMessage msg = waitMsg(inReplyTo); // _createdInstance

		// チェック
		boolean result = !msg.isNoSuchAgentOrDvm();
		if (result)
			yuigon = msg;

		return result;
	}

	String getScript() {
		return script;
	}

	String getFilename() {
		return filename;
	}

	String getOrigin() {
		return origin;
	}

	/** インスペクタを開く */
	void inspect() {
		prodsys.showInspector();
	}

	/** インスペクタを開く */
	void showInspectorStop(int lineno, boolean nonstop) {
		prodsys.showInspectorStop(lineno, nonstop);
	}

	/** 表示 */
	void println(String string) {
		dvm.println(string);
		prodsys.println(string);
	}

	/** 表示 */
	void printlnE(String string) {
		dvm.printlnE(name + ":" + string);
		prodsys.println(string);
	}

	/** 名前を返す。*/
	String getName() {
		return name;
	}

	///////////////////////////////////////////////////////////////////////////

	public String call(String method, String arg[]) {
		System.out.println("(call) Not yet.");
		return null;
	}

	public void scall(String method, Object arg[]) {
		System.out.println("(scall) Not yet.");
	}

	/**
	 * controlの実装(ガワ)
	 * @param method メソッド名
	 * @param args 引数
	 */
	public Object control(String method, Object[] args) {
		if (baseProcess == null) {
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10, "ベースプロセスがloadBPされてません。");
			} else {
				throw new PSException(
					-10,
					"A base process is loadBP and it does not become precocious.");
			}
		}

		if (virtual_bp) { // added by takagaki
			Object[] method_args = new Object[args.length + 1];
			// added by takagaki
			method_args[0] = method; // added by takagaki
			for (int i = 0; i < args.length; i++) { // added by takagaki
				method_args[i + 1] = args[i]; // added by takagaki
			} // added by takagaki
			return methodImpl(baseProcess, "encourage", method_args);
			// added by takagaki
		} // added by takagaki
		else { // added by takagaki
			return methodImpl(baseProcess, method, args);
		} // added by takagaki
	}

	/**
	 * methodの実装(ガワ)
	 */
	public Object method(String classname, String method, Object[] arg) {
 		
		Class targetClass = null;
		try {
			//targetClass = dvm.classForName(classname);
			targetClass = classLoader.loadClass(classname);
		} catch (Exception e) {
			e.printStackTrace();
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10, "クラス" + classname + "のロードに失敗。");
			} else {
				throw new PSException(
					-10,
					"--Loading of " + classname + " goes wrong.");
			}
		}

		Object target = null;
		try {
			target = targetClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10, "クラス" + classname + "のインスタンス生成に失敗。");
			} else {
				throw new PSException(
					-10,
					"Instance generation of " + classname + " goes wrong.");
			}
		}
		//add uchiya
		if(target.getClass().getName().equals("dash.DammyBP")){
			if (System.getProperty("language").equals("japanese")) {
						throw new PSException(-10, "クラス" + classname + "のインスタンス生成に失敗。");
					} else {
						throw new PSException(
							-10,
							"Instance generation of " + classname + " goes wrong.");
					}
		};

		return methodImpl(target, method, arg);
	}

	/**
	 * control, methodの実装
	 */
	private Object methodImpl(Object target, String method, Object arg[]) {
		//System.out.println("targetclass="+target);
		
		Object result;

		Class[] tmpParams = null;
		if (arg.length == 0) {
			tmpParams = new Class[0];
		} else {
			tmpParams = new Class[1];
			tmpParams[0] =
				Array
					.newInstance((new Object()).getClass(), new int[1])
					.getClass();
		}
		Class targetClass = target.getClass();
		//System.out.println("targetclass="+targetClass);
		Method targetMethod = null;
		try {
			targetMethod = targetClass.getMethod(method, tmpParams);
		} catch (Exception e) {
			String param = null;
			if (arg.length == 0)
				param = "()";
			else
				param = "(java.lang.Object[])";
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(
					-10,
					"クラス"
						+ targetClass.getName()
						+ "のメソッド"
						+ method
						+ param
						+ "が見つかりません。");
			} else {
				throw new PSException(
					-10,
					"not found method ("
						+ targetClass.getName()
						+ "--"
						+ method
						+ param
						+ ")");

			}
		}

		Object[] params = null;
		if (arg.length == 0) {
			params = new Object[0];
		} else {
			params = new Object[1];
			params[0] = arg;
		}
		try {
			result = targetMethod.invoke(target, params);
		} catch (Exception e) {
			Throwable th = null;
			if (e instanceof java.lang.reflect.InvocationTargetException) {
				th =
					((java.lang.reflect.InvocationTargetException) e)
						.getCause();
			} else
				th = e;
			th.printStackTrace();
			String param = null;
			if (arg.length == 0)
				param = "()";
			else
				param = "(java.lang.Object[])";
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(
					-10,
					"クラス"
						+ targetClass.getName()
						+ "のメソッド"
						+ method
						+ param
						+ "の呼び出し中に例外"
						+ th.toString()
						+ "が発生しました。");
			} else {
				throw new PSException(
					-10,
					"method error("
						+ targetClass.getName()
						+ "--"
						+ method
						+ param
						+ ")");
			}
		}

		return result;
	}

	/**
	 * メッセージを送信する。
	 * @param oav sendアクション。(send :performative ...)など。
	 */
	public String send(OAVdata oav) {
		return sendMsg(oav);
	}

	/**
	 * メッセージを返信する。
	 * @param oav replyアクション。
	 */
	public String reply(OAVdata oav) {
		return sendMsg(oav);
	}

	/**
	 * メッセージをブロードキャストする。
	 * @param oav broadcastアクション。
	 */
	public String broadcast(OAVdata oav) {
		return sendMsg(oav);
	}

	/**
	 * 現在の時刻を取得する。
	 * @return "12:34:56"という形式のString。24時間制。
	 */
	public final String currentTime() {
		// 現在時刻(ミリ秒)
		Date date = new Date();

		// 12:34:56のミリ秒
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		int s = calendar.get(Calendar.SECOND);
		//add uchiya
		int millisecond = calendar.get(Calendar.MILLISECOND);

		String hour = ((h < 10) ? "0" : "") + h;
		String min = ((m < 10) ? "0" : "") + m;
		String sec = ((s < 10) ? "0" : "") + s;

		return hour + ":" + min + ":" + sec+ ":" + millisecond;
	}

	public boolean exists(String agname) {
		return dvm.hasAgent(agname);
	}

	/** 標準出力(環境モニタ)とインスペクタに文字列を表示する */
	public void print(String string) {
		println(string);
	}

	/** 環境モニタに文字を表示する。*/
	public void settext(String string) {
		dvm.settextOnNewif(name, string);
	}

	/** インスペクタを開く */
	public void inspect(int lineno, boolean nonstop) {
		showInspectorStop(lineno, nonstop);
	}

	/**
	 * ベースプロセスのインスタンスを生成する。
	 * 成功した場合、trueを返す。
	 * 失敗した場合、PSExceptionを上げる。
	 */
	public boolean loadBP(String classname) {
		if (baseProcess != null) {
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10,"既にクラス"
						+ baseProcess.getClass().getName()
						+ "がloadBPされています");
			} else {
				throw new PSException(
					-10,
					"It has already loadBPed)."
						+ baseProcess.getClass().getName());
			}
		}
		try {
			Class bpClass = null;
			virtual_bp = false; // added by takagaki
			try {
				bpClass = classLoader.loadClass(classname);	
				if (!classname.equals("dash.DammyBP") && bpClass.getName().equals
				("baseProcess.dashSample.DammyBP")) 
				 // added by takagaki
			  		virtual_bp = true; // added by takagaki
			//	} // added by takagaki
			} catch (ClassNotFoundException ee) {
				
				//uchiya
				//bpClass = classLoader.loadClass("dash.DammyBP");
				//virtual_bp = true; // added by takagaki
			}
			baseProcess = (DashBP) bpClass.newInstance(); // インスタンス生成
			baseProcess.setAgent(this); // エージェントセット
		} catch (Exception e) {
			String msg = "(loadBP " + classname + ")失敗！";
			if (e instanceof ClassNotFoundException) {
				if (System.getProperty("language").equals("japanese")) {
					msg += "(クラス" + classname + "のクラスファイルが見つかりません)";
				} else {
					msg += "(The class file of "
						+ classname
						+ " is not found.)";
				}
			} else if (e instanceof InstantiationException) {
				if (System.getProperty("language").equals("japanese")) {
					msg += "(クラス" + classname + "をインスタンシエートできません)";
				} else {
					msg += "(instantiate error -" + classname + ")";
				}
			} else if (e instanceof IllegalAccessException) {
				if (System.getProperty("language").equals("japanese")) {
					msg += "(クラス" + classname + "がpublicではありません)";
				} else {
					msg += "(" + classname + " is not public.)";
				}
			} else if (e instanceof ClassCastException)
				msg += "(クラス" + classname + "は「implements DashBP」してません)";
			else
				msg += "(その他)";
			throw new PSException(-10, msg);
		}
		return true;
	}

	/**
	 * ベースプロセスのスレッドを開始する。
	 * スレッド名は、"BP:Javaクラス名:エージェント名"
	 * 成功した場合、trueを返す。
	 * 失敗した場合、PSExceptionを上げる。
	 */
	public boolean startBP() {
		if (bpThread != null) {
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(
					-10,
					"既にクラス"
						+ baseProcess.getClass().getName()
						+ "がstartBPされています");
			} else {
				throw new PSException(
					-10,
					"It has already startBPed.-"
						+ baseProcess.getClass().getName()
						+ "");

			}
		}

		bpThread =
			new Thread(
				baseProcess,
				"BP:" + baseProcess.getClass() + ":" + getName());
		bpThread.start();
		return true;
	}

	/**
	 * selectorで指定されたエージェントの情報を返す。
	 * @param selector 一つの要素は、String[2]。
	 * [0]は、条件指定子。":name", ":environment"など。
	 * [1]は、その属性値。
	 * @return マッチするものがない場合、空のVector。
	 * マッチした場合、String[9]を要素とするVector。
	 * ネームサーバを使わない場合や、
	 * 指定したネームサーバが存在しないか、このホストでネームサーバが
	 * 起動していない場合、null。
	 * 配列の添字の意味については、NSInterface参照。
	 */
	public Vector lookup(String[][] selector) {
		Vector result = dvm.lookup(selector);
		if (result == null) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("アクションlookupの実行に失敗しました。");
			} else {
				printlnE("Execution of Action lookup went wrong.");
			}
		}
		return result;
	}

	/**
	 * rulesetで指定したルールセットをアクティブにする。
	 * 存在しないルールセットを指定した場合はエラーとなる。
	 * @return 成功した場合、null。
	 * 失敗した場合、現在利用できるルールセット。
	 */
	public String[] activateRuleset(String[] ruleset) {
		return prodsys.activateRuleset(ruleset);
	}

	/**
	 * fileで指定したファイルに記述されたルールセットを読みこむ。
	 * @param filename ファイル名
	 * @param envname 環境名
	 * @param letter r, w, rw, wrのどれか
	 * @param lineno 行番号
	 * @return 成功した場合、読みこんだルールセット名。
	 * 失敗した場合、null。
	 */
	public String includeRuleset(
		String filename,
		String envname,
		String letter,
		int lineno) {
		return prodsys.includeRuleset(
			filename,
			envname,
			letter,
			dvm,
			origin,
			lineno);
	}

	/**
	 * アラームをセットしたりキャンセルしたりする。
	 * @param type ":cancel", ":after", ":every"のいずれか
	 * @param msec ":cancel"の場合、0。
	 *             ":after", ":every"の場合、ミリ秒を表す文字列。
	 * @param arg ":cancel"の場合"all", "alarm*"。
	 *            ":after", ":every"の場合、OAVデータ。
	 */
	public String[] setAlarm(String type, long msec, String arg) {
		String[] array;
		if (type.equals(":cancel")) {
			array = dvm.cancelAlarm(getName(), arg);
		} else {
			array = new String[1];
			boolean repeat = type.equals(":every");
			array[0] = dvm.setAlarm(getName(), repeat, msec, arg);
		}
		return array;
	}

	/**
	 * ベースプロセスからのイベントを受け取り、メッセージキューにいれる。
	 * 改行が含まれる場合、:contentは各行のリストとなる。
	 * 改行が含まれない場合、
	 * 両端に()を付けたものがOAVデータまたはリスト形式にならない場合は、
	 * 全体を""で囲った文字列を要素にもつリストが:contentになる。
	 * @see ps.Action.resultConv()
	 */
	public void raiseEvent(String string) {
		String content = null;
		if (string.indexOf('\n') >= 0) {
			StringTokenizer st = new StringTokenizer(string, "\n", true);
			StringBuffer buf = new StringBuffer();
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (s.equals("\n")) {
					buf.append("\"\" ");
					continue;
				} else {
					buf.append("\"");
					buf.append(s);
					buf.append("\" ");
					if (st.hasMoreTokens())
						st.nextToken(); // 改行
				}
			}
			content = "(" + buf.toString() + ")";
		} else {
			try {
				content = "(" + string + ")";
				OAVdata test =
					OAVdata.fromString(
						"(Test :content " + content + ")",
						false);
			} catch (SyntaxException e) {
				System.err.println(e);
				e.printStackTrace();
				content = "(\"" + TafString.convertMeta(string) + "\")";
			}
		}
		DashMessage event = new DashMessage(content, true);
		putMsg(event);
	}

	/**
	 * 通知されたアラームoavを(Msg :content alarm)形式で
	 * メッセージキューにいれる。
	 */
	void notifyAlarm(String id, String oav) {
		DashMessage alarm = new DashMessage(oav, false);
		alarm.from = id;
		putMsg(alarm);
	}

	public String getAgentName() {
		return getName();
	}

	/**
	 * uchiya
	 */
	boolean killflag = false;
	
	public void killForce() {
		synchronized (msgQueue) {
			killflag = true;
			msgQueue.notify();
		}
	}

	public void sendStartMessage() {
		putMsg(new DashMessage());
	}

}
