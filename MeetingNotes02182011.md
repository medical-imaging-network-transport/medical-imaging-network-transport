# Attendees #
  * Tim Dawson
  * Gorkem Sevinc
  * Jim Philbin
  * Tim Culp
  * Dan Chaffee
  * Anasuya Basu

# Progress Since Last Meeting #
  * Jim Philbin
    * WG27 meeting scheduled @ HIMS
    * Chris Hafey to present REST concepts
    * present proposal to incorporate with WADO
    * writing introduction to MINT, will share
  * Gorkem Sevinc
    * fixed MINT2DICOM to ignore tags ending in 0000
    * raised question about PUT for creating study
  * Anasuya Basu
    * writing unit tests on C++ implementation
  * Tim Culp
    * will be attending HIMSS next week; doing MINT demo @ booth
    * cloud-based image processing demo
    * Aspera FASP UDP-based protocol to speed data transfer
    * DICOM study comparison tool updates
    * exclusion flag to ignore specific tags, regex
    * WARN rather than ERROR for trailing characters in text strings
  * Dan Chaffe
    * No Updates
  * Tim Dawson
    * Uli did a code review and fixed defects in normalization
    * updated RI to allow multiple transfer syntaxes
    * options for handling for OB, OW, UN, and SQ at study/series level
      * do not allow
      * always little endian
      * add transferSyntax option to attribute
    * changelog currently has remoteUser and principal fields
    * binary item sizes limited to 2GB
      * RI uses an int for this, s/b long
      * pathology slides may be larger than this

# Decisions #
  * for floating-point, in addition to value="", use bytes="" with base-64 encoded - transfer syntax TBD
  * for now specification will not handle individual attributes >2GB
  * remove 'principal' from changelog, it is redundant to remoteUser

# New Actions #
  * look into using PUT for study update (investigate multi-part MIME with PUT)
  * schedule meeting at SIIM to discuss 2.0 version of protocol
  * support for move/copy operations as forms of post operations
  * need to discuss binary and float handling with DICOM vendors at HIMSS (Jim)
  * need to reach out to DCM4CHE re: 2GB limit (Chris/Jim)
  * Tim C to send Dan a pathology image to test with DCM4CHE

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