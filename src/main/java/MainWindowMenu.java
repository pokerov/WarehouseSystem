import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MainWindowMenu {
  private Menu menuBar;

  private Menu subMenuFile, subMenuOperations,
               subMenuEdit, subMenuView,
               subMenuReports, subMenuOthers,
               subMenuWindows, subMenuHelp;

  protected MenuItem menuItemFile,
                     menuItemFileNewDb, menuItemFileOpenDb,
                     menuItemFileConnectToDb, menuItemCloseDb,
                     menuItemFilePrint, menuItemFileExit;
  protected MenuItem menuItemOperations,
                     menuItemOperationsPurchases, menuItemOperationsSales, operationsInvoices;
  protected MenuItem menuItemEdit,
                     menuItemEditItems, menuItemEditVAT, menuItemEditCompanies;
  protected MenuItem menuItemReports,
                     menuItemReportsByDay, menuItemReportsByMonth,
                     menuItemReportsForPeriod, menuItemReportsForAll;
  protected MenuItem menuItemSettings, menuItemView, menuItemOthers, menuItemWindows;
  protected MenuItem menuItemCloseActiveWindow;
  protected MenuItem menuItemHelp,
                     menuItemHelpDocumentation, menuItemHelpLicense, menuItemHelpAbout;

  private final String OUT_OF_JAR = "src/main/resources/";
  
  public final void addMenus(Shell mainWindow, SelectionListener selectionListener){
    menuBar = new Menu(mainWindow, SWT.BAR);
    mainWindow.setMenuBar(menuBar);
    
    menuItemFile = new MenuItem(menuBar, SWT.CASCADE);
    menuItemFile.setText("File");
    subMenuFile = new Menu(mainWindow, SWT.DROP_DOWN);
    menuItemFile.setMenu(subMenuFile);
    menuItemFilePrint = new MenuItem(subMenuFile, SWT.PUSH);
    menuItemFilePrint.setText("Print");
    menuItemFilePrint.setAccelerator(SWT.MOD1 + 'P');
    try {
      menuItemFilePrint.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "print.png"));
    }
    catch (Exception e) {}
    menuItemFilePrint.addSelectionListener(selectionListener);
    new MenuItem(subMenuFile, SWT.SEPARATOR);
    menuItemFileExit = new MenuItem(subMenuFile, SWT.PUSH);
    menuItemFileExit.setText("Exit");
    menuItemFileExit.setAccelerator(SWT.MOD1 + 'X');
    menuItemFileExit.addSelectionListener(selectionListener);
    
    menuItemOperations = new MenuItem(menuBar, SWT.CASCADE);
    menuItemOperations.setText("Operations");
    subMenuOperations = new Menu(mainWindow, SWT.DROP_DOWN);
    menuItemOperations.setMenu(subMenuOperations);
    menuItemOperationsPurchases = new MenuItem(subMenuOperations, SWT.PUSH);
    menuItemOperationsPurchases.setText("Purchases");
    menuItemOperationsPurchases.setAccelerator(SWT.F1);
    try {
      menuItemOperationsPurchases.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "add.png"));
    }
    catch (Exception e) {}
    menuItemOperationsPurchases.addSelectionListener(selectionListener);
    menuItemOperationsSales = new MenuItem(subMenuOperations, SWT.PUSH);
    menuItemOperationsSales.setText("Sales");
    menuItemOperationsSales.setAccelerator(SWT.F2);
    try {
      menuItemOperationsSales.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "remove.png"));
    }
    catch (Exception e) {}
    menuItemOperationsSales.addSelectionListener(selectionListener);
    
    menuItemEdit = new MenuItem(menuBar, SWT.CASCADE);
    menuItemEdit.setText("Edit");
    subMenuEdit = new Menu(mainWindow, SWT.DROP_DOWN);
    menuItemEdit.setMenu(subMenuEdit);
    menuItemEditItems = new MenuItem(subMenuEdit, SWT.PUSH);
    menuItemEditItems.setText("Edit Items");
    menuItemEditItems.setAccelerator(SWT.MOD1 + 'E');
    try {
      menuItemEditItems.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "edit.png"));
    }
    catch (Exception e) {}
    menuItemEditItems.addSelectionListener(selectionListener);
    new MenuItem(subMenuEdit, SWT.SEPARATOR);
    menuItemEditVAT = new MenuItem(subMenuEdit, SWT.PUSH);
    menuItemEditVAT.setText("Edit VAT");
    menuItemEditVAT.setAccelerator(SWT.MOD1 + 'T');
    try {
      menuItemEditVAT.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "vat.png"));
    }
    catch (Exception e) {}
    menuItemEditVAT.addSelectionListener(selectionListener);
    menuItemEditVAT.setEnabled(false);
    menuItemEditCompanies = new MenuItem(subMenuEdit, SWT.PUSH);
    menuItemEditCompanies.setText("Edit companies");
    try {
      menuItemEditCompanies.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "companies.png"));
    }
    catch (Exception e) {}
    menuItemEditCompanies.addSelectionListener(selectionListener);
    
    menuItemReports = new MenuItem(menuBar, SWT.CASCADE);
    menuItemReports.setText("Reports");
    subMenuReports = new Menu(mainWindow, SWT.DROP_DOWN);
    menuItemReports.setMenu(subMenuReports);
    menuItemReportsByDay = new MenuItem(subMenuReports, SWT.PUSH);
    menuItemReportsByDay.setText("By day");
    try {
      menuItemReportsByDay.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "report.png"));
    }
    catch (Exception e) {}
    menuItemReportsByDay.addSelectionListener(selectionListener);
    menuItemReportsByMonth = new MenuItem(subMenuReports, SWT.PUSH);
    menuItemReportsByMonth.setText("By month");
    try {
      menuItemReportsByMonth.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "report.png"));
    }
    catch (Exception e) {}
    menuItemReportsByMonth.addSelectionListener(selectionListener);
    menuItemReportsForPeriod = new MenuItem(subMenuReports, SWT.PUSH);
    menuItemReportsForPeriod.setText("For period");
    try {
      menuItemReportsForPeriod.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "reports.png"));
    }
    catch (Exception e) {}
    menuItemReportsForPeriod.addSelectionListener(selectionListener);
    
    menuItemHelp = new MenuItem(menuBar, SWT.CASCADE);
    menuItemHelp.setText("Help");
    subMenuHelp = new Menu(mainWindow, SWT.DROP_DOWN);
    menuItemHelp.setMenu(subMenuHelp);
    menuItemHelpDocumentation = new MenuItem(subMenuHelp, SWT.PUSH);
    menuItemHelpDocumentation.setText("Documentation");
    menuItemHelpDocumentation.setAccelerator(SWT.MOD1 + '?');
    try {
      menuItemHelpDocumentation.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "help.png"));
    }
    catch (Exception e) {}
    menuItemHelpDocumentation.addSelectionListener(selectionListener);
    menuItemHelpLicense = new MenuItem(subMenuHelp, SWT.PUSH);
    menuItemHelpLicense.setText("License");
    menuItemHelpLicense.addSelectionListener(selectionListener);
    new MenuItem(subMenuHelp, SWT.SEPARATOR);
    menuItemHelpAbout = new MenuItem(subMenuHelp, SWT.PUSH);
    menuItemHelpAbout.setText("Abouts");
    menuItemHelpAbout.addSelectionListener(selectionListener);
  }
}
