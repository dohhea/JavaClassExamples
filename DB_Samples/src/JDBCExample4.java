import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import java.util.*;
import se.datadosen.component.*;

// 교재에 나와 있는 drink_info 테이블에서 자료를 추출하는 프로그램 샘플입니다.
public class JDBCExample4 {
	// DB 관련 변수들
	Connection conn;		// DB 연결 Connection 객체참조변
	
	// 최상위 프레임
	JFrame frame;
	String frameTitle = "칵테일 데이터베이스 클라이언트";

	// 텍스트 박스들
    JTextField drink_name;	// drink_name 필드 디스플레이를 위한 박스
    JTextField cost;		// cost 필드 디스플레이를 위한 박스
    JTextField color;		// color 필드 디스플레이를 위한 박스
    
    // 버튼들
    JButton bDelete;		// names 리스트에서 선택된 레코드를 삭제하기 위한 버튼
    
    // 리스트
    JList names = new JList();			// 칵테일 이름을 나열해 주는 리스트
    
    public static void main(String[] args) {
       JDBCExample4 client = new JDBCExample4();
       client.setUpGUI();
       client.dbConnectionInit();
    }

    private void setUpGUI() {
    	// GUI 만들기
	   	frame = new JFrame(frameTitle);

	   	// 칵테일 이름 전체를 보여주는 컨트롤 (왼쪽 패널)
	   	JPanel leftTopPanel = new JPanel(new RiverLayout());
	   	
        JScrollPane cScroller = new JScrollPane(names);
        cScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        cScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        names.setVisibleRowCount(6);
        names.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        names.setFixedCellWidth(100);
        leftTopPanel.add("br center", new JLabel("칵테일 이름들"));
        leftTopPanel.add("p center", cScroller);
       
        // 입력(출력)창들과 라벨 (오른쪽 상단 패널)
	   	JPanel rightTopPanel = new JPanel(new RiverLayout());
	   	drink_name = new JTextField(20);
	   	cost = new JTextField(20);
	   	color = new JTextField(20);

	   	rightTopPanel.add("br center", new JLabel("칵  테  일  정  보"));
	   	rightTopPanel.add("p left", new JLabel("이   름"));
	   	rightTopPanel.add("tab", drink_name);
	   	rightTopPanel.add("br", new JLabel("가   격"));
	   	rightTopPanel.add("tab", cost);
	   	rightTopPanel.add("br", new JLabel("색"));
	   	rightTopPanel.add("tab", color);
        
        // 하단 패널
	   	JPanel bottomPanel = new JPanel();
        bDelete = new JButton("삭제");
        bottomPanel.add("center", bDelete);

	   	// GUI 배치
        JPanel topPanel = new JPanel(new GridLayout(1,2));
        topPanel.add(leftTopPanel);
        topPanel.add(rightTopPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ActionListener의 설정
		names.addListSelectionListener(new NameListListener());
        bDelete.addActionListener(new DeleteButtonListener());
        
        // 클라이언드 프레임 창 조정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.setSize(700,350);
        frame.setVisible(true);
    }

    // DB를 연결하는 메소드
    private void dbConnectionInit() {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");					// JDBC드라이버를 JVM영역으로 가져오기
    		conn = DriverManager.getConnection("jdbc:mysql://localhost/proj1", "root", "mite");	// DB 연결하기
    		prepareList();				// 리스트의 초기화 (DB에서 자료 읽어옴)
    	}
        catch (ClassNotFoundException cnfe) {
            System.out.println("JDBC 드라이버 클래스를 찾을 수 없습니다 : " + cnfe.getMessage());
        }
        catch (Exception ex) {
            System.out.println("DB 연결 에러 : " + ex.getMessage());
        }
	}

    // DB에 있는 전체 레코드를 불러와서 리스트에 뿌려주는 메소드
    public void prepareList() {
    	try {
    		Statement stmt = conn.createStatement();			// SQL 문을 작성을 위한  Statement 객체 생성

    		// 현재 DB에 있는 내용 추출해서 칵테일 목록을 names 리스트에 출력하기
    		ResultSet rs = stmt.executeQuery("SELECT drink_name FROM drink_info");
    		Vector<String> list = new Vector<String>();
    		while (rs.next()) {
    			list.add(rs.getString("drink_name"));		
    		}
    		stmt.close();										// statement는 사용후 닫는 습관
    		Collections.sort(list);								// 우선 정렬하자. 우와! 이렇게 쉽게 정렬이!! 중간고사 이후 수업에서 배울 것임!!
    		names.setListData(list);							// names 리스트에 drink_name 값들을 제공한다
    		if (!list.isEmpty())								// 리스트가 바뀌고 나면 항상 첫번째 아이텀을 가리키게 한다
    			names.setSelectedIndex(0);
    	} catch (SQLException sqlex) {
    		System.out.println("SQL 에러 : " + sqlex.getMessage());
    		sqlex.printStackTrace();
    	}
    }

	// 리스트 박스에 액션이 발생하면 처리하는 리스너
	public class NameListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent lse) {					// 리스트의 선택이 바뀔때마다 호출
			if (!lse.getValueIsAdjusting() && !names.isSelectionEmpty()) {  // 현재 선택이 다 끝난 경우에 처리
		    	try {
		    		Statement stmt = conn.createStatement();				// SQL 문장 만들기 위한 Statement 객체
		    		ResultSet rs = stmt.executeQuery("SELECT drink_name, cost, color FROM drink_info WHERE drink_name = '" +
		    				(String)names.getSelectedValue() + "'");
		    		rs.next();												// 여러개가 리턴되어도 첫번째 것으로 사용 
		    		drink_name.setText(rs.getString("drink_name"));			// DB에서 리턴 된 값을 가지고 택스트 박스 채움
		    		cost.setText(Double.toString(rs.getDouble("cost")));		
		    		color.setText(rs.getString("color"));	
		    		stmt.close();
		    	} catch (SQLException sqlex) {
		    		System.out.println("SQL 에러 : " + sqlex.getMessage());
		    		sqlex.printStackTrace();
		    	} catch (Exception ex) {
		    		System.out.println("DB Handling 에러(리스트 리스너) : " + ex.getMessage());
		    		ex.printStackTrace();
		    	}
			}
		}
	}
	
	// 삭제 버튼의 리스너
	public class DeleteButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
	    	try {
	    		Statement stmt = conn.createStatement();				// SQL 문을 작성을 위한  Statement 객체 생성
	    		stmt.executeUpdate("DELETE FROM drink_info WHERE drink_name = '" +
	    				drink_name.getText().trim() + "'");
	    		stmt.close();
	    		prepareList();											// 리스트 박스 새 리스트로 다시 채움 
	    	} catch (SQLException sqlex) {
	    		System.out.println("SQL 에러 : " + sqlex.getMessage());
	    		sqlex.printStackTrace();
	    	} catch (Exception ex) {
	    		System.out.println("DB Handling 에러(DELETE 리스너) : " + ex.getMessage());
	    		ex.printStackTrace();
	    	}
		}
	}
}
