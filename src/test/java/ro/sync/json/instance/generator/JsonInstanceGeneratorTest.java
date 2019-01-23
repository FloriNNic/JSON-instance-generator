package ro.sync.json.instance.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JsonInstanceGeneratorTest {

	/**
	 * Test the behavior of the generator by using different schemas
	 * @throws IOException 
	 */
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	public void performValidation(String schemaResource, String expectedJson) {
	   exception.expect(ValidationException.class);
	   InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
	   Schema jsonSchema = SchemaLoader.load(new JSONObject(new JSONTokener(schema)));
	   jsonSchema.validate(expectedJson);
	}
	  
	//TODO
	@Test
	public void test1() throws IOException {
		String schemaResource = "/schema1.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\n" + "    \"name\": \"name\",\n" + "    \"age\": 0\n" + "}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 *Test ReferenceSchema and NumberSchema.   
	 *@throws IOException
	 */
	@Test
	public void test2() throws IOException {
		String schemaResource = "/schema2.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\"rectangle\": {\n" + "    \"a\": 5,\n" + "    \"b\": 5\n" +"}}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test ObjectSchema, BooleanSchema, StringSchema and ArraySchema.
	 * @throws IOException
	 */
	@Test
	public void test3() throws IOException {
		String schemaResource = "/schema3.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\n" + "    \"price\": 0,\n" + "    \"name\": \"name\",\n" + "    \"checked\": false,\n" + "    \"id\": 0,\n" + 
				"    \"dimensions\": {\n" + "        \"width\": 0,\n" + "        \"height\": 0\n" + "    },\n" + "    \"tags\": [\n" + 
				"        \"tags0\",\n" + "        \"tags1\"\n" + "    ]\n" + "}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test ObjectSchema (requiredProperties), ArraySchema (minItems and maxItems).
	 * @throws IOException
	 */
	@Test
	public void test4() throws IOException {
		String schemaResource = "/schema4.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\n" + "    \"fruits\": [\n" + "        \"fruits0\",\n" + "        \"fruits1\"\n"  + "    ],\n" + 
				"    \"vegetables\": [\n" + "        {\n" + "            \"veggieName\": \"veggieName\",\n" + 
				"            \"veggieLike\": false\n" + "        },\n" + "        {\n" + "            \"veggieName\": \"veggieName\",\n" + 
				"            \"veggieLike\": false\n" + "        }\n" +  "    ]\n" + "}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test EnumSchema.
	 * @throws IOException
	 */
	@Test
	public void test5() throws IOException {
		String schemaResource = "/schema5.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\"fruits\": [\n" + "    0,\n" + "    \"fruits\",\n" + "    \"Avenue\",\n" + "    \"SE\"\n" + "]}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test ArraySchema, StringSchema (minLength, maxLength, format).
	 * @throws IOException
	 */
	@Test
	public void test6() throws IOException {
		String schemaResource = "/schema6.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\"personnel\": {\"person\": [\n" + "    {\n" + "        \"name\": {\n" + "            \"given\": \"given\",\n" + 
				"            \"family\": \"family\"\n" + "        },\n" + "        \"link\": {\n" + "            \"manager\": \"manager\",\n" + 
				"            \"subordinates\": [\n" + "                \"subordinates0\",\n" + "                \"subordinates1\"\n" + "            ]\n" + 
				"        },\n" + "        \"id\": \"id\",\n" + "        \"email\": \"example@domain.com\"\n" + "    },\n" + "    {\n" + "        \"name\": {\n" + 
				"            \"given\": \"given\",\n" + "            \"family\": \"family\"\n" + "        },\n" + "        \"link\": {\n" + 
				"            \"manager\": \"manager\",\n" + "            \"subordinates\": [\n" + "                \"subordinates0\",\n" + "                \"subordinates1\"\n" + "            ]\n" + 
				"        },\n" + "        \"id\": \"id\",\n" + "        \"email\": \"example@domain.com\"\n" + "    }\n" + "]}}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test EmptySchema and ObjectSchema (requiredProperties).
	 * @throws IOException
	 */
	@Test
	public void test7() throws IOException {
		String schemaResource = "/schema7.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\n" + "    \"name\": \"\",\n" + "    \"age\": \"\"\n" + "}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test ObjectSchema (requiredProperties).
	 * @throws IOException
	 */
	@Test
	public void test8() throws IOException {
		String schemaResource = "/schema8.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\"note\": {\n" + "    \"heading\": \"heading\",\n" + "    \"from\": \"from\",\n" + "    \"to\": \"to\"\n" + "}}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	
	@Test
	public void test9() throws IOException {
		String schemaResource = "/schema9.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		String expectedJson = "{\"note\": {\n" + "    \"heading\": \"heading\",\n" + "    \"from\": \"from\",\n" + "    \"to\": \"to\"\n" + "}}";
		
		assertEquals(expectedJson, generatedJson);
		performValidation(schemaResource, expectedJson);
	}
	/**
	 * Test regex generation.
	 * @throws IOException
	 */
	@Test
	public void test10() throws IOException {
		String schemaResource = "/schema10.json";
		InputStream schema = JsonInstanceGeneratorTest.class.getResourceAsStream(schemaResource);
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		
		String generatedJson = JsonInstanceGenerator.generate(schema, options);
		StringTokenizer stk = new StringTokenizer(generatedJson, "{}\": ", false);
		stk.nextToken();
		String stringToEvaluate = stk.nextToken();
		assertTrue(stringToEvaluate.length() == 8 && stringToEvaluate.length() <= 13);
		performValidation(schemaResource, generatedJson);
	}
	
	

}
