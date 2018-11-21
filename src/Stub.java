import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stub {
	static long pageSize = 512;
	/**
	 *  Stub method for dropping tables
	 *  @param dropTableString is a String of the user input
	 */
	public static void dropTable(String dropTableString) {
		System.out.println("STUB: This is the dropTable method.");
		System.out.println("\tParsing the string:\"" + dropTableString + "\"");
	}
	
	/**
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
	public static void parseQuery(String queryString) {
		System.out.println("STUB: This is the parseQuery method");
		System.out.println("\tParsing the string:\"" + queryString + "\"");
	}

	/**
	 *  Stub method for updating records
	 *  @param updateString is a String of the user input
	 */
	public static void parseUpdate(String updateString) {
		System.out.println("STUB: This is the dropTable method");
		System.out.println("Parsing the string:\"" + updateString + "\"");
	}

	
	/**
	 *  Stub method for creating new tables
	 *  @param queryString is a String of the user input
	 */
	public static void parseCreateTable(String createTableString) {
		
		System.out.println("STUB: Calling your method to create a table");
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));
		System.out.println(createTableTokens);
		/* Define table file name */
		Boolean flag = false;
		String tableFileName = createTableTokens.get(2) + ".tbl";

		/* YOUR CODE GOES HERE */
		
		/*  Code to create a .tbl file to contain table data */
		try {
			File file = new File("data/userdata");
			String[] oldFiles = file.list();
			System.out.println(oldFiles.length);
			for (int i = 0; i < oldFiles.length; i++) {
				//System.out.println(oldFiles[i]+" "+ tableFileName);
				if (oldFiles[i].equals(tableFileName)) {
					System.out.println("Table already exists");
					flag = true;
				}
			}
			
			
			
			if(flag == false) {
			/*  Create RandomAccessFile tableFile in read-write mode.
			 *  Note that this doesn't create the table file in the correct directory structure
			 */
			RandomAccessFile tableFile = new RandomAccessFile("data/userdata/"+tableFileName, "rw");
			tableFile.setLength(pageSize);
			tableFile.seek(0);
			tableFile.write(0x0D); // leaf page
			
			tableFile.write(0); // number of records

			tableFile.write(0x20); // start of content
			tableFile.write(0); // col name

			tableFile.write(0xFF); // next page pointer
			tableFile.write(0xFF);
			tableFile.write(0xFF);
			tableFile.write(0xFF);
			
			tableFile.close();
			
			String tableName = createTableTokens.get(2);

			String[] splitTable = createTableString.split(",");

			List<String> columnNames = new ArrayList<>();
			List<String> columnTypes = new ArrayList<>();
			List<String> columnNullable = new ArrayList<>();
			
			for (int i = 0; i < splitTable.length; i++) {
				//
				System.out.println(splitTable[i].split(" ")[3]);
				if (i == 0) {
					columnNames.add(splitTable[i].split(" ")[4]);
					columnTypes.add(splitTable[i].split(" ")[5]);

					if (splitTable[i].split(" ")[7].toUpperCase().contains("PRIMARY")
							|| splitTable[i].split(" ")[7].toUpperCase().contains("KEY")) {
						columnNullable.add("PRI");
					} else if (splitTable[i].split(" ")[7].toUpperCase() == "NOT") {
						columnNullable.add("NO");
					} else {
						columnNullable.add("YES");
					}

				} else {
					columnNames.add(splitTable[i].split(" ")[2]);
					columnTypes.add(splitTable[i].split(" ")[3]);

					if (splitTable[i].split(" ").length == 4) {
						columnNullable.add("YES");
					} else if (splitTable[i].split(" ").length > 4) {
						if (splitTable[i].split(" ")[3].toUpperCase().contains("NOT")
								|| splitTable[i].split(" ")[4].toUpperCase().contains("NOT")) {
							columnNullable.add("NO");
						} else {
							columnNullable.add("YES");
						}
					}
				}
			}
			
			RandomAccessFile meta_tables = new RandomAccessFile("data/catalog/davisbase_tables.tbl", "rw");

			meta_tables.seek(0x08);
			Long pointer1 = Long.decode("0x08");
			Long location = Long.decode("0x08");

			int loc = 0;

			while (true) {
				if (!meta_tables.readBoolean()) {
					if (location == 8) {
						break;
					} else {
						meta_tables.seek(location - 1);
						loc = Integer.valueOf(meta_tables.readShort());
						break;
					}
				} else {
					pointer1 = meta_tables.getFilePointer();
					location = pointer1;
					pointer1 += 1;
					meta_tables.seek(pointer1);
				}
			}

			int lengthOfRecordBefore = 0;

			if (loc == 0) {
				lengthOfRecordBefore = 0;
			} else {
				lengthOfRecordBefore = loc;
			}

			int recordLocation = 0;
			if (lengthOfRecordBefore == 0) {
				recordLocation = 512 - tableName.length() - 6 - lengthOfRecordBefore;
			} else {
				recordLocation = lengthOfRecordBefore - tableName.length() - 6;
			}
 
			meta_tables.seek(recordLocation);

			int pos = (int) meta_tables.getFilePointer();

			meta_tables.write(tableName.length());
			meta_tables.write(columnNames.size());

			meta_tables.seek(0x01);
			int recordsSizeForRow_Id = meta_tables.read(); // for number of
															// records in
															// table

			meta_tables.seek(recordLocation + 2);
			meta_tables.writeInt(recordsSizeForRow_Id + 1);

			meta_tables.seek(recordLocation + 6);
			for (int i = 0; i < tableName.length(); i++) {
				meta_tables.write(tableName.charAt(i));
			}

			meta_tables.seek(0x01);
			int recordsSize = meta_tables.read(); // for number of records
													// in
													// table
			meta_tables.seek(0x01);
			meta_tables.write(recordsSize + 1);
			meta_tables.writeShort(pos);

			meta_tables.seek(0x08);
			Long pointer = Long.decode("0x08");

			while (true) {
				if (!meta_tables.readBoolean()) {
					meta_tables.seek(pointer);
					meta_tables.writeShort(pos);
					break;
				}
				pointer = meta_tables.getFilePointer();
				pointer += 1;
				meta_tables.seek(pointer);
			}
			
			meta_tables.close();
			for (int i = 0; i < columnNames.size(); i++) {
				MetaColumnsTable(columnNames.get(i), columnTypes.get(i), i, recordsSizeForRow_Id + 1, tableName,
						columnNullable.get(i));
			}
			
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	private static int columnsSize = 0;
	private static String columnTable = "data/catalog/davisbase_columns.tbl";

	public static void MetaColumnsTable(String columnName, String type, int position, int row_id, String tableName,
			String isNullable) {

		RandomAccessFile meta_columns;
		try {
			meta_columns = new RandomAccessFile(columnTable, "rw");

			meta_columns.seek(1);
			int count = meta_columns.read();

			if (count == 10) {
				columnsSize++;
				meta_columns.seek(4);
				meta_columns.writeInt(columnsSize);
				meta_columns.close();
				columnTable = "data/catalog/davisbase_columns" + columnsSize + ".tbl";
				meta_columns = new RandomAccessFile(columnTable, "rw");
				meta_columns.setLength(pageSize);
				meta_columns.seek(0);
				meta_columns.write(0x0D);
				meta_columns.write(0x00);

				meta_columns.write(0x00); // start of content
				meta_columns.write(0x00);

				meta_columns.write(0xFF); // last page
				meta_columns.write(0xFF);
				meta_columns.write(0xFF);
				meta_columns.write(0xFF);

			}

			meta_columns.seek(0x08);
			Long pointer2 = Long.decode("0x08");
			Long location1 = Long.decode("0x08");
			int loc1 = 0;

			while (true) {
				if (!meta_columns.readBoolean()) {
					if (location1 == 8) {
						break;
					} else {
						// System.out.println(location1);
						meta_columns.seek(location1 - 1);
						loc1 = Integer.valueOf(meta_columns.readShort());
						break;
					}
				} else {
					pointer2 = meta_columns.getFilePointer();
					location1 = pointer2;
					pointer2 += 1;
					meta_columns.seek(pointer2);
				}
			}

			int lengthOfRecordBefore1 = 0;

			if (loc1 == 0) {
				lengthOfRecordBefore1 = 0;
			} else {
				lengthOfRecordBefore1 = loc1;
			}
			// System.out.println(lengthOfRecordBefore1);
			int recordLocation1 = 0;
			if (lengthOfRecordBefore1 == 0) {
				recordLocation1 = 512 - row_id - tableName.length() - 7 - columnName.length() - type.length() - position
						- 3 - lengthOfRecordBefore1;
			} else {
				recordLocation1 = lengthOfRecordBefore1 - row_id - tableName.length() - 7 - columnName.length()
						- type.length() - position - 3 - 1;
			}

			// System.out.println(recordLocation1);

			meta_columns.seek(recordLocation1);

			int pos1 = (int) meta_columns.getFilePointer();

			meta_columns.write(columnName.length());

			meta_columns.seek(0x01);
			int recordsSizeForRow_Id1 = meta_columns.read(); // for number of
																// records in
			meta_columns.seek(recordLocation1 + 1);
			meta_columns.writeInt(recordsSizeForRow_Id1 + 1);
			meta_columns.writeInt(row_id);
			for (int i = 0; i < tableName.length(); i++) {
				meta_columns.write(tableName.charAt(i));
			}
			for (int i = 0; i < columnName.length(); i++) {
				meta_columns.write(columnName.charAt(i));
			}
			String[] datatypes = getBytesForType(type);
			for (int i = 0; i < datatypes[1].length(); i++) {
				meta_columns.write(datatypes[1].charAt(i));
			}

			if (position == 0) {

				for (int i = 0; i < isNullable.length(); i++) {
					meta_columns.write(isNullable.charAt(i));
				}

			}
			if (position != 0) {
				for (int i = 0; i < isNullable.length(); i++) {
					meta_columns.write(isNullable.charAt(i));
				}
			}

			meta_columns.seek(0x01);
			int recordsSize1 = meta_columns.read(); // for number of records in
													// table
			meta_columns.seek(0x01);
			meta_columns.write(recordsSize1 + 1);
			meta_columns.writeShort(pos1);

			meta_columns.seek(0x08);
			Long pointer3 = Long.decode("0x08");

			while (true) {
				if (!meta_columns.readBoolean()) {
					meta_columns.seek(pointer3);
					meta_columns.writeShort(pos1);
					break;
				}
				pointer3 = meta_columns.getFilePointer();
				pointer3 += 1;
				meta_columns.seek(pointer3);
			}

			meta_columns.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static String[] getBytesForType(String type) {

		int a = 0;
		String serial_type_code = "0x00";

		switch (type.toUpperCase()) {

		case "TINYINT":
			a = 1;
			serial_type_code = "0x04";
			break;
		case "SMALLINT":
			a = 2;
			serial_type_code = "0x05";
			break;
		case "INT":
			a = 4;
			serial_type_code = "0x06";
			break;
		case "BIGINT":
			a = 8;
			serial_type_code = "0x07";
			break;
		case "REAL":
			a = 4;
			serial_type_code = "0x08";
			break;
		case "DOUBLE":
			a = 8;
			serial_type_code = "0x09";
			break;
		case "DATETIME":
			a = 8;
			serial_type_code = "0x0A";
			break;
		case "DATE":
			a = 8;
			serial_type_code = "0x0B";
			break;
		case "TEXT":
			serial_type_code = "0x0C";
			break;

		}

		String[] result = new String[2];
		result[0] = String.valueOf(a);
		result[1] = serial_type_code;

		return result;
	}

	public static String getTypeForBytes(String type) {

		String returntype = null;

		switch (type) {

		case "0x04":
			returntype = "TINYINT";
			break;
		case "0x05":
			returntype = "SMALLINT";
			break;
		case "0x06":

			returntype = "INT";
			break;
		case "0x07":
			returntype = "BIGINT";
			break;
		case "0x08":
			returntype = "REAL";
			break;
		case "0x09":

			returntype = "DOUBLE";
			break;
		case "0x0A":
			returntype = "DATETIME";
			break;
		case "0x0B":
			returntype = "DATE";
			break;
		case "0x0C":
			returntype = "TEXT";
			break;

		}

		return returntype;
	}

}
