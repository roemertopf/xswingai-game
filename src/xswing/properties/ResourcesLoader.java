package xswing.properties;

import static lib.mylib.options.Paths.FONT_DIR;
import static lib.mylib.options.Paths.MUSIC_DIR;
import static lib.mylib.options.Paths.RES_DIR;
import static lib.mylib.options.Paths.SOUND_DIR;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import lib.mylib.Sound;
import lib.mylib.properties.GameConfig;
import lib.mylib.properties.ObjectConfig;
import lib.mylib.properties.ObjectConfigSet;

public class ResourcesLoader {
	
	public static Map<String, ObjectConfig>  getObjectStore(ObjectConfigSet objectConfigSet){
		Map<String, ObjectConfig> objectsStore= new HashMap<String, ObjectConfig>(objectConfigSet.getConfigs().size());
		for (ObjectConfig objectConfig : objectConfigSet) {
			objectsStore.put(objectConfig.getObjectName(), objectConfig);
		}
		return objectsStore;
	}
	
	public static void accesAllResources(GameConfig config) throws SlickException {
		for (ObjectConfigSet objectConfigSet : config) {

			for (String music : config.getMusicPlayList()) {
				new Music(MUSIC_DIR + music,true);
			}

			for (ObjectConfig objectConfig : objectConfigSet) {
				if (objectConfig.isSetImage()) {
					new Image(RES_DIR + objectConfig.getImage());
				}
				if (objectConfig.isSetSounds()) {
					for (String sound : objectConfig.getSounds().values()) {
						new Sound(SOUND_DIR + sound);
					}
				}
				if (objectConfig.isSetImages()) {
					for (String image : objectConfig.getImages().values()) {
						new Image(RES_DIR + image);
					}
				}
				if (objectConfig.isSetFonts()) {
					for (String font : objectConfig.getFonts()) {
						new AngelCodeFont(FONT_DIR + font + ".fnt", FONT_DIR + font + ".png");
					}
				}
			}
		}

	}

}
