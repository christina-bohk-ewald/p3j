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

import java.util.Iterator;

import org.hibernate.FlushMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import p3j.database.IProjectionResultsIterator;
import p3j.experiment.results.ResultsOfTrial;
import p3j.pppm.ProjectionModel;

/**
 * HIbernate implementation of a results iterator.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProjectionResultsIterator implements IProjectionResultsIterator {

	/** The hibernate session. */
	private final Session session;

	/** The scrollable result set. */
	private final ScrollableResults results;

	/** The previous results. */
	private ResultsOfTrial previousResult;

	/** The next results. */
	private ResultsOfTrial nextResult;

	/**
	 * Instantiates a new projection results iterator.
	 * 
	 * @param sessionFactory
	 *          the session factory
	 * @param projectionID
	 *          the projection ID
	 */
	public ProjectionResultsIterator(SessionFactory sessionFactory,
	    int projectionID) {

		session = sessionFactory.openSession();
		session.setFlushMode(FlushMode.AUTO);
		ProjectionModel currentProjection = (ProjectionModel) session
		    .createCriteria(ProjectionModel.class)
		    .add(Restrictions.eq("ID", projectionID)).list().get(0);

		results = session.createCriteria(ResultsOfTrial.class)
		    .add(Restrictions.eq("projection", currentProjection))
		    .addOrder(Order.asc("ID")).scroll();
		nextResult = getNextElement();
	}

	private ResultsOfTrial getNextElement() {
		return !results.next() ? null : (ResultsOfTrial) results.get(0);
	}

	@Override
	public ResultsOfTrial getNextResult() {
		if (previousResult != null) {
			clearFromCache(previousResult);
		}

		ResultsOfTrial result = null;
		if (nextResult != null) {
			result = nextResult;
			nextResult = getNextElement();
			previousResult = result;
		}
		return result;
	}

	/**
	 * Clears result from cache.
	 * 
	 * @param resultsOfTrial
	 *          the trial results to be cleared from the cache
	 */
	public void clearFromCache(ResultsOfTrial resultsOfTrial) {
		session.evict(resultsOfTrial);
		session.flush();
	}

	@Override
	public Iterator<ResultsOfTrial> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return nextResult != null;
	}

	@Override
	public ResultsOfTrial next() {
		return getNextResult();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removal is not supported.");
	}

}
