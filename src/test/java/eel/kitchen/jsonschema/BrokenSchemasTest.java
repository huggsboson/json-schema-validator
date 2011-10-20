/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema;

import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class BrokenSchemasTest
{
    private static final JsonNode testNode;
    private static final JsonNode dummy;
    private static final SchemaNodeFactory factory = new SchemaNodeFactory();

    private JasonSchema schema;
    private SchemaNode schemaNode;
    private List<String> ret;

    static {
        try {
            testNode = JasonHelper.load("broken-schemas.json");
            dummy = new ObjectMapper().readTree("\"hello\"");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Test
    public void testNullSchema()
    {
        schema = new JasonSchema(null);
        assertFalse(schema.validate(dummy));

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "#: schema is null");
    }

    @Test
    public void testNotASchema()
    {
        schemaNode = factory.getSchemaNode(testNode.get("not-a-schema"));

        assertFalse(schemaNode.isValid());

        ret = schemaNode.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema is not an object");
    }

    @Test
    public void testIllegalType()
    {
        schemaNode = factory.getSchemaNode(testNode.get("illegal-type"));

        assertFalse(schemaNode.isValid());
        ret = schemaNode.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "type property is neither a string nor an "
            + "array");
    }

    @Test
    public void testIllegalTypeArray()
    {
        schemaNode = factory.getSchemaNode(testNode.get("illegal-type-array"));

        assertFalse(schemaNode.isValid());

        ret = schemaNode.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "non string or schema element in type "
            + "property array");
    }

    @Test
    public void testEmptyTypeSet()
    {
        schemaNode = factory.getSchemaNode(testNode.get("empty-type-set"));

        assertFalse(schemaNode.isValid());

        ret = schemaNode.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testDisallowAny()
    {
        schemaNode = factory.getSchemaNode(testNode.get("disallow-any"));

        assertFalse(schemaNode.isValid());

        ret = schemaNode.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testIntegerVsNumber()
    {
        schemaNode = factory.getSchemaNode(testNode.get("integer-vs-number"));

        assertFalse(schemaNode.isValid());

        ret = schemaNode.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "schema does not allow any type??");
    }

    @Test
    public void testUnknownType()
    {
        schemaNode = factory.getSchemaNode(testNode.get("unknown-type"));

        assertFalse(schemaNode.isValid());

        ret = schemaNode.getMessages();

        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "unknown type pwet");
    }

    @Test
    public void testVoidEnum()
    {
        schema = new JasonSchema(testNode.get("void-enum"));

        assertFalse(schema.validate(dummy));

        ret = schema.getMessages();
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), "#: node does not match any value in the "
            + "enumeration");
    }
}
