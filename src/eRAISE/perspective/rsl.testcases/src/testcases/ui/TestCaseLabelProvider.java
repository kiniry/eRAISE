/**
 * 
 */
package testcases.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import testcases.model.RSLTestFile;
import testcases.model.TestCase;

/**
 * Provides the labels for the TestView
 * 
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class TestCaseLabelProvider extends LabelProvider{
	private static final Image TEST_TRUE = getImage("testok.gif");
	private static final Image TEST_FALSE = getImage("testfail.gif");
	private static final Image TEST_ERROR = getImage("testerr.gif");
	  
	private static final Image FILE_TEST_TRUE = getImage("file_true.gif");
	private static final Image FILE_TEST_FALSE = getImage("file_false.gif");
	private static final Image FILE_TEST_ERROR = getImage("file_error.gif");
	  
	private final String RUNTIME_ERR_FILE = "resources/sml_rt_errors.txt";
	
	@Override
	public String getText(Object element) {
	    if (element instanceof RSLTestFile) {
	      RSLTestFile rtf = (RSLTestFile) element;
	      return rtf.getName();
	    }
	  
	    if(element instanceof TestCase){
	    	TestCase tc = (TestCase) element;
	    	String tcText = tc.getName();
	    	if(tcText == "")
	    		return tc.getValue();
	    	return tcText+" "+tc.getValue();
	    }
	    return "";
	 
	  }

	@Override
	public Image getImage(Object element) {
		boolean runtimeErr = false;
		boolean falseTest = false;
	    if (element instanceof RSLTestFile) {
	    	List<TestCase> tflist= ((RSLTestFile) element).getTestCases();
	    	for(TestCase tc : tflist ){
	    		if( this.isRunTimeError(tc.getValue()))
	    			runtimeErr = true;
	    		if(tc.getValue().equals("false"))
	    			falseTest =true;
	    	}
	    	
	    	if(runtimeErr == true)
	    		return FILE_TEST_ERROR;
	    	if(falseTest == true)
	    		return FILE_TEST_FALSE;
	    	return FILE_TEST_TRUE;
	    }
	    
	    if(element instanceof TestCase){
	    	TestCase tc = (TestCase) element;
	    	if( tc.getValue().equals("false") )
	    		return TEST_FALSE;
	    	if( this.isRunTimeError(tc.getValue()))
	    		return TEST_ERROR;
	    	return TEST_TRUE;
	    }
	    return null;
	  }

	// Helper Method to load the images
	private static Image getImage(String file) {
	    Bundle bundle = FrameworkUtil.getBundle(TestCaseLabelProvider.class);
	    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
	    ImageDescriptor image = ImageDescriptor.createFromURL(url);
	    return image.createImage();

	} 
	
	public boolean isRunTimeError(String value) {
		File setupFile = null;
		
		//get access to bundle
		Bundle bundle = FrameworkUtil.getBundle(TestCaseLabelProvider.class);
		
		//get the runtime path to the resources/rsltc program
		IPath path = new Path(RUNTIME_ERR_FILE);				
		URL setupUrl = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
		
		//get the file with the absolute path 
		try {
			setupFile = new File(FileLocator.toFileURL(setupUrl).toURI());	
		
		} catch (URISyntaxException | IOException e) {			
			e.printStackTrace();
		}
		
		
		//log.debug("sml_rt_errors.txt location: "+ setupFile.getAbsolutePath());
		
		BufferedReader br=null;
		FileReader fr = null;
		String line;
		
		try {
			fr = new FileReader(setupFile);
			br = new BufferedReader(fr);
				
			while ((line = br.readLine()) != null) {
			   String[] pieces = line.split(" x ");
			   
			   if(pieces.length == 1 && value.contains(pieces[0])){ //it contains one line with no x
				  //log.debug("Just one line "+pieces[0]);
				   br.close();
				   return true;
			   }
			   
			   int i=0;
			   for(i=0; i<pieces.length; i++){
				   //log.debug(" "+pieces[i]);
				   if(!value.contains(pieces[i])) //does not contain one of the pieces
					   i = pieces.length + 3;
			   }
			   
			   if(i < pieces.length+3){//if all pieces were found in the test value
				   br.close();
				   return true;
			   }
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}