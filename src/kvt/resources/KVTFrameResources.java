package kvt.resources;

import java.util.ListResourceBundle;

/**
 * Class for KvT resources
 * 
 * @author Dan Fulea, 29 Apr. 2015
 */
public class KVTFrameResources extends ListResourceBundle{

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
					
			{ "KVTFrame.NAME", "Tube kilovoltage and exposure time" },
			//========================================================================
			{ "SaveViewDBFrame.NAME", "Save/View database" },
			{ "data.load", "Data" },
			{ "sort.by", "Sort by: " },
			{ "records.count", "Records count: " },
			{ "records.label", "Records:" },
			{ "records.border", "Records" },
			{ "kvR.border", "Kilovoltage repeatability" },
			{ "tR.border", "Exposure time repeatability" },
			{ "kvAc.border", "Kilovoltage accuracy" },
			{ "tAc.border", "Exposure time accuracy" },
			{ "details.border", "Details" },
			{ "t.border", "Exposure time repeatability" },
			{ "save.saveB", "Save" },
			{ "save.saveB.toolTip", "Save record in database" },
			{ "save.saveB.mnemonic", new Character('S') },
			{ "save.deleteB", "Delete" },
			{ "save.deleteB.toolTip", "Delete record" },
			{ "save.deleteB.mnemonic", new Character('D') },
			{ "save.viewB", "View all records" },
			{ "save.viewB.toolTip", "View all records" },
			{ "save.viewB.mnemonic", new Character('V') },
			//=========================================================================
			{"reprod.tab.title", "KV repeatability"},
	        {"ac.tab.title", "KV accuracy"},
	        {"tab.related.Label", "Double click on editable cells below to change values!"},
	        {"reprod.tab.title1", "Exposure time repeatability"},
	        {"ac.tab.title1", "Exposure time accuracy"},
//===============================
	        { "nosave.kv.rep.error.message", "Please perform KV repeatability test!" },
	        { "nosave.t.rep.error.message", "Please perform Exposure time repeatability test!" },
	        { "nosave.kv.acc.error.message", "Please perform KV accuracy test!" },
	        { "nosave.t.acc.error.message", "Please perform Exposure time accuracy test!" },
			{ "nosave.error.title", "Nothing to save" },
	        
//=======================	        
	        {"menu.file", "Fisier"},
	        {"menu.file.mnemonic", new Character('F') },
	        {"menu.file.exit", "Iesire"},
	        {"menu.file.exit.mnemonic", new Character('I') },
	        {"menu.file.saveBD", "Salvare in BD"},
	        {"menu.file.saveBD.mnemonic", new Character('S') },
	        {"menu.file.raport", "Raport..."},
	        {"menu.file.raport.mnemonic", new Character('p') },
	        {"menu.view", "Vizualizare BD"},
	        {"menu.view.mnemonic", new Character('V')},
	        //toolbar buttons
	        { "saveB", "View/Save in database" },
			{ "saveB.toolTip", "View database and/or save in database" },
			{ "saveB.mnemonic", new Character('w') },
	        {"toolBar.saveBD", "Salvare in BD"},
	        {"toolBar.saveBD.mnemonic", new Character('S')},
	        {"toolBar.saveBD.toolTip", "Salveaza rezultatele in baza de date"},
	        {"toolBar.saveBD.iconName", "Save16"},
	        {"toolBar.saveBD.iconName.url", "/Jad/images/Save16.gif"},
	        {"toolBar.raport", "Raport..."},
			{"toolBar.raport.mnemonic", new Character('p')},
	        {"toolBar.raport.toolTip", "Genereaza raportul rezultatelor in vederea printarii"},
	        {"toolBar.raport.iconName", "raport"},
	        {"toolBar.raport.iconName.url", "/Jad/images/raport.gif"},
	        {"toolBar.bd", "Vizualizare BD..."},
			{"toolBar.bd.mnemonic", new Character('B')},
	        {"toolBar.bd.toolTip", "Vizualizarea bazei de date si a operatiilor adecvate"},
	        {"toolBar.bd.iconName", "About16"},
	        {"toolBar.bd.iconName.url", "/Jad/images/About16.gif"},
	        {"toolBar.exit", "Iesire"},
			{"toolBar.exit.mnemonic", new Character('I')},
	        {"toolBar.exit.toolTip", "Inchiderea acestei ferestrei"},
	        {"toolBar.exit.iconName", "check"},
	        {"toolBar.exit.iconName.url", "/Jad/images/check.gif"},
	        //kvP
	        {"tab.columns", new String[] {"Set","Measured"}},/////////////////
	        {"kvp.border","Kilovoltage repeteability: "},////
	        {"kvp.border2","Kilovoltage accuracy: "},///////
	        {"kvp.setKvL","Set kilovoltage: "},
	        {"kvp.cvmpL","Maximum permissible variation [%]: "},
	        {"kvp.maskvL","Measured:"},
	        {"kvp.addB", "Add"},
	        {"kvp.addB.mnemonic", new Character('A')},
	        {"kvp.calcB", "Compute"},
	        {"kvp.calcB.mnemonic", new Character('C')},
	        {"kvp.delB", "Delete"},
	        {"kvp.delB.mnemonic", new Character('D')},
	        {"kvp.resetB", "Delete all"},
	        {"kvp.resetB.mnemonic", new Character('e')},
	        {"kvac.addrB", "Add row"},
	        {"kvac.addrB.mnemonic", new Character('A')},
	        {"kvac.delrB", "Delete row"},
	        {"kvac.delrB.mnemonic", new Character('D')},
	        {"kvac.calcB", "Compute"},
	        {"kvac.calcB.mnemonic", new Character('C')},
	        {"ac.label", "Maximum permissible difference [%]:"},
	        //texpP
	        {"texp.border","Exposure time repeteability: "},//
	        {"texp.border2","Exposure time accuracy: "},//
	        {"texp.setTexpL","Set exposure time: "},
	        {"texp.cvmpL","Maximum permissible variation [%]: "},
	        {"texp.mastexpL","Measured:"},
	        {"texp.addB", "Add"},
	        {"texp.addB.mnemonic", new Character('A')},
	        {"texp.calcB", "Compute"},
	        {"texp.calcB.mnemonic", new Character('C')},
	        {"texp.delB", "Delete"},
	        {"texp.delB.mnemonic", new Character('D')},
	        {"texp.resetB", "Delete all"},
	        {"texp.resetB.mnemonic", new Character('e')},
	        {"texpac.addrB", "Add row"},
	        {"texpac.addrB.mnemonic", new Character('A')},
	        {"texpac.delrB", "Delete row"},
	        {"texpac.delrB.mnemonic", new Character('D')},
	        {"texpac.calcB", "Compute"},
	        {"texpac.calcB.mnemonic", new Character('C')},
	        //rezultat
	        {"reprod.rezultat", "Results: "},
	        {"reprod.rezultat.succes", " Test PASSED!"},
	        {"reprod.rezultat.fail", " Test NOT PASSED!"},
	        {"reprod.rezultat.cv", "Coefficient of variation (C.V.) [%] = "},
	        {"reprod.rezultat.cvmp", "Maximum permissible C.V. [%] = "},
	        {"ac.rezultat.cv", "Diff.(%) = "},
	        {"ac.rezultat.cvmp", "Diff.max.(%) = "},
	        {"ac.rezultat.set", "Set = "},
	        {"ac.rezultat.mas", "Meas. = "},
	        //bd
	        {"viewBd.title","Vizualizare baza de date"},
	        {"viewBd.ac.kv.tab.baseName","ackv"},
	        {"viewBd.ac.texp.tab.baseName","actexp"},
	        {"viewBd.reprod.kv.tab.baseName","reprodkv"},
	        {"viewBd.reprod.texp.tab.baseName","reprodtexp"},
	        {"viewBd.tab.sufixName","date"},
	        {"commonColumnFromUpdateTables", "id"},
	        {"viewBd.recordLabel","Nr. inregistrari:"},
	        {"viewBd.selectCurrentB", "Inregistrare curenta"},
	        {"viewBd.selectCurrentB.mnemonic", new Character('c')},
	        {"viewBd.texp.selectCurrentB.mnemonic", new Character('u')},
	        {"viewBd.selectAllB", "Toate inregistrarile"},
	        {"viewBd.selectAllB.mnemonic", new Character('T')},
	        {"viewBd.texp.selectAllB.mnemonic", new Character('o')},
	        {"viewBd.selectSelectB", "Inregistrarea selectata"},
	        {"viewBd.selectSelectB.mnemonic", new Character('S')},
	        {"viewBd.texp.selectSelectB.mnemonic", new Character('e')},
	        {"saveDb.reprod.tab2.columns", new String[] {"nrcrt","id","fixat","masurat"}},
	        {"saveDb.reprod.tab1.columns", new String[] {"id","cv","cv_max_permis","rezultat_test"}},
	        {"saveDb.ac.tab1.columns", new String[] {"nrcrt","id","fixat","masurat","diff",
	        					"diff_max_permisa","rezultat_test"}},
	        {"saveDb.true", "da"},
	        {"saveDb.false", "nu"},
	        //raport
	        {"raport.category.db","Informatii generale"},
	        {"raport.umed","Unitatea medicala:"},
	        {"raport.sectia","Sectia:"},
	        {"raport.localitate","Localitatea:"},
	        {"raport.tipxinstal","Tip instalatie X:"},
	        {"raport.numarserial","Numar serial:"},
	        {"raport.datamasurarii","Data masuratorilor:"},
	        {"raport.adresa","Adresa:"},
	        {"raport.telefon","Telefon:"},
	        {"raport.email","E-mail:"},
	        {"raport.contact","Persoana de contact:"},
	        {"raport.reprod.category.teor.kv", "Referinte teoretice -Reproductibilitate (kilovoltaj):"},
	        {"raport.reprod.category.teor.texp", "Referinte teoretice -Reproductibilitate (timp expunere):"},
	        {"raport.reprod.kv.teor", "Kilovoltaj setat:"},
	        {"raport.reprod.texp.teor", "Timp de expunere setat:"},
	        {"raport.reprod.cv.teor", "Coeficient de variatie maxim permis (%):"},
	        {"raport.reprod.category.exp.kv","Evaluari experimentale -Reproductibilitate (kilovoltaj):"},
	        {"raport.reprod.category.exp.texp","Evaluari experimentale -Reproductibilitate (timp expunere):"},
	        {"raport.reprod.punctederetea","Valori masurate:"},
	        {"raport.reprod.punctederetea.kv","kV"},
	        {"raport.reprod.punctederetea.texp","s sau ms"},
	        {"raport.reprod.cvkv.exp","Coeficient de variatie calculat (%): "},
	        {"raport.reprod.rez.exp","Rezultat test: "},
			{"raport.ac.category.kv", "Acuratete kilovoltaj-(kV):"},
	        {"raport.ac.category.texp", "Acuratete timp expunere -(s sau ms):"},
	        {"raport.ac.cv.teor", "Diferenta maxima permisa (%):"},
	        {"raport.ac.rez.exp","Rezultat test: "},
	        {"raport.ac.set.exp","Setat:"},
	        {"raport.ac.mas.exp","Masurat:"},
	        {"raport.ac.dif.exp","Diferenta (%):"},
	        //dialogs
	        {"dialog.insertInListError.title", "Error"},
	        {"dialog.insertInList.message", "Insert real numbers!"},
	        {"dialog.insertInList.message2", "Too few data entries!"},
	        {"dialog.insertInTableError.title", "Error...."},
	        {"dialog.insertInTable.message", "Insert positive numbers / Delete empty rows!"},
	        {"dialog.processReportError.title", "Eroare...."},
	        {"dialog.processReportError.message", "Eroare la procesarea acestui raport!"},
	        {"dialog.reportError.title", "Eroare...."},
	        {"dialog.reportError.message", "Eroare neasteptata la afisarea raportului!"},
	        {"dialog.selectError.title", "Eroare...."},
	        {"dialog.selectError.message", "Nu se poate efectua aceasta selectie!!"},
	        {"dialog.alterError.title", "Eroare...."},
	        {"dialog.alterError.message", "Nu se poate efectua aceasta modificare in baza de date!!"},
	        {"dialog.insertError.title", "Eroare...."},
	        {"dialog.insertError.message", "Nu se poate introduce in baza de date!!"},
	        {"dialog.exit.title", "Confirmare inchidere..."},
	        {"dialog.exit.message", "Salvati modificarile in baza de date?"},
	        {"dialog.exit.buttons", new Object[]{"Da","Nu"}},
			
	};
}