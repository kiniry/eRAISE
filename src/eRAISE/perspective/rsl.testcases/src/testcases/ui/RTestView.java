/**
 * 
 */
package testcases.ui;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import testcases.model.RSLTestCaseModel;

/**
 * Displays the RSL test results
 * 
 * Defines the "Test" View content as a
 * TreeViewer 
 * 
 * @author Marieta V. Fasie
 * 	marietafasie at gmail dot com
 *
 */
public class RTestView extends ViewPart {

	
	public static final String ID = "rsl.testcases.testview";
	
	private static TreeViewer viewer;
	
	
	/**
	 * 
	 */

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {	
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		
		TestCaseContentProvider contentProv = new TestCaseContentProvider(); 		
		viewer.setContentProvider(contentProv);
		contentProv.setViewer(viewer);
		
		viewer.setLabelProvider(new TestCaseLabelProvider());
		    
		// Expand level
		viewer.setAutoExpandLevel(2);
		
		// Provide the input to the viewer
		viewer.setInput(new RSLTestCaseModel());

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			  @Override
			  public void doubleClick(DoubleClickEvent event) {
			    TreeViewer viewer = (TreeViewer) event.getViewer();
			    IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection(); 
			    Object selectedNode = thisSelection.getFirstElement(); 
			    viewer.setExpandedState(selectedNode,
			        !viewer.getExpandedState(selectedNode));
			  }

			 
		});
	
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();

	}
	
	public TreeViewer getViewer(){
		return viewer;
	}

}
