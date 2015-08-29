
public class Program {  
  public static void main(String[] args){
    try {
      for (int i=0; i<args.length; i++){
        if (args.length == 0) continue;
        if (args[0].equals("first") && !args[0].isEmpty()) new ConnectWindow(true);
      }
    }
    catch (ArrayIndexOutOfBoundsException e) {}
    catch (Exception e) {}
    try {
      new ConnectWindow(false);
    }
    catch (Exception e) {}
  }
}
