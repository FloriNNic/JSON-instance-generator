package ro.sync.json.instance.generator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class JsonInstanceGeneratorTest {

	/**
	 * Test the behaviour of the generator by using different schemas
	 * @throws IOException 
	 */
	@Test
	public void test1() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema1.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\n" + "    \"name\": \"name\",\n" + "    \"age\": 0\n" + "}", generatedJson);
	}
	
	@Test
	public void test2() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema2.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\"rectangle\": {\n" + "    \"a\": 5,\n" + "    \"b\": 5\n" +"}}", generatedJson);
	}
	
	@Test
	public void test3() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema3.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\n" + "    \"price\": 0,\n" + "    \"name\": \"name\",\n" + "    \"checked\": false,\n" + "    \"id\": 0,\n" + 
				"    \"dimensions\": {\n" + "        \"width\": 0,\n" + "        \"height\": 0\n" + "    },\n" + "    \"tags\": [\n" + 
				"        \"tags0\",\n" + "        \"tags1\"\n" + "    ]\n" + "}", generatedJson);
	}
	
	@Test
	public void test4() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema4.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\n" + "    \"fruits\": [\n" + "        \"fruits0\",\n" + "        \"fruits1\"\n"  + "    ],\n" + 
				"    \"vegetables\": [\n" + "        {\n" + "            \"veggieName\": \"veggieName\",\n" + 
				"            \"veggieLike\": false\n" + "        },\n" + "        {\n" + "            \"veggieName\": \"veggieName\",\n" + 
				"            \"veggieLike\": false\n" + "        }\n" +  "    ]\n" + "}", generatedJson);
	}
	
	@Test
	public void test5() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema5.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\"fruits\": [\n" + 
				"    0,\n" + 
				"    \"fruits\",\n" + 
				"    \"Avenue\",\n" + 
				"    \"SE\"\n" + 
				"]}", generatedJson);
	}
	
	@Test
	public void test6() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema6.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\"personnel\": {\"person\": [\n" + "    {\n" + "        \"name\": {\n" + "            \"given\": \"given\",\n" + 
				"            \"family\": \"family\"\n" + "        },\n" + "        \"link\": {\n" + "            \"manager\": \"manager\",\n" + 
				"            \"subordinates\": [\n" + "                \"subordinates0\",\n" + "                \"subordinates1\"\n" + "            ]\n" + 
				"        },\n" + "        \"id\": \"id\",\n" + "        \"email\": \"email\"\n" + "    },\n" + "    {\n" + "        \"name\": {\n" + 
				"            \"given\": \"given\",\n" + "            \"family\": \"family\"\n" + "        },\n" + "        \"link\": {\n" + 
				"            \"manager\": \"manager\",\n" + "            \"subordinates\": [\n" + "                \"subordinates0\",\n" + 
				"                \"subordinates1\"\n" + "            ]\n" + "        },\n" + "        \"id\": \"id\",\n" + "        \"email\": \"email\"\n" + 
				"    }\n" + "]}}", generatedJson);
	}
	
	@Test
	public void test7() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/schema7.json");
		
		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);
		
		assertEquals("{\n" + "    \"name\": \"\",\n" + "    \"age\": \"\"\n" + "}", generatedJson);
	}
	
	@Test
	public void test8() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/output.json");

		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);

		assertEquals("{\"note\": {\n" + 
				"    \"heading\": \"heading\",\n" + 
				"    \"from\": \"from\",\n" + 
				"    \"to\": \"to\"\n" + 
				"}}", generatedJson);
	}
	
	@Test
	public void test9() throws IOException {
		InputStream inputStream = JsonInstanceGeneratorTest.class.getResourceAsStream("/input.json");

		JsonGeneratorOptions options = new JsonGeneratorOptions(); 
		String generatedJson = JsonInstanceGenerator.generate(inputStream, options);

		assertEquals("{\"note\": {\n" + 
				"    \"heading\": \"heading\",\n" + 
				"    \"from\": \"from\",\n" + 
				"    \"to\": \"to\"\n" + 
				"}}", generatedJson);
	}
	
	

}
