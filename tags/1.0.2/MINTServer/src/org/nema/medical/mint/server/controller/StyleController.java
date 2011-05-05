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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StyleController {

	public String style(
			final HttpServletRequest req, final HttpServletResponse res) {
		// TODO check the filesystem if requested stylesheet exists
		// before returning the default style jsp.
		return "style";
	}

	@RequestMapping("/{style}.xsl")
	public String root(
		final HttpServletRequest req, final HttpServletResponse res){
			return style(req, res);
	}
	
	@RequestMapping("/{dir1}/{style}.xsl")
	public String dir1(
			final HttpServletRequest req, final HttpServletResponse res) {
			return style(req, res);
	}
	
	@RequestMapping("/{dir1}/{dir2}/{style}.xsl")
	public String dir2(
			final HttpServletRequest req, final HttpServletResponse res) {
		return style(req, res);
	}
	
	@RequestMapping("/{dir1}/{dir2}/{dir3}/{style}.xsl")
	public String dir3(
			final HttpServletRequest req, final HttpServletResponse res) {
		return style(req, res);
	}

	@RequestMapping("/jobs/status/{style}.xsl")
	public String jobsStatusStyle(
			final HttpServletRequest req, final HttpServletResponse res) {
		return style(req, res);
	}
	
    @RequestMapping("/studies/{uuid}/changelog/{style}.xsl")
    public String changelogStyle(
            final HttpServletRequest req, final HttpServletResponse res) {
        return style(req, res);
    }

    @RequestMapping("/studies/{style}.xsl")
    public String studySearch(
            final HttpServletRequest req, final HttpServletResponse res) {
        return style(req, res);
    }
}
