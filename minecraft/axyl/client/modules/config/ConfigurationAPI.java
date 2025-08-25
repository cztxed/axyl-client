package axyl.client.modules.config;

import java.io.IOException; 
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.*;
import org.json.JSONObject;

import com.google.common.base.Charsets;

import java.io.File;

public class ConfigurationAPI {
	
	public static Configuration loadExistingConfiguration(File file) throws IOException {
		JSONObject jsonObject = new JSONObject(FileUtils.readFileToString(file, Charsets.UTF_8));
		return new Configuration(file, jsonObject.toMap());
	}
	
	public static Configuration newConfiguration(File file) {
		return new Configuration(file);
	}
}