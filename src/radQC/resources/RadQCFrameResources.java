package radQC.resources;

import java.util.ListResourceBundle;

/**
 * Main class resources <br>
 * 
 * @author Dan Fulea, 21 Apr. 2015
 */
public class RadQCFrameResources extends ListResourceBundle{

	/**
	 * Returns the array of strings in the resource bundle.
	 * 
	 * @return the resources.
	 */
	public Object[][] getContents() {
		// TODO Auto-generated method stub
		return CONTENTS;
	}

	/** The resources to be localised. */
	private static final Object[][] CONTENTS = {

			// displayed images..
			{ "form.icon.url", "/danfulea/resources/personal.png" },///jdf/resources/duke.png" },
			{ "icon.url", "/danfulea/resources/personal.png" },///jdf/resources/globe.gif" },

			{ "img.zoom.in", "/danfulea/resources/zoom_in.png" },
			{ "img.zoom.out", "/danfulea/resources/zoom_out.png" },
			{ "img.pan.left", "/danfulea/resources/arrow_left.png" },
			{ "img.pan.up", "/danfulea/resources/arrow_up.png" },
			{ "img.pan.down", "/danfulea/resources/arrow_down.png" },
			{ "img.pan.right", "/danfulea/resources/arrow_right.png" },
			{ "img.pan.refresh", "/danfulea/resources/arrow_refresh.png" },

			{ "img.accept", "/danfulea/resources/accept.png" },
			{ "img.insert", "/danfulea/resources/add.png" },
			{ "img.delete", "/danfulea/resources/delete.png" },
			{ "img.delete.all", "/danfulea/resources/bin_empty.png" },
			{ "img.view", "/danfulea/resources/eye.png" },
			{ "img.set", "/danfulea/resources/cog.png" },
			{ "img.report", "/danfulea/resources/document_prepare.png" },
			{ "img.today", "/danfulea/resources/time.png" },
			{ "img.open.file", "/danfulea/resources/open_folder.png" },
			{ "img.open.database", "/danfulea/resources/database_connect.png" },
			{ "img.save.database", "/danfulea/resources/database_save.png" },
			{ "img.substract.bkg", "/danfulea/resources/database_go.png" },
			{ "img.close", "/danfulea/resources/cross.png" },
			{ "img.about", "/danfulea/resources/information.png" },
			{ "img.printer", "/danfulea/resources/printer.png" },
					
			{ "Application.NAME", "RadQC - Quality control for Radiological Devices" },
			{ "About.NAME", "About" },
			{ "MedicalUnitFrame.NAME", "Medical unit database" },
			{ "DeviceTypeFrame.NAME", "X-ray device type database" },
			{ "LocationFrame.NAME", "Location database" },
			{ "DisplayInformationFrame.NAME", "Information" },
			{ "DisplayInformation.border", "Information" },
			{ "SearchFrame.NAME", "Search in database" },
			{ "FocalSpotFrame.NAME", "Focal spot estimation" },
			{ "InterfaceFrame.NAME", "Interface/Cavity study" },
			
			{ "Author", "Author:" },
			{ "Author.name", "Dan Fulea , fulea.dan@gmail.com" },

			{ "Version", "Version:" },
			{ "Version.name", "RadQC 1.1.1" },

			{
					"License",
					//"This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License (version 2) as published by the Free Software Foundation. \n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. \n" },
			"Copyright (c) 2014, Dan Fulea \nAll rights reserved.\n\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n\n1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n\n2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\nTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" },
			
			//=================
			{ "menu.file", "File" },
			{ "menu.file.mnemonic", new Character('F') },
			
			{ "menu.tools", "Tools" },
			{ "menu.tools.mnemonic", new Character('T') },

			{ "menu.file.mtf", "MTF computation..." },
			{ "menu.file.mtf.mnemonic", new Character('M') },
			{ "menu.file.mtf.toolTip", "Computation of MTF (Modular transfer function)" },
			
			{ "menu.file.fs", "Focal spot estimation..." },
			{ "menu.file.fs.mnemonic", new Character('o') },
			{ "menu.file.fs.toolTip", "Computation of focal spot (NEMA standard usage)" },

			{ "menu.tools.sensitometry", "Film sensitometry" },
			{ "menu.tools.sensitometry.mnemonic", new Character('s') },
			{ "menu.tools.sensitometry.toolTip", "Perform film sensitometry" },
			
			{ "menu.tools.interface", "Interface/Cavity study" },
			{ "menu.tools.interface.mnemonic", new Character('I') },
			{ "menu.tools.interface.toolTip", "Perform kerma and dose computation for interface of two media as well as corection factor for cavity" },
			
			{ "menu.file.exit", "Close" },
			{ "menu.file.exit.mnemonic", new Character('C') },
			{ "menu.file.exit.toolTip", "Close the application" },

			{ "menu.help", "Help" },
			{ "menu.help.mnemonic", new Character('H') },

			{ "menu.help.about", "About..." },
			{ "menu.help.about.mnemonic", new Character('A') },
			{ "menu.help.about.toolTip",
					"Several informations about this application" },

			{ "menu.help.LF", "Look and feel..." },
			{ "menu.help.LF.mnemonic", new Character('L') },
			{ "menu.help.LF.toolTip", "Change application look and feel" },
			
			//=====================================
			
			{ "status.save", "Saved: " },
			{ "status.error", "Unexpected error!" },
			
			{ "number.error", "Insert valid positive numbers! " },
			{ "number.error.title", "Unexpected error" },
									
			{ "dialog.exit.title", "Confirm..." },
			{ "dialog.exit.message", "Are you sure?" },
			{ "dialog.exit.buttons", new Object[] { "Yes", "No" } },
			
			//========DB and records@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			{ "main.db", "radqc" },
			{ "main.db.deviceTable", "device" },//main radiographic table
			{ "main.db.deviceTable.mammo", "devicemammo" },//main mammo table
			{ "main.db.deviceTable.fluoro", "devicefluoro" },//main fluoro table
			{ "main.db.deviceTable.ct", "devicect" },//main ct table
			
			{ "main.db.muTable", "mu" },//medical unit table
			{ "main.db.deviceTypeTable", "deviceType" },//X-ray Device type table
			{ "main.db.locationTable", "location" },//Location table
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			{ "records.border", "Records" },
			{ "records.label", "Records:" },
			
			{ "main.db.hvlFiltrationLimitsTable", "hvlFiltrationLimits" },
			{ "main.db.hvlFiltrationLimitsTable.mammo", "hvlFiltrationLimitsMammo" },
			{ "main.db.hvlFiltrationTable", "hvlFiltration" },
			{ "main.db.hvlFiltrationTable.mammo", "hvlFiltrationMammo" },
			{ "main.db.hvlFiltrationTable.fluoro", "hvlFiltrationFluoro" },
			{ "main.db.hvlFiltrationTable.ct", "hvlFiltrationCt" },
			//====================================================================================kvt
			{ "main.db.kv.RepeatabilityTable", "kvRepeatability" },
			{ "main.db.kv.RepeatabilityTable.detail", "kvRepeatabilityDetail" },
			{ "main.db.t.RepeatabilityTable", "tRepeatability" },
			{ "main.db.t.RepeatabilityTable.detail", "tRepeatabilityDetail" },
			{ "main.db.kv.AccuracyTable", "kvAccuracy" },			
			{ "main.db.t.AccuracyTable", "tAccuracy" },
						
			{ "main.db.kv.RepeatabilityTable.mammo", "kvRepeatabilityMammo" },
			{ "main.db.kv.RepeatabilityTable.detail.mammo", "kvRepeatabilityDetailMammo" },
			{ "main.db.t.RepeatabilityTable.mammo", "tRepeatabilityMammo" },
			{ "main.db.t.RepeatabilityTable.detail.mammo", "tRepeatabilityDetailMammo" },
			{ "main.db.kv.AccuracyTable.mammo", "kvAccuracyMammo" },			
			{ "main.db.t.AccuracyTable.mammo", "tAccuracyMammo" },
			
			{ "main.db.output.Table", "outputTable" },
			{ "main.db.output.RepeatabilityTable", "outputRepeatability" },
			{ "main.db.output.RepeatabilityTable.detail", "outputRepeatabilityDetail" },
			{ "main.db.output.LinearityTable", "outputLinearity" },
			{ "main.db.output.LinearityTable.detail", "outputLinearityDetail" },
			
			{ "main.db.output.Table.mammo", "outputTablemammo" },
			{ "main.db.output.RepeatabilityTable.mammo", "outputRepeatabilitymammo" },
			{ "main.db.output.RepeatabilityTable.detail.mammo", "outputRepeatabilityDetailmammo" },
			{ "main.db.output.LinearityTable.mammo", "outputLinearitymammo" },
			{ "main.db.output.LinearityTable.detail.mammo", "outputLinearityDetailmammo" },
			
			{ "main.db.aec.RepeatabilityTable", "aecRepeatability" },
			{ "main.db.aec.RepeatabilityTable.detail", "aecRepeatabilityDetail" },
			{ "main.db.aec.LinearityTable", "aecLinearity" },
			{ "main.db.aec.LinearityTable.detail", "aecLinearityDetail" },
			
			{ "main.db.aec.RepeatabilityTable.mammo", "aecRepeatabilitymammo" },
			{ "main.db.aec.RepeatabilityTable.detail.mammo", "aecRepeatabilityDetailmammo" },
			{ "main.db.aec.LinearityTable.mammo", "aecLinearitymammo" },
			{ "main.db.aec.LinearityTable.detail.mammo", "aecLinearityDetailmammo" },
			
			{ "main.db.other.Table", "otherTable" },
			{ "main.db.other.Table.mammo", "otherTablemammo" },
			{ "main.db.other.Table.fluoro", "otherTablefluoro" },
			{ "main.db.other.Table.ct", "otherTablect" },
			
			{ "main.db.dose.Table", "doseTable" },//rad
			{ "main.db.dose.Table.mammo", "doseTablemammo" },
			{ "main.db.dose.Table.fluoro", "doseTablefluoro" },
			{ "main.db.dose.Table.ct", "doseTablect" },
			//===============================@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			{ "status.default.label", "Database table: "},
			{ "status.default.rad.label", "Radiography"},
			{ "status.default.mammo.label", "Mammography"},
			{ "status.default.fluoro.label", "Fluoroscopy"},
			{ "status.default.ct.label", "CT"},
									
			{ "difference.yes", "Yes" },
			{ "difference.no", "No" },

			//=======================
			//========GUI==========================
			{ "main.MU.label", "Medical unit: " },
			{ "main.MU.button", "Update MU" },
			{ "main.MU.button.mnemonic", new Character('M') },
			{ "main.MU.button.toolTip", "Update medical unit database" },
			{ "main.dept.label", "Department: " },

			{ "main.device.label", "X-ray device: " },
			{ "main.device.button", "Update device" },
			{ "main.device.button.mnemonic", new Character('p') },
			{ "main.device.button.toolTip", "Update X-ray device database" },
			{ "main.sn.label", "Serial number: " },

			{ "main.location.label", "Location: " },
			{ "main.location.button", "Update location" },
			{ "main.location.button.mnemonic", new Character('l') },
			{ "main.location.button.toolTip", "Update location database" },
			{ "main.manufacture.label", "Device manufacture date (yr): " },
			{ "main.county.label", "County/District: " },
			
			{ "main.telephone.label", "Telephone: " },
			{ "main.email.label", "Email: " },
			{ "main.contact.label", "Contact name: " },
			{ "main.notes.label", "Additional information: " },
			
			{ "main.button.today", "Today" },
			{ "main.button.today.toolTip", "Set the date at today" },
			{ "main.button.today.mnemonic", new Character('y') },
			{ "main.date.day", "Day: " },
			{ "main.date.month", "Month: " },
			{ "main.date.year", "Year: " },
			{ "main.date.border", "Measurement date" },
			
			{ "main.qc.label", "Quality control test: " },
			{ "main.qc.button", "Test" },
			{ "main.qc.button.mnemonic", new Character('Q') },
			{ "main.qc.button.toolTip", "Perform quality control test" },
			
			{ "main.radiography.rb", "Radiography" },
			{ "main.radiography.rb.toolTip", "Radiography specific QC tests" },
			{ "main.mammography.rb", "Mammography" },
			{ "main.mammography.rb.toolTip", "Mamoography specific QC tests" },
			{ "main.fluoroscopy.rb", "Fluoroscopy" },
			{ "main.fluoroscopy.rb.toolTip", "Fluoroscopy specific QC tests" },
			{ "main.ct.rb", "CT" },
			{ "main.ct.rb.toolTip", "CT specific QC tests" },
			{ "main.rb.border", "Examination type" },

			{ "main.display.button", "Display record information" },
			{ "main.display.button.mnemonic", new Character('y') },
			{ "main.display.button.toolTip", "Display record information and its last QC tests result (if any)" },
			
			{ "main.update.button", "Update" },
			{ "main.update.button.mnemonic", new Character('U') },
			{ "main.update.button.toolTip", "Update data from database" },
			
			{ "main.refresh.button", "Refresh" },
			{ "main.refresh.button.mnemonic", new Character('R') },
			{ "main.refresh.button.toolTip", "Display all records from database" },

			{ "main.search.button", "Search..." },
			{ "main.search.button.mnemonic", new Character('S') },
			{ "main.search.button.toolTip", "Search data" },

			{ "mu.insert.button", "Insert" },
			{ "mu.insert.button.mnemonic", new Character('I') },
			{ "mu.insert.button.toolTip", "Insert data in database" },
			
			{ "mu.delete.button", "Delete" },
			{ "mu.delete.button.mnemonic", new Character('D') },
			{ "mu.delete.button.toolTip", "Delete data from database" },
			
			{ "display.report", "Pdf print..." },
			{ "display.report.toolTip", "Save report as pdf file" },
			{ "display.report.mnemonic", new Character('P') },
			
			{ "main.location.ch", "Location" },
			{ "main.medicalUnit.ch", "Medical unit" },
			{ "main.device.ch", "X-Ray device type" },
			
			{ "main.key.border", "Search keys" },
			{ "main.param.border", "Search parameters (case-sensitive!)" },

			{ "search.search.button", "Search in database" },
			{ "search.search.button.mnemonic", new Character('S') },
			{ "search.search.button.toolTip", "Search in database" },
			
			{ "db.location.columnName", "Location" },
			{ "db.medicalUnit.columnName", "Medical_unit" },
			{ "db.device.columnName", "XRay_device" },

			//========================
			{ "main.display.mu", "Medical unit: " },
			{ "main.display.dept", "Department: " },
			{ "main.display.device", "X-Ray device type: " },
			{ "main.display.sn", "Serial number: " },
			{ "main.display.manufacture", "Manufacture date: " },
			{ "main.display.county", "County: " },
			{ "main.display.location", "Location: " },
			{ "main.display.tel", "Telephone: " },
			{ "main.display.email", "Email: " },
			{ "main.display.contact", "Contact name: " },
			{ "main.display.notes", "Additional information: " },
			{ "main.display.examination", "Device used in: " },
			
			{ "main.display.measurementDate", "Measurement date [YYYY-MM-DD]: " },
			{ "main.display.hvl", "HVL [mmAl]: " },
			{ "main.display.hvl.kv", " , at kVp: " },
			{ "main.display.hvl.test", "HVL test status: " },
			{ "main.display.filtration", "Total tube filtration [mmAl]: " },
			{ "main.display.filtration.test", "Total tube filtration test status: " },
			
			{ "main.display.variation", "Variation: " },
			{ "main.display.limit", "Limit: " },
			{ "main.display.kvRepeatability.test", "kV repeatability test status: " },
			{ "main.display.tRepeatability.test", "Exposure time repeatability test status: " },
			{ "main.display.set", "Set: " },
			{ "main.display.measured", "Measured: " },
			{ "main.display.kvAccuracy.test", "kV accuracy test status: " },
			{ "main.display.tAccuracy.test", "Exposure time accuracy test status: " },
			
			{ "main.display.output", "Tube output at reference settings [uGy/mAs]: " },
			{ "main.display.output.limit", "Limit [uGy/mAs]: " },
			{ "main.display.output.test", "Tube output test status: " },
			{ "main.display.outputRepeatability.test", "Tube output repeatability test status: " },
			{ "main.display.outputLinearity.test", "mAs linearity test status: " },
			
			{ "main.display.aecRepeatability.test", "AEC repeatability test status: " },
			{ "main.display.aecLinearity.test", "AEC compensation test status: " },
			
			{ "display.dosimetry.records", "All dosimetry records:" },
			{ "display.dosimetry.exam", "Examination: " },
			{ "display.dosimetry.patient", "Patient simulation: " },
			{ "display.dosimetry.tube", "Tube parameters: " },
			{ "display.dosimetry.ripple", "waveform ripple: " },
			{ "display.dosimetry.anodeAngle", "anode angle: " },
			{ "display.dosimetry.anodeMaterial", "anode material: " },
			{ "display.dosimetry.filtration", "total filtration: " },
			{ "display.dosimetry.unc", "Measurement overall uncertainty [%]: " },
			{ "display.dosimetry.ESAK", "ESAK, Entrance Surface Air Kerma (with backscatter) [mGy]: " },
			{ "display.dosimetry.DRL", "DRL, Diagnostic Reference Level [mGy]: " },
			{ "display.dosimetry.test", "Test result: " },
			{ "display.dosimetry.KAIR", "KAIR, Kerma free in AIR at entrance surface (without backscatter) [mGy]: " },
			{ "display.dosimetry.FSD", "FSD, Focus to entrance Surface Distance [cm]: " },
			{ "display.dosimetry.KAP", "KAP, Kerma free in air - Area Product [uGy x mm2]: " },
			{ "display.dosimetry.effectiveDose", "Effective dose: " },
			{ "display.dosimetry.risk", "Lifetime fatal cancer risk [cases/million population]: " },
			
			{ "display.dosimetry.fluoro.doseRate", "Dose rate [mGy/min]: " },
			{ "display.dosimetry.fluoro.maxDoseRate", "Maximum permissible dose rate [mGy/min]: " },
			{ "display.dosimetry.fluoro.test", "Test result: " },
			
			{ "display.dosimetry.breastDiameter", "Breast diameter: " },
			{ "display.dosimetry.breastThickness", "Breast thickness: " },
			{ "display.dosimetry.AGD", "AGD, Average Glandular Dose (computed using pre-build Monte Carlo conversion factors from literature) [mGy]: " },
			{ "display.dosimetry.AGD.limit", "Maximum permissible AGD [mGy]: " },
			{ "display.dosimetry.breastDose", "Dose in breast (Monte Carlo): " },
			
			{ "display.dosimetry.CTDI", "CTDIc free, CT Dose Index free in air measured on central axis (at patient mid-plane) (kerma/rotation) [mGy]: " },
			{ "display.dosimetry.CTDIvol", "CTDIvol, CT Dose Index on CT phantom (kerma/rotation) [mGy]: " },
			{ "display.dosimetry.DLP", "DLP, Dose Length Product index (CTDIc,free x length of scan) [mGy x cm]: " },
			{ "display.dosimetry.sliceT", "Slice thickness [mm]: " },
			{ "display.dosimetry.pitch", "Pitch: " },
			{ "display.dosimetry.rotAngleInc", "Rotation angle increment [deg]: " },
			{ "display.dosimetry.fanBeam", "Fan beam geometry: " },
			{ "display.dosimetry.FSD.ct", "FCA, Focus to Central Axis Distance [cm]: " },
			//=======================================================
			{ "main.gctest.rad.hvl", "HVL/Total filtration" },
			{ "main.gctest.rad.accuracy", "Tube voltage/Exposure time" },//repeatability and accuracy
			{ "main.gctest.rad.output", "Output/Repeatability/mAs linearity" },
			{ "main.gctest.rad.alignment", "Film sensitometry" },//NA
			{ "main.gctest.rad.dose", "Dosimetry for radiography" },//to be used in further MC-software such as GEANT4 based online server
			{ "main.gctest.rad.highcontrast", "High contrast resolution/focal spot" },//NA
			{ "main.gctest.rad.lowcontrast", "Low contrast resolution" },//NA
			{ "main.gctest.rad.other", "Others..." },//high, low contrast, alignment etc.
			{ "main.gctest.rad.aec", "AEC repeatability/compensation" },
			
			{ "main.gctest.mammo.hvl", "HVL/Total filtration" },
			{ "main.gctest.mammo.accuracy", "Tube voltage/Exposure time" },
			{ "main.gctest.mammo.output", "Output/Repetability/mAs linearity" },
			{ "main.gctest.mammo.alignment", "Film sensitometry" },//NA, alignment=>to Other
			{ "main.gctest.mammo.highcontrast", "High contrast resolution" },//NA=>to Other
			{ "main.gctest.mammo.lowcontrast", "Low contrast resolution" },//NA=>to Other
			{ "main.gctest.mammo.agd", "Dosimetry for mammography" },
			{ "main.gctest.mammo.other", "Others..." },
			{ "main.gctest.mammo.aec", "AEC repeatability/compensation" },
			
			{ "main.gctest.fluoro.hvl", "HVL/Total filtration" },//NA->Rg mode
			{ "main.gctest.fluoro.output", "Output/Repetability/mAs linearity" },//NA->Rg mode
			{ "main.gctest.fluoro.highcontrast", "High contrast resolution" },//NA
			{ "main.gctest.fluoro.lowcontrast", "Low contrast resolution" },//NA
			{ "main.gctest.fluoro.dose", "Dosimetry for fluoroscopy" },
			{ "main.gctest.fluoro.other", "Others..." },
			
			{ "main.gctest.ct.hvl", "HVL/Total filtration" },
			{ "main.gctest.ct.output", "Dosimetry for CT" },
			{ "main.gctest.ct.other", "Others..." },
			
			{ "main.nolink.error.title", "No X-ray device in database" },
			{ "main.nolink.error.message", "QC test is assigned to a valid X-ray device in database" },
			{ "main.nolink.error2.message", "Nothing to display!" },
			
			//==============
			{"dialog.selectNul.title", "Error...."},
	        {"dialog.selectNul.message", "Select a single record!"},
	        {"dialog.delete.title", "Confirm delete"},
	        {"dialog.delete.message", "Are you sure?\n"+
	                                "All entry associated data will be erased!"},
	                                
	        {"dialog.update.title", "Confirm update"},
	        {"dialog.update.message", "Are you sure?\n"+
	                    	         "Current entry information will be overwritten!"},	                                
			//=================================			
			{ "pdf.metadata.title", "RadQC PDF" },
			{ "pdf.metadata.subject", "RadQC analysis results" },
			{ "pdf.metadata.keywords", "RadQC, PDF" },
			{ "pdf.metadata.author", "RadQC" },
			{ "pdf.content.title", "RadQC - General Report" },
			{ "pdf.content.subtitle", "Report generated by: " },
			{ "pdf.page", "Page " },
			{ "pdf.header", "RadQC output" },
			{ "file.extension", "pdf" },
			{ "file.description", "PDF file" },
			{ "dialog.overwrite.title", "Overwriting..." },
			{ "dialog.overwrite.message", "Are you sure?" },
			{ "dialog.overwrite.buttons", new Object[] { "Yes", "No" } },

			{"interf.media1.cb", new String[] {"Air","Aluminium","G4_PLEXIGLASS","Lead","soil_typicalloam_seltzer","Water"}},
			{"interf.cavity.cb", new String[] {"Air","ArCO2_80_20"}},
			{ "interf.media1.label", "First media material: " },
			{ "interf.media2.label", "Second media (cavity) material: " },
			{ "interf.cavity.label", "Cavity effective thickness [cm]: " },
			{ "interf.energy.label", "If not a spectrum, enter incident gamma energy [MeV]: " },
			{"autorefresh.ch","View graphics"},
			{"spectrum.ch","Is X-ray spectrum"},
			{"runs.label", "Number of runs (MonteCarlo): "},
			{"calcB", "Compute"},
	        {"calcB.mnemonic", new Character('C')},
	        {"calcB.toolTip", "Perform calculation"},
	        {"kv.label", "Kilovoltage: "},			
			{"filtration.label", "Total tube filtration taken from database (if no record then a generic value of 2.5 is displayed) [mmAl]: "},
			{"anodeAngle.label", "Anode angle [deg]: "},
			{"ripple.label", "Waveform ripple: "},
			//{"mAs.label", "mAs (not mandatory if KAP is available): "},
			{"tube.border", "Tube settings"},
			{"dialog.number.title", "Error"},
	        {"dialog.number.message", "Insert real numbers!"},
	        {"dialog.cavity.message", "Invalid cavity thickness"},
	        {"dialog.energy.message", "Invalid energy value"},
	        {"dialog.filtration.message", "Invalid filtration value"},
	        {"dialog.run.message", "Invalid number of runs value"},
	        {"dialog.ripple", "If anode=Mo or Rh, angle>=9,angle <=23;kv>=25,kv<=32;ripple=0; \n if anod=W, angle >=6 angle<=22;kv>=30;kv<=150!! \n for NONZERO ripple=>kv =55,60,65,...,90!!"},
	        {"dialog.number.negative.message", "Insert positive numbers!"},
	        
	        {"interf.INFO", "BIPM (Bureau International des Poids et Mesures) can perform calibration of exposure (X) for BIPM standard ionization chamber using various X-ray tube settings or various standard gamma sources such 137Cs or 60Co. Exposure X=DQ/Dm and all electrons are stopped in Dm (definition of exposure) hence EEC is met (Electron Equilibrium Condition). From X, KERMA K is simply: K=(W/e) X /(1-g). W/e in air is known to be 33.07J/C. The radiative correction factor (fraction of electron energy loss by bremsstrahlung ) g is knwon for various sources (e.g. MC simulations). Of course, for BIPM standard, the collision kerma K(1-g) is equal with the absorbed dose in standard ionization chamber.\n\n All other dosimeters, regardless of their dimensions can be calibrated versus primary BIPM standard in terms of kerma in air using a calibration factor (kerma  = reading x cal_factor). It doesn't matter if electrons are stopped or not in the dosimeter chamber which is CONSISTENT WITH KERMA DEFINITION VERSUS EXPOSURE. That's why KERMA is a powerful concept. Of course, under EEC and when radiative loss of electron energy is neglected, KERMA equals the dose absorbed in cavity (dosimeter) material. Dosimeters should be used in exposure conditions closed to calibration ones (closed enough to perform some 'light' corrections such as pressure, temperature, HVL).\n\n Regardless of calibration condition, the dosimeter reading is always proportional with dose absorbed in dosimeter active volume. When using dosimeter in exposure condition far from calibration condition, such as close to interface of media, we must compute corection factors using MC tehniques for absorbed dose in cavity and surrounding region (esspecially when we cannot apply cavity theory)"},

	};
}
