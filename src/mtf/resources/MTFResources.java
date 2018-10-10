package mtf.resources;

import java.util.ListResourceBundle;

/**
 * The resource bundle for MTF project, main class. <br>
 * 
 * @author Dan Fulea, 18 Jun. 2013
 * 
 */

public class MTFResources extends ListResourceBundle{
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
			
			{ "Application.NAME", "MTF based on pixel profile data (aquired using e.g. imageJ)" },

			{ "Author", "Author:" },
			{ "Author.name", "Dan Fulea , fulea.dan@gmail.com" },

			{ "Application.name", "MTF" },
			{ "Version", "Version:" },
			{ "Version.name", "MTF 1.0" },

			{
					"License",
					//"This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License (version 2) as published by the Free Software Foundation. \n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. \n" },
			"Copyright (c) 2014, Dan Fulea \nAll rights reserved.\n\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n\n1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n\n2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\nTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" },
			
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
			{ "runB.toolTip", "Compute MTF" },
			{ "runB.mnemonic", new Character('u') },

			{ "openB", "Load pixel profile" },
			{ "openB.toolTip", "Load pixel profile data aquired by (for instance) imageJ" },
			{ "openB.mnemonic", new Character('L') },

			{ "printB", "Pdf print..." },
			{ "printB.toolTip", "Save report as pdf file" },
			{ "printB.mnemonic", new Character('P') },
			
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
			
			{ "menu.file.exit", "Close" },
			{ "menu.file.exit.mnemonic", new Character('C') },
			{ "menu.file.exit.toolTip", "Close the application" },
			
			{ "menu.file.filtration", "Tube attenuators..." },
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

			//{ "pdf.metadata.title", "MTF PDF" },
			//{ "pdf.metadata.subject", "Results" },
			//{ "pdf.metadata.keywords", "MTF, PDF" },
			//{ "pdf.metadata.author", "MTF" },
			//{ "pdf.content.title", "MTF Simulation Report" },
			//{ "pdf.content.subtitle", "Report generated by: " },
			//{ "pdf.page", "Page " },
			//{ "pdf.header", "MTF output" },
			//{ "file.extension", "pdf" },
			//{ "file.description", "PDF file" },
			
			{ "pdf.metadata.title", "RadQC PDF" },
			{ "pdf.metadata.subject", "RadQC analysis results" },
			{ "pdf.metadata.keywords", "RadQC, PDF" },
			{ "pdf.metadata.author", "RadQC" },
			{ "pdf.content.title", "RadQC - MTF Report" },
			{ "pdf.content.subtitle", "Report generated by: " },
			{ "pdf.page", "Page " },
			{ "pdf.header", "RadQC output" },
			{ "file.extension", "pdf" },
			{ "file.description", "PDF file" },
			
			{ "dialog.overwrite.title", "Overwriting..." },			
			{ "dialog.overwrite.message", "Are you sure?" },
			{ "dialog.overwrite.buttons", new Object[] { "Yes", "No" } },

	};
}
