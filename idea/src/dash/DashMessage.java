package dash;

import java.util.*;

/**
 * DASHのメッセージ形式。
 * TAFと同じような感じ。
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
  /** パフォーマティブ */
  String performative;

  /** 送信元エージェント名 */
  String from;

  /** 送信先エージェント名 */
  String to;

	/** メッセージID。環境で生成されたメッセージの中で一意な1以上の整数。 */
	long replyWith;
	
	/** 他のメッセージに対する返信メッセージの場合、そのメッセージID。
      そうでない場合は0。*/
	long inReplyTo;

  /** メッセージの送信元の環境名、またはnull。
      nullの場合は、同じ環境内へのメッセージであることを表す。*/
  String departure;

  /** メッセージの到着する環境名、またはnull。
      nullの場合は、同じ環境内へのメッセージであることを表す。*/
  String arrival;

  /** 内容。*/
  String content; 

  /** その他の属性 */
  private Hashtable otherAttributes;

  /** 新規なメッセージ(返信メッセージではないメッセージ)を作る。*/
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

  /** 返信メッセージを作る。*/
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

  /** 返信メッセージを作る。*/
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

  /** ベースプロセスからのイベント、あるいはアラームを作る。*/
  public DashMessage(String content, boolean isEvent) {
    performative = (isEvent ? "__Event" : "__Alarm");
    setContent(content);
  }

  // added by takagaki
  public DashMessage()
  {
    this.performative = DashMessage.DEDUCTIONSTART;
  }

  /** contentをセットする */
  public void setContent(String content) {
    this.content = content;
  }

  /** その他の属性をセットする */
  public void setOtherAttributes(String attr, Object value) {
    if (otherAttributes == null)
      otherAttributes = new Hashtable();
    otherAttributes.put(attr, value);
  }

  /** その他の属性をゲットする */
  public String getOtherAttributes(String attr) {
    if (otherAttributes == null)
      return null;
    else
      return (String)otherAttributes.get(attr);
  }

  /** String以外のその他の属性をゲットする */
  public Object getOtherAttributesB(String attr) {
    return otherAttributes.get(attr);
  }

  /** メッセージの「その他属性」をセットする。 */
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

  /** 指定された「その他の属性」があるならtrueを返す。*/
  public boolean hasOtherAttribute(String attr) {
    if (otherAttributes == null)
      return false;

    return (otherAttributes.get(attr) != null);
  }

  /**
   * specify_managerメッセージならtrueを返す。
   * CNPルールセットができたあとで考える。
   */
  public boolean isSpecifyManager() {
    return false;
  }

  /**
   * :systemが、NO_SUCH_AGENT か NO_SUCH_DVM ならtrueを返す。
   */
  public boolean isNoSuchAgentOrDvm() {
    if (otherAttributes != null) {
      Object obj = otherAttributes.get(":system");
      if ("NO_SUCH_AGENT".equals(obj) || "NO_SUCH_DVM".equals(obj))
        return true;
    }
    return false;
  }

  /** Stringに変換する。*/
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

  /** 改行の入った表示用のStringに変換する。*/
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

  /** content以外を改行の入った表示用のStringに変換する。*/
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
