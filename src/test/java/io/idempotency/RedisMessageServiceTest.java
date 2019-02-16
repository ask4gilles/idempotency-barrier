/*
 * Copyright 2016-2019 the original author or authors.
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
package io.idempotency;

import java.util.Optional;
import java.util.StringJoiner;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RedisMessageServiceTest {

  @Mock
  private RedisTemplate<String, Message> mRedisTemplate;
  @InjectMocks
  private RedisMessageService idempotentMessageService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSave() {
    // given
    Message message = IdempotencyTestUtils.createIdempotentMessage();

    // when
    idempotentMessageService.save(message);

    // then
    ArgumentCaptor<SessionCallback<Object>> captor = ArgumentCaptor.forClass(SessionCallback.class);
    verify(mRedisTemplate).execute(captor.capture());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindByMessageIdAndConsumerQueueName() {
    // given
    Message expectedMessage = IdempotencyTestUtils.createIdempotentMessage();
    String messageId = expectedMessage.getId();
    String consumerQueueName = expectedMessage.getConsumerQueueName();
    String key = getKey(messageId, consumerQueueName);

    ValueOperations<String, Message> mValueOperations = mock(ValueOperations.class);
    given(mRedisTemplate.opsForValue()).willReturn(mValueOperations);
    given(mValueOperations.get(key)).willReturn(expectedMessage);

    // when
    Optional<Message> actualMessage = idempotentMessageService.find(IdempotencyTestUtils.createIdempotentContext());

    // then
    assertThat(actualMessage, notNullValue());
    assertThat(actualMessage.isPresent(), is(true));
    assertThat(expectedMessage, sameInstance(actualMessage.get()));
    verify(mValueOperations).get(key);
  }

  private String getKey(String messageId, String consumerQueueName) {
    StringJoiner stringJoiner = new StringJoiner(":");
    stringJoiner.add(messageId);
    stringJoiner.add(consumerQueueName);
    return stringJoiner.toString();
  }
}