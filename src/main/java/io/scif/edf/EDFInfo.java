/*
 * #%L
 * SCIFIO Life Sciences Extension
 * %%
 * Copyright (C) 2013 - 2016 Open Microscopy Environment:
 * 	- Board of Regents of the University of Wisconsin-Madison
 * 	- Glencoe Software, Inc.
 * 	- University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package io.scif.edf;

import io.scif.MetaTable;
import io.scif.common.Constants;

import java.io.IOException;
import java.util.StringTokenizer;

import org.scijava.io.handle.DataHandle;
import org.scijava.io.location.Location;

/**
 * SDTInfo encapsulates the header information for Becker &amp; Hickl SPC-Image
 * SDT files.
 * 
 * @author Curtis Rueden
 * @author Mark Hiner
 */
public class SDTInfo {

	// -- Constants --

	public static final short BH_HEADER_CHKSUM = 0x55aa;
	public static final short BH_HEADER_NOT_VALID = 0x1111;
	public static final short BH_HEADER_VALID = 0x5555;

	public static final int FIFO_IMAGE_MODE = 13;

	/** For .set files (setup only). */
	public static final String SETUP_IDENTIFIER = "SPC Setup Script File";

	/** For normal .sdt files (setup + data). */
	public static final String DATA_IDENTIFIER = "SPC Setup & Data File";

	/**
	 * For .sdt files created automatically in Continuous Flow mode measurement
	 * (no setup, only data).
	 */
	public static final String FLOW_DATA_IDENTIFIER = "SPC Flow Data File";

	/**
	 * For .sdt files created using DLL function SPC_save_data_to_sdtfile (no
	 * setup, only data).
	 */
	public static final String DLL_DATA_IDENTIFIER = "SPC DLL Data File";

	/**
	 * For .sdt files created in FIFO mode (setup, data blocks = Decay, FCS, FIDA,
	 * FILDA &amp; MCS curves for each used routing channel).
	 */
	public static final String FCS_DATA_IDENTIFIER = "SPC FCS Data File";

	public static final String X_STRING = "#SP [SP_SCAN_X,I,";
	public static final String Y_STRING = "#SP [SP_SCAN_Y,I,";
	public static final String T_STRING = "#SP [SP_ADC_RE,I,";
	public static final String C_STRING1 = "#SP [SP_SCAN_RX,I,";
	public static final String C_STRING2 = "#SP [SP_SCAN_RY,I,";

	// -- Fields --

	public int width, height, timeBins, channels, timepoints;

	// -- Fields - File header --

	/** Software revision number (lower 4 bits &gt;= 10(decimal)). */
	public short revision;

	/**
	 * Offset of the info part which contains general text information (Title,
	 * date, time, contents etc.).
	 */
	public int infoOffs;

	/** Length of the info part. */
	public short infoLength;

	/**
	 * Offset of the setup text data (system parameters, display parameters, trace
	 * parameters etc.).
	 */
	public int setupOffs;

	/** Length of the setup data. */
	public short setupLength;

	/** Offset of the first data block. */
	public int dataBlockOffs;

	/**
	 * no_of_data_blocks valid only when in 0 .. 0x7ffe range, if equal to 0x7fff
	 * the field 'reserved1' contains valid no_of_data_blocks.
	 */
	public short noOfDataBlocks;

	// length of the longest block in the file
	public int dataBlockLength;

	// offset to 1st. measurement description block
	// (system parameters connected to data blocks)
	public int measDescBlockOffs;

	// number of measurement description blocks
	public short noOfMeasDescBlocks;

	// length of the measurement description blocks
	public short measDescBlockLength;

	// valid: 0x5555, not valid: 0x1111
	public int headerValid;

	// reserved1 now contains noOfDataBlocks
	public long reserved1; // unsigned

	public int reserved2;

	// checksum of file header
	public int chksum;

	// -- Fields - File Info --

	public String info;

	// -- Fields -- Setup --

	public String setup;

	// -- Fields - MeasureInfo --

	public boolean hasMeasureInfo;

	/** Time of creation. */
	public String time;

	/** Date of creation. */
	public String date;

	/** Serial number of the module. */
	public String modSerNo;

	public short measMode;
	public float cfdLL;
	public float cfdLH;
	public float cfdZC;
	public float cfdHF;
	public float synZC;
	public short synFD;
	public float synHF;
	public float tacR;
	public short tacG;
	public float tacOF;
	public float tacLL;
	public float tacLH;
	public short adcRE;
	public short ealDE;
	public short ncx;
	public short ncy;
	public int page;
	public float colT;
	public float repT;
	public short stopt;
	public int overfl;
	public short useMotor;
	public int steps;
	public float offset;
	public short dither;
	public short incr;
	public short memBank;

	/** Module type. */
	public String modType;

	public float synTH;
	public short deadTimeComp;

	/** 2 = disabled line markers. */
	public short polarityL;

	public short polarityF;
	public short polarityP;

	/** Line predivider = 2 ** (linediv). */
	public short linediv;

	public short accumulate;
	public int flbckY;
	public int flbckX;
	public int bordU;
	public int bordL;
	public float pixTime;
	public short pixClk;
	public short trigger;
	public int scanX;
	public int scanY;
	public int scanRX;
	public int scanRY;
	public short fifoTyp;
	public int epxDiv;
	public int modTypeCode;

	/** New in v.8.4. */
	public int modFpgaVer;

	public float overflowCorrFactor;
	public int adcZoom;

	/** Cycles (accumulation cycles in FLOW mode). */
	public int cycles;

	// -- Fields - MeasStopInfo --

	public boolean hasMeasStopInfo;

	/** Last SPC_test_state return value (status). */
	public int status;

	/** Scan clocks bits 2-0 (frame, line, pixel), rates_read - bit 15. */
	public int flags;

	/**
	 * Time from start to - disarm (simple measurement) - or to the end of the
	 * cycle (for complex measurement).
	 */
	public float stopTime;

	/** Current step (if multi-step measurement). */
	public int curStep;

	/**
	 * Current cycle (accumulation cycle in FLOW mode) - (if multi-cycle
	 * measurement).
	 */
	public int curCycle;

	/** Current measured page. */
	public int curPage;

	/** Minimum rates during the measurement. */
	public float minSyncRate;

	/** (-1.0 - not set). */
	public float minCfdRate;

	public float minTacRate;
	public float minAdcRate;

	/** Maximum rates during the measurement. */
	public float maxSyncRate;

	/** (-1.0 - not set). */
	public float maxCfdRate;

	public float maxTacRate;
	public float maxAdcRate;
	public int mReserved1;
	public float mReserved2;

	// -- Fields - MeasFCSInfo --

	public boolean hasMeasFCSInfo;

	/** Routing channel number. */
	public int chan;

	/**
	 * Bit 0 = 1 - decay curve calculated. Bit 1 = 1 - fcs curve calculated. Bit 2
	 * = 1 - FIDA curve calculated. Bit 3 = 1 - FILDA curve calculated. Bit 4 = 1
	 * - MCS curve calculated. Bit 5 = 1 - 3D Image calculated.
	 */
	public int fcsDecayCalc;

	/** Macro time clock in 0.1 ns units. */
	public long mtResol; // unsigned

	/** Correlation time [ms]. */
	public float cortime;

	/** No of photons. */
	public long calcPhotons; // unsigned

	/** No of FCS values. */
	public int fcsPoints;

	/** Macro time of the last photon. */
	public float endTime;

	/**
	 * No of Fifo overruns when &gt; 0 fcs curve &amp; endTime are not valid.
	 */
	public int overruns;

	/**
	 * 0 - linear FCS with log binning (100 bins/log) when bit 15 = 1 (0x8000) -
	 * Multi-Tau FCS where bits 14-0 = ktau parameter.
	 */
	public int fcsType;

	/**
	 * Cross FCS routing channel number when chan = crossChan and mod == crossMod
	 * - Auto FCS otherwise - Cross FCS.
	 */
	public int crossChan;

	/** Module number. */
	public int mod;

	/** Cross FCS module number. */
	public int crossMod;

	/** Macro time clock of cross FCS module in 0.1 ns units. */
	public long crossMtResol; // unsigned

	// -- Fields - extended MeasureInfo -

	public boolean hasExtendedMeasureInfo;

	/**
	 * 4 subsequent fields valid only for Camera mode or FIFO_IMAGE mode.
	 */
	public int imageX;
	public int imageY;
	public int imageRX;
	public int imageRY;

	/** Gain for XY ADCs (SPC930). */
	public short xyGain;

	/** Use or not Master Clock (SPC140 multi-module). */
	public short masterClock;

	/** ADC sample delay (SPC-930). */
	public short adcDE;

	/** Detector type (SPC-930 in camera mode). */
	public short detType;

	/** X axis representation (SPC-930). */
	public short xAxis;

	// -- Fields - MeasHISTInfo --

	public boolean hasMeasHISTInfo;

	/** Interval time [ms] for FIDA histogram. */
	public float fidaTime;

	/** Interval time [ms] for FILDA histogram. */
	public float fildaTime;

	/** No of FIDA values. */
	public int fidaPoints;

	/** No of FILDA values. */
	public int fildaPoints;

	/** Interval time [ms] for MCS histogram. */
	public float mcsTime;

	/** No of MCS values. */
	public int mcsPoints;

	// -- Fields - BHFileBlockHeader --

	/**
	 * Number of the block in the file. Valid only when in 0..0x7ffe range,
	 * otherwise use lblock_no field obsolete now, lblock_no contains full block
	 * no information.
	 */
	public short blockNo;

	/** Offset of the data block from the beginning of the file. */
	public int dataOffs;

	/** Offset to the data block header of the next data block. */
	public int nextBlockOffs;

	/** See blockType defines below. */
	public int blockType;

	/**
	 * Number of the measurement description block corresponding to this data
	 * block.
	 */
	public short measDescBlockNo;

	/** Long blockNo - see remarks below. */
	public long lblockNo; // unsigned

	/** reserved2 now contains block (set) length. */
	public long blockLength; // unsigned

	// -- Constructor --

	/**
	 * Constructs a new SDT header by reading values from the given input source,
	 * populating the given metadata table.
	 */
	public SDTInfo(final DataHandle<Location> handle,
		final MetaTable meta) throws IOException
	{
		// read bhfileHeader
		revision = handle.readShort();
		infoOffs = handle.readInt();
		infoLength = handle.readShort();
		setupOffs = handle.readInt();
		setupLength = handle.readShort();
		dataBlockOffs = handle.readInt();
		noOfDataBlocks = handle.readShort();
		dataBlockLength = handle.readInt();
		measDescBlockOffs = handle.readInt();
		noOfMeasDescBlocks = handle.readShort();
		measDescBlockLength = handle.readShort();
		headerValid = handle.readUnsignedShort();
		reserved1 = (0xffffffffL & handle.readInt()); // unsigned
		reserved2 = handle.readUnsignedShort();
		chksum = handle.readUnsignedShort();

		// save bhfileHeader to metadata table
		if (meta != null) {
			final String bhfileHeader = "bhfileHeader.";
			meta.put(bhfileHeader + "revision", new Short(revision));
			meta.put(bhfileHeader + "infoOffs", new Integer(infoOffs));
			meta.put(bhfileHeader + "infoLength", new Short(infoLength));
			meta.put(bhfileHeader + "setupOffs", new Integer(setupOffs));
			meta.put(bhfileHeader + "dataBlockOffs", new Integer(dataBlockOffs));
			meta.put(bhfileHeader + "noOfDataBlocks", new Short(noOfDataBlocks));
			meta.put(bhfileHeader + "dataBlockLength", new Integer(dataBlockLength));
			meta.put(bhfileHeader + "measDescBlockOffs", new Integer(
				measDescBlockOffs));
			meta.put(bhfileHeader + "noOfMeasDescBlocks", new Short(
				noOfMeasDescBlocks));
			meta.put(bhfileHeader + "measDescBlockLength", new Integer(
				measDescBlockLength));
			meta.put(bhfileHeader + "headerValid", new Integer(headerValid));
			meta.put(bhfileHeader + "reserved1", new Long(reserved1));
			meta.put(bhfileHeader + "reserved2", new Integer(reserved2));
			meta.put(bhfileHeader + "chksum", new Integer(chksum));
		}

		// read file info
		handle.seek(infoOffs);
		final byte[] infoBytes = new byte[infoLength];
		handle.readFully(infoBytes);
		info = new String(infoBytes, Constants.ENCODING);

		StringTokenizer st = new StringTokenizer(info, "\n");
		final int count = st.countTokens();
		st.nextToken();
		String key = null, value = null;
		for (int i = 1; i < count - 1; i++) {
			final String token = st.nextToken().trim();
			if (token.indexOf(":") == -1) continue;
			key = token.substring(0, token.indexOf(":")).trim();
			value = token.substring(token.indexOf(":") + 1).trim();
			if (meta != null) {
				meta.put(key, value);
			}
		}

		// read setup
		handle.seek(setupOffs);
		final byte[] setupBytes = new byte[setupLength];
		handle.readFully(setupBytes);
		setup = new String(setupBytes, Constants.ENCODING);

		st = new StringTokenizer(setup, "\n");
		while (st.hasMoreTokens()) {
			final String token = st.nextToken().trim();

			if (token.startsWith("#SP") || token.startsWith("#DI") ||
				token.startsWith("#PR") || token.startsWith("#MP"))
			{
				final int open = token.indexOf("[");
				key = token.substring(open + 1, token.indexOf(",", open));
				value = token.substring(token.lastIndexOf(",") + 1, token.length() - 1);
			}
			else if (token.startsWith("#TR") || token.startsWith("#WI")) {
				key = token.substring(0, token.indexOf("[")).trim();
				value = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
			}

			if (key != null && value != null && meta != null) meta.put(key, value);

			if (token.indexOf(X_STRING) != -1) {
				final int ndx = token.indexOf(X_STRING) + X_STRING.length();
				final int end = token.indexOf("]", ndx);
				width = Integer.parseInt(token.substring(ndx, end));
			}
			else if (token.indexOf(Y_STRING) != -1) {
				final int ndx = token.indexOf(Y_STRING) + Y_STRING.length();
				final int end = token.indexOf("]", ndx);
				height = Integer.parseInt(token.substring(ndx, end));
			}
			else if (token.indexOf(T_STRING) != -1) {
				final int ndx = token.indexOf(T_STRING) + T_STRING.length();
				final int end = token.indexOf("]", ndx);
				timeBins = Integer.parseInt(token.substring(ndx, end));
			}
			else if (token.indexOf(C_STRING1) != -1) {
				final int ndx = token.indexOf(C_STRING1) + C_STRING1.length();
				final int end = token.indexOf("]", ndx);
				channels =
					nonZeroProduct(channels, Integer.parseInt(token.substring(ndx, end)));
			}
			else if (token.indexOf(C_STRING2) != -1) {
				final int ndx = token.indexOf(C_STRING2) + C_STRING2.length();
				final int end = token.indexOf("]", ndx);
				channels =
					nonZeroProduct(channels, Integer.parseInt(token.substring(ndx, end)));
			}
		}

		// read measurement data
		if (noOfMeasDescBlocks > 0) {
			handle.seek(measDescBlockOffs);

			hasMeasureInfo = measDescBlockLength >= 211;
			hasMeasStopInfo = measDescBlockLength >= 211 + 60;
			hasMeasFCSInfo = measDescBlockLength >= 211 + 60 + 38;
			hasExtendedMeasureInfo = measDescBlockLength >= 211 + 60 + 38 + 26;
			hasMeasHISTInfo = measDescBlockLength >= 211 + 60 + 38 + 26 + 24;

			if (hasMeasureInfo) {
				time = handle.readString(9).trim();
				date = handle.readString(11).trim();
				modSerNo = handle.readString(16).trim();

				measMode = handle.readShort();
				cfdLL = handle.readFloat();
				cfdLH = handle.readFloat();
				cfdZC = handle.readFloat();
				cfdHF = handle.readFloat();
				synZC = handle.readFloat();
				synFD = handle.readShort();
				synHF = handle.readFloat();
				tacR = handle.readFloat();
				tacG = handle.readShort();
				tacOF = handle.readFloat();
				tacLL = handle.readFloat();
				tacLH = handle.readFloat();
				adcRE = handle.readShort();
				ealDE = handle.readShort();
				ncx = handle.readShort();
				ncy = handle.readShort();
				page = handle.readUnsignedShort();
				colT = handle.readFloat();
				repT = handle.readFloat();
				stopt = handle.readShort();
				overfl = handle.readUnsignedByte();
				useMotor = handle.readShort();
				steps = handle.readUnsignedShort();
				offset = handle.readFloat();
				dither = handle.readShort();
				incr = handle.readShort();
				memBank = handle.readShort();

				modType = handle.readString(16).trim();

				synTH = handle.readFloat();
				deadTimeComp = handle.readShort();
				polarityL = handle.readShort();
				polarityF = handle.readShort();
				polarityP = handle.readShort();
				linediv = handle.readShort();
				accumulate = handle.readShort();
				flbckY = handle.readInt();
				flbckX = handle.readInt();
				bordU = handle.readInt();
				bordL = handle.readInt();
				pixTime = handle.readFloat();
				pixClk = handle.readShort();
				trigger = handle.readShort();
				scanX = handle.readInt();
				scanY = handle.readInt();
				scanRX = handle.readInt();
				scanRY = handle.readInt();
				fifoTyp = handle.readShort();
				epxDiv = handle.readInt();
				modTypeCode = handle.readUnsignedShort();
				modFpgaVer = handle.readUnsignedShort();
				overflowCorrFactor = handle.readFloat();
				adcZoom = handle.readInt();
				cycles = handle.readInt();

				timepoints = stopt;

				// save MeasureInfo to metadata table
				if (meta != null) {
					final String measureInfo = "MeasureInfo.";
					meta.put(measureInfo + "time", time);
					meta.put(measureInfo + "date", date);
					meta.put(measureInfo + "modSerNo", modSerNo);
					meta.put(measureInfo + "measMode", new Short(measMode));
					meta.put(measureInfo + "cfdLL", new Float(cfdLL));
					meta.put(measureInfo + "cfdLH", new Float(cfdLH));
					meta.put(measureInfo + "cfdZC", new Float(cfdZC));
					meta.put(measureInfo + "cfdHF", new Float(cfdHF));
					meta.put(measureInfo + "synZC", new Float(synZC));
					meta.put(measureInfo + "synFD", new Short(synFD));
					meta.put(measureInfo + "synHF", new Float(synHF));
					meta.put(measureInfo + "tacR", new Float(tacR));
					meta.put(measureInfo + "tacG", new Short(tacG));
					meta.put(measureInfo + "tacOF", new Float(tacOF));
					meta.put(measureInfo + "tacLL", new Float(tacLL));
					meta.put(measureInfo + "tacLH", new Float(tacLH));
					meta.put(measureInfo + "adcRE", new Short(adcRE));
					meta.put(measureInfo + "ealDE", new Short(ealDE));
					meta.put(measureInfo + "ncx", new Short(ncx));
					meta.put(measureInfo + "ncy", new Short(ncy));
					meta.put(measureInfo + "page", new Integer(page));
					meta.put(measureInfo + "colT", new Float(colT));
					meta.put(measureInfo + "repT", new Float(repT));
					meta.put(measureInfo + "stopt", new Short(stopt));
					meta.put(measureInfo + "overfl", new Integer(overfl));
					meta.put(measureInfo + "useMotor", new Short(useMotor));
					meta.put(measureInfo + "steps", new Integer(steps));
					meta.put(measureInfo + "offset", new Float(offset));
					meta.put(measureInfo + "dither", new Short(dither));
					meta.put(measureInfo + "incr", new Short(incr));
					meta.put(measureInfo + "memBank", new Short(memBank));
					meta.put(measureInfo + "modType", modType);
					meta.put(measureInfo + "synTH", new Float(synTH));
					meta.put(measureInfo + "deadTimeComp", new Short(deadTimeComp));
					meta.put(measureInfo + "polarityL", new Short(polarityL));
					meta.put(measureInfo + "polarityF", new Short(polarityF));
					meta.put(measureInfo + "polarityP", new Short(polarityP));
					meta.put(measureInfo + "linediv", new Short(linediv));
					meta.put(measureInfo + "accumulate", new Short(accumulate));
					meta.put(measureInfo + "flbckY", new Integer(flbckY));
					meta.put(measureInfo + "flbckX", new Integer(flbckX));
					meta.put(measureInfo + "bordU", new Integer(bordU));
					meta.put(measureInfo + "bordL", new Integer(bordL));
					meta.put(measureInfo + "pixTime", new Float(pixTime));
					meta.put(measureInfo + "pixClk", new Short(pixClk));
					meta.put(measureInfo + "trigger", new Short(trigger));
					meta.put(measureInfo + "scanX", new Integer(scanX));
					meta.put(measureInfo + "scanY", new Integer(scanY));
					meta.put(measureInfo + "scanRX", new Integer(scanRX));
					meta.put(measureInfo + "scanRY", new Integer(scanRY));
					meta.put(measureInfo + "fifoTyp", new Short(fifoTyp));
					meta.put(measureInfo + "epxDiv", new Integer(epxDiv));
					meta.put(measureInfo + "modTypeCode", new Integer(modTypeCode));
					meta.put(measureInfo + "modFpgaVer", new Integer(modFpgaVer));
					meta.put(measureInfo + "overflowCorrFactor", new Float(
						overflowCorrFactor));
					meta.put(measureInfo + "adcZoom", new Integer(adcZoom));
					meta.put(measureInfo + "cycles", new Integer(cycles));
				}

				// extract dimensional parameters from measure info
				if (scanX > 0) width = scanX;
				if (scanY > 0) height = scanY;
				if (adcRE > 0) timeBins = adcRE;
				if (scanRX > 0 || scanRY > 0) {
					channels = nonZeroProduct(scanRX, scanRY);
				}
			}

			if (hasMeasStopInfo) {
				// MeasStopInfo - information collected when measurement is finished
				status = handle.readUnsignedShort();
				flags = handle.readUnsignedShort();
				stopTime = handle.readFloat();
				curStep = handle.readInt();
				curCycle = handle.readInt();
				curPage = handle.readInt();
				minSyncRate = handle.readFloat();
				minCfdRate = handle.readFloat();
				minTacRate = handle.readFloat();
				minAdcRate = handle.readFloat();
				maxSyncRate = handle.readFloat();
				maxCfdRate = handle.readFloat();
				maxTacRate = handle.readFloat();
				maxAdcRate = handle.readFloat();
				mReserved1 = handle.readInt();
				mReserved2 = handle.readFloat();

				// save MeasStopInfo to metadata table
				if (meta != null) {
					final String measStopInfo = "MeasStopInfo.";
					meta.put(measStopInfo + "status", new Integer(status));
					meta.put(measStopInfo + "flags", new Integer(flags));
					meta.put(measStopInfo + "stopTime", new Float(stopTime));
					meta.put(measStopInfo + "curStep", new Integer(curStep));
					meta.put(measStopInfo + "curCycle", new Integer(curCycle));
					meta.put(measStopInfo + "curPage", new Integer(curPage));
					meta.put(measStopInfo + "minSyncRate", new Float(minSyncRate));
					meta.put(measStopInfo + "minCfdRate", new Float(minCfdRate));
					meta.put(measStopInfo + "minTacRate", new Float(minTacRate));
					meta.put(measStopInfo + "minAdcRate", new Float(minAdcRate));
					meta.put(measStopInfo + "maxSyncRate", new Float(maxSyncRate));
					meta.put(measStopInfo + "maxCfdRate", new Float(maxCfdRate));
					meta.put(measStopInfo + "maxTacRate", new Float(maxTacRate));
					meta.put(measStopInfo + "maxAdcRate", new Float(maxAdcRate));
					meta.put(measStopInfo + "reserved1", new Integer(mReserved1));
					meta.put(measStopInfo + "reserved2", new Float(mReserved2));
				}
			}

			if (hasMeasFCSInfo) {
				// MeasFCSInfo - information collected when FIFO measurement is finished
				chan = handle.readUnsignedShort();
				fcsDecayCalc = handle.readUnsignedShort();
				mtResol = (0xffffffffL & handle.readInt()); // unsigned
				cortime = handle.readFloat();
				calcPhotons = (0xffffffffL & handle.readInt()); // unsigned
				fcsPoints = handle.readInt();
				endTime = handle.readFloat();
				overruns = handle.readUnsignedShort();
				fcsType = handle.readUnsignedShort();
				crossChan = handle.readUnsignedShort();
				mod = handle.readUnsignedShort();
				crossMod = handle.readUnsignedShort();
				crossMtResol = (0xffffffffL & handle.readInt()); // unsigned

				// save MeasFCSInfo to metadata table
				if (meta != null) {
					final String measFCSInfo = "MeasFCSInfo.";
					meta.put(measFCSInfo + "chan", new Integer(chan));
					meta.put(measFCSInfo + "fcsDecayCalc", new Integer(fcsDecayCalc));
					meta.put(measFCSInfo + "mtResol", new Long(mtResol));
					meta.put(measFCSInfo + "cortime", new Float(cortime));
					meta.put(measFCSInfo + "calcPhotons", new Long(calcPhotons));
					meta.put(measFCSInfo + "fcsPoints", new Integer(fcsPoints));
					meta.put(measFCSInfo + "endTime", new Float(endTime));
					meta.put(measFCSInfo + "overruns", new Integer(overruns));
					meta.put(measFCSInfo + "fcsType", new Integer(fcsType));
					meta.put(measFCSInfo + "crossChan", new Integer(crossChan));
					meta.put(measFCSInfo + "mod", new Integer(mod));
					meta.put(measFCSInfo + "crossMod", new Integer(crossMod));
					meta.put(measFCSInfo + "crossMtResol", new Float(crossMtResol));
				}
			}

			if (hasExtendedMeasureInfo) {
				imageX = handle.readInt();
				imageY = handle.readInt();
				imageRX = handle.readInt();
				imageRY = handle.readInt();
				xyGain = handle.readShort();
				masterClock = handle.readShort();
				adcDE = handle.readShort();
				detType = handle.readShort();
				xAxis = handle.readShort();

				// save extra MeasureInfo to metadata table
				if (meta != null) {
					final String measureInfo = "MeasureInfo.";
					meta.put(measureInfo + "imageX", new Integer(imageX));
					meta.put(measureInfo + "imageY", new Integer(imageY));
					meta.put(measureInfo + "imageRX", new Integer(imageRX));
					meta.put(measureInfo + "imageRY", new Integer(imageRY));
					meta.put(measureInfo + "xyGain", new Short(xyGain));
					meta.put(measureInfo + "masterClock", new Short(masterClock));
					meta.put(measureInfo + "adcDE", new Short(adcDE));
					meta.put(measureInfo + "detType", new Short(detType));
					meta.put(measureInfo + "xAxis", new Short(xAxis));
				}
			}

			if (hasMeasHISTInfo) {
				// MeasHISTInfo - extension of FCSInfo, valid only for FIFO meas
				// extension of MeasFCSInfo for other histograms (FIDA, FILDA, MCS)
				fidaTime = handle.readFloat();
				fildaTime = handle.readFloat();
				fidaPoints = handle.readInt();
				fildaPoints = handle.readInt();
				mcsTime = handle.readFloat();
				mcsPoints = handle.readInt();

				// save MeasHISTInfo to metadata table
				if (meta != null) {
					final String measHISTInfo = "MeasHISTInfo.";
					meta.put(measHISTInfo + "fidaTime", new Float(fidaTime));
					meta.put(measHISTInfo + "fildaTime", new Float(fildaTime));
					meta.put(measHISTInfo + "fidaPoints", new Integer(fidaPoints));
					meta.put(measHISTInfo + "fildaPoints", new Integer(fildaPoints));
					meta.put(measHISTInfo + "mcsTime", new Float(mcsTime));
					meta.put(measHISTInfo + "mcsPoints", new Integer(mcsPoints));
				}
			}
		}

		handle.seek(dataBlockOffs);

		readBlockHeader(handle);

		// save BHFileBlockHeader to metadata table
		if (meta != null) {
			final String bhFileBlockHeader = "BHFileBlockHeader.";
			meta.put(bhFileBlockHeader + "blockNo", new Short(blockNo));
			meta.put(bhFileBlockHeader + "dataOffs", new Integer(dataOffs));
			meta.put(bhFileBlockHeader + "nextBlockOffs", new Integer(nextBlockOffs));
			meta.put(bhFileBlockHeader + "blockType", new Integer(blockType));
			meta.put(bhFileBlockHeader + "measDescBlockNo",
				new Short(measDescBlockNo));
			meta.put(bhFileBlockHeader + "lblockNo", new Long(lblockNo));
			meta.put(bhFileBlockHeader + "blockLength", new Long(blockLength));
		}

		// similar logic to TRI2, to "account for SPC-152 type images"
		if (FIFO_IMAGE_MODE == measMode) {
			if (imageX > 0) width = imageX;
			if (imageY > 0) height = imageY;
			if (imageRX > 0 || imageRY > 0) {
				channels = nonZeroProduct(scanRX, scanRY);
			}
			channels *= noOfDataBlocks;
		}
	}

	/**
	 * Convenience method for reading from a block header section of an SDT header
	 * stream. Updates this SDTInfo object based on the read information. Fields
	 * updated are:
	 * <ul>
	 * <li>{@link #blockNo}</li>
	 * <li>{@link #dataOffs}</li>
	 * <li>{@link #nextBlockOffs}</li>
	 * <li>{@link #blockType}</li>
	 * <li>{@link #measDescBlockNo}</li>
	 * <li>{@link #lblockNo}</li>
	 * <li>{@link #blockLength}</li>
	 * </ul>
	 * 
	 * @param stream - stream to read from
	 * @throws IOException
	 */
	public void readBlockHeader(final DataHandle<Location> stream)
		throws IOException
	{
		// read BHFileBlockHeader
		blockNo = stream.readShort();
		dataOffs = stream.readInt();
		nextBlockOffs = stream.readInt();
		blockType = stream.readUnsignedShort();
		measDescBlockNo = stream.readShort();
		lblockNo = (0xffffffffL & stream.readInt()); // unsigned
		blockLength = (0xffffffffL & stream.readInt()); // unsigned
	}

	// -- Helper methods --

	private int nonZeroProduct(final int... args) {
		int product = 1;
		for (final int arg : args) {
			if (arg > 0) product *= arg;
		}
		return product;
	}
}
