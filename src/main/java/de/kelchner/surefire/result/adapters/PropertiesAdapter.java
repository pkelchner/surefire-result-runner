/*
 * Copyright 2013 Patrick Kelchner
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kelchner.surefire.result.adapters;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PropertiesAdapter extends XmlAdapter<Properties, Map<String,String>> {
	@Override
	public Map<String, String> unmarshal(Properties v) throws Exception {
		if (v == null || v.getProperty().size() == 0) return null;
		
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>(v.getProperty().size());
		
		for (Property p : v.getProperty()) {
			result.put(p.getName(), p.getValue());
		}
		
		return result;
	}

	@Override
	public Properties marshal(Map<String, String> v) throws Exception {
		if (v == null || v.size() == 0) return null;
		
		Properties result = new Properties();
		
		for (Entry<String,String> e : v.entrySet()) {
			Property p = new Property();
			p.setName(e.getKey());
			p.setValue(e.getValue());
			result.getProperty().add(p);
		}
		
		return result;
	}		
}