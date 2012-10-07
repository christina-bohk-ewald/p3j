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
 * Exception to be thrown when loaded projection has a format that does not
 * allow automated import.
 * 
 * Created on 07.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class LoadedProjectionFormatException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7314821595793032607L;

  /**
   * Instantiates a new loaded projection format exception.
   * 
   * @param msg
   *          the message
   */
  public LoadedProjectionFormatException(String msg) {
    super(msg);
  }

}
