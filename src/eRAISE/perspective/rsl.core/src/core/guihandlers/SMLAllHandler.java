/**
 * 
 */
package core.guihandlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.swt.custom.BusyIndicator;

import core.Console;
import core.PluginLog;


/**
 * Called by the SML menu
 * 
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class SMLAllHandler extends AbstractHandler {
	
	private static PluginLog log = PluginLog.getInstance();

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//get all projects available in workspace
		IWorkspaceRoot root = org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot();
		final IProject[]  projects = root.getProjects();
		
		final SMLHandler smltr = new SMLHandler();
		
		//clear console
		Console.getInstance().clear();
		
		// Show a busy indicator while the runnable is executed
		BusyIndicator.showWhile(null, new Runnable() {

			@Override
			public void run() {
				for(int index = 0; index < projects.length; index++){
					//for each project					
					log.debug("Calling translateObject for "+projects[index]);
					smltr.translateObject(projects[index]);
				}
				
			}
		});
		return null;
	}

}
