package xswing.properties;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.newdawn.slick.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import lib.mylib.properties.GameConfig;
import lib.mylib.properties.GameConfigs;

public class XSGameConfigs {

	public static final String OPTION_FILE_NAME = "config.json";

	private static GameConfigs configs = new DefaultGameConfigs(); // war auskommentiert new..

	
	public static GameConfig getConfig()  {
		return getConfig(OPTION_FILE_NAME);
	}
	
	public static GameConfig getConfig(String configFile)  {
		if(configs==null){
			configs = loadConfig(configFile);
		}
		return configs.getSelectedConfig();
	}
	
	public static GameConfigs loadConfig(String configFile)  {
		Gson gson = new Gson();
		GameConfigs configs;
		try {
			configs = gson.fromJson(new FileReader(configFile), GameConfigs.class);
			Log.info("Loaded configFile: "+configFile);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return new DefaultGameConfigs();
		} catch (JsonIOException e) {
			e.printStackTrace();
			return new DefaultGameConfigs();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			return new DefaultGameConfigs();
		}
		return configs;
	}	

}
