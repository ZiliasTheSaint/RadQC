package xrtf_mAs.resources;

import java.util.ListResourceBundle;

/**
 * The resource bundle for xrtf_mAs project, main class. <br>
 * 
 * @author Dan Fulea, 11 Jun. 2013
 * 
 */
public class MainFrameResources extends ListResourceBundle{
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

			// menu labels...
			{ "form.icon.url", "/danfulea/resources/personal.png" },///jdf/resources/duke.png"},
			{ "icon.url", "/danfulea/resources/personal.png" },///jdf/resources/globe.gif"},

			{ "img.chart.bar", "/danfulea/resources/chart_bar.png" },
			{ "img.chart.curve", "/danfulea/resources/chart_curve.png" },
			
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
			
			{ "Application.NAME", "XRTF_mAs: XRay Tube Filtration and mAs evaluation" },
			{ "SaveViewDBFrame.NAME", "Save/View database" },

			{ "Author", "Author:" },
			{ "Author.name", "Dan Fulea , fulea.dan@gmail.com" },

			{ "Application.name", "XRTF_mAs" },
			{ "Version", "Version:" },
			{ "Version.name", "XRTF_mAs 1.0" },

			{
					"License",
					"This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License (version 2) as published by the Free Software Foundation. \n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. \n" },
			
			{ "pleaseWait.label", "Work in progress!" },

			{ "plswait.title", "Please wait!" },
			{ "plswait.label", "Work in progres..." },
	
	
			{ "mainPanel.textArea.label", "Results:" },

			{ "menu.file", "File" },
			{ "menu.file.mnemonic", new Character('F') },

			{ "menu.help.LF", "Look and feel..." },
			{ "menu.help.LF.mnemonic", new Character('L') },
			{ "menu.help.LF.toolTip", "Change application look and feel" },
			
			{ "addB", "Add" },
			{ "addB.toolTip", "Add entry" },
			{ "addB.mnemonic", new Character('A') },
			
			{ "deleteB", "Delete" },
			{ "deleteB.toolTip", "Delete an entry" },
			{ "deleteB.mnemonic", new Character('D') },
			
			{ "resetB", "Reset" },
			{ "resetB.toolTip", "Reset all entries" },
			{ "resetB.mnemonic", new Character('R') },
			
			{ "runB", "Run" },
			{ "runB.toolTip", "Application start" },
			{ "runB.mnemonic", new Character('u') },

			{ "killB", "Kill" },
			{ "killB.toolTip", "Application stop" },
			{ "killB.mnemonic", new Character('K') },

			{ "printB", "Pdf print..." },
			{ "printB.toolTip", "Save report as pdf file" },
			{ "printB.mnemonic", new Character('P') },
			
			{ "filtrationB", "Compute tube total filtration..." },
			{ "filtrationB.toolTip", "Compute tube total filtration based on attenuators" },
			{ "filtrationB.mnemonic", new Character('f') },
			
			{ "setLimitsB", "Set these limits" },
			{ "setLimitsB.toolTip", "Set these limits in database" },
			{ "setLimitsB.mnemonic", new Character('S') },
			{ "setLimits.border", "Reccommended limits" },
			
			{ "saveB", "View/Save in database" },
			{ "saveB.toolTip", "View database and/or save in database" },
			{ "saveB.mnemonic", new Character('w') },
			
			{ "save.saveB", "Save" },
			{ "save.saveB.toolTip", "Save record in database" },
			{ "save.saveB.mnemonic", new Character('S') },
			{ "save.deleteB", "Delete" },
			{ "save.deleteB.toolTip", "Delete record" },
			{ "save.deleteB.mnemonic", new Character('D') },
			{ "save.viewB", "View all records" },
			{ "save.viewB.toolTip", "View all records" },
			{ "save.viewB.mnemonic", new Character('V') },
			
			{"expData.border","Experimental data (for HVL and tube Total Filtration calculation): "},
	        {"expData.mmAlLabel","mmAl: "},
	        {"expData.fmmAlLabel","f(mmAl): "},
	        
	        {"tubeData.border","XRay tube settings : "},
	        {"tubeData.anodeLabel","Anode: "},
	        {"tubeData.rippleLabel","Ripple: "},
	        {"tubeData.kvLabel","kV: "},
	        {"tubeData.anodeAngleLabel","Anode angle: "},
	        {"tubeData.filtrationLabel","Total Filtration [mmAl] -if not known, leave the field blank: "},
	        {"tubeData.hvlLabel","HVL [mmAl] -if not known, leave the field blank: "},
	        
	        {"expData2.border","Experimental data (for mAs calculation): "},
	        {"expData2.exposureLabel","Exposure or Dose: "},
	        {"expData2.distanceLabel","measured at distance [cm]: "},
	        
	        {"expData.unc","Estimated measurement uncertainty [%]: "},
	        
	        {"dialog.ripple", "If anode=Mo or Rh, angle>=9,angle <=23;kv>=25,kv<=32;ripple=0; \n if anod=W, angle >=6 angle<=22;kv>=30;kv<=150!! \n for ripple=>kv =55,60,65,...,90!!"},
	        {"dialog.invalidNrP.message", "Number of pair data points must be >= 3!"},
			/*{
					"numberOfHystoriesCb",
					new String[] { "1000", "10000", "20000", "40000", "80000",
							"150000", "300000", "1000000" } },*/

			{ "menu.file.exit", "Close" },
			{ "menu.file.exit.mnemonic", new Character('C') },
			{ "menu.file.exit.toolTip", "Close the application" },
			
			{ "menu.file.filtration", "Compute tube total filtration..."},//"Tube attenuators..." },
			{ "menu.file.filtration.mnemonic", new Character('T') },
			{ "menu.file.filtration.toolTip", "Evaluate tube total filtration if absorbers are known" },

			{ "menu.help", "Help" },
			{ "menu.help.mnemonic", new Character('H') },

			{ "menu.help.about", "About..." },
			{ "menu.help.about.mnemonic", new Character('A') },
			{ "menu.help.about.toolTip",
					"Several informations about this application" },
			

			{ "text.simulation.stop", "Interrupred by user!" },

			{ "status.wait", "Waiting for your action!" },
			{ "status.computing", "Computing..." },
			{ "status.done", "All done! " },
			{ "status.error", "Unexpected error!" },
			{ "status.save", "Saved: " },
			{ "number.error", "Insert valid positive numbers! " },
			{ "number.duplicate.error", "Error: No duplicates are allowed! " },
			{ "number.y.error", "Initial value of fmmAl, i.e. y0, should be MAX! " },

			{ "text.alphaEff", "Alpha efficiency [/100 particles] = " },
			{ "text.alphaEff.err", " ; 2 sigma uncertainty [%] = " },
			{ "text.solidAngle", "Geometry solid angle [sr] = " },
			{ "text.solidAngle.err", " ; 2 sigma uncertainty [%] = " },

			{ "dialog.exit.title", "Confirm..." },
			{ "dialog.exit.message", "Are you sure?" },
			{ "dialog.exit.buttons", new Object[] { "Yes", "No" } },

			//{ "pdf.metadata.title", "XRTF_mAS PDF" },
			//{ "pdf.metadata.subject", "Results" },
			//{ "pdf.metadata.keywords", "XRTF_mAS, PDF" },
			//{ "pdf.metadata.author", "XRTF_mAS" },
			//{ "pdf.content.title", "XRTF_mAS Simulation Report" },
			//{ "pdf.content.subtitle", "Report generated by: " },
			//{ "pdf.page", "Page " },
			//{ "pdf.header", "XRTF_mAS output" },
			//{ "file.extension", "pdf" },
			//{ "file.description", "PDF file" },
			
			{ "pdf.metadata.title", "RadQC PDF" },
			{ "pdf.metadata.subject", "RadQC analysis results" },
			{ "pdf.metadata.keywords", "RadQC, PDF" },
			{ "pdf.metadata.author", "RadQC" },
			{ "pdf.content.title", "RadQC - HVL and Total Filtration Report" },
			{ "pdf.content.subtitle", "Report generated by: " },
			{ "pdf.page", "Page " },
			{ "pdf.header", "RadQC output" },
			{ "file.extension", "pdf" },
			{ "file.description", "PDF file" },
			
			{ "dialog.overwrite.title", "Overwriting..." },
			{ "dialog.overwrite.message", "Are you sure?" },
			{ "dialog.overwrite.buttons", new Object[] { "Yes", "No" } },

			{ "alpha.settings",
					"Simulation settings for alpha detector-source geometry:" },
			{ "alpha.results", "Simulation results:" },
			
			{ "hvl.permitted.min", "Minimum permissible HVL in mmAl (At 80 kVp: 2.3 mmAl for pre-2012 devices or 2.9 mmAl): " },
			{ "hvl.permitted.min.mammo", "Minimum permissible HVL in mmAl (At 28 kVp: 0.28 mmAl for Mo/MO devices or kV/100 mmAl): " },
			{ "filtration.permitted.min", "Minimum permissible total filtration in mmAl (2.5 mmAl or 3.5 mmAl for new pediatric devices): " },
			{ "filtration.permitted.min.mammo", "Minimum permissible total filtration in mmAl (NOT AVAILABLE): " },
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			{ "records.border", "Records" },
			{ "records.label", "Records:" },
			
			{ "nosave.error.message", "Please perform HVL test!" },
			{ "nosave.error.title", "Nothing to save" },

	};
}
