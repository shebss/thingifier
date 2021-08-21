package uk.co.compendiumdev.thingifier.core.domain.instances;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.compendiumdev.thingifier.core.domain.definitions.Cardinality;
import uk.co.compendiumdev.thingifier.core.domain.definitions.EntityDefinition;
import uk.co.compendiumdev.thingifier.core.domain.definitions.relationship.RelationshipDefinition;
import uk.co.compendiumdev.thingifier.core.domain.definitions.relationship.RelationshipVector;

class RelationshipInstanceTest {

    @Test
    void canCreateARelationshipInstance(){

        EntityInstanceCollection thingfrom = new EntityInstanceCollection(new EntityDefinition("from", "from"));
        EntityInstanceCollection thingto = new EntityInstanceCollection(new EntityDefinition("to", "to"));
        RelationshipVector vector = new RelationshipVector(
                thingfrom.definition(), "fromto", thingto.definition(), Cardinality.ONE_TO_ONE
        );
        RelationshipDefinition defn = RelationshipDefinition.create(vector);

        final EntityInstance fromInstance = thingfrom.createManagedInstance();
        final EntityInstance toInstance = thingfrom.createManagedInstance();
        RelationshipVectorInstance rel = new RelationshipVectorInstance(
                                    vector, fromInstance, toInstance);

        System.out.println(rel);

        Assertions.assertEquals(defn, rel.getRelationshipDefinition());
        Assertions.assertEquals(toInstance, rel.getTo());
        Assertions.assertEquals(fromInstance, rel.getFrom());

        Assertions.assertTrue(rel.involves(fromInstance));
        Assertions.assertTrue(rel.involves(toInstance));
        Assertions.assertFalse(rel.involves(thingfrom.createManagedInstance()));

        Assertions.assertEquals(toInstance, rel.getOtherThingInstance(fromInstance));
        Assertions.assertEquals(fromInstance, rel.getOtherThingInstance(toInstance));

    }
}
