/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
// source: mint-metadata.proto

package org.nema.medical.mint.metadata.gpb;

public final class MINT2GPB {
  private MINT2GPB() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class StudyData extends
      com.google.protobuf.GeneratedMessage {
    // Use StudyData.newBuilder() to construct.
    private StudyData() {
      initFields();
    }
    private StudyData(boolean noInit) {}
    
    private static final StudyData defaultInstance;
    public static StudyData getDefaultInstance() {
      return defaultInstance;
    }
    
    public StudyData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_StudyData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_StudyData_fieldAccessorTable;
    }
    
    // optional string study_instance_uid = 1;
    public static final int STUDY_INSTANCE_UID_FIELD_NUMBER = 1;
    private boolean hasStudyInstanceUid;
    private java.lang.String studyInstanceUid_ = "";
    public boolean hasStudyInstanceUid() { return hasStudyInstanceUid; }
    public java.lang.String getStudyInstanceUid() { return studyInstanceUid_; }
    
    // repeated .mint.metadata.AttributeData attributes = 2;
    public static final int ATTRIBUTES_FIELD_NUMBER = 2;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
      return attributes_.get(index);
    }
    
    // repeated .mint.metadata.SeriesData series = 3;
    public static final int SERIES_FIELD_NUMBER = 3;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData> series_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData> getSeriesList() {
      return series_;
    }
    public int getSeriesCount() { return series_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData getSeries(int index) {
      return series_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasStudyInstanceUid()) {
        output.writeString(1, getStudyInstanceUid());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(2, element);
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData element : getSeriesList()) {
        output.writeMessage(3, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasStudyInstanceUid()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getStudyInstanceUid());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, element);
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData element : getSeriesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData result;
      
      // Construct using org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData();
        return builder;
      }
      
      protected org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData.getDescriptor();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData getDefaultInstanceForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        if (result.series_ != java.util.Collections.EMPTY_LIST) {
          result.series_ =
            java.util.Collections.unmodifiableList(result.series_);
        }
        org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData) {
          return mergeFrom((org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData other) {
        if (other == org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData.getDefaultInstance()) return this;
        if (other.hasStudyInstanceUid()) {
          setStudyInstanceUid(other.getStudyInstanceUid());
        }
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.attributes_.addAll(other.attributes_);
        }
        if (!other.series_.isEmpty()) {
          if (result.series_.isEmpty()) {
            result.series_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData>();
          }
          result.series_.addAll(other.series_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              setStudyInstanceUid(input.readString());
              break;
            }
            case 18: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
            case 26: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addSeries(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional string study_instance_uid = 1;
      public boolean hasStudyInstanceUid() {
        return result.hasStudyInstanceUid();
      }
      public java.lang.String getStudyInstanceUid() {
        return result.getStudyInstanceUid();
      }
      public Builder setStudyInstanceUid(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasStudyInstanceUid = true;
        result.studyInstanceUid_ = value;
        return this;
      }
      public Builder clearStudyInstanceUid() {
        result.hasStudyInstanceUid = false;
        result.studyInstanceUid_ = getDefaultInstance().getStudyInstanceUid();
        return this;
      }
      
      // repeated .mint.metadata.AttributeData attributes = 2;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated .mint.metadata.SeriesData series = 3;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData> getSeriesList() {
        return java.util.Collections.unmodifiableList(result.series_);
      }
      public int getSeriesCount() {
        return result.getSeriesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData getSeries(int index) {
        return result.getSeries(index);
      }
      public Builder setSeries(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.series_.set(index, value);
        return this;
      }
      public Builder setSeries(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.Builder builderForValue) {
        result.series_.set(index, builderForValue.build());
        return this;
      }
      public Builder addSeries(org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.series_.isEmpty()) {
          result.series_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData>();
        }
        result.series_.add(value);
        return this;
      }
      public Builder addSeries(org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.Builder builderForValue) {
        if (result.series_.isEmpty()) {
          result.series_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData>();
        }
        result.series_.add(builderForValue.build());
        return this;
      }
      public Builder addAllSeries(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData> values) {
        if (result.series_.isEmpty()) {
          result.series_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData>();
        }
        super.addAll(values, result.series_);
        return this;
      }
      public Builder clearSeries() {
        result.series_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:mint.metadata.StudyData)
    }
    
    static {
      defaultInstance = new StudyData(true);
      org.nema.medical.mint.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:mint.metadata.StudyData)
  }
  
  public static final class SeriesData extends
      com.google.protobuf.GeneratedMessage {
    // Use SeriesData.newBuilder() to construct.
    private SeriesData() {
      initFields();
    }
    private SeriesData(boolean noInit) {}
    
    private static final SeriesData defaultInstance;
    public static SeriesData getDefaultInstance() {
      return defaultInstance;
    }
    
    public SeriesData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_SeriesData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_SeriesData_fieldAccessorTable;
    }
    
    // optional string series_instance_uid = 1;
    public static final int SERIES_INSTANCE_UID_FIELD_NUMBER = 1;
    private boolean hasSeriesInstanceUid;
    private java.lang.String seriesInstanceUid_ = "";
    public boolean hasSeriesInstanceUid() { return hasSeriesInstanceUid; }
    public java.lang.String getSeriesInstanceUid() { return seriesInstanceUid_; }
    
    // optional string exclude = 2;
    public static final int EXCLUDE_FIELD_NUMBER = 2;
    private boolean hasExclude;
    private java.lang.String exclude_ = "";
    public boolean hasExclude() { return hasExclude; }
    public java.lang.String getExclude() { return exclude_; }
    
    // repeated .mint.metadata.AttributeData attributes = 3;
    public static final int ATTRIBUTES_FIELD_NUMBER = 3;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
      return attributes_.get(index);
    }
    
    // repeated .mint.metadata.AttributeData normalized_instance_attributes = 4;
    public static final int NORMALIZED_INSTANCE_ATTRIBUTES_FIELD_NUMBER = 4;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> normalizedInstanceAttributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getNormalizedInstanceAttributesList() {
      return normalizedInstanceAttributes_;
    }
    public int getNormalizedInstanceAttributesCount() { return normalizedInstanceAttributes_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getNormalizedInstanceAttributes(int index) {
      return normalizedInstanceAttributes_.get(index);
    }
    
    // repeated .mint.metadata.InstanceData instances = 5;
    public static final int INSTANCES_FIELD_NUMBER = 5;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData> instances_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData> getInstancesList() {
      return instances_;
    }
    public int getInstancesCount() { return instances_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData getInstances(int index) {
      return instances_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasSeriesInstanceUid()) {
        output.writeString(1, getSeriesInstanceUid());
      }
      if (hasExclude()) {
        output.writeString(2, getExclude());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(3, element);
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getNormalizedInstanceAttributesList()) {
        output.writeMessage(4, element);
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData element : getInstancesList()) {
        output.writeMessage(5, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasSeriesInstanceUid()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getSeriesInstanceUid());
      }
      if (hasExclude()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getExclude());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, element);
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getNormalizedInstanceAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, element);
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData element : getInstancesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData result;
      
      // Construct using org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData();
        return builder;
      }
      
      protected org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.getDescriptor();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData getDefaultInstanceForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        if (result.normalizedInstanceAttributes_ != java.util.Collections.EMPTY_LIST) {
          result.normalizedInstanceAttributes_ =
            java.util.Collections.unmodifiableList(result.normalizedInstanceAttributes_);
        }
        if (result.instances_ != java.util.Collections.EMPTY_LIST) {
          result.instances_ =
            java.util.Collections.unmodifiableList(result.instances_);
        }
        org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData) {
          return mergeFrom((org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData other) {
        if (other == org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.getDefaultInstance()) return this;
        if (other.hasSeriesInstanceUid()) {
          setSeriesInstanceUid(other.getSeriesInstanceUid());
        }
        if (other.hasExclude()) {
          setExclude(other.getExclude());
        }
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.attributes_.addAll(other.attributes_);
        }
        if (!other.normalizedInstanceAttributes_.isEmpty()) {
          if (result.normalizedInstanceAttributes_.isEmpty()) {
            result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.normalizedInstanceAttributes_.addAll(other.normalizedInstanceAttributes_);
        }
        if (!other.instances_.isEmpty()) {
          if (result.instances_.isEmpty()) {
            result.instances_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData>();
          }
          result.instances_.addAll(other.instances_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              setSeriesInstanceUid(input.readString());
              break;
            }
            case 18: {
              setExclude(input.readString());
              break;
            }
            case 26: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
            case 34: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addNormalizedInstanceAttributes(subBuilder.buildPartial());
              break;
            }
            case 42: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addInstances(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional string series_instance_uid = 1;
      public boolean hasSeriesInstanceUid() {
        return result.hasSeriesInstanceUid();
      }
      public java.lang.String getSeriesInstanceUid() {
        return result.getSeriesInstanceUid();
      }
      public Builder setSeriesInstanceUid(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasSeriesInstanceUid = true;
        result.seriesInstanceUid_ = value;
        return this;
      }
      public Builder clearSeriesInstanceUid() {
        result.hasSeriesInstanceUid = false;
        result.seriesInstanceUid_ = getDefaultInstance().getSeriesInstanceUid();
        return this;
      }
      
      // optional string exclude = 2;
      public boolean hasExclude() {
        return result.hasExclude();
      }
      public java.lang.String getExclude() {
        return result.getExclude();
      }
      public Builder setExclude(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasExclude = true;
        result.exclude_ = value;
        return this;
      }
      public Builder clearExclude() {
        result.hasExclude = false;
        result.exclude_ = getDefaultInstance().getExclude();
        return this;
      }
      
      // repeated .mint.metadata.AttributeData attributes = 3;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated .mint.metadata.AttributeData normalized_instance_attributes = 4;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getNormalizedInstanceAttributesList() {
        return java.util.Collections.unmodifiableList(result.normalizedInstanceAttributes_);
      }
      public int getNormalizedInstanceAttributesCount() {
        return result.getNormalizedInstanceAttributesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getNormalizedInstanceAttributes(int index) {
        return result.getNormalizedInstanceAttributes(index);
      }
      public Builder setNormalizedInstanceAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.normalizedInstanceAttributes_.set(index, value);
        return this;
      }
      public Builder setNormalizedInstanceAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.normalizedInstanceAttributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addNormalizedInstanceAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.normalizedInstanceAttributes_.isEmpty()) {
          result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.normalizedInstanceAttributes_.add(value);
        return this;
      }
      public Builder addNormalizedInstanceAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.normalizedInstanceAttributes_.isEmpty()) {
          result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.normalizedInstanceAttributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllNormalizedInstanceAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.normalizedInstanceAttributes_.isEmpty()) {
          result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.normalizedInstanceAttributes_);
        return this;
      }
      public Builder clearNormalizedInstanceAttributes() {
        result.normalizedInstanceAttributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated .mint.metadata.InstanceData instances = 5;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData> getInstancesList() {
        return java.util.Collections.unmodifiableList(result.instances_);
      }
      public int getInstancesCount() {
        return result.getInstancesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData getInstances(int index) {
        return result.getInstances(index);
      }
      public Builder setInstances(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.instances_.set(index, value);
        return this;
      }
      public Builder setInstances(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.Builder builderForValue) {
        result.instances_.set(index, builderForValue.build());
        return this;
      }
      public Builder addInstances(org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.instances_.isEmpty()) {
          result.instances_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData>();
        }
        result.instances_.add(value);
        return this;
      }
      public Builder addInstances(org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.Builder builderForValue) {
        if (result.instances_.isEmpty()) {
          result.instances_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData>();
        }
        result.instances_.add(builderForValue.build());
        return this;
      }
      public Builder addAllInstances(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData> values) {
        if (result.instances_.isEmpty()) {
          result.instances_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData>();
        }
        super.addAll(values, result.instances_);
        return this;
      }
      public Builder clearInstances() {
        result.instances_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:mint.metadata.SeriesData)
    }
    
    static {
      defaultInstance = new SeriesData(true);
      org.nema.medical.mint.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:mint.metadata.SeriesData)
  }
  
  public static final class InstanceData extends
      com.google.protobuf.GeneratedMessage {
    // Use InstanceData.newBuilder() to construct.
    private InstanceData() {
      initFields();
    }
    private InstanceData(boolean noInit) {}
    
    private static final InstanceData defaultInstance;
    public static InstanceData getDefaultInstance() {
      return defaultInstance;
    }
    
    public InstanceData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_InstanceData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_InstanceData_fieldAccessorTable;
    }
    
    // optional string sop_instance_uid = 1;
    public static final int SOP_INSTANCE_UID_FIELD_NUMBER = 1;
    private boolean hasSopInstanceUid;
    private java.lang.String sopInstanceUid_ = "";
    public boolean hasSopInstanceUid() { return hasSopInstanceUid; }
    public java.lang.String getSopInstanceUid() { return sopInstanceUid_; }
    
    // optional string exclude = 2;
    public static final int EXCLUDE_FIELD_NUMBER = 2;
    private boolean hasExclude;
    private java.lang.String exclude_ = "";
    public boolean hasExclude() { return hasExclude; }
    public java.lang.String getExclude() { return exclude_; }
    
    // optional string transfer_syntax_uid = 3;
    public static final int TRANSFER_SYNTAX_UID_FIELD_NUMBER = 3;
    private boolean hasTransferSyntaxUid;
    private java.lang.String transferSyntaxUid_ = "";
    public boolean hasTransferSyntaxUid() { return hasTransferSyntaxUid; }
    public java.lang.String getTransferSyntaxUid() { return transferSyntaxUid_; }
    
    // repeated .mint.metadata.AttributeData attributes = 4;
    public static final int ATTRIBUTES_FIELD_NUMBER = 4;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
      return attributes_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasSopInstanceUid()) {
        output.writeString(1, getSopInstanceUid());
      }
      if (hasExclude()) {
        output.writeString(2, getExclude());
      }
      if (hasTransferSyntaxUid()) {
        output.writeString(3, getTransferSyntaxUid());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(4, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasSopInstanceUid()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getSopInstanceUid());
      }
      if (hasExclude()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getExclude());
      }
      if (hasTransferSyntaxUid()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(3, getTransferSyntaxUid());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData result;
      
      // Construct using org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData();
        return builder;
      }
      
      protected org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.getDescriptor();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData getDefaultInstanceForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData) {
          return mergeFrom((org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData other) {
        if (other == org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.getDefaultInstance()) return this;
        if (other.hasSopInstanceUid()) {
          setSopInstanceUid(other.getSopInstanceUid());
        }
        if (other.hasExclude()) {
          setExclude(other.getExclude());
        }
        if (other.hasTransferSyntaxUid()) {
          setTransferSyntaxUid(other.getTransferSyntaxUid());
        }
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.attributes_.addAll(other.attributes_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              setSopInstanceUid(input.readString());
              break;
            }
            case 18: {
              setExclude(input.readString());
              break;
            }
            case 26: {
              setTransferSyntaxUid(input.readString());
              break;
            }
            case 34: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional string sop_instance_uid = 1;
      public boolean hasSopInstanceUid() {
        return result.hasSopInstanceUid();
      }
      public java.lang.String getSopInstanceUid() {
        return result.getSopInstanceUid();
      }
      public Builder setSopInstanceUid(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasSopInstanceUid = true;
        result.sopInstanceUid_ = value;
        return this;
      }
      public Builder clearSopInstanceUid() {
        result.hasSopInstanceUid = false;
        result.sopInstanceUid_ = getDefaultInstance().getSopInstanceUid();
        return this;
      }
      
      // optional string exclude = 2;
      public boolean hasExclude() {
        return result.hasExclude();
      }
      public java.lang.String getExclude() {
        return result.getExclude();
      }
      public Builder setExclude(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasExclude = true;
        result.exclude_ = value;
        return this;
      }
      public Builder clearExclude() {
        result.hasExclude = false;
        result.exclude_ = getDefaultInstance().getExclude();
        return this;
      }
      
      // optional string transfer_syntax_uid = 3;
      public boolean hasTransferSyntaxUid() {
        return result.hasTransferSyntaxUid();
      }
      public java.lang.String getTransferSyntaxUid() {
        return result.getTransferSyntaxUid();
      }
      public Builder setTransferSyntaxUid(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasTransferSyntaxUid = true;
        result.transferSyntaxUid_ = value;
        return this;
      }
      public Builder clearTransferSyntaxUid() {
        result.hasTransferSyntaxUid = false;
        result.transferSyntaxUid_ = getDefaultInstance().getTransferSyntaxUid();
        return this;
      }
      
      // repeated .mint.metadata.AttributeData attributes = 4;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:mint.metadata.InstanceData)
    }
    
    static {
      defaultInstance = new InstanceData(true);
      org.nema.medical.mint.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:mint.metadata.InstanceData)
  }
  
  public static final class AttributeData extends
      com.google.protobuf.GeneratedMessage {
    // Use AttributeData.newBuilder() to construct.
    private AttributeData() {
      initFields();
    }
    private AttributeData(boolean noInit) {}
    
    private static final AttributeData defaultInstance;
    public static AttributeData getDefaultInstance() {
      return defaultInstance;
    }
    
    public AttributeData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_AttributeData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_AttributeData_fieldAccessorTable;
    }
    
    // optional uint32 tag = 1;
    public static final int TAG_FIELD_NUMBER = 1;
    private boolean hasTag;
    private int tag_ = 0;
    public boolean hasTag() { return hasTag; }
    public int getTag() { return tag_; }
    
    // optional string exclude = 2;
    public static final int EXCLUDE_FIELD_NUMBER = 2;
    private boolean hasExclude;
    private java.lang.String exclude_ = "";
    public boolean hasExclude() { return hasExclude; }
    public java.lang.String getExclude() { return exclude_; }
    
    // optional string vr = 3;
    public static final int VR_FIELD_NUMBER = 3;
    private boolean hasVr;
    private java.lang.String vr_ = "";
    public boolean hasVr() { return hasVr; }
    public java.lang.String getVr() { return vr_; }
    
    // optional string string_value = 4;
    public static final int STRING_VALUE_FIELD_NUMBER = 4;
    private boolean hasStringValue;
    private java.lang.String stringValue_ = "";
    public boolean hasStringValue() { return hasStringValue; }
    public java.lang.String getStringValue() { return stringValue_; }
    
    // optional uint32 binary_item_id = 5;
    public static final int BINARY_ITEM_ID_FIELD_NUMBER = 5;
    private boolean hasBinaryItemId;
    private int binaryItemId_ = 0;
    public boolean hasBinaryItemId() { return hasBinaryItemId; }
    public int getBinaryItemId() { return binaryItemId_; }
    
    // repeated .mint.metadata.ItemData items = 6;
    public static final int ITEMS_FIELD_NUMBER = 6;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData> items_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData> getItemsList() {
      return items_;
    }
    public int getItemsCount() { return items_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData getItems(int index) {
      return items_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasTag()) {
        output.writeUInt32(1, getTag());
      }
      if (hasExclude()) {
        output.writeString(2, getExclude());
      }
      if (hasVr()) {
        output.writeString(3, getVr());
      }
      if (hasStringValue()) {
        output.writeString(4, getStringValue());
      }
      if (hasBinaryItemId()) {
        output.writeUInt32(5, getBinaryItemId());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData element : getItemsList()) {
        output.writeMessage(6, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasTag()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, getTag());
      }
      if (hasExclude()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getExclude());
      }
      if (hasVr()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(3, getVr());
      }
      if (hasStringValue()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(4, getStringValue());
      }
      if (hasBinaryItemId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(5, getBinaryItemId());
      }
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData element : getItemsList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData result;
      
      // Construct using org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData();
        return builder;
      }
      
      protected org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.getDescriptor();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getDefaultInstanceForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.items_ != java.util.Collections.EMPTY_LIST) {
          result.items_ =
            java.util.Collections.unmodifiableList(result.items_);
        }
        org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData) {
          return mergeFrom((org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData other) {
        if (other == org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.getDefaultInstance()) return this;
        if (other.hasTag()) {
          setTag(other.getTag());
        }
        if (other.hasExclude()) {
          setExclude(other.getExclude());
        }
        if (other.hasVr()) {
          setVr(other.getVr());
        }
        if (other.hasStringValue()) {
          setStringValue(other.getStringValue());
        }
        if (other.hasBinaryItemId()) {
          setBinaryItemId(other.getBinaryItemId());
        }
        if (!other.items_.isEmpty()) {
          if (result.items_.isEmpty()) {
            result.items_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData>();
          }
          result.items_.addAll(other.items_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setTag(input.readUInt32());
              break;
            }
            case 18: {
              setExclude(input.readString());
              break;
            }
            case 26: {
              setVr(input.readString());
              break;
            }
            case 34: {
              setStringValue(input.readString());
              break;
            }
            case 40: {
              setBinaryItemId(input.readUInt32());
              break;
            }
            case 50: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addItems(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional uint32 tag = 1;
      public boolean hasTag() {
        return result.hasTag();
      }
      public int getTag() {
        return result.getTag();
      }
      public Builder setTag(int value) {
        result.hasTag = true;
        result.tag_ = value;
        return this;
      }
      public Builder clearTag() {
        result.hasTag = false;
        result.tag_ = 0;
        return this;
      }
      
      // optional string exclude = 2;
      public boolean hasExclude() {
        return result.hasExclude();
      }
      public java.lang.String getExclude() {
        return result.getExclude();
      }
      public Builder setExclude(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasExclude = true;
        result.exclude_ = value;
        return this;
      }
      public Builder clearExclude() {
        result.hasExclude = false;
        result.exclude_ = getDefaultInstance().getExclude();
        return this;
      }
      
      // optional string vr = 3;
      public boolean hasVr() {
        return result.hasVr();
      }
      public java.lang.String getVr() {
        return result.getVr();
      }
      public Builder setVr(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasVr = true;
        result.vr_ = value;
        return this;
      }
      public Builder clearVr() {
        result.hasVr = false;
        result.vr_ = getDefaultInstance().getVr();
        return this;
      }
      
      // optional string string_value = 4;
      public boolean hasStringValue() {
        return result.hasStringValue();
      }
      public java.lang.String getStringValue() {
        return result.getStringValue();
      }
      public Builder setStringValue(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasStringValue = true;
        result.stringValue_ = value;
        return this;
      }
      public Builder clearStringValue() {
        result.hasStringValue = false;
        result.stringValue_ = getDefaultInstance().getStringValue();
        return this;
      }
      
      // optional uint32 binary_item_id = 5;
      public boolean hasBinaryItemId() {
        return result.hasBinaryItemId();
      }
      public int getBinaryItemId() {
        return result.getBinaryItemId();
      }
      public Builder setBinaryItemId(int value) {
        result.hasBinaryItemId = true;
        result.binaryItemId_ = value;
        return this;
      }
      public Builder clearBinaryItemId() {
        result.hasBinaryItemId = false;
        result.binaryItemId_ = 0;
        return this;
      }
      
      // repeated .mint.metadata.ItemData items = 6;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData> getItemsList() {
        return java.util.Collections.unmodifiableList(result.items_);
      }
      public int getItemsCount() {
        return result.getItemsCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData getItems(int index) {
        return result.getItems(index);
      }
      public Builder setItems(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.items_.set(index, value);
        return this;
      }
      public Builder setItems(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.Builder builderForValue) {
        result.items_.set(index, builderForValue.build());
        return this;
      }
      public Builder addItems(org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData>();
        }
        result.items_.add(value);
        return this;
      }
      public Builder addItems(org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.Builder builderForValue) {
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData>();
        }
        result.items_.add(builderForValue.build());
        return this;
      }
      public Builder addAllItems(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData> values) {
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData>();
        }
        super.addAll(values, result.items_);
        return this;
      }
      public Builder clearItems() {
        result.items_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:mint.metadata.AttributeData)
    }
    
    static {
      defaultInstance = new AttributeData(true);
      org.nema.medical.mint.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:mint.metadata.AttributeData)
  }
  
  public static final class ItemData extends
      com.google.protobuf.GeneratedMessage {
    // Use ItemData.newBuilder() to construct.
    private ItemData() {
      initFields();
    }
    private ItemData(boolean noInit) {}
    
    private static final ItemData defaultInstance;
    public static ItemData getDefaultInstance() {
      return defaultInstance;
    }
    
    public ItemData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_ItemData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.metadata.gpb.MINT2GPB.internal_static_mint_metadata_ItemData_fieldAccessorTable;
    }
    
    // repeated .mint.metadata.AttributeData attributes = 1;
    public static final int ATTRIBUTES_FIELD_NUMBER = 1;
    private java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
      return attributes_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(1, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      for (org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData result;
      
      // Construct using org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData();
        return builder;
      }
      
      protected org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.getDescriptor();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData getDefaultInstanceForType() {
        return org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData) {
          return mergeFrom((org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData other) {
        if (other == org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.getDefaultInstance()) return this;
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.attributes_.addAll(other.attributes_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // repeated .mint.metadata.AttributeData attributes = 1;
      public java.util.List<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:mint.metadata.ItemData)
    }
    
    static {
      defaultInstance = new ItemData(true);
      org.nema.medical.mint.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:mint.metadata.ItemData)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_mint_metadata_StudyData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_mint_metadata_StudyData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_mint_metadata_SeriesData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_mint_metadata_SeriesData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_mint_metadata_InstanceData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_mint_metadata_InstanceData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_mint_metadata_AttributeData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_mint_metadata_AttributeData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_mint_metadata_ItemData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_mint_metadata_ItemData_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023mint-metadata.proto\022\rmint.metadata\"\204\001\n" +
      "\tStudyData\022\032\n\022study_instance_uid\030\001 \001(\t\0220" +
      "\n\nattributes\030\002 \003(\0132\034.mint.metadata.Attri" +
      "buteData\022)\n\006series\030\003 \003(\0132\031.mint.metadata" +
      ".SeriesData\"\342\001\n\nSeriesData\022\033\n\023series_ins" +
      "tance_uid\030\001 \001(\t\022\017\n\007exclude\030\002 \001(\t\0220\n\nattr" +
      "ibutes\030\003 \003(\0132\034.mint.metadata.AttributeDa" +
      "ta\022D\n\036normalized_instance_attributes\030\004 \003" +
      "(\0132\034.mint.metadata.AttributeData\022.\n\tinst" +
      "ances\030\005 \003(\0132\033.mint.metadata.InstanceData",
      "\"\210\001\n\014InstanceData\022\030\n\020sop_instance_uid\030\001 " +
      "\001(\t\022\017\n\007exclude\030\002 \001(\t\022\033\n\023transfer_syntax_" +
      "uid\030\003 \001(\t\0220\n\nattributes\030\004 \003(\0132\034.mint.met" +
      "adata.AttributeData\"\217\001\n\rAttributeData\022\013\n" +
      "\003tag\030\001 \001(\r\022\017\n\007exclude\030\002 \001(\t\022\n\n\002vr\030\003 \001(\t\022" +
      "\024\n\014string_value\030\004 \001(\t\022\026\n\016binary_item_id\030" +
      "\005 \001(\r\022&\n\005items\030\006 \003(\0132\027.mint.metadata.Ite" +
      "mData\"<\n\010ItemData\0220\n\nattributes\030\001 \003(\0132\034." +
      "mint.metadata.AttributeDataB0\n\"org.nema." +
      "medical.mint.metadata.gpbB\010MINT2GPBH\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_mint_metadata_StudyData_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_mint_metadata_StudyData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_mint_metadata_StudyData_descriptor,
              new java.lang.String[] { "StudyInstanceUid", "Attributes", "Series", },
              org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData.class,
              org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData.Builder.class);
          internal_static_mint_metadata_SeriesData_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_mint_metadata_SeriesData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_mint_metadata_SeriesData_descriptor,
              new java.lang.String[] { "SeriesInstanceUid", "Exclude", "Attributes", "NormalizedInstanceAttributes", "Instances", },
              org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.class,
              org.nema.medical.mint.metadata.gpb.MINT2GPB.SeriesData.Builder.class);
          internal_static_mint_metadata_InstanceData_descriptor =
            getDescriptor().getMessageTypes().get(2);
          internal_static_mint_metadata_InstanceData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_mint_metadata_InstanceData_descriptor,
              new java.lang.String[] { "SopInstanceUid", "Exclude", "TransferSyntaxUid", "Attributes", },
              org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.class,
              org.nema.medical.mint.metadata.gpb.MINT2GPB.InstanceData.Builder.class);
          internal_static_mint_metadata_AttributeData_descriptor =
            getDescriptor().getMessageTypes().get(3);
          internal_static_mint_metadata_AttributeData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_mint_metadata_AttributeData_descriptor,
              new java.lang.String[] { "Tag", "Exclude", "Vr", "StringValue", "BinaryItemId", "Items", },
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.class,
              org.nema.medical.mint.metadata.gpb.MINT2GPB.AttributeData.Builder.class);
          internal_static_mint_metadata_ItemData_descriptor =
            getDescriptor().getMessageTypes().get(4);
          internal_static_mint_metadata_ItemData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_mint_metadata_ItemData_descriptor,
              new java.lang.String[] { "Attributes", },
              org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.class,
              org.nema.medical.mint.metadata.gpb.MINT2GPB.ItemData.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
