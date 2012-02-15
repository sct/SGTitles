package com.sgcraft.sgtitles.title;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.sgcraft.sgtitles.SGTitles;

public class Title {
	public Integer id;
	public String name;
	public String data;
	public String position;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public Title(String name, String data, String position) {
		if (!exists(name)) {
			this.name = name;
			this.data = data;
			this.position = position;
			SGTitles.sql.query("INSERT INTO titles (name,data,position) VALUES ('" + name + "','" + data + "','" + position +"'");
			ResultSet rs = SGTitles.sql.query("SELECT last_insert_rowid()");
			try {
				rs.first();
				this.id = rs.getInt(0);
			} catch (SQLException e) {
				logger.info(e.getMessage());
			}
		}
	}
	
	public Boolean exists(String name) {
		ResultSet rs = SGTitles.sql.query("SELECT id FROM titles WHERE name='" + name + "'");
		if (rs != null)
			return true;
		else
			return false;
	}
	
	public void load(String name) {
		ResultSet rs = SGTitles.sql.query("SELECT * FROM titles WHERE name='" + name + "'");
		if (rs != null) {
			try {
				rs.first();
				this.id = rs.getInt("id");
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
