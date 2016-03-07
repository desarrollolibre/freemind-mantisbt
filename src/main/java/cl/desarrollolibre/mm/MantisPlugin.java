package cl.desarrollolibre.mm;

import freemind.extensions.ExportHook;

public class MantisPlugin extends ExportHook {

	public MantisPlugin() {
		super();
		
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}
	
	public void startupMapHook() {
		super.startupMapHook();
		export();

	}

	private void export() {
		Mantis mantis = new Mantis();
		MantisExportDialog exp = new MantisExportDialog(getController(), mantis);
		exp.setVisible(true);
	}

}
