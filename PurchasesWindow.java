import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.text.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class PurchasesWindow implements SelectionListener {
	
	private Shell purchases_window;
	
	private Label lDate, lCode, lProduct, lQuantity, lType, lSingle_value, lValue, lCompany;
	private Text tProduct, tValue;
	private Button bSubmit;
	private String[] values = new String[10];
	private SQLManager sql;
	private Combo cType, cCompany;
	private Spinner sCode, sQuantity, sSingle_value, sDay, sMonth, sYear;
	private String[] sql_companies;
	private Display d;

	public PurchasesWindow(SQLManager sql_man){
		sql = sql_man;
		d = Display.getCurrent();
		purchases_window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
		purchases_window.setText("Доставки");
		
		GridLayout layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		
		purchases_window.setLayout(layout);
		try {
			purchases_window.setImage(new Image(purchases_window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		
		addUI();
		
		purchases_window.pack();
		centerWindow(purchases_window);
		purchases_window.open();
		while(!purchases_window.isDisposed())
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
		
		GridData tGridData = new GridData();
		tGridData.widthHint = 150;
		
		lCode = new Label(purchases_window, SWT.NONE);
		lCode.setText("Код");
		
		sCode = new Spinner(purchases_window, SWT.BORDER | SWT.WRAP);
		sCode.setLayoutData(tGridData);
		sCode.setTextLimit(5);
		sCode.setMinimum(0);
		sCode.setMaximum(99999);
		
		lDate = new Label(purchases_window, SWT.NONE);
		lDate.setText("Дата");
		
		Composite c = new Composite(purchases_window, SWT.NONE);
		GridData gdc = new GridData();
		gdc.horizontalAlignment = SWT.FILL;
		gdc.verticalAlignment = SWT.FILL;
		
		c.setLayout(new GridLayout(3, true));
		c.setLayoutData(gdc);
		sDay = new Spinner(c, SWT.BORDER | SWT.WRAP);
		sDay.setMinimum(1);
		sDay.setMaximum(31);
		sDay.setTextLimit(2);
		sMonth = new Spinner(c, SWT.BORDER |  SWT.WRAP);
		sMonth.setMinimum(1);
		sMonth.setMaximum(12);
		sMonth.setTextLimit(2);
		sYear = new Spinner(c, SWT.BORDER |  SWT.WRAP);
		sYear.setMinimum(2014);
		sYear.setMaximum(2100);
		sYear.setTextLimit(4);
		
		DateFormat day = new SimpleDateFormat("dd");
		sDay.setSelection(Integer.parseInt(day.format(new Date())));
		DateFormat month = new SimpleDateFormat("MM");
		sMonth.setSelection(Integer.parseInt(month.format(new Date())));
		DateFormat year = new SimpleDateFormat("yyyy");
		sYear.setSelection(Integer.parseInt(year.format(new Date())));
		
		
		lProduct = new Label(purchases_window, SWT.NONE);
		lProduct.setText("Продукт");
		
		tProduct = new Text(purchases_window, SWT.BORDER | SWT.SINGLE);
		tProduct.setLayoutData(tGridData);
		tProduct.setTextLimit(50);
		tProduct.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event){
				tProduct.setBackground(new Color(d, 255, 255, 255));
			}
		});
		
		lQuantity = new Label(purchases_window, SWT.NONE);
		lQuantity.setText("Количество");
		
		sQuantity = new Spinner(purchases_window, SWT.BORDER | SWT.WRAP);
		sQuantity.setLayoutData(tGridData);
		sQuantity.setMinimum(0);
		sQuantity.setMaximum(999);
		sQuantity.setTextLimit(3);
		
		lType = new Label(purchases_window, SWT.NONE);
		lType.setText("Брой");
		
		cType = new Combo(purchases_window, SWT.READ_ONLY);
		cType.setLayoutData(tGridData);
		cType.setItems(new String[]{"бр.", "кутия", "пакет", "стек"});
		cType.select(0);
		
		lSingle_value = new Label(purchases_window, SWT.NONE);
		lSingle_value.setText("Ед. цена (лв)");
		
		sSingle_value = new Spinner(purchases_window, SWT.BORDER | SWT.WRAP);
		sSingle_value.setLayoutData(tGridData);
		sSingle_value.setMinimum(0);
		sSingle_value.setMaximum(99999);
		sSingle_value.setTextLimit(5); // need a bit thinking
		sSingle_value.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				tValue.setText(String.valueOf(sSingle_value.getSelection() * sQuantity.getSelection()));
			}
			
		});
		
		lValue = new Label(purchases_window, SWT.NONE);
		lValue.setText("Стойност (лв)");
		
		tValue = new Text(purchases_window, SWT.BORDER | SWT.SINGLE);
		tValue.setLayoutData(tGridData);
		tValue.setText("0");
		tValue.setEnabled(false);
		
		lCompany = new Label(purchases_window, SWT.NONE);
		lCompany.setText("Доставчик");
		
		cCompany = new Combo(purchases_window, SWT.DROP_DOWN);
		cCompany.setLayoutData(tGridData);
		cCompany.setTextLimit(50);
		try {
			sql_companies = sql.GetCompany();
		}
		catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		cCompany.setItems(sql_companies);
		cCompany.select(0);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 2;
		bSubmit = new Button(purchases_window, SWT.PUSH);
		bSubmit.setText("Добави");
		bSubmit.setLayoutData(gd);
		bSubmit.addSelectionListener(this);
		try {
			bSubmit.setImage(new Image(purchases_window.getDisplay(), "icons" + File.separator + "add.png"));
		}
		catch (Exception e) {}
	}
	
	private void setValues(String code, String product, String quantity, String type, String svalue, String value, String company, String day, String month, String year) {
		values[0] = code.toString();
		values[1] = product.toString();
		values[2] = quantity.toString();
		values[3] = type.toString();
		values[4] = svalue.toString();
		values[5] = value.toString();
		values[6] = company.toString();
		values[7] = day.toString();
		values[8] = month.toString();
		values[9] = year.toString();
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {}
	public void widgetSelected(SelectionEvent event) {
		Object ev = event.getSource();
		if (ev == bSubmit) {
			try {
				if (!tProduct.getText().isEmpty() && !sql.CheckCodeExist(sCode.getText())) {
					setValues(sCode.getText(), tProduct.getText(), sQuantity.getText(), cType.getText(), sSingle_value.getText(), tValue.getText(), cCompany.getText(), sDay.getText(), sMonth.getText(), sYear.getText());
					String selDate = values[9] + "-" + values[8] + "-" + values[7];
					sql.AddNewEntry(Integer.parseInt(values[0]), values[1], Integer.parseInt(values[2]), values[3], Integer.parseInt(values[4]), Integer.parseInt(values[5]), values[6], selDate);
					purchases_window.close();
				}
				else {
					tProduct.setBackground(new Color(d, 255, 0, 0));
					sCode.setBackground(new Color(d, 255, 0, 0));
				}
			}
			catch (SQLException e) {
				System.out.println("SQL Error: " + e.getMessage());
			}
			catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
}
