package dosimetry.resources;

import java.util.ListResourceBundle;

/**
 * Class for dosimetry resources
 * 
 * @author Dan Fulea, 03 May 2015
 */
public class DosimetryFrameResources extends ListResourceBundle{
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
					
			{ "DosimetryFrame.NAME", "Dosimetry - Generic Radiography" },
			{ "KAPEvalFrame.NAME", "KAP - quick evaluation" },
			{ "SaveViewDBFrame.NAME", "Save/View database" },
			{ "DosimetryFrameFluoro.NAME", "Dosimetry - Fluoroscopy" },
			{ "DosimetryFrameMammo.NAME", "Dosimetry - Mammography" },
			{ "DosimetryFrameCt.NAME", "Dosimetry - CT" },
			{ "CTDIEvalFrame.NAME", "CTDIvol - quick evaluation" },
			{ "CTDIAirEvalFrame.NAME", "CTDIair - evaluation from mAs per rotation" },
			
			{"phantomSex.cb", new String[] {"Male","Female"}},
			{"phantomSex.male", "Male"},
			{"phantomSex.male", "Female"},
			{"phantom.scaleXY","Phantom weight/height scale factors: scaleXY= "},
			{"phantom.scaleZ","; scaleZ= "},
			//----------------------
			{"phantomSex.label", "Phantom sex: "},
			{"ageGroup.newborn.rb", "Newborn"},
			{"ageGroup.1y.rb", "1y"},
			{"ageGroup.5y.rb", "5y"},
			{"ageGroup.10y.rb", "10y"},
			{"ageGroup.15y.rb", "15y"},
			{"ageGroup.adult.rb", "Adult"},
			{"ageGroup.label", "Age group: "},
			{"phantom.border", "Phantom selection"},
			{"phantomAge.label", "Phantom age [yrs]: "},
			{"phantomHeight.label", "Phantom height [cm]: "},
			{"phantomWeight.label", "Phantom weight [kg]: "},
			
			{"phantom.breast.diameter.label", "Breast diameter [cm]: "},
			{"phantom.breast.thickness.label", "Breast thickness [cm]: "},
			
			{"kv.label", "Kilovoltage: "},			
			{"filtration.label", "Total tube filtration taken from database (if no record then a generic value of 9.0 is displayed) [mmAl]: "},
			{"filtration.label.mammo", "Total tube filtration taken from database (if no record then a generic value of 0.5 is displayed) [mmAl]: "},
			{ "filtrationB", "Compute tube total filtration..." },
			{ "filtrationB.toolTip", "Compute tube total filtration based on attenuators" },
			{ "filtrationB.mnemonic", new Character('f') },
			{"anodeAngle.label", "Anode angle [deg]: "},
			{"ripple.label", "Waveform ripple: "},
			{"mAs.label", "mAs (not mandatory if KAP is available): "},
			{"tube.border", "Tube settings"},
			{"anodeMaterial.label", "Anode material: "},
			{"anodeMaterial.cb", new String[] {"W","MO","RH"}},//for mammo
			
			{"fanBeam.cb", new String[] {"on","off"}},
			{"fanBeam.label", "Fan beam geometry (pencil beam otherwise): "},
			{"distance.label.ct", "Focus to central axis (phantom mid-plane) distance [cm]: "},
			{"sliceThickness.label", "Collimation (beam width) - slice thickness T (NT if multislice scanner) or field height if dental panoramic scan mode [mm]: "},
			{"rotationAngleIncrement.label", "Rotation angle increment (for helical scan, let the default value) [deg]: "},
			{"pitchFactor.label", "Pitch factor p (p = couch movement in 1 rotation/T): "},
			{"ctdi.label", "Free in air kerma on central axis per rotation (mathematical definition of CTDI) [uGy]: "},
			{"ctdiVol.label", "OPTIONAL - CTDIvol, for comparison with suspension level [uGy]: "},
			{"CTDIB", "Evaluate CTDIvol..."},
	        {"CTDIB.mnemonic", new Character('E')},
	        {"CTDIB.toolTip", "Perform CTDIvol calculation based on dosimeter reading"},
	        //{"BSF.label", "Standard backscatter factor BSF (general 1.1 - 1.6, 1.1 for mammo): "},
	        {"CTDIB.air", "Evaluate CTDIair..."},
	        {"CTDIB.air.mnemonic", new Character('v')},
	        {"CTDIB.air.toolTip", "Perform CTDIair calculation based on mAs per rotation"},
			
			{"exam.border", "KAP and examination settings"},
			{"exam.ct.border", "CTDI and examination settings"},
			{"kap.label", "Air kerma-area product (KAP - not mandatory if mAs is available) [uGy x mm2]: "},
			{"usemas.cb", new String[] {"yes","no"}},
			{"usemas.label", "Use mAs for KAP calculation?: "},
			{"distance.label", "Focus-phantom midplane distance (based on projection, phatom type and updated via KAP evaluation) [cm]: "},
			{"distance.label.mammo", "Focus-breast entrance distance (updated via KAP evaluation))[cm]: "},
			{"examination.label", "Radiological examination: "},
			{"examination.cb", new String[] {
					"Abdomen",
					"CervicalSpine",
					"Chest",					
					"DentalPanoramic",
					"Femur",
					"FullSpine",
					"Head",
					"HipJoint",
					"Knee",
					"LumbarSpine",
					"LumboSacralJunction",
					"Pelvis",
					"Shoulder",
					"ThoracicSpine",
					"WholeBody"
			}},
			
			{"abdomen.limit.ct", new String[] {
					"0.0","0.0","25.0","0.0","0.0","30"
			}},
			{"skull.limit.ct", new String[] {
					"0.0","0.0","0.0","0.0","0.0","80"
			}},
			//0-yr,1-yr,5-yr,10-yr,15-yr,adult;0.0 means NA, in mGy
			{"abdomen.limit", new String[] {
					"0.0","0.4","0.5","0.8","1.2","10"
			}},
			{"chest.ap.limit", new String[] {
					"0.05","0.05","0.07","0.120","0.0","0.3"
			}},
			{"chest.lat.limit", new String[] {
					"0.0","0.0","0.2","0.0","0.0","1.5"
			}},
			{"skull.ap.limit", new String[] {
					"0.0","0.8","1.1","1.1","1.1","5.0"
			}},
			{"skull.lat.limit", new String[] {
					"0.0","0.5","0.8","0.8","0.8","3.0"
			}},
			{"pelvis.limit", new String[] {
					"0.0","0.5","0.6","0.7","2.0","10"
			}},
			
			{"lumbarspine.ap.limit", new String[] {
					"0.0","0.0","0.0","0.0","0.0","10"
			}},
			
			{"lumbarspine.lat.limit", new String[] {
					"0.0","0.0","0.0","0.0","0.0","30"
			}},
			
			{"lumbosacral.limit", new String[] {
					"0.0","0.0","0.0","0.0","0.0","40"
			}},
			
			{"breastThickness.array.AGD.DRL",  new double[] {2.0,3.0,4.0,5.0,6.0,7.0}},
			{"breastThickness.array.DRL",  new double[] {1.0,1.5,2.0,3.0,4.5,6.5}},
			
			{"breastThickness.array.AGD",  new double[] {3.0,4.0,5.0,6.0,7.0,8.0}},
			{"kv.array.AGD",  new double[] {26.0,28.0,32.0}},
			{"HVL.array.AGD",  new double[] {0.31,0.35,0.41}},
			{"g.array.AGD",  new double[][] 
		         {
		
					{215,165,132,109,92,80},
					{238,183,147,121,103,89},
					{277,216,175,145,124,107},
					
					{218,168,134,111,94,82},
					{241,186,149,123,105,91},
					{278,218,177,147,125,109},
					
					{222,172,139,115,98,85},
					{244,190,153,127,108,94},
					{280,220,179,149,127,110}
		         }
			},
			
			{"projection.label", "Projection: "},
			{"projection.cb", new String[] {"AP","LLAT","PA","RLAT"}},
			{"runs.label", "Number of runs (MonteCarlo): "},
			{"calcB", "Compute"},
	        {"calcB.mnemonic", new Character('C')},
	        {"calcB.toolTip", "Perform calculation"},
	        
	        {"autorefresh.ch","View phantom"},
	        {"helicalscan.ch","Helical Scan"},
	        {"halffield.ch","Assymmetric (half-field) scan"},
	        {"dentalpanoramic.ch","Dental panoramic scan mode (a single 180 degrees rotation)"},
	        {"autoT.ch","If dental panoramic scan mode, auto-adjust T to be the field height related to the radiological examination"},
	        //
	        {"KAP.INFO", "The field dimensions should not be visible because those values are used internally to match phantom not actual patient. \n Still, one can quickly check X-ray DAP-meter accuracy by using same distance for detector and phantom and the experimental field dimensions (uncheck auto-update)!"},
	        {"CT.INFO", "These evaluations are for CTDI measured/computed on CT phantom (head or body). The common protocol is to multiply the reading with L/T (L is chamber length of 100 mm, T is slice thickness). By doing so, you artificially increase the reading as if the whole ionization chamber is uniformely exposed!?! "
	        		+ " Also, because the reading is directly proportional with T (more dose when more irradiated length), the CTDI evaluated this way is INDEPENDENT of T. The same T independence of CTDI is found when applying TRS457 from IAEA. In fact all these "
	        		+ "institutions are completely wrong when comes to evaluating CTDI unless it is not related to ONE ROTATION. From its mathematical expression as being the integral of dose over ionisation chamber length divided by actual irradiated length T, "
	        		+ "we noticed that the integral is approximately equal with the reading times T (plus some realatively very small numbers). That's because the almost all dose profile is concentrated in T region (in ideal chamber, all dose profile is inside T). Therefore by dividing with T, we find that "
	        		+ "actually the CTDI is THE READING of ionisation chamber (properly calibrated obviously). Hence, when assessing the CTDIair, the mathematical definition of CTDI match our needs of air kierma (the reading) per rotation. The IAEA and other methods of assessing CTDI violates its mathematical definition. "
	        		+ "If you want to follow the mathematical definition of CTDI, let calibration field be the same as slice thickness field, they will cancel each other so the reading is all that matters.\n\n"
	        		+ "The quantity required for Monte Carlo simulation is kerma free in air per rotation measured at center! This CTDIair can be measured with properly calibrated ionisation chamber or TLD (our definition of CTDI which match the CTDI mathematical definition)!. It can also be evaluated from mAs per rotation. The CTDIvol is NOT the CTDIair!"},
	        
	        {"KAPB", "Evaluate KAP..."},
	        {"KAPB.mnemonic", new Character('E')},
	        {"KAPB.toolTip", "Perform KAP calculation based on dosimeter reading"},
	        {"BSF.label", "Standard backscatter factor BSF (general 1.1 - 1.6, 1.1 for mammo): "},
	        {"BSF.tooltip", "Typical range 1.1 - 1.6. Standard values: 1.1 for mammography and 1.3 for the rest"},
	        
	        {"expData2.exposureLabel","Exposure or free in air kerma: "},
	        {"detector.thickness.label","Distance from support/table to the midplane of sensitive detector volume) [cm]: "},
	        {"focus.table.label","Distance from focus to support/table (detector geometry) [cm]: "},
	        {"focus.patient.label","Distance from focus to support/table (phantom geometry) [cm]: "},
	        {"geometry.label","Detector placed on top of support/table. Focus->detector->support/table"},
	        {"field.label","Field size at support/table (width x height to match the phantom examination): "},
	        {"autoupdate.ch","Auto-update field dimensions when computation starts"},
	        
	        { "status.wait", "Waiting for your action!" },
			{ "status.computing", "Computing..." },
			{ "status.done", "All done! " },
			{ "status.error", "Unexpected error!" },
			
	        {"geometry.label.ct","Detector (pencil type ionization chamber) placed on CT central axis for CTDIc and at peripheral side (CTDIp) of CT PHANTOM"},
	        
	        {"geometry.label.ct.mAs","mAs per rotation = mA x rotation_time[s]. It can also be computed as: effective_mAs x pitch (effective_mAs = mA x rotation_time[s]/pitch)"},
	        {"ctdiEval.air.mas.label","mAs per rotation: "},
	        {"ctdiEval.air.computeB", "Compute"},
	        {"ctdiEval.air.computeB.mnemonic", new Character('C')},
	        {"ctdiEval.air.text", "Computation for CT XRay tube having the following specifications: "},
	        {"ctdiEval.air.text.fca", "Focus to isocenter (CT central axis) distance [cm]: "},
	        {"ctdiEval.air.text.kv", "Tube kilovoltage: "},
	        {"ctdiEval.air.text.filtration", "Total tube filtration in mmAl equivalent: "},
	        {"ctdiEval.air.text.uanod", "Anode angle [deg]: "},
	        {"ctdiEval.air.text.ripple", "Waveform ripple: "},
	        
	        {"ctdiEval.length.label","Ionization chamber length [mm]: "},
	        {"ctdiEval.slices.label","Number of slices: "},
	        {"ctdiEval.exposure.label", "Reading FOR A SINGLE ROTATION of CT scanner: "},
	        {"ctdiEval.calFactor.label","Calibration factor -e.g. for 100 mm ionisation chamber- [mGy x mm/Reading_on_mGyScale]: "},
	        {"ctdiEval.sliceThickness.label","Collimation (beam width) - slice thickness T (NT if multislice scanner) or field height if dental panoramic scan mode [mm]: "},
	        {"ctdiEval.pitch.label","Pitch factor: "},
	        {"ctdiEval.setCTDIcB", "Set as CTDIc"},
	        {"ctdiEval.setCTDIcB.mnemonic", new Character('S')},
	        {"ctdiEval.addB", "Add as CTDIp"},
	        {"ctdiEval.addB.mnemonic", new Character('A')},
	        {"ctdiEval.deleteB", "Delete"},
	        {"ctdiEval.deleteB.mnemonic", new Character('D')},
	        {"ctdiEval.resetB", "Delete all"},
	        {"ctdiEval.resetB.mnemonic", new Character('e')},
	        //
	        {"expData.unc","Estimated measurement uncertainty [%]: "},
	        { "kap.KAP.mgycm2", "KAP [mGycm2]: " },
	        { "kap.KAP.ugymm2", "KAP [uGymm2]: " },
	        {"FCA.cm", "Focus-phantom midplane distance [cm]: "},
	        {"FSD.cm", "Focus-phantom entrance distance [cm]: "},
	        
	        {"Kair.table.mgy", "Kerma at support/table level free in air in detector geometry [mGy]: "},
	        {"Kair.table.phantom.mgy", "Kerma at support/table level free in air in phantom geometry [mGy]: "},
	        {"Kair.mgy", "Kerma at patient entrance free in air [mGy]: "},
	        {"ESAK.mgy", "Kerma at patient entrance with backscatter (ESAK) [mGy]: "},
	        {"AGD.mgy", "AGD, Average Glandular Dose [mGy]: "},
	        {"DRL.mgy", "Maximum permissible AGD [mGy]: "},
	        
	        {"DRL", "Diagnostic reference level DRL (if available) [mGy]: "},
	        {"DRL.ct", "Maximum permissible CTDIvol (if available) [mGy]: "},
	        {"MC.notAvailable", "Monte-Carlo simulation module is not available in this version!"},
			{ "saveB", "View/Save in database" },
			{ "saveB.toolTip", "View database and/or save in database" },
			{ "saveB.mnemonic", new Character('w') },
						        	        
			//========================================================================
			{ "SaveViewDBFrame.NAME", "Save/View database" },
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			{ "records.label", "Records:" },
			{ "records.border", "Records" },			
			
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
			
			//===============
			{"fluoro.doseRate.label"," => Dose rate (at maximum settings) [mGy/min]: "},
			{"fluoro.max.doseRate.label","Maximum permissible dose rate [mGy/min]: "},
			{"fluoro.dosimeter.read.label","Dosimeter reading: "},
			
			{ "nosave.error.title", "Nothing to save" },
	        { "nosave.error.message", "Please perform dose computation first!" },
	        
	        //rezultat///////////////////////////
	        {"reprod.rezultat", "Results: "},
	        {"reprod.rezultat2", "Results"},
	        {"rezultat.ESAK", " ESAK test result: "},
	        {"rezultat.AGD", " AGD test result: "},
	        {"rezultat.CTDI", " CTDIvol test result: "},
	        {"rezultat.succes", " Test PASSED!"},
	        {"rezultat.fail", " Test NOT PASSED!"},
	        	        	        	        
	        //dialogs
	        {"dialog.number.title", "Error"},
	        {"dialog.number.message", "Insert real numbers!"},
	        {"dialog.number.negative.message", "Insert positive numbers!"},
	        {"dialog.geometry.message", "Wrong geometry, please reset distances!"},
	        
	        {"dialog.ripple", "If anode=Mo or Rh, angle>=9,angle <=23;kv>=25,kv<=32;ripple=0; \n if anod=W, angle >=6 angle<=22;kv>=30;kv<=150!! \n for NONZERO ripple=>kv =55,60,65,...,90!!"},
	        	        
	        {"dialog.age.message", "Invalid phantom age value"},
	        {"dialog.height.message", "Invalid phantom height value"},
	        {"dialog.mass.message", "Invalid phantom weight value"},
	        {"dialog.filtration.message", "Invalid filtration value"},
	        {"dialog.mas.message", "Invalid mAs value"},
	        {"dialog.distance.message", "Invalid focus to phantom mid-plane value"},
	        {"dialog.kap.message", "Invalid KAP value"},
	        {"dialog.run.message", "Invalid number of runs value"},
	        {"dialog.input.message", "Unexpected error has occurred when trying to process input data!"},
	        {"dialog.sliceThickness.message", "Invalid slice thickness value"},
	        {"dialog.pitchFactor.message", "Invalid pitch factor value"},
	        {"dialog.angleIncrement.message", "Invalid rotation angle increment value"},
	        {"dialog.ctdi.message", "Invalid CTDI value"},
	        
	        {"dialog.ctdic.message", "Invalid CTDIc value"}
	        
	        
	};
}
