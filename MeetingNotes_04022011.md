# Attendees #
  * Tim Culp
  * Tim Dawson
  * Gorkem Sevinc
  * Jim Philbin
  * Dan Chaffee

# Discussion #
  * disallow OB/OW/OF/UN tags at study and series level due to lack of transfer syntax. no objections but to formally accept this need updates to the normalization document with explanations & notifications.  need to consider possible thumbnails at the series level.
    * Options:
      * Encode the byte ordering on a per item basis
      * Specify the byte ordering for study/series level items (i.e. little endian)
      * Encode the byte ordering for study/series level items as a study level tag
  * disallow use of floating point for normalization, study/series level
    * Uli's changes resulted in bloat to the metadata for some studies.  A lot of floating point numbers are "0.0".
      * Tim Culp suggested that we investigate to see how often floating point numbers are non zero (if this is common - perhaps we can do a special case optimization)
  * should we require use of non implicit transfer syntaxes?  If so SDK should include DICOM/MINT conversion to MINT standard format (including explicit little endian) - both metadata and binary items
    * Question about whether or not data dictionary is "customizable" by end users
    * There is an issue related to versioning of the data dictionary - what happens to existing data if someone adds/deletes tags to the study/series level - it won't match up!
  * need to allow any incoming DICOM via SCP or part 10 import, but should require strict conformance to MINT standards when receiving data in MINT format.
    * Big discussion about where validation should happen
      * Jim, Tim, Tim and Dan think the mint server should be able to do some form of content validation and reject requests that don't conform
      * Chris thinks the server should not do content level validation, just structural
    * Idea of the need for a "reference architecture" that shows MINT in context is needed to help understand this validation issue

# Decision #
  * Disallow OB/OW/OF/UN tags at study/series level
  * Disallow use of floating point for normalization, study/series level
  * Encode floating point as text and binary (not just text).  This avoids any loss due to binary->text conversion
  * Once you go to production with MINT, do not change any tags in the study/series level

# Decisions #

# New Actions #
  * Revisit OB/OW/OF/UN tags at study/series levels as part of MINT/WG27 activities (Jim/Chris)
  * Revisit binary floating point tags at study/series levels as part of MINT/WG27 activities (Jim/Chris)
  * Revisit data dictionary design as part of DICOM/WG27 work (Chris/Jim)
  * Explore deprecating implicit transfer syntax support in MINT via DICOM/WG27 work (Chris/Jim)
  * Investigate how the MINT RI handles valdiation errors for insert and update operations and report back to next weeks meeting (Dan)

# Old Actions #
  * DICOM2MINT conversion should be updated so it handles the decision for how to name binary items with multi-part mime filename [Uli](Uli.md)
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