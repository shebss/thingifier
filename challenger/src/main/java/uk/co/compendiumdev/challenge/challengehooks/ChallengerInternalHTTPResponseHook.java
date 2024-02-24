package uk.co.compendiumdev.challenge.challengehooks;

import uk.co.compendiumdev.challenge.ChallengerState;
import uk.co.compendiumdev.challenge.challengesrouting.XChallengerHeader;
import uk.co.compendiumdev.thingifier.api.http.headers.headerparser.BearerAuthHeaderParser;
import uk.co.compendiumdev.challenge.CHALLENGE;
import uk.co.compendiumdev.challenge.ChallengerAuthData;
import uk.co.compendiumdev.challenge.challengers.Challengers;
import uk.co.compendiumdev.thingifier.api.http.HttpApiRequest;
import uk.co.compendiumdev.thingifier.application.internalhttpconversion.InternalHttpResponse;
import uk.co.compendiumdev.thingifier.application.sparkhttpmessageHooks.InternalHttpResponseHook;

import static uk.co.compendiumdev.thingifier.api.http.HttpApiRequest.VERB.*;


public class ChallengerInternalHTTPResponseHook implements InternalHttpResponseHook {

    private final Challengers challengers;

    public ChallengerInternalHTTPResponseHook(final Challengers challengers) {
        this.challengers = challengers;
    }


    @Override
    public void run(final HttpApiRequest request, final InternalHttpResponse response) {

        ChallengerAuthData challenger = challengers.getChallenger(request.getHeader("X-CHALLENGER"));

        // allow cross origin requests
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if (request.getVerb() == OPTIONS && request.getHeaders().headerExists("Access-Control-Allow-Methods")) {
            response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Allow-Methods"));
        }

        // we can complete a challenge while the user is null - creating the user
        if (request.getVerb() == POST &&
                request.getPath().contentEquals("challenger") &&
                response.getStatusCode() == 201) {
            // challenger did not exist so we need to find it to pass the challenge

            String challengerId = response.getHeader("X-Challenger");
            challenger = challengers.getChallenger(challengerId);
            if (challenger != null && challenger.getState()== ChallengerState.NEW) {
                challengers.pass(challenger, CHALLENGE.CREATE_NEW_CHALLENGER);
            }
        }

        if (request.getVerb() == GET &&
                request.getPath().startsWith("challenger/") &&
                response.getStatusCode() == 204) {

            String challengerId = response.getHeader("X-Challenger");
            challenger = challengers.getChallenger(challengerId);
            if (challenger != null && challenger.getState()== ChallengerState.LOADED_FROM_PERSISTENCE) {
                challengers.pass(challenger, CHALLENGE.GET_RESTORE_EXISTING_CHALLENGER);
            }
        }

        if (request.getVerb() == POST &&
                request.getPath().startsWith("challenger") &&
                response.getStatusCode() == 200) {

            String givenChallengerId = request.getHeader("X-Challenger");
            String challengerId = response.getHeader("X-Challenger");
            challenger = challengers.getChallenger(challengerId);
            if (challenger != null && givenChallengerId.equals(challengerId) && challenger.getState()== ChallengerState.LOADED_FROM_PERSISTENCE) {
                challengers.pass(challenger, CHALLENGE.POST_RESTORE_EXISTING_CHALLENGER);
            }
        }


        if (challenger == null) {
            if (!request.getPath().contentEquals("challenger") &&
                    !request.getPath().contains("mirror/r") // exclude mirror endpoints from adding a challenger
            ) {

                if (!response.getHeaders().headerExists("X-CHALLENGER")) {
                    XChallengerHeader.setResultHeaderBasedOnChallenger(response, challenger);
                }
            }
            // cannot track challenges
            if (response.getStatusCode() == 404 && (response.getBody() == null || response.getBody().isEmpty())) {
                setResponseAs404(request, response);
            }
            return;
        }

        if (challenger != null) {
            if (!response.getHeaders().headerExists("X-CHALLENGER")) {
                if(!request.getPath().contains("mirror/r")){
                    // exclude mirror endpoints from adding a challenger
                    XChallengerHeader.setResultHeaderBasedOnChallenger(response, challenger);
                }

            }
        }

        // No endpoint defined so this 404 created by Spark routing
        if (request.getVerb() == GET &&
                request.getPath().contentEquals("todo") &&
                response.getStatusCode() == 404) {
            challengers.pass(challenger, CHALLENGE.GET_TODOS_NOT_PLURAL_404);
        }


        if (request.getVerb() == OPTIONS &&
                request.getPath().contentEquals("todos") &&
                response.getStatusCode() == 200) {
            challengers.pass(challenger, CHALLENGE.OPTIONS_TODOS);
        }

        if(request.getVerb() == PUT && request.getPath().startsWith("todos") && response.getStatusCode() == 400){
            if(response.getBody().contains("Cannot create todo with PUT due to Auto fields id")){
                challengers.pass(challenger,CHALLENGE.PUT_TODOS_400);
            }
        }

        if (request.getVerb() == POST &&
                request.getPath().contentEquals("secret/token") &&
                request.getHeaders().headerExists("Authorization") &&
                request.getHeader("Authorization").length() > 10 &&
                response.getStatusCode() == 401) {
            challengers.pass(challenger, CHALLENGE.CREATE_SECRET_TOKEN_401);
        }

        if (request.getVerb() == POST &&
                request.getPath().contentEquals("secret/token") &&
                request.getHeaders().headerExists("Authorization") &&
                request.getHeader("Authorization").length() > 10 &&
                response.getStatusCode() == 201) {
            challengers.pass(challenger, CHALLENGE.CREATE_SECRET_TOKEN_201);
        }

        if (request.getVerb() == GET &&
                request.getPath().contentEquals("secret/note") &&
                request.getHeaders().headerExists("X-AUTH-TOKEN") &&
                request.getHeader("X-AUTH-TOKEN").length() > 1 &&
                response.getStatusCode() == 403) {
            challengers.pass(challenger, CHALLENGE.GET_SECRET_NOTE_403);
        }

        if (request.getVerb() == GET &&
                request.getPath().contentEquals("secret/note") &&
                !request.getHeaders().headerExists("X-AUTH-TOKEN") &&
                response.getStatusCode() == 401) {
            challengers.pass(challenger, CHALLENGE.GET_SECRET_NOTE_401);
        }

        if (request.getVerb() == POST &&
                request.getPath().contentEquals("secret/note") &&
                request.getHeaders().headerExists("X-AUTH-TOKEN") &&
                request.getHeader("X-AUTH-TOKEN").length() > 1 &&
                request.getBody().contains("\"note\"") &&
                response.getStatusCode() == 403) {
            challengers.pass(challenger, CHALLENGE.POST_SECRET_NOTE_403);
        }

        if (request.getVerb() == POST &&
                request.getPath().contentEquals("secret/note") &&
                !request.getHeaders().headerExists("X-AUTH-TOKEN") &&
                request.getBody().contains("\"note\"") &&
                response.getStatusCode() == 401) {
            challengers.pass(challenger, CHALLENGE.POST_SECRET_NOTE_401);
        }

        if (request.getVerb() == POST &&
                request.getPath().contentEquals("secret/note") &&
                request.getHeaders().headerExists("X-AUTH-TOKEN") &&
                request.getBody().contains("\"note\"") &&
                response.getStatusCode() == 200) {
            challengers.pass(challenger, CHALLENGE.POST_SECRET_NOTE_200);
        }

        if (request.getVerb() == GET &&
                request.getPath().contentEquals("secret/note") &&
                request.getHeaders().headerExists("X-AUTH-TOKEN") &&
                response.getStatusCode() == 200) {
            challengers.pass(challenger, CHALLENGE.GET_SECRET_NOTE_200);
        }

        if (request.getVerb() == GET &&
                request.getPath().contentEquals("secret/note") &&
                request.getHeaders().headerExists("Authorization") &&
                new BearerAuthHeaderParser(request.getHeader("Authorization")).isValid() &&
                response.getStatusCode() == 200) {
            challengers.pass(challenger, CHALLENGE.GET_SECRET_NOTE_BEARER_200);
        }

        if (request.getVerb() == POST &&
                request.getPath().contentEquals("secret/note") &&
                request.getHeaders().headerExists("Authorization") &&
                new BearerAuthHeaderParser(request.getHeader("Authorization")).isValid() &&
                request.getBody().contains("\"note\"") &&
                response.getStatusCode() == 200) {
            challengers.pass(challenger, CHALLENGE.POST_SECRET_NOTE_BEARER_200);
        }

        if (response.getStatusCode() == 404 && (response.getBody() == null || response.getBody().isEmpty())) {
            setResponseAs404(request, response);
        }
    }

    private void setResponseAs404(HttpApiRequest request, InternalHttpResponse response) {

        if (request.getAcceptHeader() != null && !request.getAcceptHeader().isEmpty()) {
            if (request.getAcceptHeader().contains("html")){
                // treat as a GUI request and redirect
                response.setStatus(307);
                response.setHeader("Location", "/gui/404/" + request.getPath());
                return;
            }
            if (request.getAcceptHeader().startsWith("application/")) {
                if (request.getAcceptHeader().endsWith("xml")) {
                    response.setType("application/xml");
                    response.setBody("<errorMessages><message>404 resource Unknown</message></errorMessages>");
                    return;
                }
                if(request.getAcceptHeader().endsWith("json")) {
                    response.setType("application/json");
                    response.setBody("{\"errorMessages\":[\"404 resource Unknown\"]}");
                    return;
                }
            }
        }
    }

}
