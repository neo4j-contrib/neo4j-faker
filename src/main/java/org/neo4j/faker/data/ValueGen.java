package org.neo4j.faker.data;


import org.neo4j.faker.util.TDGUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

public class ValueGen {
	Map<String,Object> parsedELM = new HashMap<String,Object>();
	Map<String,Object> parsedNELM = new HashMap<String,Object>();
	Map<String,Object> parsedLELM = new HashMap<String,Object>();
	Map<String,Integer> nelmPOS = new HashMap<String,Integer>();
	Map<String,Object> parsedELMS = new HashMap<String,Object>();
	Map<String, ParsedDate> parsedDates = new HashMap<String,ParsedDate>();
	Map<String,List<String>> parsedFile = new HashMap<String,List<String>>();
	Map<String, DecimalFormat> parsedNumberFormats = new HashMap<String, DecimalFormat>();
	Map<String,Long> codeSequences = new HashMap<String,Long>();
	String capitals = "ABCDEFGHILKLMNOPQRSTUVWXYZ";
	Random random = new Random();
	String TDGRoot;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public ValueGen(String tdgRoot) {
		this.TDGRoot = tdgRoot;
	}
	public long getSeq(String def) {
		if (!codeSequences.containsKey(def)) {
			codeSequences.put(def, Long.valueOf(0));
		}
		long l = codeSequences.get(def);
		l++;
		codeSequences.put(def,l);
		return l;
	}

	
	public ParsedDate getParsedDate(String def) throws Exception {
		if (parsedDates.containsKey(def)) {
			return parsedDates.get(def);
		}
		ParsedDate pd = new ParsedDate(def);
		parsedDates.put(def,pd);
		return pd;
	}
	public boolean elementFromIsString(String def) {
		if (parsedELM.containsKey(def)) {
			return (parsedELM.get(def) instanceof String[]);
		}
		// parse it
		String[] vals = def.split(",");
		if (vals[0].trim().startsWith("'")  ) {
			// string
			List<String> slist = new ArrayList<String>();
			for (String s : vals) {
				if (!s.isEmpty()) {
					// strip away the single quotes
					slist.add(s.replace("'", " ").trim());
				}
			}
			String[] ps = slist.toArray(new String[0]);
			parsedELM.put(def,ps);
			return true;
		} else {
			// numeric
			List<Double> llist = new ArrayList<Double>();
			for (String s: vals) {
				if (!s.isEmpty()) {
					llist.add(Double.valueOf(s));
				}
			}
			parsedELM.put(def, llist.toArray(new Double[0]));
			return false;
		}
		
	}
	public void registerStringElements(String def) {
		if (parsedELM.containsKey(def)) return;
		parsedELM.put(def,def.split(","));
	}
	public void registerNumberElements(String def) {
		if (parsedELM.containsKey(def)) return;
		List<Double> llist = new ArrayList<Double>();
		for (String s: def.split(",")) {
			if (!s.isEmpty()) {
				llist.add(Double.valueOf(s));
			}
		}
		parsedELM.put(def, llist.toArray(new Double[0]));
	}

	public boolean nextElementFromIsString(String def) {
		if (parsedELM.containsKey(def)) {
			return (parsedELM.get(def) instanceof String[]);
		}
		// parse it
		String[] vals = def.split(",");
		if (vals[0].trim().startsWith("'")  ) {
			// string
			List<String> slist = new ArrayList<String>();
			for (String s : vals) {
				if (!s.isEmpty()) {
					// strip away the single quotes
					slist.add(s.replace("'", " ").trim());
				}
			}
			String[] ps = slist.toArray(new String[0]);
			parsedELM.put(def,ps);
			return true;
		} else {
			// numeric
			List<Double> llist = new ArrayList<Double>();
			for (String s: vals) {
				if (!s.isEmpty()) {
					llist.add(Double.valueOf(s));
				}
			}
			parsedELM.put(def, llist.toArray(new Double[0]));
			return false;
		}
		
	}
	public String randomStringElement(String def) {
		if (def == null) return "";
		Random rnd = new Random();
		String[] p = (String[]) parsedELM.get(def);
		return p[rnd.nextInt(p.length)];
	}
	public String nextStringElement(String def) {
		if (def == null) return "";
		int pos = 0;
		if (!nelmPOS.containsKey(def)) {
			nelmPOS.put(def,pos);
		} else {
			pos = nelmPOS.get(def);
		}
		// this change may break the prop file generator
		String[] p = (String[]) parsedELM.get(def);
		if (pos == p.length) pos = 0;
		String r = p[pos];
		pos++;
		nelmPOS.put(def,pos);
		return r;
	}
	public Double nextDoubleElement(String def) {
		if (def == null) return Double.valueOf(-1);
		int pos = 0;
		if (!nelmPOS.containsKey(def)) {
			nelmPOS.put(def,pos);
		} else {
			pos = nelmPOS.get(def);
		}
		Double[] p = (Double[]) parsedELM.get(def);
		if (pos == p.length) pos = 0;
		Double r = p[pos];
		pos++;
		nelmPOS.put(def,pos);
		return r;
	}
	public Long nextLongElement(String def) {
		if (def == null) return Long.valueOf(-1);
		if (!parsedLELM.containsKey(def)) {
			String[] vals = def.split(",");
			List<Long> llist = new ArrayList<Long>();
			for (String s: vals) {
				if (!s.isEmpty()) {
					llist.add(Long.valueOf(s));
				}
			}
			parsedLELM.put(def, llist.toArray(new Long[0]));
		}
		int pos = 0;
		if (!nelmPOS.containsKey(def)) {
			nelmPOS.put(def,pos);
		} else {
			pos = nelmPOS.get(def);
		}
		Long[] p = (Long[]) parsedLELM.get(def);
		if (pos == p.length) pos = 0;
		Long r = p[pos];
		pos++;
		nelmPOS.put(def,pos);
		return r;
	}

	public Long randomLongElement(String def) {

		if (def == null) return Long.valueOf(-1);
		if (!parsedLELM.containsKey(def)) {
			String[] vals = def.split(",");
			List<Long> llist = new ArrayList<Long>();
			for (String s: vals) {
				if (!s.isEmpty()) {
					llist.add(Long.valueOf(s));
				}
			}
			parsedLELM.put(def, llist.toArray(new Long[0]));
		}
		Random rnd = new Random();
		Long[] p = (Long[]) parsedLELM.get(def);
		return p[rnd.nextInt(p.length)];
	}


	public Double randomDoubleElement(String def) {
		if (def == null) return Double.valueOf(-1);
		Random rnd = new Random();
		Double[] p = (Double[]) parsedELM.get(def);
		return p[rnd.nextInt(p.length)];
	}
	public boolean elementsFromIsString(String def) {
		if (parsedELMS.containsKey(def)) {
			return (parsedELMS.get(def) instanceof String[][]);
		}
		// parse it
		String[] vals = def.split(";");
		if (vals[0].trim().startsWith("'")  ) {
			// string format
			List<String[]> alist = new ArrayList<String[]>();
			for (String s : vals) {
				if (!s.isEmpty()) {
					List<String> slist = new ArrayList<String>();
					String[] aval = s.split(",");
					for (String a:aval) {
						if (!a.isEmpty()) {
							slist.add(a.replace("'", " ").trim());
						}
					}
					String[] subArray = slist.toArray(new String[0]);
					alist.add(subArray);
				}
			}
			String[][] ps = alist.toArray(new String[0][0]);
			parsedELMS.put(def,ps);
			return true;
		} else {
			return false;
		}
		
	}
	public boolean elementsFromIsDouble(String def) {
		if (parsedELMS.containsKey(def)) {
			return (parsedELMS.get(def) instanceof Double[][]);
		}
		// parse it
		String[] vals = def.split(";");
		// Double
		List<Double[]> llist = new ArrayList<Double[]>();
		for (String s: vals) {
			if (!s.isEmpty()) {
				List<Double> slist = new ArrayList<Double>();
				String[] aval = s.split(",");
				for (String a:aval) {
					if (!a.isEmpty()) {
						slist.add(Double.valueOf(a.replace("'", " ").trim()));
					}
				}
				Double[] subArray = slist.toArray(new Double[0]);
				llist.add(subArray);
			}
		}
		Double[][] ps = llist.toArray(new Double[0][0]);
		parsedELMS.put(def, ps);
		return true;
	}


	public boolean elementsFromIsLong(String def) {
		if (parsedELMS.containsKey(def)) {
			return (parsedELMS.get(def) instanceof Long[][]);
		}
		// parse it
		String[] vals = def.split(";");
		// Double
		List<Long[]> llist = new ArrayList<Long[]>();
		for (String s: vals) {
			if (!s.isEmpty()) {
				List<Long> slist = new ArrayList<Long>();
				String[] aval = s.split(",");
				for (String a:aval) {
					if (!a.isEmpty()) {
						slist.add(Long.valueOf(a.replace("'", " ").trim()));
					}
				}
				Long[] subArray = slist.toArray(new Long[0]);
				llist.add(subArray);
			}
		}
		Long[][] ps = llist.toArray(new Long[0][0]);
		parsedELMS.put(def, ps);
		return true;
	}


	public String[] randomStringElements(String def) {
		if (def == null) return new String[0];
		String[][] all = (String[][]) parsedELMS.get(def);
		Random rnd = new Random();
		String[] p = all[rnd.nextInt(all.length)]; 
		return p;
	}
	public Double[] randomDoubleElements(String def) {
		if (def == null) return new Double[0];
		Double[][] alls = (Double[][]) parsedELMS.get(def);
		Random rnd = new Random();
		Double[] p = alls[rnd.nextInt(alls.length)];
		return p;
	}
	public Integer getRandomYear(String def) {
		if (def == null) return Integer.valueOf(0);
		String[] vals = def.split(",");
		int yF = Integer.valueOf(vals[0]);
		int yT = Integer.valueOf(vals[1]);
		int diff = yT - yF;
		int ir = random.nextInt(diff);
		return yF + ir;
	}
	public Object getFileValue(String def) throws IOException {
		String[] parms = def.split(",");
		if (parms.length != 2) {
			return "listFile must have two parameters!! ";
		}
		String fileName = parms[0];
		String returnType = parms[1];
		// the list
		List<String> values = null;
		if (parsedFile.containsKey(def)) {
			values = parsedFile.get(def);
		} else {
			values = TDGUtils.readResourceFile(TDGRoot, fileName);
			parsedFile.put(def, values);
		}
		int rndIndex = random.nextInt(values.size());
		String v = values.get(rndIndex);
		if (returnType.equalsIgnoreCase("integer")) {
			return Integer.valueOf(v);
		} else if (returnType.equalsIgnoreCase("double")){
			return Double.valueOf(v);
		} else if (returnType.equalsIgnoreCase("long")){
			return Long.valueOf(v);
		} else {
			return v; // defaults to string
		}
	}
	public Object getNextFileValue(String def) throws IOException {
		String[] parms = def.split(",");
		if (parms.length != 3) {
			return "listFileNext must have three parameters!! ";
		}
		String fileName = parms[0];
		String returnType = parms[1];
		String identifier = parms[2];
		// the list
		List<String> values = null;
		if (parsedFile.containsKey(def)) {
			values = parsedFile.get(def);
		} else {
			values = TDGUtils.readResourceFile(TDGRoot,fileName);
			parsedFile.put(def, values);
		}
		Integer iPos = nelmPOS.get(def);
		if (iPos == null) {
			iPos = Integer.valueOf(0);
			nelmPOS.put(def, iPos);
		}
		int pos = iPos;
		String v = values.get(pos);
		pos++;
		if (pos >= values.size()) {
			pos = 0;
		}
        nelmPOS.put(def, pos); 
		if (returnType.equalsIgnoreCase("integer")) {
			return Integer.valueOf(v);
		} else if (returnType.equalsIgnoreCase("double")){
			return Double.valueOf(v);
		} else if (returnType.equalsIgnoreCase("long")){
			return Long.valueOf(v);
		} else {
			return v; // defaults to string
		}
	}
	public Object getNumberValue(String def) {
		String[] parms = def.split(",");
		if (parms.length < 2 || parms.length > 3) {
			return "the number must have two or three parameters!! ";
		}
		int from = Integer.valueOf(parms[0]);
		int to = Integer.valueOf(parms[1]);
		if (parms.length == 2) {
			return genDoubleValue(from,to);
		} else {
		   Double v = genDoubleValue(from,to);
		   if (parsedNumberFormats.containsKey(parms[2])) {
			   return parsedNumberFormats.get(parms[2]).format(v); 
		   } else {
			   DecimalFormat df = new DecimalFormat(parms[2]);
			   parsedNumberFormats.put(parms[2],df);
			   return df.format(v);
		   }
		}
	}
	public Object getNumberValueR(String def) {
		String[] parms = def.split(",");
		if (parms.length != 3) {
			return "the numberR must have three parameters!! ";
		}
		int from = Integer.valueOf(parms[0]);
		int to = Integer.valueOf(parms[1]);
		int d = Integer.valueOf(parms[2]);
		Double v = genDoubleValue(from,to);
		String format = "###";
		if (d > 0) {
			format = format + ".";
			for (int i =0 ; i < d; i++) {
				format = format + "#";
			}
		}
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		symbols.setDecimalSeparator('.');
		DecimalFormat nf = new DecimalFormat(format, symbols);
		return Double.valueOf(nf.format(v));
	}
    public Boolean getBoolean(String funcDef) {
    	int truetreshold = Integer.valueOf(funcDef);
    	int i = random.nextInt(100);
    	return i < truetreshold;
    }
	public Long getLong(String def) {
		String[] parms = def.split(",");
		int from = Integer.valueOf(parms[0]);
		int to = Integer.valueOf(parms[1]);
		int rdnInt = random.nextInt(to - from);
		long v  = from + rdnInt;
		return v;
	}
	private Double genDoubleValue(int from, int to) {
		int diff = to - from -1 ;
		int rdnInt = random.nextInt(diff);
		Double v  = from + rdnInt + random.nextDouble();
		return v;
	}
    public Integer getPercentage() {
    	return random.nextInt(101);
    }
	public String getCode(String def) {
		StringBuffer db = new StringBuffer();
		char[] array = def.toCharArray();
		for (char c : array) {
			if (c == '#') {
				db.append(random.nextInt(10));
			} else if (c == '_') {
				db.append(capitals.charAt(random.nextInt(capitals.length())));
			} else {
				db.append(c);
			}
		}
		return db.toString().trim();
	}
	public String getIP(String def) {
		// split on dot
		String[] elms = def.split("\\.");
//		System.out.println("length elements " + elms.length + " def = " + def);
		if (elms.length != 4) return "function IP needs 4 sections #.#.#.# or 178.33.2.# !";
		String val = "";
		for (String elm: elms) {
			if (elm.isEmpty() || elm.trim().equals("#")) {
				// we have to generate a value
				elm = "" + random.nextInt(255);
			}
			// this is the current value
			if (val.equals("")) {
				val = elm;
			} else {
				val = val + "." + elm;
			}
		}
		return val;
	}
	
	
	public Long getNow() {
		Date d = new Date();
		Long n = Long.valueOf(d.getTime());
		return n;
	}
	public String getToday() {
		Date d = new Date();
		return sdf.format(d);
	}
	private List<String> readFile(File fin) throws IOException {
		List<String> list = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(fin);
	 
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		while ((line = br.readLine()) != null) {
			list.add(line.trim());
		}
	 
		br.close();
		return list;
	}
}
