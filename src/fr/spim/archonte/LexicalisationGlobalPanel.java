package fr.spim.archonte;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

/**
 * @author Laurent Mazuel
 */
public class LexicalisationGlobalPanel {

	/* Doe Annotation */
	public static final String doeNS = "http://cwi.nl/~troncy/DOE#";
	public static final IRI prefLabelIRI = IRI.create(doeNS + "prefLabel");
	public static final IRI altLabelIRI = IRI.create(doeNS + "altLabel");
	public static final IRI definitionIRI = IRI.create(doeNS + "definition");
	public static final IRI hiddenLabelIRI = IRI.create(doeNS + "hiddenLabel");

	public static Set<IRI> doeAnnotations = null;
	static {
		doeAnnotations = new HashSet<IRI>();
		doeAnnotations.add(prefLabelIRI);
		doeAnnotations.add(altLabelIRI);
		doeAnnotations.add(definitionIRI);
		doeAnnotations.add(hiddenLabelIRI);
	}

	/** Language allowed in panel */
	private static final String[] LANGUAGE = {"fr", "en", "nl", "de", "it", "pt", "es", "la"};

	/* Protege ontology fields */
	private OWLEntity currentEntity = null;
	private OWLOntology currentOntology = null;
	private Set<OWLAnnotation> currentAnnotations = null;
	private AbstractOWLViewComponent protegeView = null;

	/* SWING components */
	private JTextField preferredTermField = null;
	private JTextField labelTextField = null;
	private JTextArea encyclopedicDefArea = null;
	//	private JTextArea commentArea = null;
	private JTextField synonymTextField = null;
	private DefaultListModel synonymListModel = null;
	private JButton addSynonymButton = null;
	private JButton removeSynonymButton = null;
	private JButton getLabelFromOntologyButton = null;
	private JComboBox languageSelectionBox = null;

	private String currentLanguage = null;
	// If this plugin is activated
	private boolean isActivated = true;

	/** Create the GUI. */
	LexicalisationGlobalPanel(AbstractOWLViewComponent protegeView) {
		// Initializing fields
		this.protegeView = protegeView;
		currentLanguage = "fr";

		// Build an actionListener
		ArchonteActionListener actionListener = new ArchonteActionListener();
		ArchonteListActionListener listActionListener = new ArchonteListActionListener();

		// Set value for this panel
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		protegeView.setLayout(gridbag);
		protegeView.setBorder(BorderFactory.createEmptyBorder());

		// Construct the components
		JLabel preferredTermLabel = new JLabel("Preferred Term :");
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(20, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(preferredTermLabel, c);
		protegeView.add(preferredTermLabel);

		preferredTermField = new JTextField();
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.insets = new Insets(20, 10, 0, 20);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(preferredTermField, c);
		protegeView.add(preferredTermField);

		JLabel encyclopedicDefLabel1 = new JLabel("Encyclopedic");
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(20, 10, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(encyclopedicDefLabel1, c);
		protegeView.add(encyclopedicDefLabel1);

		JLabel encyclopedicDefLabel2 = new JLabel("definition :");
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.insets = new Insets(35, 10, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;  //.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(encyclopedicDefLabel2, c);
		protegeView.add(encyclopedicDefLabel2);

		encyclopedicDefArea = new JTextArea();
		encyclopedicDefArea.setLineWrap(true);encyclopedicDefArea.setWrapStyleWord(true);
		JScrollPane encyclopedicDefScrollPane = new JScrollPane(encyclopedicDefArea);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.25;
		c.insets = new Insets(10, 10, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(encyclopedicDefScrollPane, c);
		protegeView.add(encyclopedicDefScrollPane);

		JLabel commentLabel = new JLabel("Language :");
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(10, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(commentLabel, c);
		protegeView.add(commentLabel);

		languageSelectionBox = new JComboBox(LANGUAGE);
		languageSelectionBox.setSelectedIndex(0);
		languageSelectionBox.addActionListener(actionListener);
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.50;
		c.insets = new Insets(30, 0, 10, 0);
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(languageSelectionBox, c);
		protegeView.add(languageSelectionBox);

		//		commentArea = new JTextArea();
		//		commentArea.setLineWrap(true); commentArea.setWrapStyleWord(true);
		//		JScrollPane commentDefScrollPane = new JScrollPane(commentArea);
		//		c.gridx = 3;
		//		c.gridy = 1;
		//		c.gridwidth = GridBagConstraints.REMAINDER;
		//		c.gridheight = 1;
		//		c.weightx = 0.5;
		//		c.weighty = 0.25;
		//		c.insets = new Insets(10, 10, 0, 20);
		//		c.anchor = GridBagConstraints.CENTER;
		//		c.fill = GridBagConstraints.BOTH;
		//		gridbag.setConstraints(commentDefScrollPane, c);
		//		this.add(commentDefScrollPane);

		JLabel LabelLabel = new JLabel("Label :");
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(10, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(LabelLabel, c);
		protegeView.add(LabelLabel);

		labelTextField = new JTextField();
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.insets = new Insets(20, 10, 10, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(labelTextField, c);
		protegeView.add(labelTextField);

		getLabelFromOntologyButton = new JButton();
		getLabelFromOntologyButton.setPreferredSize(new Dimension(140,30));
		getLabelFromOntologyButton.setMinimumSize(new Dimension(140,30));
		getLabelFromOntologyButton.setText("Get label from tree");
		getLabelFromOntologyButton.setToolTipText("Fill the label field with the label from the selected ndoe in the tree");
		getLabelFromOntologyButton.addActionListener(actionListener);
		c.gridx = 3;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(10, 10, 0, 20);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(getLabelFromOntologyButton, c);
		protegeView.add(getLabelFromOntologyButton);

		JLabel newSynonymLabel = new JLabel("New synonym :");
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(10, 10, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(newSynonymLabel, c);
		protegeView.add(newSynonymLabel);

		synonymTextField = new JTextField();
		synonymTextField.addActionListener(actionListener);
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.insets = new Insets(10, 10, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		gridbag.setConstraints(synonymTextField, c);
		protegeView.add(synonymTextField);

		JLabel synonymListLabel = new JLabel("Synonym List :");
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(30, 10, 10, 0);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(synonymListLabel, c);
		protegeView.add(synonymListLabel);

		synonymListModel = new DefaultListModel();
		JList synonymList = new JList(synonymListModel);
		synonymList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		synonymList.addListSelectionListener(listActionListener);
		JScrollPane synonymListScrollPane = new JScrollPane(synonymList);
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.insets = new Insets(10, 10, 10, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(synonymListScrollPane, c);
		protegeView.add(synonymListScrollPane);

		addSynonymButton = new JButton();
		addSynonymButton.setPreferredSize(new Dimension(140,30));
		addSynonymButton.setMinimumSize(new Dimension(140,30));
		addSynonymButton.setText("add synonym");
		addSynonymButton.addActionListener(actionListener);
		c.gridx = 3;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(10, 10, 0, 20);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(addSynonymButton, c);
		protegeView.add(addSynonymButton);

		removeSynonymButton = new JButton();
		removeSynonymButton.setPreferredSize(new Dimension(140,30));
		removeSynonymButton.setMinimumSize(new Dimension(140,30));
		removeSynonymButton.setText("remove synonym");
		removeSynonymButton.addActionListener(actionListener);
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.insets = new Insets(30, 10, 0, 20);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(removeSynonymButton, c);
		protegeView.add(removeSynonymButton);
	}

	void updateView(OWLEntity selectedClass) {
		if(selectedClass != null && isActivated) {

			// First, save the information of last concept (if exist)
			saveCurrentInformation();

			// Update fields
			currentEntity = selectedClass;
			currentOntology = protegeView.getOWLModelManager().getActiveOntology();

			// Clear all the field before loading
			clearAllFields();

			// Assign current class to GUI and return the showed annotation
			Set<OWLAnnotation> annotFromModel = null;
			try {
				annotFromModel = loadFromModel(selectedClass, currentOntology, currentLanguage);
			}
			catch (NeedConvertionException err ) {
				// Argh, old version....
				StringBuilder buf = new StringBuilder();
				buf.append("<html><p>");
				buf.append("Your ontology uses old DOE annotations format.<br/>");
				buf.append("You have to convert this file to SKOS if you want to use this plugin.<br/>");
				buf.append("Would you make it now? Otherwise, this plugin will be disabled.<br/></p>");
				buf.append("<p>Note that this process can freeze your Protégé app during some <br/>");
				buf.append("few seconds during convertion depending the size of your ontology</p>");
				buf.append("</html>");
				int result = JOptionPaneEx.showConfirmDialog(
						(Component)null,
						"Converting DOE to SKOS",
						new JLabel(buf.toString()),
						JOptionPane.WARNING_MESSAGE,
						JOptionPane.YES_NO_OPTION,
						(JComponent)null) ;
				if(result != JOptionPane.YES_OPTION) {
					isActivated = false;
					currentEntity = null;
					return;
				}
				OWLDataFactory factory = protegeView.getOWLModelManager().getOWLDataFactory();
				OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(currentOntology)); 
				DoeToSkosVisitor convertor = new DoeToSkosVisitor(walker, factory);
				walker.walkStructure(convertor);
				OWLModelManager manager = protegeView.getOWLModelManager();
				manager.applyChanges(convertor.getChanges());
				JOptionPane.showMessageDialog(null, "I have made "+convertor.getChanges().size()+" changes on annotations.");
				// Maintenant, je peux charger le modèle
				annotFromModel = loadFromModel(selectedClass, currentOntology, currentLanguage);
			}
			currentAnnotations = annotFromModel;
		}
	}

	void disposeView() {
		// Save information of last concept selected
		saveCurrentInformation();
		currentEntity = null;
		currentOntology = null;
		if(currentAnnotations != null)
			currentAnnotations.clear();
	}

	private void saveCurrentInformation() {
		if(currentEntity != null && currentOntology.containsEntityInSignature(currentEntity)) {
			// Remove old annotations from model
			ArchonteUtils.removeAnnotation(protegeView.getOWLModelManager(), currentEntity, currentOntology, currentAnnotations);
			// Save the fields into the model
			saveFieldsToModel(protegeView.getOWLModelManager(), currentEntity, currentOntology, currentLanguage);
		}
	}

	/** Clear all the Swing fields.
	 * 
	 */
	void clearAllFields() {
		synonymListModel.clear();
		//		commentArea.setText("");
		encyclopedicDefArea.setText("");
		labelTextField.setText("");
		preferredTermField.setText("");
	}

	/** Save the current information in the class.
	 * @param selectedClass The selected class.
	 */
	void saveFieldsToModel(OWLModelManager modelManager,
			OWLEntity selectedClass,
			OWLOntology activeOntology,
			String lang) {

		// Get the direct String 
		String newLabel = labelTextField.getText();
		String preferredTerm = preferredTermField.getText();
		String encyclo = encyclopedicDefArea.getText();
		//		String comment = commentArea.getText();
		Collection<String> synonymList = new Vector<String>();
		Enumeration enumeration = synonymListModel.elements();
		while(enumeration.hasMoreElements()) {
			synonymList.add((String)enumeration.nextElement());
		}

		Set<OWLAnnotation> annotations = new TreeSet<OWLAnnotation>();

		OWLDataFactory factory = protegeView.getOWLModelManager().getOWLDataFactory();
		if(!"".equals(newLabel))  {
			OWLLiteral altIDConstant = factory.getOWLLiteral(newLabel, lang);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(SKOSVocabulary.HIDDENLABEL.getIRI()),
					altIDConstant);
			annotations.add(annot);
		}
		if(!"".equals(preferredTerm)) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(preferredTerm, lang);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(SKOSVocabulary.PREFLABEL.getIRI()),
					altIDConstant);
			annotations.add(annot);
		}
		if(!"".equals(encyclo)) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(encyclo, lang);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(SKOSVocabulary.DEFINITION.getIRI()),
					altIDConstant);
			annotations.add(annot);
		}
		for(String el : synonymList) {
			OWLLiteral altIDConstant = factory.getOWLLiteral(el, lang);
			OWLAnnotation annot = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(SKOSVocabulary.ALTLABEL.getIRI()),
					altIDConstant);
			annotations.add(annot);
		}

		ArchonteUtils.addAnnotation(modelManager, selectedClass, activeOntology, annotations);
	}

	/**
	 * @param selectedClass
	 * @param activeOntology
	 */
	Set<OWLAnnotation> loadFromModel(OWLEntity selectedClass,
			OWLOntology activeOntology,
			String langToLoad) throws NeedConvertionException {

		Set<OWLAnnotation> finalAnnotation = new TreeSet<OWLAnnotation>();
		Set<OWLAnnotation> annotations = selectedClass.getAnnotations(activeOntology);
		for(OWLAnnotation annotation : annotations) {
			OWLAnnotationValue constant = annotation.getValue();
			if(constant instanceof OWLLiteral) {
				OWLLiteral untypedConstant = (OWLLiteral)constant;
				String literal = untypedConstant.getLiteral();
				String lang = untypedConstant.getLang();
				if(langToLoad.equals(lang)) {

					IRI annotationURI = annotation.getProperty().getIRI();
					if(doeAnnotations.contains(annotationURI)) {
						// Get out of here!!!
						throw new NeedConvertionException();
					}
					else if(annotationURI.equals(SKOSVocabulary.PREFLABEL.getIRI())) {
						// Pref label
						preferredTermField.setText(literal);
						finalAnnotation.add(annotation);							
					}
					else if(annotationURI.equals(SKOSVocabulary.ALTLABEL.getIRI())) {
						// Synonym
						synonymListModel.addElement(literal);
						finalAnnotation.add(annotation);							
					}
					else if(annotationURI.equals(SKOSVocabulary.DEFINITION.getIRI())) {
						// EncyclopedicDefinition
						encyclopedicDefArea.setText(literal);
						finalAnnotation.add(annotation);							
					}
					else if(annotationURI.equals(SKOSVocabulary.HIDDENLABEL.getIRI())) {
						// altID
						labelTextField.setText(literal);
						finalAnnotation.add(annotation);							
					}						
				}
			}
		}
		return finalAnnotation;
	}

	/** Used if convertion is necessary.
	 * @author Laurent Mazuel 
	 */
	private class NeedConvertionException extends RuntimeException {
		public NeedConvertionException() {
			// TODO Auto-generated constructor stub
		}
	}

	private class ArchonteActionListener implements ActionListener {

		/** Package constructor */
		ArchonteActionListener() {}

		public void actionPerformed(ActionEvent e) {

			if (e.getSource()==addSynonymButton || e.getSource()==synonymTextField) {
				if (!synonymTextField.getText().equals("")) {
					// Add to synonym list, do not modify OWL file
					synonymListModel.addElement(synonymTextField.getText());
					synonymTextField.setText("");
				}
			}
			else if (e.getSource()==removeSynonymButton) {
				if (!synonymTextField.getText().equals("")) {
					// Remove a synonym, do not modify OWL file
					synonymListModel.removeElement(synonymTextField.getText());
					synonymTextField.setText("");
				}
			}
			else if (e.getSource()==getLabelFromOntologyButton) {
				IRI treeURI = currentEntity.getIRI();
				String labelUri = treeURI.getFragment();
				labelTextField.setText(labelUri);
				saveCurrentInformation();
			}
			else if (e.getSource()==languageSelectionBox) {
				JComboBox cb = (JComboBox)e.getSource();
				String lang = (String)cb.getSelectedItem();
				// Save current information
				saveCurrentInformation();
				// Clear all the fields
				clearAllFields();
				// Assign new language
				currentLanguage = lang;
				// Load fields from new languages
				currentAnnotations = loadFromModel(currentEntity, currentOntology, currentLanguage);
			}
			else
				System.out.println("Action NOT YET implemented !");
		}
	}

	//    private void ensureSelected(OWLEntity entity) {
	//        protegeView.getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
	//    }

	private class ArchonteListActionListener implements ListSelectionListener {

		ArchonteListActionListener() {}

		public void valueChanged(ListSelectionEvent e) {

			synonymTextField.setText((String)((JList)e.getSource()).getSelectedValue());
		}
	}
}

//How to modify the label in the tree.
//if (!labelTextField.getText().equals("")) {
//	OWLEntityRenamer renamer = new OWLEntityRenamer(
//			protegeView.getOWLModelManager().getOWLOntologyManager(),
//			protegeView.getOWLModelManager().getActiveOntologies());
//
//	String newLabel = labelTextField.getText();
//	// Save the fieds before rename. Otherwise, it makes strange things...
//	updateView(currentEntity);
//	// Make the change
//	try {
//		URI oldURI = currentEntity.getURI();
//		// Only change the fragment
//	    final URI newURI = new URI(oldURI.getScheme(), oldURI.getSchemeSpecificPart(), newLabel);
//		protegeView.getOWLModelManager().applyChanges(renamer.changeURI(currentEntity, newURI));
//        currentEntity.accept(new OWLEntityVisitor() {
//            public void visit(OWLClass cls) {
//                ensureSelected(protegeView.getOWLModelManager().getOWLDataFactory().getOWLClass(newURI));
//            }
//
//            public void visit(OWLObjectProperty property) {
//                ensureSelected(protegeView.getOWLModelManager().getOWLDataFactory().getOWLObjectProperty(newURI));
//            }
//
//            public void visit(OWLDataProperty property) {
//                ensureSelected(protegeView.getOWLModelManager().getOWLDataFactory().getOWLDataProperty(newURI));
//            }
//
//            public void visit(OWLIndividual individual) {
//                ensureSelected(protegeView.getOWLModelManager().getOWLDataFactory().getOWLIndividual(newURI));
//            }
//
//            public void visit(OWLDataType dataType) {
//            }
//        });
//	} catch (URISyntaxException e1) {
//		System.err.println("URI error");
//		e1.printStackTrace();
//	}					
//}
