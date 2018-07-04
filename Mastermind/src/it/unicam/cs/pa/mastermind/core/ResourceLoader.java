package core;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ResourceLoader {

	private static Map<String,BufferedImage> codep,keyp,struct;
	private Map<String,CodePeg> pegSet;

	private ResourceLoader(Map<String,CodePeg> pegSet)
	{
		this.pegSet = pegSet;
		this.loadDynamicResources();
	}
	
	private ResourceLoader()
	{
		this.loadFixedResources();
	}
	private void loadDynamicResources() //get from pegset
	{
		codep = new LinkedHashMap<>();
		try {
			for(String peg : this.pegSet.keySet())
			{
				if(peg!="EMPTY")
					codep.put(peg, ImageIO.read(getClass().getResource("graphics/"+peg+".png")));
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Resource not found!");
		}
	}
	
	private void loadFixedResources()
	{
		keyp = new HashMap<>();
		struct = new HashMap<>();
		try {
			keyp.put("WHITE", ImageIO.read(getClass().getResource("graphics/whitecode.png")));
			keyp.put("BLACK", ImageIO.read(getClass().getResource("graphics/blackcode.png")));
			struct.put("EMPTYSN", ImageIO.read(getClass().getResource("graphics/emptyshadedframe.png")));
			struct.put("EMPTYML", ImageIO.read(getClass().getResource("graphics/empty4shaded.png")));
			struct.put("EMPTYFRAME", ImageIO.read(getClass().getResource("graphics/emptyframe.png")));
			struct.put("SNKEY", ImageIO.read(getClass().getResource("graphics/snkey.png")));
		} catch (IOException e) {
			throw new RuntimeException("Resource not found!");
		}

	}
	
	public static Map<String,BufferedImage> getCodeResources(Map<String,CodePeg> pegSet)
	{
		if(codep == null)
			new ResourceLoader(pegSet);
		return codep;
	}
	
	public static Map<String,BufferedImage> getKeyResources()
	{
		if(keyp == null)
			new ResourceLoader();
		return keyp;
	}
	
	public static Map<String,BufferedImage> getStructResources()
	{
		if(struct == null)
			new ResourceLoader();
		return struct;
	}
}
