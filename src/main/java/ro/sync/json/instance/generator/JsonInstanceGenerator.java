package ro.sync.json.instance.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonInstanceGenerator {
	
	public static String generate(JsonGeneratorOptions options) throws IOException {
		File uploadFile = new File(options.getUploadSystemID());
		String uploadContent = new String(Files.readAllBytes(Paths.get(uploadFile.toURI())));

		JSONObject rawSchema = new JSONObject(uploadContent);
		Schema schema = SchemaLoader.load(rawSchema);

		JsonGeneratorEngine jsonGenerator = new JsonGeneratorEngine(options);
		jsonGenerator.visit(schema);
		JSONObject jsonObj = new JSONObject(jsonGenerator.builder.toString());
		String generatedJSON = jsonObj.toString(4);
		System.out.println("JSON to write:\n "+ generatedJSON);
		return generatedJSON;
	}

	public static String generate(InputStream inputStream, JsonGeneratorOptions options) throws IOException {
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
		Schema schema = SchemaLoader.load(rawSchema);

		JsonGeneratorEngine JSONgenerator = new JsonGeneratorEngine(options);
		JSONgenerator.visit(schema);
		System.out.println(JSONgenerator.builder.toString());
		JSONObject jsonObj = new JSONObject(JSONgenerator.builder.toString());
		String generatedJSON = jsonObj.toString(4);
		return generatedJSON;
	}

	public static String generateUgly(InputStream inputStream, JsonGeneratorOptions options) {
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));

		Schema schema = SchemaLoader.load(rawSchema);

		JsonGeneratorEngine JSONgenerator = new JsonGeneratorEngine(options);
		JSONgenerator.visit(schema);

		return JSONgenerator.builder.toString();
	}

	public static void main(String[] args) throws IOException {
		try (InputStream inputStream = JsonInstanceGenerator.class.getResourceAsStream("multiple.json")) {
			JsonGeneratorOptions options = new JsonGeneratorOptions();  
			options.setGenerateRandomValues(false);
			System.out.println("Ugly string: " + generateUgly(inputStream, options));
		}
	}
}