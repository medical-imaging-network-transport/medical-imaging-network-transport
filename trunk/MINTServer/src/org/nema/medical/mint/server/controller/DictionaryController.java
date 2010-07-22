package org.nema.medical.mint.server.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DictionaryController {
	
	@Autowired
	protected File mintHome;
	
	@ModelAttribute("dictionaries")
	public List<String> getDictionaries() {
		return new LinkedList<String>();
	}
	
	private static final String DICTIONARY_FOLDER_NAME = "datadictionary";

	@RequestMapping("/dictionary")
	public String dictionary(@ModelAttribute("dictionaries") final List<String> dictionaries,
							 final HttpServletRequest req,
							 final HttpServletResponse res)
	{
		File dictionaryRoot = new File(mintHome, DICTIONARY_FOLDER_NAME);
		
		if(dictionaryRoot.exists())
		{
			for(String s : dictionaryRoot.list())
			{
				if(s.endsWith(".xml"))
				{
					dictionaries.add(s.substring(0, s.lastIndexOf(".xml")));
				}
			}
		}else{
			//No dictionaries preset
		}
		
		return "dictionary";
	}
	
	@RequestMapping("/dictionary/{type}")
	public String dictionaryEntry(@PathVariable("type") String type,
								  final HttpServletRequest req,
								  final HttpServletResponse res,
								  ModelMap map)
	{
		if(StringUtils.isBlank(type))
		{
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "must specify type");
			return "error";
		}
		
		File dictionaryRoot = new File(mintHome, DICTIONARY_FOLDER_NAME);
		
		if(!type.contains("."))
		{
			type += ".xml";
		}
		
		File dictionaryFile = new File(dictionaryRoot, type);
		
		if(dictionaryFile.exists() && dictionaryFile.canRead())
		{
			try {
				res.setContentType("text/xml");
				res.setContentLength(Long.valueOf(dictionaryFile.length()).intValue());
				Utils.streamFile(dictionaryFile, res.getOutputStream());
			} catch (final IOException e) {
				if (!res.isCommitted()) {
					res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					map.put("error_msg", "failed to stream dictionary document");
					return "error";
				}
			}
		}else{
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "Type specified does not exist");
			return "error";
		}
		
		return null;
	}
}
