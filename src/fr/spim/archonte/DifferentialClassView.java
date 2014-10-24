package fr.spim.archonte;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Laurent Mazuel
 */
public class DifferentialClassView extends AbstractOWLClassViewComponent {

	/** To make Java happy */
	private static final long serialVersionUID = 5207361518184316214L;
	
	private DifferentialGlobalView<OWLClass> view = null;

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLClassViewComponent#initialiseClassView()
	 */
	@Override
	public void initialiseClassView() throws Exception {
		OWLModelManager manager = getOWLModelManager();
		OWLObjectHierarchyProvider<OWLClass> hierarchyProvider = manager.getOWLHierarchyManager().getOWLClassHierarchyProvider(); 
		view  = new DifferentialGlobalView<OWLClass>(manager, hierarchyProvider, this);
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLClassViewComponent#updateView(org.semanticweb.owl.model.OWLClass)
	 */
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		view.updateView(selectedClass);
		return selectedClass;
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent#disposeView()
	 */
	@Override
	public void disposeView() {
		view.disposeView();
	}

}
