package fr.spim.archonte;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.AssertedSuperClassHierarchyProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

public class DifferentialClassHierarchyProvider extends AssertedSuperClassHierarchyProvider {

    private ChildClassExtractor childClassExtractor;
    private Set<OWLOntology> ontologies;
	
	public DifferentialClassHierarchyProvider(OWLModelManager owlModelManager) {
		super(owlModelManager);
        childClassExtractor = new ChildClassExtractor();
	}
	
	@Override
	public void setOntologies(Set<OWLOntology> ontologies) {
		super.setOntologies(ontologies);
		this.ontologies = ontologies;
	}
	
	@Override
	public boolean containsReference(OWLClass object) {
		// First test: exist really in real tree
		boolean existInRealTree = super.containsReference(object);
		// It don't exist in tree, no question...
		if(!existInRealTree) return false;
		// Second test, it is contained in this tree if it is a differantial concept.
		NamedClassExtractor extractor = new NamedClassExtractor();
		object.accept(extractor);
		Set<OWLClass> namedClasses = extractor.getResult();
		return namedClasses.size() == 1;
	}

	@Override
	public Set<OWLClass> getChildren(OWLClass object) {
		// Only one root in OWL: thing
		OWLClass root = getRoots().iterator().next();

		// If the param is root, the super class makes this great.
		if(object.equals(root)) {
			return super.getChildren(object);
		}
		else {
			// Extract the differential children
			Set<OWLClass> result = extractChildren(object);
			// Verify that there is no cycle
			for (Iterator<OWLClass> it = result.iterator(); it.hasNext();) {
				OWLClass curChild = it.next();
				if (getAncestors(object).contains(curChild)) {
					it.remove();
				}
			}
			// Return the children
			return result;
		}
	}
	
    private Set<OWLClass> extractChildren(OWLClass parent) {
        childClassExtractor.setCurrentParentClass(parent);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getReferencingAxioms(parent)) {
                if (ax.isLogicalAxiom()) {
                    ax.accept(childClassExtractor);
                }
            }
        }
        return childClassExtractor.getResult();
    }
    
	/** The same class as defined in {@link AssertedClassHierarchyProvider2}
	 */
	private class NamedClassExtractor extends OWLObjectVisitorAdapter {

		Set<OWLClass> result = new HashSet<OWLClass>();

		public void reset() { result.clear(); }

		public Set<OWLClass> getResult() { return result; }

		@Override
		public void visit(OWLClass desc) {
			result.add(desc);
		}

		@Override
		public void visit(OWLObjectIntersectionOf desc) {
			for (OWLClassExpression op : desc.getOperands()) {
				op.accept(this);
			}
		}
	}
	
	/**
     * Checks whether a class description contains ONLY the specified named conjunct (no other named class).
     */
    private class OnlyThisNamedConjunctChecker extends OWLObjectVisitorAdapter {

        private boolean foundParamClass;
        private boolean foundOtherNamedClass;
        private OWLClass searchClass;

        public boolean containsOnlySpecifiedClass(OWLClass conjunct, OWLClassExpression description) {
            foundParamClass = false;
            foundOtherNamedClass = false;
            searchClass = conjunct;
            description.accept(this);
            return !foundOtherNamedClass && foundParamClass;
        }

        @Override
		public void visit(OWLClass desc) {
            if (desc.equals(searchClass)) {
                foundParamClass = true;
            }
            else if(!desc.isAnonymous()) {
            	foundOtherNamedClass = true;
            }
        }

        @Override
		public void visit(OWLObjectIntersectionOf desc) {
            for (OWLClassExpression op : desc.getOperands()) {
                op.accept(this);
                if (foundOtherNamedClass) {
                    break;
                }
            }
        }
    }

    private class ChildClassExtractor extends OWLAxiomVisitorAdapter {

        private OnlyThisNamedConjunctChecker checker = new OnlyThisNamedConjunctChecker();
        private NamedClassExtractor namedClassExtractor = new NamedClassExtractor();
        private OWLClass currentParentClass;
        private Set<OWLClass> results = new HashSet<OWLClass>();

        public void reset() {
            results.clear();
            namedClassExtractor.reset();
        }

        public void setCurrentParentClass(OWLClass currentParentClass) {
            this.currentParentClass = currentParentClass;
            reset();
        }

        public Set<OWLClass> getResult() {
            return new HashSet<OWLClass>(results);
        }

        @Override
		public void visit(OWLSubClassOfAxiom axiom) {
            // Example:
            // If searching for subs of B, candidates are:
            // SubClassOf(A B)
            // SubClassOf(A And(B ...))
            if (checker.containsOnlySpecifiedClass(currentParentClass, axiom.getSuperClass())) {
                // We only want named classes
                if (!axiom.getSubClass().isAnonymous()) {
                    results.add(axiom.getSubClass().asOWLClass());
                }
            }
        }

//        @Override
//		public void visit(OWLEquivalentClassesAxiom axiom) {
//            // EquivalentClasses(A  And(B...))
//            if (!namedClassInEquivalentAxiom(axiom)){
//                return;
//            }
//            Set<OWLDescription> candidateDescriptions = new HashSet<OWLDescription>();
//            boolean found = false;
//            for (OWLDescription equivalentClass : axiom.getDescriptions()) {
//                if (!checker.containsOnlySpecifiedClass(currentParentClass, equivalentClass)) {
//                    // Potential operand
//                    candidateDescriptions.add(equivalentClass);
//                }
//                else {
//                    // This axiom is relevant
//                    if (equivalentClass.isAnonymous()) {
//                        found = true;
//                    }
//                }
//            }
//            if (!found) {
//                return;
//            }
//            namedClassExtractor.reset();
//            for (OWLDescription desc : candidateDescriptions) {
//                desc.accept(namedClassExtractor);
//            }
//            results.addAll(namedClassExtractor.getResult());
//        }
//
//        private boolean namedClassInEquivalentAxiom(OWLEquivalentClassesAxiom axiom) {
//            for (OWLDescription equiv : axiom.getDescriptions()){
//                if (!equiv.isAnonymous()){
//                    return true;
//                }
//            }
//            return false;
//        }
    }
}
