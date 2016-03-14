/**
 * 
 */
package fr.spim.archonte;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * @author Laurent Mazuel
 */
public class ArchonteUtils {

	private ArchonteUtils() {}

	/** Removes the set annotations of the entity from the given ontology using the given manager.
	 * @param modelManager
	 * @param currentEntity
	 * @param ontology
	 * @param annotations
	 */
	public static void removeAnnotation(OWLModelManager modelManager,
			OWLEntity currentEntity,
			OWLOntology ontology,
			Set<OWLAnnotation> annotations) {

		OWLDataFactory factory = modelManager.getOWLDataFactory();

		List<OWLOntologyChange> changes = new Vector<OWLOntologyChange>();
		for(OWLAnnotation annot : annotations) {
			OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(
					currentEntity.getIRI(),
					annot);
			changes.add(new RemoveAxiom(ontology, axiom));
		}
		modelManager.applyChanges(changes);
	}

	/** Adding the set annotations to the entity from the given ontology using the given manager.
	 * @param modelManager
	 * @param currentEntity
	 * @param ontology
	 * @param annotations
	 */
	public static void addAnnotation(OWLModelManager modelManager,
			OWLEntity currentEntity,
			OWLOntology ontology,
			Set<OWLAnnotation> annotations) {

		OWLDataFactory factory = modelManager.getOWLDataFactory();

		List<OWLOntologyChange> changes = new Vector<OWLOntologyChange>();
		for(OWLAnnotation annot : annotations) {
			OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(
					currentEntity.getIRI(), 
					annot);
			changes.add(new AddAxiom(ontology, axiom));
		}
		modelManager.applyChanges(changes);
	}

	/** Get all the siblings of the given OWL entity (without me).
	 * If multiple parent, all siblings will be in the set.
	 * @param <T>
	 * @param hierarchyProvider
	 * @param entity
	 * @return
	 */
	public static <T extends OWLEntity> Set<T> getAllSiblings(OWLObjectHierarchyProvider<T> hierarchyProvider, T entity) {
		Set<T> parents = hierarchyProvider.getParents(entity);
		Set<T> siblings = new TreeSet<T>();
		for(T parent : parents) {
			Set<T> children = hierarchyProvider.getChildren(parent);
			for(T child : children) {
				if(!child.equals(entity))
					siblings.add(child);
			}
		}
		return siblings;
	}

	/** Cut the first and the last character.
	 * For instance, string:
	 * "MyString"
	 * will be result
	 * MyString
	 * @param s
	 * @return
	 */
	public static String trimDoubleQuot(String s) {
		return s.substring(1, s.length()-1);
	}
}
