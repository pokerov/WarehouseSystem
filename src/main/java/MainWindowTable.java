import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class MainWindowTable {
  private CTabFolder cTabFolder;
  private CTabItem cTabItem;
  private Composite composite;
  private Table table;
  private TableColumn tableColumnCode, tableColumnDate, tableColumnProduct, tableColumnQuantity, tableColumnType, tableColumnSingleValue, tableColumnValue, tableColumnCompany;
  private final String OUT_OF_JAR = "src/main/resources/";

  public void addTableFrame(Shell mainWindow) {
    cTabFolder = new CTabFolder(mainWindow, SWT.BORDER);
    cTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));  
    cTabFolder.setSimple(true);
    cTabFolder.setUnselectedImageVisible(false);
    cTabFolder.setUnselectedCloseVisible(false);
    cTabItem = new CTabItem(cTabFolder, SWT.NONE);
    cTabItem.setText("Products in stock");
    try {
      cTabItem.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "warehouse.png"));
    }
    catch (Exception e) {}

    composite = new Composite(cTabFolder, SWT.NONE);
    composite.setLayout(new FillLayout());

    addTable(composite);

    cTabItem.setControl(composite);    
    cTabFolder.setSelection(cTabItem);
  }

  private void addTable(Composite composite){
    table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
    tableColumnCode = new TableColumn(table, SWT.CENTER);
    tableColumnCode.setText("Code");
    tableColumnCode.setWidth(70);

    tableColumnDate = new TableColumn(table, SWT.CENTER);
    tableColumnDate.setText("Date");
    tableColumnDate.setWidth(100);

    tableColumnProduct = new TableColumn(table, SWT.CENTER);
    tableColumnProduct.setText("Product");
    tableColumnProduct.setWidth(200);

    tableColumnQuantity = new TableColumn(table, SWT.CENTER);
    tableColumnQuantity.setText("Quantity");
    tableColumnQuantity.setWidth(100);

    tableColumnType = new TableColumn(table, SWT.CENTER);
    tableColumnType.setText("Type");
    tableColumnType.setWidth(70);

    tableColumnSingleValue = new TableColumn(table, SWT.CENTER);
    tableColumnSingleValue.setText("SingleValue");
    tableColumnSingleValue.setWidth(100);

    tableColumnValue = new TableColumn(table, SWT.CENTER);
    tableColumnValue.setText("Value");
    tableColumnValue.setWidth(100);

    tableColumnCompany = new TableColumn(table, SWT.CENTER);
    tableColumnCompany.setText("Company");
    tableColumnCompany.setWidth(200);

    table.setHeaderVisible(true);
  }

}
