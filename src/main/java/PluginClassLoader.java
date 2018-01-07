import java.io.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PluginClassLoader extends ClassLoader {

  private File directory;
  
  private Class c;
  
  public PluginClassLoader (File dir) {
    directory = dir;
  }
  
  public Class loadClass (String name) throws ClassNotFoundException {
    return loadClass(name, true);
  }
  
  public Class loadClass (String classname, boolean resolve) throws ClassNotFoundException {
    try {
      c = findLoadedClass(classname);
      if (c == null) {
        try { c = findSystemClass(classname); }
        catch (Exception e) {}
      }
      if (c == null) {
        String filename = classname.replace('.', File.separatorChar) + ".class";
        File f = new File(directory, filename);
        int length = (int) f.length();
        byte[] classbytes = new byte[length];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        in.readFully(classbytes);
        in.close();
        c = defineClass(classname, classbytes, 0, length);
      }
      if (resolve) resolveClass(c);
      return c;
    }
    catch (Exception e) {
      throw new ClassNotFoundException(e.toString());
    }
  }
}
