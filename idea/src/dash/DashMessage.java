package dash;

import java.util.*;

/**
 * DASH�̃��b�Z�[�W�`���B
 * TAF�Ɠ����悤�Ȋ����B
 */
public class DashMessage implements java.io.Serializable {

  public static final String DVM       = "_dvm";
  public static final String LOCALHOST = "_localhost";
  public static final String IF        = "_interface";
  public static final String BCAST     = "_broadcast";

  public static final String CREATEINSTANCE  = "_createInstance";
  public static final String CREATEDINSTANCE = "_createdInstance";
  public static final String INSTANTIATE     = "_instantiate";
  public static final String MOVE            = "_move";
  public static final String FEEDBACK        = "_feedback";
  public static final String GETBYTECODE     = "_getBytecode";
  public static final String PUTBYTECODE     = "_putBytecode";
  public static final String KILLFORCE       = "_killForce";
  public static final String ACTIVATEMOBILE  = "_activateMobile";
  public static final String GETRULESET      = "_getRuleset";
  public static final String PUTRULESET      = "_putRuleset";

  public static final String DEDUCTIONSTART  = "_decuctionStart"; // added by takagaki
  /** �p�t�H�[�}�e�B�u */
  String performative;

  /** ���M���G�[�W�F���g�� */
  String from;

  /** ���M��G�[�W�F���g�� */
  String to;

	/** ���b�Z�[�WID�B���Ő������ꂽ���b�Z�[�W�̒��ň�ӂ�1�ȏ�̐����B */
	long replyWith;
	
	/** ���̃��b�Z�[�W�ɑ΂���ԐM���b�Z�[�W�̏ꍇ�A���̃��b�Z�[�WID�B
      �����łȂ��ꍇ��0�B*/
	long inReplyTo;

  /** ���b�Z�[�W�̑��M���̊����A�܂���null�B
      null�̏ꍇ�́A���������ւ̃��b�Z�[�W�ł��邱�Ƃ�\���B*/
  String departure;

  /** ���b�Z�[�W�̓�����������A�܂���null�B
      null�̏ꍇ�́A���������ւ̃��b�Z�[�W�ł��邱�Ƃ�\���B*/
  String arrival;

  /** ���e�B*/
  String content; 

  /** ���̑��̑��� */
  private Hashtable otherAttributes;

  /** �V�K�ȃ��b�Z�[�W(�ԐM���b�Z�[�W�ł͂Ȃ����b�Z�[�W)�����B*/
  public DashMessage(String perf, String from, String to, String arrival, long id) {
    this.performative = perf;
    this.from = from;
    this.to = to;
    this.arrival = arrival;
    this.replyWith = id;
    this.inReplyTo = 0;
    if ("null".equals(this.arrival))
      this.arrival = null;
  }

  /** �ԐM���b�Z�[�W�����B*/
  public DashMessage(String perf, DashMessage msg, long id) {
    this.performative = perf;
    this.from = msg.to;
    this.to   = msg.from;
    this.arrival = msg.departure;
    this.replyWith = id;
    this.inReplyTo = msg.replyWith;
    if ("null".equals(this.arrival))
      this.arrival = null;
  }

  /** �ԐM���b�Z�[�W�����B*/
  public DashMessage(String perf, String from, ps.OAVdata msg, long id) {
    this.performative = perf;
    this.from = from;
    this.to   = msg.getValueString(":from");
    this.arrival = msg.getValueString(":departure");
    this.replyWith = id;
    this.inReplyTo = Long.parseLong(msg.getValueString(":replyWith"));
    if ("null".equals(this.arrival))
      this.arrival = null;
  }

  /** �x�[�X�v���Z�X����̃C�x���g�A���邢�̓A���[�������B*/
  public DashMessage(String content, boolean isEvent) {
    performative = (isEvent ? "__Event" : "__Alarm");
    setContent(content);
  }

  // added by takagaki
  public DashMessage()
  {
    this.performative = DashMessage.DEDUCTIONSTART;
  }

  /** content���Z�b�g���� */
  public void setContent(String content) {
    this.content = content;
  }

  /** ���̑��̑������Z�b�g���� */
  public void setOtherAttributes(String attr, Object value) {
    if (otherAttributes == null)
      otherAttributes = new Hashtable();
    otherAttributes.put(attr, value);
  }

  /** ���̑��̑������Q�b�g���� */
  public String getOtherAttributes(String attr) {
    if (otherAttributes == null)
      return null;
    else
      return (String)otherAttributes.get(attr);
  }

  /** String�ȊO�̂��̑��̑������Q�b�g���� */
  public Object getOtherAttributesB(String attr) {
    return otherAttributes.get(attr);
  }

  /** ���b�Z�[�W�́u���̑������v���Z�b�g����B */
  public void setOtherAttributes(ps.OAVdata oav) {
    for (Enumeration e = oav.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      if (key.equals(":performative") ||
          key.equals(":from")         || key.equals(":to")        ||
          key.equals(":departure")    || key.equals(":arrival")   ||
          key.equals(":replyWith")    || key.equals(":inReplyTo") ||
          key.equals(":content")      ||
          key.equals(":system")) {
        ;
      } else {
        String value = oav.getValueString(key);
        setOtherAttributes(key, value);
      }
    }
  }

  /** �w�肳�ꂽ�u���̑��̑����v������Ȃ�true��Ԃ��B*/
  public boolean hasOtherAttribute(String attr) {
    if (otherAttributes == null)
      return false;

    return (otherAttributes.get(attr) != null);
  }

  /**
   * specify_manager���b�Z�[�W�Ȃ�true��Ԃ��B
   * CNP���[���Z�b�g���ł������Ƃōl����B
   */
  public boolean isSpecifyManager() {
    return false;
  }

  /**
   * :system���ANO_SUCH_AGENT �� NO_SUCH_DVM �Ȃ�true��Ԃ��B
   */
  public boolean isNoSuchAgentOrDvm() {
    if (otherAttributes != null) {
      Object obj = otherAttributes.get(":system");
      if ("NO_SUCH_AGENT".equals(obj) || "NO_SUCH_DVM".equals(obj))
        return true;
    }
    return false;
  }

  /** String�ɕϊ�����B*/
  public String toString() {
    return "(Msg" +
      " :performative " + performative +
      " :from "         + from         +
      " :to "           + to           +
      " :replyWith "    + replyWith    +
      " :inReplyTo "    + inReplyTo    +
      " :departure "    + departure    +
      " :arrival "      + arrival      +
      toStringOtherAttributes(false)   +
      " :content "      + content      +  ")";
  }

  /** ���s�̓������\���p��String�ɕϊ�����B*/
  public String toString2() {
    return "(Msg"+
      "\n  :performative " + performative +
      "\n  :from "         + from         +
      "\n  :to "           + to           +
      "\n  :replyWith "    + replyWith    +
      "\n  :inReplyTo "    + inReplyTo    +
      "\n  :departure "    + departure    +
      "\n  :arrival "      + arrival      +
      toStringOtherAttributes(true)       +
      "\n  :content "      + content      +  ")";
  }

  private String toStringOtherAttributes(boolean withCR) {
    if (otherAttributes == null)
      return "";

    StringBuffer buf = new StringBuffer();
    for (Enumeration e = otherAttributes.keys(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      buf.append(withCR ? "\n  " : " ");
      buf.append(key);
      buf.append(" ");
      buf.append(otherAttributes.get(key));
    }
    return buf.toString();
  }

  /** content�ȊO�����s�̓������\���p��String�ɕϊ�����B*/
  public String toStringWithoutContent() {
    return
      ":performative " + performative + "\n" +
      ":from "         + from         + "\n" +
      ":to "           + to           + "\n" +
      ":replyWith "    + replyWith    + "\n" +
      ":inReplyTo "    + inReplyTo    + "\n" +
      ":departure "    + departure    + "\n" +
      ":arrival "      + arrival      + "\n" +
      toStringOtherAttributes(true);
  }

}
