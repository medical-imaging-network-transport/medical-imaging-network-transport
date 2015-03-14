# Attendees #
  * Tim Culp
  * Dan Chaffee
  * Tony O'Sullivan IBISWORKS.COM
  * Uli Bubenheimer
  * Jennifer Tucker Vanderlbilt University Medical Center
  * Gorkem Sevinc
  * Anna
  * Rafael Ning
  * Tim Dawson

# Progress Since Last Meeting #
  * Chris Hafey
    * DICOM Standards Committee reviewed the WG27 proposal for MINT like work item and voted it down 8-5
    * WG27 Met on Wednesday April 13 to discuss
      * Just because it was voted down does not mean this is not wanted or useful
      * DSC prefers to work on small sets of functionality - MINT is too big
      * DSC spent 2 hours debating work item proposal.  WG27 spent 2 hours discussing the vote decision and what to do next
  * Tim Culp
    * WG27 Meeting
      * Main reason it was rejected was because we had too much in the proposal - suggested we do it incrementally
      * They are looking for some kind of white paper
      * Want it to be more evolutionary instead of revolutionary
      * Everyone was impressed with the design and believe that web technologies are important and should be embraced
      * Asked for help from Kevin O'Donnel (Toshiba) and others that are more familiar with how to get things done with DSC to help
    * Spent a little time on switching the dicom conformance test tool to use the MINT Data dictionary instead of its own
      * Discovered our MINT Data dictionary was a bit out of date with the dcm4che version
    * Uli's changes for handling floating point data properly triggered conformance test failures - this is a good thing!
  * Gorkem
    * Issue with binary item naming: some parts of the reference implementation do not use this?  Need to define what name we use to identify the binary item id.
      * proposal - Multi-part filename should match binary item id
  * Tim Dawson
    * Uli has made the changes discussed last time.
    * Uli has been fixing some defects based on feedback from Vital Images testing
    * Changelog currently lists results in reverse chronological order - which is great for humans but not machines.  Should be in chronological order so the changes could be grabbed a few at a time (rather than the whole thing in one batch).  After thinking through this with Uli, it was clear that both use cases are valuable.
      * Proposal: Add parameter to changelog which specifies order (ascending or descending)
        * Challenge - do we need to support both use cases?  Which is more important?
        * Original goal of changelog was to support synchronization so that is probably more important
  * Uli Bubbenheimer
    * Has created a branch already for 1.0.x bug fixes.  Everyone else should stay on trunk
  * Dan Chaffe
    * Going to be working out the spec for MINT over the next two weeks

# Discussion #
  * for GMT handling Metadata should be treated separately
    * we are using this for changelog, but metadata is still using DICOM DT/TM VRs
    * Changelog search parameters would be done in GMT


= Outstanding items (need further discussion)
  * disallow OB/OW/OF/UN tags at study and series level due to lack of transfer syntax. no objections but to formally accept this need updates to the normalization document with explanations & notifications.  need to consider possible thumbnails at the series level.
  * disallow use of floating point for normalization, study/series level
  * should we require explicit little endian?  If so SDK should include DICOM/MINT conversion to MINT standard format (including explicit little endian) - both metadata and binary items
  * need to allow any incoming DICOM via SCP or part 10 import, but should require strict conformance to MINT standards when receiving data in MINT format.



# Decisions #
  * Multi-part field name will match the binary item id on upload
  * Changelog will support an optional query parameter "order" with values either "asc" or "desc" for ascending and descending.  Asc = chronological order.  If query parameter is not specified, server will default to "desc"
  * Changelog search parameter "since" and "until" shall be in GMT with a timezone

# New Actions #
  * DICOM2MINT conversion should be updated so it handles the decision for how to name binary items with multi-part mime filename [Uli](Uli.md)
  * Schedule a meeting next Friday to deal with the backlog items [Chris](Chris.md)


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