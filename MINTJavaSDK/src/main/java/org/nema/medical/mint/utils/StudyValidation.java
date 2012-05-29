package org.nema.medical.mint.utils;

import org.nema.medical.mint.datadictionary.AttributesType;
import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.datadictionary.LevelAttributes;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.metadata.StudyMetadata;

import java.util.Collection;
import java.util.HashSet;

import static org.nema.medical.mint.utils.Iter.iter;
import static org.nema.medical.mint.utils.StudyUtils.tagString;

/**
 * @author Uli Bubenheimer
 */
public class StudyValidation {
    private static final int TRANSFER_SYNTAX_TAG = 0x00020010;

    StudyValidation() {
        throw new AssertionError("Class not to be instantiated");
    }

    public static void validateStudyMetadata(final StudyMetadata study, final MetadataType type)
            throws StudyTraversals.TraversalException {
        validateTypeInfo(study, type);
        validateDICOMTransferSyntax(study);
        validateDICOMVRValueExistence(study);
    }

    private static void validateTypeInfo(final StudyMetadata study, final MetadataType type)
            throws StudyTraversals.TraversalException {
        validateUnknownAttributes(study, type);
        validateAttributeLevel(study, type);
    }

    static void validateUnknownAttributes(final StudyMetadata study, final MetadataType type)
            throws StudyTraversals.TraversalException {
        if (AttributesType.UnknownAttribute.REJECT == type.getAttributes().getUnknownAttributes()) {
            final AttributesType attributesType = type.getAttributes();
            StudyTraversals.allAttributeTraverser(study, new StudyTraversals.AttributeAction() {
                @Override
                public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                    if (!attributesType.containsElement(attribute.getTag())) {
                        throw new StudyTraversals.TraversalException("Invalid attribute "
                                + tagString(attribute.getTag()));
                    }
                }
            });
        }
    }

    static void validateAttributeLevel(final StudyMetadata study, final MetadataType type)
            throws StudyTraversals.TraversalException {
        final class LevelCheck implements StudyTraversals.AttributeAction {
            private final String levelName;
            private final LevelAttributes levelTags;
            public LevelCheck(final String levelName, final LevelAttributes levelTags) {
                this.levelName = levelName;
                this.levelTags = levelTags;
            }

            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                final int tag = attribute.getTag();
                if (!levelTags.containsTag(tag)) {
                    throw new StudyTraversals.TraversalException("Tag " + tagString(tag) + " invalid at " + levelName
                            + " level");
                }
            }
        }

        StudyTraversals.flatStudyAttributeTraverser(study, new LevelCheck("study", type.getStudyAttributes()));
        StudyTraversals.flatSeriesAttributeTraverser(study, new LevelCheck("series", type.getSeriesAttributes()));
    }

    /**
     * Validates that, for a DICOM study, all attributes have a VR value. This method must not be called for
     * metadata with a type other than DICOM, as the VR field is optional otherwise.
     * @param study the DICOM study metadata
     * @throws StudyTraversals.TraversalException if there is an attribute in the metadata without the VR value set.
     */
    static void validateDICOMVRValueExistence(final StudyMetadata study) throws StudyTraversals.TraversalException {
        StudyTraversals.allAttributeTraverser(study, new StudyTraversals.AttributeAction() {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                if (attribute.getVr() == null) {
                    throw new StudyTraversals.TraversalException("Missing VR value for Attribute \""
                            + tagString(attribute.getTag())
                            + "\" in Study " + study.getStudyInstanceUID());
                }
            }
        });
    }
    /**
     * For a DICOM study, verifies that the transferSyntaxUID value in
     * <instance sopInstanceUID="123" transferSyntaxUID="456">
     * is equal to the DICOM attribute <attr tag="00020010" vr="UI" val="456"> value. This method must not be called
     * for metadata with a type other than DICOM, as other types do not, in general, have a concept of transfer syntax.
     *
     * @param study the DICOM study metadata
     * @throws StudyTraversals.TraversalException if there is an instance with a transfer syntax mismatch.
     */
    static void validateDICOMTransferSyntax(final StudyMetadata study) throws StudyTraversals.TraversalException {
    	//loop over each series
    	for (final Series s: iter(study.seriesIterator())) {
    		final Attribute normalizedTransferSyntaxAttribute = s.getNormalizedInstanceAttribute(TRANSFER_SYNTAX_TAG);
    		final String normalizedTransferSyntax =
                    (normalizedTransferSyntaxAttribute != null) ? normalizedTransferSyntaxAttribute.getVal() : null;
    		final boolean transferSyntaxNormalized = (normalizedTransferSyntax != null);

            //loop over the instances
    		for (final Instance instance: iter(s.instanceIterator())) {
    			//This is the instance level transfer syntax element attribute
    			//<instance sopInstanceUID="123" transferSyntaxUID="456" >
    			final String instanceTransferSyntax = instance.getTransferSyntaxUID();
    			//This is the dicom tag attribute for transfer syntax
    			//<attr tag="00020010" vr="UI" val="456">
    			final String instanceAttributeTransferSyntax = transferSyntaxNormalized ?
                        normalizedTransferSyntax : instance.getValueForAttribute(TRANSFER_SYNTAX_TAG);
    			//The two different transfer syntaxes must be equal in every case or the entire study is invalid.
                //An exception is the case where no transfer syntax attribute is available on the instance.
    			if (instanceAttributeTransferSyntax != null &&
                        !instanceTransferSyntax.equalsIgnoreCase(instanceAttributeTransferSyntax)) {
    				throw new StudyTraversals.TraversalException("Mismatch of Transfer Syntax " + instanceTransferSyntax
                            + " specified for SOP Instance UID " + instance.getSOPInstanceUID()
                            + " and transfer syntax " + instanceAttributeTransferSyntax
                            + " from instance file storage attributes");
    			}
    		}
    	}
    }

    /**
     * This method will return true if the given study has passed all
     * implemented validation checks. This is validation for studies that are
     * being 'created' or 'updated' it is not expected that this validation with
     * pass on studies already written to disk.
     *
     * @param study the study
     * @param type the study's type definition
     * @param binaryItemIds the study's binary item IDs
     * @throws StudyTraversals.TraversalException if a validation error occurred
     */
    public static void validateStudy(final StudyMetadata study, final MetadataType type,
                                     final Collection<Integer> binaryItemIds)
            throws StudyTraversals.TraversalException {
        validateBinaryItemsReferences(study, binaryItemIds);
        validateStudyMetadata(study, type);
    }

    /**
     * This method will determine if there the bid references from the study to
     * binary items are all existing and that there are no excess binary items.
     * The expected usage of this method is during study create and study update
     * to ensure the passed in metadata and binary items are in agreement (i.e.,
     * no unreferenced binary items and no bids in the metadata that point to
     * nothing).
     *
     * @param study the study
     * @param binaryItemIds the study's binary item IDs
     * @throws StudyTraversals.TraversalException if a validation error occurred
     */
    public static void validateBinaryItemsReferences(final StudyMetadata study,
                                                     final Collection<Integer> binaryItemIds)
            throws StudyTraversals.TraversalException {
        final Collection<Integer> studyBids = new HashSet<Integer>();

          /*
           * Collect ids from attributes
           *
           * NOTE: StudyMetadata has a method that does almost exactly this except that
           * this method detects repeated bids (which is not allowed). Do not just
           * replace the below with that method without first considering this
           * fact.
           */

        StudyTraversals.allAttributeTraverser(study, new StudyTraversals.AttributeAction() {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                final int bid = attribute.getBid();
                if (bid >= 0) {
                    final int frameCount = attribute.getFrameCount();
                    if (frameCount >= 1) {
                        for (int newBid = bid; newBid < (bid + frameCount); newBid++) {
                            if (!studyBids.add(newBid)) {
                                //If the set already contained the bid, should be unique reference
                                throw new StudyTraversals.TraversalException("Duplicate binary ID in attribute \""
                                        + tagString(attribute.getTag()) + " of study "
                                        + study.getStudyInstanceUID());
                            }
                        }
                    } else {
                        if (!studyBids.add(bid)) {
                            //If the set already contained the bid, should be unique reference
                            throw new StudyTraversals.TraversalException("Duplicate binary ID in attribute \""
                                    + tagString(attribute.getTag()) + " of study "
                                    + study.getStudyInstanceUID());
                        }
                    }
                }
            }
        });

        //Can't use equals() here, since the collection types may be quite different
        if (studyBids.size() != binaryItemIds.size() || !studyBids.containsAll(binaryItemIds)) {
            throw new StudyTraversals.TraversalException("Mismatch of available binary data items " + binaryItemIds
                    + " and binary IDs in metadata " + studyBids + " for study " + study.getStudyInstanceUID());
        }
    }
}
