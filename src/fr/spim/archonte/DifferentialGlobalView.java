package fr.spim.archonte;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * @author Laurent Mazuel
 */
public class DifferentialGlobalView<T extends OWLEntity> {

	/* Doe Annotation */
	private static String doeNS = "http://cwi.nl/~troncy/DOE";
	private static IRI swpIRI = IRI.create(doeNS + "#swp");
	private static IRI swsIRI = IRI.create(doeNS + "#sws");
	private static IRI dwsIRI = IRI.create(doeNS + "#dws");
	private static IRI dwpIRI = IRI.create(doeNS + "#dwp");

	/* Protege ontology fields */
	private T currentClass = null;
	private OWLOntology currentOntology = null;
	private OWLModelManager currentOWLModelManager = null;
	private OWLDataFactory factory = null;
	private OWLObjectHierarchyProvider<T> objectHierarchyManager = null;
	private Set<OWLAnnotation> currentAnnotations = null;

	/* Protege swing component */
	AbstractOWLSelectionViewComponent protegeView = null;

	/* Swing component */
	private JTextArea SWPArea = null;
	private JTextArea SWSArea = null;
	private JTextArea DWSArea = null;
	private JTextArea DWPArea = null;
	private JButton editSWPButton = null;
	private JButton editSWSButton = null;
	private JButton buildDWPButton = null;

	DifferentialGlobalView(OWLModelManager owlModelManager,
			OWLObjectHierarchyProvider<T> objectHierarchyManager,
			AbstractOWLSelectionViewComponent protegeView) {
		// Initializing fields
		currentOWLModelManager = owlModelManager; // Assume that the OWL model manager never change...
		factory = currentOWLModelManager.getOWLDataFactory();
		this.objectHierarchyManager = objectHierarchyManager;
		this.protegeView = protegeView;

		// Listener
		DifferentialActionListener actionListener = new DifferentialActionListener();

		// Set value for this panel
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		protegeView.setLayout(gridbag);
		protegeView.setBorder(BorderFactory.createEmptyBorder());

		// Construct the components
		JLabel SWPLabel1 = new JLabel("Similarity");
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(30, 10, 0, 0);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(SWPLabel1, c);
		protegeView.add(SWPLabel1);

		JLabel SWPLabel2 = new JLabel("with Parent :");
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(SWPLabel2, c);
		protegeView.add(SWPLabel2);

		SWPArea = new JTextArea();
		SWPArea.setEditable(false);
		SWPArea.setBackground(new Color(230, 230, 230));
		SWPArea.setLineWrap(true);SWPArea.setWrapStyleWord(true);
		JScrollPane SWPScrollPane = new JScrollPane(SWPArea);
		SWPScrollPane.setPreferredSize(new Dimension(350,75));
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridheight = 2;
		c.insets = new Insets(10, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(SWPScrollPane, c);
		protegeView.add(SWPScrollPane);

		JLabel SWSLabel1 = new JLabel("Similarity");
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(30, 10, 0, 0);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(SWSLabel1, c);
		protegeView.add(SWSLabel1);

		JLabel SWSLabel2 = new JLabel("with Siblings :");
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(SWSLabel2, c);
		protegeView.add(SWSLabel2);

		SWSArea = new JTextArea();
		SWSArea.setEditable(false);
		SWSArea.setLineWrap(true);SWSArea.setWrapStyleWord(true);
		SWSArea.setBackground(new Color(230, 230, 230));
		JScrollPane SWSScrollPane = new JScrollPane(SWSArea);
		SWSScrollPane.setPreferredSize(new Dimension(350,75));
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridheight = 2;
		c.insets = new Insets(10, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(SWSScrollPane, c);
		protegeView.add(SWSScrollPane);

		JLabel DWSLabel1 = new JLabel("Difference");
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(30, 10, 0, 0);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(DWSLabel1, c);
		protegeView.add(DWSLabel1);

		JLabel DWSLabel2 = new JLabel("with Siblings :");
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(DWSLabel2, c);
		protegeView.add(DWSLabel2);

		DWSArea = new JTextArea();
		DWSArea.setLineWrap(true);DWSArea.setWrapStyleWord(true);
		JScrollPane DWSScrollPane = new JScrollPane(DWSArea);
		DWSScrollPane.setPreferredSize(new Dimension(350,75));
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridheight = 2;
		c.insets = new Insets(10, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(DWSScrollPane, c);
		protegeView.add(DWSScrollPane);

		JLabel DWPLabel1 = new JLabel("Difference");
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(30, 10, 0, 0);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(DWPLabel1, c);
		protegeView.add(DWPLabel1);

		JLabel DWPLabel2 = new JLabel("with Parent :");
		c.gridx = 0;
		c.gridy = 7;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(DWPLabel2, c);
		protegeView.add(DWPLabel2);

		DWPArea = new JTextArea();
		DWPArea.setEditable(false);
		DWPArea.setBackground(new Color(230, 230, 230));
		DWPArea.setLineWrap(true);DWPArea.setWrapStyleWord(true);
		JScrollPane DWPScrollPane = new JScrollPane(DWPArea);
		DWPScrollPane.setPreferredSize(new Dimension(350,75));
		c.gridx = 1;
		c.gridy = 6;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridheight = 2;
		c.insets = new Insets(10, 5, 10, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(DWPScrollPane, c);
		protegeView.add(DWPScrollPane);

		editSWPButton = new JButton();
		editSWPButton.setPreferredSize(new Dimension(100,30));
		editSWPButton.setText("edit SWP");
		editSWPButton.addActionListener(actionListener);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(30, 20, 0, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(editSWPButton, c);
		protegeView.add(editSWPButton);

		editSWSButton = new JButton();
		editSWSButton.setPreferredSize(new Dimension(100,30));
		editSWSButton.setText("edit SWS");
		editSWSButton.addActionListener(actionListener);
		c.gridx = 2;
		c.gridy = 2;
		c.insets = new Insets(30, 20, 0, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(editSWSButton, c);
		protegeView.add(editSWSButton);

		buildDWPButton = new JButton();
		buildDWPButton.setPreferredSize(new Dimension(100,30));
		buildDWPButton.setText("build DWP");
		buildDWPButton.addActionListener(actionListener);
		c.gridx = 2;
		c.gridy = 6;
		c.insets = new Insets(30, 20, 0, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(buildDWPButton, c);
		protegeView.add(buildDWPButton);
	}

	/** Clear all the Swing fields.
	 * 
	 */
	void clearAllFields() {
		DWPArea.setText("");
		DWSArea.setText("");
		SWPArea.setText("");
		SWSArea.setText("");
	}

	@SuppressWarnings("unchecked")
	void updateView(T selectedClass) {
		if(selectedClass != null) {
			// Get the active ontology
			OWLOntology activeOntology = currentOWLModelManager.getActiveOntology();

			// First, save the information of last concept (if exist)
			if(currentClass != null  && activeOntology.containsEntityInSignature(currentClass)) {
				// Remove old annotations from OWL model
				ArchonteUtils.removeAnnotation(currentOWLModelManager, currentClass, currentOntology, currentAnnotations);
				// Save the fields into the OWL model
				saveFieldsToModel(currentOWLModelManager, currentClass, currentOntology);
			}

			// Update fields
			currentClass = selectedClass;
			currentOntology = activeOntology;

			// Clear all the field before loading
			clearAllFields();

			// Assign current class to GUI and return the showed annotation
			Set<OWLAnnotation> annotFromModel = loadFromModel(selectedClass, activeOntology);
			currentAnnotations = annotFromModel;
		}
	}

	void disposeView() {
		// Save information of last concept selected
		if(currentClass != null) {
			saveFieldsToModel(currentOWLModelManager, currentClass, currentOntology);
		}
		currentClass = null;
		currentOntology = null;
		currentOWLModelManager = null;
		currentAnnotations.clear();
	}

	/** Save the current information in the class.
	 * @param selectedClass The selected class.
	 */
	void saveFieldsToModel(OWLModelManager modelManager,
			OWLEntity selectedClass,
			OWLOntology activeOntology) {

		// Get the direct String
		String swsText = SWSArea.getText();
		String swpText = SWPArea.getText();
		String dwsText = DWSArea.getText();
		String dwpText = DWPArea.getText();

		Set<OWLAnnotation> annotations = new TreeSet<OWLAnnotation>();

		if(!"".equals(swsText)) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(swsText);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(swsIRI), altIDConstant);
			annotations.add(annot);
		}
		if(!"".equals(swpText)) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(swpText);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(swpIRI), altIDConstant);
			annotations.add(annot);
		}
		if(!"".equals(dwsText)) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(dwsText);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(dwsIRI), altIDConstant);
			annotations.add(annot);
		}
		if(!"".equals(dwpText)) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(dwpText);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(dwpIRI), altIDConstant);
			annotations.add(annot);
		}

		ArchonteUtils.addAnnotation(modelManager, selectedClass, activeOntology, annotations);
	}

	/**
	 * @param selectedClass
	 * @param activeOntology
	 */
	Set<OWLAnnotation> loadFromModel(OWLEntity selectedClass,
			OWLOntology activeOntology) {

		Set<OWLAnnotation> finalAnnotation = new TreeSet<OWLAnnotation>();
		Set<OWLAnnotation> annotations = selectedClass.getAnnotations(activeOntology);
		for(OWLAnnotation annotation : annotations) {
			OWLAnnotationValue value = annotation.getValue();
			if(value instanceof OWLLiteral) {
				OWLLiteral constant = (OWLLiteral)value;
				String literal = constant.getLiteral();
				IRI annotationURI = annotation.getProperty().getIRI();

				if(annotationURI.equals(dwsIRI)) {
					DWSArea.setText(literal);
					finalAnnotation.add(annotation);						
				}
				else if(annotationURI.equals(swpIRI)) {
					SWPArea.setText(literal);
					finalAnnotation.add(annotation);						
				}
				else if(annotationURI.equals(dwpIRI)) {
					DWPArea.setText(literal);
					finalAnnotation.add(annotation);						
				}
				else if(annotationURI.equals(swsIRI)) {
					SWSArea.setText(literal);
					finalAnnotation.add(annotation);
				}
			}
		}
		return finalAnnotation;
	}

	// Edit SWP or SWS Button action performed
	private void editButton_actionPerformed(String principle) {
		// Display a Warning Message if the principle exists !
		//		if ((principle.equals("SWP")&&!(parentEditor.getParentView().getDOController().getCurrentNotion().getSWP().equals(""))) || (principle.equals("SWS")&&!(parentEditor.getParentView().getDOController().getCurrentNotion().getSWS().equals("")))) {
		//		JOptionPane.showMessageDialog(this, "The changes will be spread among siblings.", "Be Careful !", JOptionPane.INFORMATION_MESSAGE);
		//		}
		// Call the Edit Principle Dialog
		String oldContent = null;
		if (principle.equals("SWP"))
			oldContent = SWPArea.getText();
		else if (principle.equals("SWS"))
			oldContent = SWSArea.getText();
		EditPrincipleDialog edit = new EditPrincipleDialog(this, principle, oldContent);
		Dimension dialogSize = edit.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		edit.setLocation((screenSize.width-dialogSize.width)/2, (screenSize.height-dialogSize.height)/2);
		edit.setVisible(true);
	}

	// If OK Button of EditPrincipleDialog has been pressed
	void confirmEditButton(String principleType, String principleContent) {
		Set<T> siblings = ArchonteUtils.getAllSiblings(objectHierarchyManager, currentClass);
		if (principleType.equals("SWP")) {
			SWPArea.setText(principleContent);
		}
		else if (principleType.equals("SWS")) {
			SWSArea.setText(principleContent);
		}
		for(OWLEntity sib : siblings) {
			changeDifferentialAnnotation(principleType, sib, principleContent);
		}
	}

	void changeDifferentialAnnotation(String principleType, OWLEntity myEntity, String diffText) {
		// All annotations
		Set<OWLAnnotation> annotations = myEntity.getAnnotations(
				currentOntology,
				factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI()));
		// The annotation if a find it
		OWLAnnotation searchAnnotation = null;
		// The prefix I will use
		IRI goodPrefix = (principleType.equals("SWP"))?swpIRI:swsIRI;

		// Searching for the right annotation
		for(OWLAnnotation annotation : annotations) {
			OWLAnnotationValue value = annotation.getValue();
			if(value instanceof OWLLiteral) {
				IRI annotURI = annotation.getProperty().getIRI();
				if(annotURI.equals(goodPrefix)) {
					searchAnnotation = annotation;
					break;
				}
			}
		}

		// If there was an annotation, delete it
		if(searchAnnotation != null) {
			// Get the axiom
			OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(
					myEntity.getIRI(),
					searchAnnotation);
			// Remove it
			currentOWLModelManager.applyChange(new RemoveAxiom(currentOntology, axiom));
		}
		// Create a new annotation
		OWLLiteral altIDConstant = factory.getOWLLiteral(diffText);
		OWLAnnotation newAnnot = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(goodPrefix), altIDConstant);
		// Create the axiom
		OWLAxiom newAxiom = factory.getOWLAnnotationAssertionAxiom(
				myEntity.getIRI(),
				newAnnot);
		// Add it
		currentOWLModelManager.applyChange(new AddAxiom(currentOntology, newAxiom));
	}

	// Build DWP Button action performed
	private void buildDWPButton_actionPerformed() {
		DWPArea.setText(SWSArea.getText() + " : " + DWSArea.getText());
	}

	class DifferentialActionListener implements ActionListener {

		DifferentialActionListener() {}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==editSWPButton)
				editButton_actionPerformed("SWP");
			else if (e.getSource()==editSWSButton)
				editButton_actionPerformed("SWS");
			else if (e.getSource()==buildDWPButton)
				buildDWPButton_actionPerformed();
		}
	}
}
