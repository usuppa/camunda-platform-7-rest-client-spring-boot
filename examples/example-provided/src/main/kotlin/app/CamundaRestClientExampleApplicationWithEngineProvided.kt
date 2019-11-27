/*-
 * #%L
 * camunda-rest-client-spring-boot-example
 * %%
 * Copyright (C) 2019 Camunda Services GmbH
 * %%
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 *  under one or more contributor license agreements. See the NOTICE file
 *  distributed with this work for additional information regarding copyright
 *  ownership. Camunda licenses this file to you under the Apache License,
 *  Version 2.0; you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * #L%
 */

package org.camunda.bpm.extension.rest.example.engine.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.camunda.bpm.extension.rest.EnableCamundaRestClient
import org.camunda.bpm.extension.rest.example.engine.client.ProcessClientConfiguration
import org.camunda.bpm.extension.rest.example.engine.process.JacksonDataFormatConfigurator
import org.camunda.bpm.extension.rest.example.engine.process.ProcessDelegatesConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Starts example application.
 * Profile "disableClient" disables the scheduled rest client.
 */
fun main(args: Array<String>) {
  runApplication<CamundaRestClientExampleApplicationWithEngineProvided>(*args)
}

@SpringBootApplication
@Import(
  ProcessDelegatesConfiguration::class,
  /**
   * Specify the Spring Profile "disableClient" to disable the scheduled client.
   */
  ProcessClientConfiguration::class
)
@EnableScheduling
@EnableCamundaRestClient
class CamundaRestClientExampleApplicationWithEngineProvided {

  @Bean
  fun objectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()
    return JacksonDataFormatConfigurator.configureObjectMapper(objectMapper)
  }
}
