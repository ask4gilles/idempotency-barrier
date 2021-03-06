/*
 * Copyright 2019 the original author or authors.
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
package io.fency;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.amqp.core.MessageProperties;

/**
 * @author Gilles Robert
 */
public final class IdempotencyTestUtils {

  private IdempotencyTestUtils() {

  }

  public static Message createIdempotentMessage() {
    MessageProperties messageProperties = createMessageProperties();
    MessageContext context = new MessageContext(messageProperties);
    return new Message(context);
  }

  public static MessageContext createIdempotentContext() {
    MessageProperties messageProperties = createMessageProperties();
    return new MessageContext(messageProperties);
  }

  private static MessageProperties createMessageProperties() {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setMessageId("msg-id");
    messageProperties.setReceivedRoutingKey("routing-key");
    messageProperties.setConsumerQueue("queue-name");
    messageProperties.setTimestamp(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
    return messageProperties;
  }
}
