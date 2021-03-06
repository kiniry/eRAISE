/**
 * 
 */
package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;




/**
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class TypeChecker {

	private static PluginLog log = PluginLog.getInstance();
	
	/**
	 * Type checks an RSL file
	 * 
	 * @param context an RSL file
	 */
	public void typeCheck(IFile ifile){
		
		log.debug("Enter typeCheck method");
		
		
		String infomessage = "";
		Process process = null;
		
		log.debug("Current platform: "+System.getProperty("os.name"));
		
		//path of the file opened in the editor
		IPath ipath = ifile.getLocation();
		String filePath = ipath.toString();
		ProcessBuilder builder = null;
		
		String os = System.getProperty("os.name");
		
		String programPath = System.getProperty("rsltc");
		
		if( programPath == null){
			Activator.getDefault().setBinariesPath();
			programPath = System.getProperty("rsltc");
		}
		
		
		
		if( os.indexOf("win") >= 0 || os.indexOf("Win")>=0){
			
			/*
			 * to execute a DOS command from a Java program, you need to prepend
			 * the Windows command shell cmd /c to the command
			 * you want to execute. In other words the type checker
			 * needs to be wrapped in the cmd.
			 * The '/c' switch terminates the command shell after the command completes
			 */

			log.debug("System: "+System.getProperty("os.name"));
			
			log.debug("Building program: cmd /c \n    "+ programPath+" \n   "+filePath);
			builder = new ProcessBuilder("cmd", "/C", programPath, filePath);
				
			
			Map<String, String> env = builder.environment();
			
			if (! env.containsKey("Path")) {
				log.debug("Adding env variable: "+programPath);
		    	env.put("Path", programPath);	    	
		    }
			else{
				String path = env.get("Path");
				if(!path.contains(programPath)){
					log.debug("Adding env variable: "+";"+programPath);
					path = path.concat(";"+programPath);
					env.put("Path", path);
				}
			}
			
			//correlate the error messages with the output messages
			builder.redirectErrorStream(true);
		}
		
		else{
			
			if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 ){
				//linux							
				
				//give rights
				log.debug("rsltc path: "+programPath);
				
				ProcessBuilder giveRights = new ProcessBuilder("chmod", "777",programPath);
				log.debug("Rights given");
				try {
					giveRights.start();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
					
				builder = new ProcessBuilder(programPath, filePath);
			}
		}
		
		//correlate the error messages with the output messages
		builder.redirectErrorStream(true);
				
		try {
			//start the program
			log.debug("Process started...");
			process = builder.start();
		
			//get the input stream
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			//read the program output line by line
			String line = br.readLine();
						
			while (line != null && ! line.trim().equals("--EOF--")) {
		        infomessage += line+"\n";
		        line = br.readLine();
		    }
		
			log.debug("Program finished\n");
		
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		
		Console.getInstance().print(infomessage);
		
	}
	
}
