/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
public class TypesController {
	
	@Autowired
	protected File typesRoot;
	
	@Autowired
	protected Integer fileResponseBufferSize;

	@Autowired
	protected Integer fileStreamBufferSize;
	
	@ModelAttribute("types")
	public List<String> getTypes() {
		return new LinkedList<String>();
	}

	@RequestMapping("/types")
	public String types(@ModelAttribute("types") final List<String> types,
						final HttpServletRequest req,
						final HttpServletResponse res)
	{
		for(String s : typesRoot.list())
		{
			if(s.endsWith(".xml"))
			{
				types.add(s.substring(0, s.lastIndexOf(".xml")));
			}
		}
		
		return "types";
	}
	
	@RequestMapping("/types/{type}")
	public String typeEntry(@PathVariable("type") String type,
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
		
		//If no file extension, add .xml
		//TODO make sure this works as expected
		if(!type.contains("."))
		{
			type += ".xml";
		}
		
		File typeFile = new File(typesRoot, type);
		
		if(typeFile.exists() && typeFile.canRead())
		{
			try {
				res.setContentType("text/xml");
				res.setContentLength(Long.valueOf(typeFile.length()).intValue());
				res.setBufferSize(fileResponseBufferSize);
				Utils.streamFile(typeFile, res.getOutputStream(), fileStreamBufferSize);
			} catch (final IOException e) {
				if (!res.isCommitted()) {
					res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					map.put("error_msg", "Failed to stream type definition document");
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