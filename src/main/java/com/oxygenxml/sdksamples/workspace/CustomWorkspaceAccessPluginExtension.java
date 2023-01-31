package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;
import ro.sync.json.instance.generator.ui.JsonInstanceGeneratorDialog;

/**
 * Plugin extension - workspace access extension.
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
  /**
   * The custom messages area. A sample component added to your custom view.
   */
  private JTextArea customMessagesArea;

  /**
   * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
   */
  public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
	  //You can set or read global options.
	  //The "ro.sync.exml.options.APIAccessibleOptionTags" contains all accessible keys.
	  //		  pluginWorkspaceAccess.setGlobalObjectProperty("can.edit.read.only.files", Boolean.FALSE);
	  // Check In action

	  //You can access the content inside each opened WSEditor depending on the current editing page (Text/Grid or Author).  
	  // A sample action which will be mounted on the main menu, toolbar and contextual menu.
	final Action selectionSourceAction = createShowSelectionAction(pluginWorkspaceAccess);
	

	  // Create your own main menu and add it to Oxygen or remove one of Oxygen's menus...
	  pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
		  
		  /**
		   * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
		   */
		  public void customizeMainMenu(JMenuBar mainMenuBar) {
			  JMenu myMenu = new JMenu("MyMenu");
			  myMenu.add(selectionSourceAction);
			  // Add your menu before the Help menu
			  mainMenuBar.add(myMenu, mainMenuBar.getMenuCount() - 2);
		  }
	  });
	  
  }

	/**
	 * Create the Swing action which shows the current selection.
	 * 
	 * @param pluginWorkspaceAccess The plugin workspace access.
	 * @return The "Show Selection" action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createShowSelectionAction(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		return new AbstractAction("Generate sample JSON files") {
			public void actionPerformed(ActionEvent actionevent) {
				JsonInstanceGeneratorDialog dialog = new JsonInstanceGeneratorDialog();
		    	dialog.setDefaultCloseOperation(OKCancelDialog.DISPOSE_ON_CLOSE);
		    	dialog.setVisible(true);
		    	if (dialog.getResult() == OKCancelDialog.RESULT_OK) {
		    		dialog.createInstance();
		    	}
			}
		};
	}
  
  /**
   * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
   */
  public boolean applicationClosing() {
	  //You can reject the application closing here
    return true;
  }
}