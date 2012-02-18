package com.sgcraft.sgtitles.title;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.sgcraft.sgtitles.SGTitles;

public class Title {
	public String name;
	public String data;
	public String position;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public Title(String name) {
		this.name = name;
	}
	
	public Title(String name, String data, String position) {
		if (!exists(name)) {
			this.name = name.toLowerCase();
			this.data = data;
			this.position = position.toLowerCase();
			SGTitles.sql.query("INSERT INTO titles (name,data,position) VALUES ('" + name + "','" + data + "','" + position + "')");
		}
	}
	
	public String getData() {
		return this.data;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPos() {
		return this.position;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public void setPos(String position) {
		this.position = position;
	}
	
	public void save() {
		SGTitles.sql.query("UPDATE titles SET data='" + this.data + "',position='" + this.position + "' WHERE name='" + this.name + "'");
	}
	
	
	public Boolean isPrefix() {
		if (this.position.equalsIgnoreCase("prefix"))
			return true;
		else
			return false;
	}
	
	public Boolean isSuffix() {
		if (this.position.equalsIgnoreCase("suffix"))
			return true;
		else
			return false;
	}
	
	public static Boolean exists(String name) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(id) AS counted FROM titles WHERE name='" + name + "'");
			int counted = rs.getInt("counted");
			rs.close();
			if (counted > 0)
				return true;
		} catch (SQLException e) {
			// Do stuff?
		}
		return false;
	}
	
	public static Title load(String name) {
		Title title = new Title(name);
		
		title.loadTitleData(name);
		
		return title;
	}
	
	public void loadTitleData(String name) {
		ResultSet rs = SGTitles.sql.query("SELECT * FROM titles WHERE name='" + name + "' LIMIT 1");
		if (rs != null) {
			try {
				this.name = rs.getString("name");
				this.data = rs.getString("data");
				this.position = rs.getString("position");
				rs.close();
			} catch (SQLException e) {
				logger.info(e.getMessage());
			}
		}
	}

}
