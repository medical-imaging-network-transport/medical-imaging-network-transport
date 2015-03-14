# Attendees #
  * Tony O'Sullivan, IBIS
  * Raj Krish, IBIS
  * Tim Dawson, Vital
  * Uli Bubenheimer, Vital
  * Tim Culp, Harris
  * Tricia Hess, Harris
  * Dan Chaffee, Harris
  * Jim Philbin, Peake

# Agenda #
  * Welcome IBIS
  * Other topics
  * MINT 2.0 api

# Welcome IBIS #
  * system primarily focused on clinical research
  * currently storing data, metadata in Oracle 11g
  * have their own normalization mechanism
  * interested in implementing MINT

# Other topics #
  * WG27 RSNA meeting notes - Tim Culp, planned by end of week

# MINT 2.0 api #
  * discussed whether image rendering (part of WADO) needs to be part of MINT as well
  * decided to defer this from MINT 2.0 will discuss at a later date
  * need to see WADO XML; Tim C will send Lawrence Tarbox some sample data and will get WADO xml back for comparison purposes
  * need to find a viewer implementation interested in new series/instance based URIs before finalizing this piece of the API
  * reviewed API marked 'draft final' during last meeting
    * existing MINT functions w/ new URIs
    * new data storage APIs
  * reviewed/updated DICOM-oriented APIs
    * Get Series
    * Get Instances
    * Get Instance
    * Get Image