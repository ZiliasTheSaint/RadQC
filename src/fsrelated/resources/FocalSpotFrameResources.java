package fsrelated.resources;

import java.util.ListResourceBundle;

/**
 * The resource bundle for FocalSpot. <br>
 * 
 * @author Dan Fulea, 28 Apr. 2015
 * 
 */

public class FocalSpotFrameResources extends ListResourceBundle{
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
			
			{ "FocalSpotFrame.NAME", "Focal spot estimation" },

			{ "Author", "Author:" },
			{ "Author.name", "Dan Fulea , fulea.dan@gmail.com" },

			{ "Application.name", "NoName, module" },
			{ "Version", "Version:" },
			{ "Version.name", "----" },

			{
					"License",
					//"This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License (version 2) as published by the Free Software Foundation. \n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. \n" },
			"Copyright (c) 2014, Dan Fulea \nAll rights reserved.\n\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n\n1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n\n2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\nTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" },
			
			{"expData.unc","Estimated measurement uncertainty [%]: "},
			
			{ "pleaseWait.label", "Work in progress!" },
			{ "plswait.title", "Please wait!" },
			{ "plswait.label", "Work in progres..." },
		
			{ "mainPanel.textArea.label", "Results:" },

			{ "menu.file", "File" },
			{ "menu.file.mnemonic", new Character('F') },

			{ "menu.file.exit", "Close" },
			{ "menu.file.exit.mnemonic", new Character('C') },
			{ "menu.file.exit.toolTip", "Close the application" },
			
			{ "menu.help.LF", "Look and feel..." },
			{ "menu.help.LF.mnemonic", new Character('L') },
			{ "menu.help.LF.toolTip", "Change application look and feel" },
			
			{ "computeB", "Compute" },
			{ "computeB.toolTip", "Perform computation" },
			{ "computeB.mnemonic", new Character('C') },

			{ "print.report", "Pdf print..." },
			{ "print.report.toolTip", "Save report as pdf file" },
			{ "print.report.mnemonic", new Character('P') },
			
	        { "number.error.title", "Error" },
			{ "number.error.message", "Insert valid positive numbers! " },
			{"dialog.fscalc.message", "Focal spot calculation error. Nul division!"},
			//{ "number.duplicate.error", "Error: No duplicates are allowed! " },
			
			{ "dialog.exit.title", "Confirm..." },
			{ "dialog.exit.message", "Are you sure?" },
			{ "dialog.exit.buttons", new Object[] { "Yes", "No" } },

			{"fs.tab.tab1.title", "RMI 112B tester"},
			{"fs.htesterL","Tester height[cm]: "},
	        {"fs.hspatiatorL","Additional separator height [cm]: "},
	        {"fs.ffdL","SID - Source to image distance [cm]: "},
	        {"fs.ffdmpL","Maximum permissible deviation for SID [%]: "},
	        {"fs.doriftesterL","Distance between holes on tester [cm]: "},
	        {"fs.doriffilmL","Distance between holes on film [cm]: "},
	        {"fs.grup.lpmmL","Line group index-number still visible: "},
	        {"fs.lpmmL","lp/mm: "},
	        {"fs.lpmmL.checkbox","use only this group"},
	        {"fs.nominal", "Given nominal focal spot [mm]: "},
	        {"fs.fsgi.index", new String[] {"1","2","3","4",
	        								"5","6","7","8",
	        								"9","10","11","12"}},
	        {"fs.fsgi.lpmm", new String[] {"0.84","1.0","1.19","1.41",
	        								"1.68","2.0","2.38","2.83",
	        								"3.36","4.0","4.76","5.66"}},
			{"fs.NEMAstd.nom", new String[] {"0.3","0.4","0.8","1.2"}},
			{"fs.NEMAstd.effmax", new String[] {"0.65","0.85","1.6","2.4"}},
			{"fs.NEMAstd.effmin", new String[] {"0.45","0.6","1.2","1.7"}},
			{"fs.tester.rmi112b", "rmi112b"},		
			
			{"rezultat", "Result: "},
			{"rezultat.succes", " Test PASSED!"},
	        {"rezultat.fail", " Test NOT PASSED!"},
	        {"fs.rezultat.mag", "Magnification = "},
	        {"fs.rezultat.ffdm", "Measured SID [cm] = "},
	        {"fs.rezultat.ffdc", "Computed SID [cm] = "},
	        {"fs.rezultat.ffddiff", "Difference (%)= "},
	        {"rezultat.ffd", "SID test result: "},
	        {"fs.rezultat.ffddiffmp", "Maximum permissible difference [%]= "},
	        {"fs.rezultat.fs.min", "NEMA standard: minimum effective focal spot corresponding to the given nominal focal spot [mm] = "},
	        {"fs.rezultat.fs.max", "NEMA standard: maximum effective focal spot corresponding to the given nominal focal spot [mm] =  "},
	        {"fs.rezultat.fs.calc", "Computed effective focal spot [mm] = "},
	        {"fs.rezultat.fs.nom", "Given nominal focal spot [mm] = "},
	        {"rezultat.fs", "Focal spot test result: "},
	        
	        { "pdf.metadata.title", "RadQC PDF" },
			{ "pdf.metadata.subject", "RadQC analysis results" },
			{ "pdf.metadata.keywords", "RadQC, PDF" },
			{ "pdf.metadata.author", "RadQC" },
			{ "pdf.content.title", "RadQC - Focal Spot Report" },
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
