package org.cytoscape.io.ndex.internal;


import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class NdexSignInDialog extends JDialog
	implements ActionListener {

	public NdexSignInDialog(Frame aFrame) {
		super(aFrame, "Connect to an NDEx Server", true);

		initControls();
		pack();
		setModal(true);
		setResizable(false);
		setLocationRelativeTo(aFrame);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnCancel) {
			// Cancel button pressed -> close the window
			this.setVisible(false);
			this.dispose();
			
		} else if (source == btnSignIn) {
			// SignIn button pressed -> authenticate credentials with selected NDEx Server
			
			if (safeSetAndCheckCredential()){
				// valid credentials and successful sign in
				this.setVisible(false);
				this.dispose();
			} else {
				// failure, try again
				// for now, doing nothing, but need to prompt user...
			}
		}
	}
	
	private boolean safeSetAndCheckCredential(){
		try {
			NdexInterface.INSTANCE.setCredential(useridField.getText(), passwordField.getText());
			return NdexInterface.INSTANCE.checkCredential();
		} catch (Exception e) {
			return false;
		}
	}
			


	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -6256113953155955101L;


	/**
	 * Creates and lays out the controls inside this dialog's content pane.
	 * <p>
	 * This method is called upon initialization only.
	 * </p>
	 */
	private void initControls() {
		// NDEx Server Selector
		// (For now, default to local host)

		JPanel authorizationTextPanel = new JPanel();
		FlowLayout fl_authorizationTextPanel = (FlowLayout) authorizationTextPanel.getLayout();
		fl_authorizationTextPanel.setAlignment(FlowLayout.LEFT);
		add(authorizationTextPanel);
		
		JLabel lblAuthorization = new JLabel("Sign In to NDEx");
		lblAuthorization.setHorizontalAlignment(SwingConstants.LEFT);
		lblAuthorization.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 15));
		authorizationTextPanel.add(lblAuthorization);
		
		JPanel authorizationPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) authorizationPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(authorizationPanel);
		
		JPanel idAndPassPanel = new JPanel();
		authorizationPanel.add(idAndPassPanel);
		idAndPassPanel.setLayout(new BoxLayout(idAndPassPanel, BoxLayout.Y_AXIS));
		
		JPanel idPanel = new JPanel();
		FlowLayout fl_idPanel = (FlowLayout) idPanel.getLayout();
		fl_idPanel.setAlignment(FlowLayout.RIGHT);
		idAndPassPanel.add(idPanel);
		
		JLabel lblUserId = new JLabel("User ID");
		idPanel.add(lblUserId);
		
		useridField = new JTextField();
		idPanel.add(useridField);
		useridField.setColumns(10);
		
		JPanel passwordPanel = new JPanel();
		idAndPassPanel.add(passwordPanel);
		passwordPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JLabel lblPassword = new JLabel("Password");
		passwordPanel.add(lblPassword);
		
		passwordField = new JTextField();
		passwordPanel.add(passwordField);
		passwordField.setColumns(10);
		
		btnSignIn = new JButton("Sign In");
		authorizationPanel.add(btnSignIn);
		
		btnCancel = new JButton("Cancel");
		authorizationPanel.add(btnCancel);

		// Add Sign In and Cancel buttons
		/*
		JPanel buttons = new JPanel(new GridLayout(1, 2, 4, 0));
		buttons.add(btnSignIn = Utils.createButton("Sign In", null, this));
		buttons.add(btnCancel = Utils.createButton("Cancel", null, this));
		Box buttonsBox = Box.createHorizontalBox();
		buttonsBox.add(Box.createHorizontalGlue());
		buttonsBox.add(buttons);
		buttonsBox.add(Box.createHorizontalGlue());
	
		Container contentPane = getContentPane();
		contentPane.add(inputPanel, BorderLayout.NORTH);
		contentPane.add(buttonsBox, BorderLayout.PAGE_END);
		
		*/
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}


	private JButton btnSignIn;
	private JButton btnCancel;
	private JTextField useridField;
	private JTextField passwordField;
	


}
