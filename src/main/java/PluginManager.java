import java.io.*;

@SuppressWarnings({"rawtypes"})
public class PluginManager {

  private String pluginsDir;
  private ClassLoader cl = null;
  private File dir = null;
  private IPlugin pf = null;
  private Class c = null;
  
  public PluginManager() {
    pluginsDir = "plugins";
    dir = new File(System.getProperty("user.dir") + File.separator + pluginsDir);
    cl = new PluginClassLoader(dir);
  }
  
  public void loadPlugin(String pluginName){
    if (dir.exists() && dir.isDirectory()) {
      try {
        c = cl.loadClass(pluginName);
        Class[] cI = c.getInterfaces();
        if (cI[0].getName().equals("IPlugin")) {
          pf = (IPlugin) c.newInstance();
        }
      }
      catch (Exception e) {}
    }
  }
  
  public String getPluginName(){
    return !pf.equals(null) ? pf.pluginName() : "Error";
  }
  
  public void loadGUI(){
    if (!pf.equals(null)) {
      if (!pf.isMainWindowVisible()) pf.loadGUI();
    }
  }
  
  public String[] getPlugins() {
    return dir.list();
  }
  
  protected void finalize(){
    cl = null;
    dir = null;
    pf = null;
    c = null;
  }
}
