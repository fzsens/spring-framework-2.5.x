/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.ejb.CreateException;
import javax.ejb.EJBObject;

import org.aopalliance.intercept.AspectException;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.remoting.RemoteAccessException;

/**
 * <p>Basic invoker for a remote Stateless Session Bean.
 * "Creates" a new EJB instance for each invocation.
 * 
 * <p>See {@link org.springframework.jndi.AbstractJndiLocator} for info on
 * how to specify the JNDI location of the target EJB.
 *
 * <p>In a bean container, this class is normally best used as a singleton. However,
 * if that bean container pre-instantiates singletons (as do the XML ApplicationContext
 * variants) you may have a problem if the bean container is loaded before the EJB
 * container loads the target EJB. That is because the JNDI lookup will be performed in
 * the init method of this class and cached, but the EJB will not have been bound at the
 * target location yet. The solution is to not pre-instantiate this factory object, but
 * allow it to be created on first use. In the XML containers, this is controlled via
 * the "lazy-init" attribute.
 *
 * <p>This invoker is typically used with an RMI business interface, which serves
 * as super-interface of the EJB component interface. Alternatively, this invoker
 * can also proxy a remote SLSB with a matching non-RMI business interface, i.e. an
 * interface that mirrors the EJB business methods but does not declare RemoteExceptions.
 * In the latter case, RemoteExceptions thrown by the EJB stub will automatically get
 * converted to Spring's unchecked RemoteAccessException.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 09-May-2003
 * @version $Id: SimpleRemoteSlsbInvokerInterceptor.java,v 1.8 2004-03-17 17:19:42 jhoeller Exp $
 * @see org.springframework.remoting.RemoteAccessException
 */
public class SimpleRemoteSlsbInvokerInterceptor extends AbstractRemoteSlsbInvokerInterceptor {
	
	/**
	 * Constructor for use as JavaBean.
	 * Sets "resourceRef" to false by default.
	 * @see #setResourceRef
	 */
	public SimpleRemoteSlsbInvokerInterceptor() {
		setResourceRef(false);
	}
	
	/**
	 * Convenient constructor for programmatic use.
	 * @see org.springframework.jndi.AbstractJndiLocator#setJndiName
	 * @see org.springframework.jndi.AbstractJndiLocator#setResourceRef
	 */
	public SimpleRemoteSlsbInvokerInterceptor(String jndiName, boolean resourceRef) throws AspectException {
		setJndiName(jndiName);
		setResourceRef(resourceRef);
		try {
			afterPropertiesSet();
		}
		catch (Exception ex) {
			throw new AspectException("Failed to create EJB invoker interceptor", ex);
		}
	}
	
	/**
	 * This is the last invoker in the chain.
	 * "Creates" a new EJB instance for each invocation.
	 * Can be overridden for custom invocation strategies.
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			EJBObject ejb = newSessionBeanInstance();
			Method method = invocation.getMethod();
			if (method.getDeclaringClass().isInstance(ejb)) {
				// directly implemented
				return method.invoke(ejb, invocation.getArguments());
			}
			else {
				// not directly implemented
				Method proxyMethod = ejb.getClass().getMethod(method.getName(), method.getParameterTypes());
				return proxyMethod.invoke(ejb, invocation.getArguments());
			}
		}
		catch (InvocationTargetException ex) {
			Throwable targetException = ex.getTargetException();
			logger.info("Method of remote EJB [" + getJndiName() + "] threw exception", ex.getTargetException());
			if (targetException instanceof RemoteException &&
					!Arrays.asList(invocation.getMethod().getExceptionTypes()).contains(RemoteException.class)) {
				throw new RemoteAccessException("Could not invoke remote EJB [" + getJndiName() + "]", targetException);
			}
			else if (targetException instanceof CreateException) {
				if (!Arrays.asList(invocation.getMethod().getExceptionTypes()).contains(RemoteException.class)) {
					throw new RemoteAccessException("Could not create remote EJB [" + getJndiName() + "]", targetException);
				}
				else {
					throw new RemoteException("Could not create remote EJB [" + getJndiName() + "]", targetException);
				}
			}
			else {
				throw targetException;
			}
		}
		catch (Throwable ex) {
			throw new AspectException("Failed to invoke remote EJB [" + getJndiName() + "]", ex);
		}
	}

}
