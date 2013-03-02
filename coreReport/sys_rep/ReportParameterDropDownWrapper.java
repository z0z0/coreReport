package rs.fimes.domain.sys.rep;

import java.io.Serializable;

public class ReportParameterDropDownWrapper implements Serializable,Comparable<ReportParameterDropDownWrapper> {
    
    private static final long serialVersionUID = -6191675030813712538L;
    private String value;
    private String label;
    private Integer sortField;
    
    public ReportParameterDropDownWrapper(String value, String label,
            Integer sortField) {
        super();
        this.value = value;
        this.label = label;
        this.sortField = sortField;
    }
    
    public ReportParameterDropDownWrapper(Integer value, String label,
            Integer sortField) {
        super();
        this.value = String.valueOf(value);
        this.label = label;
        this.sortField = sortField;
    }
    
    public ReportParameterDropDownWrapper(String value, String label) {
        super();
        this.value = value;
        this.label = label;
        this.sortField = null;
    }
    
    public ReportParameterDropDownWrapper(Integer value, String label) {
        super();
        this.value = String.valueOf(value);
        this.label = label;
        this.sortField = value;
    }
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Integer getSortField() {
        return sortField;
    }
    public void setSortField(Integer sortField) {
        this.sortField = sortField;
    }

    @Override
    public int compareTo(ReportParameterDropDownWrapper o) {
        if(sortField!=null) {
            return sortField - o.sortField;
        }
        return 0;
    }

}
