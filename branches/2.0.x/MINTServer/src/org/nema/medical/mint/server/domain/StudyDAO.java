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
package org.nema.medical.mint.server.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.nema.medical.mint.utils.DateTimeParseException;
import org.nema.medical.mint.utils.JodaDateUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class StudyDAO extends HibernateDaoSupport {

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	private static int MAXSECONDS = 60 * 60 * 24;

	private int between(final int first, final int second, final int value) {
		final int min = Math.min(first, second);
		final int max = Math.max(first, second);
		return Math.min(Math.max(value, min), max);
	}

    @SuppressWarnings("unchecked")
	public List<MINTStudy> findStudies(final String studyInstanceUID, final String accessionNumber,
                                final String accessionNumberIssuer, final String patientID,
                                final String patientIDIssuer, final Date minStudyDateTime, final Date minStudyDate,
                                final Date maxStudyDateTime, final Date maxStudyDate, final int limit,
                                final int offset) throws DateTimeParseException {

        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MINTStudy.class);
        detachedCriteria.addOrder(Order.desc("lastModified"));
        
        if (studyInstanceUID != null && StringUtils.isNotBlank(studyInstanceUID)) {
            detachedCriteria.add(Restrictions.eq("studyInstanceUID", studyInstanceUID));
        }
        if (accessionNumber != null && StringUtils.isNotBlank(accessionNumber)) {
            detachedCriteria.add(Restrictions.eq("accessionNumber", accessionNumber));
        }
        if (accessionNumberIssuer != null && StringUtils.isNotBlank(accessionNumberIssuer)) {
            detachedCriteria.add(Restrictions.eq("issuerOfAccessionNumber", accessionNumberIssuer));
        }
        if (patientID != null && StringUtils.isNotBlank(patientID)) {
            detachedCriteria.add(Restrictions.eq("patientID", patientID));
        }
        if (patientIDIssuer != null && StringUtils.isNotBlank(patientIDIssuer)) {
            detachedCriteria.add(Restrictions.eq("issuerOfPatientID", patientIDIssuer));
        }
        if (minStudyDateTime != null) {
            detachedCriteria.add(Restrictions.ge("dateTime", minStudyDateTime));
        }
        if (minStudyDate != null) {
            detachedCriteria.add(Restrictions.ge("dateTime", minStudyDate));
        }
        if (maxStudyDateTime != null) {
            detachedCriteria.add(Restrictions.le("dateTime", maxStudyDateTime));
        }
        if (maxStudyDate != null) {
            detachedCriteria.add(Restrictions.lt("dateTime", maxStudyDate));
        }

        //Eliminate deleted studies from search results
        detachedCriteria.add(Restrictions.ne("studyVersion", -1));

        int firstResult = (offset-1) * limit;
        final List<MINTStudy> list = (List<MINTStudy>)getHibernateTemplate().findByCriteria(detachedCriteria,firstResult,limit);
        return list;
    }

	@SuppressWarnings("unchecked")
	public MINTStudy findStudy(final String uuid) {
		if (StringUtils.isNotBlank(uuid)) {
			final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MINTStudy.class).add(
					Restrictions.eq("id", uuid));
			final List<MINTStudy> list = getHibernateTemplate().findByCriteria(detachedCriteria);
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<MINTStudy> getMostRecentStudies(final int max, final int seconds) {
		final Calendar calendar = Calendar.getInstance(GMT);
		calendar.add(Calendar.SECOND, -between(1, MAXSECONDS, seconds));

		final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MINTStudy.class)
				.add(Restrictions.ge("lastModified", calendar.getTime())).addOrder(Order.desc("lastModified"));

        //Eliminate deleted studies from search results
        detachedCriteria.add(Restrictions.ne("studyVersion", "-1"));

		final List<MINTStudy> list = getHibernateTemplate().findByCriteria(detachedCriteria, 0, between(1, 50, max));
		return list;
	}

    public MINTStudy insertStudy(final MINTStudy study) {
        if (study != null) {
            study.setLastModified(MINTStudy.now());
            getHibernateTemplate().save(study);
            getHibernateTemplate().flush();
            getHibernateTemplate().refresh(study);
        }
        return study;
    }

    public MINTStudy updateStudy(final MINTStudy study) {
        if (study != null) {
            study.setLastModified(MINTStudy.now());
            getHibernateTemplate().saveOrUpdate(study);
            getHibernateTemplate().flush();
            getHibernateTemplate().refresh(study);
        }
        return study;
    }
}
