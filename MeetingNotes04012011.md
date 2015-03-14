# Attendees #
  * Tim Dawson
  * Tim Culp
  * Uli Bubenheimer
  * Gorkem Sevinc
  * Jim Philbin
  * Dan Chaffee
  * Rafael Ming

# Progress Since Last Meeting #
> Aunt Minnie article!!  http://www.auntminnie.com/index.aspx?d=1&sec=sup&sub=pac&pag=dis&ItemID=94794&wf=4294

  * Tim Dawson
    * In the home stretch for VitreaView our MINT-based viewer
  * Jim Philbin
    * Work item was approved two weeks ago by WG27
    * 2 weeks from now it will be voted on in Pisa by the DICOM committee - cast your votes if you have them!
    * Some lively discussion - polarized groups that think this is very important, others seem to think its redundant.  Phillips is onboard.
    * Then there will be the work to create what we have and write up the supplement / documentation in DICOM format
  * Tim Culp
    * integration test framework is up, automated build and test cases every night. testing dicom->mint and mint->dicom, running 10 verificatino test cases
    * currently at the limits of his virtual environment, will have more disk space soon
    * existing DICOM study conformance tools use up a lot of disk space decomposing and storing in temporary files. 2GB study would blow up to 8GB. now it is using filename + offset. (may be an issue with multiframe but will cross that bridge when we come to it)
    * looking to do proxy voting for WG27 item for Pisa meeting
  * Gorkem Sevinc
    * Hasn't been able to work on binary w/o multipart separators
    * Need some cleanup on the subversion code. Would like to create separate MINT JavaSDK into Mint Client & Server SDKs.

# Discussion #
  * Proposal - top level SDK directory, Java/C++ subdirectories. top level directories SDK, Clients, Conformance, Utilities.  (accepted)
  * Proposal - MINT should only accept GMT times.  Any non-GMT time should return a 4XX error code indicating that time must be in GMT. (accepted)
  * Proposal - MINT should return 204 No Content when changelog "since" future time (accepted)
  * Proposal - add binary representation for floating point at the instance level, keep text version as well. (accepted)

= Outstanding items (need further writeup)
  * for GMT handling Metadata should be treated separately
  * disallow OB/OW/OF/UN tags at study and series level due to lack of transfer syntax. no objections but to formally accept this need updates to the normalization document with explanations & notifications.  need to consider possible thumbnails at the series level.
  * disallow use of floating point for normalization, study/series level
  * should we require explicit little endian?  If so SDK should include DICOM/MINT conversion to MINT standard format (including explicit little endian) - both metadata and binary items
  * need to allow any incoming DICOM via SCP or part 10 import, but should require strict conformance to MINT standards when receiving data in MINT format.

# Discussion from last time #
  * Gorkem asked about whether or not we could add an alternative batch binary mechanism that returns a binary blob with offsets + binary items in addition to the multi-part mime mechanism.  Everyone OK with this
    * Use a custom mime-type and pass that to binaryitems/all - server will detect and return alternative representation
    * Gorkem to prototype and report back with results

  * Gorkem asked how the study summary is defined
    * Implicit logic - uses study and series level attributes defined in data dictionary

# Decisions #

# Issues raise @ WG27 meeting #

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