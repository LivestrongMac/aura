/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.integration.test.root.parser.handler;


import org.auraframework.def.ComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.impl.AuraImplTestCase;
import org.auraframework.throwable.quickfix.InvalidDefinitionException;
import org.auraframework.throwable.quickfix.QuickFixException;
import org.junit.Test;

public class MethodDefHandlerTest extends AuraImplTestCase {
    //sanity test : positive case
    @Test
    public void testBasicMethod() throws QuickFixException  {
        getSimpleCmpDesc("<aura:method name='myMethod'> </aura:method>");
    }
    
    //method missing name should throw error
    @Test
    public void testMethodMissingName() throws Exception {
        try {
            DefDescriptor<ComponentDef> desc = getSimpleCmpDesc("<aura:method> </aura:method>");
            definitionService.getDefinition(desc);
            fail("Expect to fail with method without name");
        } catch (Exception e) {
            checkExceptionContains(e, InvalidDefinitionException.class, "The attribute 'name' is required on '<aura:method>'.");
        }
    }
    
    //two method with same name should throw error
    @Test
    public void testMethodDuplicate() throws Exception {
        String basicMethod = "<aura:method name='myMethod'> </aura:method>";
        try {
            DefDescriptor<ComponentDef> desc = getSimpleCmpDesc(basicMethod+basicMethod);
            definitionService.getDefinition(desc);
            fail("Expect to fail with method with duplicate name");
        } catch (Exception e) {
             checkExceptionContains(e, InvalidDefinitionException.class,
                     "There is already a method named 'myMethod' on component");
        }
    }
    
    //test method with same name as an event
    @Test
    public void testMethodDuplicateAsEvent() throws Exception {
        String basicMethod = "<aura:method name='myMethod'> </aura:method>";
        String basicEvent = "<aura:registerEvent name='myMethod' type='aura:operationComplete'/>";
        try {
            DefDescriptor<ComponentDef> desc = getSimpleCmpDesc(basicEvent+basicMethod);
            definitionService.getDefinition(desc);
            fail("Expect to fail with method with duplicate name as an event");
        } catch (Exception e) {
             checkExceptionContains(e, InvalidDefinitionException.class,
                     "The method 'myMethod' conflicts with an event of the same name on component");
        }
    }
    
    //test method with same name as an attribute, method is defined before attribute : test added for W-2520943
    //the error is actually being throw from handling event tag, but we don't have a eventDefHandlerTest.java 
    @Test
    public void testMethodDuplicateAsEvent2() throws Exception {
        String basicMethod = "<aura:method name='myMethod'> </aura:method>";
        String basicEvent = "<aura:registerEvent name='myMethod' type='aura:operationComplete'/>";
        try {
            DefDescriptor<ComponentDef> desc = getSimpleCmpDesc(basicMethod+basicEvent);
            definitionService.getDefinition(desc);
            fail("Expect to fail with method with duplicate name as an event");
        } catch (Exception e) {
             checkExceptionContains(e, InvalidDefinitionException.class,
                     "The event 'myMethod' conflicts with a method of the same name on component");
        }
    }
    
    //test method with same name as an attribute
    @Test
    public void testMethodDuplicateAsAttrite() throws Exception {
        String basicMethod = "<aura:method name='myMethod'> </aura:method>";
        String basicAttribute = "<aura:attribute name='myMethod' type='String'/>";
        try {
            DefDescriptor<ComponentDef> desc = getSimpleCmpDesc(basicAttribute+basicMethod);
            definitionService.getDefinition(desc);
            fail("Expect to fail with method with duplicate name as an attribute");
        } catch (Exception e) {
             checkExceptionContains(e, InvalidDefinitionException.class,
                     "The method 'myMethod' conflicts with an attribute of the same name on component");
        }
    }
    
    //test method with same name as an attribute, method is defined before attribute : test added for W-2520943
    //one can move this test to AttributeDefHandlerTest, as the error is being throw from handing attribute Tag
    //but they have a different setup there. so i just leave this one here.
    @Test
    public void testMethodDuplicateAsAttrite2() throws Exception {
        String basicMethod = "<aura:method name='myMethod'> </aura:method>";
        String basicAttribute = "<aura:attribute name='myMethod' type='String'/>";
        try {
            DefDescriptor<ComponentDef> desc = getSimpleCmpDesc(basicMethod+basicAttribute);
            definitionService.getDefinition(desc);
            fail("Expect to fail with method with duplicate name as an attribute");
        } catch (Exception e) {
             checkExceptionContains(e, InvalidDefinitionException.class,
                     "The attribute 'myMethod' conflicts with a method of the same name on component");
        }
    }
    
    //basic setup
    private DefDescriptor<ComponentDef> getSimpleCmpDesc(String markup) {
        DefDescriptor<ComponentDef> cmpDesc = getAuraTestingUtil().createStringSourceDescriptor(null,
                ComponentDef.class, null);
        addSourceAutoCleanup(cmpDesc, String.format(baseComponentTag, "", markup));
        return cmpDesc;
    }

}
