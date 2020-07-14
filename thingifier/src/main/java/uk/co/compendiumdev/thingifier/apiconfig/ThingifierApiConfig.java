package uk.co.compendiumdev.thingifier.apiconfig;

import uk.co.compendiumdev.thingifier.JsonOutputConfig;

public class ThingifierApiConfig {

    private boolean willShowIdsInResponsesIfAvailable;
    private JsonOutputConfig jsonOutputConfig;
    private boolean willShowGuidsInResponses;
    private boolean willShowSingleInstancesAsPlural;
    private boolean willShowIdsInUrlsIfAvailable;
    private boolean willEnforceDeclaredTypesInInput;

    // todo: allowFilteringThroughUrlParams  true/false (default: true)
    // todo: enforceFilteringTrhoughUrlParams true/false ie. 404 error if params when not supported (default: true)
    // todo: api request level allow filtering e.g. on some /things allow filtering but not others
    // todo: enforcePluralsInApiCalls true/false i.e. throw404ErrorIfWrongPluralSingluarUsed

    public ThingifierApiConfig(){
        jsonOutputConfig = new JsonOutputConfig();

        // default to the most modern and 'up to date' config
        willShowSingleInstancesAsPlural=true;
        willShowGuidsInResponses=true;  // custom headers
        willShowIdsInResponsesIfAvailable = true; // relationship rendering
        willShowIdsInUrlsIfAvailable = true;  // location headers, api urls
        willEnforceDeclaredTypesInInput = true;

        // by default
        jsonOutputConfig.allowShowGuidsInResponse(willShowGuidsInResponses);
        jsonOutputConfig.compressRelationships(true);
        jsonOutputConfig.relationshipsUsesIdsIfAvailable(willShowIdsInResponsesIfAvailable);

    }

    public JsonOutputConfig jsonOutput() {
        return jsonOutputConfig;
    }


    public ThingifierApiConfig allowShowGuidsInResponses(boolean allow){
        willShowGuidsInResponses = allow;
        if(!allow) {
            jsonOutput().relationshipsUsesIdsIfAvailable(true);
            jsonOutput().allowShowGuidsInResponse(false);
        }
        return this;
    }

    public ThingifierApiConfig allowShowIdsInResponsesIfAvailable(boolean allow) {
        willShowIdsInResponsesIfAvailable = allow;
        jsonOutput().relationshipsUsesIdsIfAvailable(allow);
        return this;
    }

    public ThingifierApiConfig allowShowIdsInUrlsIfAvailable(boolean allow) {
        willShowIdsInUrlsIfAvailable = allow;
        jsonOutput().relationshipsUsesIdsIfAvailable(allow);
        return this;
    }

    public ThingifierApiConfig showSingleInstancesAsPlural(boolean yes){
        willShowSingleInstancesAsPlural = yes;
        return this;
    }

    public ThingifierApiConfig shouldEnforceDeclaredTypesInInput(boolean config){
        willEnforceDeclaredTypesInInput = config;
        return this;
    }

    public boolean singleInstancesArePlural() {
        return willShowSingleInstancesAsPlural;
    }

    public boolean showIdsInResponsesIfAvailable() {
        return willShowIdsInResponsesIfAvailable;
    }

    public boolean showGuidsInResponses() {
        return willShowGuidsInResponses;
    }

    public boolean showIdsInUrlsIfAvailable() {
        return willShowIdsInUrlsIfAvailable;
    }

    public boolean enforceDeclaredTypesInInput() {
        return willEnforceDeclaredTypesInInput;
    }
}