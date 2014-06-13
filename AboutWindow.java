import java.io.File;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;

public class AboutWindow {

	private Display d;
	private Shell window;
	
	public AboutWindow () {
		d = Display.getCurrent();
		window = new Shell(d, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		window.setText("Относно");
		window.setLayout(new GridLayout(1, false));
		try {
			window.setImage(new Image(window.getDisplay(), "icons" + File.separator + "icon_big.png"));
		}
		catch (Exception e) {}
		
		Label lName = new Label(window, 0);
		FontData fd = new FontData();
		Font font = new Font(d, fd);
		fd.setStyle(SWT.BOLD);
		fd.setHeight(30);
		lName.setFont(font);
		lName.setText("Warehouse System");
		
		
		Label lInfo = new Label(window, 0);
		lInfo.setText("© 2014 Кристиян Димов");
		
		Button bClose = new Button(window, SWT.PUSH);
		bClose.setText("Затвори");
		bClose.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		bClose.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event){
				window.close();
			}
		});;
		
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
}
