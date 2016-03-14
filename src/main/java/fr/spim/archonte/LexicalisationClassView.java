package fr.spim.archonte;

import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Laurent Mazuel
 */
public class LexicalisationClassView extends AbstractOWLClassViewComponent {

	/** To make Java Happy */
	private static final long serialVersionUID = 6616815800389411183L;
			
	private LexicalisationGlobalPanel view = null;

	// create the GUI
	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLClassViewComponent#initialiseClassView()
	 */
	@Override
	public void initialiseClassView() throws Exception {
		view = new LexicalisationGlobalPanel(this);
	}
		
	// called automatically when the global selection changes
	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLClassViewComponent#updateView(org.semanticweb.owl.model.OWLClass)
	 */
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		view.updateView(selectedClass);
		return selectedClass;
	}
	
	
	// remove any listeners and perform tidyup (none required in this case)
	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent#disposeView()
	 */
	@Override
	public void disposeView() {
		try {
			view.disposeView();
		} catch (Exception e) {
			// No big deal
		}
	}
}
