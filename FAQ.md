
```
<?xml version="1.0" encoding="UTF-8"?>
<metadata type="ExternalData" version="1.0"
    xmlns="http://medical.nema.org/mint"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://medical.nema.org/mint http://medical-imaging-network-transport.googlecode.com/files/mint-datadictionary.xsd">
    <attributes unknown-attributes="reject">
        <element tag="0" keyword="Data">External Data</element>
    </attributes>
    <study-attributes>
        <attribute tag='0' desc="Data"/>
    </study-attributes>
    <series-attributes>
    </series-attributes>
</metadata>
```

  * Q: What would a metadata document look like for the `ExternalData` type above?
  * A: Here is an example for a study with study instance UID 1.3.51.0.1.8.20021007174143.10. Remember that as a first step a DICOM metadata document with this study instance UID must be uploaded, before an `ExternalData` document can be added to the same study as part of an update.
```
<?xml version="1.0" encoding="UTF-8"?>
<study xmlns="http://medical.nema.org/mint" studyInstanceUID="1.3.51.0.1.8.20021007174143.10" type="ExternalData">
    <attributes>
        <attr tag="0" bid="0"/>
    </attributes>
    <seriesList>
    </seriesList>
</study>
```