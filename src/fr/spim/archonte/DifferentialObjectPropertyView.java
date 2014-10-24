package fr.spim.archonte;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.objectproperty.AbstractOWLObjectPropertyViewComponent;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class DifferentialObjectPropertyView extends
		AbstractOWLObjectPropertyViewComponent {

	/** To make Java happy */
	private static final long serialVersionUID = -328975047134249766L;
	
	private DifferentialGlobalView<OWLObjectProperty> view = null;

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
		OWLModelManager manager = getOWLModelManager();
		OWLObjectHierarchyProvider<OWLObjectProperty> hierarchyProvider = manager.getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider();
		view = new DifferentialGlobalView<OWLObjectProperty>(manager, hierarchyProvider, this);
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLPropertyViewComponent#disposeView()
	 */
	@Override
	public void disposeView() {
		view.disposeView();
	}
}
