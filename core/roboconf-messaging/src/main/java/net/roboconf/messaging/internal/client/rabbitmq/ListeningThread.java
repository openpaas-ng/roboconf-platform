/**
 * Copyright 2014-2015 Linagora, Université Joseph Fourier, Floralis
 *
 * The present code is developed in the scope of the joint LINAGORA -
 * Université Joseph Fourier - Floralis research program and is designated
 * as a "Result" pursuant to the terms and conditions of the LINAGORA
 * - Université Joseph Fourier - Floralis research program. Each copyright
 * holder of Results enumerated here above fully & independently holds complete
 * ownership of the complete Intellectual Property rights applicable to the whole
 * of said Results, and may freely exploit it in any manner which does not infringe
 * the moral rights of the other copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.roboconf.messaging.internal.client.rabbitmq;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import net.roboconf.messaging.internal.utils.RabbitMqUtils;
import net.roboconf.messaging.messages.Message;

import com.rabbitmq.client.QueueingConsumer;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ListeningThread extends Thread {

	private final Logger logger;
	private final QueueingConsumer consumer;
	private final LinkedBlockingQueue<Message> messageQueue;
	private final String id;


	/**
	 * Constructor.
	 * @param threadName
	 * @param logger
	 * @param consumer
	 * @param messageQueue
	 * @param id
	 */
	public ListeningThread(
			String threadName,
			Logger logger,
			QueueingConsumer consumer,
			LinkedBlockingQueue<Message> messageQueue,
			String id ) {

		super( threadName );
		this.logger = logger;
		this.consumer = consumer;
		this.messageQueue = messageQueue;
		this.id = id;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		RabbitMqUtils.listenToRabbitMq( this.id, this.logger, this.consumer, this.messageQueue );
	}
}
