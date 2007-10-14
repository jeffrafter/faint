/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 * 
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.                                     
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 * 
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/

package de.offis.faint.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


/**
 * @author maltech
 * 
 */
public class HotSpotController<PluginType, FilterType> {
	
	private PluginType[] plugins;	

	private PluginType activePlugin;

	private FilterType[] filters;

	private ArrayList<FilterType> activeFilters = new ArrayList<FilterType>();
	
	public HotSpotController(PluginType[] plugins, FilterType[] filters){
		this.plugins = plugins;
		this.activePlugin = plugins[0];
		if (filters!=null){
			this.filters = filters;
			for (int i = 0; i < filters.length; i++)
				activeFilters.add(filters[i]);
		}
	}
	
	public void serializeContent(){		
		serializeArray(plugins);
		if (filters != null)
			serializeArray(filters);
	}
	
	private void serializeArray(Object[] array){
		for (Object element : array){
			String fullClassName = element.getClass().toString();

			String fileName = MainController.getInstance().getDataDir().getPath() + File.separator
			                  + fullClassName.substring(fullClassName.lastIndexOf('.') + 1)
			                  + MainController.PLUGIN_SUFFIX;
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fileName);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(element);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(element.getClass().toString());
			}
		}
	}
	
	public PluginType[] getAvailablePlugins() {
		return this.plugins;
	}

	public PluginType getActivePlugin() {
		return activePlugin;
	}

	public void setActivePlugin(PluginType plugin) {
		this.activePlugin = plugin;
	}

	public FilterType[] getAvailableFilters() {
		return filters;
	}


	public void setFilterStatus(FilterType filter, boolean status) {
		if (status && !this.activeFilters.contains(filter))
			activeFilters.add(filter);
		
		if (!status && this.activeFilters.contains(filter))
			activeFilters.remove(filter);
	}

	public ArrayList<FilterType> getActiveFilters() {
		return activeFilters;
	}
}
