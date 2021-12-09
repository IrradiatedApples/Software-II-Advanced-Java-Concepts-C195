/**
 *
 * @author Arthur J Amende
 * */
package Classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Only used to display data for Report One, number of appointments by month.
 */
public class ReportOne {
    private int month;
    private int year;
    private String type;
    private int count;

    public ReportOne(int month, int year, String type, int count){
        this.month = month;
        this.year = year;
        this.type = type;
        this.count = count;
    }

    public ReportOne(int month, int year, String type){
        this.month = month;
        this.year = year;
        this.type = type;
        this.count = 1;
    }

    public ReportOne(ReportOne reportOneToCopy){
        this.month = reportOneToCopy.month;
        this.year = reportOneToCopy.year;
        this.type = reportOneToCopy.type;
        this.count = reportOneToCopy.count;
    }

    public ReportOne(){
        month = 1;
        year = 9999;
        type = "Dummy";
        count = 0;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount(){
        ++count;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getMonthYear(){
        LocalDate date = LocalDate.of(year,month,1);
        return date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}
