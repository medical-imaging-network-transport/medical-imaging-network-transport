# Introduction #

Rules that may be applied to MINT data to validate it.

# Details #

Items below that have been implemented so far have been implemented in the StudyUtil class in MINTCommon.

## Binary Item Reference Validation ##
Bid references in metadata and binary items must agree exactly.  If there are any binary items that are not referenced in the metadata or the metadata references binary items that do not exist, this is an invalid request and should be denied.
  * Checked on request for study create by MINTServer
  * Checked on request for study update by MINTServer
This functionality has been implemented.