package cl.desarrollolibre.mm;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectLocator;
import biz.futureware.mantis.rpc.soap.client.MantisConnectPortType;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;
import freemind.modes.MindMapNode;

public class Mantis {
	public static final String MANTIS_URL_VIEW = "view.php?id=";
	public static final String MANTIS_CATEGORY_GENERAL = "General";
	
	private MantisConnectPortType connect;
	
	private String mantisUrl;
	private String mantisEndPoint;
	private String mantisUser;
	private String mantisPass;
	
	/**
	 * Create a Mantis issue from a Freemind child. 
	 * @param child
	 * @return BigInteger id of issue just created.
	 */
	public BigInteger createIssue(BigInteger projectId, MindMapNode child, BigInteger level, int currentLevel) {
		IssueData issue = new IssueData();
		issue.setSummary(child.getText());
		issue.setCategory(MANTIS_CATEGORY_GENERAL);
		
		String description = addChildLikeDescription(child, level, currentLevel);
		issue.setDescription(description);
		
		ObjectRef projectObjectRef = new ObjectRef(projectId, "");
		issue.setProject(projectObjectRef);
		
		try {
			issue.setId(connect.mc_issue_add(this.mantisUser, this.mantisPass, issue));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return issue.getId();
	}
	
	/**
	 * Iterates recursively over childs of MindMapNode to get their texts. 
	 * @param child
	 * @param level
	 * @param currentLevel
	 * @return
	 */
	private String addChildLikeDescription(MindMapNode child, BigInteger level, int currentLevel) {
		StringBuilder childDescription = new StringBuilder();
		if (child.hasChildren()) {
			for (Iterator j = child.childrenFolded(); j.hasNext();) {
				MindMapNode childNode = (MindMapNode) j.next();
				
				for (int countLevel = 1; countLevel < currentLevel; countLevel++) {
					childDescription.append(" > ");
				}
				childDescription.append(childNode.getPlainTextContent());
				childDescription.append("\r\r");				
				if (Integer.parseInt(String.valueOf(level)) > currentLevel) {
					childDescription.append(addChildLikeDescription(childNode, level, currentLevel+1));
				}
			}
		}
		else {
			childDescription.append(child.getText());
		}
		return childDescription.toString();
	}
	
	/**
	 * Starting point for access to SOAP Web Services and implements remote interface.
	 */
	public void createConnect(String pMantisUrl, String pMantisEndPoint) {
		this.mantisUrl = pMantisUrl;
		this.mantisEndPoint = pMantisEndPoint;
		MantisConnectLocator mcl = new MantisConnectLocator();
		mcl.setMantisConnectPortEndpointAddress(this.mantisUrl + this.mantisEndPoint);
		try {
			setConnect(mcl.getMantisConnectPort());
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public void destroyConnect() {
		setConnect(null);
	}

	public MantisConnectPortType getConnect() {
		return this.connect;
	}

	public void setConnect(MantisConnectPortType connect) {
		this.connect = connect;
	}

	public String getMantisUser() {
		return mantisUser;
	}

	public void setMantisUser(String mantisUser) {
		this.mantisUser = mantisUser;
	}

	public String getMantisPass() {
		return mantisPass;
	}

	public void setMantisPass(String mantisPass) {
		this.mantisPass = mantisPass;
	}
}
