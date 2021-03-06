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
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class RTAllHandler extends AbstractHandler {

	private static PluginLog log = PluginLog.getInstance();
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		//get all projects available in workspace		
		IWorkspaceRoot root = org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();

		//clear console
		Console.getInstance().clear();
		
		final RTHandler rthandler = new RTHandler();
		// Show a busy indicator while the runnable is executed
		
		/*
		Shell shell = new Shell(Display.getDefault());
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		
		IRunnableWithProgress runnable;
		
		runnable = new IRunnableWithProgress(){
		     public void run(IProgressMonitor monitor) {
		         monitor.beginTask("Some nice progress message here ...", 100);
		         for(int index = 0; index < projects.length; index++){
						//for each project call type check project
						rthandler.runTestsObject(projects[index]);
					}
		         monitor.done();
		     }
		 }; 
		
		try {
			
			dialog.run(true, true, runnable);
			
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/		
		
		BusyIndicator.showWhile(null, new Runnable() {

			@Override
			public void run() {
				for(int index = 0; index < projects.length; index++){
					log.debug("For each project call runTestsObject");
					rthandler.runTestsObject(projects[index]);
				}
			}
		});

		return null;
	}

}
