/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.java;

import static junit.framework.Assert.assertEquals;
import static org.mule.modules.interceptor.matchers.Matchers.contains;
import static org.mule.munit.common.mocking.Attribute.attribute;
import org.mule.munit.runner.functional.FunctionalMunitSuite;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;


public class JavaMunitTest extends FunctionalMunitSuite
{

    @Override
    protected String getConfigResources()
    {
        return "mule-config.xml";
    }

    @Test
    public void test() throws Exception
    {
        whenMessageProcessor("create.*")
                .ofNamespace("ji.*")
                .withAttributes(attribute("name").ofNamespace("doc").withValue("jiraMp"))
                .thenReturn(muleMessageWithPayload("expected"));

        Object payload = runFlow("callingJira", testEvent("something")).getMessage().getPayload();

        assertEquals("expected", payload);
    }

    @Test
    public void testMockingOfSubFlowRef() throws Exception
    {
        whenMessageProcessor("sub-flow")
                .withAttributes(attribute("name").withValue(contains("callingJiraSubFlow")))
                .thenReturn(muleMessageWithPayload("hello world"));

        assertEquals(runFlow("callingSubFlow", testEvent("something")).getMessage().getPayload(),"hello world");
    }

    @Test
    public void testMockingOfFlowRef() throws Exception
    {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("name", "callingJira");
        whenMessageProcessor("flow")
                .withAttributes(attribute("name").withValue("callingJira"))
                .thenReturn(muleMessageWithPayload("hello world"));

        assertEquals(runFlow("callingFlow", testEvent("something")).getMessage().getPayload(), "hello world");
    }


    @Test
    public void testSetMuleAppHome() throws Exception
    {

        Object payload = runFlow("setMuleAppHomeFlow", testEvent("something")).getMessage().getPayload();

        assertEquals(new File(getClass().getResource("/mule-config.xml").getPath()).getParentFile().getAbsolutePath(), payload);
    }


}
