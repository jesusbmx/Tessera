package tessera.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Campo de texto con menu desplegable de opciones marcables; el texto es la lista separada por comas.
 */
public class PopupMenuExtension implements ActionListener, DocumentListener, MouseListener {
    
    private final JTextField textField;
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final String delimit = ",";
    
    private final List<String> chips = new ArrayList<>();
    
    public PopupMenuExtension(JTextField textField) {
        this.textField = textField;
        
        this.textField.getDocument().addDocumentListener(this);
        
        this.textField.addMouseListener(this);
        
        this.textField.putClientProperty("JTextField.leadingIcon",
                new FlatSVGIcon("tessera/img/arrow_down.svg", 16, 16));
        
                
        this.popupMenu.setFocusable(false);
        
        updatePopupMenu();
    }
    
    private void updatePopupMenu() {
        popupMenu.removeAll();
        for (String chip : chips) {
            final JCheckBoxMenuItem menuItemZpl = new JCheckBoxMenuItem(chip);
            menuItemZpl.addActionListener(this);
            popupMenu.add(menuItemZpl);
        }
    }
    
    public List<String> getChips() {
        return chips;
    }

    public void setChips(List<String> chipsList) {
        this.chips.clear();
        this.chips.addAll(chipsList);
        updatePopupMenu();
    }
    
    public void setChips(String chipsString) {
        String[] split = chipsString.split(this.delimit);
        setChips(new java.util.ArrayList<>(java.util.Arrays.asList(split)));
    }

    void updateComponent() {
        final String text = textField.getText().toLowerCase();
        
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            final Component component = popupMenu.getComponent(i);
            
            if (component instanceof JCheckBoxMenuItem) {
                final JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) component;
                
                if (text.contains(menuItem.getText())) {
                    menuItem.setSelected(true);
                } else {
                    menuItem.setSelected(false);
                }
                
                menuItem.updateUI();
            }
        }
        
        popupMenu.revalidate();
        popupMenu.repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        final String text = textField.getText().toLowerCase();
        
        Set<String> extensions = Arrays.stream(text.split(delimit))
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        
        final JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
        final String extension = menuItem.getText();
        
        if (menuItem.isSelected()) {
            extensions.add(extension);
        } else {
            extensions.remove(extension);
        }
        
        textField.setText(String.join(delimit, extensions));
        //closePopup();
    }

    
    public void showPopup() {
        if (textField.isDisplayable() && textField.isShowing()) {
            updateComponent();
            
            Point location = textField.getLocationOnScreen();
            int x = location.x;
            int y = location.y + textField.getSize().height;
            popupMenu.setLocation(x, y);
            popupMenu.setInvoker(textField);
            popupMenu.setVisible(Boolean.TRUE);
        }
    }
    
    public void closePopup() { popupMenu.setVisible(Boolean.FALSE); }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateComponent();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateComponent();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateComponent();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        showPopup();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopup();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
