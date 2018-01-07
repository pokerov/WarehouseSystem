import java.io.File;
import java.sql.SQLException;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

public class ModifyWindow implements SelectionListener {

  private Shell window;
  private SQLManager sql;
  private Display d;
  private Label lCode, lProduct, lQuantity, lSingle_value, lValue, lCompany;
  private Text tProduct, tValue;
  private Button bModify;
  private String[] oldValues = new String[7];
  private Combo cCode, cCompany;
  private Spinner sQuantity, sSingle_value;
  private String[] sql_companies;
  
  public ModifyWindow (SQLManager sql_man) {
    sql = sql_man;
    d = Display.getCurrent();
    window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
    window.setText("Редактиране на запис");
    try {
      window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
    }
    catch (Exception e) {}

    GridLayout layout = new GridLayout();
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    layout.marginBottom = 10;
    layout.marginLeft = 10;
    layout.marginRight = 10;
    layout.marginTop = 10;
    layout.numColumns = 2;
    window.setLayout(layout);
    
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
  
  private void addUI () {
    GridData tGridData = new GridData();
    tGridData.widthHint = 150;
    
    lCode = new Label(window, SWT.NONE);
    lCode.setText("Код");
    
    cCode = new Combo(window, SWT.READ_ONLY);
    cCode.addSelectionListener(this);
    cCode.setLayoutData(tGridData);
    try{
      cCode.setItems(sql.GetCodes());
    }
    catch (SQLException e) {
      System.out.println("SQL Error: " + e.getMessage());
    }
    catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
    
    lProduct = new Label(window, SWT.NONE);
    lProduct.setText("Продукт");
    
    tProduct = new Text(window, SWT.BORDER | SWT.SINGLE);
    tProduct.setLayoutData(tGridData);
    tProduct.setEnabled(false);
    
    lQuantity = new Label(window, SWT.NONE);
    lQuantity.setText("Количество");
    
    sQuantity = new Spinner(window, SWT.BORDER | SWT.WRAP);
    sQuantity.setLayoutData(tGridData);
    sQuantity.setMinimum(0);
    sQuantity.setMaximum(999);
    sQuantity.setEnabled(false);
    
    lSingle_value = new Label(window, SWT.NONE);
    lSingle_value.setText("Ед. цена (лв)");
    
    sSingle_value = new Spinner(window, SWT.BORDER | SWT.WRAP);
    sSingle_value.setLayoutData(tGridData);
    sSingle_value.setMinimum(0); // need a bit thinking
    sSingle_value.setMaximum(99999);
    sSingle_value.setEnabled(false);
    sSingle_value.addModifyListener(new ModifyListener(){
      public void modifyText(ModifyEvent event) {
        tValue.setText(String.valueOf(sSingle_value.getSelection() * sQuantity.getSelection()));
      }
    });
    
    lValue = new Label(window, SWT.NONE);
    lValue.setText("Стойност (лв)");
    
    tValue = new Text(window, SWT.BORDER | SWT.SINGLE);
    tValue.setLayoutData(tGridData);
    tValue.setEnabled(false);
    
    lCompany = new Label(window, SWT.NONE);
    lCompany.setText("Доставчик");
    
    cCompany = new Combo(window, SWT.DROP_DOWN);
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
    cCompany.setEnabled(false);
    
    GridData gd = new GridData();
    gd.horizontalAlignment = SWT.FILL;
    gd.horizontalSpan = 2;
    bModify = new Button(window, SWT.PUSH);
    bModify.setText("Приложи промените");
    bModify.setLayoutData(gd);
    bModify.addSelectionListener(this);
    bModify.setBackground(new Color(d, 255, 0, 0));
    bModify.setEnabled(false);
    try {
      bModify.setImage(new Image(window.getDisplay(), "icons" + File.separator + "edit.png"));
    }
    catch (Exception e) {}
  }
  
  public void widgetDefaultSelected (SelectionEvent event) {}
  public void widgetSelected (SelectionEvent event) {
    Object ev = event.getSource();
    if (ev == cCode) {
      try {
        oldValues = sql.GetRowByCode(Integer.parseInt(cCode.getText()));
      }
      catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
      }
      catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
      
      bModify.setEnabled(true);
      sQuantity.setEnabled(true);
      sSingle_value.setEnabled(true);
      cCompany.setEnabled(true);
      tProduct.setText(oldValues[1]);
      sQuantity.setSelection(Integer.parseInt(oldValues[2]));
      sSingle_value.setSelection(Integer.parseInt(oldValues[4]));
      tValue.setSelection(Integer.parseInt(oldValues[5]));
      cCompany.setText(oldValues[6]);
    }
    if (ev == bModify){
      try {
        sql.ModifyEntry("Warehouse", "Products", Integer.parseInt(cCode.getText()), Integer.parseInt(oldValues[2]), Integer.parseInt(oldValues[4]), Integer.parseInt(oldValues[5]), oldValues[6], 
            sQuantity.getSelection(), sSingle_value.getSelection(), Integer.parseInt(tValue.getText()), cCompany.getText());
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
