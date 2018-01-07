import java.io.File;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;

public class AboutWindow {

  private final String OUT_OF_PACKAGE = "src/main/resources/";
  private Display currentDisplay;
  private Shell currentWindow;

  public AboutWindow () {
    currentDisplay = Display.getCurrent();
    currentWindow = new Shell(currentDisplay, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    currentWindow.setText("About");
    currentWindow.setLayout(new GridLayout(1, false));
    try {
      currentWindow.setImage(new Image(currentWindow.getDisplay(), OUT_OF_PACKAGE + "icons" + File.separator + "icon_big.png"));
    }
    catch (Exception e) {}

    Label labelName = new Label(currentWindow, 0);
    FontData fontData = new FontData();
    Font font = new Font(currentDisplay, fontData);
    fontData.setStyle(SWT.BOLD);
    fontData.setHeight(30);
    labelName.setFont(font);
    labelName.setText("Warehouse System");

    Label labelInfo = new Label(currentWindow, 0);
    labelInfo.setText("(c) 2014 Kristiyan Dimov");

    Button buttonClose = new Button(currentWindow, SWT.PUSH);
    buttonClose.setText("Close");
    buttonClose.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    buttonClose.addListener(SWT.Selection, new Listener(){
      public void handleEvent(Event event){
        currentWindow.close();
      }
    });;

    currentWindow.pack();
    centerWindow(currentWindow);
    currentWindow.open();
    while(!currentWindow.isDisposed())
      if(!currentDisplay.readAndDispatch())currentDisplay.sleep();
  }

  private void centerWindow(Shell shell) {
    Monitor primaryMonitor = currentDisplay.getPrimaryMonitor();
    Rectangle rectangleMonitor = primaryMonitor.getBounds();
    Rectangle rectangle = shell.getBounds();
    int x = rectangleMonitor.x + (rectangleMonitor.width - rectangle.width) / 2;
    int y = rectangleMonitor.y + (rectangleMonitor.height - rectangle.height) / 2;
    shell.setLocation(x, y);
  }
}
