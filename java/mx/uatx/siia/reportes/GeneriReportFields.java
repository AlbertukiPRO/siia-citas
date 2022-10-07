package mx.uatx.siia.reportes;

public class GeneriReportFields {
    private String area;
    private String fecha;
    private String dateprint;

    private String tramite;

    public String getDateprint() {
        return dateprint;
    }

    public String getTramite() {
        return tramite;
    }

    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    public void setDateprint(String dateprint) {
        this.dateprint = dateprint;
    }

    public GeneriReportFields(String area, String tramite, String dateprint, String a){
        this.area = area;
        this.tramite = tramite;
        this.dateprint = dateprint;
    }

    public GeneriReportFields(String area, String fecha, String dateprint) {
        this.area = area;
        this.fecha = fecha;
        this.dateprint = dateprint;
    }

    public GeneriReportFields(String nombreArea, String fecha) {
        this.area = nombreArea;
        this.fecha = fecha;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
