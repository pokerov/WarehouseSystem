import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class MainWindowToolBar {

  private final String OUT_OF_JAR = "src/main/resources/";

  private ToolBar toolBar;
  protected ToolItem toolItemRefresh, toolItemPurchases,
                     toolItemSalesCodes, toolItemSalesProducts,
                     toolItemModify, toolItemClose, toolItemPlugin;
  
  public final void addToolbar(Shell mainWindow, SelectionListener selectionListener, PluginManager pluginManager){
    toolBar = new ToolBar(mainWindow, SWT.FLAT | SWT.RIGHT);
    toolItemClose = new ToolItem(toolBar, 0);
    toolItemClose.setText("Close");
    try {
      toolItemClose.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "close.png"));
    }
    catch (Exception e) {}
    toolItemClose.addSelectionListener(selectionListener);
    
    new ToolItem(toolBar, SWT.SEPARATOR);
    
    toolItemRefresh = new ToolItem(toolBar, 0);
    toolItemRefresh.setText("Refresh");
    try {
      toolItemRefresh.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "refresh.png"));
    }
    catch (Exception e) {}
    toolItemRefresh.addSelectionListener(selectionListener);
    
    new ToolItem(toolBar, SWT.SEPARATOR);
    
    toolItemPurchases = new ToolItem(toolBar, 0);
    toolItemPurchases.setText("Purchases");
    try {
      toolItemPurchases.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "add.png"));
    }
    catch (Exception e) {}
    toolItemPurchases.addSelectionListener(selectionListener);
    
    toolItemSalesCodes = new ToolItem(toolBar, 0);
    toolItemSalesCodes.setText("Sales Code");
    try {
      toolItemSalesCodes.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "remove.png"));
    }
    catch (Exception e) {}
    toolItemSalesCodes.addSelectionListener(selectionListener);
    
    toolItemSalesProducts = new ToolItem(toolBar, 0);
    toolItemSalesProducts.setText("Sales Product");
    try {
      toolItemSalesProducts.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "remove.png"));
    }
    catch (Exception e) {}
    toolItemSalesProducts.addSelectionListener(selectionListener);
    
    toolItemModify = new ToolItem(toolBar, 0);
    toolItemModify.setText("Modify");
    try {
      toolItemModify.setImage(new Image(mainWindow.getDisplay(), OUT_OF_JAR + "icons" + File.separator + "edit.png"));
    }
    catch (Exception e) {}
    toolItemModify.addSelectionListener(selectionListener);
    
    new ToolItem(toolBar, SWT.SEPARATOR);
    final String[] plugins = pluginManager.getPlugins();
    for (int i=0; i<plugins.length; i++) plugins[i] = plugins[i].substring(0, plugins[i].length()-6);

    int i=0;
    for ( i=0; i< plugins.length; i++){
      if (plugins[i].equals("IPlugin")) continue;
      if (plugins[i].contains("$")) continue;
      ToolItem ti = new ToolItem(toolBar, 0);
      ti.setText(plugins[i]);
      final String selection = plugins[i];
      ti.addSelectionListener(new SelectionListener(){
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {}
        @Override
        public void widgetSelected(SelectionEvent e) {
          try {
            pluginManager.loadPlugin(selection);
            pluginManager.loadGUI();
          }
          catch (Exception ex) {
            MessageBox mb = new MessageBox(mainWindow, SWT.ICON_ERROR);
            mb.setMessage("Extension, does not support valid API!");
            mb.setText("Error");
            mb.open();
          }
        }
        
      });
    }    
  }
}
