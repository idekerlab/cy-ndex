package org.cytoscape.io.ndex.internal.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.io.ndex.internal.rest.NdexRestClient;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

public class NdexSearchPanel extends JPanel {

	// NdexWebServiceClient ndexClient;
	NdexRestClient restClient;
	TaskManager<?, ?> taskManager;
	CyNetworkManager manager;
	CyNetworkViewFactory viewFactory;
	CyNetworkViewManager viewManager;
	private JTextField searchField;
	private JTable resultTable;
	private DefaultTableModel tableModel;
	private JTextField useridField;
	private JTextField passwordField;

	public NdexSearchPanel() {
		super();
		createUI();

	}
//new NdexSearchPanel(taskManager,cyNetworkManagerServiceRef,cyNetworkViewFactoryServiceRef,cyNetworkViewManagerServiceRef);

	public NdexSearchPanel(TaskManager<?, ?> taskManager,CyNetworkManager manager,CyNetworkViewFactory viewFactory,CyNetworkViewManager viewManager) {
		super();
		// this.ndexClient = ndexClient;
		// this.restClient = restClient;
		this.manager = manager;
		this.viewFactory = viewFactory;
		this.viewManager = viewManager;
		this.taskManager = taskManager;

		createUI();
	}

	private void createUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.white);
		setBorder(null);
		
		createAuthorizationPanel();

		createSearchPanel();

		createResultPanel();

		createButtonPanel();
	}

	private void createSearchPanel() {
		
		
		
		JPanel searchTextPanel = new JPanel();
		FlowLayout fl_searchTextPanel = (FlowLayout) searchTextPanel
				.getLayout();
		fl_searchTextPanel.setAlignment(FlowLayout.LEFT);
		add(searchTextPanel);

		JLabel lblSearchNetworksFrom = new JLabel("Search networks from NDEx");
		lblSearchNetworksFrom.setFont(new Font("Microsoft Sans Serif",
				Font.BOLD, 15));
		lblSearchNetworksFrom.setHorizontalAlignment(SwingConstants.LEFT);
		searchTextPanel.add(lblSearchNetworksFrom);

		JPanel searchPanel = new JPanel();
		add(searchPanel);

		searchField = new JTextField();
		searchPanel.add(searchField);
		searchField.setColumns(20);

		JButton searchButton = new JButton("Search\r\n");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				searchNetwork();
			}
		});
		searchPanel.add(searchButton);
	}

	private void createAuthorizationPanel() {
		JPanel authorizationTextPanel = new JPanel();
		FlowLayout fl_authorizationTextPanel = (FlowLayout) authorizationTextPanel.getLayout();
		fl_authorizationTextPanel.setAlignment(FlowLayout.LEFT);
		add(authorizationTextPanel);
		
		JLabel lblAuthorization = new JLabel("Authorization");
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
		
		JButton authorizationButton = new JButton("Login");
		authorizationPanel.add(authorizationButton);
	}

	private void createResultPanel() {
		JPanel resultTextPanel = new JPanel();
		FlowLayout fl_resultTextPanel = (FlowLayout) resultTextPanel
				.getLayout();
		fl_resultTextPanel.setAlignment(FlowLayout.LEFT);
		add(resultTextPanel);

		JLabel lblSearchResult = new JLabel("Search result");
		lblSearchResult
				.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 15));
		resultTextPanel.add(lblSearchResult);

		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(resultPanel);
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));

		tableModel = new DefaultTableModel();
		tableModel.addColumn("Network ID");
		tableModel.addColumn("Network Name");
		resultTable = new JTable(tableModel){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String[] test = { "", "" };
		tableModel.addRow(test);
		resultTable.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		JScrollPane scrollPane = new JScrollPane(resultTable);
		scrollPane.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		resultPanel.add(scrollPane);

		JButton importButton = new JButton("Import");
		importButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				importNetwork();
			}
		});
		resultPanel.add(importButton);
	}

	private void createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		add(buttonPanel);

		JButton closeButton = new JButton("Close");
		buttonPanel.add(closeButton);
	}

	public void searchNetwork() {
		taskManager.execute(new TaskIterator(new SearchNetworkTask()));
	}

	public void importNetwork() {
		taskManager.execute(new TaskIterator(new ImportNetworkTask()));
	}

	public void setRestClient(NdexRestClient restClient) {
		this.restClient = restClient;
		
	}

	private final class SearchNetworkTask extends AbstractTask {
		@Override
		public void run(TaskMonitor taskMonitor) throws Exception {
			// TODO add authorization
			restClient.setCredential("dexterpratt", "insecure");
			String searchString = searchField.getText().toUpperCase();
			List<String> result = new ArrayList<String>(
					restClient.findNetworks(searchString));
			System.out.println(result);

			tableModel.setRowCount(0);
			for (String res : result) {
				//TODO change split character to better string
				String[] line = res.split(",");
				tableModel.addRow(line);
			}
		}
	}
	
	private final class ImportNetworkTask extends AbstractTask{
		@Override
		public void run(TaskMonitor taskMonitor) throws Exception {
			// TODO Auto-generated method stub
			restClient.setCredential("dexterpratt", "insecure");
			if(resultTable.getSelectedRow()>=0){
				//TODO extract column number
				String ndexNetworkId = (String)resultTable.getValueAt(resultTable.getSelectedRow(), 0);
			CyNetwork network = restClient.getNetwork(ndexNetworkId);
			manager.addNetwork(network);
			System.out.println("network node count is "+network.getNodeCount());
			CyNetworkView view = viewFactory.createNetworkView(network);
			viewManager.addNetworkView(view);
			}
		}
	}

}
