// UnborderedTextField.java
package mit.cadlab.dome.swing;

import javax.swing.JTextField;
import javax.swing.border.Border;

public class UnborderedTextField extends JTextField {
  
  public UnborderedTextField() {
    super();
  }
  
  public UnborderedTextField(int columns) {
    super(columns);
  }
  
  public UnborderedTextField(String text) {
    super(text);
  }
  
  public UnborderedTextField(String text, int columns) {
    super(text,columns);
  }
  
  public void setBorder(Border b) {}
  
}
