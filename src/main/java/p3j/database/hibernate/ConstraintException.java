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
package p3j.database.hibernate;

/**
 * This exception gets thrown when certain database constraints are violated.
 * 
 * Created: August 18, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ConstraintException extends RuntimeException {

  /** Serialization ID. */
  private static final long serialVersionUID = -7809321652909211426L;

  /**
   * Default constructor.
   * 
   * @param msg
   *          the message explaining the violation
   */
  public ConstraintException(String msg) {
    super(msg);
  }

}
