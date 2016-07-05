package dash;

import java.util.*;

/**
 * �A���[���̊Ǘ�������N���X
 */
class AlarmManager {

  /**
   * �S�ẴA���[���Bkey�̓A���[��ID�Bvalue��Alarm�B
   * (alarm :cancel)�����ƁA�G���g���폜�����B
   */
  private Hashtable allAlarms;

  /** DVM */
  private DVM dvm;
  
  /** �R���X�g���N�^ */
  AlarmManager(DVM dvm) {
    allAlarms = new Hashtable();
    this.dvm = dvm;
  }
  
  /**
   * �A���[�����Z�b�g����B
   * @param agname �G�[�W�F���g�̖��O
   * @param repeat ����Ԃ��ꍇtrue
   * @param time   �~���b
   * @param oav    �ʒm����OAV�^�f�[�^
   * @return �A���[��ID
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
   * alarmID�Ŏw�肵���A���[�����L�����Z������B
   * @return �L�����Z�������A���[��ID
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
   * �G�[�W�F���gagname�ɃA���[��oav��`����B
   * @return Alarm�X���b�h���~������ׂ��Ȃ�false
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
   * �A���[���̃X���b�h
   * �X���b�h���́A�G�[�W�F���g��:�A���[��ID
   */
  private class Alarm extends Thread {
  
    private String agname;
    private boolean repeat;
    private long time;
    private String alarmID;
    private String oav;
    
    /**
     * �R���X�g���N�^
     * @param agname �G�[�W�F���g��
     * @param alarmID �A���[��ID (alarm+currentTime())
     * @param repeat ����Ԃ��ꍇtrue
     * @param time �A���[������܂ł̎���(�~���b)
     * @param oav    �ʒm����OAV�^�f�[�^
     */
    Alarm(ThreadGroup dashThreads, String agname, String alarmID, boolean repeat, long time, String oav) {
      super(dashThreads, agname+":"+alarmID);
      this.agname = agname;
      this.repeat = repeat;
      this.alarmID = alarmID;
      this.time = time;
      this.oav = oav;
    }

    /** �X���[�v��ɃA���[����ʒm����B */
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
   * ���݂̎������AYYYYMMDDhhmmssxxx�`���ŕԂ��B
   */
  private String currentTime() {
    Calendar rightNow = Calendar.getInstance();
    StringBuffer buf = new StringBuffer();

    int i = rightNow.get(Calendar.YEAR);     // YYYY�N
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.MONTH)+1;      // MM��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.DATE);         // DD��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));

    i = rightNow.get(Calendar.HOUR_OF_DAY);  // hh��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));
      
    i = rightNow.get(Calendar.MINUTE);       // mm��
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));
      
    i = rightNow.get(Calendar.SECOND);       // ss�b
    if (i<10) buf.append("0");
    buf.append(Integer.toString(i));
      
    i = rightNow.get(Calendar.MILLISECOND);  // xxx �~���b(
    if (i<10) buf.append("00");
    else if (i<100) buf.append("0");
    buf.append(Integer.toString(i));

    return buf.toString();
  }
}
