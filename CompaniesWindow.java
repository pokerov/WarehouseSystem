import java.io.File;
import java.sql.SQLException;
import java.util.TreeSet;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;

public class CompaniesWindow {
	
	private Display d;
	private Shell companies_window;
	private List lCompanies;
	private SQLManager sql;
	private String[] sql_companies;
	private TreeSet<String> companies = new TreeSet<>();
	
	public CompaniesWindow(SQLManager sql_man){
		sql = sql_man;
		d = Display.getCurrent();
		companies_window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
		companies_window.setText("Доставчици");
		try {
			companies_window.setImage(new Image(companies_window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		
		GridLayout layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.numColumns = 1;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		companies_window.setLayout(layout);
		
		addUI();
		
		companies_window.pack();
		centerWindow(companies_window);
		companies_window.open();
		while(!companies_window.isDisposed())
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
	
	private void addUI () {
		
		GridData gdList = new GridData();
		gdList.heightHint = 300;
		gdList.widthHint = 200;
		
		lCompanies = new List(companies_window, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		lCompanies.setLayoutData(gdList);
		try {
			sql_companies = sql.GetCompany();
		}
		catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		for (int i=0; i<sql_companies.length; i++){
			companies.add(sql_companies[i]);
		}
		
		lCompanies.setItems(companies.toArray(new String[companies.size()]));
	}
}
