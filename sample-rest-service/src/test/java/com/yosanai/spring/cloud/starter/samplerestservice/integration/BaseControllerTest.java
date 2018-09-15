package com.yosanai.spring.cloud.starter.samplerestservice.integration;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Transactional
public class BaseControllerTest {
	
	public static final int BATCH_SIZE = 5;

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgmtPort;

	@Autowired
	protected TestRestTemplate restTemplate;

	protected String getURL(String suffix) {
		return String.format("http://localhost:%d/%s", port, suffix);
	}

	protected String getURL(Class<?> classDef, String... path) {
		return String.format("http://localhost:%d%s/%s", port, classDef.getAnnotation(RequestMapping.class).value()[0],
				null == path ? "" : Stream.of(path).collect(Collectors.joining("/")));
	}

	protected String getMgmtURL(String suffix) {
		return String.format("http://localhost:%d/%s", mgmtPort, suffix);
	}

	protected String encodeAsParams(Object... params) throws UnsupportedEncodingException {
		StringBuilder ret = new StringBuilder();
		if (null != params) {
			for (int idx = 0; idx < params.length; idx = idx + 2) {
				if (0 < ret.length()) {
					ret.append("&");
				}
				ret.append(params[idx]).append("=")
						.append(URLEncoder.encode(params[idx + 1].toString(), StandardCharsets.UTF_8.toString()));
			}
		}
		return ret.toString();
	}

	protected String getURLWithParam(String url, Object... params) throws UnsupportedEncodingException {
		return StringUtils.joinWith("?", url, encodeAsParams(params));
	}

	public int rndInt() {
		return RandomUtils.nextInt(1, 1000);
	}

	public String rndStr() {
		return rndStr(10);
	}

	public String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
	}

	@Test
	public void testContextLoads() throws Exception {
		assertTrue(0 < mgmtPort);
	}
}
