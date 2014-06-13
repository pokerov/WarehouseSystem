import java.io.*;
import java.sql.SQLException;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.custom.*;

public class MainWindow implements SelectionListener, ShellListener {

	Menu bar;
	Menu subFile, subOperations, subEdit, subView, subReports, subOthers, subWindows, subHelp;
	MenuItem fileMenu, operationsMenu, editMenu, viewMenu, reportsMenu,	othersMenu, windowsMenu, helpMenu;
	MenuItem fileNewDB, fileOpenDB, fileConnectToDB, fileCloseDB, filePrint, fileExit;
	MenuItem operationsPurchases, operationsSales, operationsInvoices;
	MenuItem editItems, editVAT, editCompanies;
	MenuItem byDay, byMonth, forPeriod, forAll;
	MenuItem settings;
	MenuItem closeActiveWindow;
	MenuItem documentation, license, about;
	
	Shell main_window;
	
	private String[] values = new String[4];
	private Table table;
	private TableColumn colCode, colDate, colProduct, colQuantity, colType, colSingle_value, colValue, colCompany;
	private SQLManager sql;
	private ToolTip tip;
	private CTabFolder folder;
	private CTabItem stock;
	private ToolBar toolbar;
	private ToolItem tiRefresh, tiPurchases, tiSalesCode, tiSalesProduct, tiModify, tiClose, tiPlugin;
	private Display d;
	private PluginManager pm;
	private Tray tray;

	public MainWindow(String[] vals){
		values = vals;
		d = Display.getCurrent();
		main_window = new Shell(d);
		main_window.setText("Warehouse System");
		main_window.setLocation(50, 50);
		main_window.setSize(950, 400);
		main_window.setLayout(new GridLayout(1, false));
		try {
			main_window.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		main_window.addShellListener(this);
		
		pm = new PluginManager();
		
		tray = d.getSystemTray();
		tip = new ToolTip(main_window, SWT.BALLOON | SWT.ICON_INFORMATION);
		if (tray == null){
			MessageBox mb = new MessageBox(main_window, SWT.OK | SWT.ICON_WARNING);
			mb.setMessage("Няма поддръжка от операционната система за икона на системния панел!");
			mb.setText("Внимание");
			mb.open();
			System.out.println();
		}
		else {
			TrayItem trayItem = new TrayItem(tray, 0);
			trayItem.setToolTipText("Warehouse System");
			try {
				trayItem.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "warehouse.png"));
			}
			catch (Exception e) {}
			trayItem.setToolTip(tip);
		}
		
		addMenus();
		addToolbar();
		
		folder = new CTabFolder(main_window, SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));	
		folder.setSimple(true);
		folder.setUnselectedImageVisible(false);
		folder.setUnselectedCloseVisible(false);
		stock = new CTabItem(folder, SWT.NONE);
		stock.setText("Наличност в склада");
		try {
			stock.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "warehouse.png"));
		}
		catch (Exception e) {}
		
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new FillLayout());
		
		addTable(c);
		
		stock.setControl(c);		
		folder.setSelection(stock);
		
		operationsPurchases.setEnabled(false);
		operationsSales.setEnabled(false);
		editItems.setEnabled(false);
		folder.setEnabled(false);
		tiRefresh.setEnabled(false);
		
		main_window.setMinimumSize(950, 400);
		
		connect();
		centerWindow(main_window);
		
		main_window.open();
		while(!main_window.isDisposed())
			if(!d.readAndDispatch())d.sleep();
		d.dispose();
	}
	
	private void centerWindow(Shell shell) {
		Monitor primary = d.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}
	
	private void refreshTable() {
		table.removeAll();
		try {
			for (int row=1; row<=sql.CountItems("Warehouse", "Products"); row++){
				String[] col = sql.GetRow(row);
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
	
	private void connect() {
		try{
			sql = new SQLManager(values[0], values[1], values[2], values[3]);
			tip.setText("Установена връзка");
			tip.setMessage("Връзката с базата от данни е успешна.");
			tip.setVisible(true);

			for (int row=1; row<=sql.CountItems("Warehouse", "Products"); row++){
				String[] col = sql.GetRow(row);
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
		operationsPurchases.setEnabled(true);
		operationsSales.setEnabled(true);
		editItems.setEnabled(true);
		tiRefresh.setEnabled(true);
		folder.setEnabled(true);
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
	
					gc.drawString("Код", 200, hy);
					gc.drawString("Дата", 500, hy);
					gc.drawString("Продукт", 1100, hy);
					gc.drawString("Количество", 2200, hy);
					gc.drawString("Брой", 2800, hy);
					gc.drawString("Ед. Цена", 3100, hy);
					gc.drawString("Общо", 3600, hy);
					gc.drawString("Доставчик", 4000, hy);
					
					gc.drawLine(200, hy + 125, 4900, hy + 125);
	
					for (int i=0; i<ti.length; i++){
						gc.drawString(ti[i].getText(0), 200, ry);
						gc.drawString(ti[i].getText(1),	500, ry);
						gc.drawString(ti[i].getText(2),	1100, ry);
						gc.drawString(ti[i].getText(3),	2200, ry);
						gc.drawString(ti[i].getText(4),	2800, ry);
						gc.drawString(ti[i].getText(5),	3100, ry);
						gc.drawString(ti[i].getText(6),	3600, ry);
						gc.drawString(ti[i].getText(7),	4000, ry);
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
			MessageBox mb = new MessageBox(main_window, SWT.ICON_ERROR);
			mb.setText("Грешка");
			mb.setMessage("Грешка при отпечатване.\n" + e.getMessage());
			mb.open();
		}		
	}
	
	public void shellActivated(ShellEvent event) {}
	public void shellDeactivated(ShellEvent event) {}
	public void shellClosed(ShellEvent event) {
		sql = null;
		pm = null;
		tip.dispose();
		tray.dispose();
		main_window.dispose();
		System.exit(0);
	}
	public void shellIconified(ShellEvent event) {}
	public void shellDeiconified(ShellEvent event) {}

	public void widgetSelected(SelectionEvent event){
		Object ev = event.getSource();
		if (ev == fileNewDB)System.out.println("New database pressed.");
		if (ev == fileOpenDB)System.out.println("Open database pressed.");
		if (ev == fileConnectToDB) {
			
		}
		if (ev == fileCloseDB){
			sql = null;
			table.removeAll();
			fileCloseDB.setEnabled(false);
			fileConnectToDB.setEnabled(true);
			tip.setText("SQL connection closed.");
			tip.setMessage("Connection to database is now closed.");
			tip.setVisible(true);
		}
		if (ev == filePrint) {
			print("Warehouse Job", main_window, table);
		}
		if (ev == fileExit) {
			sql = null;
			pm = null;
			tip.dispose();
			tray.dispose();
			main_window.dispose();
			System.exit(0);
		}
		
		if (ev == operationsPurchases){
			new PurchasesWindow(sql);
			refreshTable(); 
		}
		if (ev == operationsSales) {
			new ChooseWindow(sql);
			refreshTable();
		}
		if (ev == operationsInvoices)System.out.println("Invoices pressed.");
		
		if (ev == editItems) {
			new ModifyWindow(sql);
			refreshTable();
		}
		if (ev == editVAT)System.out.println("VAT pressed.");
		if (ev == editCompanies) new CompaniesWindow(sql);
		if (ev == byDay) new ReportsByDay(sql);
		if (ev == byMonth) new ReportsByMonth(sql);
		if (ev == forPeriod) new ReportsForPeriod(sql);
		if (ev == forAll)System.out.println("For all pressed.");
		
		if (ev == settings)System.out.println("Settings pressed.");
		
		if (ev == closeActiveWindow)System.out.println("Close active window pressed.");
		
		if (ev == documentation) {
			MessageBox mb = new MessageBox(main_window, SWT.OK | SWT.ICON_INFORMATION);
			mb.setMessage("Моля прочетете документация включена в Магистърската Теза към продукта.");
			mb.setText("Документация");
			mb.open();
		}
		if (ev == license) {
			MessageBox mb = new MessageBox(main_window, SWT.OK | SWT.ICON_INFORMATION);
			mb.setMessage("CC, Някои права са запазени.");
			mb.setText("Лиценз");
			mb.open();
		}
		if (ev == about) new AboutWindow();
		if (ev == tiRefresh) refreshTable();
		if (ev == tiPurchases) {
			new PurchasesWindow(sql);
			refreshTable();
		}
		if (ev == tiSalesCode) {
			new DeleteByCodeWindow(sql);
			refreshTable();
		}
		if (ev == tiSalesProduct) {
			new DeleteByProductWindow(sql);
			refreshTable();
		}
		if (ev == tiModify) {
			new ModifyWindow(sql);
			refreshTable();
		}
		if (ev == tiClose) {
			main_window.close();
		}
		if (ev == tiPlugin) {
			
			pm.loadGUI();
		}
	}
	public void widgetDefaultSelected(SelectionEvent event){}
	
	private void addTable(Composite composite){
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		colCode = new TableColumn(table, SWT.CENTER);
		colCode.setText("Код");
		colCode.setWidth(70);
		
		colDate = new TableColumn(table, SWT.CENTER);
		colDate.setText("Дата");
		colDate.setWidth(100);
		
		colProduct = new TableColumn(table, SWT.CENTER);
		colProduct.setText("Продукт");
		colProduct.setWidth(200);
		
		colQuantity = new TableColumn(table, SWT.CENTER);
		colQuantity.setText("Количество");
		colQuantity.setWidth(100);
		
		colType = new TableColumn(table, SWT.CENTER);
		colType.setText("Брой");
		colType.setWidth(70);
		
		colSingle_value = new TableColumn(table, SWT.CENTER);
		colSingle_value.setText("Ед. цена (лв)");
		colSingle_value.setWidth(100);
		
		colValue = new TableColumn(table, SWT.CENTER);
		colValue.setText("Стойност (лв)");
		colValue.setWidth(100);
		
		colCompany = new TableColumn(table, SWT.CENTER);
		colCompany.setText("Доставчик");
		colCompany.setWidth(200);
		
		table.setHeaderVisible(true);
	}
	
	private void addToolbar(){
		toolbar = new ToolBar(main_window, SWT.FLAT | SWT.RIGHT);
		tiClose = new ToolItem(toolbar, 0);
		tiClose.setText("Изход");
		try {
			tiClose.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "close.png"));
		}
		catch (Exception e) {}
		tiClose.addSelectionListener(this);
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		tiRefresh = new ToolItem(toolbar, 0);
		tiRefresh.setText("Обнови");
		try {
			tiRefresh.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "refresh.png"));
		}
		catch (Exception e) {}
		tiRefresh.addSelectionListener(this);
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		tiPurchases = new ToolItem(toolbar, 0);
		tiPurchases.setText("Доставки");
		try {
			tiPurchases.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "add.png"));
		}
		catch (Exception e) {}
		tiPurchases.addSelectionListener(this);
		
		tiSalesCode = new ToolItem(toolbar, 0);
		tiSalesCode.setText("Изтрий код");
		try {
			tiSalesCode.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "remove.png"));
		}
		catch (Exception e) {}
		tiSalesCode.addSelectionListener(this);
		
		tiSalesProduct = new ToolItem(toolbar, 0);
		tiSalesProduct.setText("Изтрий продукт");
		try {
			tiSalesProduct.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "remove.png"));
		}
		catch (Exception e) {}
		tiSalesProduct.addSelectionListener(this);
		
		tiModify = new ToolItem(toolbar, 0);
		tiModify.setText("Редактиране");
		try {
			tiModify.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "edit.png"));
		}
		catch (Exception e) {}
		tiModify.addSelectionListener(this);
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		final String[] plugins = pm.getPlugins();
		for (int i=0; i<plugins.length; i++) plugins[i] = plugins[i].substring(0, plugins[i].length()-6);

		int i=0;
		for ( i=0; i< plugins.length; i++){
			if (plugins[i].equals("IPlugin")) continue;
			if (plugins[i].contains("$")) continue;
			ToolItem ti = new ToolItem(toolbar, 0);
			ti.setText(plugins[i]);
			final String selection = plugins[i];
			ti.addSelectionListener(new SelectionListener(){
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						pm.loadPlugin(selection);
						pm.loadGUI();
					}
					catch (Exception ex) {
						MessageBox mb = new MessageBox(main_window, SWT.ICON_ERROR);
						mb.setMessage("Разширението не поддържа валиден интерфейс!");
						mb.setText("Грешка");
						mb.open();
					}
				}
				
			});
		}		
	}
	
	private void addMenus(){
		bar = new Menu(main_window, SWT.BAR);
		main_window.setMenuBar(bar);
		
		fileMenu = new MenuItem(bar, SWT.CASCADE);
		fileMenu.setText("&Файл");
		subFile = new Menu(main_window, SWT.DROP_DOWN);
		fileMenu.setMenu(subFile);
		filePrint = new MenuItem(subFile, SWT.PUSH);
		filePrint.setText("&Печат");
		filePrint.setAccelerator(SWT.MOD1 + 'P');
		try {
			filePrint.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "print.png"));
		}
		catch (Exception e) {}
		filePrint.addSelectionListener(this);
		new MenuItem(subFile, SWT.SEPARATOR);
		fileExit = new MenuItem(subFile, SWT.PUSH);
		fileExit.setText("Изход");
		fileExit.setAccelerator(SWT.MOD1 + 'X');
		fileExit.addSelectionListener(this);
		
		operationsMenu = new MenuItem(bar, SWT.CASCADE);
		operationsMenu.setText("&Операции");
		subOperations = new Menu(main_window, SWT.DROP_DOWN);
		operationsMenu.setMenu(subOperations);
		operationsPurchases = new MenuItem(subOperations, SWT.PUSH);
		operationsPurchases.setText("Доставки\tF1");
		operationsPurchases.setAccelerator(SWT.F1);
		try {
			operationsPurchases.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "add.png"));
		}
		catch (Exception e) {}
		operationsPurchases.addSelectionListener(this);
		operationsSales = new MenuItem(subOperations, SWT.PUSH);
		operationsSales.setText("Продажби\tF2");
		operationsSales.setAccelerator(SWT.F2);
		try {
			operationsSales.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "remove.png"));
		}
		catch (Exception e) {}
		operationsSales.addSelectionListener(this);
		
		editMenu = new MenuItem(bar, SWT.CASCADE);
		editMenu.setText("&Промяна");
		subEdit = new Menu(main_window, SWT.DROP_DOWN);
		editMenu.setMenu(subEdit);
		editItems = new MenuItem(subEdit, SWT.PUSH);
		editItems.setText("Записи");
		editItems.setAccelerator(SWT.MOD1 + 'E');
		try {
			editItems.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "edit.png"));
		}
		catch (Exception e) {}
		editItems.addSelectionListener(this);
		new MenuItem(subEdit, SWT.SEPARATOR);
		editVAT = new MenuItem(subEdit, SWT.PUSH);
		editVAT.setText("ДДС");
		editVAT.setAccelerator(SWT.MOD1 + 'T');
		try {
			editVAT.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "vat.png"));
		}
		catch (Exception e) {}
		editVAT.addSelectionListener(this);
		editVAT.setEnabled(false);
		editCompanies = new MenuItem(subEdit, SWT.PUSH);
		editCompanies.setText("Доставчици");
		try {
			editCompanies.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "companies.png"));
		}
		catch (Exception e) {}
		editCompanies.addSelectionListener(this);
		
		reportsMenu = new MenuItem(bar, SWT.CASCADE);
		reportsMenu.setText("&Отчет");
		subReports = new Menu(main_window, SWT.DROP_DOWN);
		reportsMenu.setMenu(subReports);
		byDay = new MenuItem(subReports, SWT.PUSH);
		byDay.setText("Дневен");
		try {
			byDay.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "report.png"));
		}
		catch (Exception e) {}
		byDay.addSelectionListener(this);
		byMonth = new MenuItem(subReports, SWT.PUSH);
		byMonth.setText("Месечен");
		try {
			byMonth.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "report.png"));
		}
		catch (Exception e) {}
		byMonth.addSelectionListener(this);
		forPeriod = new MenuItem(subReports, SWT.PUSH);
		forPeriod.setText("За период");
		try {
			forPeriod.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "reports.png"));
		}
		catch (Exception e) {}
		forPeriod.addSelectionListener(this);
		
		helpMenu = new MenuItem(bar, SWT.CASCADE);
		helpMenu.setText("&Помощ");
		subHelp = new Menu(main_window, SWT.DROP_DOWN);
		helpMenu.setMenu(subHelp);
		documentation = new MenuItem(subHelp, SWT.PUSH);
		documentation.setText("Документация");
		documentation.setAccelerator(SWT.MOD1 + '?');
		try {
			documentation.setImage(new Image(main_window.getDisplay(), "icons" + File.separator + "help.png"));
		}
		catch (Exception e) {}
		documentation.addSelectionListener(this);
		license = new MenuItem(subHelp, SWT.PUSH);
		license.setText("Лиценз");
		license.addSelectionListener(this);
		new MenuItem(subHelp, SWT.SEPARATOR);
		about = new MenuItem(subHelp, SWT.PUSH);
		about.setText("Относно");
		about.addSelectionListener(this);
	}
}
