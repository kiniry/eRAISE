/**
 * 
 */
package core;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class Console{
	
	private final String CONSOLE_ID = IConsoleConstants.ID_CONSOLE_VIEW;
	
	private String message;
	
	private static Console instance = null; 
	
	public static Console getInstance(){
		if(instance == null)
			instance = new Console();
		
		return instance;
	}

	
	public Console(){
		
	}
	
	/**
	 * Displays output to plug-in Console
	 * 
	 * @param infomessage
	 * @param exitValue
	 */
	public void print(String infomessage, int channel, IFile ifile) {
		
		//make the console visible
		this.message = infomessage;
		
		// obtain the active page
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		
		IConsoleView view = null; 
		
		try {
			view = (IConsoleView) page.showView(CONSOLE_ID);
		} catch (PartInitException e) {
			System.out.println("Error when displaying console "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		MessageConsole rslConsole = findConsole(IConsoleConstants.ID_CONSOLE_VIEW);
		
		if(view != null){
			view.display(rslConsole);
			clear(rslConsole);
			MessageConsoleStream out = rslConsole.newMessageStream();
			out.println(message);
			
			/*
			//Notify registered observers
			//set changed
			setChanged();
				
			//
			//notify observers
			//
			//System.out.println("called notify");
			TCMessage tcm = new TCMessage(ifile, message, channel);
			notifyObservers(tcm);
		
			*/
		}		
		
	}
	

	/**
	 * Identifies a console by its id
	 *  
	 * @param idConsoleView
	 * @return
	 */
	private static MessageConsole findConsole(String name) {
		
		ConsolePlugin plugin = ConsolePlugin.getDefault();
	    IConsoleManager conMan = plugin.getConsoleManager();
	    IConsole[] existing = conMan.getConsoles();
	    //search for the console
	    for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	    
	    //if none found create a new console 
	    MessageConsole tcConsole = new MessageConsole(name, null);
	    conMan.addConsoles(new IConsole[]{tcConsole});
	    return tcConsole;
	}


	/**
	 * Clear console content
	 * 
	 * @param rslConsole
	 */
	public static void clear(MessageConsole rslConsole ){
		rslConsole.clearConsole();
	}
}
