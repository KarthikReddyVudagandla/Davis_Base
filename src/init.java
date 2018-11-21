import static java.lang.System.out;
import java.io.File;
import java.io.RandomAccessFile;


public class init {

	static int pageSize = 512;
	
	public void onInit() {
		try {
			createDatabase();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createDatabase() {
		boolean metaTables = false;
		boolean metaColumns = false;

		try {
			File file = new File("data");
			file.mkdir();
			File catalog = new File(file,"catalog");
			catalog.mkdir();
			File userdata = new File(file,"userdata");
			userdata.mkdir();
			String[] oldTables = catalog.list();
			//System.out.println(file.list());
			for (int i = 0; i < oldTables.length; i++) {
				if (oldTables[i].equals("davisbase_tables.tbl"))
					metaTables = true;
				if (oldTables[i].equals("davisbase_columns.tbl"))
					metaColumns = true;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		if(!metaTables) {
			try {
				System.out.println("creating davisbase_tables metatable ....");
				RandomAccessFile davisbaseTablesCatalog = new RandomAccessFile("data/catalog/davisbase_tables.tbl", "rw");
				davisbaseTablesCatalog.setLength(pageSize);
				davisbaseTablesCatalog.seek(0);
				davisbaseTablesCatalog.write(0x0D); //leaf page
				davisbaseTablesCatalog.write(0x00); //Data count
				
				davisbaseTablesCatalog.write(0x00); // start of content
				davisbaseTablesCatalog.write(0x00);
				
	
				davisbaseTablesCatalog.write(0xFF); // last page
				davisbaseTablesCatalog.write(0xFF); 
				davisbaseTablesCatalog.write(0xFF);
				davisbaseTablesCatalog.write(0xFF);
				
				davisbaseTablesCatalog.close();
	
			} catch (Exception e) {
				out.println("Error creating meta tables ");
				out.println(e);
			}
		}
		if(!metaColumns) {
			try {
				System.out.println("creating davisbase_columns metatable ....");
				RandomAccessFile davisbaseColumnsCatalog = new RandomAccessFile("data/catalog/davisbase_columns.tbl", "rw");
				davisbaseColumnsCatalog.setLength(pageSize);
				davisbaseColumnsCatalog.seek(0);
				davisbaseColumnsCatalog.write(0x0D);
				davisbaseColumnsCatalog.write(0x00);
				
				davisbaseColumnsCatalog.write(0x00); // start of content
				davisbaseColumnsCatalog.write(0x00);
				
	
				davisbaseColumnsCatalog.write(0xFF);
				davisbaseColumnsCatalog.write(0xFF);
				davisbaseColumnsCatalog.write(0xFF);
				davisbaseColumnsCatalog.write(0xFF);
				
				davisbaseColumnsCatalog.close();
	
			} catch (Exception e) {
				out.println("Error creating meta columns ");
				out.println(e);
			}
		}
	}
}
