# neo4j-faker

With this tool you can generate fake Demo or Test data into the neo4j database via **Cypher Functions** (neo4j 3.1+) and or by using the **Data Generator**. You can see it as a Swiss Army knife to build a fake data set.

The tool is inspired by GraphGen and the faker libraries (java faker), However to make it possible to generate larger datasets the person name generator (firstname, lastname and fullname) is implemented in a different way.

From neo4j-faker 0.9.1 and higher the Neo4j database version should at least be 4.x and higher.

### Note: The Cypher Functions and Data Denerator scripts are meant/build to run in a single thread!!

## Getting started

- Download the latest <a href="../../releases">release</a> from github
- Unzip the distribution zip file into a directory
- Copy all the contents of the dist directory to the neo4j server plugin directory

  - Faker Cypher Functions

    - Add the following line in the noo4j.conf file to allow access to the faker functions.

      <pre class="code">dbms.security.procedures.unrestricted=fkr.*</pre>

  - Data Generator Only

    - Register the Data Generator Unmanaged Plugin on the neo4j server
    - You have to add the following line to your neo4j.conf file

      <pre class="code">dbms.unmanaged_extension_classes=org.neo4j.faker.ump=/testdata</pre>

    - For convenience you can disable the authentication. The curl examples in this documentation are without authentication.</p>
      <pre class="code">dbms.security.authorization_enabled=false</pre>

- (re) Start the neo4j server

  - Data Generator Only

    - Test the plugin
    - Test the availability of the plugin by calling it with a non existing property file name from you local browser</p>

      ```
      http://127.0.0.1:7474/testdata/tdg/pfile/test.props
      ```

      Since version 0.9.1 (Neo4j version 4 and higher) you can add the target database as an URL parameter:

      ```
      http://127.0.0.1:7474/testdata/tdg/pfile/test.props?dbname=mydb
      ```

      The browser will now give an error message stating that the file test.props cannot be found.</p>

### Data Generator Help

The complete help with detailed instructions and examples on how to create the property files with the data definitions for the data loader can be found in the doc directory (doc/index.html).

## Cypher Faker Functions and Procedures

You can call dbms.function() in the browser to see the available fkr. functions. There is one procedure to create relationships: fkr.createRelations.

examples:

```$xslt
// generating 1000 Person nodes
foreach (i in range(0,1000) |
    create (p:Person { uid : i })
    set p += fkr.person('1960-01-01','2000-01-01')
)
```

```
// generating 500 CreditCards
foreach (a in range(0,500) |
    create (c:CreditCard {uid : fkr.code('Demo #### #### #### ####')})
    set c.limit = fkr.longElement('5000,5000,5000,1000,1000,1000,1000,10000')
    set c.balance = fkr.number(10,1000)
)
```

```$xslt
//
// generating a Person City structure with relationships
//
create index on :City(name);


foreach (i in range(0,100000) |
    create (p:Person:Proc { uid : i })
    set p += fkr.person('1960-01-01','2000-01-01')
)
;

foreach (ci in range(0,40) |
    merge (cit:City:Proc { name : fkr.stringFromFile("cities.txt") })
)
;

match (c:City:Proc) remove c:Proc with collect(c) as cities
match (p:Person:Proc) remove p:Proc with cities, collect(p) as persons
with cities
,    persons
call fkr.createRelations(persons, "LIVES_IN" , cities, "n-1") yield relationships as livesRelations
call fkr.createRelations(persons, "IS_MARE" , cities , "1-n") yield relationships as mareRelations
call fkr.createRelations(cities, "HAS_POLICE_CHIEF" , persons, "1-1") yield relationships as chiefRelations
foreach ( rel in livesRelations |
set rel.likes = fkr.long(0,100)
)
return size(cities), size(persons), size(livesRelations), size(mareRelations), size(chiefRelations)
;

```

In this example we use a temporary extra Label 'Proc' to match only the nodes just created for creating relationships.

### Procedures

| name                | signature                                                                                                                                                                     | description                                                                                                                                                                                                                                                                                                                                                                              |
| ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| fkr.createRelations | fkr.createRelations(startNodes :: LIST? OF ANY?, relationshipType :: STRING?, endNodes :: LIST? OF ANY?, cardinality :: STRING?) :: (relationships :: LIST? OF RELATIONSHIP?) | Create Relationships between a list of start nodes and a list of end nodes. cardinality can be '1-n' (The end node may have max 1 relation from the start node), 'n-1' (The start node may have max one relation to the end node) or '1-1' (The start and end node may have max one relationship of this type). Note that the procure returns one row with a list of relationships in it |

### Functions

#### Special Functions (generating multiple property values)

| name                | signature                                                                                                                | description                                                                                       |
| ------------------- | ------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------- |
| fkr.user            | fkr.user(dateFrom :: STRING?, dateTo :: STRING?, companyDomain :: STRING?) :: (MAP?)                                     | generates a system User with firstname, lastname, userId, mail and startdate                      |
| fkr.person          | fkr.person(dateFrom :: STRING?, dateTo :: STRING?) :: (MAP?)                                                             | generates a Person with firstname, lastname, fullName, birthdate and ssn                          |
| fkr.fromToDate      | fkr.fromToDate(dateFromFrom :: STRING?, dateFromTo :: STRING?, dateToFrom :: STRING?, dateToTo :: STRING?) :: (MAP?)     | This will generate a from and a to date property (number value generated with date YYYYMMDD)      |
| fkr.fromToDateTime  | fkr.fromToDateTime(dateFromFrom :: STRING?, dateFromTo :: STRING?, dateToFrom :: STRING?, dateToTo :: STRING?) :: (MAP?) | This will generate a from and a to timestamp property (number value generated with date YYYYMMDD) |
| fkr.dateAndTime     | fkr.dateAndTime(dateFrom :: STRING?, dateTo :: STRING?) :: (MAP?)                                                        | This will generate a date and seperate time property                                              |
| fkr.dateAndTimeNowR | fkr.dateAndTimeNowR(minutesRange :: INTEGER?) :: (MAP?)                                                                  | This will generate a random date between 'now' minus a minutes range                              |

#### Person Functions

| name          | signature                    | description                     |
| ------------- | ---------------------------- | ------------------------------- |
| fkr.firstName | fkr.firstName() :: (STRING?) | generates a firstname           |
| fkr.lastName  | fkr.lastName() :: (STRING?)  | generate a lastname             |
| fkr.fullName  | fkr.fullName() :: (STRING?)  | generate firstname and lastname |

#### Company Functions

| name                   | signature                             | description                   |
| ---------------------- | ------------------------------------- | ----------------------------- |
| fkr.company            | fkr.company() :: (STRING?)            | generate company name         |
| fkr.companyBusiness    | fkr.companyBusiness() :: (STRING?)    | generate company business     |
| fkr.companyCatchPhrase | fkr.companyCatchPhrase() :: (STRING?) | generate company catch phrase |

#### Internet Functions

| name      | signature                            | description                                                                                                             |
| --------- | ------------------------------------ | ----------------------------------------------------------------------------------------------------------------------- |
| fkr.ip    | fkr.ip(mask :: STRING?) :: (STRING?) | generate a ip number based on the mask given: '192.168.56.##' for every '#' a number between 0 and 10 will be generated |
| fkr.email | fkr.email() :: (STRING?)             | generate email address                                                                                                  |
| fkr.url   | fkr.url() :: (STRING?)               | generate url                                                                                                            |

#### Location Functions

| name                    | signature                              | description                    |
| ----------------------- | -------------------------------------- | ------------------------------ |
| fkr.streetAddress       | fkr.streetAddress() :: (STRING?)       | generate address               |
| fkr.streetAddressFull   | fkr.streetAddressFull() :: (STRING?)   | generate full address          |
| fkr.streetAddressNumber | fkr.streetAddressNumber() :: (STRING?) | generate street address number |
| fkr.streetName          | fkr.streetName() :: (STRING?)          | generate street name           |
| fkr.streetSuffix        | fkr.streetSuffix() :: (STRING?)        | generate street suffix         |
| fkr.secundaryAddress    | fkr.secundaryAddress() :: (STRING?)    | generate secundary address     |
| fkr.country             | fkr.country() :: (STRING?)             | generate country               |
| fkr.timezone            | fkr.timezone() :: (STRING?)            | generate a timezone            |
| fkr.zipcode             | fkr.zipcode() :: (STRING?)             | generate zip code              |
| fkr.longitude           | fkr.longitude() :: (STRING?)           | generate longitude             |
| fkr.latitude            | fkr.latitude() :: (STRING?)            | generate latitude              |

#### Other Functions

| name            | signature                      | description             |
| --------------- | ------------------------------ | ----------------------- |
| fkr.isbn10      | fkr.isbn10() :: (STRING?)      | generate isbn10 code    |
| fkr.isbn13      | fkr.isbn13() :: (STRING?)      | generate isbn13 code    |
| fkr.phoneNumber | fkr.phoneNumber() :: (STRING?) | generate a phone number |

#### Sequence Array and List Functions

| name                   | signature                                                                       | description                                                                                                 |
| ---------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| fkr.sequence           | fkr.sequence(sequenceName :: STRING?) :: (INTEGER?)                             | generate a sequence number for the given 'sequence' name                                                    |
| fkr.stringElement      | fkr.stringElement(values :: STRING?) :: (STRING?)                               | generate an element from comma separated list of strings \                                                  | 'a','b','c'\  |  |
| fkr.stringElementsFrom | fkr.stringElementsFrom(values :: STRING?) :: (LIST? OF ANY?)                    | generate an String array from semicolon separated list of String arrays 'a','b','c';'d','e','f'             |
| fkr.stringFromFile     | fkr.stringFromFile(fileName :: STRING?) :: (STRING?)                            | get a string value from a provided file. File must be in the plugins/ddgres folder                          |
| fkr.nextStringElement  | fkr.nextStringElement(values :: STRING?) :: (STRING?)                           | get the next element from comma separated list of strings 'a,b,c'                                           |
| fkr.nextStringFromFile | fkr.nextStringFromFile(fileName :: STRING?, identifier :: STRING?) :: (STRING?) | get the next string value from a provided file (per identifier). File must be in the plugins/ddgres folder. |
| fkr.doubleElement      | fkr.doubleElement(values :: STRING?) :: (FLOAT?)                                | generate a double element from comma separated list of numbers '1,2,3'                                      |
| fkr.doubleFromFile     | fkr.doubleFromFile(fileName :: STRING?) :: (FLOAT?)                             | get double value from a provided file. File must be in the plugins/ddgres folder                            |
| fkr.doubleElementsFrom | fkr.doubleElementsFrom(values :: STRING?) :: (LIST? OF ANY?)                    | generate an double array from semicolon separated list of double arrays 1.1,2,0.3;3,8.0,23                  |
| fkr.nextDoubleElement  | fkr.nextDoubleElement(values :: STRING?) :: (FLOAT?)                            | generate a double element from comma separated list of numbers '1.1,2.3,0.3'                                |
| fkr.nextDoubleFromFile | fkr.nextDoubleFromFile(fileName :: STRING?, identifier :: STRING?) :: (FLOAT?)  | get next double value from a provided file (per identifier). File must be in the plugins/ddgres folder      |
| fkr.longElement        | fkr.longElement(values :: STRING?) :: (INTEGER?)                                | generate a long element from comma separated list of longs '1,2,3'                                          |
| fkr.longFromFile       | fkr.longFromFile(fileName :: STRING?) :: (INTEGER?)                             | get a long value from a provided file. File must be in the plugins/ddgres folder                            |
| fkr.nextLongElement    | fkr.nextLongElement(values :: STRING?) :: (INTEGER?)                            | generate an element from comma separated list of longs '1,2,3'                                              |
| fkr.nextLongFromFile   | fkr.nextLongFromFile(fileName :: STRING?, identifier :: STRING?) :: (INTEGER?)  | get the next long value from a provided file (per identifier). File must be in the plugins/ddgres folder    |

#### Range Functions

| name                | signature                                                                                   | description                                                                         |
| ------------------- | ------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |
| fkr.long            | fkr.long(from :: INTEGER?, to :: INTEGER?) :: (INTEGER?)                                    | get a random long number value within the provided range                            |
| fkr.number          | fkr.number(from :: INTEGER?, to :: INTEGER?) :: (FLOAT?)                                    | get a random number value within the provided range                                 |
| fkr.numberFormatted | fkr.numberFormatted(from :: INTEGER?, to :: INTEGER?, numberFormat :: STRING?) :: (STRING?) | get a random number value within the provided range as a formatted string           |
| fkr.numberRounded   | fkr.numberRounded(from :: INTEGER?, to :: INTEGER?, decimals :: INTEGER?) :: (FLOAT?)       | get a random number value within the provided range with a given number of decimals |
| fkr.percentage      | fkr.percentage() :: (INTEGER?)                                                              | get a random number percentage between 0 and 101                                    |

#### Date Functions

| name           | signature                                                           | description                                                                           |
| -------------- | ------------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| fkr.date       | fkr.date(dateFrom :: STRING?, dateTo :: STRING?) :: (INTEGER?)      | generates a date 'long' (yyyyMMdd) in the given period parameter format is yyyy-MM-dd |
| fkr.dateString | fkr.dateString(dateFrom :: STRING?, dateTo :: STRING?) :: (STRING?) | generates a date string in the given period parameter format is yyyy-MM-dd            |
| fkr.timestamp  | fkr.timestamp(dateFrom :: STRING?, dateTo :: STRING?) :: (INTEGER?) | generates a date timestamp in the given period parameter format is yyyy-MM-dd         |
| fkr.today      | fkr.today(dateFrom :: STRING?, dateTo :: STRING?) :: (STRING?)      | get the current date:yyyy-MM-dd                                                       |
| fkr.year       | fkr.year(yearFrom :: INTEGER?, yearTo :: INTEGER?) :: (INTEGER?)    | generates year value given period parameters                                          |

#### Boolean Functions

| name        | signature                                             | description                                               |
| ----------- | ----------------------------------------------------- | --------------------------------------------------------- |
| fkr.boolean | fkr.boolean(percentageTrue :: INTEGER?) :: (BOOLEAN?) | generates a boolean value, give the % true as a parameter |

#### Code Generating Functions

| name             | signature                                      | description                                                                                                             |
| ---------------- | ---------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| fkr.code         | fkr.code(mask :: STRING?) :: (STRING?)         | generate a code based on the mask given: '#' gives a random number '\_' will generate a random letter                   |
| fkr.codeSequence | fkr.codeSequence(mask :: STRING?) :: (STRING?) | generate a code based on the code mask given appended with a sequence number                                            |
| fkr.ip           | fkr.ip(mask :: STRING?) :: (STRING?)           | generate a ip number based on the mask given: '192.168.56.##' for every '#' a number between 0 and 10 will be generated |

#### Text Generating Functions

| name              | signature                                                        | description                                                       |
| ----------------- | ---------------------------------------------------------------- | ----------------------------------------------------------------- |
| fkr.txtWords      | fkr.txtWords(wordAmount :: INTEGER?) :: (LIST? OF ANY?)          | generate a word array for a given length                          |
| fkr.txtText       | fkr.txtText(characterAmount :: INTEGER?) :: (STRING?)            | generate text with the given amount of characters                 |
| fkr.txtSentence   | fkr.txtSentence() :: (STRING?)                                   | generate sentence                                                 |
| fkr.txtSentences  | fkr.txtSentences(sentenceCount :: INTEGER?) :: (LIST? OF ANY?)   | generate(s) [sentenceCount] sentences                             |
| fkr.txtParagraph  | fkr.txtParagraph(paragraphCount :: INTEGER?) :: (STRING?)        | generate a paragraph with the given amount of sentences           |
| fkr.txtParagraphs | fkr.txtParagraphs(paragraphCount :: INTEGER?) :: (LIST? OF ANY?) | generate a [paragraphCount] of paragraphs with ~ 4 sentences each |
