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

		try {
			File file = new File("data");
			file.mkdir();
			File catalog = new File(file,"catalog");
			catalog.mkdir();
			File userdata = new File(file,"userdata");
			userdata.mkdir();
			String[] oldTables = catalog.list();
			System.out.println(file.list());
			for (int i = 0; i < oldTables.length; i++) {
				File oldFiles = new File(catalog, oldTables[i]);
				oldFiles.delete();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			RandomAccessFile davisbaseTablesCatalog = new RandomAccessFile("data/catalog/davisbase_tables.tbl", "rw");
			davisbaseTablesCatalog.setLength(pageSize);
			davisbaseTablesCatalog.seek(0);
			davisbaseTablesCatalog.write(0x0D);
			davisbaseTablesCatalog.write(0x00);
			
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

		try {
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
