package com.ge.ceed.domeapi;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

//No known references
@Deprecated
public class UserDefinedExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes arg0) {
		boolean ret = arg0.getName() == "changeListeners";
		return ret;
	}

}
