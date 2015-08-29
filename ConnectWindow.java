import java.io.File;
import java.util.regex.*;
import java.util.regex.Pattern;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;

public class ConnectWindow implements SelectionListener, ShellListener {
  
  private Shell window;
  private Label lAddress, lPort, lUser, lPassword;
  private Text tUser, tPassword;
  private Button bConnect, bCancel;
  private String[] cw_values = new String[4];
  private boolean cancel = false;
  private Combo cAddress, cPort;
  private Display d;
  private boolean firstRun = false;

  public ConnectWindow(boolean isFirstRun) {
    firstRun = isFirstRun;
    try {
      d = new Display();
    }
    catch (Exception e) {}
    window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
    window.setText("Свързване");
    window.setSize(300, 200);
    window.addShellListener(this);
    try {
      window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
    }
    catch (Exception e) {}
    
    GridLayout layout = new GridLayout();
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    layout.marginBottom = 10;
    layout.marginLeft = 10;
    layout.marginRight = 10;
    layout.marginTop = 10;
    layout.numColumns = 2;
    window.setLayout(layout);
    
    try {
      centerWindow(window);
    }
    catch (Exception e) {}
    addUI();
    
    window.pack();
    window.open();
    while(!window.isDisposed())
      if(!d.readAndDispatch())d.sleep();
  }
  
  private void centerWindow(Shell shell) {
    Monitor primary = d.getPrimaryMonitor();
    Rectangle bounds = primary.getBounds();
    Rectangle rect = shell.getBounds();
    int x = bounds.x + (bounds.width - rect.width) / 2;
    int y = bounds.y + (bounds.height - rect.height) / 2;
    shell.setLocation(x, y);
  }
  
  private void addUI(){
    GridData data = new GridData();
    data.widthHint = 150;
    
    lAddress = new Label(window, SWT.RIGHT);
    lAddress.setText("Адрес на сървъра");
    
    cAddress = new Combo(window, SWT.DROP_DOWN);
    cAddress.setLayoutData(data);
    cAddress.setTextLimit(15);
    cAddress.setItems(new String[]{"127.0.0.1"});
    
    lPort = new Label(window, SWT.RIGHT);
    lPort.setText("Номер на порт");
    
    cPort = new Combo(window, SWT.DROP_DOWN);
    cPort.setLayoutData(data);
    cPort.setTextLimit(5);
    cPort.setItems(new String[]{"3306"});
    
    lUser = new Label(window, SWT.RIGHT);
    lUser.setText("Потребителско име");
    
    tUser = new Text(window, SWT.SINGLE | SWT.BORDER);
    tUser.setLayoutData(data);
    tUser.setTextLimit(20);
    tUser.setToolTipText("Име за достъп до MySQL база данни");
    
    lPassword = new Label(window, SWT.RIGHT);
    lPassword.setText("Парола");
        
    tPassword = new Text(window, SWT.SINGLE | SWT.BORDER);
    tPassword.setLayoutData(data);
    tPassword.setTextLimit(20);
    tPassword.setToolTipText("Парола за вписване");
    
    bCancel = new Button(window, SWT.PUSH);
    bCancel.setText("Отказ");
    bCancel.setLayoutData(data);
    bCancel.setBackground(new Color(d, 255, 0, 0));
    bCancel.addSelectionListener(this);
    
    bConnect = new Button(window, SWT.PUSH);
    bConnect.setText("Свързване");
    bConnect.setLayoutData(data);
    bConnect.setBackground(new Color(d, 0, 255, 0));
    bConnect.addSelectionListener(this);
    try {
      bConnect.setImage(new Image(window.getDisplay(), "icons" + File.separator + "connect.png"));
    }
    catch (Exception e) {}

    cAddress.select(0);
    cPort.select(0);
    tUser.setText("root");
    tPassword.setText("1234");
  }
  
  private void setValues(String addr, String prt, String usr, String pwd) {
    cw_values[0] = addr;
    cw_values[1] = prt;
    cw_values[2] = usr;
    cw_values[3] = pwd;
  }
  
  public String[] getValues(){
    return cw_values;
  }
  
  private void setCancel(){
    cancel = true;
  }
  
  public boolean isCancel(){
    return cancel;
  }
  
    private boolean validateIP(String ip) {
        if (!ip.isEmpty()) {
            String pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                             "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                             "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                             "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(ip);
            return matcher.matches();
        }
        else return false;
    }

    private boolean valid() {
        if (cAddress.getText().isEmpty() || !validateIP(cAddress.getText())) return false;
        else if (cPort.getText().isEmpty() || !cPort.getText().matches("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")) return false;
        else if (tUser.getText().isEmpty()) return false;
        else if (tPassword.getText().isEmpty()) return false;
        else return true;
    }
  
  public void widgetSelected(SelectionEvent event){
    Object e = event.getSource();
    if (e == bConnect) {
      if(valid()) {
                setValues(cAddress.getText(), cPort.getText(), tUser.getText(), tPassword.getText());
                window.close();
                if (firstRun) new FirstRunWindow(cw_values);
                else new MainWindow(cw_values);
            }
            else {
                if (cAddress.getText().isEmpty() || !validateIP(cAddress.getText())) cAddress.setBackground(new Color(d, 255, 0, 0));
                if (cPort.getText().isEmpty() || !cPort.getText().matches("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")) cPort.setBackground(new Color(d, 255, 0, 0));
                if (tUser.getText().isEmpty()) tUser.setBackground(new Color(d, 255, 0, 0));
                if (tPassword.getText().isEmpty()) tPassword.setBackground(new Color(d, 255, 0, 0));
            }
    }
    if (e == bCancel){
      setCancel();
      window.dispose();
    }
  }
  public void widgetDefaultSelected(SelectionEvent event){}
  
  public void shellActivated(ShellEvent event) {}
  public void shellDeactivated(ShellEvent event) {}
  public void shellClosed(ShellEvent event) {
    setCancel();
    if (firstRun) {
      window.dispose();
    }
  }
  public void shellIconified(ShellEvent event) {}
  public void shellDeiconified(ShellEvent event) {}
}
