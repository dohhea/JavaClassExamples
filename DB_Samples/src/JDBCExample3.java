import java.sql.*;
class JDBCExample3 {
    public static void main(String args[]) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/proj1", "root", "mite");
            stmt = conn.createStatement();
            int rowNum = stmt.executeUpdate(
                "INSERT INTO drink_info (drink_name, cost, carbs, color, " +
                  "ice, calories) VALUES ('My Invention', 10.00, 0.01, " +
                   "'White', 'Y', 22);");
            System.out.println(rowNum + "행이 추가되었습니다.");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("해당 클래스를 찾을 수 없습니다." +
                cnfe.getMessage());
        }
        catch (SQLException se) {
            System.out.println(" SQL에러. " + se.getMessage());
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