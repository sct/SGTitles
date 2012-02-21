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
import java.util.ArrayList;
import java.util.List;

import com.sgcraft.sgtitles.SGTitles;

public class TitleManager {
	
	public static void addTitle(String name, String data, String position) {
		Title newtitle = new Title(name,data,position);
		if (newtitle.getName() != null) {
			SGTitles.TitleList.put(name, newtitle);
		}
	}
	
	public static void addTitle(String name, String data, String position, Boolean overwrite) {
		Title title = TitleManager.get(name);
		if (title != null) {
			updateTitle(title,data,position);
		} else {
			Title newtitle = new Title(name,data,position);
			if (newtitle.getName() != null) {
				SGTitles.TitleList.put(name, newtitle);
			}
		}
	}
	
	public static Title get(String name) {
		if (SGTitles.TitleList.containsKey(name)) {
			return SGTitles.TitleList.get(name);
		}
		return null;
	}
	
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
	
	public static void updateTitle(Title mTitle,String data, String position) {
		mTitle.setData(data);
		mTitle.setPos(position);
		mTitle.save();
	}
	
	public static void removeTitle(Title title) {
		SGTitles.TitleList.remove(title.getName());
		SGTitles.sql.query("DELETE FROM titles WHERE name='" + title.getName() + "';");
		SGTitles.sql.query("DELETE FROM player_titles WHERE title_name='" + title.getName() + "';");
		SGTitles.sql.query("UPDATE active_titles SET title_prefix=NULL WHERE title_prefix='" + title.getName() + "';");
		SGTitles.sql.query("UPDATE active_titles SET title_suffix=NULL WHERE title_suffix='" + title.getName() + "';");
	}
	
	public static String replaceColors(String replace) {
		return replace.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
	}

}
