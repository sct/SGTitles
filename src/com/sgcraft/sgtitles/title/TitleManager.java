package com.sgcraft.sgtitles.title;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sgcraft.sgtitles.SGTitles;

public class TitleManager {
	
	public static void loadAllTitles() {
		List<String> tmpNames = new ArrayList<String>();
		try {
			ResultSet rs = SGTitles.sql.query("SELECT name FROM titles");
			while (rs.next()) {
				tmpNames.add(rs.getString("name"));
			}
			rs.close();
			
			for (String tmpName : tmpNames) {
				loadTitle(tmpName);
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
		if (newtitle.getName() != null) {
			SGTitles.TitleList.put(name, newtitle);
		}
	}

}
