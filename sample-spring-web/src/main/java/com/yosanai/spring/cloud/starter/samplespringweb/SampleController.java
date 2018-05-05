package com.yosanai.spring.cloud.starter.samplespringweb;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {
	@GetMapping("/")
	public String getIndex() {
		return "index-page";
	}
	@GetMapping("/sample-page")
	public String getSamplePage(Model model) {
		model.addAttribute("now", new Date());
		return "sample/sample-page";
	}
}
