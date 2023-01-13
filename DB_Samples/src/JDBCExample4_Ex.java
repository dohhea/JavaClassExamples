import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import java.util.*;
import se.datadosen.component.RiverLayout;

// ���翡 ���� �ִ� drink_info ���̺��� �ڷḦ �����ϴ� ���α׷� �����Դϴ�.
public class JDBCExample4_Ex {
	// DB ���� ������
	Connection conn;		// DB ���� Connection ��ü������

	// �ֻ��� ������
	JFrame frame;
	String frameTitle = "Ĭ���� �����ͺ��̽� Ŭ���̾�Ʈ";

	// �ؽ�Ʈ �ڽ���
	JTextField drink_name;	// drink_name �ʵ� ���÷��̸� ���� �ڽ�
	JTextField cost;		// cost �ʵ� ���÷��̸� ���� �ڽ�
	JTextField color;		// color �ʵ� ���÷��̸� ���� �ڽ�

	// ��ư��
	JButton bDelete;		// names ����Ʈ���� ���õ� ���ڵ带 �����ϱ� ���� ��ư
	JButton bUpdate;		// names ����Ʈ���� ���õ� ���ڵ带 �����ϱ� ���� ��ư

	// ����Ʈ
	JList names = new JList();			// Ĭ���� �̸��� ������ �ִ� ����Ʈ

	public static void main(String[] args) {
		JDBCExample4_Ex client = new JDBCExample4_Ex();
		client.setUpGUI();
		client.dbConnectionInit();
	}

	private void setUpGUI() {
		// GUI �����
		frame = new JFrame(frameTitle);

		// Ĭ���� �̸� ��ü�� �����ִ� ��Ʈ�� (���� �г�)
		JPanel leftTopPanel = new JPanel(new RiverLayout());

		JScrollPane cScroller = new JScrollPane(names);
		cScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		cScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		names.setVisibleRowCount(6);
		names.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		names.setFixedCellWidth(100);
		leftTopPanel.add("br center", new JLabel("Ĭ���� �̸���"));
		leftTopPanel.add("p center", cScroller);

		// �Է�(���)â��� �� (������ ��� �г�)
		JPanel rightTopPanel = new JPanel(new RiverLayout());
		drink_name = new JTextField(20);
		// drink_name.setEditable(false);
		cost = new JTextField(20);
		color = new JTextField(20);

		rightTopPanel.add("br center", new JLabel("Ĭ  ��  ��  ��  ��"));
		rightTopPanel.add("p left", new JLabel("��   ��"));
		rightTopPanel.add("tab", drink_name);
		rightTopPanel.add("br", new JLabel("��   ��"));
		rightTopPanel.add("tab", cost);
		rightTopPanel.add("br", new JLabel("��"));
		rightTopPanel.add("tab", color);

		// �ϴ� �г�
		JPanel bottomPanel = new JPanel();
		bDelete = new JButton("����");
		bottomPanel.add(bDelete);
		bUpdate = new JButton("����");
		bottomPanel.add(bUpdate);

		// GUI ��ġ
		JPanel topPanel = new JPanel(new GridLayout(1,2));
		topPanel.add(leftTopPanel);
		topPanel.add(rightTopPanel);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		// ActionListener�� ����
		names.addListSelectionListener(new NameListListener());
		bDelete.addActionListener(new ButtonListener());
		bUpdate.addActionListener(new ButtonListener());

		// Ŭ���̾�� ������ â ����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.setSize(700,350);
		frame.setVisible(true);
	}

	// DB�� �����ϴ� �޼ҵ�
	private void dbConnectionInit() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");					// JDBC����̹��� JVM�������� ��������
			conn = DriverManager.getConnection("jdbc:mysql://localhost/proj1", "root", "mite");	// DB �����ϱ�
			prepareList();
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("JDBC ����̹� Ŭ������ ã�� �� �����ϴ� : " + cnfe.getMessage());
		}
		catch (Exception ex) {
			System.out.println("DB ���� ���� : " + ex.getMessage());
		}
	}

	// DB�� �ִ� ��ü ���ڵ带 �ҷ��ͼ� ����Ʈ�� �ѷ��ִ� �޼�
	public void prepareList() {
		try {
			Statement stmt = conn.createStatement();			// SQL ���� �ۼ��� ����  Statement ��ü ����

			// ���� DB�� �ִ� ���� �����ؼ� Ĭ���� ����� names ����Ʈ�� ����ϱ�
			ResultSet rs = stmt.executeQuery("SELECT drink_name FROM drink_info");
			Vector<String> list = new Vector<String>();
			while (rs.next()) {
				list.add(rs.getString("drink_name"));		
			}
			stmt.close();										// statement�� ����� �ݴ� ����
			Collections.sort(list);								// �켱 ��������. ���! �̷��� ���� ������!! �߰���� ���� �������� ��� ����!!
			names.setListData(list);							// names ����Ʈ�� drink_name ������ �����Ѵ�
			if (!list.isEmpty())								// ����Ʈ�� �ٲ�� ���� �׻� ù��° �������� ����Ű�� �Ѵ�
				names.setSelectedIndex(0);
		} catch (SQLException sqlex) {
			System.out.println("SQL ���� : " + sqlex.getMessage());
			sqlex.printStackTrace();
		}
	}

	// ����Ʈ �ڽ��� �׼��� �߻��ϸ� ó���ϴ� ������
	public class NameListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent lse) {					// ����Ʈ�� ������ �ٲ𶧸��� ȣ��
			if (!lse.getValueIsAdjusting() && !names.isSelectionEmpty()) {  // ���� ������ �� ���� ��쿡 ó��
				try {
					Statement stmt = conn.createStatement();				// SQL ���� ����� ���� Statement ��ü
					ResultSet rs = stmt.executeQuery("SELECT drink_name, cost, color FROM drink_info WHERE drink_name = '" +
							(String)names.getSelectedValue() + "'");
					rs.next();												// �������� ���ϵǾ ù��° ������ ��� 
					drink_name.setText(rs.getString("drink_name"));			// DB���� ���� �� ���� ������ �ý�Ʈ �ڽ� ä��
					cost.setText(Double.toString(rs.getDouble("cost")));		
					color.setText(rs.getString("color"));	
					stmt.close();
				} catch (SQLException sqlex) {
					System.out.println("SQL ���� : " + sqlex.getMessage());
					sqlex.printStackTrace();
				} catch (Exception ex) {
					System.out.println("DB Handling ����(����Ʈ ������) : " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	public class ButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			if (e.getSource() == bDelete) {
				try {
					Statement stmt = conn.createStatement();				// SQL ���� �ۼ��� ����  Statement ��ü ����
					stmt.executeUpdate("DELETE FROM drink_info WHERE drink_name = '" +
							drink_name.getText().trim() + "'");
					stmt.close();
					prepareList();											// ����Ʈ �ڽ� �� ����Ʈ�� �ٽ� ä�� 
				} catch (SQLException sqlex) {
					System.out.println("SQL ���� : " + sqlex.getMessage());
					sqlex.printStackTrace();
				} catch (Exception ex) {
					System.out.println("DB Handling ����(DELETE ������) : " + ex.getMessage());
					ex.printStackTrace();
				}
			} else if (e.getSource() == bUpdate) {
				try {
					Statement stmt = conn.createStatement();				// SQL ���� �ۼ��� ����  Statement ��ü ����
					stmt.executeUpdate("UPDATE drink_info SET cost = " + cost.getText() + ", color = " + "'" + color.getText() + "'" + 
							" WHERE drink_name = '" + drink_name.getText().trim() + "'");
					stmt.close();
					prepareList();											// ����Ʈ �ڽ� �� ����Ʈ�� �ٽ� ä�� 
				} catch (SQLException sqlex) {
					System.out.println("SQL ���� : " + sqlex.getMessage());
					sqlex.printStackTrace();
				} catch (Exception ex) {
					System.out.println("DB Handling ����(DELETE ������) : " + ex.getMessage());
					ex.printStackTrace();
				}
				
			}
		}
	}
}
