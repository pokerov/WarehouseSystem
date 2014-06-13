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

public class ReportsForPeriod implements SelectionListener {

	private Display d;
	private Shell window;
	private SQLManager sql;
	private Combo cFDays, cFMonths, cTDays, cTMonths;
	private Label lFrom, lTo, lError;
	private Table table;
	private TableColumn colCode, colDate, colProduct, colQuantity, colType, colSingle_value, colValue, colCompany;
	private String[] days = new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","21","21","22","23","24","25","26","27","28","29","30","31"};
	private String[] months = new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
	private Composite c;
	private Button bShow, bPrint;
	
	public ReportsForPeriod (SQLManager sql_man) {
		sql = sql_man;
		d = Display.getCurrent();
		window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
		window.setText("����� �� ������");
		try {
			window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		window.setLayout(new GridLayout(2, false));
		
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
					gc.drawString("����� �� ������ (" + cFDays.getText() + "-" + cFMonths.getText() + " �� " + cTDays.getText() + "-" + cTMonths.getText() + ")", 200, 150);
					
					gc.setFont(new Font(d, "", SWT.NONE, SWT.NONE));
					gc.drawString("���", 200, hy);
					gc.drawString("����", 500, hy);
					gc.drawString("�������", 1100, hy);
					gc.drawString("����������", 2200, hy);
					gc.drawString("����", 2800, hy);
					gc.drawString("��. ����", 3100, hy);
					gc.drawString("����", 3600, hy);
					gc.drawString("���������", 4000, hy);
					
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
			mb.setText("������");
			mb.setMessage("������ ��� �����������.\n" + e.getMessage());
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
		gTable.horizontalSpan = 2;
		gTable.verticalAlignment = SWT.FILL;
		gTable.horizontalAlignment = SWT.FILL;
		
		c = new Composite(window, 0);
		RowLayout rl = new RowLayout();
		rl.wrap = true;
		rl.type = SWT.HORIZONTAL;
		rl.marginLeft = 10;
		rl.marginRight = 10;
		rl.center = true;
		rl.pack = true;
		c.setLayout(rl);
		
		lFrom = new Label(c, 0);
		lFrom.setText("��");
		
		cFDays = new Combo(c, SWT.READ_ONLY);
		cFDays.setItems(days);
		
		cFMonths = new Combo(c, SWT.READ_ONLY);
		cFMonths.setItems(months);
		
		lTo = new Label(c, 0);
		lTo.setText("��");
		
		cTDays = new Combo(c, SWT.READ_ONLY);
		cTDays.setItems(days);
		
		cTMonths = new Combo(c, SWT.READ_ONLY);
		cTMonths.setItems(months);
		
		bShow = new Button(c, SWT.PUSH);
		bShow.addSelectionListener(this);
		bShow.setText("������");
		
		bPrint = new Button(c, SWT.PUSH | SWT.RIGHT);
		bPrint.setText("�����");
		bPrint.addSelectionListener(this);
		try {
			bPrint.setImage(new Image(window.getDisplay(), "icons" + File.separator + "print.png"));
		}
		catch (Exception e) {}
		bPrint.setEnabled(false);
		
		lError = new Label(c, 0);
		lError.setBackground(new Color(d, 255, 0, 0));
		
		c.pack();
		
		table = new Table(window, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayout(new FillLayout());
		table.setLayoutData(gTable);
		colCode = new TableColumn(table, SWT.CENTER);
		colCode.setText("���");
		colCode.setWidth(70);
		
		colDate = new TableColumn(table, SWT.CENTER);
		colDate.setText("����");
		colDate.setWidth(100);
		
		colProduct = new TableColumn(table, SWT.CENTER);
		colProduct.setText("�������");
		colProduct.setWidth(200);
		
		colQuantity = new TableColumn(table, SWT.CENTER);
		colQuantity.setText("����������");
		colQuantity.setWidth(100);
		
		colType = new TableColumn(table, SWT.CENTER);
		colType.setText("����");
		colType.setWidth(70);
		
		colSingle_value = new TableColumn(table, SWT.CENTER);
		colSingle_value.setText("��. ���� (��)");
		colSingle_value.setWidth(100);
		
		colValue = new TableColumn(table, SWT.CENTER);
		colValue.setText("�������� (��)");
		colValue.setWidth(100);
		
		colCompany = new TableColumn(table, SWT.CENTER);
		colCompany.setText("���������");
		colCompany.setWidth(200);
		
		table.setHeaderVisible(true);
	}
	
	public void widgetDefaultSelected (SelectionEvent event) {}
	public void widgetSelected (SelectionEvent event) {
		Object ev = event.getSource();
		if (ev == bShow){
			try {
				table.removeAll();
				for (int i=1; i<=sql.CountItems("Warehouse", "Products"); i++){
					String[] col = sql.GetRowForPeriod(i, cFDays.getText(), cFMonths.getText(), cTDays.getText(), cTMonths.getText());
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(new String[]{col[0], col[7], col[1], col[2], col[3], col[4], col[5], col[6]});
				}
			}
			catch (SQLException e) {}
			catch (Exception e) {}
			bPrint.setEnabled(true);
		}
		
		if (ev == bPrint) print("Warehouse for Period", window, table);
	}
}
