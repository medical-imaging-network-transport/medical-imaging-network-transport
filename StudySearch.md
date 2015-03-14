# Introduction #

The purpose of the study search resource is to support a basic searching facility to find a study.


# URL #

$MINTROOT/studies

# Search Parameters #

Search parameters are passed as URL query strings.  Only studies which match all search parameters are returned (logical AND).  The search parameters are:

  * studyInstanceUID - Search by study instance uid
  * patientId - Search by patient ID.  Can optionally specify the patientIdIssuer
  * accessionNumber - search by accession number.  Can optionally specify the accessionNumberIssuer
  * studyDateTimeFrom/studyDateTimeTo - DateTime range to search on
  * pageSize - number of studies to return per query


# Search Results #

For each study matching the query, the following is returned:
  * studyUUID
  * lastUpdateDateTime - the DateTime the study was last updated

The following XSD defines the search results:

<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
> 

&lt;xsd:element name="Studies"&gt;


> > 

&lt;xsd:complexType&gt;


> > > 

&lt;xsd:sequence&gt;


> > > > 

&lt;xsd:element name="Study" maxOccurs="unbounded" minOccurs="0"&gt;


> > > > > 

&lt;xsd:complexType&gt;


> > > > > > 

&lt;xsd:sequence&gt;


> > > > > > > 

&lt;xsd:element name="studyUUID"&gt;


> > > > > > > > 

&lt;xsd:complexType&gt;


> > > > > > > > > 

&lt;xsd:attribute name="value" type="xsd:string"/&gt;



> > > > > > > > 

&lt;/xsd:complexType&gt;



> > > > > > > 

&lt;/xsd:element&gt;


> > > > > > > 

&lt;xsd:element name="lastUpdateDateTime"&gt;


> > > > > > > > 

&lt;xsd:complexType&gt;


> > > > > > > > > 

&lt;xsd:attribute name="value" type="xsd:dateTime"/&gt;



> > > > > > > > 

&lt;/xsd:complexType&gt;



> > > > > > > 

&lt;/xsd:element&gt;



> > > > > > 

&lt;/xsd:sequence&gt;



> > > > > 

&lt;/xsd:complexType&gt;



> > > > 

&lt;/xsd:element&gt;



> > > 

&lt;/xsd:sequence&gt;



> > 

&lt;/xsd:complexType&gt;



> 

&lt;/xsd:element&gt;




Unknown end tag for &lt;/schema&gt;



Sample XML:

<?xml version="1.0" encoding="utf-8"?>


&lt;studies&gt;


> 

&lt;study&gt;


> > 

&lt;studyUUID value="560e1d6d-6b0b-4351-8430-a44c278ed345"/&gt;


> > 

&lt;lastUpdateDateTime value="20100817141900"/&gt;



> 

&lt;/study&gt;


> 

&lt;study&gt;


> > 

&lt;studyUUID value="cddf6a80-862d-4759-b63c-90c6c6cf4692"/&gt;


> > 

&lt;lastUpdateDateTime value="20100817141900"/&gt;



> 

&lt;/study&gt;




&lt;/studies&gt;

