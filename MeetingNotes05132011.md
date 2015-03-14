# Attendees #
  * Gorkem Sevinc
  * Jim Philbin
  * Anna
  * Tim Dawson
  * Uli Bubbenheimer
  * Rafael
  * Tim Culp
  * Dan Chaffee


# Discussion #
  * Jim Philbin
    * Joint WG Meeting between WG6 and WG27 on Monday June 6 @ 8AM
    * Working on a study oriented message
    * Discussion about MINT vs DICOM Performance.  Concern expressed about showing any DICOM numbers because it varies so much and someone will always call foul.  It does make sense to show what kind of performance Vital Images is getting with MINT as that is not arguable.
    * Suggest we have a breakfast meeting at SIIM - 7AM on Friday June 3rd?.
      * SIIM Attendees: Tim D, Jim P, Tim C, Gorkem
  * Tim Dawson
    * Need to address obvious objections for vendors that don't have strong motivations to add support for MINT
    * Discussion that some vendors may not be motivated to adopt MINT (not because MINT is bad or wrong, just for other reasons)
    * Need to focus on addressing misperceptions and misinformation
    * Wondering if we should push the time back so west coast can join more easily
  * Tim Culp
    * Had a conference call with Dee Csipo yesterday
      * Didn't understand the use cases
      * He submitted the work item for QIDO, was concerned that no progress would be made on it because of MINT
    * Submitted a change for handling repeating binary tags (OB,OW, UN can have undefined lengths)
  * Gorkem
    * Been working on white paper with Jim and one pass normalization algorithm
  * Uli
    * Little endian implicit transfer syntax problem that Jason Nye posted about a few weeks ago.  MINT will select VR's for some tags that may not be right (because the right one depends on the context of the instance).  For example - pixel data can be OB (8 bit) or OW (16 bit) depending upon another attributes
      * Discussion that it would be bad to not support implicit transfer syntaxes
      * Discussion that we could just put the possible values of the VR into the metadata
    * Need some clarification on change related to binary data items in uploads
      * Unclear why this change was made?
      * Are we intending for these binary items to come in arbitrary order or if they must be sequential
      * Gorkem says that the binary items could be sent in an order different than they appear in the metadata.


# Decisions #
  * Move MINT meeting time to 11 EST so people on west coast can call in

# New Actions #
  * Setup new WebEx for MINT for 11 EST (Tim Dawson)
  * Coordinate SIIM event (Jim)
  * Reach out to Dejarnette to discuss fixup rules for implicit to explicit transfer syntaxes (Gorkem)
  * Reach out to Acuo to discuss fixup rules for implicit to explicit transfer syntaxes (Chris)


# Old Actions #
  * Update metadata document to match RI + decisions (Jim)
  * Write up some issues/thoughts related to validation and post to group (Dan)

  * Revisit OB/OW/OF/UN tags at study/series levels as part of MINT/WG27 activities (Jim/Chris)
    * Revisit binary floating point tags at study/series levels as part of MINT/WG27 activities (Jim/Chris)
    * Revisit data dictionary design as part of DICOM/WG27 work (Chris/Jim)
    * Explore deprecating implicit transfer syntax support in MINT via DICOM/WG27 work (Chris/Jim)
    * Investigate how the MINT RI handles valdiation errors for insert and update operations and report back to next weeks meeting (Dan)
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