package doctor;

import java.util.List;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;

public interface DoctorFIS {

	List<ReportEntry> computeDCRestrictions(String uml);

	List<ReportEntry> computeDCURestrictions(String uml);
	

}