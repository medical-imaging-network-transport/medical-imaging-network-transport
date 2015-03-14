# Attendees #
  * Tim Dawson
  * Tim Culp
  * Gorkem Sevinc
  * Anasuya Basu
  * Jim Philbin

# Progress Since Last Meeting #
  * Tim Dawson
    * Uli is removing 'principal' from changelog
    * Also creating separation between Controller layer and storage in RI
    * Looked into POST vs. PUT - Jim agrees we should keep as-is
  * Tim Culp
    * HIMSS went well; demoed MINT load & update
    * DICOM WG meeting, garnered some interest
    * Demoed TCP accelerator from Aspera Fasp
    * Continued analysis of data sets
    * Updates to conformance tools, defect with sequence records
    * Identified defects in DICOM-MINT, see TESTUS.zip
  * Jim Philbin
    * WG27 meeting @ HIMS, Chris' REST presentation went well
    * Meeting was well attended
    * Agreed to create a work item for MINT, get accepted by DICOM committee, then into fasttrack review/update/vote process
    * Work item is in process, finalize by March WG meeting
    * Voted on the week of April 12th
    * Philips is totally on board
    * Our chances look good

# Decisions #
  * spec should identify that the first item in a create/update POST is metadata and filename must be one of {metadata.xml, metadata.xml.gz, metadata.gpb, metadata.gpb.gz}
  * spec should not rely on sequential binary item ids starting at zero for each update, but should use unique BIDs and filenames on upload should match

# Issues raise @ WG27 meeting #
  * we should have a page that identifies which services are available via WADO
    * e.g. explicit little-endian, jpeg compression transfer syntax

# Old Actions #
  * schedule meeting at SIIM to discuss 2.0 version of protocol
  * support for move/copy operations as forms of post operations
  * need to discuss binary and float handling with DICOM vendors at HIMSS (Jim)
  * need to reach out to DCM4CHE re: 2GB limit (Chris/Jim)
  * Tim C to send Dan a pathology image to test with DCM4CHE
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