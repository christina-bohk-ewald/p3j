/*
 * Copyright 2006 - 2013 Christina Bohk and Roland Ewald
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package p3j.misc;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import p3j.pppm.ProjectionModel;
import p3j.pppm.SubPopulation;
import p3j.pppm.SubPopulationModel;

/**
 * Tests for {@link Serializer}.
 * 
 * Created on 01.08.2013
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class TestSerializer {

  private static final String TEMPORARY_MODEL_FILE = "./testModel.p3j";

  final SubPopulationModel subPopModel = new SubPopulationModel();
  {
    subPopModel.getSubPopulations().add(
        new SubPopulation("TestPop", true, true, true));
    subPopModel.getSubPopulations().add(
        new SubPopulation("TestPop2", false, false, false));
  }

  final ProjectionModel testProjectionModel = new ProjectionModel(
      "scenarioName", "scenarioDescription", 3, 50, 100, 2011, subPopModel);

  @Test
  public void testSubPoopulationStorage() throws IOException,
      ClassNotFoundException {
    (new Serializer()).save(testProjectionModel, TEMPORARY_MODEL_FILE);
    ProjectionModel projectionModel = (ProjectionModel) (new Serializer())
        .load(TEMPORARY_MODEL_FILE);
    SubPopulationModel loadedModel = projectionModel.getSubPopulationModel();

    Assert.assertEquals("Number of sub-populations should match.", subPopModel
        .getSubPopulations().size(), loadedModel.getSubPopulations().size());

    for (int i = 0; i < subPopModel.getSubPopulations().size(); i++) {
      SubPopulation expectedSubPop = subPopModel.getSubPopulations().get(i);
      Assert.assertEquals("Sub-populations should be equal", expectedSubPop,
          loadedModel.getSubPopulations().get(i));
    }
  }

}
