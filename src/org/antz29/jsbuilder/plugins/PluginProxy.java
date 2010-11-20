package org.antz29.jsbuilder.plugins;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PluginProxy {
	
	private static PluginProxy instance = new PluginProxy();
	
	private PluginProxy() {}
	
	public static PluginProxy getInstance() {
		return instance;
	}
	
    private class ProxyHandler implements InvocationHandler {
        private final Object delegate;

        public ProxyHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method delegateMethod = delegate.getClass().getMethod( method.getName(), method.getParameterTypes() );
            return delegateMethod.invoke( delegate, args );
        }
    }

	public Object createProxy(Object object, Class<?>[] interfaces, ClassLoader cl) {
    	ProxyHandler handler = new ProxyHandler( object );
        return Proxy.newProxyInstance( cl, interfaces, handler );
    }
}
