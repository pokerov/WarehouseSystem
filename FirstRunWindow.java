import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;

import java.io.File;
import java.sql.*;

public class FirstRunWindow implements SelectionListener {
	
	private Display d;
	private Shell window;
	private Button bCreateDB, bCreateTable, bClose;
	private Label lCreateDB, lCreateTable;
	private String[] args;
	private SQLManager sql;

	public FirstRunWindow (String[] c_args) {
		args = c_args;
		d = Display.getCurrent();
		window = new Shell(d, SWT.DIALOG_TRIM);
		window.setText("Първоначално включване");
		window.setLayout(new GridLayout(2, false));
		try {
			window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		
		try {
			sql = new SQLManager(args[0], args[1], args[2], args[3]);
		}
		catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		addUI();
		
		window.pack();
		centerWindow(window);
		window.open();
		while(!window.isDisposed())
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
	
	private void addUI () {	
		GridData gButtons = new GridData();
		gButtons.widthHint = 150;
		
		GridData gClose = new GridData();
		gClose.widthHint = 150;
		gClose.horizontalSpan = 2;
		gClose.horizontalAlignment = SWT.RIGHT;
		
		lCreateDB = new Label(window, SWT.WRAP);
		lCreateDB.setText("Създава нова база от данни според изискванията");
		
		bCreateDB = new Button(window, SWT.PUSH | SWT.RIGHT);
		bCreateDB.setText("Създай БД");
		bCreateDB.setLayoutData(gButtons);
		bCreateDB.addSelectionListener(this);
		try {
			bCreateDB.setImage(new Image(window.getDisplay(), "icons" + File.separator + "db.png"));
		}
		catch (Exception e) {}
		
		lCreateTable = new Label(window, SWT.WRAP);
		lCreateTable.setText("Създава таблици според изисквания на БД");
		
		bCreateTable = new Button(window, SWT.PUSH | SWT.RIGHT);
		bCreateTable.setText("Създай таблици");
		bCreateTable.setLayoutData(gButtons);
		bCreateTable.addSelectionListener(this);
		try {
			bCreateTable.setImage(new Image(window.getDisplay(), "icons" + File.separator + "table.png"));
		}
		catch (Exception e) {}
		bCreateTable.setEnabled(false);
		
		bClose = new Button(window, SWT.PUSH);
		bClose.setText("Затвори");
		bClose.setLayoutData(gClose);
		bClose.addSelectionListener(this);
	}
	
	public void widgetDefaultSelected (SelectionEvent event) {}
	public void widgetSelected (SelectionEvent event) {
		Object ev = event.getSource();
		if (ev == bCreateDB) {
			try {
				sql.CreateDB("Warehouse");
			}
			catch (SQLException e) {
				System.err.println("SQL Error: " + e.getMessage());
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
			bCreateDB.setEnabled(false);
			bCreateTable.setEnabled(true);
		}
		if (ev == bCreateTable) {
			try {
				sql.AddNewTable("Warehouse", "Products");
			}
			catch (SQLException e) {
				System.err.println("SQL Error: " + e.getMessage());
			}
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
			bCreateTable.setEnabled(false);
		}
		if (ev == bClose) window.dispose();
	}
}
