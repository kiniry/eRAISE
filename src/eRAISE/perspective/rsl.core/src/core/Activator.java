package core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;



/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	//logger
	
	//rsltc binaries
	public static String [][] binaries = {
		{"All","rslml","resources/raise/sml"}, //0
		{"All","latex","resources/latex_content.txt"}, //1
	    {"Windows", "rsltc", "resources/win/raise/rsltc.exe"}, //2
	    {"Windows", "smlnj", "resources/win/sml"}, //3
	    {"Linux", "rsltc", "resources/linux/raise/rsltc"}, //4
	    {"Linux", "smlnj","resources/linux/sml"} //5
	    
	};
	
	
	//Don't initialize to lazily compute the listeners
	private List<IRSLTestCasesListener> listeners ;
	
	
	private static final String LISTENER_ID = "rsl.core.testcaseslisteners";
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Calls all listeners when a series of test are started
	 * ex: when the RT button is pressed
	 */
	public void testsStarted(){
		Iterator<IRSLTestCasesListener> it = getListeners().iterator();
		
		//for each listener
		//log.debug("Calls every listener");
		while(it.hasNext()){
			final IRSLTestCasesListener listener = (IRSLTestCasesListener) it.next();
			
			////log.debug("Create runnable");
			ISafeRunnable runnable = new ISafeRunnable() {
				
				@Override
				public void run() throws Exception {
					listener.testsStarted();
					
				}
				
				@Override
				public void handleException(Throwable exception) {
					;					
				}
			};
				
			////log.debug("Run for every listener");
			SafeRunner.run(runnable);
		}
	}
	
	private List<IRSLTestCasesListener> getListeners() {
		if( listeners == null){
			listeners = computeListeners();
			////log.debug("Listeners computed");
		}
		return listeners;
	}

	private List<IRSLTestCasesListener> computeListeners() {
		////log.debug("---- Enter compute listeners");
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(LISTENER_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		
		ArrayList<IRSLTestCasesListener> list = new ArrayList<IRSLTestCasesListener>();
		
		//for each extension to our extension point
		for(int index = 0; index < extensions.length; index++){
			IConfigurationElement[] elements = extensions[index].getConfigurationElements();
			
			//for each configuration element of a extension 
			for(int j = 0; j < elements.length; j++){
				try {
					Object listener = elements[j].createExecutableExtension("class");
					if(listener instanceof IRSLTestCasesListener)
						////log.debug("Added listener");
						list.add((IRSLTestCasesListener)listener);
				} catch (CoreException e) {
					//log.error(e.getMessage(), e);
				}
			}
		}
		//log.debug("----End compute listeners");
		return list;
	}

	/**
	 * Calls all listeners when all tests in a file are finished
	 * 
	 * @param name
	 * @param message
	 * @param ifile
	 */
	public void testsFinished(String message, IFile ifile) {
		Iterator<IRSLTestCasesListener> it = getListeners().iterator();
		final String msg = message;
		final IFile file = ifile;
		
		//for each listener
		while(it.hasNext()){
			final IRSLTestCasesListener listener = (IRSLTestCasesListener) it.next();
			
			ISafeRunnable runnable = new ISafeRunnable() {
				
				@Override
				public void run() throws Exception {
					listener.testFinished(msg, file);
				}
				
				@Override
				public void handleException(Throwable exception) {
					//TODO what happens if a listener throws exception?
					
				}
			};
				
			SafeRunner.run(runnable);
		}
		
	}
	
	
	public void addListener(IRSLTestCasesListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(IRSLTestCasesListener listener){
		listeners.remove(listener);
	}
	
	
	public void setBinariesPath(){
		String os = System.getProperty("os.name");
		
		if(os.indexOf("win") >= 0 || os.indexOf("Win")>=0){
			//windows
			//log.debug("Loading binaries windows");
			String rsltcWin = binaries[2][2];
			System.setProperty("rsltc", findPluginResource("rsl.core", rsltcWin));
			
			String smlnjWin = binaries[3][2];
			System.setProperty("smlnj", findPluginResource("rsl.core", smlnjWin));
			
			String rslmlWin = binaries[0][2];
			System.setProperty("rslml", findPluginResource("rsl.core", rslmlWin));
			
			String latexPath = binaries[1][2];
			System.setProperty("latex", findPluginResource("rsl.core", latexPath));
		}
		else
			if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 ){
				//linux
				//log.debug("Loading binaries linux");
				String rsltcLin = binaries[4][2];
				System.setProperty("rsltc", findPluginResource("rsl.core", rsltcLin));
				
				String smlnjLin = binaries[5][2];
				System.setProperty("smlnj", findPluginResource("rsl.core", smlnjLin));
				
				String rslmlLin = binaries[0][2];
				System.setProperty("rslml", findPluginResource("rsl.core", rslmlLin));
				
				String latexPath = binaries[1][2];
				System.setProperty("latex", findPluginResource("rsl.core", latexPath));
			}
			else {
				//so far we don't support other platforms;
			}
		
	}
	
	/**
	 * This method takes a relative path of a file and
	 * returns the absolute path of the file according
	 * to the runtime workspace
	 * 
	 * @param pluginName  The name of the plugin
	 * @param path  The path (as a String) of the file within the plugin
	 * @return The absolute file system location of the target file
	 *
	 */
	 public String findPluginResource(final String pluginName, 
              final String path){
		  //log.debug("---Enter findPLuginresource");
			
		  final Bundle bundle = Platform.getBundle(pluginName);
			
		  //log.debug("getting bundle");
		  IPath p = new Path(path);
		  final URL url = FileLocator.find(bundle, p, null);
		  //log.debug("getting url: "+url);
			
		  URL resolvedURL=null;
		  try {
			  resolvedURL = FileLocator.resolve(url);
			  //log.debug("Resolved url: "+resolvedURL);
			
		  } catch (IOException e) {
			  //log.error(e.getMessage(), e);
		  }
			
		  final String filePath = resolvedURL.getPath();	    
		  //log.debug("Resolved filePath: "+filePath);
			
		  final Path resourcePath = new Path(filePath);
			
		  p = resourcePath.makeAbsolute();
		  String s = p.toOSString();
		  //log.debug("ResourcePath: "+p);
			
		  if(s.startsWith("file:/") || s.startsWith("file:\\"))
			  s = s.substring(6);
			
		  //log.debug("----Returning "+s);
		  return s;
		}
}
