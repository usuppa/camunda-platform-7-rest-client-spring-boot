package org.camunda.bpm.extension.feign.itest

import com.tngtech.jgiven.annotation.As
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.variable.Variables.createVariables
import org.junit.Test
import java.util.*

@RuntimeServiceCategory
@As("Catch Signal")
class RuntimeServiceCatchSignalITest : CamundaBpmFeignITestBase<RuntimeService, RuntimeServiceActionStage, RuntimeServiceAssertStage>() {

  @Test
  fun `should signal waiting instance`() {
    val processDefinitionKey = processDefinitionKey()
    val signalName = "mySignal1"
    val userTaskId = "user-task"
    given()
      .process_with_intermediate_signal_catch_event_is_deployed(processDefinitionKey, userTaskId, signalName)
      .and()
      .process_is_started_by_key(processDefinitionKey)
      .and()

    whenever()
      .remoteService
      .signalEventReceived(signalName)

    then()
      .process_instance_exists(processDefinitionKey) { instance, stage ->
        assertThat(instance.businessKey).isNull()
        assertThat(
          stage.localService.createProcessInstanceQuery()
            .processInstanceId(instance.id)
            .activityIdIn(userTaskId)
            .singleResult()
        ).isNotNull
      }
  }

  @Test
  fun `should signal waiting instance and set variables`() {
    val processDefinitionKey = processDefinitionKey()
    val signalName = "mySignal2"
    val userTaskId = "user-task"
    given()
      .process_with_intermediate_signal_catch_event_is_deployed(processDefinitionKey, userTaskId, signalName)
      .and()
      .process_is_started_by_key(processDefinitionKey, "my-business-key1", "caseInstanceId1", createVariables().putValue("VAR1", "VAL1"))

    whenever()
      .remoteService
      .signalEventReceived(signalName, createVariables().putValue("VAR2", "VAL2"))

    then()
      .process_instance_exists(processDefinitionKey) { instance, stage ->
        assertThat(instance.businessKey).isEqualTo("my-business-key1")
        assertThat(instance.caseInstanceId).isEqualTo("caseInstanceId1")
        assertThat(
          stage.localService.createProcessInstanceQuery()
            .processInstanceId(instance.id)
            .activityIdIn(userTaskId)
            .singleResult()
        ).isNotNull
        assertThat(
          stage.localService.getVariables(instance.id, listOf("VAR1", "VAR2"))
        ).containsValues("VAL1", "VAL2")

      }
  }

  @Test
  fun  `should signal waiting instance by execution id and signal name`() {
    val processDefinitionKey = processDefinitionKey()
    val signalName = "mySignal3"
    val userTaskId = "user-task"
    given()
      .process_with_intermediate_signal_catch_event_is_deployed(processDefinitionKey, userTaskId, signalName)
      .and()
      .process_is_started_by_key(processDefinitionKey, "my-business-key1", "caseInstanceId1", createVariables().putValue("VAR1", "VAL1"))
      .and()
      .execution_is_waiting_for_signal()

    whenever()
      .remoteService
      .signalEventReceived(signalName, given().execution.id)

    then()
      .process_instance_exists(processDefinitionKey) { instance, stage ->
        assertThat(instance.businessKey).isEqualTo("my-business-key1")
        assertThat(instance.caseInstanceId).isEqualTo("caseInstanceId1")
        assertThat(
          stage.localService.createProcessInstanceQuery()
            .processInstanceId(instance.id)
            .activityIdIn(userTaskId)
            .singleResult()
        ).isNotNull
        assertThat(
          stage.localService.getVariables(instance.id, listOf("VAR1"))
        ).containsValues("VAL1")
      }
  }

  @Test
  fun  `should signal waiting instance by execution id and signal name and set variables`() {
    val processDefinitionKey = processDefinitionKey()
    val signalName = "mySignal4"
    val userTaskId = "user-task"
    given()
      .process_with_intermediate_signal_catch_event_is_deployed(processDefinitionKey, userTaskId, signalName)
      .and()
      .process_is_started_by_key(processDefinitionKey, "my-business-key1", "caseInstanceId1", createVariables().putValue("VAR1", "VAL1"))
      .and()
      .execution_is_waiting_for_signal()

    whenever()
      .remoteService
      .signalEventReceived(signalName, given().execution.id, createVariables().putValue("VAR2", "VAL2"))

    then()
      .process_instance_exists(processDefinitionKey) { instance, stage ->
        assertThat(instance.businessKey).isEqualTo("my-business-key1")
        assertThat(instance.caseInstanceId).isEqualTo("caseInstanceId1")
        assertThat(
          stage.localService.createProcessInstanceQuery()
            .processInstanceId(instance.id)
            .activityIdIn(userTaskId)
            .singleResult()
        ).isNotNull
        assertThat(
          stage.localService.getVariables(instance.id, listOf("VAR1", "VAR2"))
        ).containsValues("VAL1", "VAL2")
      }
  }

  @Test
  fun  `should signal waiting instance created by signal builder and set variables`() {
    val processDefinitionKey = processDefinitionKey()
    val signalName = "mySignal7"
    val userTaskId = "user-task"
    given()
      .process_with_intermediate_signal_catch_event_is_deployed(processDefinitionKey, userTaskId, signalName)
      .and()
      .process_is_started_by_key(processDefinitionKey, "my-business-key1", "caseInstanceId1", createVariables().putValue("VAR1", "VAL1"))
      .and()
      .execution_is_waiting_for_signal()

    whenever()
      .remoteService
      .createSignalEvent(signalName)
      .executionId(given().execution.id)
      .setVariables(createVariables().putValue("VAR2", "VAL2"))
      .send()

    then()
      .process_instance_exists(processDefinitionKey) { instance, stage ->
        assertThat(instance.businessKey).isEqualTo("my-business-key1")
        assertThat(instance.caseInstanceId).isEqualTo("caseInstanceId1")
        assertThat(
          stage.localService.createProcessInstanceQuery()
            .processInstanceId(instance.id)
            .activityIdIn(userTaskId)
            .singleResult()
        ).isNotNull
        assertThat(
          stage.localService.getVariables(instance.id, listOf("VAR1", "VAR2"))
        ).containsValues("VAL1", "VAL2")
      }
  }
}
