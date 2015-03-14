# Attendees #
  * Jim Philbin
  * Gorkem Sevinc
  * Anna
  * Rafael
  * Sam
  * Mahmaud
  * Tim Dawson
  * Uli Bubenheimer
  * Dan Chaffee

# Agenda #
  * WG-6/WG-27 recap
  * Changes needed for MINT 2.0
  * MINT study compare
  * Status Updates

# WG6/WG-27 recap #
  * Joint meeting with WG-6 DICOM Architecture/Technical workgroup, WG-27 web technology workgroup
  * Jim presented the problem space, what MINT is trying to do
  * Started out fairly negative but did come up with some possible solutions.
    * David Clunie suggested an approach to use multiframe with secondary capture
  * We will create a work item to do study-level transport of CT/MR/PET
  * In WG-27, agreed to bring RESTful WADO up to level of SOAP WADO
    * new work item for web version of CSTORE
    * no work item for update at this point, need to work on this
  * WG-6 pushing back strongly on REST, need to define reasons why this is absolutely needed
  * Basic summary: made headway, need to work in small steps, built bridges with the WG-6 that we will work with them in a collaborative fashion

# Changes needed for MINT 2.0 #
  * REST changes (including changing or eliminating the 'jobs' interface)
    * Vital is working on a proposal for MINT resource changes
    * JHU is also working on a document
    * great topic for next meeting
  * Need to improve our documentation (and marketing) to eliminate FUD and misconceptions
    * Whitepaper (Jim)
    * Performance Report (Gorkem, Lauren)
    * Updates to ICD based on MINT 2.0 (Dan Chaffee)
    * SDK Documentation (Gorkem)
    * Reference Manual (for use of the reference impl? Jim needs to clarify)
    * Architecture Document (Tim Dawson, start with Chris' MINT technical overview)
    * README files to each top level project within codebase (Gorkem)
    * JavaDoc / DOxygen (all committers)
  * Making XML tags within Metadata optional
    * all tags are currently required whether they have content or not
      * this affects unit tests - need to create empty tags, e.g. 

&lt;normalizedInstanceAttributes/&gt;


      * when removing something with exclusion tag, but still have to have empty tags
    * recommendation is to allow any tag to be optional except top level study
    * no opposition, approved
      * Sam to make this change to XSD, JiBX, Uli will review, Dan will update ICD
  * Server Change Log
    * currently 100 updates to a study yield 100 entries in the server change log
      * typical use case is for synchronization/updating between servers
    * recommendation is to consolidate changes for a given study when viewing the server-level change log
    * study-level change log would still contain all changes
    * do we need the ability to get **all** changes at server level?
      * not a lot of strong use cases, possibly audit?
      * cost of getting full changes by repeatedly calling study change log would be high
    * recommendation is to add a "consolidate" parameter to server-level and default to 'true'.  if true only the most recent change for a study would be included in the log. if false, current behavior
      * Dan will update ICD
      * Sam will update reference impl, send to Uli for code review
  * Binary data upload part naming
    * defer to next meeting
  * Client SDK
    * H2MI has implemented something, needs permission to release (Gorkem)
  * Permanent vs. Temporary Updates & Types
    * defer to next meeting
  * Error Handling (and inclusion within metadata)
    * introduced the topic (issues when converting from DICOM)
    * defer to next meeting
  * Ability to make single-call pull of metadata + binary
    * defer to next meeting

  * Ability to request binary response with offsets instead of multipart-mime
    * Performance test to compare and validate before finalizing change
    * defer to next meeting

  * Reference Impl changes (Storage API)
    * defer to next meeting

# Discussion #
  * Jim
    * Sam will be working with Uli and others on the reference impl
    * Mahmaud will be working with DCM4CHE to create study-level multiframe
    * Need to use this approach to build credible benchmarks