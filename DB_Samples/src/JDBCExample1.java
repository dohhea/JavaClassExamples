import java.sql.*;
class JDBCExample1 {
    public static void main(String args[]) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
            		"jdbc:mysql://localhost:3306/proj1", "root", "mite");
            System.out.println("�����ͺ��̽��� �����߽��ϴ�.");
            conn.close();
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("�ش� Ŭ������ ã�� �� �����ϴ�." + cnfe.getMessage());
        }
        catch (SQLException se) {
            System.out.println("SQL�����Դϴ�. " + se.getMessage());
        }
    }
}
