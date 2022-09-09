package mx.uatx.siia.reportes;

public class FieldsNuevaCita {

    private String nombreuser;
    private String fechacita;
    private String tramite;
    private String programaedu;
    private String matricula;
    private String hora;
    private String descripcion;
    private String foliocita;
    private String link;
    private String fechaprint;

    public String getNombreuser() {
        return nombreuser;
    }

    public String getFechacita() {
        return fechacita;
    }

    public void setFechacita(String fechacita) {
        this.fechacita = fechacita;
    }

    public String getTramite() {
        return tramite;
    }

    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    public String getProgramaedu() {
        return programaedu;
    }

    public void setProgramaedu(String programaedu) {
        this.programaedu = programaedu;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoliocita() {
        return foliocita;
    }

    public void setFoliocita(String foliocita) {
        this.foliocita = foliocita;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFechaprint() {
        return fechaprint;
    }

    public void setFechaprint(String fechaprint) {
        this.fechaprint = fechaprint;
    }

    public void setNombreuser(String nombreuser) {
        this.nombreuser = nombreuser;
    }

    public FieldsNuevaCita(String nombreuser, String fechacita, String tramite, String programaedu, String matricula, String hora, String descripcion, String foliocita, String link, String fechaprint) {
        this.nombreuser = nombreuser;
        this.fechacita = fechacita;
        this.tramite = tramite;
        this.programaedu = programaedu;
        this.matricula = matricula;
        this.hora = hora;
        this.descripcion = descripcion;
        this.foliocita = foliocita;
        this.link = link;
        this.fechaprint = fechaprint;
    }
}
