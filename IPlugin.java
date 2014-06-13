
public interface IPlugin {
	public void startPlugin(String[] args);
	public void stopPlugin();
	public void loadGUI();
	public boolean hasError();
	public String pluginName();
	public int pluginVersion();
	public boolean isMainWindowVisible();
}
