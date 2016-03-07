package cl.desarrollolibre.mm;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import accessories.plugins.util.window.WindowClosingAdapter;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class MantisExportDialog extends JFrame {
	
	private static final String PLUGINS_MANTIS_DIALOG_STORE_URL = "plugins.Mantis.ExportDialog.store.url"; 
	private static final String PLUGINS_MANTIS_DIALOG_STORE_ENDPOINT = "plugins.Mantis.ExportDialog.store.endpoint"; 
	private static final String PLUGINS_MANTIS_DIALOG_STORE_USER = "plugins.Mantis.ExportDialog.store.user"; 
	private static final String PLUGINS_MANTIS_DIALOG_STORE_PASSWORD = "plugins.Mantis.ExportDialog.store.password"; 
	
	protected JTextField fieldMantisUrl = null;
	protected JTextField fieldMantisEndPoint = null;
	protected JTextField fieldMantisUser = null;
	protected JPasswordField fieldMantisPass = null;
	protected JLabel labelConnectResult = null;
	protected JLabel labelProject = null;
	protected JLabel labelDepthLevel = null;
	protected JComboBox<ComboItem> listProjects = null;
	protected JComboBox<ComboItem> listDepthLevels = null;
	protected JLabel labelExportFrom = null;
	protected ButtonGroup groupExportFrom = null;
	protected JRadioButton radioExportFromRootNode = null;
	protected JRadioButton radioExportFromSelectedNode = null;
	protected JButton buttonTest = null;
	protected JButton buttonExport = null;

	private final ModeController mController;
	private Mantis mantis;

	class ExportListener implements ActionListener {
		private MantisExportDialog parent = null;
		boolean exitSystem = true;
		private boolean cancel = false;

		public ExportListener(MantisExportDialog m) {
			parent = m;
		}

		public ExportListener(MantisExportDialog m, boolean pCancel) {
			parent = m;
			cancel = pCancel;
		}

		public void actionPerformed(ActionEvent e) {
			if (!cancel) {
				MindMap model = parent.mController.getMap();
				
				parent.mantis.createConnect(parent.fieldMantisUrl.getText(), parent.fieldMantisEndPoint.getText());
				parent.mantis.setMantisUser(parent.fieldMantisUser.getText());
				parent.mantis.setMantisPass(String.valueOf(parent.fieldMantisPass.getPassword()));
				
				ComboItem selectedProject = (ComboItem) parent.listProjects.getSelectedItem();
				ComboItem selectedDepthLevel = (ComboItem) parent.listDepthLevels.getSelectedItem();
				
				MindMapNode nodeToExport = null;
				
				if (parent.groupExportFrom.getSelection().getActionCommand().equals("selected")) {
					nodeToExport = parent.mController.getSelected();
				}
				else {
					nodeToExport = model.getRootNode();
				}
				
				for (Iterator i = nodeToExport.childrenFolded(); i.hasNext();) {
					MindMapNode child = (MindMapNode) i.next();
					BigInteger issueId = parent.mantis.createIssue(selectedProject.value, child, selectedDepthLevel.value , 1);
					
					child.setLink(parent.fieldMantisUrl.getText() + Mantis.MANTIS_URL_VIEW + issueId.toString());
				}
				
				parent.mantis.destroyConnect();
			}

			parent.setVisible(false);
			parent.dispose();
		}

	}

	class TestConnectionListener implements ActionListener {
		private MantisExportDialog parent = null;
		boolean exitSystem = true;

		public TestConnectionListener(MantisExportDialog m) {
			parent = m;
		}

		public void actionPerformed(ActionEvent e) {
		
			parent.labelConnectResult.setText("Testing...");

			parent.mantis.createConnect(parent.fieldMantisUrl.getText(), parent.fieldMantisEndPoint.getText());
			
			ProjectData[] userProjects = null;
			try {
				char[] charPassword = parent.fieldMantisPass.getPassword();
				userProjects = parent.mantis.getConnect().mc_projects_get_user_accessible(parent.fieldMantisUser.getText(), String.valueOf(charPassword));
				
				mController.getFrame().setProperty(PLUGINS_MANTIS_DIALOG_STORE_URL, parent.fieldMantisUrl.getText());
				mController.getFrame().setProperty(PLUGINS_MANTIS_DIALOG_STORE_ENDPOINT, parent.fieldMantisEndPoint.getText());
				mController.getFrame().setProperty(PLUGINS_MANTIS_DIALOG_STORE_USER, parent.fieldMantisUser.getText());
								
				parent.labelProject.setEnabled(true);
				parent.listProjects.setEnabled(true);
				parent.labelDepthLevel.setEnabled(true);
				parent.listDepthLevels.setEnabled(true);
				parent.labelExportFrom.setEnabled(true);
				parent.radioExportFromRootNode.setEnabled(true);
				parent.radioExportFromSelectedNode.setEnabled(true);
				parent.buttonExport.setEnabled(true);
				parent.buttonTest.setEnabled(false);
				
			} catch (RemoteException e1) {
				parent.labelConnectResult.setForeground(Color.red);
				parent.labelConnectResult.setText("Error: " + e1.getMessage());
			}
			
			if (userProjects != null) {
				parent.labelConnectResult.setForeground(Color.green);
				parent.labelConnectResult.setText("Connection successfully.");
				
				parent.labelProject.setVisible(true);
				parent.listProjects.setVisible(true);
				eachProjects(userProjects, 1);
			}
			
			parent.mantis.destroyConnect();
		}
		
		private void eachProjects(ProjectData[] projects, int level) {
			StringBuilder str = new StringBuilder();
			for (int i = 1; i < level; ++i) {
			    str.append(" » ");
			}
			for (ProjectData projectData : projects) {
				ComboItem item = new ComboItem(projectData.getId(), str.toString() + projectData.getName());
				parent.listProjects.addItem(item);
				
				ProjectData[] subProjects = projectData.getSubprojects();
				if (subProjects.length > 0) {
					eachProjects(subProjects, level + 1);
				}
			}
		}
	}

	public MantisExportDialog(ModeController pController, Mantis pMantis) {

		super("Export to Mantis Bug Tracker"); 
		this.mController = pController;
		this.mantis = pMantis;

		String prefUrl = mController.getFrame().getProperty(PLUGINS_MANTIS_DIALOG_STORE_URL);
		String prefEndPoint= mController.getFrame().getProperty(PLUGINS_MANTIS_DIALOG_STORE_ENDPOINT);
		String prefUser = mController.getFrame().getProperty(PLUGINS_MANTIS_DIALOG_STORE_USER);

		setBackground(Color.lightGray);
		this.addWindowListener(new WindowClosingAdapter(false));
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc;
		getContentPane().setLayout(gbl);

		gbc = makegbc(0, 0, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		JLabel labelUser = new JLabel("User"); 
		gbl.setConstraints(labelUser, gbc);
		getContentPane().add(labelUser);

		gbc = makegbc(1, 0, 1, 1);
		gbc.weightx = 300;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldMantisUser = new JTextField(prefUser);
		fieldMantisUser.setColumns(20);
		gbl.setConstraints(fieldMantisUser, gbc);
		getContentPane().add(fieldMantisUser);

		gbc = makegbc(0, 1, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		JLabel labelPassword = new JLabel("Password"); 
		gbl.setConstraints(labelPassword, gbc);
		getContentPane().add(labelPassword);

		gbc = makegbc(1, 1, 1, 1);
		gbc.weightx = 100;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldMantisPass = new JPasswordField();
		fieldMantisPass.setColumns(20);
		gbl.setConstraints(fieldMantisPass, gbc);
		getContentPane().add(fieldMantisPass);
		
		gbc = makegbc(0, 2, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		JLabel labelUrl = new JLabel("Url Mantis"); 
		gbl.setConstraints(labelUrl, gbc);
		getContentPane().add(labelUrl);

		gbc = makegbc(1, 2, 1, 1);
		gbc.weightx = 100;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldMantisUrl = new JTextField(prefUrl != null && !prefUrl.equals("") ? prefUrl : "http://localhost/mantis/");
		fieldMantisUrl.setColumns(20);
		gbl.setConstraints(fieldMantisUrl, gbc);
		getContentPane().add(fieldMantisUrl);
		
		gbc = makegbc(0, 3, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		JLabel labelEndPoint = new JLabel("WSDL"); 
		gbl.setConstraints(labelEndPoint, gbc);
		getContentPane().add(labelEndPoint);

		gbc = makegbc(1, 3, 1, 1);
		gbc.weightx = 100;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldMantisEndPoint = new JTextField(prefEndPoint != null && !prefEndPoint.equals("") ? prefEndPoint : "api/soap/mantisconnect.php");
		fieldMantisEndPoint.setColumns(20);
		gbl.setConstraints(fieldMantisEndPoint, gbc);
		getContentPane().add(fieldMantisEndPoint);
		
		buttonTest = new JButton("Test connection"); 
		gbc = makegbc(0, 4, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		buttonTest.addActionListener(new TestConnectionListener(this));
		gbl.setConstraints(buttonTest, gbc);
		getContentPane().add(buttonTest);
		gbc = makegbc(1, 4, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		labelConnectResult = new JLabel(); 
		gbl.setConstraints(labelConnectResult, gbc);
		getContentPane().add(labelConnectResult);
		
		gbc = makegbc(0, 5, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		labelProject = new JLabel("Select project"); 
		labelProject.setEnabled(false);
		gbl.setConstraints(labelProject, gbc);
		getContentPane().add(labelProject);
		
		listProjects = new JComboBox();
		gbc = makegbc(1, 5, 1, 1);
		gbc.fill =
		GridBagConstraints.BOTH;
		listProjects.setEnabled(false);
		gbl.setConstraints(listProjects, gbc);
		getContentPane().add(listProjects);
		 
		gbc = makegbc(0, 6, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		labelExportFrom = new JLabel("Node to export?"); 
		labelExportFrom.setEnabled(false);
		gbl.setConstraints(labelExportFrom, gbc);
		getContentPane().add(labelExportFrom);

		gbc = makegbc(1, 6, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		radioExportFromRootNode = new JRadioButton("Root node");
		radioExportFromRootNode.setActionCommand("root");
		radioExportFromRootNode.setSelected(true);
		radioExportFromRootNode.setEnabled(false);
		gbl.setConstraints(radioExportFromRootNode, gbc);
		getContentPane().add(radioExportFromRootNode);
		
		gbc = makegbc(2, 6, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		radioExportFromSelectedNode = new JRadioButton("Selected node");
		radioExportFromSelectedNode.setActionCommand("selected");
		radioExportFromSelectedNode.setEnabled(false);
		gbl.setConstraints(radioExportFromSelectedNode, gbc);
		getContentPane().add(radioExportFromSelectedNode);
		
		groupExportFrom = new ButtonGroup();
		groupExportFrom.add(radioExportFromRootNode);
		groupExportFrom.add(radioExportFromSelectedNode);
		
		gbc = makegbc(0, 7, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		labelDepthLevel = new JLabel("Depth level"); 
		labelDepthLevel.setEnabled(false);
		gbl.setConstraints(labelDepthLevel, gbc);
		getContentPane().add(labelDepthLevel);
		
		listDepthLevels = new JComboBox();
		gbc = makegbc(1, 7, 1, 1);
		gbc.fill =
		GridBagConstraints.BOTH;
		listDepthLevels.setEnabled(false);
		gbl.setConstraints(listDepthLevels, gbc);
		getContentPane().add(listDepthLevels);
		
		ComboItem item;
		for (int level = 1; level <= 10; level++) {
			item = new ComboItem(new BigInteger(String.valueOf(level)), String.valueOf(level));
			listDepthLevels.addItem(item);
		}

		buttonExport = new JButton("Export"); 
		buttonExport.setEnabled(false);
		gbc = makegbc(2, 8, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		buttonExport.addActionListener(new ExportListener(this));
		gbl.setConstraints(buttonExport, gbc);
		getContentPane().add(buttonExport);

		JButton cbutton = new JButton("Cancel"); 
		gbc = makegbc(1, 8, 1, 1);
		gbc.anchor = gbc.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(cbutton, gbc);
		cbutton.addActionListener(new ExportListener(this, true));
		getContentPane().add(cbutton);
		pack();
	}

	private GridBagConstraints makegbc(int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.insets = new Insets(1, 1, 1, 1);
		gbc.anchor = gbc.WEST;
		return gbc;
	}

	public class ComboItem {
	    private BigInteger value;
	    private String label;

	    public ComboItem(BigInteger value, String label) {
	        this.value = value;
	        this.label = label;
	    }

	    public BigInteger getValue() {
	        return this.value;
	    }

	    public String getLabel() {
	        return this.label;
	    }

	    @Override
	    public String toString() {
	        return label;
	    }
	}
}
