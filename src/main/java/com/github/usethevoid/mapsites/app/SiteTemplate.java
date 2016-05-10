package com.github.usethevoid.mapsites.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SiteTemplate {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	final static Gson gson = new GsonBuilder().create();
	
	public static final int LIST_SEARCH = 0;
	public static final int MAP_SEARCH = 1;
	
	static class Field {
		String key;
		String type;
		Map<String,Object> templateOptions;
	}

	
	public static SiteTemplate readFromFile(String path) {
		try {
			InputStream in = new FileInputStream(new File(path));
			String json = IOUtils.toString(in, "UTF-8");
			SiteTemplate template = gson.fromJson(json, SiteTemplate.class);
			template.init();
			return template;
		} catch (IOException e) {
			throw new RuntimeException("Template resource not found");
		}
	}
	
	//read from json
	String nameField;
	String[] previewFields;
	String[] detailsFields;
	String[] imageTypes;
	List<String[]> searchFields;
	List<Field> fields;
	
	
	// set at init
	Set<String> fieldSet = new HashSet<String>();
	Map<String,String> fieldTypes = new HashMap<String,String>();
	Map<String,Field> field2Obj = new HashMap<String,Field> ();
	Map<String,Class> type2Class = new HashMap<String,Class>(); 

	private void init() {		
		for (String field : previewFields)
			fieldSet.add(field);
		for (String field : detailsFields)
			fieldSet.add(field);
		for (Field field: fields) {
			field2Obj.put(field.key, field);
			fieldTypes.put(field.type, field.type);
		}
		
	}
	
	public void registerType(String name, Class type) {
		type2Class.put(name,type);
	}

	public String[] getPreviewFields() {
		return previewFields;
	}

	public String[] getDetailsFields() {
		return detailsFields;
	}

	public final Set<String> getFieldSet() {
		return fieldSet;
	}
	
	public Object fieldFromJson(String json, String type) {
		Class typeClass = type2Class.get(type);
		if (typeClass != null)
			return gson.fromJson(json, typeClass);
		return json;
	}
	
	public String[] getSearchFields(int index) {
		return searchFields.get(index);
	}
	
	public String getPreviewFieldsJson() {
		List<Field> fieldsObjs = new ArrayList<Field>();
		for (String field: previewFields)
			fieldsObjs.add(field2Obj.get(field));
		return gson.toJson(fieldsObjs);
	}
	
	public String getCompleteFieldsJson() {
		List<Field> fieldsObjs = new ArrayList<Field>();
		for (String field: previewFields)
			fieldsObjs.add(field2Obj.get(field));
		for (String field: detailsFields)
			fieldsObjs.add(field2Obj.get(field));
		return gson.toJson(fieldsObjs);
	}
	
	public String getSearchFieldsJson(int index) {
		List<Field> fieldsObjs = new ArrayList<Field>();
		String[] fieldNames = getSearchFields(index);
		if (fieldNames == null)
			return null;
		for (String field: fieldNames)
			fieldsObjs.add(field2Obj.get(field));
		return gson.toJson(fieldsObjs);
	}
	
	
	public String[] getImageTypes() {
		return imageTypes;
	}
}
