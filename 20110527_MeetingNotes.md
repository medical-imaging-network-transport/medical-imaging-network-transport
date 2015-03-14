# Attendees #
  * Gorkem Sevinc
  * Tim Culp
  * Lauren Burrell
  * Tim Dawson

# Agenda #
  * status updates from individuals on the call
  * update schedule of calls
  * finalize MINT Meeting @ SIIM (breakfast Friday?)
  * conformance testing issue - use of RI-specific storage vs. REST call
  * any further discussion of binary data upload part naming

# Discussion #
  * Tim Culp / Lauren Burrell
    * Epsilon check on conformance tests, ready not checked in.
    * Reorganized conformance test outputs
    * MINT benchmarking for SIIM - getting setup @ Hopkins
      * gpb vs. xml
      * parsing MINT vs. DICOM
      * time to first image, time to last image
      * network simulator available, but not ready yet
  * Gorkem
    * No updates, requested help from Vital (will reach out to Uli)
    * Jim is documenting a one-pass algorithm for normalization
    * Waiting to hear back from Jim for whitepaper
  * Tim
    * Vital is wrapping regulatory documents on VitreaView release, will show @ SIIM
    * Looking to restructure reference implementation to add storage API to simplify changes
      * may donate Vital implementation as example

  * Conformance test
    * conformance test has been updated to use http/rest call; Vital was using an oldversion

# Decisions #
  * Move MINT meeting time to 12 EST so people on west coast can call in (and allow use of VITAL's webex)
  * tentative date/time for MINT meeting @ SIIM Saturday morning, Jim will send an agenda

# Old Actions #
  * Setup new WebEx for MINT for 12 EST (Tim Dawson)
  * Coordinate SIIM event (Jim)
  * Reach out to Dejarnette to discuss fixup rules for implicit to explicit transfer syntaxes (Gorkem)
  * Reach out to Acuo to discuss fixup rules for implicit to explicit transfer syntaxes (Chris)