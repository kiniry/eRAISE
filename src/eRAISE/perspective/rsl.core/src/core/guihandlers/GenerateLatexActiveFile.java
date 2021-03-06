/**
 * 
 */
package core.guihandlers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import core.Console;
import core.ResourceHandler;
import core.PluginLog;


/**
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class GenerateLatexActiveFile extends AbstractHandler {

	private static PluginLog log = PluginLog.getInstance();
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//Get the editor	
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();		
		IEditorInput input = null;
		
		try{
			input = editorPart.getEditorInput();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return null;
		}
	
		if (input instanceof FileEditorInput) {
		    final IFile ifile = ((FileEditorInput) input).getFile();
		    //clear console
		    Console.getInstance().clear();
		    
		    //call the type checker if ifile is a file
			if(ifile != null){
				BusyIndicator.showWhile(null, new Runnable() {
					@Override
					public void run() {
						log.debug("Calling generateLatex for "+ifile.getName());
						generateLatex(ifile);
					}
				});
			}
		}
		return null;
	}

	
	public static void generateLatex(IFile ifile) {
		//generate new project with name Doc 
		String latexProjectName = ifile.getProject().getName()+"Doc";
		IProject latexProject = ResourcesPlugin.getWorkspace().getRoot().getProject(latexProjectName);
		//if the project already exists it won't create it
		ResourceHandler.addProject(latexProject);
		
		
		//create main.tex file
		ResourceHandler.addFile("main.tex", new Path(latexProjectName+"/src"));
		
		//add the 
		//\lstset{language=RSL}
		//\lstinputlisting{activefile.rsl}
		Path mainFilePath =  new Path(latexProjectName+"/src/main.tex");
		IFile latexFile =  ResourcesPlugin.getWorkspace().getRoot().getFile(mainFilePath);
		
		log.debug("main.tex file path: "+latexFile.getProjectRelativePath());
		//if main.tex does not contain \lstinputlisting{activefile.rsl}
		String text="";
		try {
			
			InputStream is = latexFile.getContents();
			log.debug("Bufered reader ");
			BufferedReader br = new BufferedReader( new InputStreamReader(is));
			String line;
			boolean includes = false;
			
			//check if it contains the lstinputlistings
			log.debug("Read main.tex");
			while ((line = br.readLine()) != null){
				if(line.contains("\\lstinputlisting{../.."+ifile.getFullPath()+"}")){
					log.debug("It already contains the line");
					includes = true;
				}
				else
					if(line.contains("\\end{document}") && includes == false){
						text +="\n\\lstset{language=rsl}\n";
						text +="\\lstinputlisting{../.."+ ifile.getFullPath().toString()+"}\n\n";
						text +="\\end{document}";
					}
					else
						text += line+"\n";
			}
			
			if( !includes ){
				log.debug("It does not contain the line");
				InputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));  ;
				latexFile.setContents(in, true, true, null);
				
			}
		} catch (CoreException | IOException e) {
			log.error(e.getMessage(), e);
		}
	
		Console.getInstance().print("File "+ ifile.getName() +" added to main.tex");
	}
	

}
