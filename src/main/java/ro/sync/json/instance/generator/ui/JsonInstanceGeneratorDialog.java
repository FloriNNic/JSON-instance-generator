package ro.sync.json.instance.generator.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;
import ro.sync.json.instance.generator.JsonGeneratorOptions;
import ro.sync.json.instance.generator.JsonInstanceGenerator;
import ro.sync.ui.Icons;

@SuppressWarnings("serial")
public class JsonInstanceGeneratorDialog extends OKCancelDialog {
	JTextField inputTextField   = new JTextField();
	JTextField outputTextField  = new JTextField();
	JTextField fileNameField    = new JTextField();
	JTextField recursivityField = new JTextField();
	JTextField instancesField   = new JTextField();
	JTextField repetitionsField = new JTextField();
	JCheckBox instanceOpen = new JCheckBox("Open first instance in editor");
    JCheckBox optionalProp = new JCheckBox("Generate optional properties");
    String[] choices = {"Random","Minimum"};
    JComboBox<Object> choiceStrategyList = new JComboBox<Object>(choices);
    String uploadSystemID;
    String downloadDirectory;
    JsonGeneratorOptions jsonGeneratorOptions = new JsonGeneratorOptions();
    
    public JsonInstanceGeneratorDialog() {
    	super(null, "JSON Instance Generator", true);
    	setResizable(true);
    	setMinimumSize(new Dimension(600, 400));
    	JPanel contentPanel = createPanel();
    	getContentPane().add(contentPanel);
    }

	public void setUploadSystemID(String uploadSystemID) {
		this.uploadSystemID = uploadSystemID;
	}

	public void setDownloadDirectory(String downloadDirectory) {
		this.downloadDirectory = downloadDirectory;
	}
	
	public void selectFile(String use, JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        if (use == "input") {
        	chooser.setDialogTitle("Select a schema");
        	chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json");
    		chooser.addChoosableFileFilter(filter);
        } else if (use == "output") {
        	chooser.setDialogTitle("Choose a directory to save your file: ");
    		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            textField.setText(selectedFile.toURI().toString());
            if (use == "input") {
            	setUploadSystemID(selectedFile.toString());
            } else if (use == "output") {
            	setDownloadDirectory(selectedFile.toString());
            }
            
        } else {
            // user changed their mind
        }
    }
	
	public void addImageOnJButton (JButton button) {
		URL imageToLoad = getClass().getClassLoader().getResource("browse.png");
        ImageIcon icon = null;
    	if (imageToLoad != null) {
    		icon =  Icons.getIcon(imageToLoad.toString());
    	}
		button.setPreferredSize(new Dimension(20, 20));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setIcon(icon);
	}
	
	
    public JPanel createPanel() {
        //JFrame f = new JFrame("Generate Sample JSON Files");
        JPanel form = new JPanel();
        //f.getContentPane().setLayout(new BorderLayout());
        //f.getContentPane().add(form, BorderLayout.NORTH);

        form.setLayout(new GridBagLayout());
        GridBagBuilder grid = new GridBagBuilder();

        grid.addLabel("URL: ", form);
        grid.addLabel(inputTextField, form);
        
        JButton inputButton = new JButton();
        addImageOnJButton(inputButton);
        grid.addLastField(inputButton, form);
        inputButton.addActionListener(e -> {
			selectFile("input", inputTextField);
        });
        
        grid.addLabel("Output folder: ", form);
        grid.addMiddleField(outputTextField, form);
        
        JButton outputButton = new JButton();
        addImageOnJButton(outputButton);
        grid.addLastField(outputButton, form);
        outputButton.addActionListener(e -> {
        	selectFile("output",outputTextField);
        });
        
        JLabel fileNameLabel = new JLabel();
        fileNameLabel.setText("File name: ");
        grid.addLabel(fileNameLabel, form);
        fileNameField.setPreferredSize( new Dimension( 60, 20 ));
        fileNameField.setText("instance.json");
        grid.addLastField(fileNameField, form);

        grid.addLabel("Number of instances: ", form);
        instancesField.setText("1");
        grid.addLastField(instancesField, form);
        
        grid.addLabel("Number of repetitions: ", form);
        repetitionsField.setText("1");
        grid.addLastField(repetitionsField, form);
        
        grid.addLabel("Maximum recursivity level: ", form);
        recursivityField.setText("1");
        grid.addLastField(recursivityField, form);
        
        grid.addLabel("Choice strategy: ", form);
        choiceStrategyList.setSelectedIndex(0);
        choiceStrategyList.setPreferredSize(new Dimension(10, 20));
        grid.addLastField(choiceStrategyList, form);
        
        optionalProp.setMnemonic(KeyEvent.VK_C); 
        optionalProp.setSelected(true);
        grid.addLastField(optionalProp, form);
        
        instanceOpen.setMnemonic(KeyEvent.VK_C); 
        instanceOpen.setSelected(true);
        grid.addLastField(instanceOpen, form);
        
        form.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        return form;
    }
    
    public JsonGeneratorOptions getGeneratorOptions() {
    	jsonGeneratorOptions.setGenerateRandomValues(choiceStrategyList.getSelectedItem().toString() == "Random" ? true : false);
    	jsonGeneratorOptions.setDownloadDirectory(downloadDirectory);
    	jsonGeneratorOptions.setUploadSystemID(uploadSystemID);
    	return jsonGeneratorOptions;
    }
    
    public void createInstance() {
    	getGeneratorOptions();
    	String fileNameText = fileNameField.getText();
    	int numberOfInstances = Integer.parseInt(instancesField.getText());
    	String fileName = downloadDirectory + "/" + fileNameText;
    	System.out.println("File created");
    	if (numberOfInstances > 1) {
    		String filePrefix = fileNameText.substring(0, fileNameText.lastIndexOf(".")); // get prefix of file
        	String fileExtension = fileNameText.substring(fileNameText.lastIndexOf(".")); // get the extension of file
    		for (int i = 1; i <= numberOfInstances; i++ ) {
      		  // the final filename is obtained by appending to directory path the file prefix + the index of instance + the extension
  	    	  fileName = downloadDirectory + "/" + filePrefix + Integer.toString(i) + fileExtension;
  	    	  writeInFile(fileName);
    		}
    	} else {
    		writeInFile(fileName);
    	}
    }
    
    public void writeInFile(String fileName){
    	System.out.println("File name: " + fileName);
    	String generated = null;
		try {
			generated = JsonInstanceGenerator.generate(getGeneratorOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	PrintWriter writer = null;
    	try {
    		writer = new PrintWriter(new FileWriter(fileName, true));
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}
    	writer.write(generated);
    	writer.close();
    }
    
    public static void main(String[] args) throws Exception {
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	JsonInstanceGeneratorDialog dialog = new JsonInstanceGeneratorDialog();
    	dialog.setDefaultCloseOperation(OKCancelDialog.DISPOSE_ON_CLOSE);
    	dialog.setVisible(true);
    	if (dialog.getResult() == RESULT_OK) {
    		dialog.createInstance();
    	}
      
    }

}