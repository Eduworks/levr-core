package com.eduworks.resolver;

import com.eduworks.lang.EwList;
import com.eduworks.lang.EwMap;

public class Context extends EwMap<String,Object>
{
	public Context()
	{
		
	}
	@Override
	protected void finalize() throws Throwable
	{
		for (ContextEvent ce : finalizeEvents)
			ce.go();
		super.finalize();
	}

	public synchronized Object get(String s){
		return super.get(s);
	}
	public synchronized Object put(String s,Object o){
		return super.put(s,o);
	}
	
	EwList<ContextEvent> successEvents = new EwList<ContextEvent>();
	EwList<ContextEvent> failureEvents = new EwList<ContextEvent>();
	EwList<ContextEvent> finallyEvents = new EwList<ContextEvent>();
	EwList<ContextEvent> finalizeEvents = new EwList<ContextEvent>();
	public void onSuccess(ContextEvent c)
	{
		successEvents.add(c);
	}

	public void onFailure(ContextEvent c)
	{
		failureEvents.add(c);
	}

	public void onFinally(ContextEvent c)
	{
		finallyEvents.add(c);
	}

	public void onFinalize(ContextEvent c)
	{
		finalizeEvents.add(c);
	}
	
	public void success()
	{
		for (ContextEvent ce : successEvents)
			ce.go();
	}
	public void failure()
	{
		for (ContextEvent ce : failureEvents)
			ce.go();
	}
	public void finish()
	{
		for (ContextEvent ce : finallyEvents)
			ce.go();
	}
}
