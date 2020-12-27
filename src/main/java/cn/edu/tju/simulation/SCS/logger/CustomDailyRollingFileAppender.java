package cn.edu.tju.simulation.SCS.logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class CustomDailyRollingFileAppender extends FileAppender {
    // The code assumes that the following constants are in a increasing  
    // sequence.  
    static final int TOP_OF_TROUBLE = -1;  
    static final int TOP_OF_MINUTE = 0;  
    static final int TOP_OF_HOUR = 1;  
    static final int HALF_DAY = 2;  
    static final int TOP_OF_DAY = 3;  
    static final int TOP_OF_WEEK = 4;  
    static final int TOP_OF_MONTH = 5;  
  
    /** 
       The date pattern. By default, the pattern is set to 
       "'.'yyyy-MM-dd" meaning daily rollover. 
     */  
    private String datePattern = "'.'yyyy-MM-dd";  
    /** 
    There is one backup paramaters by default.
     */  
    protected int maxBackupIndex = 1;  
  
    /** 
       The log paramaters will be renamed to the value of the
       scheduledFilename variable when the next interval is entered. For 
       example, if the rollover period is one hour, the log paramaters will be
       renamed to the value of "scheduledFilename" at the beginning of 
       the next hour. 
 
       The precise time when a rollover occurs depends on logging 
       activity. 
     */  
    private String scheduledFilename;  
  
    /** 
       The next time we estimate a rollover should occur. 
    */  
    private long nextCheck = System.currentTimeMillis() - 1;  
  
    Date now = new Date();  
  
    SimpleDateFormat sdf;  
  
    RollingCalendar rc = new RollingCalendar();  
  
    int checkPeriod = TOP_OF_TROUBLE;  
  
    // The gmtTimeZone is used only in computeCheckPeriod() method.  
    static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");  
  
    /** 
       The default constructor does nothing. */  
    public CustomDailyRollingFileAppender() {  
    }  
  
    /** 
      Instantiate a <code>DailyRollingFileAppender</code> and open the 
      paramaters designated by <code>filename</code>. The opened filename will
      become the ouput destination for this appender. 
 
     */  
    public CustomDailyRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException {  
        super(layout, filename, true);  
        this.datePattern = datePattern;  
        activateOptions();  
    }  
  
    /** 
       The <b>DatePattern</b> takes a string in the same format as 
       expected by {@link SimpleDateFormat}. This options determines the 
       rollover schedule. 
     */  
    public void setDatePattern(String pattern) {  
        datePattern = pattern;  
    }  
  
    /** 
       Set the maximum number of backup files to keep around. 
 
       <p>The <b>MaxBackupIndex</b> option determines how many backup 
       files are kept before the oldest is erased. This option takes 
       a positive integer value. If set to zero, then there will be no 
       backup files and the log paramaters will be truncated when it reaches
       <code>MaxFileSize</code>. 
     */  
    public void setMaxBackupIndex(int maxBackups) {  
        this.maxBackupIndex = maxBackups;  
    }  
  
    /** 
    Returns the value of the <b>MaxBackupIndex</b> option. 
     */  
    public int getMaxBackupIndex() {  
        return maxBackupIndex;  
    }  
  
    /** Returns the value of the <b>DatePattern</b> option. */  
    public String getDatePattern() {  
        return datePattern;  
    }  
  
    @Override  
    public void activateOptions() {  
        super.activateOptions();  
        if (datePattern != null && fileName != null) {  
            now.setTime(System.currentTimeMillis());  
            sdf = new SimpleDateFormat(datePattern);  
            int type = computeCheckPeriod();  
            printPeriodicity(type);  
            rc.setType(type);  
            File file = new File(fileName);  
            scheduledFilename = fileName  
                    + sdf.format(new Date(file.lastModified()));  
  
        } else {  
            LogLog.error("Either File or DatePattern options are not set for appender ["+ name + "].");  
        }  
    }  
  
    void printPeriodicity(int type) {  
        switch (type) {  
            case TOP_OF_MINUTE:  
                LogLog.debug("Appender [" + name + "] to be rolled every minute.");  
                break;  
            case TOP_OF_HOUR:  
                LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");  
                break;  
            case HALF_DAY:  
                LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");  
                break;  
            case TOP_OF_DAY:  
                LogLog.debug("Appender [" + name + "] to be rolled at midnight.");  
                break;  
            case TOP_OF_WEEK:  
                LogLog.debug("Appender [" + name + "] to be rolled at start of week.");  
                break;  
            case TOP_OF_MONTH:  
                LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");  
                break;  
            default:  
                LogLog.warn("Unknown periodicity for appender [" + name + "].");  
        }  
    }  
  
    // This method computes the roll over period by looping over the  
    // periods, starting with the shortest, and stopping when the r0 is  
    // different from from r1, where r0 is the epoch formatted according  
    // the datePattern (supplied by the devices) and r1 is the
    // epoch+nextMillis(i) formatted according to datePattern. All date  
    // formatting is done in GMT and not local format because the test  
    // logic is based on comparisons relative to 1970-01-01 00:00:00  
    // GMT (the epoch).  
  
    int computeCheckPeriod() {  
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone,  
                Locale.getDefault());  
        // set sate to 1970-01-01 00:00:00 GMT  
        Date epoch = new Date(0);  
        if (datePattern != null) {  
            for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {  
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(  
                        datePattern);  
                simpleDateFormat.setTimeZone(gmtTimeZone); // do all date  
                                                           // formatting in GMT  
                String r0 = simpleDateFormat.format(epoch);  
                rollingCalendar.setType(i);  
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));  
                String r1 = simpleDateFormat.format(next);  
                // System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);  
                if (r0 != null && r1 != null && !r0.equals(r1)) {  
                    return i;  
                }  
            }  
        }  
        return TOP_OF_TROUBLE; // Deliberately head for trouble...  
    }  
  
    /** 
       Rollover the current paramaters to a new paramaters.
     */  
    void rollOver() throws IOException {  
  
        List<ModifiedTimeSortableFile> files = getAllFiles();  
        Collections.sort(files);  
        if (files.size() >= maxBackupIndex) {  
            int index = 0;  
            int diff = files.size() - (maxBackupIndex - 1);  
            for (ModifiedTimeSortableFile file : files) {  
                if (index >= diff)  
                    break;  
  
                file.delete();  
                index++;  
            }  
        }  
  
        /* Compute filename, but only if datePattern is specified */  
        if (datePattern == null) {  
            errorHandler.error("Missing DatePattern option in rollOver().");  
            return;  
        }  
        LogLog.debug("maxBackupIndex=" + maxBackupIndex);  
  
        String datedFilename = fileName + sdf.format(now);  
        // It is too early to roll over because we are still within the  
        // bounds of the current interval. Rollover will occur once the  
        // next interval is reached.  
        if (scheduledFilename.equals(datedFilename)) {  
            return;  
        }  
  
        // close current paramaters, and rename it to datedFilename
        this.closeFile();  
  
        File target = new File(scheduledFilename);  
        if (target.exists()) {  
            target.delete();  
        }  
  
        File file = new File(fileName);  
        boolean result = file.renameTo(target);  
        if (result) {  
            LogLog.debug(fileName + " -> " + scheduledFilename);  
        } else {  
            LogLog.error("Failed to rename [" + fileName + "] to ["  
                    + scheduledFilename + "].");  
        }  
  
        try {  
            // This will also close the paramaters. This is OK since multiple
            // close operations are safe.  
            this.setFile(fileName, true, this.bufferedIO, this.bufferSize);  
        } catch (IOException e) {  
            errorHandler.error("setFile(" + fileName + ", true) call failed.");  
        }  
        scheduledFilename = datedFilename;  
    }  
  
    /** 
     * This method differentiates DailyRollingFileAppender from its 
     * super class. 
     * 
     * <p>Before actually logging, this method will check whether it is 
     * time to do a rollover. If it is, it will schedule the next 
     * rollover time and then rollover. 
     * */  
    @Override  
    protected void subAppend(LoggingEvent event) {  
        long n = System.currentTimeMillis();  
        if (n >= nextCheck) {  
            now.setTime(n);  
            nextCheck = rc.getNextCheckMillis(now);  
            try {  
                rollOver();  
            } catch (IOException ioe) {  
                if (ioe instanceof InterruptedIOException) {  
                    Thread.currentThread().interrupt();  
                }  
                LogLog.error("rollOver() failed.", ioe);  
            }  
        }  
        super.subAppend(event);  
    }  
  
    /** 
     * This method searches list of log files 
     * based on the pattern given in the log4j configuration paramaters
     * and returns a collection  
     * @return List&lt;ModifiedTimeSortableFile&gt; 
     */  
    private List<ModifiedTimeSortableFile> getAllFiles() {  
        List<ModifiedTimeSortableFile> files = new ArrayList<ModifiedTimeSortableFile>();  
        FilenameFilter filter = new FilenameFilter() {  
            public boolean accept(File dir, String name) {  
                String directoryName = dir.getPath();  
                LogLog.debug("directory name: " + directoryName);  
                File file = new File(fileName);  
                String perentDirectory = file.getParent();  
                if (perentDirectory != null)  
                {  
                    String localFile = fileName.substring(directoryName.length());  
                    return name.startsWith(localFile);  
                }  
                return name.startsWith(fileName);  
            }  
        };  
        File file = new File(fileName);  
        String perentDirectory = file.getParent();  
        if (file.exists()) {  
            if (file.getParent() == null) {  
                String absolutePath = file.getAbsolutePath();  
                perentDirectory = absolutePath.substring(0, absolutePath.lastIndexOf(fileName));  
  
            }  
        }  
        File dir = new File(perentDirectory);  
        String[] names = dir.list(filter);  
  
        for (int i = 0; i < names.length; i++) {  
            files.add(new ModifiedTimeSortableFile(dir + System.getProperty("paramaters.separator") + names[i]));
        }  
        return files;  
    }  
}  
  
/** 
* The Class ModifiedTimeSortableFile extends java.io.File class and 
* implements Comparable to sort files list based upon their modified date 
*/  
class ModifiedTimeSortableFile extends File implements Serializable, Comparable<File> {  
    private static final long serialVersionUID = 1373373728209668895L;  
  
    public ModifiedTimeSortableFile(String parent, String child) {  
        super(parent, child);  
        // TODO Auto-generated constructor stub  
    }  
  
    public ModifiedTimeSortableFile(URI uri) {  
        super(uri);  
        // TODO Auto-generated constructor stub  
    }  
  
    public ModifiedTimeSortableFile(File parent, String child) {  
        super(parent, child);  
    }  
  
    public ModifiedTimeSortableFile(String string) {  
        super(string);  
    }  
  
    @Override  
    public int compareTo(File anotherPathName) {  
        long thisVal = this.lastModified();  
        long anotherVal = anotherPathName.lastModified();  
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));  
    }  
}  
  
/** 
*  RollingCalendar is a helper class to DailyRollingFileAppender. 
*  Given a periodicity type and the current time, it computes the 
*  start of the next interval.  
* */  
class RollingCalendar extends GregorianCalendar {  
    private static final long serialVersionUID = -3560331770601814177L;  
  
    int type = CustomDailyRollingFileAppender.TOP_OF_TROUBLE;  
  
    RollingCalendar() {  
        super();  
    }  
  
    RollingCalendar(TimeZone tz, Locale locale) {  
        super(tz, locale);  
    }  
  
    void setType(int type) {  
        this.type = type;  
    }  
  
    public long getNextCheckMillis(Date now) {  
        return getNextCheckDate(now).getTime();  
    }  
  
    public Date getNextCheckDate(Date now) {  
        this.setTime(now);  
  
        switch (type) {  
            case CustomDailyRollingFileAppender.TOP_OF_MINUTE:  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.MINUTE, 1);  
                break;  
            case CustomDailyRollingFileAppender.TOP_OF_HOUR:  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.HOUR_OF_DAY, 1);  
                break;  
            case CustomDailyRollingFileAppender.HALF_DAY:  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                int hour = get(Calendar.HOUR_OF_DAY);  
                if (hour < 12) {  
                    this.set(Calendar.HOUR_OF_DAY, 12);  
                } else {  
                    this.set(Calendar.HOUR_OF_DAY, 0);  
                    this.add(Calendar.DAY_OF_MONTH, 1);  
                }  
                break;  
            case CustomDailyRollingFileAppender.TOP_OF_DAY:  
                this.set(Calendar.HOUR_OF_DAY, 0);  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.DATE, 1);  
                break;  
            case CustomDailyRollingFileAppender.TOP_OF_WEEK:  
                this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());  
                this.set(Calendar.HOUR_OF_DAY, 0);  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.WEEK_OF_YEAR, 1);  
                break;  
            case CustomDailyRollingFileAppender.TOP_OF_MONTH:  
                this.set(Calendar.DATE, 1);  
                this.set(Calendar.HOUR_OF_DAY, 0);  
                this.set(Calendar.MINUTE, 0);  
                this.set(Calendar.SECOND, 0);  
                this.set(Calendar.MILLISECOND, 0);  
                this.add(Calendar.MONTH, 1);  
                break;  
            default:  
                throw new IllegalStateException("Unknown periodicity type.");  
        }  
        return getTime();  
    } 
	
}
