/*
 * The Original Code is DOE (Differential Ontology Editor).
 *
 * The Initial Developer of the Original Code is INA.
 * Copyright (C) 2001. All Rights Reserved.
 *
 * DOE was developed by INA (http://www.ina.fr)
 *
 */

// Title:       EditPrincipleDialog
// Version:     1.0
// Copyright:   Copyright (c) 2001
// Authors:     Antoine Isaac & Raphael Troncy
// Company:     INA
// Description:

package fr.spim.archonte;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class EditPrincipleDialog extends JDialog implements ActionListener {

    /** To make Java happy */
	private static final long serialVersionUID = -6678721517459232411L;
	
	private int DIALOG_WIDTH = 350;
    private int DIALOG_HEIGHT = 230;
    private String principle = null;
    private String oldContent = null;
    private JTextArea answerArea = null;
    private JButton OKButton = null;
    private JButton cancelButton = null;
    
    private DifferentialGlobalView view = null;

    public EditPrincipleDialog(DifferentialGlobalView view, String p, String c) {
    	this.view = view;
        this.principle = p;
        this.oldContent = c;
        try {
            initialize();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {

        // Set value for this panel
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);
        this.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        if (principle.equals("SWP"))
            this.setTitle("Edit SWP principle");
        else if (principle.equals("SWS"))
            this.setTitle("Edit SWS principle");
        this.setResizable(false);
        this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);

        // Construct the components
        JLabel questionLabel = new JLabel();
        if (principle.equals("SWP"))
            questionLabel.setText("Edit 'Similarity With Parent' principle :");
        else if (principle.equals("SWS"))
            questionLabel.setText("Edit 'Similarity With Siblings' principle :");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = new Insets(10, 10, 0, 10);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(questionLabel, c);
        this.getContentPane().add(questionLabel);

        answerArea = new JTextArea();
        answerArea.setLineWrap(true);answerArea.setWrapStyleWord(true);
        answerArea.setText(oldContent);
        JScrollPane answerScrollPane = new JScrollPane(answerArea);
        answerScrollPane.setPreferredSize(new Dimension(350,75));
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.insets = new Insets(10, 10, 0, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(answerScrollPane, c);
        this.getContentPane().add(answerScrollPane);

        OKButton = new JButton("OK");
        OKButton.setMinimumSize(new Dimension(80, 30));
        OKButton.setPreferredSize(new Dimension(80, 30));
        OKButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weighty = 0.0;
        c.insets = new Insets(15, 10, 15, 10);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(OKButton, c);
        this.getContentPane().add(OKButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setMinimumSize(new Dimension(80, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.addActionListener(this);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(15, 10, 15, 10);
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(cancelButton, c);
        this.getContentPane().add(cancelButton);
    }

    // Overriden Method actionPerformed of ActionListener
    public void actionPerformed(ActionEvent e) {

        if (e.getSource()==OKButton)
            OKButton_actionPerformed();
        else if (e.getSource()==cancelButton)
            cancelButton_actionPerformed();
        else
            System.out.println("ERROR : buttons allowed are OK and Cancel !");
    }

    // OK Button action performed
    private void OKButton_actionPerformed() {

        view.confirmEditButton(principle, answerArea.getText());
        this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    // Cancel Button action performed
    private void cancelButton_actionPerformed() {

        this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

}
