# Attendees #
  * Tim Culp
  * Dan Chaffee
  * Gorkem Sevinc
  * Chris Hafey
  * Tim Dawson

# Progress Since Last Meeting #
  * Chris Hafey
    * Talked with Vital Images marketing about providing some help with the MINT landing page, in progress
  * Gorkem Sevinc
    * No updates
    * Sent GUI code to Tim Dawson
  * Tim Culp
    * Working on end to end integration testing
    * Conformance test used to be file system based
      * It has now been enhanced to work with the MINT Server
    * Went through MINT2DICOM and DICOM2MINT to make sure exceptions thrown were caught in main so error codes are properly returned
    * MINTDICOMCOMPARE and MINTSTUDYCOMPARE can generate output for diff purposes
    * New tool DICOMStudyCompare that compares the contents of two folders (each with the same study) to make sure they match
    * Had discussions with Jeremey on a test framework
  * Dan Chaffee
    * No code updates
    * Did some research on security
      * Would like to collaborate with Vital on security cookbook
  * Tim Dawson
    * Uli made some minor fixes

# Notes #
  * MINT2DICOM not generation 0002,0000 but this is T1.  Tim Culp to send email to Uli, Jeremey, Damien on this

# Study Delete Discussion #
  * MINT Client needs someway to know when a study is deleted from the archive
  * Can use HTTP DELETE to delete a study
  * Need to indicate the delete in the changelog
  * Should the RI update its database saying it has been deleted or actually delete the row?  Probably keep it so we can return HTTP response code 410 gone
  * Could add attribute to changelog to indicate "created", "updated" and "deleted"
  * 

# Decisions #
  * Add attribute "operation" to changelog change element with the possible values "created", "updated" and "deleted"

# New Actions #
  * 

# Old Actions #
  * Send out email to list to propose meeting to brainstorm MINT 2011 roadmap (Chris)
  * Modify metadata version number as decided above (Dan Chaffee)
  * Review Gorkem's setup document and provide feedback about what needs to be done for version 1.0 "get started" doc (all)
  * Design "First Experience" and send to mail list (Gorkem)
  * Use cases for proprietary data (Jim)
  * Use cases for de-id (Jim)
  * Update clear canvas to support Multi-frame MINT (Tim Culp)
  * Update conformance test to support Multi-frame (Tim Culp)
  * Recipe/cookbook for deploying MINT securely (Chris Hafey + Tim Culp)
  * Specify MINT standard on Wiki (Chris Hafey + All)
    * Error codes
    * URL
    * XSD
    * Validation rules
    * Update the MINT metadata document with which VRs are candidates for binary items and the option to encode in base 64 and version number and type for study meta (Chris Hafey)
  * Add unit tests for key classes and add junit to ant to produce code coverage (Jeremy)
  * Performance cookbook (Gorkem + Chris + All)
  * Develop integration tests (Jim + Tim Culp)
  * Add support for non DICOM types (Chris)
    * Data dictionaries for Vital Volumes
    * Data dictionary for generic mime type attachments
    * Root study URL to list all types in study
  * Measure performance of MINT vs DICOM as per PPT (Gorkem)
  * Post to ClearCanvas forums, get help with packaging (Gorkem)


# Completed Actions #

# Backlog Items #
  * Look at QC workflow in more detail - specifically about metadata validation errors (does not conform to data dictionary)
  * Add mint server validation



# Issues #

# Future #