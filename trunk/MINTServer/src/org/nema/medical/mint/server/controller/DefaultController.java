package org.nema.medical.mint.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

	final Logger logger = Logger.getLogger(this.getClass());

	@RequestMapping(value = { "/**" })
	public String blackhole(final HttpServletRequest httpServletRequest) {

		logger.info("Got into the black hole: "
				+ httpServletRequest.getRequestURL());

		return "blackhole";
	}

	@ExceptionHandler
	public String exceptionHandler(final HttpServletRequest httpServletRequest,
			final Exception e) {

		logger.error(
				"Exception handling " + httpServletRequest.getRequestURL(), e);

		return "exceptionHandler";
	}
}
