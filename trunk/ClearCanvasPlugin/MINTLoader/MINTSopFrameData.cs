#region License

// Copyright (c) 2010, ClearCanvas Inc.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
//    * Redistributions of source code must retain the above copyright notice, 
//      this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright notice, 
//      this list of conditions and the following disclaimer in the documentation 
//      and/or other materials provided with the distribution.
//    * Neither the name of ClearCanvas Inc. nor the names of its contributors 
//      may be used to endorse or promote products derived from this software without 
//      specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
// THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
// GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
// ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
// OF SUCH DAMAGE.

#endregion

using System;
using ClearCanvas.Common;
using ClearCanvas.Common.Utilities;
using ClearCanvas.Dicom;
using ClearCanvas.Dicom.Iod;
using ClearCanvas.Dicom.Iod.Modules;
using ClearCanvas.Dicom.ServiceModel.Streaming;
using ClearCanvas.ImageViewer.Common;
using ClearCanvas.ImageViewer.Imaging;
using ClearCanvas.ImageViewer.StudyManagement;
using System.Collections.Generic;
using System.Threading;
using ClearCanvas.ImageViewer.StudyLoaders.Streaming;

namespace MINTLoader
{

	internal partial class MINTSopDataSource
	{
		private class MINTSopFrameData : StandardSopFrameData, IStreamingSopFrameData
		{
			private readonly FramePixelData _framePixelData;
			private readonly byte[][] _overlayData;

            public MINTSopFrameData(int frameNumber, MINTSopDataSource parent) 
				: base(frameNumber, parent, RegenerationCost.High)
			{
				_framePixelData = new FramePixelData(this.Parent, frameNumber);
				_overlayData = new byte[16][];
			}

			public new MINTSopDataSource Parent
			{
                get { return (MINTSopDataSource)base.Parent; }
			}

			public bool PixelDataRetrieved
			{
				get { return _framePixelData.AlreadyRetrieved; }
			}

			public StreamingPerformanceInfo LastRetrievePerformanceInfo
			{
				get { return _framePixelData.LastRetrievePerformanceInfo; }	
			}

			public void RetrievePixelData()
			{
				_framePixelData.Retrieve();
			}

			protected override byte[] CreateNormalizedPixelData()
			{
				byte[] pixelData = _framePixelData.GetUncompressedPixelData();

				string photometricInterpretationCode = this.Parent[DicomTags.PhotometricInterpretation].ToString();
				PhotometricInterpretation pi = PhotometricInterpretation.FromCodeString(photometricInterpretationCode);

				TransferSyntax ts = TransferSyntax.GetTransferSyntax(this.Parent.TransferSyntaxUid);
				if (pi.IsColor)
				{
					if (ts == TransferSyntax.Jpeg2000ImageCompression ||
					    ts == TransferSyntax.Jpeg2000ImageCompressionLosslessOnly ||
					    ts == TransferSyntax.JpegExtendedProcess24 ||
					    ts == TransferSyntax.JpegBaselineProcess1)
						pi = PhotometricInterpretation.Rgb;

					pixelData = ToArgb(this.Parent, pixelData, pi);
				}
				else
				{
					OverlayPlaneModuleIod opmi = new OverlayPlaneModuleIod(this.Parent);
					foreach (OverlayPlane overlayPlane in opmi)
					{
						if (IsOverlayEmbedded(overlayPlane) && _overlayData[overlayPlane.Index] == null)
						{
							byte[] overlayData = OverlayData.UnpackFromPixelData(overlayPlane.OverlayBitPosition, this.Parent[DicomTags.BitsAllocated].GetInt32(0, 0), false, pixelData);
							_overlayData[overlayPlane.Index] = overlayData;
						}
						else if (!overlayPlane.HasOverlayData)
						{
							Platform.Log(LogLevel.Warn, "The image {0} appears to be missing OverlayData for group 0x{1:X4}.", this.Parent.SopInstanceUid, overlayPlane.Group);
						}
					}

					NormalizeGrayscalePixels(this.Parent, pixelData);
				}

				return pixelData;
			}

			protected override byte[] CreateNormalizedOverlayData(int overlayGroupNumber, int overlayFrameNumber)
			{
				int frameIndex = overlayFrameNumber - 1;
				int overlayIndex = overlayGroupNumber - 1;

				byte[] overlayData = null;

				OverlayPlaneModuleIod opmi = new OverlayPlaneModuleIod(this.Parent);
				if (opmi.HasOverlayPlane(overlayIndex))
				{
					OverlayPlane overlayPlane = opmi[overlayIndex];

					if (_overlayData[overlayIndex] == null)
					{
						if (IsOverlayEmbedded(overlayPlane))
						{
							this.GetNormalizedPixelData();
						}
						else
						{
							int bitOffset;
							overlayPlane.TryComputeOverlayDataBitOffset(frameIndex, out bitOffset);

							OverlayData od = new OverlayData(bitOffset,
							                                 overlayPlane.OverlayRows,
							                                 overlayPlane.OverlayColumns,
							                                 overlayPlane.IsBigEndianOW,
							                                 overlayPlane.OverlayData);
							_overlayData[overlayIndex] = od.Unpack();
						}
					}
					overlayData = _overlayData[overlayIndex];
				}

				return overlayData;
			}

			protected override void OnUnloaded()
			{
				base.OnUnloaded();

				// dump pixel data retrieve results and stored overlays
				_framePixelData.Unload();
				_overlayData[0x0] = null;
				_overlayData[0x1] = null;
				_overlayData[0x2] = null;
				_overlayData[0x3] = null;
				_overlayData[0x4] = null;
				_overlayData[0x5] = null;
				_overlayData[0x6] = null;
				_overlayData[0x7] = null;
				_overlayData[0x8] = null;
				_overlayData[0x9] = null;
				_overlayData[0xA] = null;
				_overlayData[0xB] = null;
				_overlayData[0xC] = null;
				_overlayData[0xD] = null;
				_overlayData[0xE] = null;
				_overlayData[0xF] = null;
			}
		}

		private class FramePixelDataRetriever
		{
			public readonly string StudyInstanceUid;
			public readonly string SeriesInstanceUid;
			public readonly string SopInstanceUid;
			public readonly int FrameNumber;
			public readonly string TransferSyntaxUid;
			public readonly Uri BaseUrl;
            private MINTBinaryStream BinaryStream;
            private bool UseBulkLoading;

			public FramePixelDataRetriever(FramePixelData source, MINTBinaryStream binaryStream, bool useBulkLoading)
			{
                BaseUrl = source.Parent.BinaryUri;

				StudyInstanceUid = source.Parent.StudyInstanceUid;
				SeriesInstanceUid = source.Parent.SeriesInstanceUid;
				SopInstanceUid = source.Parent.SopInstanceUid;
				FrameNumber = source.FrameNumber;
				TransferSyntaxUid = source.Parent.TransferSyntaxUid;
                BinaryStream = binaryStream;
                UseBulkLoading = useBulkLoading;
			}

			public RetrievePixelDataResult Retrieve()
			{
				Exception retrieveException;
				RetrievePixelDataResult result = TryClientRetrievePixelData(out retrieveException);

				if (result != null)
					return result;

				// if no result was returned, then the throw an exception with an appropriate, user-friendly message
				throw TranslateStreamingException(retrieveException);
			}

			private RetrievePixelDataResult TryClientRetrievePixelData(out Exception lastRetrieveException)
			{
				// retry parameters
				const int retryTimeout = 1500;
				int retryDelay = 50;
				int retryCounter = 0;
                
                //Second parameter true to use bulk loading, false to load images one by one
                MINTStreamingClient client = new MINTStreamingClient(this.BaseUrl, UseBulkLoading, BinaryStream);
				RetrievePixelDataResult result = null;
				lastRetrieveException = null;

				CodeClock timeoutClock = new CodeClock();
				timeoutClock.Start();

				while (true)
				{
					try
					{
						if (retryCounter > 0)
							Platform.Log(LogLevel.Info, "Retrying retrieve pixel data for Sop '{0}' (Attempt #{1})", this.SopInstanceUid, retryCounter);

						CodeClock statsClock = new CodeClock();
						statsClock.Start();

						result = client.RetrievePixelData();

						statsClock.Stop();

						Platform.Log(LogLevel.Debug, "[Retrieve Info] Sop/Frame: {0}/{1}, Transfer Syntax: {2}, Bytes transferred: {3}, Elapsed (s): {4}, Retries: {5}",
						             this.SopInstanceUid, this.FrameNumber, this.TransferSyntaxUid,
						             result.MetaData.ContentLength, statsClock.Seconds, retryCounter);

						break;
					}
					catch (Exception ex)
					{
						lastRetrieveException = ex;

						timeoutClock.Stop();
						if (timeoutClock.Seconds*1000 >= retryTimeout)
						{
							// log an alert that we are aborting (exception trace at debug level only)
							int elapsed = (int)(1000*timeoutClock.Seconds);
							Platform.Log(LogLevel.Warn, "Failed to retrieve pixel data for Sop '{0}'; Aborting after {1} attempts in {2} ms", this.SopInstanceUid, retryCounter, elapsed);
							Platform.Log(LogLevel.Debug, ex, "[Retrieve Fail-Abort] Sop/Frame: {0}/{1}, Retry Attempts: {2}, Elapsed: {3} ms", this.SopInstanceUid, this.FrameNumber - 1, retryCounter, elapsed);
							break;
						}
						timeoutClock.Start();

						retryCounter++;

						// log the retry (exception trace at debug level only)
						Platform.Log(LogLevel.Warn, "Failed to retrieve pixel data for Sop '{0}'; Retrying in {1} ms", this.SopInstanceUid, retryDelay);
						Platform.Log(LogLevel.Debug, ex, "[Retrieve Fail-Retry] Sop/Frame: {0}/{1}, Retry in: {2} ms", this.SopInstanceUid, this.FrameNumber - 1, retryDelay);
						MemoryManager.Collect(retryDelay);
						retryDelay *= 2;
					}
				}

				return result;
			}
		}
		
		private class FramePixelData
		{
			private readonly object _syncLock = new object();
			private volatile bool _alreadyRetrieved;
			private RetrievePixelDataResult _retrieveResult;
			private volatile StreamingPerformanceInfo _lastRetrievePerformanceInfo;

            public readonly MINTSopDataSource Parent;
			public readonly int FrameNumber;

            public FramePixelData(MINTSopDataSource parent, int frameNumber)
			{
				Parent = parent;
				FrameNumber = frameNumber;
			}

			public bool AlreadyRetrieved
			{
				get { return _alreadyRetrieved; }
			}

			public StreamingPerformanceInfo LastRetrievePerformanceInfo
			{
				get { return _lastRetrievePerformanceInfo; }
			}

			public void Retrieve()
			{
				if (!_alreadyRetrieved)
				{
					//construct this object before the lock so there's no chance of deadlocking
					//with the parent data source (because we are accessing it's tags at the 
					//same time as it's trying to get the pixel data).
					FramePixelDataRetriever retriever = new FramePixelDataRetriever(this, Parent.binaryStream, Parent.useBulkLoading);

					lock (_syncLock)
					{
						if (!_alreadyRetrieved)
						{
							DateTime start = DateTime.Now;
							_retrieveResult = retriever.Retrieve();
							DateTime end = DateTime.Now;

							_lastRetrievePerformanceInfo = 
								new StreamingPerformanceInfo(start, end, _retrieveResult.MetaData.ContentLength);
							
							_alreadyRetrieved = true;
						}
					}
				}
			}

			public byte[] GetUncompressedPixelData()
			{
				//construct this object before the lock so there's no chance of deadlocking
				//with the parent data source (because we are accessing it's tags at the 
				//same time as it's trying to get the pixel data).
				FramePixelDataRetriever retriever = new FramePixelDataRetriever(this, Parent.binaryStream, Parent.useBulkLoading);

				lock (_syncLock)
				{
					RetrievePixelDataResult result;
					if (_retrieveResult == null)
						result = retriever.Retrieve();
					else
						result = _retrieveResult;

					//free this memory up in case it's holding a compressed buffer.
					_retrieveResult = null;

					CodeClock clock = new CodeClock();
					clock.Start();

					//synchronize the call to decompress; it's really already synchronized by
					//the parent b/c it's only called from CreateFrameNormalizedPixelData, but it doesn't hurt.
					byte[] pixelData = result.GetPixelData();

					clock.Stop();

					Platform.Log(LogLevel.Debug, "[Decompress Info] Sop/Frame: {0}/{1}, Transfer Syntax: {2}, Uncompressed bytes: {3}, Elapsed (s): {4}",
												   retriever.SopInstanceUid, FrameNumber, retriever.TransferSyntaxUid,
												   pixelData.Length, clock.Seconds);

					return pixelData;
				}
			}

			public void Unload()
			{
				lock (_syncLock)
				{
					_alreadyRetrieved = false;
					_retrieveResult = null;
				}
			}
		}

		/// <summary>
		/// Determines if the overlay data for this plane is embedded in the pixel data.
		/// </summary>
		/// <remarks>
		/// We cannot use <see cref="OverlayPlane.IsEmbedded"/> because the PixelData attribute is not in the dataset since we stream it separately.
		/// </remarks>
		private static bool IsOverlayEmbedded(OverlayPlane overlayPlane)
		{
			IDicomAttributeProvider provider = overlayPlane.DicomAttributeProvider;

			// OverlayData exists => not embedded
			DicomAttribute overlayData = provider[overlayPlane.TagOffset + DicomTags.OverlayData];
			if (overlayData.IsEmpty || overlayData.IsNull)
			{
				// embedded => BitsAllocated={8|16}, OverlayBitPosition in [0, BitsAllocated)
				int overlayBitPosition = provider[overlayPlane.TagOffset + DicomTags.OverlayBitPosition].GetInt32(0, -1);
				int bitsAllocated = provider[DicomTags.BitsAllocated].GetInt32(0, 0);
				if (overlayBitPosition >= 0 && overlayBitPosition < bitsAllocated && (bitsAllocated == 8 || bitsAllocated == 16))
				{
					// embedded => OverlayBitPosition in (HighBit, BitsAllocated) or [0, HighBit - BitsStored + 1)
					int highBit = provider[DicomTags.HighBit].GetInt32(0, 0);
					int bitsStored = provider[DicomTags.BitsStored].GetInt32(0, 0);
					return (overlayBitPosition > highBit || overlayBitPosition < highBit - bitsStored + 1);
				}
			}
			return false;
		}
	}
}
