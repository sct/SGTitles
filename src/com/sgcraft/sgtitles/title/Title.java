/*
 *  SGTitles - Give your users a collection of titles
 *  Copyright (C) 2012  SGCraft
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
