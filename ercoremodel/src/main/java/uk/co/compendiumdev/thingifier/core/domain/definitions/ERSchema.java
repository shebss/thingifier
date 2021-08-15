package uk.co.compendiumdev.thingifier.core.domain.definitions;

import uk.co.compendiumdev.thingifier.core.domain.definitions.relationship.RelationshipDefinition;
import uk.co.compendiumdev.thingifier.core.domain.definitions.relationship.RelationshipVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ERSchema {

    private final ConcurrentHashMap<String, RelationshipDefinition> relationships;
    private final ConcurrentHashMap<String, ThingDefinition> thingDefinitions;

    public ERSchema(){
        relationships = new ConcurrentHashMap<>();
        thingDefinitions = new ConcurrentHashMap<>();
    }

    public ThingDefinition defineThing(final String thingName, final String pluralName) {
        ThingDefinition definition = new ThingDefinition(thingName, pluralName);
        thingDefinitions.put(definition.getName(), definition);
        return definition;
    }

    public Collection<RelationshipDefinition> getRelationships() {
        return relationships.values();
    }

    public RelationshipDefinition defineRelationship(final ThingDefinition from, final ThingDefinition to, final String named, final Cardinality of) {
        RelationshipDefinition relationship =
                RelationshipDefinition.create(
                        new RelationshipVector(
                                from,
                                named,
                                to,
                                of));
        relationships.put(named, relationship);
        return relationship;
    }

    public boolean hasRelationshipNamed(final String relationshipName) {
        if (relationships.containsKey(relationshipName.toLowerCase())) {
            return true;
        }

        // perhaps it is a reverse relationship?
        for (RelationshipDefinition defn : relationships.values()) {
            if (defn.isTwoWay()) {
                if (defn.getReversedRelationship().getName().equalsIgnoreCase(relationshipName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasThingNamed(final String aName) {
        return thingDefinitions.containsKey(aName);
    }

    public List<String> getThingNames() {
        List<String> names = new ArrayList();
        names.addAll(thingDefinitions.keySet());
        return names;
    }


}