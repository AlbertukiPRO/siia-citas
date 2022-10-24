package mx.uatx.siia.reportes;

public class GeneriReportFields {
    private String area;
    private String fecha;
    private String dateprint;
    private String tramite;
    private String tiporeporte;
    private String rangoFechas;



    public GeneriReportFields(String area, String tramite, String dateprint, String tipo, String rangoFechas) {
        this.area = area;
        this.tramite = tramite;
        this.dateprint = dateprint;
        this.tiporeporte = tipo;
        this.rangoFechas = rangoFechas;
    }
    public String getTiporeporte() {
        return tiporeporte;
    }

    public void setTiporeporte(String tiporeporte) {
        this.tiporeporte = tiporeporte;
    }

    public String getRangoFechas() {
        return rangoFechas;
    }

    public void setRangoFechas(String rangoFechas) {
        this.rangoFechas = rangoFechas;
    }

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
