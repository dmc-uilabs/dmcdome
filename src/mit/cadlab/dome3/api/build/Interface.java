package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.util.DomeException;
import mit.cadlab.dome3.plugin.PluginModelBuilder;

import java.util.*;

public class Interface {
    protected ModelInterfaceBuilder interfaceBuilder;

    public Interface(ModelInterfaceBuilder interfaceBuilder) {
        this.interfaceBuilder = interfaceBuilder;
    }

    public Interface(ModelInterfaceBuilder interfaceBuilder, String name) {
        this.interfaceBuilder = interfaceBuilder;
        setName(name);
    }

    public String getName() {
        return interfaceBuilder.getName();
    }

    public void setName(String name) {
        interfaceBuilder.setName(name);
    }

    public List getInputParameters() {
        return(Parameter.wrapParameterList(interfaceBuilder.getCausalityManager().getIndependents()));
    }

    public List getOutputParameters() {
        return (Parameter.wrapParameterList(interfaceBuilder.getCausalityManager().getOutputs()));
    }

    public List getIndeterminateParameters() {
        return (Parameter.wrapParameterList(interfaceBuilder.getCausalityManager().getIndeterminates()));
    }

    public List getIntermediateParameters() {
        return (Parameter.wrapParameterList(interfaceBuilder.getCausalityManager().getIntermediates()));
    }

    public List getResultParameters() {
        return (Parameter.wrapParameterList(interfaceBuilder.getCausalityManager().getResults()));
    }

    public Parameter addAndMapParameter(Parameter modelParameter) {
        if (interfaceBuilder.isDefaultInterface())
            throw new DomeException("The default interface cannot be modified.");
        else {
            ArrayList list = new ArrayList();
            list.add(modelParameter.getConcreteParameter());
            ArrayList c = (ArrayList)  interfaceBuilder.addAndMapModelObjects(list);
            interfaceBuilder.getBuildContext().addModelObjectReferences(c);

            ConcreteParameter ifaceParam = (ConcreteParameter) c.get(0);
            return Parameter.wrapParameter(ifaceParam);
        }
    }

    public Parameter addAndMapParameter(Parameter modelParameter, String ifaceParamName) {
        Parameter p = addAndMapParameter(modelParameter);
        p.setName(ifaceParamName);
        return p;
    }

    public List addAndMapParameter(List modelParameters) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < modelParameters.size(); i++) {
            Parameter p = (Parameter) modelParameters.get(i);
            list.add(p.getConcreteParameter());
        }
        ArrayList ifaceParams = (ArrayList) interfaceBuilder.addAndMapModelObjects(list);
        interfaceBuilder.getBuildContext().addModelObjectReferences(ifaceParams);

        return Parameter.wrapParameterList(ifaceParams);
    }

    public void deleteParameters(List paramList) {
        PluginModelBuilder mod = (PluginModelBuilder) interfaceBuilder.getModel();
        ConnectionMappingManager mgr = mod.getMappingManager();

        List mObjs = new ArrayList();
        for (int i = 0; i < paramList.size(); i++) {
            Parameter p = (Parameter) paramList.get(i);
            ConcreteParameter cp = p.getConcreteParameter();
            mObjs.add(cp);
            mgr.removeAllMappings(cp);
        }
        interfaceBuilder.deleteModelObjects(mObjs);
    }

    public void deleteParameter(Parameter p) {
        ArrayList l = new ArrayList();
        l.add(p);
        deleteParameters(l);
    }

    public Parameter getParameterByName(String name) {
        Collection modelObjs = interfaceBuilder.getModelObjects();
        for (Iterator iterator = modelObjs.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            if (o instanceof ConcreteParameter) {
                ConcreteParameter p = (ConcreteParameter) o;
                if (p.getName().equals(name))
                    return Parameter.wrapParameter(p);
            }
        }
        return null;
    }

    public HashMap getInterfaceParamToModelParamNameMap() {
        Map realmap = interfaceBuilder.getInterfaceParamToModelParamMap(true);
        HashMap map = new HashMap();
        for (Iterator iterator = realmap.keySet().iterator(); iterator.hasNext();) {
            ConcreteParameter ip = (ConcreteParameter) iterator.next();
            ConcreteParameter mp = (ConcreteParameter) realmap.get(ip);
            map.put(ip.getName(), mp.getName());
        }
        return map;
    }

    public List getView() {
        return interfaceBuilder.getView();
    }
}
