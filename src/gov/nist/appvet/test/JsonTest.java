package gov.nist.appvet.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * @author steveq@nist.gov
 */
public class JsonTest {

	public JsonTest() {
		try {
			String jsonStr = "[{\"size\":1209411,"
					+ "\"file_location\":\"s3://appthority-apps/0a8570a5ccc037b014fd8488ddd7e23d.zip\","
					+ "\"id\":9284015,\"file_name\":\"0a8570a5ccc037b014fd8488ddd7e23d.zip\","
					+ "\"type\":10,"
					+ "\"file_full\":\"\","
					+ "\"file_hash\":\"0a8570a5ccc037b014fd8488ddd7e23d\","
					+ "\"status\":\"ready\","
					+ "\"file_type\":null,"
					+ "\"application_label\":\"Lights\","
					+ "\"file_hash_sha1\":\"46c922412487ccc073b2716466b75ae5461899a8\","
					+ "\"state\":1}]";

			InputStream inputStream = new ByteArrayInputStream(
					jsonStr.getBytes(Charset.forName("UTF-8")));

			JsonReader jsonReader = Json.createReader(inputStream);

			// reading arrays from json
			JsonArray jsonArray = jsonReader.readArray();

			// JSONArray arr = obj.getJSONArray("posts");
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject obj = jsonArray.getJsonObject(i);
				System.out.println("Reading object " + i);
				int idvalue = obj.getInt("id");
				System.out.println("ID: " + idvalue);
			}

			int index = 0;
			for (JsonValue value : jsonArray) {
				System.out.println("Value: " + value + "  at " + index++);
			}

			jsonReader.close();

			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		//JsonTest test = new JsonTest();

	}
}
