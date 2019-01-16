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
  @Override
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
		  @Override
		  public void customizeMainMenu(JMenuBar mainMenuBar) {
			  JMenu myMenu = new JMenu("My menu");
			  myMenu.add(selectionSourceAction);
			  // Add your menu before the Help menu
			  mainMenuBar.add(myMenu, mainMenuBar.getMenuCount() - 1);
		  }
	  });


//	  pluginWorkspaceAccess.addEditorChangeListener();

//
//	  //You can use this callback to populate your custom toolbar (defined in the plugin.xml) or to modify an existing Oxygen toolbar 
//	  // (add components to it or remove them) 
//	  pluginWorkspaceAccess.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
//		  /**
//		   * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
//		   */
//		  public void customizeToolbar(ToolbarInfo toolbarInfo) {
//			  //The toolbar ID is defined in the "plugin.xml"
//			  if("SampleWorkspaceAccessToolbarID".equals(toolbarInfo.getToolbarID())) {
//				  List<JComponent> comps = new ArrayList<JComponent>(); 
//				  JComponent[] initialComponents = toolbarInfo.getComponents();
//				  boolean hasInitialComponents = initialComponents != null && initialComponents.length > 0; 
//				  if (hasInitialComponents) {
//					  // Add initial toolbar components
//					  for (JComponent toolbarItem : initialComponents) {
//						  comps.add(toolbarItem);
//					  }
//				  }
//				  
//				  //Add your own toolbar button using our "ro.sync.exml.workspace.api.standalone.ui.ToolbarButton" API component
//				  ToolbarButton customButton = new ToolbarButton(selectionSourceAction, true);
//				  comps.add(customButton);
//				  toolbarInfo.setComponents(comps.toArray(new JComponent[0]));
//			  } 
//		  }
//	  });

	  
  }

	/**
	 * Create the Swing action which shows the current selection.
	 * 
	 * @param pluginWorkspaceAccess The plugin workspace access.
	 * @return The "Show Selection" action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createShowSelectionAction(
			final StandalonePluginWorkspace pluginWorkspaceAccess) {
		return new AbstractAction("Generate sample JSON files") {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				OKCancelDialog dialog = new JsonInstanceGeneratorDialog();
				dialog.setDefaultCloseOperation(OKCancelDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		};
	}
  
  /**
   * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
   */
  @Override
  public boolean applicationClosing() {
	  //You can reject the application closing here
    return true;
  }
}