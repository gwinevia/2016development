package dash;

/** DASH�̎��s���G���[��\������N���X */
public class DashException extends RuntimeException {

  public int lineno;
  public Exception exception;

  public DashException(int lineno, String s) {
    super(s);
    this.lineno = lineno;
    this.exception = null;
  }

  public DashException(int lineno, String s, Exception e) {
    super(s);
    this.lineno = lineno;
    this.exception = e;
  }

  public String printString() {
    return "line "+lineno+": "+getMessage();
  }
}
