package collect.info;


import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SaveToDatabase {
	 private static final Logger logger = LoggerFactory.getLogger("SQL_LOGGER");
     private Statement statement = null;
     private PreparedStatement preparedstatement = null;

	  private Connection connection = null;
	
      SaveToDatabase () {
  		logger.debug("-------- MySQL JDBC Connection Testing ------------");

  		try {
  			Class.forName("com.mysql.jdbc.Driver");
  		} catch (ClassNotFoundException e) {
  			logger.error("Where is your MySQL JDBC Driver?");
  			e.printStackTrace();
  			return;
  		}
  		
  		logger.debug("MySQL JDBC Driver Registered!");

		
		
		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://127.0.0.1:3306","xxx", "xxx");

		} catch (SQLException e) {
			logger.error("Connection Failed! Check output console"+e.toString());
			//e.printStackTrace();
			return;
		}
		
		if (connection != null) {
			logger.info("You made it, take control your database now!, do not forget to close after your operation:)");
			this.setConnection(connection);
        }
		
		this.setStatement(null);
		this.setPreparedstatement(null);
		
      }

      
      public void batchUpdate(Statement stmt) throws SQLException {
    	  this.getConnection();
    	    try {
    	    	this.getConnection().setAutoCommit(false);
    	        stmt.executeBatch();
    	        this.getConnection().commit();
    	        stmt.close();
    			logger.info("Records are inserted into db");
    	    } catch(BatchUpdateException b) {
    			logger.error("BatchUpdateException Failed! Check output console"+b.toString());
    			return;
    	    } catch(SQLException ex) {
    			logger.error("SQLException Failed! Check output console"+ex.toString());
    			return;
    	    } finally {
    	        if (stmt != null) { 
    	        	stmt.close(); 
    	        }
    	        this.getConnection().setAutoCommit(true);
    	    }
     }
      public int[] batchUpdate(PreparedStatement stmt) throws SQLException {
    	  int[] count=null;
    	  this.getConnection();
    	    try {
    	    	this.getConnection().setAutoCommit(false);
    	        count = stmt.executeBatch();
    	        this.getConnection().commit();
    	        
    			logger.info(count.length+" number of records are inserted into db");
    	    } catch(BatchUpdateException b) {
    			logger.error("BatchUpdateException Failed! Check output console"+b.toString());
    			return count;
    	    } catch(SQLException ex) {
    			logger.error("SQLException Failed! Check output console"+ex.toString());
    			return count;
    	    } finally {
    	        if (stmt != null) { 
    	        	stmt.close(); 
    	        }
    	        this.getConnection().setAutoCommit(true);
    	    }
    	   return count;
     }
      
	/**
	 * @return the preparedstatement
	 */
	public PreparedStatement getPreparedstatement() {
		return preparedstatement;
	}


	/**
	 * @param preparedstatement the preparedstatement to set
	 */
	public void setPreparedstatement(PreparedStatement preparedstatement) {
		this.preparedstatement = preparedstatement;
	}


	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}
	public void CloseConnection() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			logger.error("SQLException Cannot Close connection! Check output console"+e.toString());
		} finally {
			logger.info("Connection Closed");
		};
	}
	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}


	/**
	 * @return the statement
	 */
	public Statement getStatement() {
		return statement;
	}


	/**
	 * @param statement the statement to set
	 */
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
      
      
      

}
