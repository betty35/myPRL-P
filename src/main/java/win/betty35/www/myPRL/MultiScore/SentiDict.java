package win.betty35.www.myPRL.MultiScore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class SentiDict 
{
	public static String path="dict/sentiment";
	public static String pName="PC-ZH.txt";
	public static String nName="NC-ZH.txt";
	public static String iName="Intensity.txt";
	
	public static HashSet<String> possitive=null;
	public static HashSet<String> negative=null;
	public static HashMap<String,Integer> intensity=null;
	
	
	public static void init()
	{
		if(possitive==null) possitive=new HashSet<String>();
		else possitive.clear();
		if(negative==null) negative=new HashSet<String>();
		else negative.clear();
		if(intensity==null) intensity=new HashMap<String,Integer>();
		else intensity.clear();
		
		File pFile = new File(path+"/"+pName);
        
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(pFile));
           
            String str = null;
            while ((str = reader.readLine()) != null) {
            	str=str.replaceAll("\\s+", "");
            	possitive.add(str);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        File nFile = new File(path+"/"+nName);
        BufferedReader r2 = null;
        try {
            r2 = new BufferedReader(new FileReader(nFile));
            String str = null;
            while ((str = r2.readLine()) != null) {
            	str=str.replaceAll("\\s+", "");
            	negative.add(str);
            }
            r2.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (r2 != null) {
                try {
                    r2.close();
                } catch (IOException e1) {
                }
            }
        }
        
        
        File iFile = new File(path+"/"+iName);
        BufferedReader r3 = null;
        try {
            r3 = new BufferedReader(new FileReader(iFile));
            String str = null;
            int value=1;
            while ((str = r3.readLine()) != null) {
            	str=str.replaceAll("\\s+", "");
            	if(str.equals("")) value++;
            	else
            	{
            		intensity.put(str, value);
            	}
            }
            r3.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (r3 != null) {
                try {
                    r3.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
	public static boolean isPossitive(String word)
	{
		return possitive.contains(word);
	}
	
	public static boolean isNegative(String word)
	{
		return negative.contains(word);
	}
	
	public static int getIntensity(String word)
	{
		if(intensity.containsKey(word))
			return intensity.get(word);
		else return 1;
	}
}
