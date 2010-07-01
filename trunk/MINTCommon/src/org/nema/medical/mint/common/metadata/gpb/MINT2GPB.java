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
// source: mint2gpb.proto

package org.nema.medical.mint.common.metadata.gpb;

public final class MINT2GPB {
  private MINT2GPB() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public enum ECompressionMethod
      implements com.google.protobuf.ProtocolMessageEnum {
    kNone(0, 0),
    kZip(1, 1),
    ;
    
    
    public final int getNumber() { return value; }
    
    public static ECompressionMethod valueOf(int value) {
      switch (value) {
        case 0: return kNone;
        case 1: return kZip;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<ECompressionMethod>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ECompressionMethod>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ECompressionMethod>() {
            public ECompressionMethod findValueByNumber(int number) {
              return ECompressionMethod.valueOf(number)
    ;        }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.getDescriptor().getEnumTypes().get(0);
    }
    
    private static final ECompressionMethod[] VALUES = {
      kNone, kZip, 
    };
    public static ECompressionMethod valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    private final int index;
    private final int value;
    private ECompressionMethod(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    static {
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.getDescriptor();
    }
    
    // @@protoc_insertion_point(enum_scope:vtal.dcm2metalib.ECompressionMethod)
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
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_StudyData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_StudyData_fieldAccessorTable;
    }
    
    // optional string study_instance_uid = 1;
    public static final int STUDY_INSTANCE_UID_FIELD_NUMBER = 1;
    private boolean hasStudyInstanceUid;
    private java.lang.String studyInstanceUid_ = "";
    public boolean hasStudyInstanceUid() { return hasStudyInstanceUid; }
    public java.lang.String getStudyInstanceUid() { return studyInstanceUid_; }
    
    // repeated .vtal.dcm2metalib.AttributeData attributes = 2;
    public static final int ATTRIBUTES_FIELD_NUMBER = 2;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
      return attributes_.get(index);
    }
    
    // repeated .vtal.dcm2metalib.SeriesData series = 3;
    public static final int SERIES_FIELD_NUMBER = 3;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData> series_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData> getSeriesList() {
      return series_;
    }
    public int getSeriesCount() { return series_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData getSeries(int index) {
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(2, element);
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData element : getSeriesList()) {
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, element);
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData element : getSeriesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.StudyData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData buildPartial() {
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
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData.getDefaultInstance()) return this;
        if (other.hasStudyInstanceUid()) {
          setStudyInstanceUid(other.getStudyInstanceUid());
        }
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.attributes_.addAll(other.attributes_);
        }
        if (!other.series_.isEmpty()) {
          if (result.series_.isEmpty()) {
            result.series_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData>();
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
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
            case 26: {
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.newBuilder();
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
      
      // repeated .vtal.dcm2metalib.AttributeData attributes = 2;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated .vtal.dcm2metalib.SeriesData series = 3;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData> getSeriesList() {
        return java.util.Collections.unmodifiableList(result.series_);
      }
      public int getSeriesCount() {
        return result.getSeriesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData getSeries(int index) {
        return result.getSeries(index);
      }
      public Builder setSeries(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.series_.set(index, value);
        return this;
      }
      public Builder setSeries(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.Builder builderForValue) {
        result.series_.set(index, builderForValue.build());
        return this;
      }
      public Builder addSeries(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.series_.isEmpty()) {
          result.series_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData>();
        }
        result.series_.add(value);
        return this;
      }
      public Builder addSeries(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.Builder builderForValue) {
        if (result.series_.isEmpty()) {
          result.series_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData>();
        }
        result.series_.add(builderForValue.build());
        return this;
      }
      public Builder addAllSeries(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData> values) {
        if (result.series_.isEmpty()) {
          result.series_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData>();
        }
        super.addAll(values, result.series_);
        return this;
      }
      public Builder clearSeries() {
        result.series_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.StudyData)
    }
    
    static {
      defaultInstance = new StudyData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.StudyData)
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
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_SeriesData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_SeriesData_fieldAccessorTable;
    }
    
    // optional string series_instance_uid = 1;
    public static final int SERIES_INSTANCE_UID_FIELD_NUMBER = 1;
    private boolean hasSeriesInstanceUid;
    private java.lang.String seriesInstanceUid_ = "";
    public boolean hasSeriesInstanceUid() { return hasSeriesInstanceUid; }
    public java.lang.String getSeriesInstanceUid() { return seriesInstanceUid_; }
    
    // repeated .vtal.dcm2metalib.AttributeData attributes = 2;
    public static final int ATTRIBUTES_FIELD_NUMBER = 2;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
      return attributes_.get(index);
    }
    
    // repeated .vtal.dcm2metalib.AttributeData normalized_instance_attributes = 3;
    public static final int NORMALIZED_INSTANCE_ATTRIBUTES_FIELD_NUMBER = 3;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> normalizedInstanceAttributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getNormalizedInstanceAttributesList() {
      return normalizedInstanceAttributes_;
    }
    public int getNormalizedInstanceAttributesCount() { return normalizedInstanceAttributes_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getNormalizedInstanceAttributes(int index) {
      return normalizedInstanceAttributes_.get(index);
    }
    
    // repeated .vtal.dcm2metalib.InstanceData instances = 4;
    public static final int INSTANCES_FIELD_NUMBER = 4;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData> instances_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData> getInstancesList() {
      return instances_;
    }
    public int getInstancesCount() { return instances_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData getInstances(int index) {
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(2, element);
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getNormalizedInstanceAttributesList()) {
        output.writeMessage(3, element);
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData element : getInstancesList()) {
        output.writeMessage(4, element);
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, element);
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getNormalizedInstanceAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, element);
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData element : getInstancesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.SeriesData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData buildPartial() {
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
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.getDefaultInstance()) return this;
        if (other.hasSeriesInstanceUid()) {
          setSeriesInstanceUid(other.getSeriesInstanceUid());
        }
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.attributes_.addAll(other.attributes_);
        }
        if (!other.normalizedInstanceAttributes_.isEmpty()) {
          if (result.normalizedInstanceAttributes_.isEmpty()) {
            result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
          }
          result.normalizedInstanceAttributes_.addAll(other.normalizedInstanceAttributes_);
        }
        if (!other.instances_.isEmpty()) {
          if (result.instances_.isEmpty()) {
            result.instances_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData>();
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
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
            case 26: {
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addNormalizedInstanceAttributes(subBuilder.buildPartial());
              break;
            }
            case 34: {
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.newBuilder();
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
      
      // repeated .vtal.dcm2metalib.AttributeData attributes = 2;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated .vtal.dcm2metalib.AttributeData normalized_instance_attributes = 3;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getNormalizedInstanceAttributesList() {
        return java.util.Collections.unmodifiableList(result.normalizedInstanceAttributes_);
      }
      public int getNormalizedInstanceAttributesCount() {
        return result.getNormalizedInstanceAttributesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getNormalizedInstanceAttributes(int index) {
        return result.getNormalizedInstanceAttributes(index);
      }
      public Builder setNormalizedInstanceAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.normalizedInstanceAttributes_.set(index, value);
        return this;
      }
      public Builder setNormalizedInstanceAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.normalizedInstanceAttributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addNormalizedInstanceAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.normalizedInstanceAttributes_.isEmpty()) {
          result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.normalizedInstanceAttributes_.add(value);
        return this;
      }
      public Builder addNormalizedInstanceAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.normalizedInstanceAttributes_.isEmpty()) {
          result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.normalizedInstanceAttributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllNormalizedInstanceAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.normalizedInstanceAttributes_.isEmpty()) {
          result.normalizedInstanceAttributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.normalizedInstanceAttributes_);
        return this;
      }
      public Builder clearNormalizedInstanceAttributes() {
        result.normalizedInstanceAttributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated .vtal.dcm2metalib.InstanceData instances = 4;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData> getInstancesList() {
        return java.util.Collections.unmodifiableList(result.instances_);
      }
      public int getInstancesCount() {
        return result.getInstancesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData getInstances(int index) {
        return result.getInstances(index);
      }
      public Builder setInstances(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.instances_.set(index, value);
        return this;
      }
      public Builder setInstances(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.Builder builderForValue) {
        result.instances_.set(index, builderForValue.build());
        return this;
      }
      public Builder addInstances(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.instances_.isEmpty()) {
          result.instances_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData>();
        }
        result.instances_.add(value);
        return this;
      }
      public Builder addInstances(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.Builder builderForValue) {
        if (result.instances_.isEmpty()) {
          result.instances_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData>();
        }
        result.instances_.add(builderForValue.build());
        return this;
      }
      public Builder addAllInstances(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData> values) {
        if (result.instances_.isEmpty()) {
          result.instances_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData>();
        }
        super.addAll(values, result.instances_);
        return this;
      }
      public Builder clearInstances() {
        result.instances_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.SeriesData)
    }
    
    static {
      defaultInstance = new SeriesData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.SeriesData)
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
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_InstanceData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_InstanceData_fieldAccessorTable;
    }
    
    // optional string transfer_syntax_uid = 1;
    public static final int TRANSFER_SYNTAX_UID_FIELD_NUMBER = 1;
    private boolean hasTransferSyntaxUid;
    private java.lang.String transferSyntaxUid_ = "";
    public boolean hasTransferSyntaxUid() { return hasTransferSyntaxUid; }
    public java.lang.String getTransferSyntaxUid() { return transferSyntaxUid_; }
    
    // repeated .vtal.dcm2metalib.AttributeData attributes = 2;
    public static final int ATTRIBUTES_FIELD_NUMBER = 2;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
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
      if (hasTransferSyntaxUid()) {
        output.writeString(1, getTransferSyntaxUid());
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(2, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasTransferSyntaxUid()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getTransferSyntaxUid());
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.InstanceData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.getDefaultInstance()) return this;
        if (other.hasTransferSyntaxUid()) {
          setTransferSyntaxUid(other.getTransferSyntaxUid());
        }
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
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
              setTransferSyntaxUid(input.readString());
              break;
            }
            case 18: {
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional string transfer_syntax_uid = 1;
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
      
      // repeated .vtal.dcm2metalib.AttributeData attributes = 2;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.InstanceData)
    }
    
    static {
      defaultInstance = new InstanceData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.InstanceData)
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
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_ItemData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_ItemData_fieldAccessorTable;
    }
    
    // repeated .vtal.dcm2metalib.AttributeData attributes = 1;
    public static final int ATTRIBUTES_FIELD_NUMBER = 1;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(1, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.ItemData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.getDefaultInstance()) return this;
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
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
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // repeated .vtal.dcm2metalib.AttributeData attributes = 1;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.ItemData)
    }
    
    static {
      defaultInstance = new ItemData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.ItemData)
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
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_AttributeData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_AttributeData_fieldAccessorTable;
    }
    
    // optional uint32 tag = 1;
    public static final int TAG_FIELD_NUMBER = 1;
    private boolean hasTag;
    private int tag_ = 0;
    public boolean hasTag() { return hasTag; }
    public int getTag() { return tag_; }
    
    // optional string vr = 2;
    public static final int VR_FIELD_NUMBER = 2;
    private boolean hasVr;
    private java.lang.String vr_ = "";
    public boolean hasVr() { return hasVr; }
    public java.lang.String getVr() { return vr_; }
    
    // optional string string_value = 3;
    public static final int STRING_VALUE_FIELD_NUMBER = 3;
    private boolean hasStringValue;
    private java.lang.String stringValue_ = "";
    public boolean hasStringValue() { return hasStringValue; }
    public java.lang.String getStringValue() { return stringValue_; }
    
    // optional uint32 binary_item_id = 4;
    public static final int BINARY_ITEM_ID_FIELD_NUMBER = 4;
    private boolean hasBinaryItemId;
    private int binaryItemId_ = 0;
    public boolean hasBinaryItemId() { return hasBinaryItemId; }
    public int getBinaryItemId() { return binaryItemId_; }
    
    // repeated .vtal.dcm2metalib.ItemData items = 5;
    public static final int ITEMS_FIELD_NUMBER = 5;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData> items_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData> getItemsList() {
      return items_;
    }
    public int getItemsCount() { return items_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData getItems(int index) {
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
      if (hasVr()) {
        output.writeString(2, getVr());
      }
      if (hasStringValue()) {
        output.writeString(3, getStringValue());
      }
      if (hasBinaryItemId()) {
        output.writeUInt32(4, getBinaryItemId());
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData element : getItemsList()) {
        output.writeMessage(5, element);
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
      if (hasVr()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getVr());
      }
      if (hasStringValue()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(3, getStringValue());
      }
      if (hasBinaryItemId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(4, getBinaryItemId());
      }
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData element : getItemsList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.AttributeData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.items_ != java.util.Collections.EMPTY_LIST) {
          result.items_ =
            java.util.Collections.unmodifiableList(result.items_);
        }
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.getDefaultInstance()) return this;
        if (other.hasTag()) {
          setTag(other.getTag());
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
            result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData>();
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
              setVr(input.readString());
              break;
            }
            case 26: {
              setStringValue(input.readString());
              break;
            }
            case 32: {
              setBinaryItemId(input.readUInt32());
              break;
            }
            case 42: {
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.newBuilder();
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
      
      // optional string vr = 2;
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
      
      // optional string string_value = 3;
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
      
      // optional uint32 binary_item_id = 4;
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
      
      // repeated .vtal.dcm2metalib.ItemData items = 5;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData> getItemsList() {
        return java.util.Collections.unmodifiableList(result.items_);
      }
      public int getItemsCount() {
        return result.getItemsCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData getItems(int index) {
        return result.getItems(index);
      }
      public Builder setItems(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.items_.set(index, value);
        return this;
      }
      public Builder setItems(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.Builder builderForValue) {
        result.items_.set(index, builderForValue.build());
        return this;
      }
      public Builder addItems(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData>();
        }
        result.items_.add(value);
        return this;
      }
      public Builder addItems(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.Builder builderForValue) {
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData>();
        }
        result.items_.add(builderForValue.build());
        return this;
      }
      public Builder addAllItems(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData> values) {
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData>();
        }
        super.addAll(values, result.items_);
        return this;
      }
      public Builder clearItems() {
        result.items_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.AttributeData)
    }
    
    static {
      defaultInstance = new AttributeData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.AttributeData)
  }
  
  public static final class BulkItemData extends
      com.google.protobuf.GeneratedMessage {
    // Use BulkItemData.newBuilder() to construct.
    private BulkItemData() {
      initFields();
    }
    private BulkItemData(boolean noInit) {}
    
    private static final BulkItemData defaultInstance;
    public static BulkItemData getDefaultInstance() {
      return defaultInstance;
    }
    
    public BulkItemData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_BulkItemData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_BulkItemData_fieldAccessorTable;
    }
    
    // optional uint64 offset = 1;
    public static final int OFFSET_FIELD_NUMBER = 1;
    private boolean hasOffset;
    private long offset_ = 0L;
    public boolean hasOffset() { return hasOffset; }
    public long getOffset() { return offset_; }
    
    // optional .vtal.dcm2metalib.ECompressionMethod compression_method = 2;
    public static final int COMPRESSION_METHOD_FIELD_NUMBER = 2;
    private boolean hasCompressionMethod;
    private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod compressionMethod_;
    public boolean hasCompressionMethod() { return hasCompressionMethod; }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod getCompressionMethod() { return compressionMethod_; }
    
    // optional uint64 expanded_length_bytes = 3;
    public static final int EXPANDED_LENGTH_BYTES_FIELD_NUMBER = 3;
    private boolean hasExpandedLengthBytes;
    private long expandedLengthBytes_ = 0L;
    public boolean hasExpandedLengthBytes() { return hasExpandedLengthBytes; }
    public long getExpandedLengthBytes() { return expandedLengthBytes_; }
    
    private void initFields() {
      compressionMethod_ = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod.kNone;
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasOffset()) {
        output.writeUInt64(1, getOffset());
      }
      if (hasCompressionMethod()) {
        output.writeEnum(2, getCompressionMethod().getNumber());
      }
      if (hasExpandedLengthBytes()) {
        output.writeUInt64(3, getExpandedLengthBytes());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasOffset()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(1, getOffset());
      }
      if (hasCompressionMethod()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(2, getCompressionMethod().getNumber());
      }
      if (hasExpandedLengthBytes()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(3, getExpandedLengthBytes());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.BulkItemData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.getDefaultInstance()) return this;
        if (other.hasOffset()) {
          setOffset(other.getOffset());
        }
        if (other.hasCompressionMethod()) {
          setCompressionMethod(other.getCompressionMethod());
        }
        if (other.hasExpandedLengthBytes()) {
          setExpandedLengthBytes(other.getExpandedLengthBytes());
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
              setOffset(input.readUInt64());
              break;
            }
            case 16: {
              int rawValue = input.readEnum();
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod value = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(2, rawValue);
              } else {
                setCompressionMethod(value);
              }
              break;
            }
            case 24: {
              setExpandedLengthBytes(input.readUInt64());
              break;
            }
          }
        }
      }
      
      
      // optional uint64 offset = 1;
      public boolean hasOffset() {
        return result.hasOffset();
      }
      public long getOffset() {
        return result.getOffset();
      }
      public Builder setOffset(long value) {
        result.hasOffset = true;
        result.offset_ = value;
        return this;
      }
      public Builder clearOffset() {
        result.hasOffset = false;
        result.offset_ = 0L;
        return this;
      }
      
      // optional .vtal.dcm2metalib.ECompressionMethod compression_method = 2;
      public boolean hasCompressionMethod() {
        return result.hasCompressionMethod();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod getCompressionMethod() {
        return result.getCompressionMethod();
      }
      public Builder setCompressionMethod(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.hasCompressionMethod = true;
        result.compressionMethod_ = value;
        return this;
      }
      public Builder clearCompressionMethod() {
        result.hasCompressionMethod = false;
        result.compressionMethod_ = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod.kNone;
        return this;
      }
      
      // optional uint64 expanded_length_bytes = 3;
      public boolean hasExpandedLengthBytes() {
        return result.hasExpandedLengthBytes();
      }
      public long getExpandedLengthBytes() {
        return result.getExpandedLengthBytes();
      }
      public Builder setExpandedLengthBytes(long value) {
        result.hasExpandedLengthBytes = true;
        result.expandedLengthBytes_ = value;
        return this;
      }
      public Builder clearExpandedLengthBytes() {
        result.hasExpandedLengthBytes = false;
        result.expandedLengthBytes_ = 0L;
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.BulkItemData)
    }
    
    static {
      defaultInstance = new BulkItemData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.BulkItemData)
  }
  
  public static final class BulkDataTable extends
      com.google.protobuf.GeneratedMessage {
    // Use BulkDataTable.newBuilder() to construct.
    private BulkDataTable() {
      initFields();
    }
    private BulkDataTable(boolean noInit) {}
    
    private static final BulkDataTable defaultInstance;
    public static BulkDataTable getDefaultInstance() {
      return defaultInstance;
    }
    
    public BulkDataTable getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_BulkDataTable_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_BulkDataTable_fieldAccessorTable;
    }
    
    // repeated .vtal.dcm2metalib.BulkItemData items = 1;
    public static final int ITEMS_FIELD_NUMBER = 1;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData> items_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData> getItemsList() {
      return items_;
    }
    public int getItemsCount() { return items_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData getItems(int index) {
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData element : getItemsList()) {
        output.writeMessage(1, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData element : getItemsList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.BulkDataTable.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.items_ != java.util.Collections.EMPTY_LIST) {
          result.items_ =
            java.util.Collections.unmodifiableList(result.items_);
        }
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable.getDefaultInstance()) return this;
        if (!other.items_.isEmpty()) {
          if (result.items_.isEmpty()) {
            result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData>();
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
            case 10: {
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addItems(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // repeated .vtal.dcm2metalib.BulkItemData items = 1;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData> getItemsList() {
        return java.util.Collections.unmodifiableList(result.items_);
      }
      public int getItemsCount() {
        return result.getItemsCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData getItems(int index) {
        return result.getItems(index);
      }
      public Builder setItems(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.items_.set(index, value);
        return this;
      }
      public Builder setItems(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.Builder builderForValue) {
        result.items_.set(index, builderForValue.build());
        return this;
      }
      public Builder addItems(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData>();
        }
        result.items_.add(value);
        return this;
      }
      public Builder addItems(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.Builder builderForValue) {
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData>();
        }
        result.items_.add(builderForValue.build());
        return this;
      }
      public Builder addAllItems(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData> values) {
        if (result.items_.isEmpty()) {
          result.items_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData>();
        }
        super.addAll(values, result.items_);
        return this;
      }
      public Builder clearItems() {
        result.items_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.BulkDataTable)
    }
    
    static {
      defaultInstance = new BulkDataTable(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.BulkDataTable)
  }
  
  public static final class StudySummaryData extends
      com.google.protobuf.GeneratedMessage {
    // Use StudySummaryData.newBuilder() to construct.
    private StudySummaryData() {
      initFields();
    }
    private StudySummaryData(boolean noInit) {}
    
    private static final StudySummaryData defaultInstance;
    public static StudySummaryData getDefaultInstance() {
      return defaultInstance;
    }
    
    public StudySummaryData getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_StudySummaryData_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internal_static_vtal_dcm2metalib_StudySummaryData_fieldAccessorTable;
    }
    
    // repeated .vtal.dcm2metalib.AttributeData attributes = 1;
    public static final int ATTRIBUTES_FIELD_NUMBER = 1;
    private java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> attributes_ =
      java.util.Collections.emptyList();
    public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
      return attributes_;
    }
    public int getAttributesCount() { return attributes_.size(); }
    public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
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
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        output.writeMessage(1, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      for (org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData element : getAttributesList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseDelimitedFrom(
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
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData result;
      
      // Construct using com.vitalimages.contentserver.mint.Mint2Gpb.StudySummaryData.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData();
        return builder;
      }
      
      protected org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData.getDescriptor();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData getDefaultInstanceForType() {
        return org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.attributes_ != java.util.Collections.EMPTY_LIST) {
          result.attributes_ =
            java.util.Collections.unmodifiableList(result.attributes_);
        }
        org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData) {
          return mergeFrom((org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData other) {
        if (other == org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData.getDefaultInstance()) return this;
        if (!other.attributes_.isEmpty()) {
          if (result.attributes_.isEmpty()) {
            result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
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
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder subBuilder = org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAttributes(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // repeated .vtal.dcm2metalib.AttributeData attributes = 1;
      public java.util.List<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> getAttributesList() {
        return java.util.Collections.unmodifiableList(result.attributes_);
      }
      public int getAttributesCount() {
        return result.getAttributesCount();
      }
      public org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData getAttributes(int index) {
        return result.getAttributes(index);
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.attributes_.set(index, value);
        return this;
      }
      public Builder setAttributes(int index, org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        result.attributes_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(value);
        return this;
      }
      public Builder addAttributes(org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder builderForValue) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        result.attributes_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAttributes(
          java.lang.Iterable<? extends org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData> values) {
        if (result.attributes_.isEmpty()) {
          result.attributes_ = new java.util.ArrayList<org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData>();
        }
        super.addAll(values, result.attributes_);
        return this;
      }
      public Builder clearAttributes() {
        result.attributes_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:vtal.dcm2metalib.StudySummaryData)
    }
    
    static {
      defaultInstance = new StudySummaryData(true);
      org.nema.medical.mint.common.metadata.gpb.MINT2GPB.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:vtal.dcm2metalib.StudySummaryData)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_StudyData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_StudyData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_SeriesData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_SeriesData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_InstanceData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_InstanceData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_ItemData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_ItemData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_AttributeData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_AttributeData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_BulkItemData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_BulkItemData_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_BulkDataTable_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_BulkDataTable_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_vtal_dcm2metalib_StudySummaryData_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_vtal_dcm2metalib_StudySummaryData_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016mint2gpb.proto\022\020vtal.dcm2metalib\"\212\001\n\tS" +
      "tudyData\022\032\n\022study_instance_uid\030\001 \001(\t\0223\n\n" +
      "attributes\030\002 \003(\0132\037.vtal.dcm2metalib.Attr" +
      "ibuteData\022,\n\006series\030\003 \003(\0132\034.vtal.dcm2met" +
      "alib.SeriesData\"\332\001\n\nSeriesData\022\033\n\023series" +
      "_instance_uid\030\001 \001(\t\0223\n\nattributes\030\002 \003(\0132" +
      "\037.vtal.dcm2metalib.AttributeData\022G\n\036norm" +
      "alized_instance_attributes\030\003 \003(\0132\037.vtal." +
      "dcm2metalib.AttributeData\0221\n\tinstances\030\004" +
      " \003(\0132\036.vtal.dcm2metalib.InstanceData\"`\n\014",
      "InstanceData\022\033\n\023transfer_syntax_uid\030\001 \001(" +
      "\t\0223\n\nattributes\030\002 \003(\0132\037.vtal.dcm2metalib" +
      ".AttributeData\"?\n\010ItemData\0223\n\nattributes" +
      "\030\001 \003(\0132\037.vtal.dcm2metalib.AttributeData\"" +
      "\201\001\n\rAttributeData\022\013\n\003tag\030\001 \001(\r\022\n\n\002vr\030\002 \001" +
      "(\t\022\024\n\014string_value\030\003 \001(\t\022\026\n\016binary_item_" +
      "id\030\004 \001(\r\022)\n\005items\030\005 \003(\0132\032.vtal.dcm2metal" +
      "ib.ItemData\"\177\n\014BulkItemData\022\016\n\006offset\030\001 " +
      "\001(\004\022@\n\022compression_method\030\002 \001(\0162$.vtal.d" +
      "cm2metalib.ECompressionMethod\022\035\n\025expande",
      "d_length_bytes\030\003 \001(\004\">\n\rBulkDataTable\022-\n" +
      "\005items\030\001 \003(\0132\036.vtal.dcm2metalib.BulkItem" +
      "Data\"G\n\020StudySummaryData\0223\n\nattributes\030\001" +
      " \003(\0132\037.vtal.dcm2metalib.AttributeData*)\n" +
      "\022ECompressionMethod\022\t\n\005kNone\020\000\022\010\n\004kZip\020\001" +
      "B&\n\"com.vitalimages.contentserver.mintH\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_vtal_dcm2metalib_StudyData_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_vtal_dcm2metalib_StudyData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_StudyData_descriptor,
              new java.lang.String[] { "StudyInstanceUid", "Attributes", "Series", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudyData.Builder.class);
          internal_static_vtal_dcm2metalib_SeriesData_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_vtal_dcm2metalib_SeriesData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_SeriesData_descriptor,
              new java.lang.String[] { "SeriesInstanceUid", "Attributes", "NormalizedInstanceAttributes", "Instances", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.SeriesData.Builder.class);
          internal_static_vtal_dcm2metalib_InstanceData_descriptor =
            getDescriptor().getMessageTypes().get(2);
          internal_static_vtal_dcm2metalib_InstanceData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_InstanceData_descriptor,
              new java.lang.String[] { "TransferSyntaxUid", "Attributes", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData.Builder.class);
          internal_static_vtal_dcm2metalib_ItemData_descriptor =
            getDescriptor().getMessageTypes().get(3);
          internal_static_vtal_dcm2metalib_ItemData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_ItemData_descriptor,
              new java.lang.String[] { "Attributes", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ItemData.Builder.class);
          internal_static_vtal_dcm2metalib_AttributeData_descriptor =
            getDescriptor().getMessageTypes().get(4);
          internal_static_vtal_dcm2metalib_AttributeData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_AttributeData_descriptor,
              new java.lang.String[] { "Tag", "Vr", "StringValue", "BinaryItemId", "Items", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData.Builder.class);
          internal_static_vtal_dcm2metalib_BulkItemData_descriptor =
            getDescriptor().getMessageTypes().get(5);
          internal_static_vtal_dcm2metalib_BulkItemData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_BulkItemData_descriptor,
              new java.lang.String[] { "Offset", "CompressionMethod", "ExpandedLengthBytes", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData.Builder.class);
          internal_static_vtal_dcm2metalib_BulkDataTable_descriptor =
            getDescriptor().getMessageTypes().get(6);
          internal_static_vtal_dcm2metalib_BulkDataTable_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_BulkDataTable_descriptor,
              new java.lang.String[] { "Items", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkDataTable.Builder.class);
          internal_static_vtal_dcm2metalib_StudySummaryData_descriptor =
            getDescriptor().getMessageTypes().get(7);
          internal_static_vtal_dcm2metalib_StudySummaryData_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_vtal_dcm2metalib_StudySummaryData_descriptor,
              new java.lang.String[] { "Attributes", },
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData.class,
              org.nema.medical.mint.common.metadata.gpb.MINT2GPB.StudySummaryData.Builder.class);
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
