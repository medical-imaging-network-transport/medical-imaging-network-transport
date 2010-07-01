package org.nema.medical.mint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.nema.medical.mint.InboundStudyMap.StudyInfo;
import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder;
import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder.MetaBinaryPair;
import org.springframework.beans.factory.annotation.Autowired;

final class ProcessStudyFiles {

    static final class StudyInstances {
        String studyUID;
        Collection<File> instances;
    }

    public Collection<MetaBinaryPair> run() {
        final Collection<MetaBinaryPair> returnCollection = new ArrayList<MetaBinaryPair>();
        for (final Entry<String, StudyInfo> entry: inboundStudyMap.map.entrySet()) {
            final String studyUID = entry.getKey();
            final StudyInfo studyInfo = entry.getValue();
            if (System.currentTimeMillis() - studyInfo.startTime.getTime() >= 3000) {
                final StudyInstances studyInstances = new StudyInstances();
                studyInstances.studyUID = studyUID;
                studyInstances.instances = studyInfo.sopInstanceFiles;
                Logger.getLogger(this.getClass()).debug("Found " + studyInstances.instances.size() + " instances in map for study " + studyUID);
                final MetaBinaryPair processResult = process(studyInstances);
                returnCollection.add(processResult);
            }
        }
        inboundStudyMap.map.clear();
        return returnCollection;
    }

    private MetaBinaryPair process(final StudyInstances item) {
        final String studyUID = item.studyUID;
        assert studyUID != null;

        final Set<Integer> studyLevelTags = getTags("StudyTags.txt");
        final Set<Integer> seriesLevelTags = getTags("SeriesTags.txt");
        final Dcm2MetaBuilder builder = new Dcm2MetaBuilder(studyLevelTags, seriesLevelTags, studyUID);
        for (final File instanceFile: item.instances) {
            builder.accumulateFile(instanceFile);
        }
        final MetaBinaryPair metaBinaryPair = builder.finish();
        return metaBinaryPair;
    }

    private Set<Integer> getTags(final String resource) {
        final ClassLoader loader = this.getClass().getClassLoader();
        final Properties properties = new Properties();
        try {
            InputStream stream = loader.getResourceAsStream(resource);
            try {
                properties.load(stream);
            } finally {
                stream.close();
            }
        } catch(final IOException ex) {
            Logger.getLogger(this.getClass()).error("Unable to read tags file", ex);
        }
        final Set<Integer> tagSet = new HashSet<Integer>();
        for (final Object tagStr: properties.keySet()) {
            //Go to long as int is unsigned and insufficient here
            final int intTag = (int)Long.parseLong(tagStr.toString(), 16);
            tagSet.add(intTag);
        }
        return tagSet;
    }

    @Autowired
    InboundStudyMap inboundStudyMap;

}
