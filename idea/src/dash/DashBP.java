package dash;

/**
 * ベースプロセスのインタフェース。
 */
public interface DashBP extends Runnable {

  public void setAgent(DashAgent agent);

  public void finalizeBP();

}
