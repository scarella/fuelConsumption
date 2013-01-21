package scarella.consumption;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scarella.consumption.R.string;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper 
{

	private static final String DB_PATH = "/data/data/scarella.consumption/databases/";
	private static final String DB_NAME = "spotreba.s3db";
	private static final int DB_VERSION = 1;
	private final Context myContext;
	private SQLiteDatabase database;
	
	
	public DBHelper(Context context) 
	{
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}
	
	/**
	 * Creates a empty DB on the system and rewrites it with your own DB.
	 */
	public void createDatabase() throws IOException
	{
		boolean dbExists = checkDatabase();
		
		if (dbExists)
			Log.i(DBHelper.class.getName(), "Database already exists");
		else
		{
			this.getReadableDatabase();
			
			try
			{
				copyDatabase();
			}
			catch (IOException e)
			{
				Log.e(DBHelper.class.getName(), "Error copying database", e);
				throw new Error("Error copying database");
			}
		}
	}
	
	
	/**
	 * Check if the DB already exists to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDatabase()
	{ 
		boolean dbExists = false;
		String myPath = DB_PATH + DB_NAME; 
		
		try
		{
			SQLiteDatabase checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		
			if (checkDB != null)
			{
				checkDB.close();
				dbExists = true;
			}
		}
		catch (SQLException e)
		{
			Log.e(DBHelper.class.getName(), "Database " +  myPath + " doesn't exists yet.", e);
		}
		
		return dbExists;
	}
	
	/**
     * Copies your DB from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
	private void copyDatabase() throws IOException
	{
		// Open your local DB as the input stream.
		InputStream inDbStream = myContext.getAssets().open(DB_NAME);	
		
		// Path to the just created empty DB.
		String outFileName = DB_PATH + DB_NAME;
		
		// Open the empty DB as the output stream.
		OutputStream outDbStream = new FileOutputStream(outFileName);
		
		// Transfer bytes from the inputfile to the outputfile.
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inDbStream.read(buffer)) > 0)
			outDbStream.write(buffer, 0, length);
		
		outDbStream.flush();
		outDbStream.close();
		inDbStream.close();
	}
	
	/**
	 * Open DB
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException
	{
        String myPath = DB_PATH + DB_NAME;
    	database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub	
	}
	
	public synchronized void close() 
	{	 
	    if(database != null)
		    database.close();

	    super.close();
	}

}
