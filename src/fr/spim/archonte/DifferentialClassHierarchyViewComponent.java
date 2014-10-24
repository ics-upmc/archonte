package fr.spim.archonte;

import java.awt.Color;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;


public class DifferentialClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent {

    /** To make Java happy... */
	private static final long serialVersionUID = -9214158653656195848L;

	@Override
	protected void performExtraInitialisation() throws Exception {
        getTree().setBackground(new Color(255, 255, 215));
    }

    @Override
	protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        OWLObjectHierarchyProvider<OWLClass> provider = new DifferentialClassHierarchyProvider(getOWLModelManager());
        provider.setOntologies(getOWLModelManager().getActiveOntologies());
        return provider;
    }
}
