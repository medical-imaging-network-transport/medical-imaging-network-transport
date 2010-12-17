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

package org.nema.medical.mint.server.receiver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.service.StorageService;

public final class DICOMReceive {

    private static final Logger LOG = Logger.getLogger(DICOMReceive.class);

    public static final String DICOM_FILE_EXTENSION = ".dcm";
    public static final String PARTIAL_FILE_EXTENSION = ".part";

    private static final String[] storageSOPClasses = {
            UID._12leadECGWaveformStorage,
            UID.AmbulatoryECGWaveformStorage,
            UID.ArterialPulseWaveformStorage,
            UID.AutorefractionMeasurementsStorage,
            UID.BasicStructuredDisplayStorage,
            UID.BasicTextSRStorage,
            UID.BasicVoiceAudioWaveformStorage,
            UID.BlendingSoftcopyPresentationStateStorageSOPClass,
            UID.BreastTomosynthesisImageStorage,
            UID.CardiacElectrophysiologyWaveformStorage,
            UID.ChestCADSRStorage,
            UID.ColonCADSRStorage,
            UID.ColorSoftcopyPresentationStateStorageSOPClass,
            UID.ComprehensiveSRStorage,
            UID.ComputedRadiographyImageStorage,
            UID.CTImageStorage,
            UID.DeformableSpatialRegistrationStorage,
            UID.DigitalIntraoralXRayImageStorageForPresentation,
            UID.DigitalIntraoralXRayImageStorageForProcessing,
            UID.DigitalMammographyXRayImageStorageForPresentation,
            UID.DigitalMammographyXRayImageStorageForProcessing,
            UID.DigitalXRayImageStorageForPresentation,
            UID.DigitalXRayImageStorageForProcessing,
            UID.EncapsulatedCDAStorage,
            UID.EncapsulatedPDFStorage,
            UID.EnhancedCTImageStorage,
            UID.EnhancedMRColorImageStorage,
            UID.EnhancedMRImageStorage,
            UID.EnhancedPETImageStorage,
            UID.EnhancedSRStorage,
            UID.EnhancedUSVolumeStorage,
            UID.EnhancedXAImageStorage,
            UID.EnhancedXRFImageStorage,
            UID.GeneralAudioWaveformStorage,
            UID.GeneralECGWaveformStorage,
            UID.GrayscaleSoftcopyPresentationStateStorageSOPClass,
            UID.HemodynamicWaveformStorage,
            UID.KeratometryMeasurementsStorage,
            UID.KeyObjectSelectionDocumentStorage,
            UID.LensometryMeasurementsStorage,
            UID.MacularGridThicknessandVolumeReportStorage,
            UID.MammographyCADSRStorage,
            UID.MRImageStorage,
            UID.MRSpectroscopyStorage,
            UID.MultiframeGrayscaleByteSecondaryCaptureImageStorage,
            UID.MultiframeGrayscaleWordSecondaryCaptureImageStorage,
            UID.MultiframeSingleBitSecondaryCaptureImageStorage,
            UID.MultiframeTrueColorSecondaryCaptureImageStorage,
            UID.NuclearMedicineImageStorage,
            UID.OphthalmicPhotography16BitImageStorage,
            UID.OphthalmicPhotography8BitImageStorage,
            UID.OphthalmicTomographyImageStorage,
            UID.PositronEmissionTomographyImageStorage,
            UID.ProcedureLogStorage,
            UID.PseudoColorSoftcopyPresentationStateStorageSOPClass,
            UID.RawDataStorage,
            UID.RealWorldValueMappingStorage,
            UID.RespiratoryWaveformStorage,
            UID.RTBeamsTreatmentRecordStorage,
            UID.RTBrachyTreatmentRecordStorage,
            UID.RTDoseStorage,
            UID.RTImageStorage,
            UID.RTIonBeamsTreatmentRecordStorage,
            UID.RTIonPlanStorage,
            UID.RTPlanStorage,
            UID.RTStructureSetStorage,
            UID.RTTreatmentSummaryRecordStorage,
            UID.SecondaryCaptureImageStorage,
            UID.SegmentationStorage,
            UID.SiemensCSANonImageStorage,
            UID.SpatialFiducialsStorage,
            UID.SpatialRegistrationStorage,
            UID.SpectaclePrescriptionReportsStorage,
            UID.StereometricRelationshipStorage,
            UID.UltrasoundImageStorage,
            UID.UltrasoundMultiframeImageStorage,
            UID.VideoEndoscopicImageStorage,
            UID.VideoMicroscopicImageStorage,
            UID.VideoPhotographicImageStorage,
            UID.VLEndoscopicImageStorage,
            UID.VLMicroscopicImageStorage,
            UID.VLPhotographicImageStorage,
            UID.VLSlideCoordinatesMicroscopicImageStorage,
            UID.XAXRFGrayscaleSoftcopyPresentationStateStorage,
            UID.XRay3DAngiographicImageStorage,
            UID.XRay3DCraniofacialImageStorage,
            UID.XRayAngiographicImageStorage,
            UID.XRayRadiationDoseSRStorage,
            UID.XRayRadiofluoroscopicImageStorage
    };

    private static final String[] transferSyntaxes = {
            UID.DeflatedExplicitVRLittleEndian,
            UID.ExplicitVRBigEndian,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian,
            UID.JPEG2000,
            UID.JPEG2000LosslessOnly,
            UID.JPEG2000Part2Multicomponent,
            UID.JPEG2000Part2MulticomponentLosslessOnly,
            UID.JPEGBaseline1,
            UID.JPEGExtended24,
            UID.JPEGLossless,
            UID.JPEGLosslessNonHierarchical14,
            UID.JPEGLSLossless,
            UID.JPEGLSLossyNearLossless,
            UID.JPIPReferenced,
            UID.JPIPReferencedDeflate,
            UID.MPEG2,
            UID.MPEG2MainProfileHighLevel,
            UID.RLELossless
    };

    private File storageRootDir;

    public final File getStorageRootDir() {
        return storageRootDir;
    }

    public final void setStorageRootDir(final File storageRootDir) {
        this.storageRootDir = storageRootDir;
    }

    private final NetworkConnection networkConnection = new NetworkConnection();
    {
        networkConnection.setTcpNoDelay(true);
    }

    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    private final class CustomStorageService extends StorageService {
        public CustomStorageService(final String[] sopClasses) {
            super(sopClasses);
        }

        @Override
         protected void onCStoreRQ(final Association association, final int pcid, final DicomObject dcmReqObj,
                                   final PDVInputStream dataStream, final String transferSyntaxUID,
                                   final DicomObject dcmRspObj)
                throws DicomServiceException, IOException {
            final String classUID = dcmReqObj.getString(Tag.AffectedSOPClassUID);
            final String instanceUID = dcmReqObj.getString(Tag.AffectedSOPInstanceUID);
            final File associationDir = getStorageRootDir();
            final String dicomFileBaseName = instanceUID + DICOM_FILE_EXTENSION;
            final File dicomFile = new File(associationDir, dicomFileBaseName + PARTIAL_FILE_EXTENSION);
            //TODO Things could get hairy if the same instance was sent before, and has not been deleted yet
            assert !dicomFile.exists();
            final BasicDicomObject fileMetaDcmObj = new BasicDicomObject();
            fileMetaDcmObj.initFileMetaInformation(classUID, instanceUID, transferSyntaxUID);
            //600000 bytes appears to be a fairly optimal cache size to maximize throughput
            //for single-frame CT data
            final DicomOutputStream outStream = new DicomOutputStream(
                    new BufferedOutputStream(new FileOutputStream(dicomFile), 600000));
            try {
                outStream.writeFileMetaInformation(fileMetaDcmObj);
                dataStream.copyTo(outStream);
            } finally {
                outStream.close();
            }
            dicomFile.renameTo(new File(associationDir, dicomFileBaseName));
        }
    }

    private final CustomStorageService cStoreSCP = new CustomStorageService(storageSOPClasses);

    private final NetworkApplicationEntity ae = new NetworkApplicationEntity();
    {
        ae.setNetworkConnection(networkConnection);
        ae.setAssociationAcceptor(true);
        ae.register(cStoreSCP);
        ae.setPackPDV(true);
        //TODO do we need to support the Verification Service Class? If so, look at special treatment in dcmrcv.
        final TransferCapability[] transferCapabilities = new TransferCapability[storageSOPClasses.length];
        for (int i = 0; i < transferCapabilities.length; ++i) {
            transferCapabilities[i] = new TransferCapability(
                    storageSOPClasses[i], transferSyntaxes, TransferCapability.SCP);
        }
        ae.setTransferCapability(transferCapabilities);
    }

    private final Device device = new Device();
    {
        device.setAssociationReaperPeriod(Integer.MAX_VALUE);
        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(networkConnection);
    }

    public Device getDevice() {
        return device;
    }

    //The number of threads here likely corresponds to how many concurrent associations can be served
    //(but need at least two to serve a single association; also, starting with a smaller core pool
    //does not seem to work, so keep the two numbers in sync)
    private final ExecutorService executor = new ThreadPoolExecutor(
            100, 100, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

    public final void start() throws IOException {
        device.startListening(executor);
    	LOG.debug("DICOM Receive started.");
    }

    public final void stop() {
        device.stopListening();
    	LOG.debug("DICOM Receive stopped.");

    	executor.shutdown();
    	try {
    		if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
    			executor.shutdownNow();
    			if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
    				LOG.warn("DICOM Receive Executor service did not terminate.");
    			}
    		}
    	} catch (final InterruptedException e) {
    		executor.shutdownNow();
    		Thread.currentThread().interrupt();
    	}

    	LOG.debug("DICOM Receive threads shut down.");
    }

    public final void setDeviceName(final String name) {
        device.setDeviceName(name);
    }

    public final void setAETitle(final String aeTitle) {
        ae.setAETitle(aeTitle);
    }

    public final void setPort(final int port) {
        networkConnection.setPort(port);
    }

    public final void setHostName(final String hostName) {
        networkConnection.setHostname(hostName);
    }

    public final void setReaperTimeout(final int timeoutMS) {
        device.setAssociationReaperPeriod(timeoutMS);
    }
}
