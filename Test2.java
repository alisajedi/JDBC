import java.sql.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Console;
//import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

public class Test2 {
	public static ResultSet printStudentRecords(Statement s){
		try{
			ResultSet rs = s.executeQuery("select id, name, height from student");
			
			//Common method:
			System.out.println("id" + "      " + "name" + "      " + "height");
			System.out.println("---------------------------");
			while (rs.next()){
				int  id = rs.getInt("id");
				String name = rs.getString("name");
				Double height = rs.getDouble("height");
				System.out.println(id + "     " + name + "     " + height);
			}//while.
			
//			//Using Meta data:
//			ResultSetMetaData rsmd = rs.getMetaData();
//			if (rs.next()){
//				int colCount = rsmd.getColumnCount();
////				ArrayList<String> titles = new ArrayList<String>();//Just in case we need the titles later.
//				for (int i=1; i<=colCount; i++){
//					String aTitle = rsmd.getColumnLabel(i);
////					titles.add(aTitle);
//					System.out.print(aTitle + "      ");
//				}
//				System.out.println();
//				System.out.println("---------------------------");
//				rs.beforeFirst();
//				while (rs.next()){
//					for (int i=1; i<=colCount; i++){
//						Object o = rs.getObject(i);
//						System.out.print(o.toString() + "     ");					
//					}//for.
//					System.out.println();
//				}//while.
//			}//while.
			
			System.out.println();
			return rs;
		}//try.
		catch(Exception e){
			e.printStackTrace();
			return null;
		}//catch.
	}//printRows().
	//-------------------------------------------------------------------------------------------------------------------
	public static String getPassFromTerminal(){
		Console c = System.console();
		if (c==null){
			System.out.println("Error creating the console!");
			return "";
		}
		else{
//			System.out.println("User name: ");
//			userName = c.readLine();
			char passwordArray[] = c.readPassword("Pass: ");
			return new String(passwordArray);
		}//else.
	}
	//-------------------------------------------------------------------------------------------------------------------
	public static String getPassFromDialog(){
		//http://stackoverflow.com/a/15160334/2961878
		final JPasswordField jpf = new JPasswordField();
		JOptionPane jop = new JOptionPane(jpf, JOptionPane.QUESTION_MESSAGE,
		        JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = jop.createDialog("Password:");
		dialog.addComponentListener(new ComponentAdapter() {
		    @Override
		    public void componentShown(ComponentEvent e) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		                jpf.requestFocusInWindow();
		            }
		        });
		    }
		});
		dialog.setVisible(true);
		int result = (Integer) jop.getValue();
		dialog.dispose();
		char[] passwordChars = null;
		if (result == JOptionPane.OK_OPTION) {
		    passwordChars = jpf.getPassword();
		}
		if (passwordChars != null)
			return new String(passwordChars);
		else
			return "";
	}//getPassFromDialog().
	//-------------------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String userName="YOUR_SQLPLUS_USERNAME", password="";
			 
//			password = getPassFromTerminal();
			//After compiling, run using:
			//java -classpath /oracle/jdbc/lib/ojdbc6.jar:. Test2
			password = getPassFromDialog();

			Class drvClass = Class.forName("oracle.jdbc.driver.OracleDriver"); //(dynamically) loads jdbc driver
			Connection con = DriverManager.getConnection(
					"jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS",
					userName, password); 
			Statement stmt = con.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println("Welcome!");
			stmt.executeUpdate("declare tableExists int; " + 
				" begin " + 
					" select count(table_name) into tableExists from user_tables where table_name = 'STUDENT'; " + 
					" if tableExists = 1 then " + 
						" execute immediate 'drop table STUDENT'; " + 
					" end if; " + 
				" end;");
			stmt.executeUpdate("create table Student(id int, name char(10), height float, primary key(id))");
			stmt.executeUpdate("insert into student values(1, 'John', 166.3)");
			stmt.executeUpdate("insert into student values(2, 'Jill', 177.8)");
			stmt.executeUpdate("insert into student values(3, 'Jack', 163.5)");
//			stmt.executeUpdate("insert into student values(4, 'Peter', 175)");
			//Now, fetching the records for update:
			ResultSet rs = stmt.executeQuery("select id, name, height from student");
			//Select * won't work if you want to "UPDATE" (using updatable resultSet)!
			rs = printStudentRecords(stmt);

			//Update using resultSet:
			rs.first();
			rs.updateString(2, "Johnny");//For the current row, update the field # 2, which is name, to "Johnny".
			rs.updateDouble(3, 167.2);
			rs.updateRow(); //Makes the above update permanent.
			rs = printStudentRecords(stmt);

			//Insert using resultSet:
			rs.moveToInsertRow();
			rs.updateInt(1,  5);
			rs.updateString(2, "Joe");
			rs.updateDouble(3, 180.3);
			rs.insertRow();
			rs = printStudentRecords(stmt);//print the records.
			
			stmt.close();
			con.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}
