package com.sgcraft.sgtitles.title;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sgcraft.sgtitles.SGTitles;

public class TitleManager {
	
	public static void loadAllTitles() {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT name FROM titles");
			while (rs.next()) {
				loadTitle(rs.getString("name"));
			}
		} catch (SQLException e) {
			// Do something
		}
	}
	
	public static void loadTitle(String name) {
		Title newtitle;
		newtitle = Title.load(name);
		SGTitles.TitleList.put(name,newtitle);
	}
	
	public static Title get(String name) {
		if (SGTitles.TitleList.containsKey(name)) {
			return SGTitles.TitleList.get(name);
		}
		return null;
	}
	
	public static void addTitle(String name, String data, String position) {
		Title newtitle = new Title(name,data,position);
		SGTitles.TitleList.put(name, newtitle);
	}

}
