package other.resources;

import java.util.ListResourceBundle;

/**
 * Class for additional QC test resources
 * 
 * @author Dan Fulea, 03 May 2015
 */
public class OtherFrameResources extends ListResourceBundle{
	
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
					
			{ "OtherFrame.NAME", "RadQC - other tests" },
			
			{ "info.label", "Additional QC test info: " },
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			{ "records.border", "Records" },
			{ "records.label", "Records:" },
						
			{"save.saveB", "Save" },
			{ "save.saveB.toolTip", "Save record in database" },
			{ "save.saveB.mnemonic", new Character('S') },
			{ "save.deleteB", "Delete" },
			{ "save.deleteB.toolTip", "Delete record" },
			{ "save.deleteB.mnemonic", new Character('D') },
			{ "save.viewB", "View all records" },
			{ "save.viewB.toolTip", "View all records" },
			{ "save.viewB.mnemonic", new Character('V') },
			
			{ "save.viewBNotes", "View selected info" },
			{ "save.viewBNotes.toolTip", "View selected info" },
			{ "save.viewBNotes.mnemonic", new Character('w') },
						
			{ "textArea.rad.title", "Examples of additional tests for radiography: " },
			{ "textArea.rad", 
				"X-ray/light beam alignment [% from SID]: "+"   "+"; Limit: 2% SID in all directions. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"SID deviation [%]: "+"   "+"; Limit: 10% from set value. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Focal spot [mm]: "+"   "+"; Limit NEMA standard specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Film sensitometry: gamma contrast"+"   "+"; Limit: 2.8 - 3.2. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Film sensitometry: base + fog OD"+"   "+"; Limit: 0.2. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"High contrast resolution [lp/mm]: "+"   "+"; Limit: 1.6 lp/mm or tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Low contrast resolution: "+"   "+"; Limit: tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"
			},
			{"textArea.mammo.title", "Examples of additional tests for mammography: " },
			{ "textArea.mammo",
				"X-ray/light beam alignment [mm]: "+"   "+"; Limit: 5 mm in all directions. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Compression force [N]: "+"   "+"; Limit: 300 N. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"High contrast resolution [lp/mm]: "+"   "+"; Limit: 12 lp/mm or tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Low contrast resolution: "+"   "+"; Limit: threshold contrast 1.5% or tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n" 
			},
			{"textArea.fluoro.title", "Examples of additional tests for fluoroscopy: " },
			{ "textArea.fluoro", 
				"Collimation [% from SID]: "+"   "+"; Limit: 3% SID in all directions. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"High contrast resolution [lp/mm]: "+"   "+"; Limit: 0.8 lp/mm or tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"Low contrast resolution: "+"   "+"; Limit: threshold contrast 4% or tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n" 
			},
			{"textArea.ct.title", "Examples of additional tests for CT: " },
			{ "textArea.ct", 
				"CT number accuracy [HU]: "+"   "+"; Deviation limit: 10 HU for water. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"CT number uniformity [HU]: "+"   "+"; Deviation limit: 10 HU for water. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"CT alignment lights: "+"   "+"; Limit: 5 mm. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"+
				"High contrast resolution [lp/mm]: "+"   "+"; Limit: 0.5 lp/mm or tester specific. \n"+
				"Result: "+"Test PASSED/NOT PASSED"+"\n"
			},
	};

}
