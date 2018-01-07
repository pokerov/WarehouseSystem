import java.io.File;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class MainWindow extends MainWindowListeners {

  private final String OUT_OF_JAR = "src/main/resources/";

  private Shell self;

  private Table table;
  private SQLManager sqlManager;
  private ToolTip toolTip;
  private Display display;
  private PluginManager pluginManager;
  private Tray trayIcon;

  /**
   * @see self - Reference to this window (instance of Shell)
   * @param vals
   */
  public MainWindow(String[] vals){
    display = Display.getCurrent();
    self = new Shell(display);
    self.setText("Warehouse System");
    self.setSize(950, 400);
    self.setMinimumSize(950, 400);
    self.setLayout(new GridLayout(1, false));

    try {
      self.setImage(new Image(self.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "icon_big.png"));
    }
    catch (Exception e) {}

    self.addShellListener(this);

    pluginManager = new PluginManager();

    trayIcon = display.getSystemTray();
    toolTip = new ToolTip(self, SWT.BALLOON | SWT.ICON_INFORMATION);
    if (trayIcon == null){
      MessageBox mb = new MessageBox(self, SWT.OK | SWT.ICON_WARNING);
      mb.setMessage("Some exception! Tray is missing under this OS.");
      mb.setText("Exception");
      mb.open();
    }
    else {
      TrayItem trayItem = new TrayItem(trayIcon, 0);
      trayItem.setToolTipText("Warehouse System");
      try {
        trayItem.setImage(new Image(self.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "warehouse.png"));
      }
      catch (Exception e) {}
      trayItem.setToolTip(toolTip);
    }

//    mwm.menuItemOperationsPurchases.setEnabled(false);
//    mwm.menuItemOperationsSales.setEnabled(false);
//    mwm.menuItemEditItems.setEnabled(false);
//    mwtb.toolItemRefresh.setEnabled(false);

    connect(vals);
    addCore(sqlManager, pluginManager, toolTip, trayIcon, self, table);
    ProgramUtils.centerWindow(self, display);

    self.open();
    while(!self.isDisposed())
      if(!display.readAndDispatch())display.sleep();
    display.dispose();
  }

  private void connect(String[] programInputValues) {
    try{
      sqlManager = new SQLManager(programInputValues[0], programInputValues[1], programInputValues[2], programInputValues[3]);
      toolTip.setText("Connection established");
      toolTip.setMessage("You are now connected to database.");
      toolTip.setVisible(true);

      for (int row=1; row<=sqlManager.CountItems("Warehouse", "Products"); row++){
        String[] col = sqlManager.GetRow(row);
        TableItem ti = new TableItem(table, SWT.NONE);
        ti.setText(new String[]{col[0], col[7], col[1], col[2], col[3], col[4], col[5], col[6]});
      }
    }
    catch (SQLException e) {
      System.out.println("SQL Error: " + e.getMessage());
      e.printStackTrace();
    }
    catch (Exception e) {
      System.out.println("Error in MainWindow: " + e.getMessage());
      e.printStackTrace();
    }
//    mwm.menuItemOperationsPurchases.setEnabled(true);
//    mwm.menuItemOperationsSales.setEnabled(true);
//    mwm.menuItemEditItems.setEnabled(true);
//    mwtb.toolItemRefresh.setEnabled(true);
//    cTabFolder.setEnabled(true);
  }

}
