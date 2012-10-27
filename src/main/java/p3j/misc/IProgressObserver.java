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
package p3j.misc;

/**
 * Interface for components that display the progress in one way or another.
 * Used to decouple the GUI from the underlying implementation.
 * 
 * Created on 27.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public interface IProgressObserver {

  /** Adds additional waypoints to the progress bar. */
  void addWaypoints(int additionalWayPoints);

  /** Increments progress to next waypoint and shows new status. */
  void incrementProgress(String status);

  /**
   * Updates progress to given waypoint, displaying the given status.
   * 
   * @param waypoint
   *          the current waypoint
   * @param status
   *          the status
   */
  void updateProgress(int waypoint, String status);

  /**
   * Get the current waypoint
   * 
   * @return the current waypoint
   */
  int getCurrentWaypoint();

  /**
   * Call this method to indicate that the task is finished.
   */
  void taskFinished();

  /**
   * Can be used to preemptively abort a long-running task.
   * 
   * @return true if process has been cancelled by user
   */
  boolean isCancelled();

  /**
   * Deal with progress display in case the task is cancelled.
   */
  void taskCanceled();
}