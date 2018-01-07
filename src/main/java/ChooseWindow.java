import java.io.File;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

public class ChooseWindow implements SelectionListener {

  private Shell choose_window;
  private Button bDeleteByCode, bDeleteByProduct, bCancel;
  private SQLManager sql;
  private Display d;
  
  public ChooseWindow (SQLManager sql_man) {
    sql = sql_man;
    d = Display.getCurrent();
    choose_window = new Shell(d, SWT.APPLICATION_MODAL | SWT.CLOSE);
    choose_window.setText("Продажби");
    
    GridLayout layout = new GridLayout();
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    layout.marginBottom = 10;
    layout.marginLeft = 10;
    layout.marginRight = 10;
    layout.marginTop = 10;
    layout.numColumns = 1;
    choose_window.setLayout(layout);
    try {
      choose_window.setImage(new Image(choose_window.getDisplay(), "icons" + File.separator + "icon_big.png"));
    }
    catch (Exception e) {}
    
    addUI();
    
    choose_window.pack();
    centerWindow(choose_window);
    choose_window.open();
    while(!choose_window.isDisposed())
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
    GridData bGdata = new GridData();
    bGdata.widthHint = 150;
    
    bDeleteByCode = new Button(choose_window, SWT.PUSH);
    bDeleteByCode.setText("Изтрий код");
    bDeleteByCode.setBackground(new Color(d, 255, 0, 0));
    bDeleteByCode.addSelectionListener(this);
    bDeleteByCode.setLayoutData(bGdata);
    
    bDeleteByProduct = new Button(choose_window, SWT.PUSH);
    bDeleteByProduct.setText("Изтрий продукт");
    bDeleteByProduct.setBackground(new Color(d, 255, 0, 0));
    bDeleteByProduct.addSelectionListener(this);
    bDeleteByProduct.setLayoutData(bGdata);
    
    bCancel = new Button(choose_window, SWT.PUSH);
    bCancel.setText("Отказ");
    bCancel.addSelectionListener(this);
    bCancel.setLayoutData(bGdata);
  }
  
  public void widgetDefaultSelected(SelectionEvent event) {}
  public void widgetSelected(SelectionEvent event) {
    Object ev = event.getSource();
    if (ev == bDeleteByCode) {
      choose_window.setVisible(false);
      new DeleteByCodeWindow(sql);
      choose_window.close();
    }
    if (ev == bDeleteByProduct) {
      choose_window.setVisible(false);
      new DeleteByProductWindow(sql);
      choose_window.close();
    }
    if (ev == bCancel) choose_window.dispose();
  }
}
