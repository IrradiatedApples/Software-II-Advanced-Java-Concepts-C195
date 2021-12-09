/**
 *
 * @author Arthur J Amende
 * */
package Classes;

/**
 * Only used to display data for Report Three, number of appointment hours by customer.
 */
public class ReportThree extends Individual{
    private double hours;

    public ReportThree(int id, String name, double hours){
        super(id, name);
        this.hours = hours;
    }

    public ReportThree(ReportThree reportThreeToCopy){
        super(reportThreeToCopy.getId(), reportThreeToCopy.getName());
        this.hours = reportThreeToCopy.hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void addHours(double addHours){
        hours += addHours;
    }

    public double getHours() {
        return hours;
    }

    @Override
    public String getName(){
        return super.getId() + ": " + super.getName();
    }
}
