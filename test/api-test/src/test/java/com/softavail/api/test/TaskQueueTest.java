package com.softavail.api.test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.DisplayName;
import com.softavail.commsrouter.api.dto.arg.CreateRouterArg;
import com.softavail.commsrouter.api.dto.arg.CreateQueueArg;
import com.softavail.commsrouter.api.dto.arg.CreatePlanArg;
import com.softavail.commsrouter.api.dto.arg.CreateTaskArg;
import com.softavail.commsrouter.api.dto.arg.CreateAgentArg;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;
import com.softavail.commsrouter.api.dto.model.ApiObjectId;
import com.softavail.commsrouter.api.dto.model.CreatedTaskDto;
import com.softavail.commsrouter.api.dto.model.RouterDto;
import com.softavail.commsrouter.api.dto.model.RuleDto;
import com.softavail.commsrouter.api.dto.model.QueueDto;
import com.softavail.commsrouter.api.dto.model.PlanDto;
import com.softavail.commsrouter.api.dto.model.TaskDto;
import com.softavail.commsrouter.api.dto.model.AgentDto;
import com.softavail.commsrouter.api.dto.model.attribute.AttributeGroupDto;
import com.softavail.commsrouter.api.dto.model.attribute.StringAttributeValueDto;
import com.softavail.commsrouter.api.dto.model.attribute.LongAttributeValueDto;
import java.util.Collections;

/**
 * Unit test for Task to queue mapping.
 */
//@TestInstance(Lifecycle.PER_CLASS)

@DisplayName("Task to Queue mapping Tests")
public class TaskQueueTest {

  private HashMap<CommsRouterResource,String> state = new HashMap<CommsRouterResource,String>();
  private Router r = new Router(state);
  private Queue q = new Queue(state);
  private Plan p = new Plan(state);
  private Task t = new Task(state);

  @BeforeEach
  public void createRouterAndQueue() {
    CreateRouterArg routerArg = new CreateRouterArg();
    routerArg.setDescription("Router description");
    routerArg.setName("router-name");
    ApiObjectId id = r.create(routerArg);

    String predicate = "1==1";
    CreateQueueArg queueArg = new CreateQueueArg();
    queueArg.setDescription("queue description");
    queueArg.setPredicate(predicate);
    q = new Queue(state);
    id = q.create(queueArg);
  }

  @AfterEach
  public void deleteRouter() {
    r.delete();
    q.delete();
  }

  @AfterEach
  public void deletePlanAndTask() {
    p.delete();
    q.delete();
  }

  private void createPlan(String predicate){
    CreatePlanArg arg = new CreatePlanArg();
    arg.setDescription("Rule with predicate "+predicate);
    RuleDto rule = new RuleDto();
    rule.setPredicate(predicate);
    rule.setQueueId(state.get(CommsRouterResource.QUEUE));
    arg.setRules(Collections.singletonList(rule));
    ApiObjectId id = p.create(arg);
  }

  private void createTask(AttributeGroupDto requirements) throws MalformedURLException {
    CreateTaskArg arg = new CreateTaskArg();
    arg.setCallbackUrl(new URL("http://example.com"));
    arg.setRequirements(requirements);
    //("Task with  plan " + requirements.toString());
    t.createWithPlan(arg);
  }

  private void addPlanTask(AttributeGroupDto requirements, String predicate)
      throws MalformedURLException {
    createPlan(predicate);
    createTask(requirements);
  }

  @Test
  @DisplayName("Add task with no attribs queue.")
  public void addTask() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    addPlanTask(taskAttribs, "1==1");
    assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute to queue.")
  public void addTaskOneAttribute() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    taskAttribs.put("lang", new StringAttributeValueDto("en"));
    addPlanTask(taskAttribs, "1==1");
    assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute and predicate to check it - ==.")
  public void addTaskOneAttributeEquals() throws MalformedURLException {
    assertThat(q.size(), is(0));
     AttributeGroupDto taskAttribs = new AttributeGroupDto();
     taskAttribs.put("lang", new StringAttributeValueDto("en"));
     addPlanTask(taskAttribs, "#{lang}=='en'");
     assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute and predicate to check it - !=.")
  public void addTaskOneAttributeNotEquals() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    taskAttribs.put("lang", new StringAttributeValueDto("en"));
    addPlanTask(taskAttribs, "#{lang}!='bg'");
    assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute and predicate to check it - number >.")
  public void addTaskOneAttributeCompare() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    taskAttribs.put("age", new LongAttributeValueDto(20));
    addPlanTask(taskAttribs, "#{age}>18 && #{age}<33");
    assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute and predicate to check it - with parents.")
  public void addTaskOneAttributeParents() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    taskAttribs.put("age", new LongAttributeValueDto(20));
    addPlanTask(taskAttribs, "(#{age}>18 && #{age}<33)");
    assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute and predicate to check it - or.")
  public void addTaskOneAttributeOr() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    taskAttribs.put("age", new LongAttributeValueDto(20));
    addPlanTask(taskAttribs, "'true' || 'false'");
    assertThat(q.size(), is(1));
  }

  @Test
  @DisplayName("Add task with one attribute and predicate to check it 'true' && 'true'")
  public void addTaskOneAttributeAndOnly() throws MalformedURLException {
    assertThat(q.size(), is(0));
    AttributeGroupDto taskAttribs = new AttributeGroupDto();
    taskAttribs.put("age", new LongAttributeValueDto(20));
    addPlanTask(taskAttribs, "'true' && 'true'");
    assertThat(q.size(), is(1));
  }

}