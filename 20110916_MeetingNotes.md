# Agenda #

  * Data dictionary
  * MINT 2.0 REST API change discussion
  * DICOM meeting Monday/Tuesday

# Discussion #

  * PatientIdIssuer, Institution both should be normalized to study level

  * MINT 2.0 REST API
    * decision to keep XSL stylesheet in XML headers to continue to support easy debugging. request to require accept header of text/html was problematic due to insufficient browser support
    * issue with multi-part and accept header - unclear what a caller might be looking for; need to resolve
    * need to consider whether MINT data pull at series/instance level should include metadata as first time in multipart
    * question about whether URl needed series uid & instance UID or just instance UID alone; decided that due to normalization routine they would be merged anyway, so we can live with the shorter URL with just instanceUID
    * additional data types still require more discussion
    * updated proposal file see http://code.google.com/p/medical-imaging-network-transport/downloads/detail?name=MINT%20V2%200%20Operations%20%28proposed%29.xlsx&can=2&q=

# Next meeting, Monday 9/19 in Washington DC #

  * Using DICOM as metadata format (only 20% larger than GPB and smaller than XML) - Jim
    * Enhanced multi-series DICOM