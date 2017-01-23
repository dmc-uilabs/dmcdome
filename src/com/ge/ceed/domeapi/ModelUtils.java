package com.ge.ceed.domeapi;

import com.ge.ceed.domeapi.web.Response;

@Deprecated
public class ModelUtils {
	
//	private Hashtable<String, Model> _mdls = new  Hashtable<String, Model>();
//	
//	public ModelUtils() {	
//		// Sample models hard coded
//	    try {
//	    	Model m = new Model();
//		    ModelDef mdl = new ModelDef();
//		    mdl.name = "service resputation calculator";
//		    mdl.guid = "1";
//		    mdl.desc = "a model that combines a user community trust score with a validation score from the curator oft the model portal";
//			mdl.dateModified = new java.text.SimpleDateFormat ("MM/dd/yyyy HH:mm:ss").parse("07/19/2011 01:00:00").getTime();
//			m.modelDef = mdl;
//			m.inParams = new ModelParam[2];
//			m.inParams[0] = new ModelParam("community trust score", "real", "none");
//			m.inParams[1] = new ModelParam("curator validation score", "real", "none");
//			m.outParams = new ModelParam[1];
//			m.outParams[0] = new ModelParam("service reputation score", "real", "none");
//			_mdls.put(mdl.guid, m);
//			
//			m = new Model();
//			mdl = new ModelDef();
//		    mdl.name = "community trust calculator";
//		    mdl.guid = "2";
//		    mdl.desc = "excel spreadsheet that takes community ratings and model use information to calculate a score for community trust";
//			mdl.dateModified = new java.text.SimpleDateFormat ("MM/dd/yyyy HH:mm:ss").parse("07/19/2011 01:00:00").getTime();
//			m.modelDef = mdl;
//			m.inParams = new ModelParam[3];
//			m.inParams[0] = new ModelParam("number of model reviews", "real", "none");
//			m.inParams[1] = new ModelParam("average score from reviewers", "real", "none");
//			m.inParams[2] = new ModelParam("number of subscriptions", "real", "none");
//			m.outParams = new ModelParam[1];
//			m.outParams[0] = new ModelParam("community trust rating", "real", "none");
//			_mdls.put(mdl.guid, m);
//			
//			m = new Model();
//			mdl = new ModelDef();
//		    mdl.name = "heated gas 2";
//		    mdl.guid = "3";
//		    mdl.desc = "This variation of the \"heated gas\" model calculates the final mass instead of volume.";
//		    mdl.dateModified = new java.text.SimpleDateFormat ("MM/dd/yyyy HH:mm:ss").parse("02/28/2007 01:00:00").getTime();
//		    m.modelDef = mdl;
//			m.inParams = new ModelParam[6];
//			m.inParams[0] = new ModelParam("initial volume", "real", "none");
//			m.inParams[1] = new ModelParam("time", "real", "none");
//			m.inParams[2] = new ModelParam("power (heat input rate)", "real", "none");
//			m.inParams[3] = new ModelParam("initial presure", "real", "none");
//			m.inParams[4] = new ModelParam("initial temperature", "real", "none");
//			m.inParams[5] = new ModelParam("density", "real", "none");
//			m.outParams = new ModelParam[2];
//			m.outParams[0] = new ModelParam("new temperature", "real", "none");
//			m.outParams[1] = new ModelParam("new mass", "real", "none");
//			_mdls.put(mdl.guid, m);
//	    } catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public Response getModels(){
//		Response ret = new Response();
//		ret.pkg = new ModelDef[_mdls.size()];
//		int ii = 0;
//		for (Model mdl :_mdls.values()){
//			ret.pkg[ii++] = mdl.modelDef;
//		}
//		return ret;
//	}
//
//	public Response getModel(String guid){
//		Response ret = new Response();
//		if ((guid != null) && _mdls.containsKey(guid)){
//			ret.pkg = new Model[1];
//			ret.pkg[0] = _mdls.get(guid);
//		}
//		else {
//			ret.status = "error";
//			ret.msg = "model not found for guid: " + guid;
//		}
//		return ret;
//	}
//
	public Response runModel(IModel rsp) {
		Response ret = new Response();
//		if (rsp != null) {
//			for (int ii = 0; ii < rsp.outParams.length; ii++) {
//				rsp.outParams[ii].val = Double.parseDouble(rsp.outParams[ii].val.toString()) + Math.random();
//			}
//			ret.pkg = new Object[1];
//			ret.pkg[0] = rsp;
//		}
//		else {
//			ret.status = "error";
//			ret.msg = "No model specified in call to runModel.";
//		}
		return ret;
	}
}
