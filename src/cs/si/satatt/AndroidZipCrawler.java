package cs.si.satatt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.orekit.data.DataLoader;
import org.orekit.data.DataProvider;
import org.orekit.errors.OrekitException;
import org.orekit.utils.Constants;

import android.content.Context;
import android.util.Log;

public class AndroidZipCrawler implements DataProvider{
	
	private final static String TAG = "AndroidZipCrawler";

	private int source;
	private Context context;
	private String name;
	
	public AndroidZipCrawler(int source_, Context context_){
		
		this.source = source_;
		this.context = context_;
				
		
		Log.d(TAG,"ZipCrawlerBuilt");
		//Log.d(TAG,"rawstrem name : " + this.name);
	}
	
	
	 /** {@inheritDoc} */
    public boolean feed(final Pattern supported, final DataLoader visitor)
        throws OrekitException {
    	
    	Log.d(TAG,"feed called 1");

    	try {
    		
    		 // open the raw data stream
            InputStream    rawStream = null;
            ZipInputStream zip       = null;
    		
	        try {
	        	
	    		rawStream = context.getResources().openRawResource(this.source);
	    		name = context.getResources().getResourceName(this.source);

	        	
	                // add the zip format analysis layer and browse the archive
	            zip = new ZipInputStream(rawStream);
	            
	            return feed(name, supported, visitor, zip);
	            
	            } finally {
	                if (zip != null) {
	                    zip.close();
	                }
	                if (rawStream != null) {
	                    rawStream.close();
	                }
	            }

        } catch (IOException ioe) {
            throw new OrekitException(ioe, new DummyLocalizable(ioe.getMessage()));
        } catch (ParseException pe) {
            throw new OrekitException(pe, new DummyLocalizable(pe.getMessage()));
        }

    }

    /** Feed a data file loader by browsing the entries in a zip/jar.
     * @param prefix prefix to use for name
     * @param supported pattern for file names supported by the visitor
     * @param visitor data file visitor to use
     * @param zip zip/jar input stream
     * @exception OrekitException if some data is missing, duplicated
     * or can't be read
     * @return true if something has been loaded
     * @exception IOException if data cannot be read
     * @exception ParseException if data cannot be read
     */
    private boolean feed(final String prefix, final Pattern supported,
                         final DataLoader visitor, final ZipInputStream zip)
        throws OrekitException, IOException, ParseException {
    	
    	Log.d(TAG,"feed called 2");

        OrekitException delayedException = null;
        boolean loaded = false;

        // loop over all entries
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {

            try {

                if (visitor.stillAcceptsData() && !entry.isDirectory()) {

                    final String fullName = prefix + "!" + entry.getName();

                    if (ZIP_ARCHIVE_PATTERN.matcher(entry.getName()).matches()) {

                        // recurse inside the archive entry
                        loaded = feed(fullName, supported, visitor, new ZipInputStream(zip)) || loaded;

                    } else {

                        // remove leading directories
                        String entryName = entry.getName();
                        final int lastSlash = entryName.lastIndexOf('/');
                        if (lastSlash >= 0) {
                            entryName = entryName.substring(lastSlash + 1);
                        }

                        // remove suffix from gzip entries
                        final Matcher gzipMatcher = GZIP_FILE_PATTERN.matcher(entryName);
                        final String baseName = gzipMatcher.matches() ? gzipMatcher.group(1) : entryName;

                        if (supported.matcher(baseName).matches()) {

                            // visit the current entry
                            final InputStream stream =
                                gzipMatcher.matches() ? new GZIPInputStream(zip) : zip;
                            visitor.loadData(stream, fullName);
                            loaded = true;

                        }

                    }

                }

            } catch (OrekitException oe) {
                delayedException = oe;
            }

            // prepare next entry processing
            zip.closeEntry();
            entry = zip.getNextEntry();

        }

        if (!loaded && delayedException != null) {
            throw delayedException;
        }
        return loaded;

    }

}

