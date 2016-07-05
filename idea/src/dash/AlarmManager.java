package dash;

import java.util.*;

/**
 * アラームの管理をするクラス
 */
class AlarmManager {

  /**
   * 全てのアラーム。keyはアラームID。valueはAlarm。
   * (alarm :cancel)されると、エントリ削除される。
   */
  private Hashtable allAlarms;

  /** DVM */
  private DVM dvm;
  
  /** コンストラクタ */
  AlarmManager(DVM dvm) {
    allAlarms = new Hashtable();
    this.dvm = dvm;
  }
  
  /**
   * アラームをセットする。
   * @param agname エージェントの名前
   * @param repeat くり返す場合true
   * @param time   ミリ秒
   * @param oav    通知するOAV型データ
   * @return アラームID
   */
  synchronized String setAlarm(ThreadGroup dashThreads, String agname, boolean repeat, long time, String oav) {
    String alarmID = null;
    do {
      alarmID = "alarm"+currentTime();
    } while (allAlarms.get(alarmID) != null);
    Alarm alarm = new Alarm(dashThreads, agname, alarmID, repeat, time, oav);
    allAlarms.put(alarmID, alarm);
    alarm.start();
    return alarmID;
  }

  /**
   * alarmIDで指定したアラームをキャンセルする。
   * @return キャンセルしたアラームID
   */
  synchronized String[] cancelAlarm(String agname, String alarmID) {
    String res[] = null;

    if (alarmID.equals("all")) {
      res = new String[allAlarms.size()];
      //vector to arraylist changed 05/02/17
      ArrayList v = new ArrayList();
      for (Enumeration e = allAlarms.keys(); e.hasMoreElements(); ) {
        String id = (String)e.nextElement();
        Alarm alarm = (Alarm)allAlarms.get(id);
        if (alarm.belongs(agname))
          v.add(id);
      }
      res = new String[v.size()];
      int i=0;
      //enumeration to iterator changed 05/02/17
      for (Iterator it = v.iterator(); it.hasNext(); ) {
        String id = (String)it.next();
        allAlarms.remove(id);
        res[i] = id;
        i++;
      }
    } else {
      allAlarms.remove(alarmID);
      res = new String[1];
      res[0] = alarmID;
    }
    return res;
  }
  

  /**
   * エージェントagnameにアラームoavを伝える。
   * @return Alarmスレッドを停止させるべきならfalse
   */
  synchronized private boolean alarm(String agname, String alarmID, String oav) {
    if (allAlarms.get(alarmID) == null)
      return false;
    boolean result = dvm.notifyAlarm(agname, alarmID, oav);
    if (!result)
      cancelAlarm(agname, alarmID);
    return result;
  }

  /**
   * アラームのスレッド
   * スレッド名は、エージェント名:アラームID
   */
  private class Alarm extends Thread {
  
    private String agname;
    private boolean repeat;
    private long time;
    private String alarmID;
    private String oav;
    
    /**
     * コンストラクタ
     * @param agname エージェント名
     * @param alarmID アラームID (alarm+currentTime())
     * @param repeat くり返す場合true
     * @param time アラームするまでの時間(ミリ秒)
     * @param oav    通知するOAV型データ
     */
    Alarm(ThreadGroup dashThreads, String agname, String alarmID, boolean repeat, long time, String oav) {
      super(dashThreads, agname+":"+alarmID);
      this.agname = agname;
      this.repeat = repeat;
      this.alarmID = alarmID;
      this.time = time;
      this.oav = oav;
    }

    /** スリープ後にアラームを通知する。 */
    public void run() {
      boolean interrupted = false;
      do {
        try {
          Thread.sleep(time);
        } catch (InterruptedException e) {
          interrupted = true;
          repeat = false;
        }

        if (!interrupted) {
          boolean b = alarm(agname, alarmID, oav);
          repeat &= b;
        }
      } while (repeat && !interrupted);

      cancelAlarm(agname, alarmID);
    }

    /**
     */
    boolean belongs(String agname) {
      return this.agname.equals(agname);
    }
  }


  /**
   * 現在の時刻を、YYYYMMDDhhmmssxxx形式で返す。
   */
  private String currentTime() {
    Calendar rightNow = Calendar.getInstance();
    StringBuffer buf = new StringBuffer();

    int i = rightNow.get(Calendar.YEAR);     // YYYY年
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MONTH)+1;      // MM月
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.DATE);         // DD日
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.HOUR_OF_DAY);  // hh時
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));
      
    i = rightNow.get(Calendar.MINUTE);       // mm分
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));
      
    i = rightNow.get(Calendar.SECOND);       // ss秒
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));
      
    i = rightNow.get(Calendar.MILLISECOND);  // xxx ミリ秒(
    if (i<10) buf.append("00");
    else if (i<100) buf.append("0");
    buf.append(Integer.toString(i));

    return buf.toString();
  }
}
