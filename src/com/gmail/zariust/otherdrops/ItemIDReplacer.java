package com.gmail.zariust.otherdrops;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ItemIDReplacer {
	static File folder = new File("plugins" + File.separator + "OtherDrops" + File.separator + "materialchange");

	public static void replaceFile(int intVal, String matVal) {
        BufferedWriter out = null;
        try {
            File configFile = new File(folder.getAbsolutePath() + File.separator + intVal + ".to." + matVal + ".txt");
            configFile.getParentFile().mkdirs();
            if(!configFile.exists()) {
                configFile.createNewFile();
            }
            
            out = new BufferedWriter(new FileWriter(configFile));
            
            out.write("Please replace: " + intVal + " with '" + matVal + "'");
            out.close();
        } 
        
        catch (IOException exception) {
            exception.printStackTrace();
        }
		
	}
	
	public static Boolean checkEmpty() {
		Boolean isEmpty = false;
		if(folder.isDirectory()) {
			if(folder.list().length > 0) {
				isEmpty = false;
			}
			else {
				isEmpty = true;
			}
		}
		return isEmpty;
	}
}
