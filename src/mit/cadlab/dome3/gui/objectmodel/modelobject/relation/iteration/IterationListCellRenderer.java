package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Nov 3, 2003
 * Time: 5:02:53 PM
 * To change this template use Options | File Templates.
 */
public class IterationListCellRenderer extends JLabel implements ListCellRenderer {
         public IterationListCellRenderer() {
             setOpaque(true);
         }
         public Component getListCellRendererComponent(
             JList list,
             Object value,
             int index,
             boolean isSelected,
             boolean cellHasFocus)
         {
             Color fontcolor=list.getForeground();
             if(value instanceof Parameter)
             {
                 setText(((Parameter)value).getName());
                 if(value instanceof ConcreteParameter) {
                     if(((ConcreteParameter)value).getScope() instanceof Relation){
                         if(!((Relation)((ConcreteParameter)value).getScope()).getItems(CausalityStatus.INDEPENDENT).contains(value))
                             fontcolor=Color.red;
                      }
                 }

              }


             setBackground(isSelected ? list.getSelectionBackground() :list.getBackground());
             setForeground(isSelected ? list.getSelectionForeground() :fontcolor);
             return this;
         }
     }

