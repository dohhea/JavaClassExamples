import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;





import se.datadosen.component.RiverLayout;

public class DogInfoWithKey {
	// DB 관련 변수들
	Connection conn;		// DB 연결 Connection 객체참조변수
	
	// 최상위 프레임
	JFrame frame;
	String frameTitle = "강아지 정보 (pet) 데이터베이스 클라이언트";

	// 텍스트 박스들
    JTextField dogId;		// id 필드 디스플레이를 위한 박스
    JTextField name;		// name 필드 디스플레이를 위한 박스
    JTextField owner;		// owner 필드 디스플레이를 위한 박스
    JTextField species;		// species 필드 디스플레이를 위한 박스
    JTextField birth;		// birth 필드 디스플레이를 위한 박스

    // 색인을 위한 박스
    JTextField search;		// 색인을 위한  필드

    // 라디오 버튼들
    JRadioButton male = new JRadioButton("Male");			// 성별을 출력하기 위한 하디오 버튼
    JRadioButton female = new JRadioButton("Female");		// 성별을 출력하기 위한 하디오 버튼
    
    // 버튼들
    JButton bSearch;		// 색인 실행을 위한 버튼
    JButton bSave;			// 저장 실행을 위한 버튼
    JButton bDelete;		// 삭제 실행을 위한 버튼
    JButton bNew;			// 신규 실행을 위한 버튼
    
    // 리스트
    JList names = new JList();			// 강아지 이름을 나열해 주는 리스트
    
    public static void main(String[] args) {
       DogInfoWithKey client = new DogInfoWithKey();
       client.setUpGUI();
       client.dbConnectionInit();
    }

    private void setUpGUI() {
    	// build GUI
	   	frame = new JFrame(frameTitle);

	   	// 강아지 이름 전체를 보여주는 컨트롤 (왼쪽 상단 패널)
	   	JPanel leftTopPanel = new JPanel(new RiverLayout());
	   	
        JScrollPane cScroller = new JScrollPane(names);
        cScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        cScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        names.setVisibleRowCount(7);
        names.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        names.setFixedCellWidth(100);
        leftTopPanel.add("br center", new JLabel("강아지 이름들"));
        leftTopPanel.add("p center", cScroller);
	   	
        // 입력 창들과 라벨 (오른쪽 상단 패널)
	   	JPanel rightTopPanel = new JPanel(new RiverLayout());
	   	dogId = new JTextField(20);
	   	name = new JTextField(20);
	   	owner = new JTextField(20);
	   	species = new JTextField(20);
	   	birth = new JTextField(20);
	    ButtonGroup gender = new ButtonGroup();				// 라디오 버튼 그룹
	    gender.add(male);
	    gender.add(female);
	    
	   	rightTopPanel.add("br center", new JLabel("강 아 지 정 보"));
	   	rightTopPanel.add("p left", new JLabel("번   호"));
	   	rightTopPanel.add("tab", dogId);
	   	dogId.setEditable(false);  							// 편집하지 못하도록 함. Display-Only
	   	rightTopPanel.add("br", new JLabel("이   름"));
	   	rightTopPanel.add("tab", name);
	   	rightTopPanel.add("br", new JLabel("주   인"));
	   	rightTopPanel.add("tab", owner);
	   	rightTopPanel.add("br", new JLabel("종"));
	   	rightTopPanel.add("tab", species);
	   	rightTopPanel.add("br", new JLabel("성   별"));
	   	rightTopPanel.add("tab", male);
	   	rightTopPanel.add("tab", female);
	   	rightTopPanel.add("br", new JLabel("생   일"));
	   	rightTopPanel.add("tab", birth);

        // 왼쪽 하단 패널
	   	JPanel leftBottomPanel = new JPanel(new RiverLayout());
	   	JPanel tmpPanel = new JPanel(new RiverLayout());
	   	search = new JTextField(20);
        bSearch = new JButton("검색");
        tmpPanel.add(search);
        tmpPanel.add(bSearch);
        leftBottomPanel.add("center", tmpPanel);
        
        // 오른쪽 하단 패널
	   	JPanel rightBottomPanel = new JPanel(new RiverLayout());
	   	tmpPanel = new JPanel(new RiverLayout());
        bSave = new JButton("저장");
        bDelete = new JButton("삭제");
        bNew = new JButton("신규");
        tmpPanel.add(bSave);
        tmpPanel.add("tab", bDelete);
        tmpPanel.add("tab", bNew);
        rightBottomPanel.add("center", tmpPanel);
        rightBottomPanel.add("br", Box.createRigidArea(new Dimension(0,20)));

	   	// GUI 배치
        JPanel topPanel = new JPanel(new GridLayout(1,2));
        topPanel.add(leftTopPanel);
        topPanel.add(rightTopPanel);
        JPanel bottomPanel = new JPanel(new GridLayout(1,2));
        bottomPanel.add(leftBottomPanel);
        bottomPanel.add(rightBottomPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ActionListener의 설정
		names.addListSelectionListener(new NameListListener());
		MySearchListener l = new MySearchListener();
		search.addActionListener(l);								// 텍스트 박스에서 리턴 눌러 색인 시작 할 때
        bSearch.addActionListener(l);								// 버튼으로 색인 시작할 때
        bSave.addActionListener(new SaveButtonListener());
        bDelete.addActionListener(new DeleteButtonListener());
        bNew.addActionListener(new NewButtonListener());
        
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
    		prepareList();
    	}
        catch (ClassNotFoundException cnfe) {
            System.out.println("JDBC 드라이버 클래스를 찾을 수 없습니다 : " + cnfe.getMessage());
        }
        catch (Exception ex) {
            System.out.println("DB 연결 에러 : " + ex.getMessage());
        }
	}

    // DB에 있는 전체 레코드를 불러와서 리스트에 뿌려주는 메소
    public void prepareList() {
    	try {
    		Statement stmt = conn.createStatement();			// SQL 문을 작성을 위한  Statement 객체 생성

    		// 현재 DB에 있는 내용 추출해서 강아지 목록을 names 리스트에 출력하기
    		ResultSet rs = stmt.executeQuery("SELECT * FROM pet");
    		Vector<IdAndName> list = new Vector<IdAndName>();
    		while (rs.next()) {
    			list.add(new IdAndName(rs.getInt("dogId"), rs.getString("name")));		
    		}
    		stmt.close();										// statement는 사용후 닫는 습관
    		Collections.sort(list);								// 우선 정렬하자
    		names.setListData(list);							// names의 각종 속성은 그대로 두고 내용물만 바꾼다
    		if (!list.isEmpty())								// 리스트가 바뀌고 나면 항상 첫번째 아이텀을 가리키게 
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
		    		String s = "SELECT * FROM pet WHERE dogId = '" +
		    				((IdAndName)names.getSelectedValue()).getDogId() + "'";
		    		ResultSet rs = stmt.executeQuery(s);
		    		rs.next();												// 여러개가 리턴되어도 첫번째 것으로 사용 
		    		dogId.setText(""+rs.getInt("dogId"));					// DB에서 리턴 된 값을 가지고 택스트 박스 채움
		    		name.setText(rs.getString("name"));						
		    		owner.setText(rs.getString("owner"));		
		    		species.setText(rs.getString("species"));	
		    		if (rs.getString("gender").equals("m"))			
		    			male.setSelected(true);
		    		else
		    			female.setSelected(true);
		    		birth.setText(rs.getDate("birth").toString());
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
	
	// 색인 컴포넌트의 리스너
	public class MySearchListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			int index = names.getNextMatch(search.getText().trim(), 0, Position.Bias.Forward);
			if (index != -1) {
				names.setSelectedIndex(index);
			}
			search.setText("");
		}
	}

	// 삭제 버튼의 리스너
	public class DeleteButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
	    	try {
	    		Statement stmt = conn.createStatement();				// SQL 문을 작성을 위한  Statement 객체 생성
	    		stmt.executeUpdate("DELETE FROM pet WHERE dogId = " + dogId.getText());
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

	// 저장 버튼의 리스너
	public class SaveButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
	    	try {
	    		Statement stmt = conn.createStatement();				// SQL 문을 작성을 위한  Statement 객체 생성
	    		String gender;
	    		if (male.isSelected())
	    			gender = "m";
	    		else
	    			gender = "f";

	    		// 수정한 것을 Save 하는 경우는 Update로, 새것인 경우에는 Insert로 진행해야 함. 
	    		if (dogId.getText().trim().equals("")) {		// 새것인 경우 
		    		stmt.executeUpdate("INSERT INTO pet (name, owner, species, gender, birth) VALUES ('" +	// 새 레코드로 변경
		    				name.getText().trim() + "', '" +
		    				owner.getText().trim() + "', '" +
		    				species.getText().trim() + "', '" +
		    				gender + "', '" +
		    				birth.getText().trim() + "')");
	    		}
	    		else {											// 수정인 경우
		    		stmt.executeUpdate("UPDATE pet SET name='" + name.getText().trim() + "', " +
		    				"owner='"+owner.getText().trim() + "', " +
		    				"species='"+species.getText().trim() + "', " +
		    				"gender='"+gender + "', " +
		    				"birth='"+birth.getText().trim() + "' " +
		    				"WHERE dogId ="+dogId.getText());
	    		}
	    		stmt.close();
	    		String newName = name.getText().trim();					// 수정된 데이터의 이름을 기억
	    		prepareList();											// 다시 뿌려 줌 
				int index = names.getNextMatch(newName, 0, Position.Bias.Forward);
				names.setSelectedIndex(index);							// 수정/삽입된 이름의 위치를 찾음
	    	} catch (SQLException sqlex) {
	    		System.out.println("SQL 에러 : " + sqlex.getMessage());
	    		sqlex.printStackTrace();
	    	} catch (Exception ex) {
	    		System.out.println("DB Handling 에러(SAVE 리스너) : " + ex.getMessage());
	    		ex.printStackTrace();
	    	}
		}
	}

	public class NewButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			dogId.setText("");
			name.setText("");
			owner.setText("");
			species.setText("");
			male.setSelected(true);
			female.setSelected(false);
			birth.setText("");
			names.clearSelection();
		}
	}
	
	private class IdAndName implements Comparable{
		int dogId;
		String name;
		
		public IdAndName(int dogId, String name) {
			this.dogId = dogId;
			this.name = name;
		}
		public int getDogId() {
			return dogId;
		}
		public void setDogId(int dogId) {
			this.dogId = dogId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		public int compareTo(Object o) {
			return this.name.compareTo(((IdAndName)o).name);
		}
		
		public String toString() {
			return name;
		}
	}

}
