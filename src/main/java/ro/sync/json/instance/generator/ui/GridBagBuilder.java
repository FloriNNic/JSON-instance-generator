package ro.sync.json.instance.generator.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;

public class GridBagBuilder {
    private GridBagConstraints lastConstraints = null;
    private GridBagConstraints middleConstraints = null;
    private GridBagConstraints labelConstraints = null;

    public GridBagBuilder() {

        lastConstraints = new GridBagConstraints();

        lastConstraints.fill = GridBagConstraints.HORIZONTAL;

        lastConstraints.anchor = GridBagConstraints.NORTHWEST;

        lastConstraints.weightx = 1.0;

        lastConstraints.gridwidth = GridBagConstraints.REMAINDER;

        lastConstraints.insets = new Insets(4, 4, 4, 4);

        middleConstraints = (GridBagConstraints) lastConstraints.clone();

        middleConstraints.gridwidth = GridBagConstraints.RELATIVE;

        labelConstraints = (GridBagConstraints) lastConstraints.clone();

        labelConstraints.weightx = 0.0;
        labelConstraints.gridwidth = 1;
    }

    /**
     * Adds a field component. Any component may be used. The 
     * component will be stretched to take the remainder of 
     * the current row.
     */
    public void addLastField(Component c, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        if (c instanceof JButton) {
        	lastConstraints.weightx = 0.0;
        } else {
        	lastConstraints.weightx = 1.0;
        }
        gbl.setConstraints(c, lastConstraints);
        parent.add(c);
    }
    
    public void addLabel(Component c, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(c, labelConstraints);
        parent.add(c);
    }
    
    public JLabel addLabel(String s, Container parent) {
        JLabel c = new JLabel(s);
        addLabel(c, parent);
        return c;
    }

    public void addMiddleField(Component c, Container parent) {
        GridBagLayout gbl = (GridBagLayout) parent.getLayout();
        gbl.setConstraints(c, middleConstraints);
        parent.add(c);
    }
    
}
