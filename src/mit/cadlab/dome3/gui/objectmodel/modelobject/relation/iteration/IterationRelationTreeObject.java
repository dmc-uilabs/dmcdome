// EqualRelationTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;

import javax.swing.*;

public class IterationRelationTreeObject extends DomeTreeObject
{

	public IterationRelationTreeObject(ConditionIterationRelation r)
	{
		super(r);
	}

	protected Icon getClosedIcon()
	{
        if(getDomeObject() instanceof ConditionIterationRelation) {
            ConditionIterationRelation relObj= (ConditionIterationRelation)getDomeObject();
            if(relObj.getIterationType().equals(IterationRelation.WHILE_LOOP)){
                return DomeIcons.getIcon(DomeIcons.RELATIONWHILE);
            }else if(relObj.getIterationType().equals(IterationRelation.DO_WHILE_LOOP)){
                return DomeIcons.getIcon(DomeIcons.RELATIONDO);
            }else if(relObj.getIterationType().equals(IterationRelation.Timestep_LOOP)){
                return DomeIcons.getIcon(DomeIcons.RELATIONTIME);
            }
        }
		return DomeIcons.getIcon(DomeIcons.RELATION);
	}

	protected Icon getOpenIcon()
	{
         if(getDomeObject() instanceof ConditionIterationRelation) {
            ConditionIterationRelation relObj= (ConditionIterationRelation)getDomeObject();
            if(relObj.getIterationType().equals(IterationRelation.WHILE_LOOP)){
                return DomeIcons.getIcon(DomeIcons.RELATIONWHILE_OPEN);
            }else if(relObj.getIterationType().equals(IterationRelation.DO_WHILE_LOOP)){
                return DomeIcons.getIcon(DomeIcons.RELATIONDO_OPEN);
            }else if(relObj.getIterationType().equals(IterationRelation.Timestep_LOOP)){
                return DomeIcons.getIcon(DomeIcons.RELATIONTIME_OPEN);
            }
        }
		return DomeIcons.getIcon(DomeIcons.RELATION_OPEN);
	}

}
