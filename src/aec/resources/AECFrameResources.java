package aec.resources;

import java.util.ListResourceBundle;

/**
* AEC resources class
* 
* @author Dan Fulea, 03 May 2015
*/
public class AECFrameResources extends ListResourceBundle{
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
					
			{ "AECFrame.NAME", "AEC repeatability and AEC compensation" },
			{"output.tab.title", "Output"},
			{"reprod.tab.title", "Repeatability (e.g. OD, DDI at fixed settings)"},
	        {"linearity.tab.title", "AEC compensation (e.g. with phantom thickness)"},
			//---------------			
			{"output.exposureLabel","Exposure or Dose: "},
			{"output.distanceLabel"," , measured at distance [cm]: "},
			{"repeatability.maxPermissibleLabel","Maximum permissible variation: "},
			{"repeatability.measuredLabel","Measured:"},
			
			{"expData.unc","Estimated measurement uncertainty [%]: "},
			
			{"calcB", "Compute"},
	        {"calcB.mnemonic", new Character('C')},
	        {"calcB.toolTip", "Perform calculation"},
	        
			{ "saveB", "View/Save in database" },
			{ "saveB.toolTip", "View database and/or save in database" },
			{ "saveB.mnemonic", new Character('w') },
			
			{"repeatability.addB", "Add"},
	        {"repeatability.addB.mnemonic", new Character('A')},        
	        {"repeatability.delB", "Delete"},
	        {"repeatability.delB.mnemonic", new Character('D')},
	        {"repeatability.resetB", "Delete all"},
	        {"repeatability.resetB.mnemonic", new Character('e')},
	        {"repeatability.calcB", "Compute"},
	        {"repeatability.calcB.mnemonic", new Character('C')},
	        {"repeatability.calcB.toolTip", "Perform calculation"},
	        
	        {"rand.setmaL","Value measured: "},
	        {"rand.setmsL","Exposure time [ms]: "},
	        {"rand.setmasL","Tube load (charge) [mAs]: "},
	        {"rand.setmgyL", "Dose indicator: "},
			//========================================================================
			{ "SaveViewDBFrame.NAME", "Save/View database" },
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			{ "records.label", "Records:" },
			{ "records.border", "Records" },
			
			{ "output.border", "Output" },
			{ "rep.border", "AEC repeatability" },
			{ "lin.border", "AEC compensation" },
			{ "details.border", "Details" },
			
			{ "save.saveB", "Save" },
			{ "save.saveB.toolTip", "Save record in database" },
			{ "save.saveB.mnemonic", new Character('S') },
			{ "save.deleteB", "Delete" },
			{ "save.deleteB.toolTip", "Delete record" },
			{ "save.deleteB.mnemonic", new Character('D') },
			{ "save.viewB", "View all records" },
			{ "save.viewB.toolTip", "View all records" },
			{ "save.viewB.mnemonic", new Character('V') },
			
	        { "nosave.output.error.message", "Please perform output test!" },
	        { "nosave.rep.error.message", "Please perform AEC repeatability test!" },
	        { "nosave.lin.error.message", "Please perform AEC compensation test!" },	        
			{ "nosave.error.title", "Nothing to save" },

			
	        //kvP
	        {"tab.columns", new String[] {"Set","Measured"}},/////////////////
	        
	        ///////////////////////////
	        {"output.permissible.label", "Maximum permissible output [uGy/mAs] at 100 cm, 80 kVp and minimum filtration of 2.5 mmAl:"},
	        {"output.permissible.label.mammo", "Maximum permissible output [uGy/mAs] at 50 cm, 28 kVp and Mo/Mo filter/target:"},
	        
	        {"output.mA.label", "Tube current [mA]: "},
	        {"output.ms.label", "Exposure time [ms]: "},
	        {"output.OR.label", " OR "},
	        {"output.mAs.label", "Tube load (charge) [mAs]: "},
	        //////////////////////////////////////////////
	        	        
	        //rezultat///////////////////////////
	        {"reprod.rezultat", "Results: "},
	        {"reprod.rezultat2", "Results"},
	        {"reprod.rezultat.succes", " Test PASSED!"},
	        {"reprod.rezultat.fail", " Test NOT PASSED!"},
	        
	        {"reprod.rezultat.cv", "Coefficient of variation (C.V.) [%] = "},
	        {"reprod.rezultat.cvmp", "Maximum permissible C.V. [%] = "},

	        {"reprod.rezultat.cv2", "Deviation = "},
	        {"reprod.rezultat.cvmp2", "Maximum permissible deviation = "},

	        {"rand.rezultat.mas", "mAs= "},
	        {"rand.rezultat.mgy", "Dose indicator= "},
	        {"rand.rezultat.rand", "Output(Dose indicator/mAs)= "},
	        
	        {"output.rezultat.output", "Output [uGy/mAs] at reference settings = "},
	        {"output.rezultat.reference", "Maximum permissible reference output [uGy/mAs] = "},
	        //////////////////////////////
	        	        
	        //dialogs
	        {"dialog.insertInListError.title", "Error"},
	        {"dialog.insertInList.message", "Insert real numbers!"},
	        {"dialog.insertInList.message2", "Too few data entries!"},
	        {"dialog.insertInTableError.title", "Error...."},
	        {"dialog.insertInTable.message", "Insert positive numbers!"},
	        
			
	};
}
