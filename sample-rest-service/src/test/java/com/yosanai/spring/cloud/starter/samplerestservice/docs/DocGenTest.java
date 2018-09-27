package com.yosanai.spring.cloud.starter.samplerestservice.docs;

import static org.junit.Assert.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Transactional
@ActiveProfiles("test")
public class DocGenTest {

	private static final int INSERT_SIZE = 3;

	public static final String SIZE = "size";

	public static final String PAGE = "page";
	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	WebApplicationContext context;

	protected MockMvc mockMvc;

	@Value("${server.port}")
	protected int serverPort;

	@Autowired
	protected ObjectMapper objectMapper;

	private RestDocumentationResultHandler document;

	@PersistenceContext
	EntityManager entityManager;

	private RequestFieldsSnippet customerRequestFieldDescriptors = requestFields(
			fieldDescriptorsFor("firstName", "Customer's first name", "lastName", "Customer's last name",
					"sampleIgnoreInPublic", "Some data that will be ignored while in public view"));

	private PathParametersSnippet getPathParameters = pathParametersSnippet("id", "Customer ID to get");

	private PathParametersSnippet lastNamePathParameters = pathParametersSnippet("lastName", "Last name to search by");

	private RequestParametersSnippet pageRequestParameters = requestParametersSnippet(PAGE, "Page number to get", SIZE,
			"Number of results per page");

	private ResponseFieldsSnippet customerResponseFieldDescriptors = responseFields(
			fieldDescriptorsFor("firstName", "Customer's first name", "lastName", "Customer's last name", "created",
					"Created timestamp", "updated", "Updated timestamp", "id", "ID"));

	private ResponseFieldsSnippet customerResponseArrayFieldDescriptors = responseFields(fieldDescriptorsFor("[]",
			"List of customers", "[].firstName", "Customer's first name", "[].lastName", "Customer's last name",
			"[].created", "Created timestamp", "[].updated", "Updated timestamp", "[].id", "ID"));

	public void flush() {
		entityManager.flush();
		entityManager.clear();
	}

	protected String rndPhone() {
		return RandomStringUtils.random(10, false, true);
	}

	public String getRequestRootPath(Class<?> classDef) {
		return "/" + (0 < classDef.getAnnotation(RequestMapping.class).value().length
				? classDef.getAnnotation(RequestMapping.class).value()[0]
				: classDef.getAnnotation(RequestMapping.class).path()[0]) + "/";
	}

	protected String encodeAsParams(Object... params) {
		StringBuilder ret = new StringBuilder();
		if (null != params) {
			for (int idx = 0; idx < params.length; idx = idx + 2) {
				if (0 < ret.length()) {
					ret.append("&");
				}
				try {
					ret.append(params[idx]).append("=")
							.append(URLEncoder.encode(params[idx + 1].toString(), StandardCharsets.UTF_8.toString()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		}
		return ret.toString();
	}

	protected String getURLWithParam(String url, Object... params) {
		return StringUtils.joinWith("?", url, encodeAsParams(params));
	}

	public <T> T create(Class<?> classDef, Class<T> response, T obj, boolean genDoc) {
		T ret = null;
		String url = getRequestRootPath(classDef);
		try {
			MockHttpServletRequestBuilder post = post(url).contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(post).andDo(genDoc ? this.document : print()).andExpect(status().isOk())
					.andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Long id = (Long) MethodUtils.invokeMethod(ret, "getId");
			assertNotNull(id);
		} catch (Exception e) {
			// NOOP
		}
		return ret;
	}

	public <T> List<T> list(Class<?> classDef, Class<T> response, boolean genDoc, Object... args) {
		MockHttpServletRequestBuilder requestBuilder = get(getURLWithParam(getRequestRootPath(classDef), args))
				.accept(MediaType.APPLICATION_JSON);
		List<T> ret = null;
		try {
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(),
					objectMapper.getTypeFactory().constructCollectionType(List.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> T findBy(Class<?> classDef, Class<T> response, boolean genDoc, String path, Object... uriVars) {
		T ret = null;
		MockHttpServletRequestBuilder requestBuilder = get(getRequestRootPath(classDef) + path, uriVars)
				.accept(MediaType.APPLICATION_JSON);
		try {
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> List<T> findAllBy(Class<?> classDef, Class<T> response, boolean genDoc, String path, Object... uriVars) {
		List<T> ret = null;
		try {
			MockHttpServletRequestBuilder requestBuilder = get(getRequestRootPath(classDef) + path, uriVars)
					.accept(MediaType.APPLICATION_JSON);
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			String contentAsString = result.getResponse().getContentAsString();
			if (StringUtils.isEmpty(contentAsString)) {
				contentAsString = "[]";
			}
			ret = objectMapper.readValue(contentAsString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;

	}

	@Before
	public void setUp() {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(
				MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation).uris().withPort(serverPort))
				.build();
	}

	public RestDocumentationResultHandler defaultDocument(ResponseFieldsSnippet resp, RequestFieldsSnippet req) {
		return MockMvcRestDocumentation.document("{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()), resp, req);
	}

	public RestDocumentationResultHandler defaultDocument(Snippet... snippets) {
		return MockMvcRestDocumentation.document("{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()), snippets);
	}

	public FieldDescriptor[] fieldDescriptorsFor(String... strings) {
		List<FieldDescriptor> ret = new ArrayList<>();
		for (int idx = 0; idx < strings.length; idx += 2) {
			ret.add(fieldWithPath(strings[idx]).description(strings[idx + 1]));
		}
		return ret.toArray(new FieldDescriptor[] {});
	}

	public RequestParametersSnippet requestParametersSnippet(String... strings) {
		List<ParameterDescriptor> ret = new ArrayList<>();
		for (int idx = 0; idx < strings.length; idx += 2) {
			ret.add(parameterWithName(strings[idx]).description(strings[idx + 1]));
		}
		return requestParameters(ret);
	}

	public PathParametersSnippet pathParametersSnippet(String... strings) {
		List<ParameterDescriptor> ret = new ArrayList<>();
		for (int idx = 0; idx < strings.length; idx += 2) {
			ret.add(parameterWithName(strings[idx]).description(strings[idx + 1]));
		}
		return pathParameters(ret);
	}

	public Customer someCustomer(int index) {
		return new Customer("firstName" + index, "lastName" + index, "sampleIgnoreInPublic" + index);
	}

	@Test
	public void jpaCustomerCreate() {
		this.document = defaultDocument(customerResponseFieldDescriptors, customerRequestFieldDescriptors);
		Customer obj = create(CustomerController.class, Customer.class, someCustomer(1), true);
		assertNotNull(obj);
	}

	@Test
	public void jpaCustomerGet() {
		this.document = defaultDocument(customerResponseFieldDescriptors, getPathParameters);
		Customer obj = create(CustomerController.class, Customer.class, someCustomer(1), false);
		assertNotNull(obj);
		obj = findBy(CustomerController.class, Customer.class, true, "{id}", obj.getId());
		assertNotNull(obj);
	}

	@Test
	public void jpaCustomerList() {
		this.document = defaultDocument(customerResponseArrayFieldDescriptors, pageRequestParameters);
		IntStream.range(0, INSERT_SIZE).forEach(idx -> {
			Customer obj = create(CustomerController.class, Customer.class, someCustomer(idx), false);
			assertNotNull(obj);
		});
		List<Customer> objs = list(CustomerController.class, Customer.class, true, PAGE, 0, SIZE, Integer.MAX_VALUE);
		assertNotNull(objs);
		assertFalse(objs.isEmpty());
	}

	@Test
	public void jpaCustomersByLastName() {
		this.document = defaultDocument(customerResponseArrayFieldDescriptors, lastNamePathParameters);
		Customer obj = create(CustomerController.class, Customer.class, someCustomer(1), false);
		assertNotNull(obj);
		List<Customer> objs = findAllBy(CustomerController.class, Customer.class, true,
				"search/findAllByLastName/{lastName}", obj.getLastName());
		assertNotNull(objs);
		assertFalse(objs.isEmpty());
	}

}
