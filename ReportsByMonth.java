import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

import java.io.File;
import java.sql.SQLException;
import java.text.*;
import java.util.Date;

public class ReportsByMonth implements SelectionListener {

	private Display d;
	private Shell window;
	private SQLManager sql;
	private Combo cDate;
	private Label lDate;
	private Table table;
	private TableColumn colCode, colDate, colProduct, colQuantity, colType, colSingle_value, colValue, colCompany;
	private String[] dates = new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
	private Button bPrint;
	
	public ReportsByMonth (SQLManager sql_man) {
		sql = sql_man;
		d = Display.getCurrent();
		window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
		window.setText("Месечен отчет");
		try {
			window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		window.setLayout(new GridLayout(3, false));
		
		addUI();
		
		window.pack();
		centerWindow(window);
		window.open();
		while(!window.isDisposed())
			if(d.readAndDispatch())d.sleep();
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
					int hy = 300;
					int ry = 450;
					
					gc.setFont(new Font(d, "", SWT.NONE, SWT.BOLD));
					gc.drawString("Отчет по месеци", 200, 150);
					
					gc.setFont(new Font(d, "", SWT.NONE, SWT.NONE));
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
			MessageBox mb = new MessageBox(window, SWT.ICON_ERROR);
			mb.setText("Грешка");
			mb.setMessage("Грешка при отпечатване.\n" + e.getMessage());
			mb.open();
		}
	}
	
	private void centerWindow(Shell shell) {
		Monitor primary = d.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}
	
	private void addUI () {
		GridData gTable = new GridData();
		gTable.widthHint = 950;
		gTable.heightHint = 400;
		gTable.horizontalSpan = 3;
		gTable.verticalAlignment = SWT.FILL;
		gTable.horizontalAlignment = SWT.FILL;
		
		lDate = new Label(window, 0);
		lDate.setText("Изберете месец");
		
		cDate = new Combo(window, SWT.READ_ONLY);
		cDate.addSelectionListener(this);
		cDate.setItems(dates);
		
		bPrint = new Button(window, SWT.PUSH | SWT.RIGHT);
		bPrint.setText("Печат");
		bPrint.addSelectionListener(this);
		try {
			bPrint.setImage(new Image(window.getDisplay(), "icons" + File.separator + "print.png"));
		}
		catch (Exception e) {}
		bPrint.setEnabled(false);
		
		table = new Table(window, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayout(new FillLayout());
		table.setLayoutData(gTable);
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
	
	public void widgetDefaultSelected (SelectionEvent event) {}
	public void widgetSelected (SelectionEvent event) {
		Object ev = event.getSource();
		if (ev == cDate) {
			try {
				table.removeAll();
				for (int j=1; j<= sql.CountEqualDateItems(); j++){
					String[] col = sql.GetRowByMonth(cDate.getText(), j);
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(new String[]{col[0], col[7], col[1], col[2], col[3], col[4], col[5], col[6]});
				}
				
			}
			catch (SQLException e) {}
			catch (Exception e) {}
			bPrint.setEnabled(true);
		}
		if (ev == bPrint) print("Warehouse by Month", window, table);
	}
}
