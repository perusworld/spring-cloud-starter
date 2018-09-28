package com.yosanai.spring.cloud.starter.samplerestservice.hypermedia.integration;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.AnnotationRelProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Transactional
@ActiveProfiles("test")
public class BaseControllerTest {

	public static final int BATCH_SIZE = 5;

	@LocalServerPort
	protected int port;

	@Value("${local.management.port}")
	private int mgmtPort;

	@Autowired
	private TestRestTemplate restTemplate;

	protected String getURL(Class<?> classDef, String... path) {
		return String.format("http://localhost:%d/%s/%s", port,
				classDef.getAnnotation(RequestMapping.class).value()[0].replace("jpa/", ""),
				null == path ? "" : Stream.of(path).collect(Collectors.joining("/")));
	}

	@Autowired
	protected ObjectMapper objectMapper;

	protected String getURLWithParam(String url, Object... params) throws UnsupportedEncodingException {
		return StringUtils.joinWith("?", url, encodeAsParams(params));
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

	public int rndInt() {
		return RandomUtils.nextInt(1, 1000);
	}

	public String rndStr() {
		return rndStr(10);
	}

	public String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
	}

	public <T> T post(Class<?> classDef, T obj, Class<T> response) {
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<T> resp = restTemplate.exchange(getURL(classDef, ""), HttpMethod.POST,
				new HttpEntity<>(new Resource<T>(obj), headers), response);
		assertNotNull(resp);
		assertNotNull(resp.getBody());
		Long id = null;
		try {
			id = (Long) MethodUtils.invokeMethod(resp.getBody(), "getId");
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		assertNotNull(id);
		return resp.getBody();
	}

	public <T> T findBy(Class<?> classDef, Class<T> response, String... args) {
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<T> resp = restTemplate.exchange(getURL(classDef, args), HttpMethod.GET,
				new HttpEntity<>(headers), response);
		assertNotNull(resp);
		return resp.getBody();
	}

	public <T> List<T> findAllBy(Class<?> classDef, Class<T> response, String... args) {
		return exchangeAsList(getURL(classDef, args), response);
	}

	public <T> List<T> exchangeAsList(String url, Class<T> response) {
		Resources<T> ret = null;
		ResponseEntity<String> resp = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(resp);
		try {
			ret = objectMapper.readValue(resp.getBody(),
					this.objectMapper.getTypeFactory().constructParametricType(Resources.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return new ArrayList<>(ret.getContent());
	}

	@Before
	public void before() {

		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.registerModule(new Jackson2HalModule());
		objectMapper.setHandlerInstantiator(
				new Jackson2HalModule.HalHandlerInstantiator(new AnnotationRelProvider(), null, null));

	}

}
