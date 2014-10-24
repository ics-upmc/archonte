/**
 * 
 */
package fr.spim.archonte;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;
import org.semanticweb.owlapi.model.RemoveAxiom;


/** Doe to Skos annotations visitor.
 * @author Laurent Mazuel
 */
public class DoeToSkosVisitor extends OWLOntologyWalkerVisitor<Object> {

	private OWLDataFactory factory = null; 

	private List<OWLOntologyChange> changes = null;
	
	private OWLAnnotationProperty skosPrefLabel = null;
	private OWLAnnotationProperty skosAltLabel = null;
	private OWLAnnotationProperty skosDefinition = null;
	private OWLAnnotationProperty skosHiddenLabel = null;

	public DoeToSkosVisitor(
			OWLOntologyWalker walker,
			OWLDataFactory factory) {
		super(walker);
		this.factory = factory;
		this.changes = new Vector<OWLOntologyChange>();
		
		skosPrefLabel = factory.getOWLAnnotationProperty(SKOSVocabulary.PREFLABEL.getIRI());
		skosAltLabel = factory.getOWLAnnotationProperty(SKOSVocabulary.ALTLABEL.getIRI());
		skosDefinition = factory.getOWLAnnotationProperty(SKOSVocabulary.DEFINITION.getIRI());
		skosHiddenLabel = factory.getOWLAnnotationProperty(SKOSVocabulary.HIDDENLABEL.getIRI());
	}
	
	public List<OWLOntologyChange> getChanges() {
		return changes;
	}
	
	@Override
	public Object visit(OWLObjectProperty desc) {
		return visit((OWLEntity)desc);
	}
	
	@Override
	public Object visit(OWLClass desc) {
		return visit((OWLEntity)desc) ;
	}

	public Object visit(OWLEntity desc) {
		Set<OWLAnnotation> annotations = desc.getAnnotations(getCurrentOntology());
		// For each annotation...
		for(OWLAnnotation annot : annotations) {
			// Del it if it is DOE
			OWLAnnotationProperty property = annot.getProperty();
			IRI propIri = property.getIRI();
			// Trying to test IRI and if match, go!
			if(LexicalisationGlobalPanel.doeAnnotations.contains(propIri)) {
				// Remove it
				OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(
						desc.getIRI(),
						annot);
				changes.add(new RemoveAxiom(getCurrentOntology(), axiom));
				// What fragment?
				String fragment = propIri.getFragment();
				OWLAnnotation newAnnot;
				if(fragment.equals("prefLabel")) {
					newAnnot = factory.getOWLAnnotation(
							skosPrefLabel,
							annot.getValue());
				}
				else if(fragment.equals("altLabel")) {
					newAnnot = factory.getOWLAnnotation(
							skosAltLabel,
							annot.getValue());
				}
				else if(fragment.equals("definition")) {
					newAnnot = factory.getOWLAnnotation(
							skosDefinition,
							annot.getValue());
				}
				else if(fragment.equals("hiddenLabel")) {
					newAnnot = factory.getOWLAnnotation(
							skosHiddenLabel,
							annot.getValue());
				}
				else {
					// Must be an error...
					continue;
				}
				OWLAxiom newAxiom = factory.getOWLAnnotationAssertionAxiom(
						desc.getIRI(),
						newAnnot);
				changes.add(new AddAxiom(getCurrentOntology(), newAxiom));
			}
		}
		return null;
	}
}
