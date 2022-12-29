import java.sql.*;
class JDBCExample2 {
    public static void main(String args[]) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/proj1","root", "mite");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                   "SELECT drink_name, cost, color FROM drink_info;");
            System.out.println("\t칵테일 명 \t 가격 \t색깔");
            System.out.println(
              "----------------------------------------------");
            while (rs.next()) {
              String drink_name = rs.getString("drink_name");
              double cost = rs.getDouble("cost");
              String color = rs.getString("color");
              System.out.printf("%20s \t%5.2f %s%n", drink_name, 
                 cost, color);
           }
        } 
        catch (ClassNotFoundException cnfe) {
           System.out.println("해당 클래스를 찾을 수 없습니다." + 
                            cnfe.getMessage());
        }
        catch (SQLException se) {
            System.out.println("SQL에러. " + se.getMessage());
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception ignored) {
            }
            try {
                conn.close();
            }
            catch (Exception ignored) {
            }
        }
    }
}