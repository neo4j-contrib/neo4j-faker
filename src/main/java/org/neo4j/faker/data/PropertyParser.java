package org.neo4j.faker.data;

import com.github.javafaker.Faker;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class PropertyParser {
	Faker faker;
	PersonNamesGen names; 
	ValueGen valuegen;
	Writer writer;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public PropertyParser(Writer writer, String tdgRoot) throws IOException {
		this.writer = writer;
		out(" Initializing faker library");
		faker = new Faker();
		// fakerDump(faker);
		out(" Initializing Name generator");
		names = new PersonNamesGen(tdgRoot);
		valuegen = new ValueGen(tdgRoot);
	}
	public void parse(Map<String,Object> nodeProperties, Map<String,String> propDefinitions) throws Exception {
		for (String prop: propDefinitions.keySet()) {
			if (prop.startsWith("~")) {
				if (prop.equals("~FromToDateTime")) {
					// get the from date first
					String[] dates = propDefinitions.get(prop).split(","); 
					nodeProperties.put("from", genValue("randomDateTime:" + dates[0] + "," + dates[1] ));
					nodeProperties.put("to", genValue("randomDateTime:" + dates[2] + "," + dates[3] ));
				} else if (prop.equals("~FromToDate")) {
					String[] dates = propDefinitions.get(prop).split(","); 
					nodeProperties.put("from", genValue("randomDate:" + dates[0] + "," + dates[1] ));
					nodeProperties.put("to", genValue("randomDate:" + dates[2] + "," + dates[3] ));
				} else if (prop.equals("~DateAndTime")) {
					Date d = valuegen.getParsedDate(propDefinitions.get(prop)).getRandomDateObject();
					String s = sdf.format(d);
					nodeProperties.put("date",s);
					nodeProperties.put("time", d.getTime());
				} else if (prop.equals("~DateAndTimeNowR")) {
					long cur = System.currentTimeMillis();
					String def = propDefinitions.get(prop);
					//System.out.println("def " + def);
					// the amount of minutes
					int minutesRange = (Integer) Integer.parseInt(def);
					Random r = new Random();
					int cor = r.nextInt(minutesRange * 60000);
					Date d = new Date();
					d.setTime(cur - cor);
					String s = sdf.format(d);
					nodeProperties.put("date",s);
					nodeProperties.put("time", d.getTime());
				} else if (prop.equals("~Person")) {
					String[] dates = propDefinitions.get(prop).split(","); 
					String firstName = (String) genValue("firstName");
					String lastName = (String) genValue("lastName");
					nodeProperties.put("firstName", firstName);
					nodeProperties.put("lastName", lastName);
					nodeProperties.put("fullName", firstName + " " + lastName);
			        nodeProperties.put("birthDate", genValue("randomDate:" + dates[0] + "," + dates[1] ));
			        nodeProperties.put("ssn", genValue("code:__#######_#"));
				} else if (prop.equals("~User")) {
					String[] parameters = propDefinitions.get(prop).split(",");
					String firstName = (String) genValue("firstName");
					String lastName = (String) genValue("lastName");
					String userid = firstName.toLowerCase() + lastName.toLowerCase().substring(0,2);
					String mail = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + parameters[2];
					nodeProperties.put("firstName", firstName);
					nodeProperties.put("lastName", lastName);
					nodeProperties.put("userid", userid);
					nodeProperties.put("mail", mail);
			        nodeProperties.put("startDate", genValue("randomDate:" + parameters[0] + "," + parameters[1] ));
				} else {
					nodeProperties.put(prop, genValue("TBD" + propDefinitions.get(prop)) );
				}
			} else {
				nodeProperties.put(prop, genValue(propDefinitions.get(prop)) );
			}
		}
	}
	private Object genValue(String propDef) throws Exception {
		Object r = null;
		String function = "unknown";
		String funcDef = "";
		if (propDef.indexOf(":") == -1) {
			function = propDef;
		} else {
			function = propDef.substring(0, propDef.indexOf(":"));
			funcDef = propDef.substring(propDef.indexOf(":")+1);
			
		}
		switch (function.toLowerCase()) {
			case "firstname":
				r = names.firstName();
				break;
			case "lastname":
				r = names.lastName();
				break;
			case "fullname":
				r = names.fullName();
				break;
			case "constant" :
				r = funcDef;
				break;
			case "companyname" :
				r = faker.company().name().replaceAll("'","-");
				break;
			case "concat" :
				StringBuffer sb = new StringBuffer();
				for (String f: funcDef.split(";")) {
					sb.append(genValue(f).toString());
				}
				r = sb.toString();
				break;
			case "companybs" :
				r = faker.company().bs().replaceAll("'","-");
				break;
			case "companyslogan" :
				r = faker.company().catchPhrase().replaceAll("'","-");
				break;
			case "isbn10" :
				r = faker.code().isbn10();
				break;
			case "isbn13" :
				r = faker.code().isbn13();
				break;
			case "email" :
				r = faker.internet().emailAddress();
				break;
			case "url" :
				r = faker.internet().url();
				break;
			case "zipcode" :
				r = faker.address().zipCode();
				break;
			case "country" :
				r = faker.address().country();
				break;
			case "phonenumber" :
				r = faker.phoneNumber().phoneNumber();
				break;
			case "timezone" :
				r = faker.address().timeZone();
				break;
			case "longitude" :
				r = faker.address().longitude();
				break;
			case "streetaddress" :
				r = faker.address().streetAddress(false).replaceAll("'","-");
				break;
			case "streetaddressfull" :
				r = faker.address().streetAddress(true).replaceAll("'","-");
				break;
			case "secundaryaddress" :
				r = faker.address().secondaryAddress().replaceAll("'","-");
				break;
			case "streetname" :
				r = faker.address().streetName().replaceAll("'","-");
				break;
			case "streetsuffix" :
				r = faker.address().streetSuffix();
				break;
			case "streetaddressnumber" :
				r = faker.address().streetAddressNumber();
				break;
			case "latitude" :
				r = faker.address().latitude();
				break;
			case "elementfrom" :
				if (valuegen.elementFromIsString(funcDef)) {
					r = valuegen.randomStringElement(funcDef);
				} else {
					r = valuegen.randomDoubleElement(funcDef);
				}
				break;
			case "nextelementfrom" :
				if (valuegen.nextElementFromIsString(funcDef)) {
					r = valuegen.nextStringElement(funcDef);
				} else {
					r = valuegen.nextDoubleElement(funcDef);
				}
				break;
			case "nextelementfromlong" :
				r = valuegen.nextLongElement(funcDef);
				break;
			case "elementsfrom" :
				if (valuegen.elementsFromIsString(funcDef)) {
					r = valuegen.randomStringElements(funcDef);
				} else {
					r = valuegen.randomDoubleElements(funcDef);
				}
				break;
			case "randomdatestring" :
				r = valuegen.getParsedDate(funcDef).getRandomDateString();
				break;
			case "randomdate" :
				r = valuegen.getParsedDate(funcDef).getRandomDate();
				break;
			case "randomdatetime" :
				r = valuegen.getParsedDate(funcDef).getRandomDateTime();
				break;
			case "year" :
				r = valuegen.getRandomYear(funcDef);
				break;
			case "now" :
				r = valuegen.getNow();
				break;
			case "today" :
				r = valuegen.getToday();
				break;
			case "listfile" :
				r = valuegen.getFileValue(funcDef);
				break;
			case "listfilenext" :
				r = valuegen.getNextFileValue(funcDef);
				break;
			case "number" :
				r = valuegen.getNumberValue(funcDef);
				break;
			case "numberr" :
				r = valuegen.getNumberValueR(funcDef);
				break;
			case "code" :
				r = valuegen.getCode(funcDef);
				break;
			case "ip" :
				r = valuegen.getIP(funcDef);
				break;
			case "codeseq" :
				r = valuegen.getCode(funcDef) + valuegen.getSeq(funcDef);
				break;
			case "sequence" :
				r = valuegen.getSeq(funcDef);
				break;
			case "int" :
				r = valuegen.getLong(funcDef);
				break;
			case "percentage" :
				r = valuegen.getPercentage();
				break;
			case "boolean" :
				r = valuegen.getBoolean(funcDef);
				break;
			case "constantstring" :
				r = funcDef;
				break;
			case "constantnumber" :
				r = new Double(funcDef);
				break;
			case "constantlong" :
				r = new Long(funcDef);
				break;
			case "txtwords" :
				r = faker.lorem().words(Integer.valueOf(funcDef)).toArray(new String[0]);
				break;
			case "txttext" :
				r = faker.lorem().fixedString(Integer.valueOf(funcDef));
				break;
			case "txtsentence" :
				r = faker.lorem().sentence();
				break;
			case "txtsentences" :
				r = faker.lorem().sentences(Integer.valueOf(funcDef)).toArray(new String[0]);
				break;
			case "txtparagraph" :
				r = faker.lorem().paragraph(Integer.valueOf(funcDef));
				break;
			case "txtparagraphs" :
				r = faker.lorem().paragraphs(Integer.valueOf(funcDef)).toArray(new String[0]);
				break;
			default :
				r = propDef; // just giving through
				
		}
		return r;
	}

	private void out(String line) throws IOException {
		if (writer != null) {
			writer.write(line);
			writer.write("\n");
			writer.flush(); 
		} else {
			System.out.println(line);
		}
	}
}
