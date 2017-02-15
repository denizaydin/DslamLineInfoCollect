package collect.info;


import java.util.LinkedList;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class hostparams {
      private Statement statement = null;
      private ResultSet resultSet = null;
      
	private LinkedList<String> peersToConnect;
	hostparams(){
		peersToConnect =new LinkedList<String>();
	}
	public void initialize() throws Exception {
		System.out.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;
		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://127.0.0.1:3306","xxx", "xxx");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console"+e.toString());
			//e.printStackTrace();
			return;
		}
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
			statement = connection.createStatement();
			resultSet = statement
                    .executeQuery("SELECT * FROM FiXdsl.MSAN WHERE status = 1");
			  while (resultSet.next()) {
                  // It is possible to get the columns via name
                  // also possible to get the columns via the column number
                  // which starts at 1
                  // e.g. resultSet.getSTring(2);
                  peersToConnect.add(resultSet.getString("ip"));
          }
			  connection.close();
			
			
			
		} else {
			System.out.println("Failed to make connection!");
		}
		
		
	}
	

	/**
	 * @return the peersToConnect
	 */
	public LinkedList<String> getPeersToConnect() {
		return peersToConnect;
	}
	/**
	 * @param peersToConnect the peersToConnect to set
	 */
	public void setPeersToConnect(LinkedList<String> peersToConnect) {
		this.peersToConnect = peersToConnect;
	}
}
