package btwmods.stats.measurements;

import java.lang.reflect.Method;

import btwmods.Stat;

public class StatEventDispatch extends StatTick {
	public final Class declaringClass;
	public final Method method;
	public final Object[] args;
	public final int methodCalls;
	
	public StatEventDispatch(Class declaringClass, Method method, Object[] args, int methodCalls, long nanoTime) {
		super(Stat.EVENT_DISPATCH);
		this.declaringClass = declaringClass;
		this.method = method;
		this.args = args;
		this.methodCalls = methodCalls;
		record(nanoTime);
	}

}
