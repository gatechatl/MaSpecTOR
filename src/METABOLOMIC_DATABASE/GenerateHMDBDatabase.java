package METABOLOMIC_DATABASE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class GenerateHMDBDatabase {

	public static String parameter_info() {
		return "[directoryNameInput] [OutputFile]";
	}
	public static void execute(String[] args) {
		try {
			String directoryName = args[0];
			
			String outputFile = args[1];
			FileWriter fwriter = new FileWriter(outputFile);
            BufferedWriter out = new BufferedWriter(fwriter);
			
			File f = new File(directoryName);
			File[] files = f.listFiles();
			for (File file: files) {
				if (file.getName().contains("HMDB") && file.getName().contains("xml")) {
					HashMap map = readXML(file.getPath());
					Iterator itr = map.keySet().iterator();
					while (itr.hasNext()) {
						String id = (String)itr.next();
						PUBCHEM pubchem = (PUBCHEM)map.get(id);
						out.write(pubchem.ID + "\t" + pubchem.FORMULA + "\t" + pubchem.InchIKey + "\t" + pubchem.InchI + "\t" + pubchem.SMILE + "\t" + pubchem.IUPAC_trad + "\t" + pubchem.MONOISOTOPICMASS + "\t" + pubchem.NAME + "\n");
						out.flush();
						//System.out.println(pubchem.ID + "\t" + pubchem.FORMULA + "\t" + pubchem.InchIKey + "\t" + pubchem.InchI + "\t" + pubchem.SMILE + "\t" + pubchem.IUPAC_trad + "\t" + pubchem.MONOISOTOPICMASS);
					}
					
				}
				
			
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static HashMap readXML(String fileName) {
		HashMap map = new HashMap();
		try {
			PUBCHEM pubchem = new PUBCHEM();
			String id = "";
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fstream);
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
				if (str.contains("<accession>")) {
					if (!pubchem.ID.equals("")) {
						//map.put(id, pubchem);
						System.out.println("Reading ID: " + id);
					}
					String idstr = str.replaceAll("accession", "");
					idstr = idstr.replaceAll("<>", "");
					idstr = idstr.replaceAll("</>", "").trim();
					//id = new Integer(idstr);
					id = idstr;
					pubchem = new PUBCHEM();
					pubchem.ID = idstr;
					boolean skipSecondary = true;
					while (in.ready() && skipSecondary) {
						str = in.readLine();
						if (str.contains("/secondary_accessions")) {
							skipSecondary = false;
						}
					}
				}
				
				if (str.contains("<chemical_formula>")) {
					System.out.println("chem_formula: " + str);
					String formula = str.replaceAll("chemical_formula", "");
					formula = formula.replaceAll("<>", "");
					formula = formula.replaceAll("</>", "").trim();
					//System.out.println(formula);
					pubchem.FORMULA = formula;
				
				}
				
				if (str.contains("<smiles>")) {
						
					String val = str.replaceAll("smiles", "");
					val = val.replaceAll("<>", "");
					val = val.replaceAll("</>", "").trim();
					//System.out.println(val);
					pubchem.SMILE = val;
				
			
				}
				
				if (str.contains("<inchi>")) {				
					String val = str.replaceAll("inchi", "");
					val = val.replaceAll("<>", "");
					val = val.replaceAll("</>", "").trim();
					//System.out.println(val);
					pubchem.InchI = val;
				
				}
				
				if (str.contains("<inchikey>")) {
					String val = str.replaceAll("inchikey", "");
					val = val.replaceAll("<>", "");
					val = val.replaceAll("</>", "").trim();
					//System.out.println(val);
					pubchem.InchIKey = val;
				
				}

				if (str.contains("<monisotopic_moleculate_weight>")) {
					String val = str.replaceAll("monisotopic_moleculate_weight", "");
					val = val.replaceAll("<>", "");
					val = val.replaceAll("</>", "").trim();
					//System.out.println(val);
					pubchem.MONOISOTOPICMASS = val;
				
				}
				if (str.contains("<kind>logp</kind>")) {
					while (!str.contains("<value>")) {
						str = in.readLine();
					}
					if (str.contains("value")) {
						String val = str.replaceAll("value", "");
						val = val.replaceAll("<>", "");
						val = val.replaceAll("</>", "").trim();
						//System.out.println(val);
						pubchem.LogP = val;
					}
				}
				
				
				if (str.contains("<iupac_name>")) {

					String val = str.replaceAll("iupac_name", "");
					val = val.replaceAll("<>", "");
					val = val.replaceAll("</>", "").trim();
					//System.out.println(val);
					pubchem.IUPAC_trad = val;
				
					
				}
				if (str.contains("<name>")) {
					String val = str.replaceAll("name", "");
					val = val.replaceAll("<>", "");
					val = val.replaceAll("</>", "").trim();
					//System.out.println(val);
					if (pubchem.NAME.equals("")) {
						pubchem.NAME = val;
					}
					
				}
			}
			System.out.println(pubchem.ID + " " + pubchem.FORMULA);
			map.put(id, pubchem);
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public static String convertInt2String(int num) {
		String str = (new Integer(num)).toString();
		while (str.length() < 9) {
			str = "0" + str;
		}
		return str;
	}
	public static void executeCommand(String executeThis) {
    	try {
    		
    		String buffer = UUID.randomUUID().toString();
        	writeFile(buffer + "tempexecuteCommand.sh", executeThis);
        	String[] command = {"sh", buffer + "tempexecuteCommand.sh"};
        	Process p1 = Runtime.getRuntime().exec(command);
        	BufferedReader inputn = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        	String line=null;
        	while((line=inputn.readLine()) != null) {}
        	inputn.close();
        	p1.destroy();
        	File f = new File(buffer + "tempexecuteCommand.sh");
        	f.delete();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	public static void writeFile(String fileName, String command) {
    	try {
        	FileWriter fwriter2 = new FileWriter(fileName);
            BufferedWriter out2 = new BufferedWriter(fwriter2);
            out2.write(command + "\n");
            out2.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	static class PUBCHEM {
		public String NAME = "";
		public String ID = "";
		public String FORMULA = "";
		public String LogP = "";
		public String InchI = "";
		public String InchIKey = "";
		public String SMILE = "";
		public String IUPAC_trad= "";
		public String MONOISOTOPICMASS = "";
		
	}
}
