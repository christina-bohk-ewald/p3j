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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.database.hibernate.P3MDatabase;
import p3j.pppm.ProjectionModel;

/**
 * 
 * Class that stores and loads serializable classes. Needed to store and load
 * parameter files.
 * 
 * Created on February 12, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class Serializer {

	/**
	 * Flag that indicates whether objects should be stored in XML or in binary
	 * encoding.
	 */
	private boolean usingXML = true;

	/**
	 * Flag that indicates whether GZIP compression (LZW) is used.
	 */
	private boolean usingCompression = false;

	/**
	 * Loads object from file.
	 * 
	 * @param file
	 *            path to file with the object to be loaded
	 * @return object the object that has been loaded
	 * @throws IOException
	 *             if file was not found, file input failed, etc.
	 * @throws ClassNotFoundException
	 *             if class of persistent object could not be found
	 */
	public Object load(String file) throws IOException, ClassNotFoundException {
		if (usingXML) {
			return loadFromXML(file);
		}
		return loadFromBinary(file);
	}

	/**
	 * Load object from a binary file.
	 * 
	 * @param file
	 *            path and file name
	 * @return deserialised object
	 * @throws IOException
	 *             if file was not found, etc.
	 * @throws ClassNotFoundException
	 *             if class of persistent object could not be found
	 */
	public Object loadFromBinary(String file) throws IOException,
			ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(getInputStream(file));
		Object o = input.readObject();
		input.close();
		return o;
	}

	/**
	 * Load object from XML file.
	 * 
	 * @param file
	 *            path and file name
	 * @return deserialised object
	 * @throws IOException
	 *             if file was not found, a read error occurred, etc.
	 */
	public Object loadFromXML(String file) throws IOException {
		XMLDecoder xmlDecoder = new XMLDecoder(getInputStream(file));
		Object object = xmlDecoder.readObject();
		xmlDecoder.close();
		return object;
	}

	/**
	 * Create an input stream.
	 * 
	 * @param file
	 *            source file
	 * @return input stream from file
	 * @throws IOException
	 *             if stream creation fails
	 */
	protected InputStream getInputStream(String file) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		if (isUsingCompression()) {
			in = new GZIPInputStream(in);
		}
		return in;
	}

	/**
	 * Create an output stream.
	 * 
	 * @param file
	 *            target file
	 * @return output stream to file
	 * @throws IOException
	 *             if stream creation fails
	 */
	protected OutputStream getOutputStream(String file) throws IOException {
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		if (isUsingCompression()) {
			out = new GZIPOutputStream(out);
		}
		return out;
	}

	/**
	 * Save object to file.
	 * 
	 * @param object
	 *            the object to be saved in the file
	 * @param file
	 *            the file
	 * @throws IOException
	 *             if outputting went wrong
	 */
	public void save(ProjectionModel pm, String file) throws IOException {
		ProjectionModel modelToSave = retrieveFullModel(pm.getID());
		if (usingXML) {
			saveToXML(modelToSave, file);
		} else {
			saveToBinary(modelToSave, file);
		}
	}

	/**
	 * Retrieve full model for storage. The returned object must not contain any
	 * hibernate-based data structures (used for lazy evaluation etc.).
	 * 
	 * @param pmID
	 *            the ID of the projection model
	 * @return the full projection model
	 */
	private ProjectionModel retrieveFullModel(int pmID) {
		IP3MDatabase db = DatabaseFactory.createDatabase(P3MDatabase
				.getHibernateConfigFile()); // TODO: switch off lazy-eval
		return db.getProjectionByID(pmID);
	}

	/**
	 * Save object to binary file.
	 * 
	 * @param object
	 *            the object to be written
	 * @param file
	 *            the target file
	 * @throws IOException
	 *             if writing fails
	 */
	public void saveToBinary(Object object, String file) throws IOException {
		ObjectOutputStream output = new ObjectOutputStream(
				getOutputStream(file));
		output.writeObject(object);
		output.close();
	}

	/**
	 * Save object to XML file.
	 * 
	 * @param object
	 *            the object to be written
	 * @param file
	 *            the target file
	 * @throws IOException
	 *             if writing fails
	 */
	public void saveToXML(Object object, String file) throws IOException {
		XMLEncoder xmlEncoder = new XMLEncoder(getOutputStream(file));
		xmlEncoder.writeObject(object);
		xmlEncoder.close();
	}

	public boolean isUsingXML() {
		return usingXML;
	}

	public void setUsingXML(boolean usingXML) {
		this.usingXML = usingXML;
	}

	public boolean isUsingCompression() {
		return usingCompression;
	}

	public void setUsingCompression(boolean usingCompression) {
		this.usingCompression = usingCompression;
	}

}