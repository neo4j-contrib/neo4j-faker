package org.neo4j.faker.proc;

import org.neo4j.faker.core.DynRel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

public class DemoDataGen {
	public static final String ONE_TO_MANY = "1-n";
	public static final String MANY_TO_ONE = "n-1";
	public static final String ONE_TO_ONE = "1-1";


	@Context public GraphDatabaseService db;

	@Context public Log log;

	@UserFunction(name = "fkr.user")
	@Description("generates a system User with firstname, lastname, userId, mail and startdate")
	public Map<String,Object> generateUser(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo, final @Name("companyDomain") String compDomain) throws Exception {
		Map<String,Object> m = new HashMap<>();

		String firstName = DDGFunctions.getInstance( db).getNames().firstName();
		String lastName = DDGFunctions.getInstance( db).getNames().lastName();
		String userid = firstName.toLowerCase() + lastName.toLowerCase().substring(0,2);
		m.put("firstName", firstName);
		m.put("lastName", lastName);
		m.put("userid", userid);
		m.put("mail", firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + compDomain);
		m.put("startDate", DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFrom + "," + dTo).getRandomDateString());
		return m;
	}

	// FromToDateTime
	@UserFunction(name = "fkr.fromToDateTime")
	@Description("This will generate a from and a to timestamp property (number value generated with date YYYYMMDD) ")
	public Map<String,Object> generateFromToDateTime(final @Name ("dateFromFrom") String dFromFrom, final @Name("dateFromTo") String dToForm,final @Name ("dateToFrom") String dToFrom, final @Name("dateToTo") String dToTo) throws Exception {
		Map<String,Object> m = new HashMap<>();
		m.put("from", DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFromFrom + "," + dToFrom).getRandomDateTime());
		m.put("to", DDGFunctions.getInstance( db).getValuegen().getParsedDate(dToFrom + "," + dToTo).getRandomDateTime());
		return m;
	}
	// FromToDate
	@UserFunction(name = "fkr.fromToDate")
	@Description("This will generate a from and a to date property (number value generated with date YYYYMMDD) ")
	public Map<String,Object> generateFromToDate(final @Name ("dateFromFrom") String dFromFrom, final @Name("dateFromTo") String dToForm,final @Name ("dateToFrom") String dToFrom, final @Name("dateToTo") String dToTo) throws Exception {
		Map<String,Object> m = new HashMap<>();
		m.put("from", DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFromFrom + "," + dToFrom).getRandomDate());
		m.put("to", DDGFunctions.getInstance( db).getValuegen().getParsedDate(dToFrom + "," + dToTo).getRandomDate());
		return m;
	}
	// DateAndTime

	@UserFunction(name = "fkr.dateAndTime")
	@Description("This will generate a date and seperate time property" )
	public Map<String,Object> generateFromAndDate(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
		Map<String,Object> m = new HashMap<>();
		Date d = DDGFunctions.getInstance( db ).getValuegen().getParsedDate( dFrom + "," + dTo).getRandomDateObject();
		String s = DDGFunctions.getInstance( db).getSdf().format(d);
		m.put("date",s);
		m.put("time", d.getTime());
		return m;
	}

	// DateAndTimeNowR
	@UserFunction(name = "fkr.dateAndTimeNowR")
	@Description("This will generate a random date between 'now' minus a minutes range" )
	public Map<String,Object> generateDateAndTimeNowR(final @Name ("minutesRange") Long minutes) throws Exception {
		Map<String,Object> m = new HashMap<>();

		long cur = System.currentTimeMillis();
		// the amount of minutes
		Random r = new Random();
		int cor = r.nextInt(minutes.intValue() * 60000);
		Date d = new Date();
		d.setTime(cur - cor);
		m.put("date",DDGFunctions.getInstance( db).getSdf().format(d));
		m.put("time",d.getTime());

		return m;
	}


	// Person
	@UserFunction(name = "fkr.person")
	@Description("generates a Person with firstname, lastname, fullName, birthdate and ssn")
	public Map<String,Object> generatePerson(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
		Map<String,Object> m = new HashMap<>();
		String firstName = DDGFunctions.getInstance( db).getNames().firstName();
		String lastName = DDGFunctions.getInstance( db).getNames().lastName();
		String fullName = firstName + " " + lastName;
		String userid = firstName.toLowerCase() + lastName.toLowerCase().substring(0,2);
		String mail = DDGFunctions.getInstance( db).getFaker().internet().emailAddress();
		mail = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + mail.substring(mail.indexOf("@")+1);
		m.put("firstName", firstName);
		m.put("lastName", lastName);
		m.put("fullName", fullName);
		m.put("birthDate", DDGFunctions.getInstance( db).getValuegen().getParsedDate( dFrom + "," + dTo).getRandomDateString());
		m.put("ssn", DDGFunctions.getInstance( db).getValuegen().getCode("__#######_#"));

		return m;
	}



	@UserFunction(name = "fkr.firstName")
	@Description("generates a firstname")
	public String generateFirstName() throws Exception {
		String n;
		try {
			n = DDGFunctions.getInstance( db).getNames().firstName();
		} catch (Throwable ee) {
			ee.printStackTrace();
			throw new Exception(ee);
		}
		return n;
	}

	@UserFunction(name = "fkr.lastName")
	@Description("generate a lastname")
	public String generateLastName() throws Exception {
		return DDGFunctions.getInstance( db).getNames().lastName();
	}

	@UserFunction(name = "fkr.email")
	@Description("generate email address")
	public String generateMail() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().internet().emailAddress();
	}

	// fullname
	@UserFunction(name = "fkr.fullName")
	@Description("generate firstname and lastname")
	public String generatefullName() throws Exception {
		return DDGFunctions.getInstance( db).getNames().fullName();
	}

	// companyname
	//
	@UserFunction(name = "fkr.company")
	@Description("generate company name")
	public String generateCompany() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().company().name().replaceAll("'","-");
	}


//	case "companybs" :
	@UserFunction(name = "fkr.companyBusiness")
	@Description("generate company business")
	public String generateCompanyBs() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().company().bs().replaceAll("'","-");
	}


//			case "companyslogan" :
	@UserFunction(name = "fkr.companyCatchPhrase")
	@Description("generate company catch phrase")
	public String generateCompanySlogan() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().company().catchPhrase().replaceAll("'","-");
	}


//			case "isbn10" :
	@UserFunction(name = "fkr.isbn10")
	@Description("generate isbn10 code")
	public String generateisbn10() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().code().isbn10();
	}
//			case "isbn13" :
	@UserFunction(name = "fkr.isbn13")
	@Description("generate isbn13 code")
	public String generateisbn13() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().code().isbn13();
	}

//			case "url" :

	@UserFunction(name = "fkr.url")
	@Description("generate url ")
	public String generateUrl() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().internet().url();
	}

//			case "zipcode" :
	@UserFunction(name = "fkr.zipcode")
	@Description("generate zip code ")
	public String generateZipCode() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().zipCode();
	}
//			case "country" :

	@UserFunction(name = "fkr.country")
	@Description("generate country ")
	public String generateCountry() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().country();
	}

//			case "phonenumber" :

	@UserFunction(name = "fkr.phoneNumber")
	@Description("generate a phone number")
	public String generatePhoneNumber() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().phoneNumber().phoneNumber();
	}

//			case "timezone" :
	@UserFunction(name = "fkr.timezone")
	@Description("generate a timezone ")
	public String generateTimeZone() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().timeZone();
	}

//			case "longitude" :
	@UserFunction(name = "fkr.longitude")
	@Description("generate longitude ")
	public String generateLongitude() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().longitude();
	}

	//			case "latitude" :

	@UserFunction(name = "fkr.latitude")
	@Description("generate longitude ")
	public String generateLatitude() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().latitude();
	}

//			case "streetaddress" :

	@UserFunction(name = "fkr.streetAddress")
	@Description("generate address ")
	public String generateStreetAddress() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().streetAddress(false).replaceAll("'","-");
	}

//			case "streetaddressfull" :

	@UserFunction(name = "fkr.streetAddressFull")
	@Description("generate full address ")
	public String generateStreetAddressFull() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().streetAddress(true).replaceAll("'","-");
	}

//			case "secundaryaddress" :

	@UserFunction(name = "fkr.secundaryAddress")
	@Description("generate secundary address ")
	public String generateStreetSecundaryAddress() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().secondaryAddress().replaceAll("'","-");
	}

//			case "streetname" :
	@UserFunction(name = "fkr.streetName")
	@Description("generate street name")
	public String generateStreetName() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().streetName().replaceAll("'","-");
	}

//			case "streetsuffix" :

	@UserFunction(name = "fkr.streetSuffix")
	@Description("generate street suffix ")
	public String generateStreetSuffix() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().streetSuffix();
	}
//			case "streetaddressnumber" :

	@UserFunction(name = "fkr.streetAddressNumber")
	@Description("generate street address number ")
	public String generateStreetAddressNumber() throws Exception {
		return DDGFunctions.getInstance( db).getFaker().address().streetAddressNumber();
	}

	//			case "stringElementfrom" :
	@UserFunction(name = "fkr.stringElement")
	@Description("generate an element from comma separated list of strings \"'a','b','c'\" ")
	public String elementFrom(final @Name("values") String values) throws Exception {
		DDGFunctions.getInstance( db).getValuegen().registerStringElements(values);
		return DDGFunctions.getInstance( db).getValuegen().randomStringElement(values);
	}

	//			case "numberElementfrom" :
	@UserFunction(name = "fkr.doubleElement")
	@Description("generate a double element from comma separated list of numbers '1,2,3' ")
	public Double elementFromDouble(final @Name("values") String values) throws Exception {
		DDGFunctions.getInstance( db).getValuegen().registerNumberElements(values);
		return DDGFunctions.getInstance( db).getValuegen().randomDoubleElement(values);
	}

//			case "nextStringelementfrom" :
	@UserFunction(name = "fkr.nextStringElement")
	@Description("get the next element from comma separated list of strings 'a,b,c' ")
	public String nextElementFrom(final @Name("values") String values) throws Exception {
        DDGFunctions.getInstance( db).getValuegen().registerStringElements(values);
        String r = "";
        try {
            r = DDGFunctions.getInstance(db).getValuegen().nextStringElement(values);
        } catch (Throwable t) {
            t.printStackTrace();
            r = t.getMessage();
        }
		return r;
	}

//          case "nextDoubleElementFrom
	@UserFunction(name = "fkr.nextDoubleElement")
	@Description("generate a double element from comma separated list of numbers '1.1,2.3,0.3' ")
	public Double nextElementFromDouble(final @Name("values") String values) throws Exception {
        DDGFunctions.getInstance( db).getValuegen().registerNumberElements(values);
		return DDGFunctions.getInstance( db).getValuegen().nextDoubleElement(values);
	}

    //			case "nextelementfromlong" :

    @UserFunction(name = "fkr.nextLongElement")
    @Description("generate an element from comma separated list of longs '1,2,3' ")
    public Long nextElementFromLong(final @Name("values") String values) throws Exception {
        return DDGFunctions.getInstance( db).getValuegen().nextLongElement(values);
    }

	@UserFunction(name = "fkr.longElement")
	@Description("generate an long element from comma separated list of longs '1,2,3' ")
	public Long longElement(final @Name("values") String values) throws Exception {
		Long l = null;
		try {
			l = DDGFunctions.getInstance(db).getValuegen().randomLongElement(values);
		} catch (Exception ee) {
			ee.printStackTrace();
			throw ee;
		}
		return l ;
	}


//			case "StringElementsfrom" :
    @UserFunction(name = "fkr.stringElementsFrom")
    @Description("generate a String array from semicolon separated list of String arrays 'a','b','c';'d','e','f' ")
    public List<String> stringElementsFrom(final @Name("values") String values) throws Exception {
        if (DDGFunctions.getInstance( db).getValuegen().elementsFromIsString(values)) {
            List<String> l = new ArrayList<>();
            for (String s:DDGFunctions.getInstance( db).getValuegen().randomStringElements(values) ) {
                l.add(s);
            }
    		return l;
	    }
        return null;
    }


    @UserFunction(name = "fkr.doubleElementsFrom")
    @Description("generate an double array from semicolon separated list of double arrays 1.1,2,0.3;3,8.0,23 ")
    public List<Double> doubleElementsFrom(final @Name("values") String values) throws Exception {
        if (DDGFunctions.getInstance( db).getValuegen().elementsFromIsDouble(values)) {
            List<Double> l = new ArrayList<>();
            for (Double s:DDGFunctions.getInstance( db).getValuegen().randomDoubleElements(values) ) {
                l.add(s);
            }
            return l;
        }
        return null;
    }

//			case "randomdatestring" :
    @UserFunction(name = "fkr.dateString")
    @Description("generates a date string in the given period parameter format is yyyy-MM-dd")
    public String randomDateString(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
       return DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFrom + "," + dTo).getRandomDateString();
    }


//			case "randomdate" :
    @UserFunction(name = "fkr.date")
    @Description("generates a date 'long' (yyyyMMdd) in the given period parameter format is yyyy-MM-dd")
    public Long randomDate(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
	    return  (long) DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFrom + "," + dTo).getRandomDate();
    }
//			case "randomdatetime" :
    @UserFunction(name = "fkr.getTimestamp")
    @Description("generates a epoch date timestamp in the given period parameter format is yyyy-MM-dd")
    public Long randomTimestamp(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
		long ret;
		try {
			ret = (long) DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFrom + "," + dTo).getRandomDateTime();
		} catch (Throwable tt) {
			tt.printStackTrace();
			throw new Exception(tt);
		}
        return  ret ;
    }
	@UserFunction(name = "fkr.getDateTime")
	@Description("generates a datetime object in the given period parameter format is yyyy-MM-dd")
	public LocalDateTime randomDateTimeObject(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
		LocalDateTime ldt;
		try {
			long ret = (long) DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFrom + "," + dTo).getRandomDateTime();
			ldt = Instant.ofEpochMilli(ret).atZone(ZoneId.of("UTC")).toLocalDateTime();
		} catch (Throwable tt) {
			tt.printStackTrace();
			throw new Exception(tt);
		}
		return  ldt ;
	}
	@UserFunction(name = "fkr.getDate")
	@Description("generates a date object in the given period parameter format is yyyy-MM-dd")
	public LocalDate randomDateObject(final @Name ("dateFrom") String dFrom, final @Name("dateTo") String dTo) throws Exception {
		LocalDate ldt;
		try {
			long ret = (long) DDGFunctions.getInstance( db).getValuegen().getParsedDate(dFrom + "," + dTo).getRandomDateTime();
			ldt = Instant.ofEpochMilli(ret).atZone(ZoneId.of("UTC")).toLocalDate();
		} catch (Throwable tt) {
			tt.printStackTrace();
			throw new Exception(tt);
		}
		return  ldt ;
	}


	//			case "year" :
    @UserFunction(name = "fkr.year")
    @Description("generates year value given period parameters")
    public Long randomYear(final @Name ("yearFrom") Long dFrom, final @Name("yearTo") Long dTo) throws Exception {
        return  (long) DDGFunctions.getInstance( db).getValuegen().getRandomYear(dFrom + "," + dTo);
    }
//			case "today" :
    @UserFunction(name = "fkr.today")
    @Description("get the current date:yyyy-MM-dd")
    public String getToday() throws Exception {
        return  DDGFunctions.getInstance( db).getValuegen().getToday();
    }
//			case "listfile" :
    @UserFunction(name = "fkr.stringFromFile")
    @Description("get a string value from a provided file. File must be in the plugins/ddgres folder")
    public String stringFromFile(final @Name ("fileName") String listFile) throws Exception {
        return  DDGFunctions.getInstance( db).getValuegen().getFileValue( listFile + "," + "string").toString();
    }

    @UserFunction(name = "fkr.longFromFile")
    @Description("get a long value from a provided file. File must be in the plugins/ddgres folder")
    public Long longFromFile(final @Name ("fileName") String listFile) throws Exception {
        return  (Long) DDGFunctions.getInstance( db).getValuegen().getFileValue( listFile + ",long" );
    }

    @UserFunction(name = "fkr.doubleFromFile")
    @Description("get double value from a provided file. File must be in the plugins/ddgres folder")
    public Double doubleFromFile(final @Name ("fileName") String listFile) throws Exception {
        return  (Double) DDGFunctions.getInstance( db).getValuegen().getFileValue(listFile + ",double" );
    }

//			case "listfilenext" :
    @UserFunction(name = "fkr.nextStringFromFile")
    @Description("get the next string value from a provided file (per identifier). File must be in the plugins/ddgres folder.")
    public String nextStringFromFile(final @Name ("fileName") String listFile, final @Name("identifier") String ident) throws Exception {
        return  DDGFunctions.getInstance( db).getValuegen().getNextFileValue( listFile + ",string," + ident).toString();
    }

    @UserFunction(name = "fkr.nextLongFromFile")
    @Description("get the next long value from a provided file (per identifier). File must be in the plugins/ddgres folder")
    public Long nextLongFromFile(final @Name ("fileName") String listFile, final @Name("identifier") String ident) throws Exception {
        return  (Long) DDGFunctions.getInstance( db).getValuegen().getNextFileValue(listFile + ",long," + ident);
    }

    @UserFunction(name = "fkr.nextDoubleFromFile")
    @Description("get next double value from a provided file (per identifier). File must be in the plugins/ddgres folder")
    public Double nextDoubleFromFile(final @Name ("fileName") String listFile, final @Name("identifier") String ident) throws Exception {
        return  (Double) DDGFunctions.getInstance( db).getValuegen().getNextFileValue(listFile + ",double," + ident);
    }

//			case "number" :
    @UserFunction(name = "fkr.numberFormatted")
    @Description("get a random number value within the provided range as a formatted string")
    public String rNumber(final @Name ("from") long from, final @Name("to") long to, final @Name("numberFormat") String format) throws Exception {
        return  (String) DDGFunctions.getInstance( db).getValuegen().getNumberValue(from + "," + to + "," + format);
    }
    @UserFunction(name = "fkr.number")
    @Description("get a random number value within the provided range ")
    public Double rNumber(final @Name ("from") long from, final @Name("to") long to) throws Exception {
        return  (Double) DDGFunctions.getInstance( db).getValuegen().getNumberValue(from + "," + to );
    }
//			case "numberRounded" :
    @UserFunction(name = "fkr.numberRounded")
    @Description("get a random number value within the provided range with a given number of decimals")
    public Double rNumber(final @Name ("from") long from, final @Name("to") long to, final @Name("decimals") long decmls) throws Exception {
        return  (Double) DDGFunctions.getInstance( db).getValuegen().getNumberValueR(from + "," + to + "," + decmls );
    }


//			case "code" :
    @UserFunction(name = "fkr.code")
    @Description("generate a code based on the mask given: '#' gives a random number '_' will generate a random letter")
    public String genCode(final @Name ("mask") String mask) throws Exception {
        return  (String) DDGFunctions.getInstance( db).getValuegen().getCode(mask);
    }
//			case "ip" :
    @UserFunction(name = "fkr.ip")
    @Description("generate a ip number based on the mask given: '192.168.56.##' for every '#' a number between 0 and 10 will be generated")
    public String genIP(final @Name ("mask") String mask) throws Exception {
        return  (String) DDGFunctions.getInstance( db).getValuegen().getCode(mask);
    }
    //			case "sequence" :
    @UserFunction(name = "fkr.sequence")
    @Description("generate a sequence number for the given 'sequence' name")
    public Long seqNo(final @Name ("sequenceName") String sequenceName) throws Exception {
        return   DDGFunctions.getInstance( db).getValuegen().getSeq(sequenceName);
    }

    //			case "codeseq" :
    @UserFunction(name = "fkr.codeSequence")
    @Description("generate a code based on the code mask given appended with a sequence number")
    public String genCodeSeq(final @Name ("mask") String mask) throws Exception {
        return  (String) DDGFunctions.getInstance( db).getValuegen().getCode(mask) + DDGFunctions.getInstance( db).getValuegen().getSeq(mask);
    }

    @UserFunction(name = "fkr.long")
    @Description("get a random long number value within the provided range ")
    public Long lNumber(final @Name ("from") long from, final @Name("to") long to) throws Exception {
        return  (long) DDGFunctions.getInstance( db).getValuegen().getLong(from + "," + to );
    }

//			case "percentage" :
    @UserFunction(name = "fkr.percentage")
    @Description("get a random number percentage between 0 and 101")
    public Long percent() throws Exception {
        return  (long) DDGFunctions.getInstance( db).getValuegen().getPercentage();
    }

    @UserFunction(name = "fkr.boolean")
    @Description("generat boolean value, give the % true as a parameter")
    public Boolean bool(final @Name("percentageTrue") Long prctrue) throws Exception {
        return DDGFunctions.getInstance( db).getValuegen().getBoolean("" +prctrue);
    }
//			case "txtwords" :

    @UserFunction(name = "fkr.txtWords")
    @Description("generate a word array for a given length")
    public List<String> txtwords(final @Name("wordAmount") Long amountOfWords) throws Exception {
        return DDGFunctions.getInstance( db).getFaker().lorem().words(amountOfWords.intValue());
    }

    //			case "txttext" :

    @UserFunction(name = "fkr.txtText")
    @Description("generate text with the given amount of characters")
    public String txttxt(final @Name("characterAmount") Long amountOfChars) throws Exception {
        return DDGFunctions.getInstance( db).getFaker().lorem().fixedString(amountOfChars.intValue());
    }


//			case "txtsentence" :
    @UserFunction(name = "fkr.txtSentence")
    @Description("generate sentence")
    public String txtSentence() throws Exception {
        return DDGFunctions.getInstance( db).getFaker().lorem().sentence();
    }


//			case "txtsentences" :
    @UserFunction(name = "fkr.txtSentences")
    @Description("generate(s) [sentenceCount] sentences")
    public List<String> txtSentences(final @Name("sentenceCount") Long sentencecCount) throws Exception {
        return DDGFunctions.getInstance( db).getFaker().lorem().sentences(sentencecCount.intValue());
    }


//			case "txtparagraph" :
    @UserFunction(name = "fkr.txtParagraph")
    @Description("generate a paragraph with the given amount of sentences")
    public String txtParagraph(final @Name("paragraphCount") Long amountOfPar) throws Exception {
        return DDGFunctions.getInstance( db).getFaker().lorem().paragraph(amountOfPar.intValue());
    }


//			case "txtparagraphs" :
    @UserFunction(name = "fkr.txtParagraphs")
    @Description("generate a [paragraphCount] of paragraphs with ~ 4 sentences each")
    public List<String> txtParagraphs(final @Name("paragraphCount") Long parCount) throws Exception {
        return DDGFunctions.getInstance( db).getFaker().lorem().paragraphs(parCount.intValue());
    }



	@Procedure(name = "fkr.createRelations", mode = Mode.WRITE)
	@Description("Create Relationships between a list of start nodes and a list of end nodes. cardinality can be '1-n' (The end node may have max 1 relation from the start node), 'n-1' (The start node may have max one relation to the end node) or '1-1' (The start and end node may have max one relationship of this type).")
	public Stream<RelationshipResult> createRelations(@Name("startNodes") final List<Object> startNodes
			, @Name("relationshipType") final String relType
			, @Name("endNodes") final List<Object> endNodes
			, @Name("cardinality") final String cardinality) throws Exception {

		List<RelationshipResult> res = new ArrayList<>();
		res.add(new RelationshipResult(privateCreateRelations(startNodes,relType,endNodes,cardinality)));
		return res.stream();
	}

	private List<Relationship> privateCreateRelations(final List<Object> startNodes
			, final String relType
			, final List<Object> endNodes
			, final String cardinality) throws Exception {

		try {
			if (relType == null || relType.isEmpty()) {
				throw new Exception("RelationshipType is missing!");
			}
			if (cardinality == null || cardinality.isEmpty()) {
				throw new Exception("cardinality is missing!");
			}
			List<Relationship> res = new LinkedList<>();

			if (startNodes == null || startNodes.size() == 0 || endNodes == null || endNodes.size() == 0) {
				return res;
			}
			// check on startNodes
			if (!validNodes(startNodes.get(0), endNodes.get(0))) {
				throw new Exception("The lists of nodes must contain a node id (Long) or a Node object.");
			}
			if (cardinality.equals(ONE_TO_MANY)) {
				// The "end node" may have max one relation from the start node
				// we loop here through the end nodes and every end node may get a relation to the start node
				// for determining the start node we use a randomizer.
				Random random = new Random();
				for (Object endNode : endNodes) {
					Object startNode = startNodes.get(random.nextInt(startNodes.size()));
					res.add( createRelation(startNode, relType, endNode));
				}
				return res;

			} else if (cardinality.equals(MANY_TO_ONE)) {

				// The "start node" may have max one relation to the end node
				// we loop here through the start nodes and pick a random end node to connect to
				Random random = new Random();
				for (Object startNode: startNodes) {
					Object endNode = endNodes.get(random.nextInt(endNodes.size()));
					res.add( createRelation(startNode, relType, endNode));
				}
				return res;
			} else if (cardinality.equals(ONE_TO_ONE)) {

				if (startNodes.equals(endNodes)) {
					// self reference we have to create now an extra list
					List<Node> tmpList = new ArrayList<>();
					Collections.copy(endNodes, tmpList);
					Collections.shuffle(tmpList);
					int index = 0;
					for (Object startNode : startNodes) {
						Object endNode = endNodes.get(index);
						index++;
						res.add(createRelation(startNode, relType, endNode));
					}
					return res;
				}
				if (startNodes.size() <= endNodes.size()) {

					int index = 0;
					for (Object startNode : startNodes) {
						Object endNode = endNodes.get(index);
						index++;
						res.add(createRelation(startNode, relType, endNode));
					}
					return res;
				} else {

					int index = 0;
					for (Object endNode : endNodes) {
						Object startNode = startNodes.get(index);
						index++;
						res.add(createRelation(startNode, relType, endNode));
					}
					return res;
				}
			} else {
				// invalid cardinality
				throw new Exception("Invalid cardinality, Only '1-n','n-1' and '1-1' are allowed");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

	}


	private Relationship createRelation(Object start, String type, Object end) {
		Node nStart = getNode(start);
		Node nEnd = getNode(end);
		return nStart.createRelationshipTo(nEnd, DynRel.get(type));
	}

	private Node getNode(Object nob) {
		if (nob instanceof Number) {
            Transaction tx = db.beginTx();
            Node rn = tx.getNodeById(((Number) nob).longValue());
            tx.close();
			return rn;
		} else {
			return (Node) nob;
		}
	}
	private boolean validNodes(Object startObject, Object endObject) {
		return (startObject instanceof Number || startObject instanceof Node) && (endObject instanceof Number || endObject instanceof Node);
	}

}
