/*
 * Copyright 2006 - 2012 Christina Bohk and Roland Ewald
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
package p3j.gui.dialogs.execstatus;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.processor.IProcessor;

/**
 * Instruments PPPM simulations with an observer that displays the progress to
 * the user.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ExecutionProgressInstrumenter implements IComputationInstrumenter {

  /** Serialization ID. */
  private static final long serialVersionUID = 1037938671428167027L;

  /**
   * List of observers. To make sure that the same dialog is used in case of
   * multiple threads, only *one* instance of the observer is created and then
   * saved for future use.
   * 
   * TODO: This should be solved more elegantly.
   */
  private static List<IObserver<?>> observers = new ArrayList<>();

  /** The number of trials. */
  private int numberOfTrials;

  /**
   * Instantiates a new execution progress instrumenter.
   */
  public ExecutionProgressInstrumenter() {

  }

  /**
   * Instantiates a new execution progress instrumenter.
   * 
   * @param numOfTrials
   *          the num of trials
   */
  public ExecutionProgressInstrumenter(Integer numOfTrials) {
    observers.clear();
    numberOfTrials = numOfTrials;
  }

  @Override
  public void instrumentComputation(IComputationTask computationTask) {

    synchronized (observers) {
      if (observers.size() == 0) {
        observers.add(new ExecutionProgressDialog(numberOfTrials));
      }
      ((ExecutionProgressDialog) observers.get(0))
          .addSimulationRun(computationTask);
    }
    IProcessor simulator = computationTask.getProcessorInfo().getLocal();
    Mediator.create(simulator);
    simulator.registerObserver(observers.get(0));
  }

  @Override
  public List<? extends IObserver<?>> getInstantiatedObservers() {
    return observers;
  }

  /**
   * Cleans list of observers.
   */
  public static void cleanObservers() {
    observers.clear();
  }

  public int getNumberOfTrials() {
    return numberOfTrials;
  }

  public void setNumberOfTrials(int numberOfTrials) {
    this.numberOfTrials = numberOfTrials;
  }
}
