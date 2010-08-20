/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.nema.medical.mint.metadata;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData;
import org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData;
import org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData;


/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="StudyMetaType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:AttributesType" name="Attributes" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="ns:SeriesListType" name="SeriesList"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="xs:string" use="required" name="studyInstanceUID"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Study implements AttributeStore
{
    private final Map<Integer,Attribute> attributeMap = new TreeMap<Integer,Attribute>();
    private final Map<String,Series> seriesMap = new TreeMap<String,Series>();
    private String studyInstanceUID;
    private String type;
    private String version;
    private int instanceCount = -1;

    /**
     * @param tag
     * @return the attribute for the given tag
     */
    public Attribute getAttribute(final int tag) {
        return attributeMap.get(tag);
    }

    public String getValueForAttribute(final int tag) {
        Attribute attr = getAttribute(tag);
        return attr != null ? attr.getVal() : null;
    }

    /**
     * puts an Attribute into the Series - attributes are unique per tag
     * @param attr
     */
    public void putAttribute(final Attribute attr) {
        attributeMap.put(attr.getTag(), attr);
    }

    /**
     * removes the Attribute with the given tag from the Series
     * @param tag
     */
    public void removeAttribute(final int tag) {
        attributeMap.remove(tag);
    }

    /**
     * @return an iterator of all Attributes in the Series
     */
    public Iterator<Attribute> attributeIterator() {
        return attributeMap.values().iterator();
    }

    /**
     * @param uid
     * @return the series for the given uid
     */
    public Series getSeries(final String uid) {
        return seriesMap.get(uid);
    }

    /**
     * puts an Series into the Study - series are unique per uid
     * @param series
     */
    public void putSeries(final Series series) {
        seriesMap.put(series.getSeriesInstanceUID(), series);
    }

    /**
     * removes the Series with the given uid from the Study
     * @param uid
     */
    public void removeSeries(final String uid) {
        seriesMap.remove(uid);
    }

    /**
     * @return an iterator of all Series in the Study
     */
    public Iterator<Series> seriesIterator() {
        return seriesMap.values().iterator();
    }

    /**
     * Get the 'studyInstanceUID' attribute value.
     *
     * @return value
     */
    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    /**
     * Set the 'studyInstanceUID' attribute value.
     *
     * @param studyInstanceUID
     */
    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }
    
    /**
     * Get the 'type' attribute value.
     *
     * @return value
     */
	public String getType() {
		return type;
	}
    
    /**
     * Set the 'type' attribute value.
     *
     * @param type
     */
	public void setType(String type) {
		this.type = type;
	}

    /**
     * Get the 'type' attribute value.
     *
     * @return value
     */
	public String getVersion() {
		return version;
	}

    /**
     * Set the 'version' attribute value.
     *
     * @param version
     */
	public void setVersion(String version) {
		this.version = version;
	}
	
    /**
     * Get the 'instanceCount' attribute value.
     *
     * @return instanceCount
     */
	public int getInstanceCount() {
		return instanceCount;
	}

    /**
     * @return the number of Instances in the Series
     */
    public int instanceCount() {
        int count = 0;
        for (Series series : this.seriesMap.values()) {
        	count += series.instanceCount();
        }
        return count;
    }

    /**
     * Set the 'instanceCount' attribute value.
     *
     * @param instanceCount
     */
	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

    /**
     * @return a list of all binary item IDs currently in the study
     */
    public Collection<Integer> getBinaryItemIDs() {
		final Set<Integer> items = new TreeSet<Integer>();
		
		/*
		 * Should need only one instance of this structure because should be
		 * emptied after each us (enforced by the while loop)
		 */
		Queue<Attribute> sequence = new LinkedList<Attribute>();

		// iterate through each instance and collect the bids
        for (Iterator<Series> i = this.seriesIterator(); i.hasNext();) {
            for (Iterator<Instance> ii = i.next().instanceIterator(); ii.hasNext();) {
                for (Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();) {
                    Attribute a = iii.next();

                    sequence.add(a);
                    
					/*
					 * Iteratively checks for bids in each attribute and any
					 * sequence attributes beneath it
					 */
                    while(!sequence.isEmpty())
                    {
                    	Attribute curr = sequence.remove();
                    	
                    	//Check if bid exists
                    	int bid = curr.getBid();
                        if (bid >= 0) {
                            int frameCount = curr.getFrameCount();
                            if (frameCount > 1) {
                                for (int newBid = bid; newBid < (bid + frameCount); newBid++) {
                                    items.add(newBid);
                                }
                            } else {
                                items.add(bid);
                            }
                        }
                    	
                        //Add children to queue
                    	for(Iterator<Item> iiii = curr.itemIterator(); iiii.hasNext();)
                    	{
                    		for(Iterator<Attribute> iiiii = iiii.next().attributeIterator(); iiiii.hasNext();)
                    		{
                    			sequence.add(iiiii.next());
                    		}
                    	}
                    }
                }
            }
        }
        
		return items;
    }

    //  Google Protocol Buffer support - package protection intentional
    //  Google Protocol Buffer support
    //
    static Study fromGPB(StudyData studyData) {
        Study study = new Study();
        study.setStudyInstanceUID(studyData.getStudyInstanceUid());
        if (studyData.hasType()) study.setType(studyData.getType());
        if (studyData.hasVersion()) study.setType(studyData.getVersion());

        for (AttributeData attrData : studyData.getAttributesList()) {
            Attribute attr = Attribute.fromGPB(attrData);
            study.putAttribute(attr);
        }
        for (SeriesData seriesData : studyData.getSeriesList()) {
            Series series = Series.fromGPB(seriesData);
            study.putSeries(series);
        }
        return study;
    }

    StudyData toGPB() {
        StudyData.Builder builder = StudyData.newBuilder();
        if (this.studyInstanceUID != null) {
            builder.setStudyInstanceUid(this.studyInstanceUID);
        }
        if (this.type != null) {
            builder.setType(this.type);
        }
        if (this.version != null) {
            builder.setVersion(this.version);
        }
        for (Attribute attr : this.attributeMap.values()) {
            builder.addAttributes(attr.toGPB());
        }
        for (Series series: this.seriesMap.values()) {
            builder.addSeries(series.toGPB());
        }
        StudyData data = builder.build();
        return data;
    }


}
