package org.nema.medical.mint.metadata;

import java.util.Collection;
import java.util.Iterator;

public interface StudySummary {

	/**
	 * @param tag
	 * @return the attribute for the given tag
	 */
	public Attribute getAttribute(final int tag);

	/**
	 * @return an iterator of all Attributes in the Series
	 */
	public Iterator<Attribute> attributeIterator();

	/**
	 * @param uid
	 * @return the series for the given uid
	 */
	public Series getSeries(final String uid);

	/**
	 * @return an iterator of all Series in the Study
	 */
	public Iterator<Series> seriesIterator();

	/**
	 * Get the 'studyInstanceUID' attribute value.
	 *
	 * @return value
	 */
	public String getStudyInstanceUID();

	/**
	 * Get the 'type' attribute value.
	 *
	 * @return value
	 */
	public String getType();

	/**
	 * Get the 'type' attribute value.
	 *
	 * @return value
	 */
	public String getVersion();

	/**
	 * Get the 'instanceCount' attribute value.
	 *
	 * @return instanceCount
	 */
	public int getInstanceCount();

	/**
	 * Counts the number of instances in each series and updates the instanceCount variable.
	 *
	 * @return instanceCount
	 */
	public int computeInstanceCount();

	/**
	 * @return a list of all binary item IDs currently in the study
	 */
	public Collection<Integer> getBinaryItemIDs();

}