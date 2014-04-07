package org.cytoscape.io.ndex.internal.helpers;

/*
 * #%L
 * Cytoscape IO Impl (io-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

//import org.cytoscape.io.internal.util.SUIDUpdater;

enum CyObjectType {
    LIST("list"),
    STRING("string"),
    REAL("real"),
    INTEGER("integer"),
    NONE("none"),
    BOOLEAN("boolean");

    private final String value;

    CyObjectType(String v) {
        value = v;
    }

    String value() {
        return value;
    }

    static CyObjectType fromValue(String v) {
        for (CyObjectType c : CyObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

    public String toString() {
        return value;
    }
}

public class ObjectTypeMap {

    private Map<String, CyObjectType> typeMap;

    public ObjectTypeMap() {
        typeMap = new HashMap<String, CyObjectType>();

        for (CyObjectType type : CyObjectType.values())
            typeMap.put(type.name(), type);
    }

    public CyObjectType getType(String name) {
        final CyObjectType type = typeMap.get(name);
        
        if (type != null)
            return type;
        else
            return CyObjectType.NONE;
    }

    /**
     * Return the typed value for the passed value.
     * 
     * @param type the ObjectType of the value
     * @param value the value to type
     * @param name the attribute name
     * @return the typed value
     */
	public Object getTypedValue(final CyObjectType type, final String value, final String name) {
		Object typedValue = null;

		switch (type) {
		case BOOLEAN:
			if (value != null)
				typedValue = fromXGMMLBoolean("" + value);
			break;
		case REAL:
			if (value != null) {
				if (false) //SUIDUpdater.isUpdatable(name))
					typedValue = Double.valueOf(value).longValue();
				else
					typedValue = Double.valueOf(value);
			}
			break;
		case INTEGER:
			if (value != null)
				typedValue = Integer.valueOf(value);
			break;
		case STRING:
			if (value != null) {
				// Make sure we convert our newlines and tabs back
//				typedValue = NEW_LINE_PATTERN.matcher(TAB_PATTERN.matcher(value).replaceFirst(TAB_STRING))
//						.replaceFirst(NEW_LINE_STRING);
				final String sAttr = value.replace("\\t", "\t");
				typedValue = sAttr.replace("\\n", "\n");
			}
			break;
		case LIST:
			typedValue = new ArrayList<Object>();
		default:
			break;
		}

		return typedValue;
	}
	
	private static final String TAB_STRING = "\t";
	private static final String NEW_LINE_STRING = "\n";
	private static final Pattern TAB_PATTERN = Pattern.compile("\\t");
	private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\\n");
    
    public static boolean fromXGMMLBoolean(final String s) {
    	// Should be only "1", but let's be nice and also accept "true"
    	// http://www.cs.rpi.edu/research/groups/pb/punin/public_html/XGMML/draft-xgmml-20001006.html#BT
    	// We also accept "yes", because of Cy2 "has_nested_network" attribute
    	return s != null && s.matches("(?i)1|true|yes");
    }

    public static String toXGMMLBoolean(final Boolean value) {
    	return value != null && value ? "1" : "0";
    }
}
