/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.endpoint;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.message.Message;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageMappingMethodInvoker;
import org.springframework.integration.util.MethodInvoker;
import org.springframework.util.Assert;

/**
 * @author Mark Fisher
 */
public class ServiceActivatorEndpoint extends AbstractInOutEndpoint {

	public static final String DEFAULT_LISTENER_METHOD = "handle";


	private final MethodInvoker invoker;


	public ServiceActivatorEndpoint(MethodInvoker invoker) {
		Assert.notNull(invoker, "invoker must not be null");
		this.invoker = invoker;
	}

	public ServiceActivatorEndpoint(Object object) {
		this(object, DEFAULT_LISTENER_METHOD);
	}

	public ServiceActivatorEndpoint(Object object, String methodName) {
		this(new MessageMappingMethodInvoker(object, methodName));
	}


	@Override
	protected void initialize() throws Exception {
		if (this.invoker instanceof InitializingBean) {
			((InitializingBean) this.invoker).afterPropertiesSet();
		}
	}

	@Override
	protected Object handle(Message<?> message) {
		try {
			return this.invoker.invokeMethod(message);
		}
		catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new MessageHandlingException(message, "failure occurred in endpoint '" + this.getName() + "'", e);
		}
	}

}
