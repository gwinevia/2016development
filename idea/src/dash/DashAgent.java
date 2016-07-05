package dash;

import ps.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * DASH�̃G�[�W�F���g����������N���X�B
 * ��DASH�ł����Ƃ����CM_J & TPM�B
 */
public class DashAgent implements Runnable, ps.Controller {

	public Clipman cm = null;
	public Pipeman pm = null;
	/** DVM */
	//private DVM dvm;
	// COSMOS public�ɕύX
	public DVM dvm;

	/** �G�[�W�F���g�̃N���X��(�t�@�C�����Ȃ�) */
	private String cname;

	/** �G�[�W�F���g�̏o�g */
	private String origin;

	/** �G�[�W�F���g�� */
	private String name;

	/** �X�N���v�g */
	private String script;

	/** �t�@�C���� */
	private String filename;

	/** �v���_�N�V�����V�X�e�� */
	//private ProdSys prodsys;
	public ProdSys prodsys;

	/** �G�[�W�F���g�̃X���b�h */
	private Thread thread;

	/** ���b�Z�[�W�L���[ */
	private Vector msgQueue;

	/** �x�[�X�v���Z�X(�Ȃ��ꍇ��null) */
	//private DashBP baseProcess;
	public DashBP baseProcess;

	/** �x�[�X�v���Z�X�����s����X���b�h(�Ȃ��ꍇ��null) */
	private Thread bpThread;

	/**
	 * �N���X���[�_�B�x�[�X�v���Z�X�⃁�\�b�h��ǂݍ��ށB
	 * �o�C�g�R�[�h�́A�ϐ�origin�̒l�Ƃ��Ċi�[����Ă���DVM����ǂݍ��܂��B
	 * �Ⴆ�΁A���|�W�g�����琶�������ꍇ�̓��|�W�g������ǂݍ��ށB
	 */
	private ClassLoader classLoader;

	/**
	 * �ړ�������ňړ����DVM���痈��_createdInstance
	 */
	private DashMessage yuigon;

	/** �ړ��G�[�W�F���g�Ȃ�(���ł��ړ������Ȃ�)true */
	private boolean moved;

	private boolean virtual_bp; // added by takagaki
	/**
	 * �R���X�g���N�^
	 * @param cname �G�[�W�F���g�̃N���X��
	 * @param name DVM�����߂��G�[�W�F���g��
	 * @param script �X�N���v�g "(agent .. �` )"�B
	 * �ړ����ė����G�[�W�F���g�Ȃ�null�B
	 * @param filename �f�B���N�g�����܂܂Ȃ��t�@�C���� "Sample.dash"
	 * @param dvm DVM
	 * @param facts 0�ȏ��OAV�f�[�^��v�f�Ɏ����X�g
	 * @param origin �i�[����Ă������|�W�g�������邢�͍ŏ��ɐ������ꂽ���[�N�v���[�X���B
	 * @param program �ړ����ė����G�[�W�F���g�Ȃ�AgentProgram�B
	 * �����łȂ��Ȃ�null�B
	 * @param workmem �ړ����ė����G�[�W�F���g�Ȃ�WorkMem�B
	 * �����łȂ��Ȃ�null�B
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
		// (Rule-set ...)���쐬����
		prodsys.updateRulesetFact();

		// �ÓIinclude�̎��s(�ړ���ł͂Ȃ��ꍇ)�B
		try {
			if (!moved)
				prodsys.includeRuleset(dvm, origin);
		} catch (PSException e) {
			result = false;
			if (System.getProperty("language").equals("japanese")) {
				printlnE("���_���G���[:" + e.printString());
			} else {
				printlnE("Reasoning part error:" + e.printString());
			}
		} catch (DashException e) {
			result = false;
			if (System.getProperty("language").equals("japanese")) {
				printlnE("DASH�G���[:" + e.printString());
			} else {
				printlnE("DASH error:" + e.printString());
			}
		}

	}

	/**
	 * ���[�L���O�������ɁA
	 * (Status ...)�A(Members ...)�A(Msg ...)��
	 * 3�̃t�@�N�g�����ɒǉ�����B
	 * ���̌�Afacts�Ŏw�肳�ꂽ�t�@�N�g��ǉ�����B
	 * facts��
	 * (���[���Z�b�g�� OAV OAV ... ���[���Z�b�g�� OAV OAV ...)
	 * @param facts �C���X�^���V�G�[�g���Ɏw�肳��邱�Ƃ�����B
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
	 * (property ..)����A
	 * �@�\��(function :word �@�\��)��(comment :text �R�����g)��T���A
	 * [0]�ɋ@�\�����A[1]�ɃR�����g���Z�b�g���ĕԂ��B
	 * �L�q����Ă��Ȃ��ꍇ�A""���Z�b�g����B
	 */
	public String[] getFunctionAndComment() {
		return prodsys.getFunctionAndComment();
	}

	/**
	 * �G�[�W�F���g�̓�����J�n����B
	 */
	void startAgent(ThreadGroup threadGroup) {
		thread = new Thread(threadGroup, this, name);
		prodsys.println("***** Thread Start");
		thread.start();
	}

	/**
	 * �G�[�W�F���g�̃��C�����[�v�B
	 */
	public boolean result = true;
	public void run() {
		//result = true;
		// (Rule-set ...)���쐬����
		prodsys.updateRulesetFact();

		// �ÓIinclude�̎��s(�ړ���ł͂Ȃ��ꍇ)�B
		try {
			if (!moved)
				prodsys.includeRuleset(dvm, origin);
		} catch (PSException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("���_���G���[:" + e.printString());
			} else {
				printlnE("Reasoning part error:" + e.printString());
			}
		} catch (DashException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("DASH�G���[:" + e.printString());
			} else {
				printlnE("DASH error:" + e.printString());
			}
		}

		// �ŏ��̋N���B
		boolean notend = true;
		try {
			notend = prodsys.start(null);
		} catch (PSException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("���_���G���[:" + e.printString());
			} else {
				printlnE("Reasoning part error:" + e.printString());
			}
		} catch (DashException e) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("DASH�G���[:" + e.printString());
			} else {
				printlnE("DASH error:" + e.printString());
			}
		}

		// ���b�Z�[�W�E�C�x���g�҂����[�v
		while (notend) {
			DashMessage msg = waitMsg();
			//add uchiya
			//del�A�C�R���g�p����kill�����i�ǉ��j
			if (killflag) {
				//System.out.println(name+"��killflag�Ȃ̂ŃG�[�W�F���g���I�����܂�");
				break;
			} else if (msg == null) {
				break;
			}
			// Newif�����Kill�̏���(��)
			if (msg.performative.equals(DashMessage.KILLFORCE)
				&& msg.from.equals(DashMessage.IF)
				&& msg.content == null) {
				//System.out.println(name+"��killforce���b�Z�[�W���͂��Ă܂�");
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
					printlnE("���_���G���[:" + e.printString());
				} else {
					printlnE("Reasoning part error:" + e.printString());
				}
			} catch (DashException e) {
				if (System.getProperty("language").equals("japanese")) {
					printlnE("DASH�G���[:" + e.printString());
				} else {
					printlnE("DASH error:" + e.printString());
				}
			}
		}
		// �I������
		if (baseProcess != null) {
			baseProcess.finalizeBP(); // �x�[�X�v���Z�X���I��������B
			baseProcess = null;
		}
		prodsys.stopPrep(); // �I������
		prodsys.stop();
		prodsys = null;

		//System.out.println(name+"�X���b�h�I��");
		if (!killflag)
			dvm.killAgent(name, yuigon); // DVM�������������B
	}

	/**
	 * ���b�Z�[�W�L���[�̃��b�Z�[�W�����o���A�Ԃ��B
	 * �L���[����̏ꍇ�A���b�Z�[�W������܂Ńu���b�N����B
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
		    //del�A�C�R���g�p����killflag=true�ɂȂ�
			if (killflag) {
				return null;
			}
		}
		return (DashMessage) msgQueue.remove(0);
	}

	/**
	 * ���b�Z�[�W�L���[�̃��b�Z�[�WID��inReplyTo�ł��郁�b�Z�[�W�����o���Ԃ��B
	 * �L���[����̏ꍇ�A���b�Z�[�W������܂Ńu���b�N����B
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
	 * ���b�Z�[�W�L���[�̃��b�Z�[�W��S�Ď��o���AOAVdata�ɕϊ����A
	 * Vector�Ƃ��ĕԂ��B
	 * ���b�Z�[�W���Ȃ��ꍇ�Anull��Ԃ��B
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
	 * ���b�Z�[�W�����b�Z�[�W�L���[�ɓ����B
	 * ���̃��\�b�h�́A
	 * �Edash.DVM.run()�����s����X���b�h�ƁA
	 * �Edash.DashAgent.raiseEvent()�����s����X���b�h�ƁA
	 * �Edash.AlarmManager#Alarm.run()�����s����X���b�h
	 * �Ŏ��s�����B
	 */
	void putMsg(DashMessage msg) {
		synchronized (msgQueue) {
			msgQueue.addElement(msg);
			msgQueue.notify();
		}
	}

	/**
	 * ���b�Z�[�W�𑼂̃G�[�W�F���g�ɑ��M����B
	 */
	String sendMsg(OAVdata oav) {
		return dvm.sendMessageFromAgent(oav, name);
	}

	/**
	 * �A�N�V����instantiate�̎����B
	 * �C���X�^���V�G�[�g����B
	 * �C���X�^���V�G�[�g���DVM��_createInstance�𑗂�A
	 * _createdInstance���Ԃ�̂�҂B
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
	 * ���|�W�g���G�[�W�F���g�̃A�N�V����(instantiate :into :fact)�����s����B
	 * �C���X�^���V�G�[�g���DVM��_createInstance�𑗂�A
	 * _createdInstance���Ԃ�̂�҂B
	 * @see move()
	 */
	private String instantiateInto(OAVdata oav) {
		// ����
		long inReplyTo =
			dvm.instantiateAgent(oav, name, script, filename, origin);

		// �҂�
		DashMessage msg = waitMsg(inReplyTo); // _createdInstance

		// �`�F�b�N
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
	 * ���[�N�v���[�X�G�[�W�F���g�̃A�N�V����
	 * (instantiate :from :name :fact)�����s����B
	 * ���|�W�g����_instanteate�𑗂�A
	 * _createInstance���Ԃ�̂�҂B
	 */
	private String instantiateFrom(OAVdata oav) {
		// ����
		long inReplyTo = dvm.sendInstantiate(oav, name);

		// �҂�
		DashMessage msg = waitMsg(inReplyTo); // _createInstance
		if (msg.isNoSuchAgentOrDvm())
			return "FALSE";

		// ���
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
	 * �A�N�V����move�̎���
	 * @param dvmname �ړ���DVM��
	 * @see instantiateInto()
	 */
	public boolean move(String dvmname) {
		// �ړ��悪�������Ȃ�A�ړ����Ȃ��B
		if (dvmname.equals(dvm.getDVMname())) {
			println(name + ": move failed(same env. dest=" + dvmname + ".)");
			return false;
		}

		// �ړ��悪���݂��Ȃ��Ȃ�A�ړ����Ȃ��B
		if (!dvm.checkDVM(dvmname)) {
			println(name + ": move failed(not exists. dest=" + dvmname + ".)");
			return false;
		}

		// ����
		long inReplyTo =
			dvm.moveAgent(
				dvmname,
				cname,
				name,
				prodsys.getProgram(),
				prodsys.getWorkMemForMove(),
				filename,
				origin);

		// �҂�
		DashMessage msg = waitMsg(inReplyTo); // _createdInstance

		// �`�F�b�N
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

	/** �C���X�y�N�^���J�� */
	void inspect() {
		prodsys.showInspector();
	}

	/** �C���X�y�N�^���J�� */
	void showInspectorStop(int lineno, boolean nonstop) {
		prodsys.showInspectorStop(lineno, nonstop);
	}

	/** �\�� */
	void println(String string) {
		dvm.println(string);
		prodsys.println(string);
	}

	/** �\�� */
	void printlnE(String string) {
		dvm.printlnE(name + ":" + string);
		prodsys.println(string);
	}

	/** ���O��Ԃ��B*/
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
	 * control�̎���(�K��)
	 * @param method ���\�b�h��
	 * @param args ����
	 */
	public Object control(String method, Object[] args) {
		if (baseProcess == null) {
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10, "�x�[�X�v���Z�X��loadBP����Ă܂���B");
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
	 * method�̎���(�K��)
	 */
	public Object method(String classname, String method, Object[] arg) {
 		
		Class targetClass = null;
		try {
			//targetClass = dvm.classForName(classname);
			targetClass = classLoader.loadClass(classname);
		} catch (Exception e) {
			e.printStackTrace();
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10, "�N���X" + classname + "�̃��[�h�Ɏ��s�B");
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
				throw new PSException(-10, "�N���X" + classname + "�̃C���X�^���X�����Ɏ��s�B");
			} else {
				throw new PSException(
					-10,
					"Instance generation of " + classname + " goes wrong.");
			}
		}
		//add uchiya
		if(target.getClass().getName().equals("dash.DammyBP")){
			if (System.getProperty("language").equals("japanese")) {
						throw new PSException(-10, "�N���X" + classname + "�̃C���X�^���X�����Ɏ��s�B");
					} else {
						throw new PSException(
							-10,
							"Instance generation of " + classname + " goes wrong.");
					}
		};

		return methodImpl(target, method, arg);
	}

	/**
	 * control, method�̎���
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
					"�N���X"
						+ targetClass.getName()
						+ "�̃��\�b�h"
						+ method
						+ param
						+ "��������܂���B");
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
					"�N���X"
						+ targetClass.getName()
						+ "�̃��\�b�h"
						+ method
						+ param
						+ "�̌Ăяo�����ɗ�O"
						+ th.toString()
						+ "���������܂����B");
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
	 * ���b�Z�[�W�𑗐M����B
	 * @param oav send�A�N�V�����B(send :performative ...)�ȂǁB
	 */
	public String send(OAVdata oav) {
		return sendMsg(oav);
	}

	/**
	 * ���b�Z�[�W��ԐM����B
	 * @param oav reply�A�N�V�����B
	 */
	public String reply(OAVdata oav) {
		return sendMsg(oav);
	}

	/**
	 * ���b�Z�[�W���u���[�h�L���X�g����B
	 * @param oav broadcast�A�N�V�����B
	 */
	public String broadcast(OAVdata oav) {
		return sendMsg(oav);
	}

	/**
	 * ���݂̎������擾����B
	 * @return "12:34:56"�Ƃ����`����String�B24���Ԑ��B
	 */
	public final String currentTime() {
		// ���ݎ���(�~���b)
		Date date = new Date();

		// 12:34:56�̃~���b
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

	/** �W���o��(�����j�^)�ƃC���X�y�N�^�ɕ������\������ */
	public void print(String string) {
		println(string);
	}

	/** �����j�^�ɕ�����\������B*/
	public void settext(String string) {
		dvm.settextOnNewif(name, string);
	}

	/** �C���X�y�N�^���J�� */
	public void inspect(int lineno, boolean nonstop) {
		showInspectorStop(lineno, nonstop);
	}

	/**
	 * �x�[�X�v���Z�X�̃C���X�^���X�𐶐�����B
	 * ���������ꍇ�Atrue��Ԃ��B
	 * ���s�����ꍇ�APSException���グ��B
	 */
	public boolean loadBP(String classname) {
		if (baseProcess != null) {
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(-10,"���ɃN���X"
						+ baseProcess.getClass().getName()
						+ "��loadBP����Ă��܂�");
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
			baseProcess = (DashBP) bpClass.newInstance(); // �C���X�^���X����
			baseProcess.setAgent(this); // �G�[�W�F���g�Z�b�g
		} catch (Exception e) {
			String msg = "(loadBP " + classname + ")���s�I";
			if (e instanceof ClassNotFoundException) {
				if (System.getProperty("language").equals("japanese")) {
					msg += "(�N���X" + classname + "�̃N���X�t�@�C����������܂���)";
				} else {
					msg += "(The class file of "
						+ classname
						+ " is not found.)";
				}
			} else if (e instanceof InstantiationException) {
				if (System.getProperty("language").equals("japanese")) {
					msg += "(�N���X" + classname + "���C���X�^���V�G�[�g�ł��܂���)";
				} else {
					msg += "(instantiate error -" + classname + ")";
				}
			} else if (e instanceof IllegalAccessException) {
				if (System.getProperty("language").equals("japanese")) {
					msg += "(�N���X" + classname + "��public�ł͂���܂���)";
				} else {
					msg += "(" + classname + " is not public.)";
				}
			} else if (e instanceof ClassCastException)
				msg += "(�N���X" + classname + "�́uimplements DashBP�v���Ă܂���)";
			else
				msg += "(���̑�)";
			throw new PSException(-10, msg);
		}
		return true;
	}

	/**
	 * �x�[�X�v���Z�X�̃X���b�h���J�n����B
	 * �X���b�h���́A"BP:Java�N���X��:�G�[�W�F���g��"
	 * ���������ꍇ�Atrue��Ԃ��B
	 * ���s�����ꍇ�APSException���グ��B
	 */
	public boolean startBP() {
		if (bpThread != null) {
			if (System.getProperty("language").equals("japanese")) {
				throw new PSException(
					-10,
					"���ɃN���X"
						+ baseProcess.getClass().getName()
						+ "��startBP����Ă��܂�");
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
	 * selector�Ŏw�肳�ꂽ�G�[�W�F���g�̏���Ԃ��B
	 * @param selector ��̗v�f�́AString[2]�B
	 * [0]�́A�����w��q�B":name", ":environment"�ȂǁB
	 * [1]�́A���̑����l�B
	 * @return �}�b�`������̂��Ȃ��ꍇ�A���Vector�B
	 * �}�b�`�����ꍇ�AString[9]��v�f�Ƃ���Vector�B
	 * �l�[���T�[�o���g��Ȃ��ꍇ��A
	 * �w�肵���l�[���T�[�o�����݂��Ȃ����A���̃z�X�g�Ńl�[���T�[�o��
	 * �N�����Ă��Ȃ��ꍇ�Anull�B
	 * �z��̓Y���̈Ӗ��ɂ��ẮANSInterface�Q�ƁB
	 */
	public Vector lookup(String[][] selector) {
		Vector result = dvm.lookup(selector);
		if (result == null) {
			if (System.getProperty("language").equals("japanese")) {
				printlnE("�A�N�V����lookup�̎��s�Ɏ��s���܂����B");
			} else {
				printlnE("Execution of Action lookup went wrong.");
			}
		}
		return result;
	}

	/**
	 * ruleset�Ŏw�肵�����[���Z�b�g���A�N�e�B�u�ɂ���B
	 * ���݂��Ȃ����[���Z�b�g���w�肵���ꍇ�̓G���[�ƂȂ�B
	 * @return ���������ꍇ�Anull�B
	 * ���s�����ꍇ�A���ݗ��p�ł��郋�[���Z�b�g�B
	 */
	public String[] activateRuleset(String[] ruleset) {
		return prodsys.activateRuleset(ruleset);
	}

	/**
	 * file�Ŏw�肵���t�@�C���ɋL�q���ꂽ���[���Z�b�g��ǂ݂��ށB
	 * @param filename �t�@�C����
	 * @param envname ����
	 * @param letter r, w, rw, wr�̂ǂꂩ
	 * @param lineno �s�ԍ�
	 * @return ���������ꍇ�A�ǂ݂��񂾃��[���Z�b�g���B
	 * ���s�����ꍇ�Anull�B
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
	 * �A���[�����Z�b�g������L�����Z�������肷��B
	 * @param type ":cancel", ":after", ":every"�̂����ꂩ
	 * @param msec ":cancel"�̏ꍇ�A0�B
	 *             ":after", ":every"�̏ꍇ�A�~���b��\��������B
	 * @param arg ":cancel"�̏ꍇ"all", "alarm*"�B
	 *            ":after", ":every"�̏ꍇ�AOAV�f�[�^�B
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
	 * �x�[�X�v���Z�X����̃C�x���g���󂯎��A���b�Z�[�W�L���[�ɂ����B
	 * ���s���܂܂��ꍇ�A:content�͊e�s�̃��X�g�ƂȂ�B
	 * ���s���܂܂�Ȃ��ꍇ�A
	 * ���[��()��t�������̂�OAV�f�[�^�܂��̓��X�g�`���ɂȂ�Ȃ��ꍇ�́A
	 * �S�̂�""�ň͂����������v�f�ɂ����X�g��:content�ɂȂ�B
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
						st.nextToken(); // ���s
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
	 * �ʒm���ꂽ�A���[��oav��(Msg :content alarm)�`����
	 * ���b�Z�[�W�L���[�ɂ����B
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
