package uk.co.compendiumdev.casestudy.todomanager.http_api;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.co.compendiumdev.casestudy.todomanager.TodoManagerModel;
import uk.co.compendiumdev.thingifier.Thing;
import uk.co.compendiumdev.thingifier.Thingifier;
import uk.co.compendiumdev.thingifier.api.http.HttpApiRequest;
import uk.co.compendiumdev.thingifier.api.http.HttpApiResponse;
import uk.co.compendiumdev.thingifier.api.http.ThingifierHttpApi;
import uk.co.compendiumdev.thingifier.api.response.ApiResponse;
import uk.co.compendiumdev.thingifier.generic.instances.ThingInstance;


public class RelationshipHttpTest {

    private Thingifier todoManager;

    Thing todo;
    Thing project;
    Thing categories;

    @Before
    public void createDefinitions() {

        todoManager = TodoManagerModel.definedAsThingifier();

        todo = todoManager.getThingNamed("todo");
        project = todoManager.getThingNamed("project");
        categories = todoManager.getThingNamed("category");


    }

    @Test
    public void canCreateARelationshipBetweenProjectAndTodoViaTasks(){

        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO");
        todo.addInstance(atodo);

        final ThingInstance aproject = project.createInstance().setValue("title", "a Project");
        project.addInstance(aproject);

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());

        HttpApiRequest request = new HttpApiRequest("projects/" + aproject.getGUID() + "/tasks");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        //{"guid":"%s"}
        String body = String.format("{\"guid\":\"%s\"}", atodo.getGUID());
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(201, response.getStatusCode());

        Assert.assertEquals(1,aproject.connectedItems("tasks").size());

    }

    @Test
    public void canCreateARelationshipAndTodoBetweenProjectAndTodoViaTasks(){

        final ThingInstance aproject = project.createInstance().setValue("title", "a Project");
        project.addInstance(aproject);

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());
        Assert.assertEquals(0,todo.countInstances());

        HttpApiRequest request = new HttpApiRequest("projects/" + aproject.getGUID() + "/tasks");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        //{"title":"My New Todo"}
        String body = "{\"title\":\"My New Todo\"}";
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);

        Assert.assertEquals(201, response.getStatusCode());

        Assert.assertEquals(1,aproject.connectedItems("tasks").size());
        Assert.assertEquals(1,todo.countInstances());

        final ThingInstance inMemoryTodo = todo.findInstanceByGUID(response.getHeaders().get(ApiResponse.GUID_HEADER));
        Assert.assertTrue(response.getBody(), response.getBody().contains(inMemoryTodo.getGUID()));

    }

    @Test
    public void cannotCreateARelationshipBetweenProjectAndCategoryViaTasks(){


        final ThingInstance acategory = categories.createInstance().setValue("title", "a Category");
        todo.addInstance(acategory);

        final ThingInstance aproject = project.createInstance().setValue("title", "a Project");
        project.addInstance(aproject);

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());

        HttpApiRequest request = new HttpApiRequest("projects/" + aproject.getGUID() + "/tasks");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        //{"guid":"%s"}
        String body = String.format("{\"guid\":\"%s\"}", acategory.getGUID());
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(400, response.getStatusCode());

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());

        final ErrorMessages errors = new Gson().fromJson(response.getBody(), ErrorMessages.class);

        Assert.assertEquals(1,errors.errorMessages.length);

        Assert.assertEquals("Could not find a relationship named tasks between project and a category", errors.errorMessages[0]);
    }

    @Test
    public void cannotCreateARelationshipWhenGivenGuidDoesNotExist(){


        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO");
        todo.addInstance(atodo);

        final ThingInstance aproject = project.createInstance().setValue("title", "a Project");
        project.addInstance(aproject);

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());

        HttpApiRequest request = new HttpApiRequest("projects/" + aproject.getGUID() + "/tasks");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        //{"guid":"%s"}
        String body = String.format("{\"guid\":\"%s\"}", atodo.getGUID() + "bob");
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(404, response.getStatusCode());

        final ErrorMessages errors = new Gson().fromJson(response.getBody(), ErrorMessages.class);
        Assert.assertEquals(1,errors.errorMessages.length);

        Assert.assertTrue(errors.errorMessages[0],errors.errorMessages[0].startsWith("Could not find thing with GUID "));
        Assert.assertTrue(errors.errorMessages[0],errors.errorMessages[0].endsWith("bob"));
    }

    // need to see if I can create where a relationship name is the same as a plural entity
    @Test
    public void canCreateARelationshipBetweenCategoryAndTodoViaTodos(){


        final ThingInstance acategory = categories.createInstance().setValue("title", "a Category");
        categories.addInstance(acategory);

        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO");
        todo.addInstance(atodo);

        Assert.assertEquals(0,acategory.connectedItems("todos").size());

        HttpApiRequest request = new HttpApiRequest("categories/" + acategory.getGUID() + "/todos");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        //{"guid":"%s"}
        String body = String.format("{\"guid\":\"%s\"}", atodo.getGUID());
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(201, response.getStatusCode());

        Assert.assertEquals(1,acategory.connectedItems("todos").size());

    }

    @Test
    public void canCreateARelationshipAndTodoBetweenCategoryAndTodoViaTodos(){

        final ThingInstance acategory = categories.createInstance().setValue("title", "a Category");
        categories.addInstance(acategory);


        Assert.assertEquals(0,acategory.connectedItems("todos").size());
        Assert.assertEquals(0,todo.countInstances());

        HttpApiRequest request = new HttpApiRequest("categories/" + acategory.getGUID() + "/todos");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        //{"title":"My New Todo"}
        String body = "{\"title\":\"My New Todo\"}";
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);

        Assert.assertEquals(201, response.getStatusCode());

        Assert.assertEquals(1,acategory.connectedItems("todos").size());
        Assert.assertEquals(1,todo.countInstances());

        final ThingInstance inMemoryTodo = todo.findInstanceByGUID(response.getHeaders().get(ApiResponse.GUID_HEADER));
        Assert.assertTrue(response.getBody(), response.getBody().contains(inMemoryTodo.getGUID()));

    }

    @Test
    public void canCreateARelationshipBetweenProjectAndTodoViaTasksUsingXml(){

        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO");
        todo.addInstance(atodo);

        final ThingInstance aproject = project.createInstance().setValue("title", "a Project");
        project.addInstance(aproject);

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());

        HttpApiRequest request = new HttpApiRequest("projects/" + aproject.getGUID() + "/tasks");
        request.getHeaders().putAll(HeadersSupport.containsXml());

        //<todo><guid>%s</guid></todo>}
        String body = String.format("<todo><guid>%s</guid></todo>", atodo.getGUID());
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(201, response.getStatusCode());

        Assert.assertEquals(1,aproject.connectedItems("tasks").size());

    }

    @Test
    public void canDeleteARelationshipBetweenProjectAndTodoViaTasks(){

        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO");
        todo.addInstance(atodo);

        final ThingInstance aproject = project.createInstance().setValue("title", "a Project");
        project.addInstance(aproject);

        aproject.connects("tasks", atodo);

        Assert.assertEquals(1,aproject.connectedItems("tasks").size());
        Assert.assertEquals(1, todo.countInstances());
        Assert.assertEquals(1, project.countInstances());


        HttpApiRequest request = new HttpApiRequest("projects/" + aproject.getGUID() + "/tasks/" + atodo.getGUID());

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).delete(request);
        Assert.assertEquals(200, response.getStatusCode());

        Assert.assertEquals(0,aproject.connectedItems("tasks").size());
        Assert.assertEquals(1, todo.countInstances());
        Assert.assertEquals(1, project.countInstances());

    }

    // need to see if I can delete where a relationship name is the same as a plural entity
    @Test
    public void canDeleteARelationshipBetweenCategoryAndTodoViaTodos(){


        final ThingInstance acategory = categories.createInstance().setValue("title", "a Category");
        categories.addInstance(acategory);

        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO");
        todo.addInstance(atodo);

        acategory.connects("todos", atodo);

        Assert.assertEquals(1,acategory.connectedItems("todos").size());
        Assert.assertEquals(1, todo.countInstances());
        Assert.assertEquals(1, categories.countInstances());

        final HttpApiRequest request = new HttpApiRequest("categories/" + acategory.getGUID() + "/todos/" + atodo.getGUID());

        HttpApiResponse response = new ThingifierHttpApi(todoManager).delete(request);
        Assert.assertEquals(200, response.getStatusCode());

        Assert.assertEquals(0,acategory.connectedItems("todos").size());
        Assert.assertEquals(1, todo.countInstances());
        Assert.assertEquals(1, categories.countInstances());

        // if relationship doesn't exist, I should get a 404 if I reissue therequest
        response = new ThingifierHttpApi(todoManager).delete(request);
        Assert.assertEquals(404, response.getStatusCode());

        Assert.assertEquals(0,acategory.connectedItems("todos").size());
        Assert.assertEquals(1, todo.countInstances());
        Assert.assertEquals(1, categories.countInstances());
    }

    /**
     * Optional Relationships - Mandatory
     *
     * can not create an estimate without a todo
     * can create an estimate when added to a todo directly because relationship is created
     * when delete a todo the estimate is also deleted
     * TODO: amend relationship to move estimate to another TODO
     * GET estimates for a todo
     * GET todos for an estimate
     */

    // can not create an estimate on its own, without a todo
    @Test
    public void canNotCreateEstimateWithoutMandatoryRelationship(){


        HttpApiRequest request = new HttpApiRequest("estimate");
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        String body = "{\"duration\":\"3\"}";
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(400, response.getStatusCode());

        Assert.assertEquals(0, todoManager.getThingNamed("estimate").countInstances());
    }

    @Test
    public void canCreateAnEstimateForTodoMandatoryRelationship(){

        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO for estimating");
        todo.addInstance(atodo);


        HttpApiRequest request = new HttpApiRequest("todos/" + atodo.getGUID() + "/estimates" );
        request.getHeaders().putAll(HeadersSupport.acceptJson());

        String body = "{\"duration\":\"3\"}";
        request.setBody(body);

        final HttpApiResponse response = new ThingifierHttpApi(todoManager).post(request);
        Assert.assertEquals(201, response.getStatusCode());

        Assert.assertEquals(1, todoManager.getThingNamed("estimate").countInstances());
        Assert.assertEquals(1, atodo.connectedItems("estimates").size());

    }


    @Test
    public void canDeleteAnEstimateWhenTodoDeletedBecauseOfMandatoryRelationship(){



        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO for estimating");
        todo.addInstance(atodo);

        final Thing estimates = todoManager.getThingNamed("estimate");
        final ThingInstance anEstimate = estimates.createInstance().setValue("duration", "7");
        estimates.addInstance(anEstimate);

        anEstimate.connects("estimate", atodo);

        Assert.assertEquals(1, atodo.connectedItems("estimates").size());
        Assert.assertEquals(1, estimates.countInstances());
        Assert.assertEquals(1, todo.countInstances());


        final HttpApiRequest request = new HttpApiRequest("todos/" + atodo.getGUID());

        HttpApiResponse response = new ThingifierHttpApi(todoManager).delete(request);
        Assert.assertEquals(200, response.getStatusCode());


        Assert.assertEquals(0, todo.countInstances());
        Assert.assertEquals(0, estimates.countInstances());

    }

    @Test
    public void canGetEstimatesViaRelationship(){



        final ThingInstance atodo = todo.createInstance().setValue("title", "a TODO for estimating");
        todo.addInstance(atodo);

        final Thing estimates = todoManager.getThingNamed("estimate");
        final ThingInstance anEstimate = estimates.createInstance().setValue("duration", "7").setValue("description", "an estimate");
        estimates.addInstance(anEstimate);

        anEstimate.connects("estimate", atodo);

        Assert.assertEquals(1, atodo.connectedItems("estimates").size());
        Assert.assertEquals(1, estimates.countInstances());
        Assert.assertEquals(1, todo.countInstances());


        HttpApiRequest request = new HttpApiRequest("todos/" + atodo.getGUID() + "/estimates");

        HttpApiResponse response = new ThingifierHttpApi(todoManager).get(request);
        Assert.assertEquals(200, response.getStatusCode());

        System.out.println(response.getBody());

        final EstimateCollectionResponse estimatesfound = new Gson().fromJson(response.getBody(), EstimateCollectionResponse.class);

        Assert.assertEquals(1, estimatesfound.estimates.length);
        Assert.assertEquals("7", estimatesfound.estimates[0].duration);
        Assert.assertEquals("an estimate", estimatesfound.estimates[0].description);


        request = new HttpApiRequest("estimates/" + anEstimate.getGUID() + "/estimate");

        response = new ThingifierHttpApi(todoManager).get(request);
        Assert.assertEquals(200, response.getStatusCode());

        System.out.println(response.getBody());

        final TodoCollectionResponse todosfound = new Gson().fromJson(response.getBody(), TodoCollectionResponse.class);

        Assert.assertEquals(1, todosfound.todos.length);
        Assert.assertEquals("a TODO for estimating", todosfound.todos[0].title);


    }


    private class TodoCollectionResponse {

        Todo[] todos;

    }

    private class EstimateCollectionResponse {

        Estimate[] estimates;

    }

    private class Estimate {

        String duration;
        String description;
    }

    private class Todo {

        String guid;
        String title;
    }

    private class ErrorMessages {

        String[] errorMessages;
    }
}
