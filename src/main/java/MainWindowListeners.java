import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;

public class MainWindowListeners implements SelectionListener, ShellListener {

  private SQLManager sqlManager;
  private PluginManager pluginManager;
  private ToolTip toolTip;
  private Tray trayIcon;
  private Shell mainWindow;
  private Table table;

  private MainWindowMenu mainWindowMenu = new MainWindowMenu();
  private MainWindowToolBar mainWindowToolBar = new MainWindowToolBar();
  private MainWindowTable mainWindowTable = new MainWindowTable();

  private void refreshTable(Table table) {
    table.removeAll();
    try {
      for (int row=1; row<=sqlManager.CountItems("Warehouse", "Products"); row++){
        String[] col = sqlManager.GetRow(row);
        TableItem ti = new TableItem(table, SWT.NONE);
        ti.setText(new String[]{col[0], col[7], col[1], col[2], col[3], col[4], col[5], col[6]});
      }
    }
    catch (SQLException e) {
      System.out.println("SQL Error: " + e.getMessage());
    }
    catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void print(String jobName, Shell owner, Table table){
    try {
      PrintDialog dialog = new PrintDialog(owner);
      PrinterData  printData = dialog.open();
      Printer printer = new Printer(printData);
      if (printer.startJob(jobName)){
        GC gc = new GC(printer);
        TableItem[] ti = table.getItems();
        if (printer.startPage()){
          int hy = 150;
          int ry = 300;

          gc.drawString("Code", 200, hy);
          gc.drawString("Date", 500, hy);
          gc.drawString("Product", 1100, hy);
          gc.drawString("Quantity", 2200, hy);
          gc.drawString("Single qty", 2800, hy);
          gc.drawString("Single value", 3100, hy);
          gc.drawString("Total", 3600, hy);
          gc.drawString("Supplier", 4000, hy);

          gc.drawLine(200, hy + 125, 4900, hy + 125);

          for (int i=0; i<ti.length; i++){
            gc.drawString(ti[i].getText(0), 200, ry);
            gc.drawString(ti[i].getText(1),  500, ry);
            gc.drawString(ti[i].getText(2),  1100, ry);
            gc.drawString(ti[i].getText(3),  2200, ry);
            gc.drawString(ti[i].getText(4),  2800, ry);
            gc.drawString(ti[i].getText(5),  3100, ry);
            gc.drawString(ti[i].getText(6),  3600, ry);
            gc.drawString(ti[i].getText(7),  4000, ry);
            ry += 150;
          }

          printer.endPage();
        }
        gc.dispose();
        printer.endJob();
      }
      printer.dispose();
    }
    catch (Exception e) {
      MessageBox messageBox = new MessageBox(mainWindow, SWT.ICON_ERROR);
      messageBox.setText("Грешка");
      messageBox.setMessage("Грешка при отпечатване.\n" + e.getMessage());
      messageBox.open();
    }    
  }

  public void addCore(SQLManager sqlManager, PluginManager pluginManager, ToolTip toolTip, Tray trayIcon, Shell mainWindow, Table table) {
    this.sqlManager = sqlManager;
    this.pluginManager = pluginManager;
    this.toolTip = toolTip;
    this.trayIcon = trayIcon;
    this.mainWindow = mainWindow;
    this.table = table;
    mainWindowMenu.addMenus(mainWindow, this);
    mainWindowToolBar.addToolbar(mainWindow, this, pluginManager);
    mainWindowTable.addTableFrame(mainWindow);
  }

  public void shellActivated(ShellEvent event) {}
  public void shellDeactivated(ShellEvent event) {}
  public void shellClosed(ShellEvent event) {
    sqlManager = null;
    pluginManager = null;
    toolTip.dispose();
    trayIcon.dispose();
    mainWindow.dispose();
    System.exit(0);
  }
  public void shellIconified(ShellEvent event) {}
  public void shellDeiconified(ShellEvent event) {}

  public void widgetSelected(SelectionEvent event){
    Object ev = event.getSource();
    if (ev == mainWindowMenu.menuItemFileNewDb)System.out.println("New database pressed.");
    if (ev == mainWindowMenu.menuItemFileOpenDb)System.out.println("Open database pressed.");
    if (ev == mainWindowMenu.menuItemFileConnectToDb) {

    }
    if (ev == mainWindowMenu.menuItemCloseDb){
      sqlManager = null;
      table.removeAll();
      mainWindowMenu.menuItemCloseDb.setEnabled(false);
      mainWindowMenu.menuItemFileConnectToDb.setEnabled(true);
      toolTip.setText("SQL connection closed.");
      toolTip.setMessage("Connection to database is now closed.");
      toolTip.setVisible(true);
    }
    if (ev == mainWindowMenu.menuItemFilePrint) {
      print("Warehouse Job", mainWindow, table);
    }
    if (ev == mainWindowMenu.menuItemFileExit) {
      sqlManager = null;
      pluginManager = null;
      toolTip.dispose();
      trayIcon.dispose();
      mainWindow.dispose();
      System.exit(0);
    }

    if (ev == mainWindowMenu.menuItemOperationsPurchases){
      new PurchasesWindow(this.sqlManager);
      refreshTable(this.table); 
    }
    if (ev == mainWindowMenu.menuItemOperationsSales) {
      new ChooseWindow(sqlManager);
      refreshTable(table);
    }
    if (ev == mainWindowMenu.operationsInvoices)System.out.println("Invoices pressed.");

    if (ev == mainWindowMenu.menuItemEditItems) {
      new ModifyWindow(sqlManager);
      refreshTable(table);
    }
    if (ev == mainWindowMenu.menuItemEditVAT)System.out.println("VAT pressed.");
    if (ev == mainWindowMenu.menuItemEditCompanies) new CompaniesWindow(sqlManager);
    if (ev == mainWindowMenu.menuItemReportsByDay) new ReportsByDay(sqlManager);
    if (ev == mainWindowMenu.menuItemReportsByMonth) new ReportsByMonth(sqlManager);
    if (ev == mainWindowMenu.menuItemReportsForPeriod) new ReportsForPeriod(sqlManager);
    if (ev == mainWindowMenu.menuItemReportsForAll)System.out.println("For all pressed.");

    if (ev == mainWindowMenu.menuItemSettings)System.out.println("Settings pressed.");

    if (ev == mainWindowMenu.menuItemCloseActiveWindow)System.out.println("Close active window pressed.");

    if (ev == mainWindowMenu.menuItemHelpDocumentation) {
      MessageBox messageBox = new MessageBox(mainWindow, SWT.OK | SWT.ICON_INFORMATION);
      messageBox.setMessage("Моля прочетете документация включена в Магистърската Теза към продукта.");
      messageBox.setText("Документация");
      messageBox.open();
    }
    if (ev == mainWindowMenu.menuItemHelpLicense) {
      MessageBox messageBox = new MessageBox(mainWindow, SWT.OK | SWT.ICON_INFORMATION);
      messageBox.setMessage("CC, Някои права са запазени.");
      messageBox.setText("Лиценз");
      messageBox.open();
    }
    if (ev == mainWindowMenu.menuItemHelpAbout) new AboutWindow();
    if (ev == mainWindowToolBar.toolItemRefresh) refreshTable(table);
    if (ev == mainWindowToolBar.toolItemPurchases) {
      new PurchasesWindow(sqlManager);
      refreshTable(table);
    }
    if (ev == mainWindowToolBar.toolItemSalesCodes) {
      new DeleteByCodeWindow(sqlManager);
      refreshTable(table);
    }
    if (ev == mainWindowToolBar.toolItemSalesProducts) {
      new DeleteByProductWindow(sqlManager);
      refreshTable(table);
    }
    if (ev == mainWindowToolBar.toolItemModify) {
      new ModifyWindow(sqlManager);
      refreshTable(table);
    }
    if (ev == mainWindowToolBar.toolItemClose) {
      mainWindow.close();
    }
    if (ev == mainWindowToolBar.toolItemPlugin) {
      pluginManager.loadGUI();
    }
  }
  public void widgetDefaultSelected(SelectionEvent event){}
}
