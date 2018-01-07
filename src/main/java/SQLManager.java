import java.sql.*;
import java.text.*;
import java.util.Date;

public class SQLManager{

  private Connection conn;
  private Statement st;
  private ResultSet rs;

  public SQLManager(String address, String port, String user, String password) throws SQLException {
    conn = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/", user, password);
    st = conn.createStatement();
  }
  
  public void CreateDB(String db_name) throws SQLException{
    st.executeUpdate("CREATE DATABASE " + db_name);
  }
  
  public void AddNewTable(String db_name, String table) throws SQLException{ 
    st.executeUpdate("CREATE TABLE " + db_name + "." + table + 
        " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, Code INT, Product varchar(50), Quantity INT, Type varchar(10), Single_value INT, Value INT, Company varchar(50), DateIns date, VAT INT);");
  }
  
  public void AddNewTableCompanies(String db_name, String table) throws SQLException{
    st.executeUpdate("CREATE TABLE " + db_name + "." + table + " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, Company varchar(50));");
  }

  public void EmptyTable(String db_name, String table) throws SQLException{
    st.executeUpdate("DELETE FROM " + db_name + "." + table);
  }
  
  public void AddNewEntry(int code, String product, int quantity, String type, int svalue, int value, String company, String date) throws SQLException{
    st.executeUpdate("INSERT INTO " + "Warehouse" + "." + "Products" + "(Code, Product, Quantity, Type, Single_value, Value, Company, DateIns)" 
        + " VALUES ('" + code + "', '" + product + "', '" + quantity + "', '" + type + "', '" + svalue + "', '" + value + "', '" + company + "', '" + date + "');");
  }
  
  public void DeleteByName(String db_name, String table, String product) throws SQLException {
    st.executeUpdate("DELETE FROM " + db_name + "." + table + " WHERE Product='" + product + "';");
  }
  
  public void DeleteByCode(String db_name, String table, int code) throws SQLException {
    st.executeUpdate("DELETE FROM " + db_name + "." + table + " WHERE Code='" + code + "';");
  }
  
  public void ModifyEntry(String db_name, String table,int code, int quantity, int svalue, int value, String company, int n_quantity, int n_svalue, int n_value, String n_company) throws SQLException{
    st.executeUpdate("UPDATE " + db_name + "." + table + " SET Code=" + code + ", Quantity=" + n_quantity + ", Single_value=" + n_svalue + ", Value=" + n_value + ", Company='" + n_company
        + "' WHERE Code=" + code + " AND Quantity=" + quantity + " AND Single_value=" + svalue + " AND Value=" + value + " AND Company='" + company + "';");
  }
  
  public void PrintQuery(String query) throws SQLException{
    rs = st.executeQuery(query);
    while(rs.next())
      System.out.println(rs.getRow() + "| " + rs.getObject(1) + " " + rs.getObject(2) + " " + rs.getObject(3)+ " " + rs.getObject(4)+ " " + rs.getObject(5)+ " " + rs.getObject(6)+ " " + rs.getObject(7)+ " " + rs.getObject(8));
  }
  
  public int CountItems(String db_name, String table) throws SQLException{
    int counter = 0;
    rs = st.executeQuery("SELECT COUNT(*) FROM " + db_name + "." + table + ";");
    if(rs.next())counter = rs.getInt(1);
    return counter;
  }
  
  public int CountDateItems() throws SQLException {
    int count = 0;
    rs = st.executeQuery("SELECT COUNT(DISTINCT DateIns) FROM Warehouse.Products;");
    if(rs.next()) count = rs.getInt(1);
    return count;
  }
  
  public int CountEqualDateItems() throws SQLException { 
    int count = 0;
    rs = st.executeQuery("SELECT COUNT(DateIns) FROM Warehouse.Products;");
    if(rs.next()) count = rs.getInt(1);
    return count;
  }
  
  public int CountMonths() throws SQLException {
    int count = 0;
    rs = st.executeQuery("SELECT COUNT(DISTINCT(DateIns)) FROM Warehouse.Products;");
    if(rs.next()) count = rs.getInt(1);
    return count;
  }
  
  public boolean CheckCodeExist(String code) throws SQLException {
    int[] codes = new int[CountItems("Warehouse", "Products")];
    
    rs = st.executeQuery("SELECT Code FROM Warehouse.Products;");
    while(rs.next())
      codes[rs.getRow() - 1] = rs.getInt("Code");
    
    for (int i=0; i<codes.length; i++) {
      if (code.contains(Integer.toString(codes[i]))) return true;
    }
    
    return false;
  }
  
  public String[] GetCodes() throws SQLException {
    
    String[] codes = new String[CountItems("Warehouse", "Products")];
    
    rs = st.executeQuery("SELECT Code FROM Warehouse.Products;");
    while(rs.next()) {
      codes[rs.getRow()-1] = (String) rs.getObject("Code").toString();
    }
    
    return codes;
  }
  
  public String[] GetProducts () throws SQLException {
    String[] products = new String [CountItems("Warehouse", "Products")];
    
    rs = st.executeQuery("SELECT Product FROM Warehouse.Products;");
    while(rs.next())
      products[rs.getRow() - 1] = rs.getString("Product");
    
    return products;
  }
  
  public String GetProduct(int code) throws SQLException {
  
    rs = st.executeQuery("SELECT Product FROM Warehouse.Products WHERE Products.Code=" + code + ";");
    rs.absolute(1);
    return rs.getString(1);
  }
  
  public String GetCompany(String product) throws SQLException {
    rs = st.executeQuery("SELECT Company FROM Warehouse.Products WHERE Products.Product='" + product + "';");
    rs.absolute(1);
    return rs.getString(1);
  }
  
  public String GetCompany(int code) throws SQLException {
    rs = st.executeQuery("SELECT Company FROM Warehouse.Products WHERE Products.code=" + code + ";");
    rs.absolute(1);
    return rs.getString(1);
  }
  
  public String[] GetCompany() throws SQLException {
    String[] companies = new String[CountItems("Warehouse", "Products")];
    rs = st.executeQuery("SELECT Company FROM Warehouse.Products;");
    while(rs.next())
      companies[rs.getRow() - 1] = rs.getString("Company");
    return companies;
  }
  
  public String[] GetRowByCode(int code) throws SQLException {
    String[] item = new String[8];
    rs = st.executeQuery("SELECT * FROM Warehouse.Products WHERE Products.Code=" + code + ";");
    rs.absolute(1);
    item[0] = (String) rs.getObject("Code").toString();
    item[1] = (String) rs.getObject("Product");
    item[2] = (String) rs.getObject("Quantity").toString();
    item[3] = (String) rs.getObject("Type");
    item[4] = (String) rs.getObject("Single_value").toString();
    item[5] = (String) rs.getObject("Value").toString();
    item[6] = (String) rs.getObject("Company");
    Date date = new Date();
    date = rs.getDate("DateIns");
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    item[7] = dateFormat.format(date);
    return item;
  }
  
  public String[] GetRowByDay(String day, int row) throws SQLException {
    String[] col = new String[8];
    rs = st.executeQuery("SELECT * FROM Warehouse.Products WHERE DAYOFMONTH(Products.DateIns) = " + day +";");
    rs.absolute(row);
    col[0] = (String) rs.getObject("Code").toString();
    col[1] = (String) rs.getObject("Product");
    col[2] = (String) rs.getObject("Quantity").toString();
    col[3] = (String) rs.getObject("Type");
    col[4] = (String) rs.getObject("Single_value").toString();
    col[5] = (String) rs.getObject("Value").toString();
    col[6] = (String) rs.getObject("Company");
    Date date = new Date();
    date = rs.getDate("DateIns");
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    col[7] = dateFormat.format(date);
    return col;
  }
  
  public String[] GetRowByMonth(String month, int row) throws SQLException {
    String[] col = new String[8];
    rs = st.executeQuery("SELECT * FROM Warehouse.Products WHERE MONTH(Products.DateIns) = " + month + ";");
    rs.absolute(row);
    col[0] = (String) rs.getObject("Code").toString();
    col[1] = (String) rs.getObject("Product");
    col[2] = (String) rs.getObject("Quantity").toString();
    col[3] = (String) rs.getObject("Type");
    col[4] = (String) rs.getObject("Single_value").toString();
    col[5] = (String) rs.getObject("Value").toString();
    col[6] = (String) rs.getObject("Company");
    Date date = new Date();
    date = rs.getDate("DateIns");
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    col[7] = dateFormat.format(date);
    return col;
  }
  
  public String[] GetRowForPeriod(int row, String fday, String fmonth, String tday, String tmonth) throws SQLException {
    String[] col = new String[8];
    rs = st.executeQuery("SELECT * FROM Warehouse.Products WHERE DateIns BETWEEN '2014-" + fmonth +"-" + fday + "' AND '2014-" + tmonth + "-" + tday + "';");
    rs.absolute(row);
    col[0] = Integer.toString(rs.getInt("Code"));
    col[1] = rs.getString("Product");
    col[2] = Integer.toString(rs.getInt("Quantity"));
    col[3] = rs.getString("Type");
    col[4] = Integer.toString(rs.getInt("Single_value"));
    col[5] = Integer.toString(rs.getInt("Value"));
    col[6] = rs.getString("Company");
    Date date = new Date();
    date = rs.getDate("DateIns");
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    col[7] = dateFormat.format(date);
    
    return col;
  }
  
  public String[] GetRow(int row) throws SQLException {
    String[] col = new String[8];
    rs = st.executeQuery("SELECT * FROM Warehouse.Products;");
    if(rs.absolute(row)){
      col[0] = (String) rs.getObject("Code").toString();
      col[1] = (String) rs.getObject("Product");
      col[2] = (String) rs.getObject("Quantity").toString();
      col[3] = (String) rs.getObject("Type");
      col[4] = (String) rs.getObject("Single_value").toString();
      col[5] = (String) rs.getObject("Value").toString();
      col[6] = (String) rs.getObject("Company");
      Date date = new Date();
      date = rs.getDate("DateIns");
      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      col[7] = dateFormat.format(date);
    }
    return col;
  }
  
  public String[] getDay() throws SQLException {
    String[] day = new String[CountDateItems()];
    rs = st.executeQuery("SELECT DISTINCT DateIns FROM Warehouse.Products;");
    while(rs.next()){
      Date date = new Date();
      date = rs.getDate("DateIns");
      DateFormat dateFormat = new SimpleDateFormat("dd");
      day[rs.getRow() - 1] = dateFormat.format(date);
    }
    return day;
  }
  
  public String[] getYear() throws SQLException {
    String[] year = new String[CountDateItems()];
    rs = st.executeQuery("SELECT DISTINCT DateIns FROM Warehouse.Products;");
    while(rs.next()){
      Date date = new Date();
      date = rs.getDate("DateIns");
      DateFormat dateFormat = new SimpleDateFormat("yyyy");
      year[rs.getRow() - 1] = dateFormat.format(date);
    }
    return year;
  }

  protected void finalize(){
    try{
      rs.close();
      st.close();
      conn.close();
    }
    catch(SQLException e){
      System.out.println("Finilize error: " + e.getMessage());
    }
  }
}
