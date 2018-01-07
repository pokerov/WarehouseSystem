import java.io.File;
import java.sql.SQLException;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

public class DeleteByProductWindow implements SelectionListener {

  private Shell window;
  private Label lCompany;
  private Combo cProduct;
  private Button bDelete;
  private SQLManager sql;
  private String[] products;
  private Display d;
  
  public DeleteByProductWindow (SQLManager sql_man) {
    sql = sql_man;
    d = Display.getCurrent();
    window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
    window.setText("Изтриване продукт");
    
    GridLayout layout = new GridLayout();
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    layout.marginBottom = 10;
    layout.marginLeft = 10;
    layout.marginRight = 10;
    layout.marginTop = 10;
    layout.numColumns = 1;
    window.setLayout(layout);
    try {
      window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
    }
    catch (Exception e) {}
    
    addUI();
    
    window.pack();
    centerWindow(window);
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
  
  private void addUI() {
    GridData widgetGridData = new GridData();
    widgetGridData.widthHint = 150;
    
    cProduct = new Combo(window, SWT.READ_ONLY);
    cProduct.addSelectionListener(this);
    cProduct.setLayoutData(widgetGridData);
    try {
      products = sql.GetProducts();
      cProduct.setItems(products);
    }
    catch (SQLException e) {
      System.out.println("SQL Error: " + e.getMessage());
    }
    catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
    
    lCompany = new Label(window, SWT.NONE);
    lCompany.setText("");
    lCompany.setLayoutData(widgetGridData);
    
    bDelete = new Button(window, SWT.PUSH);
    bDelete.setText("Изтрий");
    bDelete.setBackground(new Color(d, 255, 0, 0));
    bDelete.addSelectionListener(this);
    try {
      bDelete.setImage(new Image(window.getDisplay(), "icons" + File.separator + "remove.png"));
    }
    catch (Exception e) {}
    bDelete.setLayoutData(widgetGridData);
    bDelete.setEnabled(false);
  }
  
  public void widgetDefaultSelected(SelectionEvent event) {}
  public void widgetSelected(SelectionEvent event) {
    Object ev = event.getSource();
    if (ev == cProduct){
      try {
        lCompany.setText(sql.GetCompany(cProduct.getText()));
      }
      catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
      }
      catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
      bDelete.setEnabled(true);
    }
    if (ev == bDelete) {
      try {
        sql.DeleteByName("Warehouse", "Products", cProduct.getText());
      }
      catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
      }
      catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
      window.close();
    }
  }
}

