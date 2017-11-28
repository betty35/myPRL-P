package win.betty35.www.myPRL.Pre.dbUtils.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
/*
 * Read a txt and get database configurations
 * 
 * for MyPRL
 * 
 * Modified by: Betty B Zhao (Lizi) 
 */


public class Configure {
	private String db_ip;
	private String db_name;
	private String db_user_name;
	private String db_user_passwd;
	private String basepath;

	public Configure()
	{
		String infor;
		try {
			infor = this.getDataBaseInfor();
			db_ip = infor.substring(6, infor.indexOf("db_name:"));
			db_name = infor.substring(infor.indexOf("db_name:")+8, infor.indexOf("db_user_name:"));
			db_user_name = infor.substring(infor.indexOf("db_user_name:")+13, infor.indexOf("db_user_password:"));
			db_user_passwd = infor.substring(infor.indexOf("db_user_password:")+17,infor.indexOf("basepath:"));
			basepath=infor.substring(infor.indexOf("basepath:")+9);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	public String getDb_ip() {
		return db_ip;
	}

	public String getBasepath() {
		return basepath;
	}

	public void setBasepath(String basepath) {
		this.basepath = basepath;
	}

	public void setDb_ip(String db_ip) {
		this.db_ip = db_ip;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	public String getDb_user_name() {
		return db_user_name;
	}

	public void setDb_user_name(String db_user_name) {
		this.db_user_name = db_user_name;
	}

	public String getDb_user_passwd() {
		return db_user_passwd;
	}

	public void setDb_user_passwd(String db_user_passwd) {
		this.db_user_passwd = db_user_passwd;
	}


	public String getDataBaseInfor() throws Exception{
		File  inforFile = new File ("C:\\configs\\conf_myPRL2.txt");
		//File  inforFile = new File ("./conf_myPRL.txt");
		BufferedReader reader = new BufferedReader(new FileReader(inforFile));
		String infor = "" ;
		String tempString = null;
		while ((tempString = reader.readLine()) != null) {
			infor += tempString;
		}
		reader.close();
		return infor;
	}
	
	
}
