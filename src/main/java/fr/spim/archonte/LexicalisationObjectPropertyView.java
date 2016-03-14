package fr.spim.archonte;

import org.protege.editor.owl.ui.view.objectproperty.AbstractOWLObjectPropertyViewComponent;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Laurent Mazuel
 */
public class LexicalisationObjectPropertyView extends
		AbstractOWLObjectPropertyViewComponent {

	/** To make Java happy */
	private static final long serialVersionUID = 3984586644861009005L;
	
	private LexicalisationGlobalPanel view = null;

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLPropertyViewComponent#updateView(org.semanticweb.owl.model.OWLProperty)
	 */
	@Override
	protected OWLObjectProperty updateView(OWLObjectProperty property) {
		view.updateView(property);
		return property;
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent#initialiseView()
	 */
	@Override
	public void initialiseView() throws Exception {
		view = new LexicalisationGlobalPanel(this);
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLPropertyViewComponent#disposeView()
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
