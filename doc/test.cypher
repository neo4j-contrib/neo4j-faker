// run this first
:get /testdata/tdg/pfile/example1.props?dbname=neo4j


// testscript cypher

RETURN fkr.user('1900-01-01','1980-01-01','example.com');
RETURN fkr.fromToDateTime('1977-01-01','1980-01-01','1990-01-01','1999-01-01');
RETURN fkr.fromToDate('1977-01-01','1980-01-01','1990-01-01','1999-01-01');
RETURN fkr.dateAndTime('1977-01-01','1980-01-01');
RETURN fkr.dateAndTimeNowR(100);
RETURN fkr.person('1977-01-01','1980-01-01');
RETURN fkr.firstName();
RETURN fkr.lastName();
RETURN fkr.email();
RETURN fkr.fullName();
RETURN fkr.company();
RETURN fkr.companyBusiness();
RETURN fkr.companyCatchPhrase();
RETURN fkr.isbn10();
RETURN fkr.isbn13();
RETURN fkr.url();
RETURN fkr.zipcode();
RETURN fkr.country();
RETURN fkr.phoneNumber();
RETURN fkr.timezone();
RETURN fkr.longitude();
RETURN fkr.latitude();
RETURN fkr.streetAddress();
RETURN fkr.streetAddressFull();
RETURN fkr.secundaryAddress();
RETURN fkr.streetName();
RETURN fkr.streetSuffix();
RETURN fkr.streetAddressNumber();
RETURN fkr.stringElement("a,b,b,b,s,s,s,s,f,f,f");
RETURN fkr.doubleElement("0.1,0.5,4.8,0.5");
RETURN fkr.nextStringElement("a,b,c,d,e,f,g");
RETURN fkr.nextDoubleElement("0.1,0.5,4.8,0.5");
RETURN fkr.nextLongElement("1,5,8,10");
RETURN fkr.longElement("1,5,8,10");
RETURN fkr.stringElementsFrom("'a','b','c';'1','2','3'");
RETURN fkr.doubleElementsFrom("1.1,2,0.3;3,8.0,23");
RETURN fkr.dateString('1977-01-01','1980-01-01');
RETURN fkr.date('1977-01-01','1980-01-01');
RETURN fkr.getTimestamp('1977-01-01','1980-01-01');
RETURN fkr.getDateTime('1977-01-01','1980-01-01');
RETURN fkr.getDate('1977-01-01','1980-01-01');
RETURN fkr.year(1200,2000);
RETURN fkr.today();
RETURN fkr.stringFromFile("skills.txt");
RETURN fkr.longFromFile("longs.txt");
RETURN fkr.doubleFromFile("doubles.txt");
RETURN fkr.nextStringFromFile("skills.txt","1");
RETURN fkr.nextLongFromFile("longs.txt","aa");
RETURN fkr.nextDoubleFromFile("doubles.txt","b");
RETURN fkr.numberFormatted(100,200,'0.00');
RETURN fkr.number(100,200);
RETURN fkr.numberRounded(100,200,3);
RETURN fkr.code("ABS##BN__ fiets");
RETURN fkr.ip("192.168.56.##");
RETURN fkr.sequence("mysquence");
RETURN fkr.codeSequence("HNMS_");
RETURN fkr.long(1,2000);
RETURN fkr.percentage();
RETURN fkr.boolean(40);
RETURN fkr.txtWords(20);
RETURN fkr.txtText(100);
RETURN fkr.txtSentence();
RETURN fkr.txtSentences(2);
RETURN fkr.txtParagraph(4);
RETURN fkr.txtParagraphs(4);
RETURN fkr.txtParagraph(4);
RETURN fkr.txtParagraph(4);

// create relations check
MATCH (p:Person) WITH p limit 10
WITH collect(p) as persons
MATCH (c:City) WITH persons, c limit 5
WITH persons, collect(c) as cities
CALL fkr.createRelations(persons,'MOVES_TO',cities,"1-n") yield relationships return count(*);

MATCH (p:Person) WITH p SKIP 50 limit 10
WITH collect(p) as persons
MATCH (c:City) WITH persons, c limit 5
WITH persons, collect(c) as cities
CALL fkr.createRelations(persons,'MOVES_TO',cities,"n-1") yield relationships return count(*);

MATCH (p:Person) WITH p SKIP 50 limit 10
WITH collect(p) as persons
MATCH (p2:Person) WITH persons, p2 SKIP 150 limit 10
WITH persons, collect(p2) as persons2
CALL fkr.createRelations(persons,'KNOWS',persons2,"1-1") yield relationships return count(*);